package parsifalgame.screens;

import parsifalgame.ClientManager;
import parsifalgame.UIElements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class WaitScreen extends BasicScreen {

	private final UIElements uiElements;

	public WaitScreen(UIElements uiElements, ClientManager manager) {
		super(uiElements, manager);
		this.uiElements = uiElements;
	}

	private Label topLabel = new Label("", skin, "large");
	private Label bottomLabel = new Label("", skin, "large");

	private Image image;

	private float rotation = 0;

	@Override
	public void show() {
		super.show();

		Texture iconImage = uiElements.getLoadingIcon();

		image = new Image(iconImage);
		image.setOrigin(iconImage.getWidth() / 2, iconImage.getHeight() / 2);
		
		mainTable.add(topLabel);
		mainTable.row();
		mainTable.add(image).pad(25);
		mainTable.row();
		mainTable.add(bottomLabel);
	}

	public void setLabelText(String topText, String bottomText) {
		topLabel.setText(topText);
		bottomLabel.setText(bottomText);
	}

	@Override
	protected void updateScreen() {
		super.updateScreen();

		if (image != null) {
			rotation -= 4.0f;
			image.setRotation(rotation);
		}
	}

	public void setColor(Color color) {
		if (image != null) {
			image.setColor(color);
		}
	}

}
