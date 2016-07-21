package parsifalgame.screens;

import parsifalgame.ClientManager;
import parsifalgame.UIElements;

public class UmpireScreen extends ObserverScreen {

	public UmpireScreen(UIElements uiElements, ClientManager manager) {
		super(uiElements, manager);
	}

	@Override
	protected SubScreen makeSubScreen() {
		return new ResultsSubScreen(this, centralArea);
	}
}
