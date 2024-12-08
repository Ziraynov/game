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

public class ResumeGameScreen implements Screen {
    private Stage stage;
    private TextButton playButton;
    private TextButton settingsButton;
    private TextButton exitButton;
    private BitmapFont font;

    private MyGame game;

    public ResumeGameScreen(MyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont();
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;

        // Кнопка "Resume Game"
        playButton = new TextButton("Resume Game", style);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen gameScreen = game.getPreviousScreen();
                gameScreen.loadControlKeys();
                gameScreen.setPaused(false);
                game.setScreen(gameScreen);
            }
        });

        // Кнопка "Settings" (пока не реализована)
        settingsButton = new TextButton("Settings", style);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setResume(true);
                game.setScreen(new SettingsScreen(game));// Переход на экран настроек
            }
        });

        // Кнопка "Exit to Menu"
        exitButton = new TextButton("Exit to Menu", style);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game)); // Возврат в главное меню
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
        stage.dispose();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
