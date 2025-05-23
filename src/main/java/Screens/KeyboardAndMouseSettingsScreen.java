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

public class KeyboardAndMouseSettingsScreen implements Screen {
    private final MyGame game;
    private Stage stage;
    private final ConfigManager configManager;

    // Виджеты
    private Label upKeyLabel;
    private Label downKeyLabel;
    private Label leftKeyLabel;
    private Label rightKeyLabel;
    private Label attackKeyLabel;


    private boolean waitingForInput = false; // Флаг ожидания ввода
    private String currentKeyAction = "";   // Какую клавишу настраиваем (moveUp, moveDown, и т.д.)

    public KeyboardAndMouseSettingsScreen(MyGame game) {
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

        // Настройки клавиш
        upKeyLabel = createKeyLabel("Move Up", "moveUp", skin);
        downKeyLabel = createKeyLabel("Move Down", "moveDown", skin);
        leftKeyLabel = createKeyLabel("Move Left", "moveLeft", skin);
        rightKeyLabel = createKeyLabel("Move Right", "moveRight", skin);
        attackKeyLabel = createKeyLabel("Attack","attack",skin);

        // Кнопка "Назад"
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                returnToPreviousScreen();
            }
        });

        // Расположение элементов на таблице
        addKeySettingRow(table, "Change Up Key", "moveUp", upKeyLabel, skin);
        addKeySettingRow(table, "Change Down Key", "moveDown", downKeyLabel, skin);
        addKeySettingRow(table, "Change Left Key", "moveLeft", leftKeyLabel, skin);
        addKeySettingRow(table, "Change Right Key", "moveRight", rightKeyLabel, skin);
        addKeySettingRow(table, "Change Attack Key", "attack", attackKeyLabel, skin);
        table.add(backButton).padTop(20).width(150).height(50).row();

        stage.addActor(table);
    }

    private Label createKeyLabel(String actionName, String keyPreference, Skin skin) {
        String key = configManager.getStringValue("controls", keyPreference);
        return new Label(actionName + ": " + key, skin);
    }

    private void addKeySettingRow(Table table, String buttonText, String keyPreference, Label keyLabel, Skin skin) {
        TextButton button = new TextButton(buttonText, skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitingForInput = true;
                currentKeyAction = keyPreference;
            }
        });
        table.add(keyLabel).padBottom(10).left();
        table.add(button).padBottom(10).row();
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

        if (waitingForInput) {
            String keyName = getPressedKey();
            if (keyName != null) {
                configManager.setStringValue("controls", currentKeyAction, keyName); // Сохраняем строку
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
            case "attack":
                attackKeyLabel.setText("Attack: " + keyName);
                break;
        }
    }
    private String getPressedKey() {
        // Проверка клавиш клавиатуры
        for (int i = 0; i < Input.Keys.MAX_KEYCODE; i++) {
            if (Gdx.input.isKeyPressed(i)) {
                return Input.Keys.toString(i); // Возвращает название клавиши
            }
        }

        // Проверка кнопок мыши
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            return "Mouse Left";
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            return "Mouse Right";
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            return "Mouse Middle";
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.BACK)) {
            return "Mouse Back";
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.FORWARD)) {
            return "Mouse Forward";
        }

        return null; // Ничего не нажато
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
