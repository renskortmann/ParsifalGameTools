package parsifalgame.screens;

import parsifalgame.ClientManager;
import parsifalgame.GameManager;
import parsifalgame.state.GameState;
import parsifalgame.state.RoundChoices;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class ResultsSubScreen extends SubScreen {

	private int numTeams = 0;
	private int teamID;

	// interactive elements
	private Table battleResultsTableContainer = new Table();
	private Label[][] battleResultsTable = null;

	private Table lostAndConqueredTableContainer = new Table();
	private Label[][] lostAndConqueredTable = null;

	private Table defensesTableContainer = new Table();
	private Label[][] defensesTable = null;

	private Table attacksTableContainer = new Table();
	private Label[][] attacksTable = null;

	private Table resourcesTableContainer = new Table();
	private Label[][] resourcesTable = null;

	private Table towerGrowthTableContainer = new Table();
	private Label[][] towerGrowthTable = null;

	private Label lostAndConqueredTeamInfo = new Label("", skin);
	private Label towerHeightTeamInfo = new Label("", skin);
	
	private Label battleResultsLabel = new Label("", skin, "large");
	private Label towerGrowthLabel = new Label("", skin, "large");
	private Label lostAndConqueredLabelUmpire = new Label("", skin, "large");
	private Label lostAndConqueredLabelTeams = new Label("", skin, "large");
	private Label defensesLabel = new Label("", skin, "large");
	private Label attacksLabel = new Label("", skin, "large");
	private Label resourcesLabel = new Label("", skin, "large");
	
	public ResultsSubScreen(BasicScreen parentScreen, Table parentTable) {
		super(parentScreen, parentTable);
		teamID = ((ClientManager)parentScreen.gameManager).getRole();
	}

	private void checkTeamCount(int numTeams) {
		if (this.numTeams == numTeams)
			return;
		this.numTeams = numTeams;

		int tableSize = numTeams + 1;
		battleResultsTable = new Label[tableSize][tableSize];
		for (int i = 0; i < tableSize; ++i) {
			for (int j = 0; j < tableSize; ++j) {
				battleResultsTable[i][j] = new Label("", skin);
			}
		}

		for (int row = 1; row < tableSize; ++row)
			battleResultsTable[row][0].setText("Aanvaller\n" + GameManager.roleNames.get(row-1));

		for (int column = 1; column < tableSize; ++column)
			battleResultsTable[0][column].setText("Verdediger\n" + GameManager.roleNames.get(column-1));

	}

	@Override
	public void show() {
		super.show();
		
		mainTable.left().top();
		mainTable.defaults().left();

		int tableSizeRows, tableSizeColumns;

		// table layouts
		if (battleResultsTable != null) {
			tableSizeRows = numTeams + 1;
			tableSizeColumns = numTeams + 1;
			battleResultsTableContainer = new Table();
			for (int i = 0; i < tableSizeRows; ++i) {
				for (int j = 0; j < tableSizeColumns; ++j) {
					battleResultsTableContainer.add(battleResultsTable[i][j]).center().pad(5);
				}
				battleResultsTableContainer.row();
			}
		}
		
		if(lostAndConqueredTable != null) {
			tableSizeRows = 2;
			tableSizeColumns = numTeams;
			lostAndConqueredTableContainer = new Table();
			for (int i = 0; i < tableSizeRows; ++i) {
				for (int j = 0; j < tableSizeColumns; ++j) {
					lostAndConqueredTableContainer.add(lostAndConqueredTable[i][j]).center().pad(5);
				}
				lostAndConqueredTableContainer.row();
			}
		}
		
		if (defensesTable != null) {
			tableSizeRows = 4;
			tableSizeColumns = 4;
			defensesTableContainer = new Table();
			for (int i = 0; i < tableSizeRows; ++i) {
				for (int j = 0; j < tableSizeColumns; ++j) {
					defensesTableContainer.add(defensesTable[i][j]).center().pad(5);
				}
				defensesTableContainer.row();
			}
		}
		
		if(attacksTable != null) {
			tableSizeRows = RoundChoices.NUM_ATTACKS_PER_TEAM;
			tableSizeColumns = 5;
			attacksTableContainer = new Table();
			for (int i = 0; i < tableSizeRows; ++i) {
				attacksTableContainer.add(new Label("Aanval " + (i+1) + ": ", skin)).pad(5);
				for (int j = 0; j < tableSizeColumns; ++j) {
					attacksTableContainer.add(attacksTable[i][j]).pad(5);
				}
				attacksTableContainer.row();
			}			
		}

		if (resourcesTable != null) {
			tableSizeRows = 7;
			tableSizeColumns = 2;
			resourcesTableContainer = new Table();
			for (int i = 0; i < tableSizeRows; ++i) {
				for (int j = 0; j < tableSizeColumns; ++j) {
					resourcesTableContainer.add(resourcesTable[i][j]).left().pad(5);
				}
				resourcesTableContainer.row();
			}
		}

		if (towerGrowthTable != null) {
			tableSizeRows = 4;
			tableSizeColumns = numTeams + 1;
			towerGrowthTableContainer = new Table();
			for (int i = 0; i < tableSizeRows; ++i) {
				for (int j = 0; j < tableSizeColumns; ++j) {
					towerGrowthTableContainer.add(towerGrowthTable[i][j]).center().pad(5);
				}
				towerGrowthTableContainer.row();
			}
		}

		// building blocks lost and conquered
		if((teamID == GameManager.CLIENT_UMPIRE) && (lostAndConqueredTable != null)) {
			mainTable.add(lostAndConqueredLabelUmpire).padBottom(10);
			mainTable.row();
			mainTable.add(lostAndConqueredTableContainer).padLeft(10);
			mainTable.row();
		}

		// Own battle results. Does not show in Umpire or Klingsor screen
		if((teamID != GameManager.CLIENT_KLINGSOR)  && (teamID != GameManager.CLIENT_UMPIRE)  && (defensesTable != null)) {
			mainTable.add(defensesLabel).padBottom(10).padTop(10);
			mainTable.row();
			mainTable.add(defensesTableContainer).padLeft(10);
			mainTable.row();			

			mainTable.add(attacksLabel).padBottom(10).padTop(10);
			mainTable.row();
			mainTable.add(attacksTableContainer).padLeft(10);
			mainTable.row();
			mainTable.add(lostAndConqueredLabelTeams).padBottom(10).padTop(10);
			mainTable.row();
			mainTable.add(towerHeightTeamInfo).padBottom(10).padTop(10);
			mainTable.row();
			mainTable.add(lostAndConqueredTeamInfo).padBottom(10);
			mainTable.row();
		}
		
		// Klingsor and Umpire screen
		if((teamID == GameManager.CLIENT_KLINGSOR) || (teamID == GameManager.CLIENT_UMPIRE)) {
			mainTable.add(towerGrowthLabel);
			mainTable.row();
			mainTable.add(towerGrowthTableContainer).padLeft(10);
			mainTable.row();			
		}
		
		//Resources update
		if(teamID != GameManager.CLIENT_KLINGSOR) {
			mainTable.add(resourcesLabel).padBottom(10).padTop(10);
			mainTable.row();
			mainTable.add(resourcesTableContainer).padLeft(10);
			mainTable.row();
		}

		// Overall battle results (shows only in umpire screen)
		if((teamID == GameManager.CLIENT_UMPIRE) && (battleResultsTable != null)) {
			mainTable.add(battleResultsLabel).padBottom(10);
			mainTable.row();
			mainTable.add(battleResultsTableContainer).padLeft(10);
			mainTable.row();
		}

		// Overall battle results (shows only in umpire screen)
		if((teamID == GameManager.CLIENT_UMPIRE)) {
			final TextField tf_discover = new TextField("0", skin, "large");
			TextButton btn_discover = new TextButton("Discover resources: ", skin, "large");
			final TextField tf_earthquake = new TextField("0", skin, "large");
			TextButton btn_earthquake = new TextButton("Earthquake strength: ", skin, "large");
			final SelectBox<String> sb_terrorism = new SelectBox<String>(skin);
			TextButton btn_terrorism = new TextButton("Destroy tower of: ", skin, "large");

			Array<String> names = new Array<String>();
			names.add("None (clear all)");
			for(int i = 0; i < numTeams; ++i)
				names.add(GameManager.roleNames.get(i));
			sb_terrorism.setItems(names);
			
			Table tb = new Table(skin);
			tb.padTop(50);
			tb.add(btn_discover).width(300);
			tb.add(tf_discover).minWidth(250);
			tb.row();
			tb.add(btn_earthquake).width(300).padTop(20);
			tb.add(tf_earthquake).minWidth(250).padTop(20);
			tb.row();
			tb.add(btn_terrorism).width(300).padTop(20);
			tb.add(sb_terrorism).minWidth(250).padTop(20);
			mainTable.add(tb);
			mainTable.row();
			
			btn_discover.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y) {
					int amount = 0;
					try {
						amount = Integer.parseInt(tf_discover.getText());
						parentScreen.getAsClient().requestDiscoverResources(amount);						
					} catch (Exception e){
						tf_discover.setText("0");
					}
				}
			});
			btn_earthquake.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y) {
					int strength = 0;
					try {
						strength = Integer.parseInt(tf_earthquake.getText());
						parentScreen.getAsClient().requestEarthquake(strength);						
					} catch (Exception e){
						tf_earthquake.setText("0");
					}
				}
			});
			btn_terrorism.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y) {
					int target = 0;
					try {
						target = sb_terrorism.getSelectedIndex() - 1;
						parentScreen.getAsClient().requestTerrorism(target);						
					} catch (Exception e){
						sb_terrorism.setSelectedIndex(0);
					}
				}
			});
		}
		
		mainTable.padBottom(50);
	}

	@Override
	protected void update() {
	}

	private String getOwnAttackResult(int defenderID, GameState lastState){
		Array<GameState.AttackInfo> infos = lastState.util_getAttacksOnDefender(defenderID);
		GameState.AttackInfo myAttack = null;
		for (GameState.AttackInfo info : infos)
		{
			if (info.attacker == teamID)
				myAttack = info;
			
			if (info.result == GameState.ATTACK_RESULT_SUCCESS){
				if (info.attacker == teamID)
					return "Geslaagd";
				else
					return "Mislukt ("+GameManager.roleNames.get(info.attacker)+" heeft de buit)";
			}
		}
		
//		boolean zwaardenFailed = myAttack.zwaarden > 0 && lastState.numShieldsAfterAttack.get(defenderID, 0) > 0;
//		boolean rakettenFailed = myAttack.raketten > 0 && lastState.numRocketShieldsAfterAttack.get(defenderID, 0) > 0;
//		boolean virussenFailed = myAttack.virussen > 0 && lastState.numAntiVirusesAfterAttack.get(defenderID, 0) > 0;
//		
//		if (zwaardenFailed && rakettenFailed && virussenFailed)

		if(myAttack != null) {
			if (myAttack.result == GameState.ATTACK_RESULT_FAIL)
				return "Mislukt (verdediging was sterker)";
			else if (myAttack.result == GameState.ATTACK_RESULT_DRAW)
				return "Verdampt";
			else
				return "ResultSubScreen.java: getOwnAttackResult(): attack result does not equal Fail or Draw.";
		}
		else
			return "ResultSubScreen.java: getOwnAttackResult(): no attack found.";
	}
	
	@Override
	public void updateGameState(int roleIndex, GameState lastState) {
		super.updateGameState(roleIndex, lastState);
		int nextRound = lastState.currentRound + 1;
		if (nextRound > 1) {
			battleResultsLabel.setText("Uitslag van de Shoot-out in ronde " + lastState.currentRound);
			lostAndConqueredLabelUmpire.setText("Veroverde en verloren bouwstenen in ronde " + lastState.currentRound);
			lostAndConqueredLabelTeams.setText(
					"In ronde " + lastState.currentRound + " veroverde jouw team " + lastState.conqueredBouwstenen.get(teamID, -1) + " bouwstenen en verloor er " + lastState.lostBouwstenen.get(teamID,  -1) + ".");
			defensesLabel.setText("Jouw verdediging in ronde " + lastState.currentRound);
			attacksLabel.setText("Jouw aanvallen in ronde " + lastState.currentRound);
			towerGrowthLabel.setText("Aangroei van de torens in ronde " + lastState.currentRound);
			

			// update battle results
			checkTeamCount(lastState.numTeams);
			int tableSize = numTeams + 1;
			if (lastState.battleResultsFromLastRound != null) {
				for (int i = 1; i < tableSize; ++i) {
					for (int j = 1; j < tableSize; ++j) {
						// maybe some calculation can happen here?
						battleResultsTable[i][j]
								.setText(lastState.battleResultsFromLastRound
										.get(i - 1).get(j - 1));
					}
				}
			}
			
			String myTowerResultLabelText = "";
			
			// update lost and conquered building blocks
			lostAndConqueredTable = new Label[2][numTeams];
			for (int j = 0; j < numTeams; j++)
				lostAndConqueredTable[0][j] = new Label(GameManager.roleNames.get(j), skin);
			for (int j = 0; j < numTeams; j++){
				String labelEntry;
				if (lastState.hacks.get(j) != null)
					labelEntry = "HACKED";
				else if(lastState.purchasedBouwstenen.get(j, -1) == 0)
					labelEntry="+" + lastState.conqueredBouwstenen.get(j,-1) + " Empty";
				else if(lastState.lostBouwstenen.get(j, -1) > 0)
					labelEntry="+" + lastState.conqueredBouwstenen.get(j,-1) + " Failed";
				else 
					labelEntry="+" + lastState.conqueredBouwstenen.get(j,-1) + " Safe";
				
				if (teamID == j)
					myTowerResultLabelText = labelEntry;
				lostAndConqueredTable[1][j] = new Label(labelEntry, skin);
			}
					

			if(teamID > -1) {
				// update defenses of and attacks on own team
				defensesTable = new Label[4][4];
				defensesTable[0][0] = new Label("", skin);
				defensesTable[0][1] = new Label("Verdediging voor de aanval", skin);
				defensesTable[0][2] = new Label("Je bent aangevallen door", skin);
				defensesTable[0][3] = new Label("Verdediging na de aanval", skin);

				String[] results = myTowerResultLabelText.trim().split(" ");
				String capturedResult = results[0];
				String transportResult = results[results.length - 1];
				lostAndConqueredTeamInfo.setText("Captured: "+capturedResult + "   Transport was: "+transportResult);
				towerHeightTeamInfo.setText("Huidige hoogte van de toren: "+lastState.towerHeightAfterLastRound.get(teamID, -1));
				
				Array<GameState.AttackInfo> attacksOnMe = lastState.util_getAttacksOnDefender(teamID);
				int nzwaard = lastState.numSwordsAttackedBy.get(teamID, -1);
				int nraket = lastState.numRocketsAttackedBy.get(teamID, -1);
				int nvirus = lastState.numVirusesAttackedBy.get(teamID, -1);
				String attackedByZwaard = nzwaard == 0 ? "0" : "[ "+nzwaard+" ]";
				String attackedByRaket = nraket == 0 ? "0" : "[ "+nraket+" ]";
				String attackedByVirus = nvirus == 0 ? "0" : "[ "+nvirus+" ]";
				for (GameState.AttackInfo att : attacksOnMe){
					if (att.zwaarden > 0)
						attackedByZwaard += ", "+att.zwaarden+" door "+GameManager.shortNames.get(att.attacker);
					if (att.raketten > 0)
						attackedByRaket += ", "+att.raketten+" door "+GameManager.shortNames.get(att.attacker);
					if (att.virussen > 0)
						attackedByVirus += ", "+att.virussen+" door "+GameManager.shortNames.get(att.attacker);
					
				}
				
				defensesTable[1][0] = new Label("Schild / Zwaard", skin);
				defensesTable[1][1] = new Label("" + lastState.numShieldsBeforeAttack.get(teamID, -1), skin);
				defensesTable[1][2] = new Label(attackedByZwaard, skin);
				defensesTable[1][3] = new Label("" + lastState.numShieldsAfterAttack.get(teamID, -1), skin);

				defensesTable[2][0] = new Label("Raketschild / Raket", skin);
				defensesTable[2][1] = new Label("" + lastState.numRocketShieldsBeforeAttack.get(teamID, -1), skin);
				defensesTable[2][2] = new Label(attackedByRaket, skin);
				defensesTable[2][3] = new Label("" + lastState.numRocketShieldsAfterAttack.get(teamID, -1), skin);

				defensesTable[3][0] = new Label("Antivirus / Virus", skin);
				defensesTable[3][1] = new Label("" + lastState.numAntiVirusesBeforeAttack.get(teamID, -1), skin);
				defensesTable[3][2] = new Label(attackedByVirus, skin);
				defensesTable[3][3] = new Label("" + lastState.numAntiVirusesAfterAttack.get(teamID, -1), skin);

				//update attacks by own team
				attacksTable = new Label[RoundChoices.NUM_ATTACKS_PER_TEAM][5];
				for(int i = 0; i < RoundChoices.NUM_ATTACKS_PER_TEAM; i++) {
					int defenderID = lastState.attacks[teamID][i].get(GameState.DEFENDER_ID_FIELD, -1);
					if(defenderID >= 0) {
						attacksTable[i][0] = new Label(GameManager.roleNames.get(defenderID,""), skin);
						nzwaard = lastState.attacks[teamID][i].get(GameState.NUM_SWORDS_FIELD, 0);
						nraket = lastState.attacks[teamID][i].get(GameState.NUM_ROCKETS_FIELD, 0);
						nvirus = lastState.attacks[teamID][i].get(GameState.NUM_VIRUSES_FIELD, 0);
						String attackResult = getOwnAttackResult(defenderID, lastState);
						attacksTable[i][1] = new Label(nzwaard + (nzwaard == 1 ? " zwaard" : " zwaarden"), skin);
						attacksTable[i][2] = new Label(nraket + (nraket == 1 ? " raket" : " raketten"), skin);
						attacksTable[i][3] = new Label(nvirus + (nvirus == 1 ? " virus" : " virussen"), skin);
						attacksTable[i][4] = new Label(attackResult, skin);
					}
					else {
						attacksTable[i][0] = new Label("Geen aanval", skin);
						attacksTable[i][1] = new Label("", skin);
						attacksTable[i][2] = new Label("", skin);
						attacksTable[i][3] = new Label("", skin);
						attacksTable[i][4] = new Label("", skin);
					}
				}
			}
			
			if(teamID < 0) {
				towerGrowthTable = new Label[4][numTeams+1];

				towerGrowthTable[0][0] = new Label("", skin);
				for (int i = 0; i < numTeams; i++)
					towerGrowthTable[0][i+1] = new Label(GameManager.roleNames.get(i), skin);

				towerGrowthTable[1][0] = new Label("Hoogte aan het begin van de ronde ", skin);
				for (int i = 0; i < numTeams; i++)
					towerGrowthTable[1][i+1] = new Label("" + lastState.towerHeightBeforeLastRound.get(i,  -1), skin);

				towerGrowthTable[2][0] = new Label("Groei tijdens de ronde ", skin);
				for (int i = 0; i < numTeams; i++)
					towerGrowthTable[2][i+1] = new Label("" + lastState.towerGrowthDuringLastRound.get(i,  -1), skin);

				towerGrowthTable[3][0] = new Label("Hoogte aan het eind van de ronde ", skin);
				for (int i = 0; i < numTeams; i++)
					towerGrowthTable[3][i+1] = new Label("" + lastState.towerHeightAfterLastRound.get(i, -1), skin);
			}
		}
		
		resourcesLabel.setText("Grondstoffen in de tuin in ronde " + lastState.currentRound);
		resourcesTable = new Label[7][2];
		resourcesTable[0][0] = new Label("Aan het begin van de ronde: ", skin);
		resourcesTable[0][1] = new Label("" + lastState.resourcesAtStartOfLastRound, skin);
		resourcesTable[1][0] = new Label("Verbruikt: ", skin);
		resourcesTable[1][1] = new Label("" + lastState.resourcesUsedInLastRound, skin);
		resourcesTable[2][0] = new Label("Natuurlijk aangegroeid: ", skin);
		resourcesTable[2][1] = new Label("" + lastState.resourcesNaturalGrowthInLastRound, skin);
		resourcesTable[3][0] = new Label("Ontdekt: ", skin);
		resourcesTable[3][1] = new Label("" + lastState.resourcesDiscoveredInLastRound, skin);
		resourcesTable[4][0] = new Label("Uit Eco Science: ", skin);
		resourcesTable[4][1] = new Label("" + lastState.resourcesFromEcoScienceInLastRound, skin);
		resourcesTable[5][0] = new Label("Naar Country Investment Trust: ", skin);
		resourcesTable[5][1] = new Label("" + lastState.resourcesToCountryInvestmentTrustInLastRound, skin);
		resourcesTable[6][0] = new Label("Aan het eind van de ronde: ", skin);
		resourcesTable[6][1] = new Label("" + lastState.resourcesAvailableInCurrentRound, skin);
	}
}
