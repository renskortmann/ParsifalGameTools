package parsifalgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;
import parsifalgame.net.Message;
import parsifalgame.net.Message.Incoming;
import parsifalgame.net.Message.Outgoing;
import parsifalgame.net.ServerHandler;
import parsifalgame.net.SocketHandler;
import parsifalgame.screens.ServerScreen;
import parsifalgame.simulator.Simulator;
import parsifalgame.state.GameState;
import parsifalgame.state.RoundChoices;

import java.util.Date;

public class ServerManager extends GameManager {

    private ServerScreen serverScreen;

    private static String outdir;
    private boolean canWriteFiles = false;

    private boolean gameStarted = false;
    private Simulator simulator;
    private RoundChoices[] allPeerChoices;

    // connection fields
    private ServerHandler serverHandler;
    private IntMap<ClientWrapper> observers = new IntMap<ClientWrapper>();
    private ClientWrapper[] facilitators;
    private ObjectMap<SocketHandler, ClientWrapper> mappedClients =
        new ObjectMap<SocketHandler, ClientWrapper>();

    private boolean resend = false;

    @Override public void create() {
        super.create();

        serverScreen = new ServerScreen(uiElements, this);
        setScreen(serverScreen);

        // prepare a game
        facilitators = new ClientWrapper[GameManager.facilitatorCount];
        resetGame();

        // listen to clients
        serverHandler = new ServerHandler(this);
        serverHandler.startListening();
    }

    public void resetGame() {
        final int num = GameManager.facilitatorCount;

        gameStarted = false;

        GameState startState = new GameState();
        startState.prepareFirst(num);
        simulator = new Simulator(startState);
        allPeerChoices = new RoundChoices[num];

        Outgoing resetMessage = Message.makeOutgoingNotifyReset();
        for (ClientWrapper client : mappedClients.values())
            resetMessage.send(client.handler);

        print("Game has been reset. Press start to begin the game.");
    }

    private String tryReadFile(String filepath) {
        try {
            return Gdx.files.local(filepath).readString();
        } catch (RuntimeException e) {
        }
        try {
            return Gdx.files.absolute(filepath).readString();
        } catch (RuntimeException e) {
        }
        try {
            return Gdx.files.external(filepath).readString();
        } catch (RuntimeException e) {
        }
        return null;
    }

    public void restoreGame(String filepath) {
        if (!Gdx.files.isLocalStorageAvailable()) {
            print("Unable to restore game from files.");
            return;
        }

        String fileContents = tryReadFile(filepath);
        if (fileContents == null)
            fileContents = tryReadFile(filepath + ".json");
        if (fileContents == null)
            fileContents = tryReadFile(outdir + filepath + ".json");
        if (fileContents == null) {
            print("Could not find gamestate file.");
            return;
        }

        GameState loadedState = null;
        try {
            loadedState = json.fromJson(GameState.class, fileContents);
        } catch (RuntimeException e) {
            e.printStackTrace(System.out);
        }
        if (loadedState == null) {
            print("File was not a valid GameState");
            return;
        }

        int num = loadedState.numTeams;
        GameManager.facilitatorCount = num;
        print("Restoring game with " + num + " players from state...");
        resetGame();
        simulator = new Simulator(loadedState);
    }

    @Override public void handleMessage(Incoming newMessage) {
        ClientWrapper client = mappedClients.get(newMessage.getSource());

        switch (newMessage.type) {
            case Connect:
                if (client == null) {
                    client = new ClientWrapper(newMessage.getSource());
                    mappedClients.put(client.handler, client);
                    print("Connected: " + client.handler.remoteAddress);
                }
                break;
            case Disconnect:
                if (client != null)
                    client.remove();
                break;
            case Announce:
                tryAddClient(client, newMessage.getInt());
                break;
            case UpdateRoundChoice:
                updateRoundChoices(client, newMessage.getRoundChoices());
                break;
            case PlainText:
                print(newMessage.getPlainText());
                break;
            case RequestDiscover:
                int amount = newMessage.getInt();
                print(amount + " new resources were requested.");
                setDiscoveredResources(amount);
                break;
            case RequestEarthquake:
                int earthquakeDamage = newMessage.getInt();
                print("Earthquake requested with strength: " + earthquakeDamage);
                setEarthquake(earthquakeDamage);
                break;
            case RequestTerrorism:
                int target = newMessage.getInt();
                if (target >= 0)
                    print("Terrorism requested on " + GameManager.roleNames.get(target));
                else
                    print("All terrorism targets cleared");
                setTerrorism(target);
                break;
            default:
                break;
        }
    }

    private void tryAddClient(ClientWrapper client, int id) {
        if (client.setRole(id)) {
            print(
                "Client " + client.handler.remoteAddress + " has selected: " + GameManager.roleNames
                    .get(id));

            Message.makeOutgoingAcceptRole().send(client.handler);

            if (gameStarted) {
                // reconnecting
                GameState lastState = simulator.getMostRecentState();
                lastState.roundTimeLimitMS = getRemainingMS();
                Outgoing stateMessage = Message.makeOutgoingState(lastState, json);
                stateMessage.send(client.handler);
            } else if (allFacilitatorsPresent())
                print("Game ready to start.");

        } else {
            client.remove();
            print("Client " + client.handler.remoteAddress + " refused");
        }
    }

    public void startGame() {
        if (!gameStarted && allFacilitatorsPresent()) {

            gameStarted = true;
            print("Starting game");

            // prepare file writing
            Date date = new Date(TimeUtils.millis());
            outdir = date.toString().replaceAll(" ", "_").replaceAll(":", "-") + "/";
            canWriteFiles = Gdx.files.isLocalStorageAvailable();
            if (canWriteFiles)
                print("Output directory: " + Gdx.files.getLocalStoragePath());
            else
                print("Unable to write to: " + Gdx.files.getLocalStoragePath());

            broadcastGameState(simulator.getMostRecentState());
        } else {
            print("Unable to start");
        }
    }

    private void broadcastGameState(GameState state) {

        String filename = "state" + state.currentRound + ".json";

        JsonValue.PrettyPrintSettings settings = new JsonValue.PrettyPrintSettings();
        settings.outputType = JsonWriter.OutputType.json;

        writeFile(filename, json.prettyPrint(state, settings));
        print("Round written to file (" + filename + ").");

        Outgoing stateMessage = Message.makeOutgoingState(state, json);

        for (ClientWrapper client : mappedClients.values()) {
            stateMessage.send(client.handler);
        }

        resetTimeLimitMS(state.roundTimeLimitMS);
    }

    public void safeDisconnectClients() {
        Outgoing safeDisconnectMessage = Message.makeOutgoingSafeDisconnect();
        for (ClientWrapper client : mappedClients.values()) {
            safeDisconnectMessage.send(client.handler);
            client.remove();
        }
        mappedClients.clear();
        observers.clear();
        facilitators = new ClientWrapper[GameManager.facilitatorCount];
    }

    private void updateRoundChoices(ClientWrapper client, RoundChoices roundChoices) {
        print("Round choices received from " + GameManager.roleNames.get(client.roleIndex));

        allPeerChoices[client.roleIndex] = roundChoices;

        int teamMask = waitingForMask();

        if (teamMask > 0) {
            Outgoing waitingForClientsMessage = Message.makeOutgoingNotifyWaitingFor(teamMask);

            for (ClientWrapper outClient : observers.values()) {
                waitingForClientsMessage.send(outClient.handler);
            }
        }

    }

    private boolean allFacilitatorsPresent() {
        for (int i = 0; i < GameManager.facilitatorCount; ++i) {
            if (facilitators[i] == null)
                return false;
        }
        return true;
    }

    private boolean allChoicesReceived() {
        for (int i = 0; i < GameManager.facilitatorCount; ++i) {
            if (allPeerChoices[i] == null)
                return false;
        }
        return true;
    }

    private int waitingForMask() {
        int mask = 0;
        int maskbit = 1;
        for (int i = 0; i < GameManager.facilitatorCount; ++i, maskbit = maskbit << 1) {
            if (allPeerChoices[i] == null)
                mask = mask | maskbit;
        }
        return mask;
    }

    public void forceResend() {
        resend = true;
    }

    @Override public void update() {

        if (gameStarted && allFacilitatorsPresent()) {

            if (allChoicesReceived()) {

                // simulate
                GameState newState = simulator.simulate(allPeerChoices);

                for (int i = 0; i < GameManager.facilitatorCount; ++i) {
                    String filename =
                        "team" + allPeerChoices[i].groupID + "_round" + newState.currentRound
                            + ".json";

                    JsonValue.PrettyPrintSettings settings = new JsonValue.PrettyPrintSettings();
                    settings.outputType = JsonWriter.OutputType.json;

                    writeFile(filename, json.prettyPrint(allPeerChoices[i], settings));
                }

                // create a new array, because we store the previous choices in
                // the
                // simulator.
                allPeerChoices = new RoundChoices[GameManager.facilitatorCount];

                broadcastGameState(newState);

                print("State simulation and sending complete. Now starting round " + (
                    newState.currentRound + 1));
            } else if (resend) {
                GameState recentState = simulator.reSimulate();

                if (recentState != null) {
                    broadcastGameState(recentState);

                    print("Resend complete");
                }
            }
        }
        resend = false;
    }

    public static void writeFile(String filename, String text) {
        FileHandle outFile = Gdx.files.local(outdir + filename);
        outFile.writeString(text, false);
    }

    @Override public void dispose() {
        serverHandler.stopListening();
        safeDisconnectClients();
        super.dispose();
    }

    public void print(String text) {
        serverScreen.appendLine(text);
    }

    private String teamNames(IntArray indexArray) {
        if (indexArray.size == 0)
            return "";
        String text = GameManager.roleNames.get(indexArray.get(0));
        for (int i = 1; i < indexArray.size; ++i) {
            text += ", " + GameManager.roleNames.get(indexArray.get(i));
        }
        return text;
    }

    public void printStatus() {
        final int num = GameManager.facilitatorCount;
        IntArray unconnectedTeams = new IntArray();
        for (int i = 0; i < num; ++i)
            if (facilitators[i] == null)
                unconnectedTeams.add(i);
        String unconnectedStr =
            "Teams connected (" + (num - unconnectedTeams.size) + " / " + num + ")";
        if (unconnectedTeams.size > 0)
            unconnectedStr += ", waiting for: " + teamNames(unconnectedTeams);

        print("");
        print(unconnectedStr);
        print("Observers connected: " + observers.size);

        if (gameStarted) {
            print("Output directory: " + Gdx.files.getLocalStoragePath());
            int round = simulator.getMostRecentState().currentRound + 1;
            print(
                "Currently in round: " + round + " (Most recent state was from round " + (round - 1)
                    + ")");

            IntArray notReadyTeams = new IntArray();
            for (int i = 0; i < num; ++i)
                if (allPeerChoices[i] == null)
                    notReadyTeams.add(i);

            if (notReadyTeams.size > 0)
                print("Waiting for choices from: " + teamNames(notReadyTeams));
        } else {
            print("Game not yet started");
        }
    }

    public void skipRound() {
        final int num = GameManager.facilitatorCount;
        if (allPeerChoices == null || allPeerChoices.length != num)
            return;
        for (int i = 0; i < num; ++i)
            if (allPeerChoices[i] == null)
                allPeerChoices[i] = new RoundChoices();
    }

    private static int observerCounter = 0;


    private class ClientWrapper {

        int roleIndex = GameManager.CLIENT_UNDEFINED;
        int observerIndex;

        final SocketHandler handler;

        ClientWrapper(SocketHandler handler) {
            this.handler = handler;
        }

        void remove() {
            if (roleIndex != GameManager.CLIENT_UNDEFINED) {
                if (roleIndex < 0)
                    observers.remove(observerIndex);
                else
                    facilitators[roleIndex] = null;
            }
            handler.terminate();
            mappedClients.remove(handler);
        }

        boolean setRole(int roleIndex) {
            this.roleIndex = roleIndex;
            if (roleIndex < 0) {
                observerIndex = observerCounter++;
                observers.put(observerIndex, this);
            } else if (roleIndex < GameManager.facilitatorCount
                && facilitators[roleIndex] == null) {
                facilitators[roleIndex] = this;
            } else {
                this.roleIndex = GameManager.CLIENT_UNDEFINED;
                return false;
            }
            return true;
        }
    }

    public void setDiscoveredResources(int num) {
        if (simulator != null)
            simulator.discovered = num;
    }

    private void setTerrorism(int target) {
        if (simulator != null) {
            if (target < 0)
                simulator.terrorismTargets.clear();
            else
                simulator.terrorismTargets.add(target);
        }
    }

    private void setEarthquake(int earthquakeDamage) {
        if (simulator != null)
            simulator.earthquakeDamage = earthquakeDamage;
    }

}
