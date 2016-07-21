package parsifalgame.screens;

import parsifalgame.ClientManager;
import parsifalgame.UIElements;
import parsifalgame.state.GameState;
import parsifalgame.state.RoundChoices;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class FacilitatorScreen extends TabbedScreen {

	public FacilitatorScreen(UIElements uiElements, ClientManager manager) {
		super(uiElements, manager);
		
		teamID = manager.getRole();
	}

	private TextButton resultsTab = new TextButton("Resultaten", skin, "large");
	private TextButton choicesTab = new TextButton("Keuzes", skin, "large");

	public final ChoicesSubScreen choicesSubScreen = new ChoicesSubScreen(this,
			centralArea);
	public final ResultsSubScreen resultsSubScreen = new ResultsSubScreen(this,
			centralArea);

	private int teamID;

	public int getTeamID() {
		return teamID;
	}
	
	@Override
	public void show() {
		super.show();

		tabBar.add(resultsTab).expandX().fill();
		tabBar.add(choicesTab).expandX().fill();

		resultsTab.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setToResults();
			}
		});

		choicesTab.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setToChoices();
			}
		});

		setToResults();
	}

	private void setToResults() {
		choicesTab.setStyle(defaultStyle);
		resultsTab.setStyle(selectedStyle);
		resultsSubScreen.show();
		visibleScreen = resultsSubScreen;
	}

	private void setToChoices() {
		resultsTab.setStyle(defaultStyle);
		choicesTab.setStyle(selectedStyle);
		choicesSubScreen.show();
		visibleScreen = choicesSubScreen;
	}

	public void updateGameState(int roleIndex, GameState newState) {
		super.updateGameState(roleIndex, newState);
		
		choicesSubScreen.updateGameState(roleIndex, newState);
		resultsSubScreen.updateGameState(roleIndex, newState);
	}

	public RoundChoices getChoices() {
		return choicesSubScreen.getConfirmedChoices();
	}
}
