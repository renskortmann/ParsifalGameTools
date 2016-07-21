package parsifalgame.desktop;

import parsifalgame.ClientManager;
import parsifalgame.GameManager;
import parsifalgame.ServerManager;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main(String[] arg) {

		// put in .bat file next to the .jar:
		// start server.jar 1

		int defaultCount = 8;

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		if (arg.length > 0) {
			if (arg[0].equals("-clientmode")) {
				if (arg.length > 1){
					int index = -3;
					try {
						index = Integer.parseInt(arg[1]);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					new LwjglApplication(new ClientManager(index), config);
				} else {
					new LwjglApplication(new ClientManager(), config);					
				}
			} else {
				try {
					GameManager.facilitatorCount = Integer.parseInt(arg[0]);
				} catch (NumberFormatException e) {
					GameManager.facilitatorCount = defaultCount;
				}
				new LwjglApplication(new ServerManager(), config);
			}
		} else {
			GameManager.facilitatorCount = defaultCount;
			new LwjglApplication(new ServerManager(), config);
		}
	}
}
