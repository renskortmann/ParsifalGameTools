package parsifalgame.screens;

import parsifalgame.ClientManager;
import parsifalgame.GameManager;
import parsifalgame.UIElements;
import parsifalgame.state.GameState;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public abstract class TabbedScreen extends BasicScreen {

	protected TabbedScreen(UIElements uiElements, ClientManager manager) {
		super(uiElements, manager);

		defaultStyle = uiElements.getSkin().get("large", TextButtonStyle.class);
		selectedStyle = new TextButtonStyle(defaultStyle);
		selectedStyle.up = selectedStyle.down;
	}
	
	private Label teamLabel = new Label("Team ?", skin, "large");
	private Label roundLabel = new Label("Ronde ", skin, "large");
	private TextButton[] teamsReadyButtons;
	private Label timeLabel = new Label("0:30", skin, "large");

	protected Table tabBar = new Table();

	protected Table centralArea = new Table();
	protected ScrollPane centralAreaPane = new ScrollPane(centralArea, skin);

	protected TextButtonStyle defaultStyle;
	protected TextButtonStyle selectedStyle;

	protected SubScreen visibleScreen;

	public void setTime(int secondsRemaining) {
		String minutes = Integer.toString(secondsRemaining / 60);
		String seconds = Integer.toString(secondsRemaining % 60);

		if (minutes.length() > 2) {
			minutes = "99";
			seconds = "59";
		} else {
			if (minutes.length() < 2)
				minutes = "0" + minutes;
			if (seconds.length() < 2)
				seconds = "0" + seconds;
		}

		timeLabel.setText(minutes + ":" + seconds);
	}

	private void initWaitingLights(){
		if (teamsReadyButtons == null) {
			teamsReadyButtons = new TextButton[8];
			for (int i = 0; i < 8; ++i){
				teamsReadyButtons[i] = new TextButton("   ", defaultStyle);
			}
		}
	}
	
	private void addWaitingLights(Table infoBar) {
		initWaitingLights();
		for (int i = 0; i < 8; ++i) {
			infoBar.add(teamsReadyButtons[i]);
		}
	}

	@Override
	public void show() {
		super.show();

		Table infoBar = new Table();

		infoBar.add(teamLabel).expandX().fillX();
		
		if (this instanceof UmpireScreen)
			addWaitingLights(infoBar);
		infoBar.add(roundLabel); // .padLeft(5);
		// infoBar.add(timeLabel).padLeft(25);

		mainTable.add(infoBar).expandX().fill();
		mainTable.row();
		mainTable.add(tabBar).expandX().fill();
		mainTable.row();
		mainTable.add(centralAreaPane).expandX().expandY().fill();
	}

	@Override
	protected void updateScreen() {
		if (visibleScreen != null)
			visibleScreen.update();
	}

	public void updateWaitingForTeams(int mask) {
		if (this instanceof ObserverScreen)
		{
			initWaitingLights();
			
			int maskBit = 1;
			for (int i = 0; i < 8; ++i, maskBit = maskBit << 1) {
				Color c;
				String name;
				if ((maskBit & mask) != 0){
					c = new Color(GameManager.roleColors.get(i));
					name = GameManager.shortNames.get(i);
				}else{
					c = Color.BLACK;
					name = "   ";
				}
				
				teamsReadyButtons[i].setText(name);
				teamsReadyButtons[i].setColor(c);
			}
		}
	}
	
	private void initWaitingForTeams(int numTeams){
		int mask = 0;
		int maskBit = 1;
		for (int i = 0; i < numTeams; ++i, maskBit = maskBit << 1)
		{
			mask = mask | maskBit;
		}
		updateWaitingForTeams(mask);
	}

	public void updateGameState(int roleIndex, GameState newState) {

			initWaitingForTeams(newState.numTeams);

			LabelStyle coloredStyle = new LabelStyle(teamLabel.getStyle());
			coloredStyle.fontColor = GameManager.roleColors.get(roleIndex);
			teamLabel.setStyle(coloredStyle);
			teamLabel.setText(GameManager.roleNames.get(roleIndex));

			if(roleIndex == GameManager.CLIENT_PROJECTOR)
				roundLabel.setText("Ronde " + (newState.currentRound));
			else				
				roundLabel.setText("Ronde " + (newState.currentRound + 1));
	}
}
