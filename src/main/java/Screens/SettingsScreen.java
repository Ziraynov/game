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
    private Label attackKeyLabel;
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

        TextButton resolutionButton = new TextButton("Resolution", skin);
        resolutionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resolutionSettings();
            }
        });

        TextButton volumeButton = new TextButton("Volume", skin);
        volumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                volumeSettings();
            }
        });

        TextButton KeyboardAndMouseButton = new TextButton("Keyboard and mouse", skin);
        KeyboardAndMouseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                keyboardAndMouseSettings();
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
        table.add(resolutionButton).padTop(20).width(150).height(50).row();
        table.add(volumeButton).padTop(20).width(150).height(50).row();
        table.add(KeyboardAndMouseButton).padTop(20).width(150).height(50).row();
        table.add(backButton).padTop(20).width(150).height(50).row();

        stage.addActor(table);
    }

    private void resolutionSettings() {
        game.setScreen(new ResolutionSettingsScreen(game));
    }

    private void volumeSettings() {
        game.setScreen(new VolumeSettingsScreen(game));
    }

    private void keyboardAndMouseSettings() {
        game.setScreen(new KeyboardAndMouseSettingsScreen(game));
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
