package Classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
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
    private Preferences preferences;

    // Виджеты
    private Label upKeyLabel;
    private Label downKeyLabel;
    private Label leftKeyLabel;
    private Label rightKeyLabel;
    private SelectBox<String> resolutionBox;
    private CheckBox fullscreenCheckbox;

    private boolean waitingForInput = false; // Флаг ожидания ввода
    private String currentKeyAction = "";   // Какую клавишу настраиваем (up, down, left, right)

    public SettingsScreen(MyGame game) {
        this.game = game;
        preferences = Gdx.app.getPreferences("GameSettings");
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
        resolutionBox.setSelected(preferences.getString("resolution", "800x600"));

        // Чекбокс для полноэкранного режима
        fullscreenCheckbox = new CheckBox(" Fullscreen", skin);
        fullscreenCheckbox.setChecked(preferences.getBoolean("fullscreen", false));


        // Настройки клавиш
        upKeyLabel = new Label("Move Up: " + getKeyName(preferences.getInteger("upKey", Input.Keys.W)), skin);
        downKeyLabel = new Label("Move Down: " + getKeyName(preferences.getInteger("downKey", Input.Keys.S)), skin);
        leftKeyLabel = new Label("Move Left: " + getKeyName(preferences.getInteger("leftKey", Input.Keys.A)), skin);
        rightKeyLabel = new Label("Move Right: " + getKeyName(preferences.getInteger("rightKey", Input.Keys.D)), skin);

        TextButton applyButton = new TextButton("Apply", skin);
        applyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySettings();
            }
        });
        // Кнопка для изменения клавиши "Вверх"
        TextButton changeUpKeyButton = new TextButton("Change Up Key", skin);
        changeUpKeyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitingForInput = true;
                currentKeyAction = "upKey";
                System.out.println("Press any key for Move Up...");
            }
        });

        // Кнопка для изменения клавиши "Вниз"
        TextButton changeDownKeyButton = new TextButton("Change Down Key", skin);
        changeDownKeyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitingForInput = true;
                currentKeyAction = "downKey";
                System.out.println("Press any key for Move Down...");
            }
        });

        // Кнопка для изменения клавиши "Влево"
        TextButton changeLeftKeyButton = new TextButton("Change Left Key", skin);
        changeLeftKeyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitingForInput = true;
                currentKeyAction = "leftKey";
                System.out.println("Press any key for Move Left...");
            }
        });

        // Кнопка для изменения клавиши "Вправо"
        TextButton changeRightKeyButton = new TextButton("Change Right Key", skin);
        changeRightKeyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitingForInput = true;
                currentKeyAction = "rightKey";
                System.out.println("Press any key for Move Right...");
            }
        });

        // Кнопка "Назад"
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!game.isResume())
                    game.setScreen(new MenuScreen(game));
                else {
                    GameScreen gameScreen = game.getPreviousScreen();
                    gameScreen.setPaused(false);
                    game.setScreen(gameScreen);
                }
            }
        });

        // Расположение элементов на таблице
        table.add(resolutionLabel).padBottom(10).left();
        table.add(resolutionBox).padBottom(10).row();
        table.add(fullscreenCheckbox).padBottom(20).left().row();
        table.add(upKeyLabel).padBottom(10).left();
        table.add(changeUpKeyButton).padBottom(10).row();
        table.add(downKeyLabel).padBottom(10).left();
        table.add(changeDownKeyButton).padBottom(10).row();
        table.add(leftKeyLabel).padBottom(10).left();
        table.add(changeLeftKeyButton).padBottom(10).row();
        table.add(rightKeyLabel).padBottom(10).left();
        table.add(changeRightKeyButton).padBottom(10).row();
        table.add(applyButton).padTop(20).width(150).height(50).row();
        table.add(backButton).padTop(20).width(150).height(50).row();

        stage.addActor(table);
    }

        private void applySettings() {
            // Сохраняем выбранные настройки
            String resolution = resolutionBox.getSelected();
            boolean fullscreen = fullscreenCheckbox.isChecked();

            // Разделяем разрешение на ширину и высоту
            String[] resParts = resolution.split("x");
            int width = Integer.parseInt(resParts[0]);
            int height = Integer.parseInt(resParts[1]);

            // Сохраняем настройки в Preferences
            preferences.putString("resolution", resolution);
            preferences.putBoolean("fullscreen", fullscreen);
            preferences.flush();

            // Применяем настройки
            if (fullscreen) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            } else {
                Gdx.graphics.setWindowedMode(width, height);
            }
        }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Если ожидается ввод клавиши
        if (waitingForInput) {
            int keyCode = getPressedKey();
            if (keyCode != -1) {
                applyKeyBinding(keyCode);
                waitingForInput = false;
                currentKeyAction = "";
            }
        }

        stage.act(delta);
        stage.draw();
    }

    private int getPressedKey() {
        for (int i = 0; i < Input.Keys.MAX_KEYCODE; i++) {
            if (Gdx.input.isKeyPressed(i)) {
                return i;
            }
        }
        return -1; // Ничего не нажато
    }

    private void applyKeyBinding(int keyCode) {
        preferences.putInteger(currentKeyAction, keyCode);
        preferences.flush();

        // Обновляем отображение надписей
        if (currentKeyAction.equals("upKey")) {
            upKeyLabel.setText("Move Up: " + getKeyName(keyCode));
        } else if (currentKeyAction.equals("downKey")) {
            downKeyLabel.setText("Move Down: " + getKeyName(keyCode));
        } else if (currentKeyAction.equals("leftKey")) {
            leftKeyLabel.setText("Move Left: " + getKeyName(keyCode));
        } else if (currentKeyAction.equals("rightKey")) {
            rightKeyLabel.setText("Move Right: " + getKeyName(keyCode));
        }

        System.out.println("Key set for " + currentKeyAction + ": " + getKeyName(keyCode));
    }

    private String getKeyName(int keyCode) {
        return Input.Keys.toString(keyCode); // Преобразование кода клавиши в строку
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
