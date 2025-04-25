package Classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import Weapon.Weapon;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Getter;

@Getter
public class HandleInput {

    private Texture characterSheet;
    private boolean isMouse;
    private final Coordinates coordinates; // Координаты персонажа
    private final Weapon weapon; // Оружие персонажа
    private final float WORLD_WIDTH, WORLD_HEIGHT;
    private final float playerWidth = 32; // Размер персонажа
    private final float playerHeight = 32;
    private TextureRegion currentFrame;
    private final ConfigManager configManager;
    private int moveUpKey;
    private int moveDownKey;
    private int moveLeftKey;
    private int moveRightKey;
    private int shootKey;
    private Animation<TextureRegion> walkDown, walkUp, walkRight, walkLeft;
    private boolean isShooting = false; // Флаг для отслеживания стрельбы
    private final float shootCooldown; // Интервал между выстрелами (в секундах)


    private String direction = "down"; // Текущее направление персонажа

    public HandleInput(Coordinates coordinates, Weapon weapon, float worldWidth, float worldHeight) {
        this.coordinates = coordinates;
        this.weapon = weapon;
        this.WORLD_HEIGHT = worldHeight;
        this.WORLD_WIDTH = worldWidth;
        this.configManager = new ConfigManager();
        this.shootCooldown = weapon.getFireRate();
    }

    public void loadControlKeys() {
        moveUpKey = Input.Keys.valueOf(configManager.getStringValue("controls", "moveUp"));
        moveDownKey = Input.Keys.valueOf(configManager.getStringValue("controls", "moveDown"));
        moveLeftKey = Input.Keys.valueOf(configManager.getStringValue("controls", "moveLeft"));
        moveRightKey = Input.Keys.valueOf(configManager.getStringValue("controls", "moveRight"));
        shootKey = Input.Keys.valueOf(configManager.getStringValue("controls", "attack"));
        if (shootKey <= 0) {
            shootKey = ButtonHelper.valueOf(configManager.getStringValue("controls", "attack"));
            isMouse = true;
        } else
            isMouse = false;

    }

    private void diagonalMovement(float x, float y, float diagonalSpeed, Coordinates coordinates, float stateTime) {

        if (Gdx.input.isKeyPressed(moveRightKey) && Gdx.input.isKeyPressed(moveUpKey)) {
            x += diagonalSpeed; // Диагональ вправо-вверх
            y += diagonalSpeed;
            currentFrame = walkRight.getKeyFrame(stateTime, true);
        } else if (Gdx.input.isKeyPressed(moveRightKey) && Gdx.input.isKeyPressed(moveDownKey)) {
            x += diagonalSpeed; // Диагональ вправо-вниз
            y -= diagonalSpeed;
            currentFrame = walkRight.getKeyFrame(stateTime, true);
        } else if (Gdx.input.isKeyPressed(moveLeftKey) && Gdx.input.isKeyPressed(moveUpKey)) {
            x -= diagonalSpeed; // Диагональ влево-вверх
            y += diagonalSpeed;
            currentFrame = walkLeft.getKeyFrame(stateTime, true);
        } else if (Gdx.input.isKeyPressed(moveLeftKey) && Gdx.input.isKeyPressed(moveDownKey)) {
            x -= diagonalSpeed; // Диагональ влево-вниз
            y -= diagonalSpeed;
            currentFrame = walkLeft.getKeyFrame(stateTime, true);
        }
        coordinates.setX(x);
        coordinates.setY(y);


    }

    private void horizontalMovement(float x, float y, float speed, Coordinates coordinates, float stateTime) {

        if (Gdx.input.isKeyPressed(moveRightKey)) {
            x += speed; // Движение вправо
            currentFrame = walkRight.getKeyFrame(stateTime, true);
        } else if (Gdx.input.isKeyPressed(moveLeftKey)) {
            x -= speed; // Движение влево
            currentFrame = walkLeft.getKeyFrame(stateTime, true);
        } else if (Gdx.input.isKeyPressed(moveUpKey)) {
            y += speed; // Движение вверх
            currentFrame = walkUp.getKeyFrame(stateTime, true);
        } else if (Gdx.input.isKeyPressed(moveDownKey)) {
            y -= speed; // Движение вниз
            currentFrame = walkDown.getKeyFrame(stateTime, true);
        }
        coordinates.setX(x);
        coordinates.setY(y);
    }

    private void shoot(float mouseX, float mouseY){
        isShooting = true;
        // Вычисляем начальные координаты пули (центр персонажа)
        float startX = coordinates.getX() + playerWidth / 2;
        float startY = coordinates.getY() + playerHeight / 2;
        // Стреляем в направлении курсора
        weapon.shoot(startX, startY, mouseX, mouseY);
    }
    public void handleInput(float stateTime, float mouseX, float mouseY) {
        float speed = 5f; // Базовая скорость
        float diagonalSpeed = (float) (speed / Math.sqrt(2)); // Уменьшенная скорость для диагонали
        boolean movingHorizontally = false;
        boolean movingVertically = false;

        characterSheet = new Texture("MainCharacter.png");
        int frameWidth = characterSheet.getWidth() / 3;
        int frameHeight = characterSheet.getHeight() / 4;
        TextureRegion[][] tempFrames = TextureRegion.split(characterSheet, frameWidth, frameHeight);
        walkDown = new Animation<>(0.15f, tempFrames[0]);
        walkUp = new Animation<>(0.15f, tempFrames[1]);
        walkRight = new Animation<>(0.15f, tempFrames[2]);
        walkLeft = new Animation<>(0.15f, tempFrames[3]);

        float PLAYER_WIDTH = 32;

        if (isMouse) {
            if (Gdx.input.isButtonPressed(shootKey)) {
                shoot(mouseX,mouseY);
            }
            else {
                isShooting = false;
            }
        } else {
            if(Gdx.input.isKeyPressed(shootKey)){
                shoot(mouseX,mouseY);
            }
            else {
                isShooting = false;
            }
        }

        if (Gdx.input.isKeyPressed(moveRightKey) && coordinates.getX() + PLAYER_WIDTH < WORLD_WIDTH) {
            movingHorizontally = true;
            direction = "right";
        }
        if (Gdx.input.isKeyPressed(moveLeftKey) && coordinates.getX() > 0) {
            movingHorizontally = true;
            direction = "left";
        }
        // Высота персонажа
        float PLAYER_HEIGHT = 32;
        if (Gdx.input.isKeyPressed(moveUpKey) && coordinates.getY() + PLAYER_HEIGHT < WORLD_HEIGHT) {
            movingVertically = true;
            direction = "up";
        }
        if (Gdx.input.isKeyPressed(moveDownKey) && coordinates.getY() > 0) {
            movingVertically = true;
            direction = "down";
        }

        //Рассчет координат перемещения
        if (movingHorizontally && movingVertically) {
            diagonalMovement(coordinates.getX(), coordinates.getY(), diagonalSpeed, coordinates, stateTime);
        } else {
            horizontalMovement(coordinates.getX(), coordinates.getY(), diagonalSpeed, coordinates, stateTime);
        }

        // Если персонаж перестал двигаться — остаётся последний кадр в текущем направлении
        if (!movingHorizontally && !movingVertically) {
            switch (direction) {
                case "right":
                    currentFrame = walkRight.getKeyFrame(0);
                    break;
                case "left":
                    currentFrame = walkLeft.getKeyFrame(0);
                    break;
                case "up":
                    currentFrame = walkUp.getKeyFrame(0);
                    break;
                case "down":
                    currentFrame = walkDown.getKeyFrame(0);
                    break;
            }
        }
    }
}
