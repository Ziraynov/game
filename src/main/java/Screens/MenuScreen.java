package Screens;

import Classes.MyGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;



public class MenuScreen implements Screen {
    private Stage stage;

    private final MyGame game;

    public MenuScreen(MyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        BitmapFont font = new BitmapFont();
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;

        // Кнопка "Игра"
        TextButton playButton = new TextButton("Game", style);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game)); // Запускаем игровой экран
            }
        });

        // Кнопка "Настройки"
        TextButton settingsButton = new TextButton("Settings", style);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game)); // Переход на экран настроек
            }
        });

        // Кнопка "Выход"
        TextButton exitButton = new TextButton("Exit", style);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit(); // Закрытие приложения
            }
        });

        Table table = new Table();
        table.center();
        table.setFillParent(true);
        table.add(playButton).padBottom(20).width(200).height(50).row();
        table.add(settingsButton).padBottom(20).width(200).height(50).row();
        table.add(exitButton).width(200).height(50).row();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        // Очищаем экран
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Отображаем интерфейс
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
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

    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
