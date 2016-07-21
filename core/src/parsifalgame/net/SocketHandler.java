package parsifalgame.net;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

import parsifalgame.GameManager;
import parsifalgame.net.Message.MessageType;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

public class SocketHandler {

	private static final String delimiter = " ";

	public final String remoteAddress;

	private SocketHandler self;
	private final Socket socket;

	private Scanner reader;
	private OutputStreamWriter writer;

	private boolean stopped = false;

	public SocketHandler(final Socket socket, final GameManager manager) {
		this.self = this;
		this.socket = socket;
		this.remoteAddress = socket.getRemoteAddress();

		Thread communicationThread = new Thread() {
			public void run() {

				Json jsonInstance = new Json();

				reader = new Scanner(socket.getInputStream());
				reader.useDelimiter(delimiter);
				writer = new OutputStreamWriter(socket.getOutputStream());

				manager.messageReceived(Message.makeIncoming(self,
						MessageType.Connect));

				while (!stopped) {

					String messageData;
					try {

						messageData = reader.next();
						if (messageData == null) {
							stopped = true;
						} else {
							manager.messageReceived(Message.makeIncoming(self,
									Base64Coder.decodeString(messageData),
									jsonInstance));
						}
					} catch (NoSuchElementException e) {
						stopped = true;
					} catch (IllegalStateException e) {
						stopped = true;
					}
				}

				terminate();
				manager.messageReceived(Message.makeIncoming(self,
						MessageType.Disconnect));
			};
		};

		communicationThread.start();
	}

	void send(String outgoingData) {
		String encodedData = Base64Coder.encodeString(outgoingData);

		try {
			writer.write(encodedData + delimiter);
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("Failed to send data");
		}
	}

	public final void terminate() {
		socket.dispose();
	}

}
