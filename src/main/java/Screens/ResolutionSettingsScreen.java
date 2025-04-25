package Screens;

import Classes.ConfigManager;
import Classes.MyGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ResolutionSettingsScreen implements Screen {
    private final MyGame game;
    private Stage stage;
    private final ConfigManager configManager;

    // Виджеты
    private SelectBox<String> resolutionBox;
    private CheckBox fullscreenCheckbox;

    public ResolutionSettingsScreen(MyGame game) {
        this.game = game;
        this.configManager = new ConfigManager(); // Загружаем конфигурацию
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Загружаем скин
        Skin skin = new Skin();
        skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
        skin.load(Gdx.files.internal("ui/uiskin.json"));

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        // Заголовок
        Label titleLabel = new Label("Settings", skin);
        titleLabel.setFontScale(2);
        table.add(titleLabel).padBottom(20).row();

        Label resolutionLabel = new Label("Screen Resolution:", skin);
        resolutionBox = new SelectBox<>(skin);
        resolutionBox.setItems("800x600", "1024x768", "1280x720", "1920x1080");
        resolutionBox.setSelected(configManager.getStringValue("graphics", "resolution"));

        // Чекбокс для полноэкранного режима
        fullscreenCheckbox = new CheckBox(" Fullscreen", skin);
        fullscreenCheckbox.setChecked(configManager.getBoolean("graphics", "fullscreen"));

        // Кнопка для применения настроек
        TextButton applyButton = new TextButton("Apply", skin);
        applyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySettings();
            }
        });

        // Кнопка "Назад"
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                returnToPreviousScreen();
            }
        });

        // Расположение элементов на таблице
        table.add(resolutionLabel).padBottom(10).left();
        table.add(resolutionBox).padBottom(10).row();
        table.add(fullscreenCheckbox).padBottom(20).left().row();
        table.add(applyButton).padTop(20).width(150).height(50).row();
        table.add(backButton).padTop(20).width(150).height(50).row();

        stage.addActor(table);
    }

    private void applySettings() {
        String resolution = resolutionBox.getSelected();
        boolean fullscreen = fullscreenCheckbox.isChecked();

        // Валидация разрешения
        if (!resolution.matches("\\d+x\\d+")) {
            System.out.println("Invalid resolution format!");
            return;
        }

        // Сохраняем настройки в ConfigManager
        configManager.setStringValue("graphics", "resolution", resolution);
        configManager.setBoolean("graphics", "fullscreen", fullscreen);
        configManager.saveConfig();

        // Применяем настройки экрана
        String[] resParts = resolution.split("x");
        int width = Integer.parseInt(resParts[0]);
        int height = Integer.parseInt(resParts[1]);

        if (fullscreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(width, height);
        }

    }

    private void returnToPreviousScreen() {
        if (!game.isResume())
            game.setScreen(new SettingsScreen(game));
        else {
            game.setScreen(new ResumeGameScreen(game));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        stage.dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
