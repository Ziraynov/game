package Screens;

import Classes.ConfigManager;
import Classes.MyGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import jdk.jfr.FlightRecorder;

public class VolumeSettingsScreen implements Screen {
    private final MyGame game;
    private Stage stage;
    private final ConfigManager configManager;

    private Integer currentVolume;

    public VolumeSettingsScreen(MyGame game) {
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

        Label volumeLabel = new Label("Volume:", skin);
        Slider volumeSlider = new Slider(0, 100, 1, false, skin);

        Integer savedVolume = configManager.getIntValue("audio", "volume");
        Label volumeValueLabel = new Label(Math.round(savedVolume) + "%", skin);
        volumeSlider.setValue(savedVolume);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                currentVolume = Math.round(volumeSlider.getValue());
                volumeValueLabel.setText(currentVolume + "%");
            }
        });
        
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
        table.add(volumeLabel).padRight(10);
        table.add(volumeSlider).width(200).padRight(10);
        table.add(volumeValueLabel);
        table.add(applyButton).padTop(20).width(150).height(50).row();
        table.add(backButton).padTop(20).width(150).height(50).row();

        stage.addActor(table);
    }

    private void applySettings() {
        configManager.setIntValue("audio", "volume", currentVolume);
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
