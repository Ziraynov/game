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

public class SettingsScreen implements Screen {
    private final MyGame game;
    private Stage stage;
    private final ConfigManager configManager;

    // Виджеты
    private Label upKeyLabel;
    private Label downKeyLabel;
    private Label leftKeyLabel;
    private Label rightKeyLabel;
    private SelectBox<String> resolutionBox;
    private CheckBox fullscreenCheckbox;

    private boolean waitingForInput = false; // Флаг ожидания ввода
    private String currentKeyAction = "";   // Какую клавишу настраиваем (moveUp, moveDown, и т.д.)

    public SettingsScreen(MyGame game) {
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
        resolutionBox.setSelected(configManager.getValue("graphics", "resolution"));

        // Чекбокс для полноэкранного режима
        fullscreenCheckbox = new CheckBox(" Fullscreen", skin);
        fullscreenCheckbox.setChecked(configManager.getBoolean("graphics", "fullscreen"));

        // Настройки клавиш
        upKeyLabel = createKeyLabel("Move Up", "moveUp", skin);
        downKeyLabel = createKeyLabel("Move Down", "moveDown", skin);
        leftKeyLabel = createKeyLabel("Move Left", "moveLeft", skin);
        rightKeyLabel = createKeyLabel("Move Right", "moveRight", skin);

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
        addKeySettingRow(table, "Change Up Key", "moveUp", upKeyLabel, skin);
        addKeySettingRow(table, "Change Down Key", "moveDown", downKeyLabel, skin);
        addKeySettingRow(table, "Change Left Key", "moveLeft", leftKeyLabel, skin);
        addKeySettingRow(table, "Change Right Key", "moveRight", rightKeyLabel, skin);
        table.add(applyButton).padTop(20).width(150).height(50).row();
        table.add(backButton).padTop(20).width(150).height(50).row();

        stage.addActor(table);
    }

    private Label createKeyLabel(String actionName, String keyPreference, Skin skin) {
        String key = configManager.getValue("controls", keyPreference);
        return new Label(actionName + ": " + key, skin);
    }

    private void addKeySettingRow(Table table, String buttonText, String keyPreference, Label keyLabel, Skin skin) {
        TextButton button = new TextButton(buttonText, skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitingForInput = true;
                currentKeyAction = keyPreference;
                System.out.println("Press any key for " + buttonText + "...");
            }
        });
        table.add(keyLabel).padBottom(10).left();
        table.add(button).padBottom(10).row();
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
        configManager.setValue("graphics", "resolution", resolution);
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

        System.out.println("Settings applied: Resolution " + resolution + ", Fullscreen: " + fullscreen);
    }

    private void returnToPreviousScreen() {
        if (!game.isResume())
            game.setScreen(new MenuScreen(game));
        else {
            game.setScreen(new ResumeGameScreen(game));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (waitingForInput) {
            int keyCode = getPressedKey();
            if (keyCode != -1) {
                String keyName = Input.Keys.toString(keyCode);
                configManager.setValue("controls", currentKeyAction, keyName);
                configManager.saveConfig();
                updateKeyLabel(currentKeyAction, keyName);
                waitingForInput = false;
                currentKeyAction = "";
            }
        }

        stage.act(delta);
        stage.draw();
    }

    private void updateKeyLabel(String keyPreference, String keyName) {
        switch (keyPreference) {
            case "moveUp":
                upKeyLabel.setText("Move Up: " + keyName);
                break;
            case "moveDown":
                downKeyLabel.setText("Move Down: " + keyName);
                break;
            case "moveLeft":
                leftKeyLabel.setText("Move Left: " + keyName);
                break;
            case "moveRight":
                rightKeyLabel.setText("Move Right: " + keyName);
                break;
        }
    }

    private int getPressedKey() {
        for (int i = 0; i < Input.Keys.MAX_KEYCODE; i++) {
            if (Gdx.input.isKeyPressed(i)) {
                return i;
            }
        }
        return -1;
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
    public void pause() {}

    @Override
    public void resume() {}
}
