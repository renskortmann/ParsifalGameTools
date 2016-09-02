package parsifalgame;

import parsifalgame.net.ClientHandler;
import parsifalgame.net.Message;
import parsifalgame.net.Message.Incoming;
import parsifalgame.net.SocketHandler;
import parsifalgame.screens.BasicScreen;
import parsifalgame.screens.ClientConnectScreen;
import parsifalgame.screens.ConfirmScreen;
import parsifalgame.screens.FacilitatorScreen;
import parsifalgame.screens.KlingsorScreen;
import parsifalgame.screens.ProjectorScreen;
import parsifalgame.screens.TabbedScreen;
import parsifalgame.screens.UmpireScreen;
import parsifalgame.screens.WaitScreen;
import parsifalgame.state.GameState;
import parsifalgame.state.RoundChoices;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Json;

public class ClientManager extends GameManager {

	private Json json = new Json();
	private GameState previousGameState = null;

	private ClientHandler clientHandler;
	private boolean gameStarted = false;
	private boolean roleWasAccepted = false;
	private SocketHandler server = null;

	private ClientConnectScreen connectScreen;
	private WaitScreen waitScreen;
	private TabbedScreen tabbedScreen;
	private ProjectorScreen projectorScreen;
	private ConfirmScreen confirmScreen;

	private BasicScreen currentScreen;

	private int roleIndex = CLIENT_UNDEFINED;
	private boolean testingMode = false;

	protected int currentRound = -1;

	public ClientManager() {
	}

	public ClientManager(int testID) {
		this.roleIndex = testID;
		this.testingMode = true;
	}

	public int getRole() {
		return roleIndex;
	}

	@Override
	public void create() {
		super.create();

		if (testingMode) {

			switch (roleIndex) {
			case CLIENT_KLINGSOR:
				tabbedScreen = new KlingsorScreen(uiElements, this);
				break;
			case CLIENT_UMPIRE:
				tabbedScreen = new UmpireScreen(uiElements, this);
				break;
			case CLIENT_PROJECTOR:
				tabbedScreen = new ProjectorScreen(uiElements, this);
				break;
			default:
				tabbedScreen = new FacilitatorScreen(uiElements, this);
			}
			GameState testState = GameState.randomSimulatedState(8, 8);
			if(roleIndex == CLIENT_PROJECTOR) {
				((ProjectorScreen)tabbedScreen).updateGameState(roleIndex, testState);
			}
			else {
				tabbedScreen.updateGameState(roleIndex, testState);
			}
			setToRoundScreen();

		} else {

			connectScreen = new ClientConnectScreen(uiElements, this);
			waitScreen = new WaitScreen(uiElements, this);

			setToConnectScreen();

			clientHandler = new ClientHandler(this);
		}
	}

	private String failureMessage = "";

	@Override
	public void handleMessage(Incoming newMessage) {
		switch (newMessage.type) {
		case Connect:
			server = newMessage.getSource();
			if (!gameStarted) {
				failureMessage = "role refused";
				setToWaitScreen("Verbonden", "Wachten op rolbevestiging");
				Message.makeOutgoingAnnounce(connectScreen.getRole()).send(
						server);
			}
			break;
		case Disconnect:
			server = null;
			if (roleWasAccepted)
				reconnectToServer();
			else {
				connectScreen.setStatusMessage(failureMessage);
				setToConnectScreen();
			}
			break;
		case SafeDisconnect:
			roleWasAccepted = false;
			gameStarted = false;
			failureMessage = "Disconnected by server";
			break;
		case NotifyReset:
			gameStarted = false;
			prepareForGame();
			break;
		case NotifyWaitingFor:
			tabbedScreen.updateWaitingForTeams(newMessage.getInt());
			break;
		case AcceptRole:
			setRole();
			break;
		case PlainText:
			Gdx.app.log("Text Message", newMessage.getPlainText());
			break;
		case UpdateGameState:
			refreshGameState(newMessage.getGameState());
			break;
		default:
			break;
		}
	}

	private void setRole() {
		roleWasAccepted = true;
		roleIndex = connectScreen.getRole();

		switch (roleIndex) {
		case GameManager.CLIENT_KLINGSOR:
			tabbedScreen = new KlingsorScreen(uiElements, this);
			break;
		case GameManager.CLIENT_UMPIRE:
			tabbedScreen = new UmpireScreen(uiElements, this);
			break;
		case GameManager.CLIENT_PROJECTOR:
			tabbedScreen = new ProjectorScreen(uiElements, this);
			break;
		case GameManager.CLIENT_UNDEFINED:
			throw new RuntimeException(
					"Invalid state: Undefined role was accepted.");
		default:
			tabbedScreen = new FacilitatorScreen(uiElements, this);
			confirmScreen = new ConfirmScreen(uiElements, this);
		}

		prepareForGame();
	}

	private void prepareForGame() {
		waitScreen.setColor(GameManager.roleColors.get(roleIndex));
		setToWaitScreen(
				"Verbonden als " + GameManager.roleNames.get(roleIndex),
				"Wachten tot het spel begint...");
	}

	private void refreshGameState(GameState newGameState) {
		gameStarted = !newGameState.gameIsFinished;
		currentRound = newGameState.currentRound + 1;

		// initialize times for this round
		resetTimeLimitMS(newGameState.roundTimeLimitMS);

		if (!newGameState.equals(previousGameState))
			tabbedScreen.updateGameState(roleIndex, newGameState);
		previousGameState = newGameState;
		setToRoundScreen();
	}

	@Override
	public void update() {

		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			// testing code to simulate this client disconnecting from the
			// server
			server.terminate();
		}

		if (getScreen() != currentScreen)
			setScreen(currentScreen);

		if (tabbedScreen != null)
			tabbedScreen.setTime((int) (getRemainingMS() / 1000));
	}

	public void connectToServer(String host, int port) {
		clientHandler.connect(host, port);
	}

	public void reconnectToServer() {
		clientHandler.reconnect();
	}

	@Override
	public void dispose() {
		if (clientHandler != null)
			clientHandler.stopConnection();
		super.dispose();
	}

	public void connectTimeout(String host, int port, int numTimeouts) {
		numTimeouts = numTimeouts % 3;
		String text = "Verbinden met " + host + ":" + port + " .";
		for (int i = 0; i < numTimeouts; ++i)
			text += '.';
		connectScreen.setStatusMessage(text);
	}

	public void sendChoices() {
		RoundChoices choices = ((FacilitatorScreen) tabbedScreen).getChoices();
		Message.makeOutgoingChoices(choices, json).send(server);
		setToWaitScreen("Keuzes verzonden", "Wachten op andere groepen...");
	}
	
	public void sendPlainText(String text){
		Message.makeOutgoingText(text).send(server);
	}

	public void setToConnectScreen() {
		currentScreen = connectScreen;
	}

	public void setToWaitScreen(String topText, String bottomText) {
		waitScreen.setLabelText(topText, bottomText);
		currentScreen = waitScreen;
	}

	public void setToRoundScreen() {
		currentScreen = tabbedScreen;
	}

	public void setToConfirmScreen() {
		currentScreen = confirmScreen;
	}

	public void requestDiscoverResources(int amount) {
		Message.makeoutgoingRequestDiscover(amount).send(server);
	}

	public void requestEarthquake(int earthquakeStrength) {
		Message.makeoutgoingRequestEarthquake(earthquakeStrength).send(server);
	}

	public void requestTerrorism(int terrorismTarget) {
		Message.makeoutgoingRequestTerrorism(terrorismTarget).send(server);
	}

}
