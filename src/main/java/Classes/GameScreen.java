package Classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class GameScreen implements Screen {
    private MyGame game;

    private SpriteBatch batch;

    // Пол
    private Texture floorTexture;

    // Персонаж
    private Texture characterSheet;
    private Animation<TextureRegion> walkDown, walkUp, walkRight, walkLeft;
    private TextureRegion currentFrame;
    private float stateTime;
    private String direction;
    private Coordinates coordinates = new Coordinates(100f, 100f);


    // Камера
    private OrthographicCamera camera;

    // Границы уровня и экрана
    private final float WORLD_WIDTH = 3000; // Ширина уровня (совпадает с экраном)
    private final float WORLD_HEIGHT = 2000; // Высота уровня (совпадает с экраном)
    private final float PLAYER_WIDTH = 32; // Ширина персонажа
    private final float PLAYER_HEIGHT = 32; // Высота персонажа

    private boolean isPaused; // Флаг состояния паузы


    public GameScreen(MyGame game) {
        this.game = game;
        isPaused = false; // Изначально игра не на паузе
    }

    @Override
    public void show() {

        if (batch == null) {
            batch = new SpriteBatch();

            // Пол
            floorTexture = new Texture("floor\\Floor1.png");

            // Персонаж
            characterSheet = new Texture("MainCharacter.png");
            int frameWidth = characterSheet.getWidth() / 3;
            int frameHeight = characterSheet.getHeight() / 4;
            TextureRegion[][] tempFrames = TextureRegion.split(characterSheet, frameWidth, frameHeight);
            walkDown = new Animation<>(0.15f, tempFrames[0]);
            walkUp = new Animation<>(0.15f, tempFrames[1]);
            walkRight = new Animation<>(0.15f, tempFrames[2]);
            walkLeft = new Animation<>(0.15f, tempFrames[3]);

            direction = "down";
            stateTime = 0f;
            currentFrame = tempFrames[0][0];

            // Камера
            camera = new OrthographicCamera();
            camera.setToOrtho(false, 400, 300); // Размеры камеры совпадают с уровнем
            camera.position.set(coordinates.getX(), coordinates.getY(), 0); // Устанавливаем начальное положение камеры

        }
    }

    @Override
    public void render(float delta) {
        if (!isPaused) {
            renderGame(delta);
        } else {
            renderPauseMenu(false);
        }
    }

    public void renderGame(float delta) {
        // Очищаем экран
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);

        // Обновляем время анимации
        stateTime += delta;

        // Обрабатываем ввод и движение персонажа
        handleInput();

        // Обновляем камеру (следим за персонажем)
        // Ограничиваем движение камеры за пределы уровня
        float cameraX = Math.max(camera.viewportWidth / 2, Math.min(coordinates.getX(), WORLD_WIDTH - camera.viewportWidth / 2));
        float cameraY = Math.max(camera.viewportHeight / 2, Math.min(coordinates.getY(), WORLD_HEIGHT - camera.viewportHeight / 2));

        camera.position.set(cameraX, cameraY, 0); // Центрируем камеру в пределах уровня
        camera.update();

        // Устанавливаем камеру в SpriteBatch
        batch.setProjectionMatrix(camera.combined);

        // Рисуем
        batch.begin();
        drawFloor(); // Рисуем пол
        batch.draw(currentFrame, coordinates.getX(), coordinates.getY()); // Рисуем персонажа
        batch.end();
    }


    public void renderPauseMenu(boolean isPaused) {

        if (isPaused) {
            game.setPreviousScreen(this); // Сохраняем текущий экран
            game.setScreen(new ResumeGameScreen(game));  // Переходим в меню паузы
        }
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    private void diagonalMovement(float x, float y, float diagonalSpeed, Coordinates coordinates) {

        if (Gdx.input.isKeyPressed(Input.Keys.D) && Gdx.input.isKeyPressed(Input.Keys.W)) {
            x += diagonalSpeed; // Диагональ вправо-вверх
            y += diagonalSpeed;
            currentFrame = walkRight.getKeyFrame(stateTime, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D) && Gdx.input.isKeyPressed(Input.Keys.S)) {
            x += diagonalSpeed; // Диагональ вправо-вниз
            y -= diagonalSpeed;
            currentFrame = walkRight.getKeyFrame(stateTime, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A) && Gdx.input.isKeyPressed(Input.Keys.W)) {
            x -= diagonalSpeed; // Диагональ влево-вверх
            y += diagonalSpeed;
            currentFrame = walkLeft.getKeyFrame(stateTime, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A) && Gdx.input.isKeyPressed(Input.Keys.S)) {
            x -= diagonalSpeed; // Диагональ влево-вниз
            y -= diagonalSpeed;
            currentFrame = walkLeft.getKeyFrame(stateTime, true);
        }
        coordinates.setX(x);
        coordinates.setY(y);


    }
    private void horizontalMovement(float x, float y, float speed, Coordinates coordinates) {

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed; // Движение вправо
            currentFrame = walkRight.getKeyFrame(stateTime, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= speed; // Движение влево
            currentFrame = walkLeft.getKeyFrame(stateTime, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += speed; // Движение вверх
            currentFrame = walkUp.getKeyFrame(stateTime, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= speed; // Движение вниз
            currentFrame = walkDown.getKeyFrame(stateTime, true);
        }
        coordinates.setX(x);
        coordinates.setY(y);
    }

    private void handleInput() {
        float speed = 5f; // Базовая скорость
        float diagonalSpeed = (float) (speed / Math.sqrt(2)); // Уменьшенная скорость для диагонали

        boolean movingHorizontally = false;
        boolean movingVertically = false;

        // Пауза (ESCAPE)
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {

            renderPauseMenu(isPaused = true);
        }

        // Проверяем ввод для движения
        if (Gdx.input.isKeyPressed(Input.Keys.D) && coordinates.getX() + PLAYER_WIDTH < WORLD_WIDTH) {
            movingHorizontally = true;
            direction = "right";
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) && coordinates.getX() > 0) {
            movingHorizontally = true;
            direction = "left";
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) && coordinates.getY() + PLAYER_HEIGHT < WORLD_HEIGHT) {
            movingVertically = true;
            direction = "up";
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) && coordinates.getY() > 0) {
            movingVertically = true;
            direction = "down";
        }

        //Рассчет координат перемещения
        if (movingHorizontally && movingVertically) {
            diagonalMovement(coordinates.getX(), coordinates.getY(), diagonalSpeed, coordinates);
        }
        else{
           horizontalMovement(coordinates.getX(), coordinates.getY(), diagonalSpeed, coordinates);
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


    private void drawFloor() {
        int tileWidth = floorTexture.getWidth();
        int tileHeight = floorTexture.getHeight();

        // Рисуем пол только в пределах камеры
        int startX = (int) (camera.position.x - camera.viewportWidth / 2) / tileWidth;
        int startY = (int) (camera.position.y - camera.viewportHeight / 2) / tileHeight;
        int endX = (int) (camera.position.x + camera.viewportWidth / 2) / tileWidth;
        int endY = (int) (camera.position.y + camera.viewportHeight / 2) / tileHeight;

        for (int i = startX - 1; i <= endX + 1; i++) {
            for (int j = startY - 1; j <= endY + 1; j++) {
                if (i * tileWidth >= 0 && i * tileWidth < WORLD_WIDTH && j * tileHeight >= 0 && j * tileHeight < WORLD_HEIGHT) {
                    batch.draw(floorTexture, i * tileWidth, j * tileHeight);
                }
            }
        }
    }

    @Override
    public void hide() {
        // Освобождение ресурсов, если это необходимо
    }

    @Override
    public void dispose() {
        batch.dispose();
        floorTexture.dispose();
        characterSheet.dispose();
    }
}
