package parsifalgame.screens;

import parsifalgame.ClientManager;
import parsifalgame.UIElements;

public class UmpireScreen extends ObserverScreen {

	public UIElements uiElements;
	public ClientManager manager;
	
	public UmpireScreen(UIElements uiElements, ClientManager manager) {
		super(uiElements, manager);
		this.uiElements = uiElements;
		this.manager = manager;
	}

	@Override
	protected SubScreen makeSubScreen() {
		return new ResultsSubScreen(this, centralArea);
	}
}
