package parsifalgame.screens;

import parsifalgame.ClientManager;
import parsifalgame.UIElements;

public class KlingsorScreen extends ObserverScreen {

	public KlingsorScreen(UIElements uiElements, ClientManager manager) {
		super(uiElements, manager);
	}

	@Override
	protected SubScreen makeSubScreen() {
		return new ResultsSubScreen(this, centralArea);
	}
}
