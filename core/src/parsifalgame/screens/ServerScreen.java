package parsifalgame.screens;

import parsifalgame.ServerManager;
import parsifalgame.UIElements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class ServerScreen extends BasicScreen {

	private final ServerManager serverManager;

	public ServerScreen(UIElements uiElements, ServerManager manager) {
		super(uiElements, manager);
		this.serverManager = manager;
	}

	// interactive elements
	private TextArea textArea = new TextArea("", skin);
	private TextField commandField = new TextField("", skin);

	// structural elements
	private Table controlTable = new Table();
	private ScrollPane textFieldPane = new ScrollPane(textArea, skin);

	// commands

	@Override
	public void show() {
		super.show();

		textArea.setDisabled(true);

		mainTable.add(textFieldPane).expandX().expandY().fill();
		mainTable.row();
		mainTable.add(controlTable).expandX().fill();

		// Add commands for the server administrator here

		addCommand(new ServerCommand("start", true) {
			@Override
			public void runCommand(String argument) {
				serverManager.startGame();
			}
		});
		addCommand(new ServerCommand("clear", true) {
			@Override
			public void runCommand(String argument) {
				clearText();
			}
		});
		addCommand(new ServerCommand("status", true) {
			@Override
			public void runCommand(String argument) {
				serverManager.printStatus();
			}
		});
		addCommand(new ServerCommand("resend", true) {
			@Override
			public void runCommand(String argument) {
				serverManager.forceResend();
			}
		});
		addCommand(new ServerCommand("skipround", false) {
			@Override
			public void runCommand(String argument) {
				serverManager.skipRound();
			}
		});
		addCommand(new ServerCommand("exit", false) {
			@Override
			public void runCommand(String argument) {
				serverManager.dispose();
				Gdx.app.exit();
			}
		});
		addCommand(new ServerCommand("discover", false) {
			@Override
			public void runCommand(String argument) {
				int num = -1;
				try {
					num = Integer.parseInt(argument);
				} catch (Exception e) {
				}
				if (num >= 0) {
					serverManager.setDiscoveredResources(num);
					appendLine("Setting discovered resources this round to "
							+ num);
				} else {
					appendLine("Incorrect syntax, should be " + commandName
							+ " [positive integer]");
				}
			}
		});
		addCommand(new ServerCommand("reset", false) {
			@Override
			public void runCommand(String argument) {
				serverManager.resetGame();
			}
		});
		addCommand(new ServerCommand("restore", false) {
			@Override
			public void runCommand(String argument) {
				serverManager.restoreGame(argument);
			}
		});
		addCommand(new ServerCommand("hello", false) {
			@Override
			public void runCommand(String argument) {
				appendLine("hello yourself!");
			}
		});

		// final organising (add buttons, command line)
		initCommandBar();
	}

	@Override
	protected void updateScreen() {

		// called every frame

	}

	// ///
	// Stuff that should stay the same
	// ///

	private Array<ServerCommand> buttonCommands = new Array<ServerCommand>();
	private ObjectMap<String, ServerCommand> allCommands = new ObjectMap<String, ServerCommand>();

	private abstract class ServerCommand {

		public final String commandName;

		ServerCommand(String commandName, boolean showAsButton) {
			if (commandName == null)
				throw new RuntimeException("command name must be non-null");
			if (commandName.contains(" "))
				throw new RuntimeException(
						"command name must not contain spaces");
			this.commandName = commandName.toLowerCase();
			if (showAsButton)
				buttonCommands.add(this);
		}

		abstract void runCommand(String argument);
	}

	private void addCommand(ServerCommand newCommand) {
		allCommands.put(newCommand.commandName, newCommand);
	}

	private void initCommandBar() {

		for (final ServerCommand command : buttonCommands) {
			TextButton button = new TextButton(command.commandName, skin);
			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					echoCommand(command.commandName);
					command.runCommand(null);
				}
			});
			controlTable.add(button).padRight(2);
		}

		// command line for additional commands, delegates to tryRunCommand
		commandField.setTextFieldListener(new TextField.TextFieldListener() {
			@Override
			public void keyTyped(TextField textField, char c) {
				if (commandField.getText().length() == 0)
					return;
				if (c == '\n' || c == '\r') {
					tryRunCommand(commandField.getText());
					commandField.setText("");
				}
			}
		});
		controlTable.add(commandField).expandX().fill();
	}

	private void tryRunCommand(String fullCommand) {
		echoCommand(fullCommand);

		String[] segments = fullCommand.split(" ", 2);
		if (segments.length == 0)
			return;
		
		String commandName = segments[0].toLowerCase();

		ServerCommand command = allCommands.get(commandName);
		if (command == null) {
			appendLine("unknown command: " + commandName);
		} else {
			if (segments.length == 1 || segments[1] == null)
				command.runCommand("");
			else
				command.runCommand(segments[1]);
		}
	}

	private void echoCommand(String command) {
		appendLine("> " + command);
	}

	public void appendLine(String text) {
		boolean attachedToBottom = textFieldPane.isBottomEdge();

		textArea.appendText(text + '\n');
		textArea.setPrefRows(textArea.getLines() * 0.8f); // magic number!! (?)
		textFieldPane.layout();
		if (attachedToBottom)
			textFieldPane.scrollTo(0, 0, 0, 0);
	}

	public void clearText() {
		textArea.setText("");
		textArea.setPrefRows(1);
		textFieldPane.layout();
	}

}
