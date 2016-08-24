package parsifalgame.screens;

import parsifalgame.GameManager;
import parsifalgame.simulator.Simulator;
import parsifalgame.state.GameState;
import parsifalgame.state.RoundChoices;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class ChoicesSubScreen extends SubScreen {

	public ChoicesSubScreen(BasicScreen parentScreen, Table parentTable) {
		super(parentScreen, parentTable);
	}

	private int currentPriceZwaard;
	private int currentPriceRaket;
	private int currentPriceVirus;
	private int numTeams;
	private int maxNumWeapons = GameState.MAX_NUM_WEAPONS;
	private int maxNumDefense = GameState.MAX_NUM_DEFENSE;
	private int currentPriceSchild;
	private int currentPriceRaketschild;
	private int currentPriceAntivirus;
	private int kostenBouwsteen;
	private int maxNumBouwstenen = GameState.MAX_NUM_BOUWSTENEN;
	private int kostenPatentRaket;
	private int kostenPatentRaketSchild;
	private int kostenPatentVirus;
	private int kostenPatentAntivirus;
	private int resourcesAvailable;
	private int towerHeight;

	// interactive elements
	private CheckBox raketPatent = new CheckBox("Raket", skin);
	private CheckBox raketschildPatent = new CheckBox("Raketschild", skin);
	private CheckBox virusPatent = new CheckBox("Virus", skin);
	private CheckBox antivirusPatent = new CheckBox("Antivirus", skin);

	private Label totaleKostenLabel = new Label("Totale kosten: 0", skin);

	private SelectBox<String> teamsBox1 = new SelectBox<String>(skin);
	private SelectBox<String> numZwaardBox1 = new SelectBox<String>(skin);
	private SelectBox<String> numRaketBox1 = new SelectBox<String>(skin);
	private SelectBox<String> numVirusBox1 = new SelectBox<String>(skin);
	private SelectBox<String> teamsBox2 = new SelectBox<String>(skin);
	private SelectBox<String> numZwaardBox2 = new SelectBox<String>(skin);
	private SelectBox<String> numRaketBox2 = new SelectBox<String>(skin);
	private SelectBox<String> numVirusBox2 = new SelectBox<String>(skin);
	private SelectBox<String> numSchildBox = new SelectBox<String>(skin);
	private SelectBox<String> numRaketSchildBox = new SelectBox<String>(skin);
	private SelectBox<String> numAntivirusBox = new SelectBox<String>(skin);
	private SelectBox<String> numBouwstenenBox = new SelectBox<String>(skin);
	private SelectBox<Integer> ecoScienceBox;
	private SelectBox<Integer> countryInvestmentTrustBox;
	private SelectBox<Integer> highTechAsiaBox;

	private TextButton submitButton = new TextButton("Stuur naar server", skin, "large");
	private TextButton hackButton = new TextButton("", skin, "large");

	private RoundChoices choices = new RoundChoices();
	
	private int existingShields;
	private int existingRocketShields;
	private int existingAntiViruses;
	private double priceFactor;
	private double relativePriceChange;

	private Integer basePriceZwaard;
	private Integer basePriceRaket;
	private Integer basePriceVirus;
	private Integer basePriceSchild;
	private Integer basePriceRaketSchild;
	private Integer basePriceAntivirus;

	private Label perStukLabel(int initPrice, int basePrice, int currentPrice) {
		Label label = perStukLabel(basePrice, currentPrice);
		if(basePrice < initPrice)
			label.setColor(Color.GREEN);
		return (label);		
	}

	private Label perStukLabel(int basePrice, int currentPrice) {
		return new Label(perStukStr(basePrice, currentPrice), skin);
	}

	private String perStukStr(int basePrice, int currentPrice) {
		int priceDifference = currentPrice - basePrice;
		if(priceDifference < 0)
			return String.format("(%d %d p st.)", basePrice, priceDifference);
		else if(priceDifference > 0)
			return String.format("(%d +%d p st.)", basePrice, priceDifference);
		else
			return "(" + basePrice + " p st.)";
	}

	@Override
	public void show() {
		super.show();

		Table leftTable = new Table();
		Table rightTable = new Table();

		// layout
		leftTable.defaults().left().pad(4);
		rightTable.defaults().left().pad(4);

		/*** Aanval ***/
		Label aanval = new Label("Aanvallen", skin, "large");
		leftTable.add(aanval).padTop(8);
		leftTable.row();

		leftTable.add();
		leftTable.add(new Label("Aanval 1", skin)).center();
		leftTable.add(new Label("Aanval 2", skin)).center();
		leftTable.row();

		leftTable.add(new Label("Doelwit: ", skin));

		Array<String> teams = new Array<String>(numTeams + 1);
		teams.add("Geen");
		for (int i = 0; i < numTeams; i++)
			teams.add(GameManager.roleNames.get(i));
		teamsBox1 = new SelectBox<String>(skin);
		teamsBox1.setItems(teams);
		leftTable.add(teamsBox1).fill();

		teamsBox2 = new SelectBox<String>(skin);
		teamsBox2.setItems(teams);
		leftTable.add(teamsBox2).fill();

		leftTable.row();

		Array<String> numWeapons = new Array<String>(maxNumWeapons + 1);
		for (int i = 0; i < maxNumWeapons + 1; i++)
			numWeapons.add("" + i);

		leftTable.add(new Label("Aantal Zwaarden: ", skin));
		numZwaardBox1 = new SelectBox<String>(skin);
		numZwaardBox1.setItems(numWeapons);
		leftTable.add(numZwaardBox1).fill();
		numZwaardBox2 = new SelectBox<String>(skin);
		numZwaardBox2.setItems(numWeapons);
		leftTable.add(numZwaardBox2).fill();
		leftTable.add(perStukLabel(GameState.INIT_PRICE_ZWAARD, basePriceZwaard, currentPriceZwaard));
		leftTable.row();

		leftTable.add(new Label("Aantal Raketten: ", skin));
		numRaketBox1 = new SelectBox<String>(skin);
		numRaketBox1.setItems(numWeapons);
		leftTable.add(numRaketBox1).fill();
		numRaketBox2 = new SelectBox<String>(skin);
		numRaketBox2.setItems(numWeapons);
		leftTable.add(numRaketBox2).fill();
		leftTable.add(perStukLabel(GameState.INIT_PRICE_RAKET, basePriceRaket, currentPriceRaket));
		leftTable.row();

		leftTable.add(new Label("Aantal Virussen: ", skin));
		numVirusBox1 = new SelectBox<String>(skin);
		numVirusBox1.setItems(numWeapons);
		leftTable.add(numVirusBox1).fill();
		numVirusBox2 = new SelectBox<String>(skin);
		numVirusBox2.setItems(numWeapons);
		leftTable.add(numVirusBox2).fill();
		leftTable.add(perStukLabel(GameState.INIT_PRICE_VIRUS, basePriceVirus, currentPriceVirus));
		leftTable.row();

		leftTable.row();

		/*** Verdediging ***/
		Label verdediging = new Label("Verdedigen", skin, "large");
		leftTable.add(verdediging).padTop(10);
		leftTable.row();

		leftTable.add(new Label("", skin));
		leftTable.add(new Label("Bestaand", skin)).center();
		leftTable.add(new Label("Nieuw", skin)).center();
		leftTable.row();
		
		Array<String> numDefense = new Array<String>(maxNumDefense + 1);
		for (int i = 0; i < maxNumDefense + 1; i++)
			numDefense.add("" + i);

		leftTable.add(new Label("Aantal Schilden", skin));
		leftTable.add(new Label("" + existingShields, skin)).center();
		numDefense.set(0, "Geen");
		numSchildBox = new SelectBox<String>(skin);
		numSchildBox.setItems(numDefense);
		leftTable.add(numSchildBox).fill();
		leftTable.add(perStukLabel(GameState.INIT_PRICE_SCHILD, basePriceSchild, currentPriceSchild));

		leftTable.row();

		leftTable.add(new Label("Aantal Raketschilden", skin));
		leftTable.add(new Label("" + existingRocketShields, skin)).center();
		numDefense.set(0, "Geen");
		numRaketSchildBox = new SelectBox<String>(skin);
		numRaketSchildBox.setItems(numDefense);
		leftTable.add(numRaketSchildBox).fill();
		leftTable.add(perStukLabel(GameState.INIT_PRICE_RAKETSCHILD, basePriceRaketSchild, currentPriceRaketschild));

		leftTable.row();

		leftTable.add(new Label("Aantal Antivirussen", skin));
		leftTable.add(new Label("" + existingAntiViruses, skin)).center();
		numDefense.set(0, "Geen");
		numAntivirusBox = new SelectBox<String>(skin);
		numAntivirusBox.setItems(numDefense);
		leftTable.add(numAntivirusBox).fill();
		leftTable.add(perStukLabel(GameState.INIT_PRICE_ANTIVIRUS, basePriceAntivirus, currentPriceAntivirus));

		leftTable.row();

		/*** transport ***/
		Label transport = new Label("Transport", skin, "large");
		leftTable.add(transport).padTop(10);

		leftTable.row();

		leftTable.add(new Label("Bouwstenen", skin));
		Array<String> numBouwstenen = new Array<String>(maxNumBouwstenen);
		numBouwstenen.add("Geen");
		for (int i = 0; i < maxNumBouwstenen; i++)
			numBouwstenen.add("" + (i + 1));
		numBouwstenenBox = new SelectBox<String>(skin);
		numBouwstenenBox.setItems(numBouwstenen);
		leftTable.add(numBouwstenenBox).colspan(2).fill();
		leftTable.add(perStukLabel(GameState.INIT_PRICE_BOUWSTEEN, kostenBouwsteen));
		leftTable.row();

		mainTable.add(leftTable).colspan(2);
		
		/*** Patenten ***/
		Label patenten = new Label("Patenten", skin, "large");
		rightTable.add(patenten).padTop(8).colspan(2);

		rightTable.row();

		raketPatent = new CheckBox("Raket", skin);
		raketschildPatent = new CheckBox("Raketschild", skin);
		virusPatent = new CheckBox("Virus",	skin);
		antivirusPatent = new CheckBox("Antivirus ", skin);

		rightTable.add(raketPatent);
		rightTable.add(new Label(perStukStr(GameState.INIT_PRICE_PATENT_RAKET, kostenPatentRaket), skin));
		rightTable.row();
		rightTable.add(raketschildPatent);
		rightTable.add(new Label(perStukStr(GameState.INIT_PRICE_PATENT_RAKETSCHILD, kostenPatentRaketSchild), skin));
		rightTable.row();
		rightTable.add(virusPatent);
		rightTable.add(new Label(perStukStr(GameState.INIT_PRICE_PATENT_VIRUS, kostenPatentVirus), skin));
		rightTable.row();
		rightTable.add(antivirusPatent);
		rightTable.add(new Label(perStukStr(GameState.INIT_PRICE_PATENT_ANTIVIRUS, kostenPatentAntivirus), skin));
		rightTable.row();
		
		/*** Klingsor's Global Trade Palace ***/
		ecoScienceBox = new SelectBox<Integer>(skin);
		countryInvestmentTrustBox = new SelectBox<Integer>(skin);
		highTechAsiaBox = new SelectBox<Integer>(skin);
		
		ecoScienceBox.setItems(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		countryInvestmentTrustBox.setItems(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		highTechAsiaBox.setItems(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		
		rightTable.add(new Label("Klingsor's global trade palace", skin, "large")).colspan(2).padTop(10);
		rightTable.row();
		rightTable.add(new Label("High Tech Asia", skin));
		rightTable.add(highTechAsiaBox);
		rightTable.row();
		rightTable.add(new Label("Country Investment Trust", skin));
		rightTable.add(countryInvestmentTrustBox);
		rightTable.row();
		rightTable.add(new Label("Eco Science", skin));
		rightTable.add(ecoScienceBox);
		rightTable.row();
		
		rightTable.add(new Label("Overige Feedback", skin, "large")).padTop(10);
		rightTable.row();
		
		/*** Totale kosten ***/
		rightTable.add(totaleKostenLabel).colspan(2);
		rightTable.row();

		/*** grondstoffen en hoogte van de toren ***/
		rightTable.add(
				new Label("Aantal grondstoffen beschikbaar: " + resourcesAvailable + 
						" (ronde 0: " + Simulator.INIT_RESOURCES + ")", skin)).colspan(2);
		rightTable.row();
		rightTable.add(new Label("Hoogte van de toren: " + towerHeight, skin)).colspan(2);
		rightTable.row();

		// prijzen 
		rightTable.add(new Label(String.format("Prijsniveau: %.0f (ronde 0: 100)" , priceFactor*100), skin)).colspan(2);
		rightTable.row();
		rightTable.add(new Label(
				String.format("Verandering prijzen t.o.v. vorige ronde: %.1f%%", relativePriceChange*100), 
				skin)).colspan(2);
		rightTable.row();
		
		mainTable.add(rightTable).top().padLeft(80);
		mainTable.row();

		mainTable.add(submitButton).colspan(3).fillX().padTop(15);
		mainTable.add(hackButton).minWidth(150).padTop(15);
		mainTable.row().padBottom(100);
		
		if (hackButton.getListeners() == null || !hackButton.getListeners().contains(hackedListener, true)) {
			hackButton.addListener(hackedListener);
		}

		if (submitButton.getListeners() == null || !submitButton.getListeners().contains(submitListener, true)) {
			submitButton.addListener(submitListener);
		}
		
		restoreChoices();
	}
	
	private ClickListener hackedListener = new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y) {
			choices.hacked = !choices.hacked;
			if (choices.hacked)
				hackButton.setText("H");
			else
				hackButton.setText("");
		}
	};

	private ClickListener submitListener = new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y) {
			storeChoices();
			parentScreen.getAsClient().setToConfirmScreen();
		}
	};
	
	private void restoreChoices() {
		numRaketBox1.setSelectedIndex(choices.attackNumberRaket[0]);
		numVirusBox1.setSelectedIndex(choices.attackNumberVirus[0]);
		numZwaardBox1.setSelectedIndex(choices.attackNumberZwaard[0]);
		teamsBox1.setSelectedIndex(choices.attackOnTeam[0] + 1);
		numRaketBox2.setSelectedIndex(choices.attackNumberRaket[1]);
		numVirusBox2.setSelectedIndex(choices.attackNumberVirus[1]);
		numZwaardBox2.setSelectedIndex(choices.attackNumberZwaard[1]);
		teamsBox2.setSelectedIndex(choices.attackOnTeam[1] + 1);
		numAntivirusBox.setSelectedIndex(choices.defenseNumberNewAntivirus);
		numRaketSchildBox.setSelectedIndex(choices.defenseNumberNewRaketschilden);
		numSchildBox.setSelectedIndex(choices.defenseNumberNewSchilden);
		choices.groupID = ((FacilitatorScreen) parentScreen).getTeamID();
		choices.groupName = new String("Team" + (choices.groupID + 1));
		antivirusPatent.setChecked(choices.patentAntivirus);
		raketPatent.setChecked(choices.patentRaket);
		raketschildPatent.setChecked(choices.patentRaketschild);
		virusPatent.setChecked(choices.patentVirus);
		numBouwstenenBox.setSelectedIndex(choices.purchaseBouwstenen);
		ecoScienceBox.setSelectedIndex(choices.ecoScienceInvestment);
		countryInvestmentTrustBox.setSelectedIndex(choices.countryInvestmentTrustInvestment);
		highTechAsiaBox.setSelectedIndex(choices.highTechAsiaInvestment);
		
		if (choices.hacked)
			hackButton.setText("H");
		else
			hackButton.setText("");
	}
	
	private void storeChoices() {
		choices.attackNumberRaket[0] = numRaketBox1.getSelectedIndex();
		choices.attackNumberVirus[0] = numVirusBox1.getSelectedIndex();
		choices.attackNumberZwaard[0] = numZwaardBox1.getSelectedIndex();
		choices.attackOnTeam[0] = teamsBox1.getSelectedIndex() - 1;
		choices.attackNumberRaket[1] = numRaketBox2.getSelectedIndex();
		choices.attackNumberVirus[1] = numVirusBox2.getSelectedIndex();
		choices.attackNumberZwaard[1] = numZwaardBox2.getSelectedIndex();
		choices.attackOnTeam[1] = teamsBox2.getSelectedIndex() - 1;
		choices.defenseNumberNewAntivirus = numAntivirusBox.getSelectedIndex();
		choices.defenseNumberNewRaketschilden = numRaketSchildBox
				.getSelectedIndex();
		choices.defenseNumberNewSchilden = numSchildBox.getSelectedIndex();
		choices.groupID = ((FacilitatorScreen) parentScreen).getTeamID();
		choices.groupName = new String("Team" + (choices.groupID + 1));
		choices.patentAntivirus = antivirusPatent.isChecked();
		choices.patentRaket = raketPatent.isChecked();
		choices.patentRaketschild = raketschildPatent.isChecked();
		choices.patentVirus = virusPatent.isChecked();
		choices.purchaseBouwstenen = numBouwstenenBox.getSelectedIndex();
		choices.ecoScienceInvestment = ecoScienceBox.getSelectedIndex();
		choices.countryInvestmentTrustInvestment = countryInvestmentTrustBox.getSelectedIndex();
		choices.highTechAsiaInvestment = highTechAsiaBox.getSelectedIndex();
	}

	@Override
	protected void update() {

		// validate choices
		if (teamsBox1.getSelectedIndex() == (((FacilitatorScreen) parentScreen).getTeamID() + 1))
			teamsBox1.setSelectedIndex(0);
		if (teamsBox2.getSelectedIndex() == (((FacilitatorScreen) parentScreen).getTeamID() + 1))
			teamsBox2.setSelectedIndex(0);

		// calculate costs
		int totaleKosten = 0;

		int numZwaard = numZwaardBox1.getSelectedIndex()
				+ numZwaardBox2.getSelectedIndex();
		int numRaket = numRaketBox1.getSelectedIndex()
				+ numRaketBox2.getSelectedIndex();
		int numVirus = numVirusBox1.getSelectedIndex()
				+ numVirusBox2.getSelectedIndex();
		int numSchild = numSchildBox.getSelectedIndex();
		int numRaketschild = numRaketSchildBox.getSelectedIndex();
		int numAntivirus = numAntivirusBox.getSelectedIndex();
		int numBouwstenen = numBouwstenenBox.getSelectedIndex();

		if (numZwaard > 0)
			totaleKosten += numZwaard * currentPriceZwaard;
		if (numRaket > 0)
			totaleKosten += numRaket * currentPriceRaket;
		if (numVirus > 0)
			totaleKosten += numVirus * currentPriceVirus;

		if (numSchild > 0)
			totaleKosten += numSchild * currentPriceSchild;
		if (numRaketschild > 0)
			totaleKosten += numRaketschild * currentPriceRaketschild;
		if (numAntivirus > 0)
			totaleKosten += numAntivirus * currentPriceAntivirus;
		if (numBouwstenen > 0)
			totaleKosten += numBouwstenen * kostenBouwsteen;

		if (raketPatent.isChecked())
			totaleKosten += kostenPatentRaket;
		if (raketschildPatent.isChecked())
			totaleKosten += kostenPatentRaketSchild;
		if (virusPatent.isChecked())
			totaleKosten += kostenPatentVirus;
		if (antivirusPatent.isChecked())
			totaleKosten += kostenPatentAntivirus;
		
		if (highTechAsiaBox.getSelected() > 0)
			totaleKosten += highTechAsiaBox.getSelected();
		if (countryInvestmentTrustBox.getSelected() > 0)
			totaleKosten += countryInvestmentTrustBox.getSelected();
		if (ecoScienceBox.getSelected() > 0)
			totaleKosten += ecoScienceBox.getSelected();
		
		totaleKostenLabel.setText("Totale kosten: " + totaleKosten);
		
		storeChoices();
	}

	@Override
	public void updateGameState(int roleIndex, GameState newState) {
		super.updateGameState(roleIndex, newState);

		numTeams = newState.numTeams;
		currentPriceZwaard = newState.prices.get(GameState.ZWAARD, -1);
		currentPriceRaket = newState.prices.get(GameState.RAKET, -1);
		currentPriceVirus = newState.prices.get(GameState.VIRUS, -1);
		currentPriceSchild = newState.prices.get(GameState.SCHILD, -1);
		currentPriceRaketschild = newState.prices.get(GameState.RAKETSCHILD, -1);
		currentPriceAntivirus = newState.prices.get(GameState.ANTIVIRUS, -1);
		kostenBouwsteen = newState.prices.get(GameState.BOUWSTEEN, -1);
		kostenPatentRaket = newState.prices.get(GameState.PATENT_RAKET, -1);
		kostenPatentRaketSchild = newState.prices.get(
				GameState.PATENT_RAKETSCHILD, -1);
		kostenPatentVirus = newState.prices.get(GameState.PATENT_VIRUS, -1);
		kostenPatentAntivirus = newState.prices.get(GameState.PATENT_ANTIVIRUS,
				-1);
		resourcesAvailable = newState.resourcesAvailableInCurrentRound;
		int teamID = ((FacilitatorScreen) parentScreen).getTeamID(); 
		towerHeight = newState.towerHeightAfterLastRound.get(teamID, -1);
		existingShields = newState.numShieldsAfterAttack.get(teamID, -1);
		existingRocketShields = newState.numRocketShieldsAfterAttack.get(teamID, -1);
		existingAntiViruses = newState.numAntiVirusesAfterAttack.get(teamID, -1);
		
		basePriceZwaard = newState.basePriceZwaard;
		basePriceRaket = newState.basePriceRaket;
		basePriceVirus = newState.basePriceVirus;
		basePriceSchild = newState.basePriceSchild;
		basePriceRaketSchild = newState.basePriceRaketSchild;
		basePriceAntivirus = newState.basePriceAntivirus;
		
		priceFactor = newState.priceFactor;
		relativePriceChange = newState.relativePriceChange;

		choices = new RoundChoices();
	}

	public RoundChoices getConfirmedChoices() {
		return choices;
	}

}
