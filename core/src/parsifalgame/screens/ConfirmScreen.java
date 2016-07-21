package parsifalgame.screens;

import parsifalgame.ClientManager;
import parsifalgame.UIElements;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ConfirmScreen extends BasicScreen {

	private ClientManager manager;

	public ConfirmScreen(UIElements uiElements, ClientManager clientManager) {
		super(uiElements, clientManager);
		this.manager = clientManager;
	}

	@Override
	public void show() {
		super.show();

		TextButton confirmButton = new TextButton("Jep", skin, "large");
		TextButton cancelButton = new TextButton("Neen", skin, "large");

		mainTable.add(new Label("Keuzes versturen", skin, "large")).colspan(2);
		mainTable.row().padTop(10);
		mainTable.add(new Label("Zeker weten?", skin, "large")).colspan(2);
		mainTable.row().padTop(10);
		mainTable.add(confirmButton).width(200).padRight(2);
		mainTable.add(cancelButton).width(200).padLeft(2);

		confirmButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				manager.sendChoices();
			}
		});
		cancelButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				manager.setToRoundScreen();
			}
		});
	}
}
