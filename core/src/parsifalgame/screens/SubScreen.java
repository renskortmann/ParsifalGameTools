package parsifalgame.screens;

import parsifalgame.state.GameState;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class SubScreen {

	protected final BasicScreen parentScreen;
	protected final Table mainTable;
	protected final Skin skin;

	protected GameState mostRecentState;

	public SubScreen(BasicScreen parentScreen, Table parentTable) {
		this.parentScreen = parentScreen;
		this.mainTable = parentTable;
		this.skin = parentScreen.skin;
	}

	public void show() {
		mainTable.clear();
	}

	protected abstract void update();

	public void updateGameState(int roleIndex, GameState newState) {
		mostRecentState = newState;
	}

}
