package Classes;

import Screens.GameScreen;
import Screens.MenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import lombok.Getter;
import lombok.Setter;

public class MyGame extends Game {

    @Getter
    @Setter
    private GameScreen previousScreen; // Хранит экран перед паузой
    private boolean isResume = false;

    @Getter
    private ConfigManager configManager; // Менеджер конфигурации

    public boolean isResume() {
        return isResume;
    }

    public void setResume(boolean resume) {
        isResume = resume;
    }

    @Override
    public void create() {
        // Инициализация менеджера конфигурации
        configManager = new ConfigManager();

        // Проверяем, был ли первый запуск
        if (!configManager.hasKey("graphics", "resolution")) {
            // Если настройки отсутствуют, устанавливаем значения по умолчанию
            configManager.setValue("graphics", "resolution", "800x600");
            configManager.setBoolean("graphics", "fullscreen", false);
            configManager.saveConfig();
        }

        // Загружаем настройки экрана
        String resolution = configManager.getValue("graphics", "resolution");
        boolean fullscreen = configManager.getBoolean("graphics", "fullscreen");

        String[] resParts = resolution.split("x");
        int screenWidth = Integer.parseInt(resParts[0]);
        int screenHeight = Integer.parseInt(resParts[1]);

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
