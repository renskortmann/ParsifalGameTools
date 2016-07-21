package parsifalgame.screens;

import parsifalgame.ClientManager;
import parsifalgame.GameManager;
import parsifalgame.UIElements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class BasicScreen extends ScreenAdapter {

	protected final Stage stage;
	protected final Skin skin;
	protected final Table mainTable;
	protected final GameManager gameManager;

	protected BasicScreen(UIElements uiElements, GameManager manager) {
		this.gameManager = manager;
		this.stage = new Stage(new ScreenViewport());
		this.skin = uiElements.getSkin();
		this.stage.setDebugAll(UIElements.DEBUG);
		this.mainTable = new Table();
	}
	
	public ClientManager getAsClient(){
		return (ClientManager)gameManager;
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		
		mainTable.setFillParent(true);
		stage.addActor(mainTable);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void hide() {
		mainTable.clear();
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	protected void updateScreen() {
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		updateScreen();

		stage.act();
		stage.draw();
	}
}
