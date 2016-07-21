package parsifalgame.net;

import parsifalgame.ClientManager;
import parsifalgame.GameManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ClientHandler {

	private final ClientManager manager;

	private String host;
	private int port;

	private Thread connectionThread;

	private final SocketHints hints = new SocketHints();

	private SocketHandler handler = null;

	public ClientHandler(ClientManager manager) {
		this.manager = manager;

		hints.connectTimeout = GameManager.clientTimeout;
	}

	public void reconnect() {
		if (!isAlive()) {
			startNewThread();
		}
	}

	public void connect(String host, int port) {
		this.host = host;
		this.port = port;

		if (!isAlive()) {
			if (handler != null) {
				handler.terminate();
				handler = null;
			}
			startNewThread();
		}
	}

	public void stopConnection() {
		if (!isAlive()) {
			if (handler != null) {
				handler.terminate();
				handler = null;
			}
		}
	}

	private boolean isAlive() {
		return connectionThread != null && connectionThread.isAlive();
	}

	private void startNewThread() {
		connectionThread = new Thread() {
			@Override
			public void run() {
				// open the socket

				int timeouts = 0;
				
				while (true) {

					Socket socket = null;
					try {
						socket = Gdx.net.newClientSocket(Protocol.TCP, host,
								port, hints);
					} catch (GdxRuntimeException e) {
						manager.connectTimeout(host, port, timeouts++);
					}

					if (socket != null) {
						handler = new SocketHandler(socket, manager);
						break;
					}
				}

			}
		};
		connectionThread.start();
	}

}
