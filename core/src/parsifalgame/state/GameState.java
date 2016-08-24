package parsifalgame.state;

import java.util.Arrays;

import parsifalgame.simulator.Simulator;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;

public class GameState {

	public static final Integer ZWAARD = 0;
	public static final Integer RAKET = 1;
	public static final Integer VIRUS = 2;
	public static final Integer SCHILD = 3;
	public static final Integer RAKETSCHILD = 4;
	public static final Integer ANTIVIRUS = 5;
	public static final Integer PATENT_RAKET = 6;
	public static final Integer PATENT_RAKETSCHILD = 7;
	public static final Integer PATENT_VIRUS = 8;
	public static final Integer PATENT_ANTIVIRUS = 9;
	public static final Integer BOUWSTEEN = 10;
	
	public static final Integer INIT_PRICE_ZWAARD = 1;
	public static final Integer INIT_PRICE_RAKET = 2;
	public static final Integer INIT_PRICE_VIRUS = 3;
	public static final Integer INIT_PRICE_SCHILD = 2;
	public static final Integer INIT_PRICE_RAKETSCHILD = 4;
	public static final Integer INIT_PRICE_ANTIVIRUS = 6;
	public static final Integer INIT_PRICE_PATENT_RAKET = 5;
	public static final Integer INIT_PRICE_PATENT_RAKETSCHILD = 5;
	public static final Integer INIT_PRICE_PATENT_VIRUS = 10;
	public static final Integer INIT_PRICE_PATENT_ANTIVIRUS = 10;
	public static final Integer INIT_PRICE_BOUWSTEEN = 5;
	
	public static final int MAX_NUM_WEAPONS = 19;
	public static final int MAX_NUM_DEFENSE = 19;
	public static final int MAX_NUM_BOUWSTENEN = 10;

	// automatically changed
	public boolean gameIsFinished = false;
	public int currentRound = 0;
	public int numTeams = 8;
	

	public long roundTimeLimitMS = (5 * 60 + 30) * 1000;

	public Array<Array<String>> battleResultsFromLastRound; // first array is
															// attackers, second
															// array is
															// defenders
	public double priceFactor;
	public double relativePriceChange;
	
	public int resourcesAtStartOfLastRound;
	public int resourcesUsedInLastRound;
	public int resourcesNaturalGrowthInLastRound;
	public int resourcesDiscoveredInLastRound;
	public int resourcesFromEcoScienceInLastRound;
	public int resourcesAvailableInCurrentRound;
	public int resourcesToCountryInvestmentTrustInLastRound;

	public int totalHighTechAsiaInvestment;

	public Integer basePriceZwaard = INIT_PRICE_ZWAARD;
	public Integer basePriceRaket = INIT_PRICE_RAKET;
	public Integer basePriceVirus = INIT_PRICE_VIRUS;
	public Integer basePriceSchild = INIT_PRICE_SCHILD;
	public Integer basePriceRaketSchild = INIT_PRICE_RAKETSCHILD;
	public Integer basePriceAntivirus = INIT_PRICE_ANTIVIRUS;

	public IntIntMap towerHeightBeforeLastRound = new IntIntMap();
	public IntIntMap towerGrowthDuringLastRound = new IntIntMap();
	public IntIntMap towerHeightAfterLastRound = new IntIntMap();
	public IntIntMap prices = new IntIntMap();

	public IntIntMap numShieldsBeforeAttack = new IntIntMap();
	public IntIntMap numRocketShieldsBeforeAttack = new IntIntMap();
	public IntIntMap numAntiVirusesBeforeAttack = new IntIntMap();
	public IntIntMap numShieldsAfterAttack = new IntIntMap();
	public IntIntMap numRocketShieldsAfterAttack = new IntIntMap();
	public IntIntMap numAntiVirusesAfterAttack = new IntIntMap();
	public IntIntMap numSwordsAttackedBy = new IntIntMap();
	public IntIntMap numRocketsAttackedBy = new IntIntMap();
	public IntIntMap numVirusesAttackedBy = new IntIntMap();

	public IntIntMap conqueredBouwstenen = new IntIntMap();
	public IntIntMap lostBouwstenen = new IntIntMap();
	public IntIntMap purchasedBouwstenen = new IntIntMap();
	
	// attacks: 1st dimension: numTeams; 2nd dimension: numAttacks;
	public IntIntMap[][] attacks;
	public static final int ATTACKER_ID_FIELD = 0;
	public static final int DEFENDER_ID_FIELD = 1;
	public static final int NUM_SWORDS_FIELD = 2;
	public static final int NUM_ROCKETS_FIELD = 3;
	public static final int NUM_VIRUSES_FIELD = 4;
	public static final int RESULT_FIELD = 5;
	
	public static final Integer ATTACK_RESULT_SUCCESS = 1;
	public static final Integer ATTACK_RESULT_FAIL = 2;
	public static final Integer ATTACK_RESULT_DRAW = 3;
	
	public IntMap<Boolean> hacks = new IntMap<Boolean>();

	public GameState() {
	}

	/**
	 * Prepare the first state in a game (essentially the default starting
	 * state)
	 * 
	 * @param numTeams
	 *            The amount of teams participating
	 */
	public void prepareFirst(int numTeams) {
		this.numTeams = numTeams;
		this.currentRound = 0;
		this.priceFactor = 1;
		
		this.resourcesAvailableInCurrentRound = Simulator.INIT_RESOURCES;
		
		prices.put(ZWAARD, basePriceZwaard);
		prices.put(RAKET, basePriceRaket);
		prices.put(VIRUS, basePriceVirus);
		prices.put(SCHILD, basePriceSchild);
		prices.put(RAKETSCHILD, basePriceRaketSchild);
		prices.put(ANTIVIRUS, basePriceAntivirus);
		prices.put(BOUWSTEEN, INIT_PRICE_BOUWSTEEN);
		prices.put(PATENT_RAKET, INIT_PRICE_PATENT_RAKET);
		prices.put(PATENT_VIRUS, INIT_PRICE_PATENT_VIRUS);
		prices.put(PATENT_RAKETSCHILD, INIT_PRICE_PATENT_RAKETSCHILD);
		prices.put(PATENT_ANTIVIRUS, INIT_PRICE_PATENT_ANTIVIRUS);

		// set as null, there isn't a last round
		battleResultsFromLastRound = null;

		attacks = new IntIntMap[numTeams][RoundChoices.NUM_ATTACKS_PER_TEAM];
		for (int i = 0; i < numTeams; i++) {
			towerGrowthDuringLastRound.put(i, 0);
			towerHeightBeforeLastRound.put(i, 0);
			towerHeightAfterLastRound.put(i, 0);
			numShieldsAfterAttack.put(i,  0);
			numRocketShieldsAfterAttack.put(i, 0);
			numAntiVirusesAfterAttack.put(i,  0);
			for(int j = 0; j < RoundChoices.NUM_ATTACKS_PER_TEAM; j++)
				attacks[i][j] = new IntIntMap();
			lostBouwstenen.put(i, 0);
			conqueredBouwstenen.put(i, 0);
			purchasedBouwstenen.put(i, 0);
		}
	}

	/**
	 * Prepare a state that will be filled by the simulator.
	 * 
	 * @param prevState
	 *            The last state that was simulated, or the initial state
	 */
	public void prepareFromPrevious(GameState prevState) {
		this.numTeams = prevState.numTeams;
		this.currentRound = prevState.currentRound + 1;

		battleResultsFromLastRound = new Array<Array<String>>(numTeams);
		for (int i = 0; i < numTeams; i++) {
			battleResultsFromLastRound.add(new Array<String>(numTeams));
			lostBouwstenen.put(i, 0);
			conqueredBouwstenen.put(i, 0);
			purchasedBouwstenen.put(i, 0);
			towerHeightBeforeLastRound.put(i, prevState.towerHeightAfterLastRound.get(i,-1));
			for (int j = 0; j < numTeams; j++)
				battleResultsFromLastRound.get(i).add("");
		}
	}

	public void initResources(int initResources) {
		resourcesAvailableInCurrentRound = initResources;
	}

	public class AttackInfo {
		public int attacker;
		public int defender;
		public int zwaarden;
		public int raketten;
		public int virussen;
		public int result;
	}
	
	public Array<AttackInfo> util_getAttacksOnDefender(int defenderID){
		Array<AttackInfo> infos = new Array<GameState.AttackInfo>();
		for (int attackerID = 0; attackerID < numTeams; attackerID++) {
			for(int numAttack = 0; numAttack < RoundChoices.NUM_ATTACKS_PER_TEAM; numAttack++) {
				if (attacks[attackerID][numAttack].get(DEFENDER_ID_FIELD, -1) == defenderID){
					IntIntMap map = attacks[attackerID][numAttack];
					AttackInfo info = new AttackInfo();
					info.attacker = attackerID;
					info.defender = defenderID;
					info.zwaarden = map.get(NUM_SWORDS_FIELD, 0);
					info.raketten = map.get(NUM_ROCKETS_FIELD, 0);
					info.virussen = map.get(NUM_VIRUSES_FIELD, 0);
					info.result = map.get(RESULT_FIELD, 0);
					infos.add(info);
				}
			}
		}
		return infos;
	}
	
	public void updateAttacks(RoundChoices[] choicesFromGroups) {
		attacks = new IntIntMap[numTeams][RoundChoices.NUM_ATTACKS_PER_TEAM];
		for (int attackerID = 0; attackerID < numTeams; attackerID++) {
			for(int numAttack = 0; numAttack < RoundChoices.NUM_ATTACKS_PER_TEAM; numAttack++) {
				attacks[attackerID][numAttack] = new IntIntMap();
				attacks[attackerID][numAttack].put(ATTACKER_ID_FIELD, attackerID);
				attacks[attackerID][numAttack].put(DEFENDER_ID_FIELD, choicesFromGroups[attackerID].attackOnTeam[numAttack]);
				attacks[attackerID][numAttack].put(NUM_SWORDS_FIELD, choicesFromGroups[attackerID].attackNumberZwaard[numAttack]);
				attacks[attackerID][numAttack].put(NUM_ROCKETS_FIELD, choicesFromGroups[attackerID].attackNumberRaket[numAttack]);
				attacks[attackerID][numAttack].put(NUM_VIRUSES_FIELD, choicesFromGroups[attackerID].attackNumberVirus[numAttack]);
				attacks[attackerID][numAttack].put(RESULT_FIELD, ATTACK_RESULT_FAIL);  // attack fails by default 
			}
		}
	}

	public static GameState randomSimulatedState(int players, int rounds) {
		GameState state = new GameState();
		state.prepareFirst(players);
		Simulator sim = new Simulator(state);
		for (int j = 0; j < rounds; ++j){
			RoundChoices[] choices = new RoundChoices[players];
			for (int i = 0; i < players; ++i){
				choices[i] = new RoundChoices();
				choices[i].patentAntivirus = MathUtils.randomBoolean();
				choices[i].patentRaket = MathUtils.randomBoolean();
				choices[i].patentRaketschild = MathUtils.randomBoolean();
				choices[i].patentVirus = MathUtils.randomBoolean();
				for (int t = 0; t < 2; ++t){
					choices[i].attackOnTeam[t] = MathUtils.random(7);
					choices[i].attackNumberRaket[t] = MathUtils.random(MAX_NUM_WEAPONS);
					choices[i].attackNumberVirus[t] = MathUtils.random(MAX_NUM_WEAPONS);
					choices[i].attackNumberZwaard[t] = MathUtils.random(MAX_NUM_WEAPONS);
				}
				choices[i].defenseNumberNewAntivirus = MathUtils.random(MAX_NUM_DEFENSE);
				choices[i].defenseNumberNewRaketschilden = MathUtils.random(MAX_NUM_DEFENSE);
				choices[i].defenseNumberNewSchilden = MathUtils.random(MAX_NUM_DEFENSE);
				choices[i].countryInvestmentTrustInvestment = MathUtils.random(20);
				choices[i].ecoScienceInvestment = MathUtils.random(20);
				choices[i].highTechAsiaInvestment = MathUtils.random(20);
				choices[i].purchaseBouwstenen = MathUtils.random(MAX_NUM_BOUWSTENEN);
			}
			state = sim.simulate(choices);
		}
		return state;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(attacks);
		result = prime
				* result
				+ ((basePriceAntivirus == null) ? 0 : basePriceAntivirus
						.hashCode());
		result = prime * result
				+ ((basePriceRaket == null) ? 0 : basePriceRaket.hashCode());
		result = prime
				* result
				+ ((basePriceRaketSchild == null) ? 0 : basePriceRaketSchild
						.hashCode());
		result = prime * result
				+ ((basePriceSchild == null) ? 0 : basePriceSchild.hashCode());
		result = prime * result
				+ ((basePriceVirus == null) ? 0 : basePriceVirus.hashCode());
		result = prime * result
				+ ((basePriceZwaard == null) ? 0 : basePriceZwaard.hashCode());
		result = prime
				* result
				+ ((battleResultsFromLastRound == null) ? 0
						: battleResultsFromLastRound.hashCode());
		result = prime
				* result
				+ ((conqueredBouwstenen == null) ? 0 : conqueredBouwstenen
						.hashCode());
		result = prime * result + currentRound;
		result = prime * result + (gameIsFinished ? 1231 : 1237);
		result = prime * result + ((hacks == null) ? 0 : hacks.hashCode());
		result = prime * result
				+ ((lostBouwstenen == null) ? 0 : lostBouwstenen.hashCode());
		result = prime
				* result
				+ ((numAntiVirusesAfterAttack == null) ? 0
						: numAntiVirusesAfterAttack.hashCode());
		result = prime
				* result
				+ ((numAntiVirusesBeforeAttack == null) ? 0
						: numAntiVirusesBeforeAttack.hashCode());
		result = prime
				* result
				+ ((numRocketShieldsAfterAttack == null) ? 0
						: numRocketShieldsAfterAttack.hashCode());
		result = prime
				* result
				+ ((numRocketShieldsBeforeAttack == null) ? 0
						: numRocketShieldsBeforeAttack.hashCode());
		result = prime
				* result
				+ ((numRocketsAttackedBy == null) ? 0 : numRocketsAttackedBy
						.hashCode());
		result = prime
				* result
				+ ((numShieldsAfterAttack == null) ? 0 : numShieldsAfterAttack
						.hashCode());
		result = prime
				* result
				+ ((numShieldsBeforeAttack == null) ? 0
						: numShieldsBeforeAttack.hashCode());
		result = prime
				* result
				+ ((numSwordsAttackedBy == null) ? 0 : numSwordsAttackedBy
						.hashCode());
		result = prime * result + numTeams;
		result = prime
				* result
				+ ((numVirusesAttackedBy == null) ? 0 : numVirusesAttackedBy
						.hashCode());
		long temp;
		temp = Double.doubleToLongBits(priceFactor);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((prices == null) ? 0 : prices.hashCode());
		result = prime
				* result
				+ ((purchasedBouwstenen == null) ? 0 : purchasedBouwstenen
						.hashCode());
		temp = Double.doubleToLongBits(relativePriceChange);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + resourcesAtStartOfLastRound;
		result = prime * result + resourcesAvailableInCurrentRound;
		result = prime * result + resourcesDiscoveredInLastRound;
		result = prime * result + resourcesFromEcoScienceInLastRound;
		result = prime * result + resourcesNaturalGrowthInLastRound;
		result = prime * result + resourcesToCountryInvestmentTrustInLastRound;
		result = prime * result + resourcesUsedInLastRound;
		result = prime * result
				+ (int) (roundTimeLimitMS ^ (roundTimeLimitMS >>> 32));
		result = prime * result + totalHighTechAsiaInvestment;
		result = prime
				* result
				+ ((towerGrowthDuringLastRound == null) ? 0
						: towerGrowthDuringLastRound.hashCode());
		result = prime
				* result
				+ ((towerHeightAfterLastRound == null) ? 0
						: towerHeightAfterLastRound.hashCode());
		result = prime
				* result
				+ ((towerHeightBeforeLastRound == null) ? 0
						: towerHeightBeforeLastRound.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameState other = (GameState) obj;
		if (!Arrays.deepEquals(attacks, other.attacks))
			return false;
		if (basePriceAntivirus == null) {
			if (other.basePriceAntivirus != null)
				return false;
		} else if (!basePriceAntivirus.equals(other.basePriceAntivirus))
			return false;
		if (basePriceRaket == null) {
			if (other.basePriceRaket != null)
				return false;
		} else if (!basePriceRaket.equals(other.basePriceRaket))
			return false;
		if (basePriceRaketSchild == null) {
			if (other.basePriceRaketSchild != null)
				return false;
		} else if (!basePriceRaketSchild.equals(other.basePriceRaketSchild))
			return false;
		if (basePriceSchild == null) {
			if (other.basePriceSchild != null)
				return false;
		} else if (!basePriceSchild.equals(other.basePriceSchild))
			return false;
		if (basePriceVirus == null) {
			if (other.basePriceVirus != null)
				return false;
		} else if (!basePriceVirus.equals(other.basePriceVirus))
			return false;
		if (basePriceZwaard == null) {
			if (other.basePriceZwaard != null)
				return false;
		} else if (!basePriceZwaard.equals(other.basePriceZwaard))
			return false;
		if (battleResultsFromLastRound == null) {
			if (other.battleResultsFromLastRound != null)
				return false;
		} else if (!battleResultsFromLastRound
				.equals(other.battleResultsFromLastRound))
			return false;
		if (conqueredBouwstenen == null) {
			if (other.conqueredBouwstenen != null)
				return false;
		} else if (!conqueredBouwstenen.equals(other.conqueredBouwstenen))
			return false;
		if (currentRound != other.currentRound)
			return false;
		if (gameIsFinished != other.gameIsFinished)
			return false;
		if (hacks == null) {
			if (other.hacks != null)
				return false;
		} else if (!hacks.equals(other.hacks))
			return false;
		if (lostBouwstenen == null) {
			if (other.lostBouwstenen != null)
				return false;
		} else if (!lostBouwstenen.equals(other.lostBouwstenen))
			return false;
		if (numAntiVirusesAfterAttack == null) {
			if (other.numAntiVirusesAfterAttack != null)
				return false;
		} else if (!numAntiVirusesAfterAttack
				.equals(other.numAntiVirusesAfterAttack))
			return false;
		if (numAntiVirusesBeforeAttack == null) {
			if (other.numAntiVirusesBeforeAttack != null)
				return false;
		} else if (!numAntiVirusesBeforeAttack
				.equals(other.numAntiVirusesBeforeAttack))
			return false;
		if (numRocketShieldsAfterAttack == null) {
			if (other.numRocketShieldsAfterAttack != null)
				return false;
		} else if (!numRocketShieldsAfterAttack
				.equals(other.numRocketShieldsAfterAttack))
			return false;
		if (numRocketShieldsBeforeAttack == null) {
			if (other.numRocketShieldsBeforeAttack != null)
				return false;
		} else if (!numRocketShieldsBeforeAttack
				.equals(other.numRocketShieldsBeforeAttack))
			return false;
		if (numRocketsAttackedBy == null) {
			if (other.numRocketsAttackedBy != null)
				return false;
		} else if (!numRocketsAttackedBy.equals(other.numRocketsAttackedBy))
			return false;
		if (numShieldsAfterAttack == null) {
			if (other.numShieldsAfterAttack != null)
				return false;
		} else if (!numShieldsAfterAttack.equals(other.numShieldsAfterAttack))
			return false;
		if (numShieldsBeforeAttack == null) {
			if (other.numShieldsBeforeAttack != null)
				return false;
		} else if (!numShieldsBeforeAttack.equals(other.numShieldsBeforeAttack))
			return false;
		if (numSwordsAttackedBy == null) {
			if (other.numSwordsAttackedBy != null)
				return false;
		} else if (!numSwordsAttackedBy.equals(other.numSwordsAttackedBy))
			return false;
		if (numTeams != other.numTeams)
			return false;
		if (numVirusesAttackedBy == null) {
			if (other.numVirusesAttackedBy != null)
				return false;
		} else if (!numVirusesAttackedBy.equals(other.numVirusesAttackedBy))
			return false;
		if (Double.doubleToLongBits(priceFactor) != Double
				.doubleToLongBits(other.priceFactor))
			return false;
		if (prices == null) {
			if (other.prices != null)
				return false;
		} else if (!prices.equals(other.prices))
			return false;
		if (purchasedBouwstenen == null) {
			if (other.purchasedBouwstenen != null)
				return false;
		} else if (!purchasedBouwstenen.equals(other.purchasedBouwstenen))
			return false;
		if (Double.doubleToLongBits(relativePriceChange) != Double
				.doubleToLongBits(other.relativePriceChange))
			return false;
		if (resourcesAtStartOfLastRound != other.resourcesAtStartOfLastRound)
			return false;
		if (resourcesAvailableInCurrentRound != other.resourcesAvailableInCurrentRound)
			return false;
		if (resourcesDiscoveredInLastRound != other.resourcesDiscoveredInLastRound)
			return false;
		if (resourcesFromEcoScienceInLastRound != other.resourcesFromEcoScienceInLastRound)
			return false;
		if (resourcesNaturalGrowthInLastRound != other.resourcesNaturalGrowthInLastRound)
			return false;
		if (resourcesToCountryInvestmentTrustInLastRound != other.resourcesToCountryInvestmentTrustInLastRound)
			return false;
		if (resourcesUsedInLastRound != other.resourcesUsedInLastRound)
			return false;
		if (roundTimeLimitMS != other.roundTimeLimitMS)
			return false;
		if (totalHighTechAsiaInvestment != other.totalHighTechAsiaInvestment)
			return false;
		if (towerGrowthDuringLastRound == null) {
			if (other.towerGrowthDuringLastRound != null)
				return false;
		} else if (!towerGrowthDuringLastRound
				.equals(other.towerGrowthDuringLastRound))
			return false;
		if (towerHeightAfterLastRound == null) {
			if (other.towerHeightAfterLastRound != null)
				return false;
		} else if (!towerHeightAfterLastRound
				.equals(other.towerHeightAfterLastRound))
			return false;
		if (towerHeightBeforeLastRound == null) {
			if (other.towerHeightBeforeLastRound != null)
				return false;
		} else if (!towerHeightBeforeLastRound
				.equals(other.towerHeightBeforeLastRound))
			return false;
		return true;
	}

}
