package parsifalgame.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

//import parsifalgame.GameManager;
import parsifalgame.state.GameState;

public class TowerHeightsSubScreen extends SubScreen {

	Label[] towerHeights;
	
	public TowerHeightsSubScreen(BasicScreen parentScreen, Table parentTable) {
		super(parentScreen, parentTable);
	}
	
	@Override
	public void show() {
		super.show();

		if(numTeams > 0) {
//			towerHeights = new Label[numTeams];
//			for(int i = 0; i < numTeams; i++) {
//				towerHeights[i] = new Label("", skin, "large");
//			}

			mainTable.center();
			mainTable.setSkin(skin);

			for(int i = 0; i < numTeams; i++) {
				mainTable.add(towerHeights[i]);
			}
			mainTable.row();

			for(int i = 0; i < numTeams; i++){
				Label teamName = new Label(((ProjectorScreen)parentScreen).teamNames[i], skin, "large");
				mainTable.add(teamName).center().expandX().pad(20);
			}
			mainTable.row();
		}
	}
	
	@Override
	protected void update() {
	}

	@Override
	public void updateGameState(int roleIndex, GameState lastState) {
		super.updateGameState(roleIndex, lastState);
		
		towerHeights = new Label[numTeams];
		for(int i = 0; i < numTeams; i++) {
			towerHeights[i] = new Label("" + lastState.towerHeightAfterLastRound.get(i,  -1), skin, "large");
		}
		
		show();
	}
}
