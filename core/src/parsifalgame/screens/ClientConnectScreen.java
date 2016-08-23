package parsifalgame.screens;

import parsifalgame.ClientManager;
import parsifalgame.GameManager;
import parsifalgame.ServerManager;
import parsifalgame.UIElements;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ClientConnectScreen extends BasicScreen {

	private final ClientManager clientManager;

	public ClientConnectScreen(UIElements uiElements, ClientManager manager) {
		super(uiElements, manager);
		this.clientManager = manager;
	}

	private int roleIndex = ClientManager.CLIENT_UNDEFINED;

	// interactive elements
	private Label roleLabel = new Label("", skin, "large");
	private TextField addressField = new TextField("localhost", skin);
//	private TextField addressField = new TextField("10.0.1.123", skin);
//	private TextField addressField = new TextField("192.168.1.26", skin);
//  private TextField addressField = new TextField("127.0.0.1", skin);
	private TextField portField = new TextField(
			Integer.toString(ServerManager.initialPort), skin);
	private TextField statusField = new TextField("", skin);

	// commands

	private void setRoleLabel() {
		LabelStyle coloredStyle = new LabelStyle(roleLabel.getStyle());
		coloredStyle.fontColor = GameManager.roleColors.get(roleIndex);
		roleLabel.setStyle(coloredStyle);
		roleLabel.setText(GameManager.roleNames.get(roleIndex));
	}

	public int getRole() {
		return roleIndex;
	}

	@Override
	public void show() {
		super.show();

		setRoleLabel();

		// layout
		TextButton setKlingsorButton = new TextButton("Klingsor", skin, "large");
		TextButton setUmpireButton = new TextButton("Umpire", skin, "large");
		TextButton teamPlusButton = new TextButton("+ Team", skin, "large");
		TextButton teamMinusButton = new TextButton("- Team", skin, "large");
		TextButton connectButton = new TextButton("Connect", skin, "large");

		mainTable.add(roleLabel).colspan(4).padBottom(10);
		mainTable.row();
		mainTable.add(setKlingsorButton).width(120).padRight(5);
		mainTable.add(setUmpireButton).width(120).padRight(5);
		mainTable.add(teamMinusButton).width(120).padRight(5);
		mainTable.add(teamPlusButton).width(120);
		mainTable.row().padTop(10);
		mainTable.add(new Label("IP", skin)).padRight(10);
		mainTable.add(addressField).colspan(3).height(40).fill();
		mainTable.row();
		mainTable.add(new Label("Port", skin)).padRight(10);
		mainTable.add(portField).colspan(3).height(40).fill();

		mainTable.row().padTop(10);

		mainTable.add(connectButton).height(50).colspan(5).fill();

		mainTable.row().padTop(10);

		mainTable.add(statusField).colspan(5).fill();

		// functionality
		statusField.setDisabled(true);

		connectButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (roleIndex == GameManager.CLIENT_UNDEFINED)
					statusField.setText("Must select a role.");
				else {
					try {
						String host = addressField.getText();
						int port = Integer.parseInt(portField.getText());
						clientManager.connectToServer(host, port);
					} catch (NumberFormatException e) {
						statusField.setText("Port must be an integer.");
					}
				}
			}
		});
		setKlingsorButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				roleIndex = ClientManager.CLIENT_KLINGSOR;
				setRoleLabel();
			}
		});
		setUmpireButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				roleIndex = ClientManager.CLIENT_UMPIRE;
				setRoleLabel();
			}
		});
		teamPlusButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				++roleIndex;
				if (roleIndex < 0)
					roleIndex = 0;
				else if (roleIndex >= GameManager.facilitatorCount)
					roleIndex = GameManager.facilitatorCount - 1;
				setRoleLabel();
			}
		});
		teamMinusButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				--roleIndex;
				if (roleIndex < 0)
					roleIndex = 0;
				setRoleLabel();
			}
		});

	}

	@Override
	protected void updateScreen() {

		// called every frame

	}

	public void setStatusMessage(String status) {
		statusField.setText(status);
	}

}
