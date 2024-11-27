package Classes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class MyGame extends Game {

    private GameScreen previousScreen; // Хранит экран перед паузой
    private boolean isResume = false;

    public boolean isResume() {
        return isResume;
    }

    public void setResume(boolean resume) {
        isResume = resume;
    }

    public void setPreviousScreen(GameScreen screen) {
        this.previousScreen = screen;
    }

    public GameScreen getPreviousScreen() {
        return previousScreen;
    }

    @Override
    public void create() {
        // Загружаем настройки
        Preferences preferences = Gdx.app.getPreferences("GameSettings");

        // Проверяем, был ли первый запуск
        if (!preferences.contains("screenWidth")) {
            // Если настройки отсутствуют, устанавливаем значения по умолчанию
            preferences.putInteger("screenWidth", 800);
            preferences.putInteger("screenHeight", 600);
            preferences.putBoolean("fullscreen", false);
            preferences.flush();
        }

        String resolution = preferences.getString("resolution","800x600");
        String[] resParts = resolution.split("x");
        int screenWidth = Integer.parseInt(resParts[0]);
        int screenHeight = Integer.parseInt(resParts[1]);
        boolean fullscreen = preferences.getBoolean("fullscreen", false);

        // Применяем настройки
        if (fullscreen) {
            // Если включен полноэкранный режим, переключаемся на него
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            // Если полноэкранный режим отключен, устанавливаем окно с заданным разрешением
            Gdx.graphics.setWindowedMode(screenWidth, screenHeight);
        }

        // Переход к экрану меню
        this.setScreen(new MenuScreen(this)); // Начинаем с экрана меню
    }

    @Override
    public void render() {
        super.render(); // Отображаем текущий экран
    }

    @Override
    public void dispose() {
        // Уничтожение ресурсов
        super.dispose();
    }
}
