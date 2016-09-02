package parsifalgame.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import parsifalgame.state.GameState;

public class ShootOutSubScreen extends SubScreen {

	private int showTeams = 0;
	GameState lastState;

	Label[] uitslagVerovering; 
	Label[] uitslagTransport;
	
	public ShootOutSubScreen(BasicScreen parentScreen, Table parentTable) {
		super(parentScreen, parentTable);
	}
	
	@Override
	public void show() {
		super.show();

		if(numTeams > 0) {

//			uitslagVerovering = new Label[numTeams];
//			uitslagTransport = new Label[numTeams];
//
//			for(int i = 0; i< numTeams; i++) {
//				uitslagTransport[i] = new Label("", skin, "large");
//				uitslagVerovering[i] = new Label("", skin, "large");
//			}

			mainTable.center();
			mainTable.setSkin(skin);

			mainTable.add();
			for(int i = 0; i < numTeams; i++){
				Label teamName = new Label(((ProjectorScreen)parentScreen).teamNames[i], skin, "large");
				mainTable.add(teamName).center().expandX().pad(20);
			}
			mainTable.row();

			Label veroverde = new Label("Veroverde", skin, "large");
			mainTable.add(veroverde).left().pad(20);
			for(int i = 0; i < numTeams; i++) {
				uitslagVerovering[i].setText("");
				mainTable.add(uitslagVerovering[i]);
			}
			mainTable.row();

			Label transport = new Label("Transport", skin, "large");
			mainTable.add(transport).left().pad(20);
			for(int i = 0; i < numTeams; i++) {
				uitslagTransport[i].setText("");
				mainTable.add(uitslagTransport[i]);
			}
			mainTable.row();

			TextButton btn_volgendTeam = new TextButton("Volgend Team", skin, "large");		
			mainTable.add(btn_volgendTeam).colspan(numTeams + 1).padTop(100);
			mainTable.row();

			btn_volgendTeam.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y) {
					if(showTeams < numTeams)
						showTeams++;

					addTeam();
				}
			});

			addTeam();
		}
	}
	
	private void addTeam() {

		if(lastState != null) {
			for(int i = 0; i < showTeams; i++) {

				uitslagVerovering[i].setText("" + lastState.conqueredBouwstenen.get(i, -1));

				if (lastState.hacks.get(i) != null) {
					uitslagVerovering[i].setText("HACKED");
					uitslagTransport[i].setText("");
				}
				else if(lastState.purchasedBouwstenen.get(i, -1) == 0)
					uitslagTransport[i].setText("Leeg");
				else if(lastState.lostBouwstenen.get(i, -1) > 0)
					uitslagTransport[i].setText("Beroofd");
				else 
					uitslagTransport[i].setText("Veilig");
			}		
		}
	}
	
	@Override
	protected void update() {
	}

	@Override
	public void updateGameState(int roleIndex, GameState lastState) {
		super.updateGameState(roleIndex, lastState);
		
		this.lastState = lastState;
		
		showTeams = 0;

		uitslagVerovering = new Label[numTeams];
		uitslagTransport = new Label[numTeams];

		for(int i = 0; i< numTeams; i++) {
			uitslagTransport[i] = new Label("", skin, "large");
			uitslagVerovering[i] = new Label("", skin, "large");
		}
	}
}
