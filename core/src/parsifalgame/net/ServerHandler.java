package parsifalgame.net;

import parsifalgame.GameManager;
import parsifalgame.ServerManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ServerHandler {

	private final ServerManager manager;

	private Thread listenThread;
	private boolean stopped = true;
	private int port = ServerManager.initialPort;

	private final ServerSocketHints hints = new ServerSocketHints();
	private ServerSocket serverSocket;

	private Array<SocketHandler> handlers = new Array<SocketHandler>();

	public ServerHandler(ServerManager manager) {
		this.manager = manager;

		hints.acceptTimeout = 0;
	}

	public void startListening() {
		if (!isAlive()) {
			startThread();
		}
	}

	public void stopListening() {
		if (isAlive()) {
			stopped = true;
			serverSocket.dispose();
		}
		handlers.clear();
	}

	private boolean isAlive() {
		return listenThread != null && listenThread.isAlive();
	}

	private void startThread() {
		listenThread = new Thread() {
			@Override
			public void run() {
				// open the server socket
				while (!tryStartListening())
					; // loop while unsuccessful
				manager.print("Listening for connections on port ("
						+ port + ")");

				stopped = false;

				// loop until aborted
				while (!stopped) {

					// Listen for a connection
					Socket socket = null;
					try {
						socket = serverSocket.accept(null);
					} catch (GdxRuntimeException e) {
					}

					// handle the new connection
					if (socket != null) {
						handlers.add(new SocketHandler(socket, manager));
					}
				}
				manager.print("Stopped listening");
			}
		};
		listenThread.start();
	}

	private boolean tryStartListening() {
		try {
			serverSocket = Gdx.net.newServerSocket(Protocol.TCP, port, hints);
			return true;
		} catch (GdxRuntimeException e) {
		}
		manager.print("Port (" + port + ") occupied. Attempting port ("
				+ (port + 1) + ")");
		++port;
		try {
			Thread.sleep(GameManager.listenerPortReselectTimeout);
		} catch (InterruptedException e) {
		}

		return false;
	}

}
