package parsifalgame;

import java.util.concurrent.ConcurrentLinkedQueue;

import parsifalgame.net.Message.Incoming;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;

public abstract class GameManager extends Game {

	protected final Json json = new Json();

	// connection constants
	public static final int initialPort = 9022;
	public static final int clientTimeout = 500;
	public static final int listenerPortReselectTimeout = 500;

	// client role constants
	public static final int CLIENT_UMPIRE = -1;
	public static final int CLIENT_KLINGSOR = -2;
	public static final int CLIENT_UNDEFINED = -3;
	public static final int CLIENT_PROJECTOR = -4;
	// a team client is any non-negative integer

	public static int facilitatorCount = 8;
	public static final IntMap<String> roleNames = new IntMap<String>();
	public static final IntMap<String> shortNames = new IntMap<String>();
	public static final IntMap<Color> roleColors = new IntMap<Color>();

	private static void putRole(int id, String name, String shortName, Color color) {
		roleNames.put(id, name);
		shortNames.put(id, shortName);
		roleColors.put(id, color);
	}

	static {

		putRole(CLIENT_PROJECTOR, "Projector", "[P]", Color.WHITE);
		putRole(CLIENT_KLINGSOR, "Klingsor", "[K]", Color.WHITE);
		putRole(CLIENT_UMPIRE, "Umpire", "[U]", Color.WHITE);
		putRole(CLIENT_UNDEFINED, "undefined", "_u_", Color.WHITE);

		putRole(0, "Team Blue", "Blu", new Color(0.4f, 0.4f, 1, 1));
		putRole(1, "Team Yellow", "Yel", new Color(1, 1, 0.4f, 1));
		putRole(2, "Team Green", "Grn", Color.GREEN);
		putRole(3, "Team Orange", "Org", Color.ORANGE);
		putRole(4, "Team Red", "Red", Color.RED);
		putRole(5, "Team Black", "Blk", Color.GRAY);
		putRole(6, "Team White", "Wht", Color.WHITE);
		putRole(7, "Team Yellow-Black", "YelBlk", new Color(0.7f, 0.7f, 0, 1));
	}

	protected UIElements uiElements = new UIElements();

	protected ConcurrentLinkedQueue<Incoming> incomingMessages = new ConcurrentLinkedQueue<Incoming>();

	private long timeLimitMillis;
	private long startMillis;
	private long remainingMillis;

	@Override
	public void create() {
		uiElements.create();
	}

	@Override
	public void dispose() {
		uiElements.dispose();
		super.dispose();
	}

	protected void resetTimeLimitMS(long limit) {
		timeLimitMillis = limit;
		startMillis = TimeUtils.millis();
	}

	protected long getRemainingMS() {
		return remainingMillis;
	}

	@Override
	public final void render() {
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);

		while (!incomingMessages.isEmpty()) {
			Incoming newMessage = incomingMessages.poll();
			handleMessage(newMessage);
		}

		remainingMillis = timeLimitMillis
				- TimeUtils.timeSinceMillis(startMillis);
		if (remainingMillis < 0)
			remainingMillis = 0;

		update();
		super.render();
	}

	public abstract void handleMessage(Incoming newMessage);

	public abstract void update();

	public final void messageReceived(Incoming newMessage) {
		incomingMessages.add(newMessage);
	}
}
