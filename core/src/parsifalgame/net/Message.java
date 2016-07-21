package parsifalgame.net;

import parsifalgame.state.GameState;
import parsifalgame.state.RoundChoices;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Logger;

public abstract class Message {

	private static final char ID_CONNECT = 'c';
	private static final char ID_DISCONNECT = 'd';
	private static final char ID_SAFEDISCONNECT = 's';
	private static final char ID_NOTIFYRESET = 'n';
	private static final char ID_NOTIFYWAITINGFOR = 'w';
	private static final char ID_PLAINTEXT = 'p';
	private static final char ID_ANNOUNCE = 'a';
	private static final char ID_ACCEPTROLE = 'y';
	private static final char ID_GAMESTATE = 'g';
	private static final char ID_ROUNDCHOICE = 'r';
	private static final char ID_REQUESTDISCOVER = 'q';
	private static final char ID_REQUESTTERRORISM = 't';
	private static final char ID_REQUESTEARTHQUAKE = 'e';

	public enum MessageType {
		Connect(ID_CONNECT), Disconnect(ID_DISCONNECT), PlainText(ID_PLAINTEXT), Announce(
				ID_ANNOUNCE), AcceptRole(ID_ACCEPTROLE), UpdateGameState(
				ID_GAMESTATE), UpdateRoundChoice(ID_ROUNDCHOICE), SafeDisconnect(
				ID_SAFEDISCONNECT), NotifyReset(ID_NOTIFYRESET), NotifyWaitingFor(ID_NOTIFYWAITINGFOR), RequestDiscover(
				ID_REQUESTDISCOVER), RequestTerrorism(ID_REQUESTTERRORISM), RequestEarthquake(ID_REQUESTEARTHQUAKE);

		final char identifier;

		MessageType(char identifier) {
			this.identifier = identifier;
		}
	}

	private static MessageType typeFromLetter(char c) {
		switch (c) {
		case ID_CONNECT:
			return MessageType.Connect;
		case ID_DISCONNECT:
			return MessageType.Disconnect;
		case ID_PLAINTEXT:
			return MessageType.PlainText;
		case ID_ANNOUNCE:
			return MessageType.Announce;
		case ID_ACCEPTROLE:
			return MessageType.AcceptRole;
		case ID_GAMESTATE:
			return MessageType.UpdateGameState;
		case ID_ROUNDCHOICE:
			return MessageType.UpdateRoundChoice;
		case ID_SAFEDISCONNECT:
			return MessageType.SafeDisconnect;
		case ID_NOTIFYRESET:
			return MessageType.NotifyReset;
		case ID_REQUESTDISCOVER:
			return MessageType.RequestDiscover;
		case ID_REQUESTEARTHQUAKE:
			return MessageType.RequestEarthquake;
		case ID_REQUESTTERRORISM:
			return MessageType.RequestTerrorism;
		case ID_NOTIFYWAITINGFOR:
			return MessageType.NotifyWaitingFor;
		default:
			throw new RuntimeException();
		}
	}

	public final MessageType type;

	private Message(MessageType type) {
		this.type = type;
	}

	public static class Incoming extends Message {

		public final SocketHandler sourceHandler;
		public final Object data;

		Incoming(SocketHandler sourceHandler, Object data, MessageType type) {
			super(type);
			this.sourceHandler = sourceHandler;
			this.data = data;
		}

		public SocketHandler getSource() {
			return sourceHandler;
		}

		public GameState getGameState() {
			return (GameState) data;
		}

		public RoundChoices getRoundChoices() {
			return (RoundChoices) data;
		}

		public String getPlainText() {
			return (String) data;
		}

		public int getInt() {
			return (Integer) data;
		}

	}

	public static class Outgoing extends Message {

		public final String data;

		Outgoing(String data, MessageType type) {
			super(type);
			this.data = data;
		}

		public void send(SocketHandler destination) {
			if (destination == null) {
				int prev = Gdx.app.getLogLevel();
				Gdx.app.setLogLevel(Logger.ERROR);
				Gdx.app.log("Messaging", "Error: destination null");
				Gdx.app.setLogLevel(prev);
			} else
				destination.send(data);
		}

	}

	public static Outgoing makeOutgoingText(String text) {
		return makeOutgoing(MessageType.PlainText, text, null);
	}

	public static Outgoing makeOutgoingState(GameState gameState,
			Json jsonInstance) {
		return makeOutgoing(MessageType.UpdateGameState, gameState,
				jsonInstance);
	}

	public static Outgoing makeOutgoingChoices(RoundChoices roundChoices,
			Json jsonInstance) {
		return makeOutgoing(MessageType.UpdateRoundChoice, roundChoices,
				jsonInstance);
	}

	public static Outgoing makeOutgoingAnnounce(int teamID) {
		return makeOutgoing(MessageType.Announce, teamID, null);
	}

	public static Outgoing makeOutgoingAcceptRole() {
		return makeOutgoing(MessageType.AcceptRole, null, null);
	}

	public static Outgoing makeOutgoingSafeDisconnect() {
		return makeOutgoing(MessageType.SafeDisconnect, null, null);
	}

	public static Outgoing makeOutgoingNotifyReset() {
		return makeOutgoing(MessageType.NotifyReset, null, null);
	}

	public static Outgoing makeOutgoingNotifyWaitingFor(int teamMask) {
		return makeOutgoing(MessageType.NotifyWaitingFor, teamMask, null);
	}

	public static Outgoing makeoutgoingRequestDiscover(int amount) {
		return makeOutgoing(MessageType.RequestDiscover, amount, null);
	}

	public static Outgoing makeoutgoingRequestTerrorism(int terrorismTarget) {
		return makeOutgoing(MessageType.RequestTerrorism, terrorismTarget, null);
	}

	public static Outgoing makeoutgoingRequestEarthquake(int doEarthquake) {
		return makeOutgoing(MessageType.RequestEarthquake, doEarthquake, null);
	}

	private static Outgoing makeOutgoing(MessageType type, Object object,
			Json jsonInstance) {
		String serializedData;
		switch (type) {
		case UpdateGameState:
			serializedData = jsonInstance.toJson(object, GameState.class);
			break;
		case PlainText:
			serializedData = (String) object;
			break;
		case UpdateRoundChoice:
			serializedData = jsonInstance.toJson(object, RoundChoices.class);
			break;
		case NotifyReset:
		case SafeDisconnect:
		case AcceptRole:
			serializedData = "";
			break;
		case NotifyWaitingFor:
		case RequestDiscover:
		case RequestEarthquake:
		case RequestTerrorism:
		case Announce:
			serializedData = Integer.toString((Integer) object);
			break;
		default:
			throw new RuntimeException("Message is of an unsendable type: \""
					+ type.name() + "\"");
		}

		return new Outgoing(type.identifier + serializedData, type);
	}

	/**
	 * Received data without message contents
	 */
	static Incoming makeIncoming(SocketHandler handler, MessageType type) {

		switch (type) {
		case PlainText:
		case UpdateGameState:
		case UpdateRoundChoice:
			throw new RuntimeException("Message of type \"" + type.name()
					+ "\" must ");
		default:
			// safe
		}

		return new Incoming(handler, null, type);
	}

	/**
	 * Received data with message contents
	 */
	static Incoming makeIncoming(SocketHandler handler, String messageData,
			Json jsonInstance) {

		MessageType type = typeFromLetter(messageData.charAt(0));
		Object data = null;
		String contents = messageData.substring(1, messageData.length());

		switch (type) {
		case PlainText:
			data = contents;
			break;
		case UpdateGameState:
			data = jsonInstance.fromJson(GameState.class, contents);
			break;
		case UpdateRoundChoice:
			data = jsonInstance.fromJson(RoundChoices.class, contents);
			break;
		case RequestDiscover:
		case RequestEarthquake:
		case RequestTerrorism:
		case NotifyWaitingFor:
		case Announce:
			data = Integer.parseInt(contents);
			break;
		case NotifyReset:
		case SafeDisconnect:
		case AcceptRole:
			data = null;
			break;
		default:
			throw new RuntimeException("Unable to process message contents: "
					+ contents);
		}

		return new Incoming(handler, data, type);
	}

}