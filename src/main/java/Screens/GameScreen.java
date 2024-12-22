package Screens;

import Classes.ConfigManager;
import Classes.Coordinates;
import Classes.HandleInput;
import Classes.MyGame;
import Weapon.Weapon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;


public class GameScreen implements Screen {
    private final MyGame game;
    private SpriteBatch batch;
    private final ConfigManager configManager;
    // Пол
    private Texture floorTexture;
    // Персонаж


    private final Coordinates coordinates = new Coordinates(100f, 100f);
    private OrthographicCamera camera;
    private float stateTime;
    // Границы уровня и экрана
    private final float WORLD_WIDTH = 3000; // Ширина уровня (совпадает с экраном)
    private final float WORLD_HEIGHT = 2000; // Высота уровня (совпадает с экраном)
    private boolean isPaused;
    public final HandleInput handleInput;

    Weapon weapon = new Weapon(
            "M4-1S",
            0.2f,
            10f,
            500f,
            300f,
            1f,
            1000,
            Gdx.audio.newSound(Gdx.files.internal("Weapon\\Weapon-Sound.mp3")),
            new Texture("Weapon\\bullet.png")
    );


    public GameScreen(MyGame game) {
        this.game = game;
        this.configManager = new ConfigManager();
        handleInput = new HandleInput(coordinates, weapon, WORLD_WIDTH, WORLD_HEIGHT);// Инициализируем менеджер настроек
        handleInput.loadControlKeys();
        isPaused = false; // Изначально игра не на паузе
    }


    public void loadCamera() {
        String resolution = configManager.getValue("graphics", "resolution"); // Ширина окна (пиксели)
        String[] resParts = resolution.split("x");
        int viewportWidth = Integer.parseInt(resParts[0]);
        int viewportHeight = Integer.parseInt(resParts[1]);
// Коэффициент для масштабирования (зависит от базового разрешения)
        float scaleFactor = .5f; // Можно варьировать для уменьшения/увеличения видимой области

// Устанавливаем размеры камеры (увеличиваем или уменьшаем область просмотра)
        camera = new OrthographicCamera(viewportWidth * scaleFactor, viewportHeight * scaleFactor);

// Центрируем камеру на начальных координатах персонажа
        camera.position.set(coordinates.getX(), coordinates.getY(), 0);
        camera.update(); // Размеры камеры совпадают с уровнем
    }

    @Override
    public void show() {

        if (batch == null) {
            batch = new SpriteBatch();

            // Пол
            floorTexture = new Texture("floor\\Floor1.png");



            stateTime = 0f;


            // Камера
            loadCamera();

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
        Vector3 worldCoordinates = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        float mouseX = worldCoordinates.x;
        float mouseY = worldCoordinates.y; // Учитываем переворот Y-координаты
        handleInput.handleInput(stateTime, mouseX, mouseY);
        TextureRegion currentFrame = handleInput.getCurrentFrame();

        // Обновляем камеру (следим за персонажем)
        // Ограничиваем движение камеры за пределы уровня
        float cameraX = Math.max(camera.viewportWidth / 2, Math.min(coordinates.getX(), WORLD_WIDTH - camera.viewportWidth / 2));
        float cameraY = Math.max(camera.viewportHeight / 2, Math.min(coordinates.getY(), WORLD_HEIGHT - camera.viewportHeight / 2));

        camera.position.set(cameraX, cameraY, 0); // Центрируем камеру в пределах уровня
        camera.update();
        weapon.update(delta);

        // Устанавливаем камеру в SpriteBatch
        batch.setProjectionMatrix(camera.combined);

        // Рисуем
        batch.begin();
        drawFloor(); // Рисуем пол
        batch.draw(currentFrame, coordinates.getX(), coordinates.getY());
        weapon.render(batch);// Рисуем персонажа
        batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {

            renderPauseMenu(isPaused = true);
        }
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
        handleInput.loadControlKeys(); // Перезагружаем клавиши из настроек
        isPaused = false;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
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
    }
}