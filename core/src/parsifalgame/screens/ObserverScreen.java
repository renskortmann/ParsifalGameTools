package parsifalgame.screens;

import parsifalgame.ClientManager;
import parsifalgame.GameManager;
import parsifalgame.UIElements;
import parsifalgame.state.GameState;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public abstract class ObserverScreen extends TabbedScreen {
//
	public ObserverScreen(UIElements uiElements, ClientManager manager) {
		super(uiElements, manager);
	}

	private Array<TabData> tabs = new Array<TabData>();

	private class TabData {

		TextButton tabButton;
		SubScreen subScreen;
		GameState state;

		TabData(GameState newState) {
			state = newState;

			int index = newState.currentRound;

			tabButton = new TextButton("Ronde " + index, defaultStyle);
			tabButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					deactivateTabButtons();
					switchTo();
				}
			});

			subScreen = makeSubScreen();
			subScreen.updateGameState(GameManager.CLIENT_UNDEFINED, state);
		}

		void switchTo() {
			tabButton.setStyle(selectedStyle);
			subScreen.show();
			visibleScreen = subScreen;
		}

		void remove() {
			tabButton.clear();
			tabButton.remove();
		}
	}
	
	protected abstract SubScreen makeSubScreen();

	private void deactivateTabButtons() {
		for (TabData tab : tabs)
			tab.tabButton.setStyle(defaultStyle);
	}
	
	@Override
	public void updateGameState(int roleIndex, GameState newState) {
		super.updateGameState(roleIndex, newState);
		
		int roundIndex = newState.currentRound;
		for (int i = roundIndex; i < tabs.size; ++i)
			tabs.get(i).remove();
		tabs.truncate(roundIndex);
		
		TabData newTab = new TabData(newState);
		tabs.add(newTab);
		deactivateTabButtons();
		newTab.switchTo();

		tabBar.clearChildren();
		for (TabData tab : tabs)
			tabBar.add(tab.tabButton).expandX().fill();

	}
}
