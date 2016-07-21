package parsifalgame.simulator;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;
import parsifalgame.state.GameState;
import parsifalgame.state.RoundChoices;

import org.apache.commons.math3.distribution.NormalDistribution;

public class Simulator {

	/**
	 * All previous states are stored
	 */
	public Array<GameState> previousStates = new Array<GameState>();
	private int mostRecentStateIndex = 0;

	private Array<RoundChoices[]> previousChoices = new Array<RoundChoices[]>();

	public int discovered = 0;
	public IntArray terrorismTargets = new IntArray();
	public int earthquakeDamage = 0;
	private int numTeams;
	
	private static final int NUM_RAKET_BEATS_ANTIVIRUS = 3;
	private static final int NUM_ZWAARD_BEATS_ANTIVIRUS = 9;
	private static final int NUM_ZWAARD_BEATS_RAKETSCHILD = 3;

	private static final int NUM_RESOURCES_PER_ZWAARD = 2;
	private static final int NUM_RESOURCES_PER_RAKET = 4;
	private static final int NUM_RESOURCES_PER_VIRUS = 6;
	private static final int NUM_RESOURCES_PER_SCHILD = 2;
	private static final int NUM_RESOURCES_PER_RAKETSCHILD = 4;
	private static final int NUM_RESOURCES_PER_ANTIVIRUS = 6;
	private static final int NUM_RESOURCES_PER_BOUWSTEEN = 1;

	public static final int INIT_RESOURCES = 400;
	private static final int MAX_REGENERATE_PER_ROUND = INIT_RESOURCES / 10;
	private static final double MEAN_REGENERATE = (double) INIT_RESOURCES;
	private static final double STDEV_REGENERATE = MEAN_REGENERATE / 3.0;
	private static final double RESOURCE_THRESHOLD_FACTOR = 0.5;
	private static final double ECO_SCIENCE_GROWTH_FACTOR = (double)INIT_RESOURCES / 50.0;
	private static final double COUNTRY_INVESTMENT_FUND_DECLINE_FACTOR = (double)INIT_RESOURCES / 200.0;
	private static final int HIGH_TECH_ASIA_THRESHOLD = 30;
	private static final double HIGH_TECH_ASIA_PRICE_DECREASE = 0.1;  // weapons become this factor cheaper when threshold is reached

	private NormalDistribution norma = new NormalDistribution(MEAN_REGENERATE, STDEV_REGENERATE);

	private class Attacker{
		public int numZwaarden, numRaketten, numVirussen, attackerID;
		
		public Attacker(int attID, int numZ, int numR, int numV) {
			attackerID = attID;
			numZwaarden = numZ;
			numRaketten = numR;
			numVirussen = numV;
		}
	}
	
	private int determineWinner(Array<Attacker> attackers) {
		Array<Integer> winners = new Array<Integer>();
		
		// check for attacker that used most virus
		int maxNumVirusUsed = 0;
		for (int i = 0; i < attackers.size; i++) {
			if(attackers.get(i).numVirussen > maxNumVirusUsed) {
				maxNumVirusUsed = attackers.get(i).numVirussen; 
				winners = new Array<Integer>();
				winners.add(attackers.get(i).attackerID);
			}
			else if ((maxNumVirusUsed > 0) && (attackers.get(i).numVirussen == maxNumVirusUsed))
				winners.add(attackers.get(i).attackerID);				
		}
		
		if(winners.size == 1)
			return winners.first().intValue();
		
		// check for attacker that used most raket
		int maxNumRaketUsed = 0;
		for (int i = 0; i < attackers.size; i++) {
			if(attackers.get(i).numRaketten > maxNumRaketUsed) {
				maxNumRaketUsed = attackers.get(i).numRaketten; 
				winners = new Array<Integer>();
				winners.add(attackers.get(i).attackerID);
			}
			else if ((maxNumRaketUsed > 0) && (attackers.get(i).numRaketten == maxNumRaketUsed))
				winners.add(attackers.get(i).attackerID);				
		}
		
		if(winners.size == 1)
			return winners.first().intValue();
		
		// check for attacker that used most zwaard
		int maxNumZwaardUsed = 0;
		for (int i = 0; i < attackers.size; i++) {
			if(attackers.get(i).numZwaarden > maxNumZwaardUsed) {
				maxNumZwaardUsed = attackers.get(i).numZwaarden; 
				winners = new Array<Integer>();
				winners.add(attackers.get(i).attackerID);
			}
			else if ((maxNumZwaardUsed > 0) && (attackers.get(i).numZwaarden == maxNumZwaardUsed))
				winners.add(attackers.get(i).attackerID);				
		}
		
		if(winners.size == 1)
			return winners.first().intValue();		
		else
			return -1;
	}
	
	
	/**
	 * Creates a simulator with a given starting GameState
	 */
	public Simulator(GameState initialState) {
		previousStates.add(initialState);
		this.numTeams = initialState.numTeams;
	}

	public GameState getMostRecentState() {
		return previousStates.get(mostRecentStateIndex);
	}

	public int getMostRecentStateIndex() {
		return mostRecentStateIndex;
	}

	public RoundChoices[] getMostRecentRoundChoices(){
		int last = previousChoices.size - 1;
		if (last >= 0)
			return previousChoices.get(last);
		else
			return null;
	}
	
	public GameState reSimulate() {
		int lastChoicesIndex = previousChoices.size - 1;
		if (lastChoicesIndex >= 0){
			// revert the most recent choices (they will be re-added later)
			RoundChoices[] reChoices = previousChoices.get(lastChoicesIndex);
			previousChoices.removeIndex(lastChoicesIndex);
			previousStates.removeIndex(mostRecentStateIndex);
			mostRecentStateIndex -= 1;
			return simulate(reChoices);
		} else 
			return null;
	}

	/**
	 * Runs the simulation and returns the next state.
	 */
	public GameState simulate(RoundChoices[] choicesFromGroups) {

		if (choicesFromGroups.length != numTeams) {
			throw new RuntimeException("wrong number of facilitators (this should not happen).");
		}

		GameState latestState = previousStates.get(mostRecentStateIndex);
		
		// prepare a new state
		GameState nextState = new GameState();
		nextState.prepareFromPrevious(latestState);

		// nullify any hacked choices
		for (int i = 0; i < numTeams; i++) {
			if (choicesFromGroups[i].hacked){
				nextState.hacks.put(i, true);
				RoundChoices oldChoices = choicesFromGroups[i];
				choicesFromGroups[i] = new RoundChoices();
				choicesFromGroups[i].groupID = oldChoices.groupID;
				choicesFromGroups[i].groupName = oldChoices.groupName;
			}
		}
		
		// update defenses before battle
		for (int i = 0; i < numTeams; i++) {
			nextState.numShieldsBeforeAttack.put(i, latestState.numShieldsAfterAttack.get(i, 0) + choicesFromGroups[i].defenseNumberNewSchilden);
			nextState.numRocketShieldsBeforeAttack.put(i, latestState.numRocketShieldsAfterAttack.get(i, 0) + choicesFromGroups[i].defenseNumberNewRaketschilden);
			nextState.numAntiVirusesBeforeAttack.put(i, latestState.numAntiVirusesAfterAttack.get(i, 0) + choicesFromGroups[i].defenseNumberNewAntivirus);

			nextState.numShieldsAfterAttack.put(i, latestState.numShieldsAfterAttack.get(i, 0) + choicesFromGroups[i].defenseNumberNewSchilden);
			nextState.numRocketShieldsAfterAttack.put(i, latestState.numRocketShieldsAfterAttack.get(i, 0) + choicesFromGroups[i].defenseNumberNewRaketschilden);
			nextState.numAntiVirusesAfterAttack.put(i, latestState.numAntiVirusesAfterAttack.get(i, 0) + choicesFromGroups[i].defenseNumberNewAntivirus);			
		}
		
		// update attacks
		nextState.updateAttacks(choicesFromGroups);
			
		// calculate battle results: iterate over all defenders
		for (int defenderID = 0; defenderID < numTeams; defenderID++) {

			int numSchilden = nextState.numShieldsBeforeAttack.get(defenderID, 0);
			int numRaketschilden = nextState.numRocketShieldsBeforeAttack.get(defenderID, 0);
			int numAntivirussen = nextState.numAntiVirusesBeforeAttack.get(defenderID, 0);

			nextState.purchasedBouwstenen.put(defenderID, choicesFromGroups[defenderID].purchaseBouwstenen);
			
			int numZwaarden = 0;
			int numRaketten = 0;
			int numVirussen = 0;

			Array<Attacker> attackers = new Array<Attacker>();

			boolean attackSuccessful = false;

			// iterate over all teams to find attackers
			for(int attackerID = 0; attackerID < numTeams; attackerID++) {
				for(int attack = 0; attack < 2; attack++)
					if((attackerID != defenderID) && (choicesFromGroups[attackerID].attackOnTeam[attack] == defenderID)) {

					// team attackerID attacks team defenderID
						attackers.add(new Attacker(
								attackerID,
								choicesFromGroups[attackerID].attackNumberZwaard[attack], 
								choicesFromGroups[attackerID].attackNumberRaket[attack], 
								choicesFromGroups[attackerID].attackNumberVirus[attack]));

						numZwaarden += choicesFromGroups[attackerID].attackNumberZwaard[attack];
						numRaketten += choicesFromGroups[attackerID].attackNumberRaket[attack];
						numVirussen += choicesFromGroups[attackerID].attackNumberVirus[attack];
					}				
			}
			nextState.numSwordsAttackedBy.put(defenderID, numZwaarden);
			nextState.numRocketsAttackedBy.put(defenderID, numRaketten);
			nextState.numVirusesAttackedBy.put(defenderID, numVirussen);
			
			// determine battle success/failure, redistribute the battle loot, 
			// and update remaining defense weapons after battle
			if(attackers.size > 0){
				
				if(numVirussen > numAntivirussen) {
					attackSuccessful = true;
					numAntivirussen = 0;
					
					// Rocket shields and shields cannot defend vs viruses, but viruses cannot harm them either?
					numRaketschilden -= Math.min(numRaketschilden, numRaketten);
					numSchilden -= Math.min(numSchilden, numZwaarden);
				} 
				else {
					numAntivirussen -= numVirussen;
					
					if (numRaketten > (numRaketschilden + numAntivirussen * NUM_RAKET_BEATS_ANTIVIRUS)) {
						attackSuccessful = true;
						numAntivirussen = 0;
						numRaketschilden = 0;
						numSchilden -= Math.min(numSchilden, numZwaarden);						
					}
					else { 
						// First, all rockets attempt to destroy all anti-viruses. 
						// If that succeeds the remaining rockets destroy rocket shields
						int decreaseAntivirussen = Math.min(numAntivirussen, numRaketten / NUM_RAKET_BEATS_ANTIVIRUS);
						numAntivirussen -= decreaseAntivirussen;
						numRaketten -= decreaseAntivirussen * NUM_RAKET_BEATS_ANTIVIRUS;
						
						// remaining rockets destroy rocket shields
						numRaketschilden -= Math.min(numRaketschilden, numRaketten);
						
						if(numZwaarden > (numSchilden + numRaketschilden * NUM_ZWAARD_BEATS_RAKETSCHILD + numAntivirussen * NUM_ZWAARD_BEATS_ANTIVIRUS)) {
							attackSuccessful = true;
							numAntivirussen = 0;
							numRaketschilden = 0;
							numSchilden = 0;
						}
						else { 
							// First, all swords attempt to destroy all anti-viruses. 
							// If that succeeds the remaining swords attempt to destroy all rocket shields. 
							// If that succeeds, the remaining swords destroy shields.
							decreaseAntivirussen = Math.min(numAntivirussen, numZwaarden / NUM_ZWAARD_BEATS_ANTIVIRUS);
							numAntivirussen -= decreaseAntivirussen;
							numZwaarden -= decreaseAntivirussen * NUM_ZWAARD_BEATS_ANTIVIRUS;

							// remaining swords destroy rocket shields
							int decreaseRaketschilden = Math.min(numRaketschilden, numZwaarden / NUM_ZWAARD_BEATS_RAKETSCHILD);
							numRaketschilden -= decreaseRaketschilden;
							numZwaarden -= decreaseAntivirussen * NUM_ZWAARD_BEATS_RAKETSCHILD;
							
							// remaining swords destroy shields
							numSchilden -= Math.min(numSchilden, numZwaarden);
						}		
					}
				}

				// update battle results in gameState for next round
				Iterator<Attacker> attackerIterator = attackers.iterator(); 
				while(attackerIterator.hasNext()) {
					int attackerID = attackerIterator.next().attackerID;
					if(attackSuccessful)
						nextState.battleResultsFromLastRound.get(attackerID).set(defenderID, "W");
					else
						nextState.battleResultsFromLastRound.get(attackerID).set(defenderID, "V");
				}

				// if defender was defeated, determine which attacking team conquered  
				// the building blocks in the transport of the defending team
				if(attackSuccessful) {
					int winner = determineWinner(attackers);
					if(winner != -1) { // there is only one winner
						int conquered = nextState.conqueredBouwstenen.get(winner, 0);
						nextState.conqueredBouwstenen.put(winner, conquered + choicesFromGroups[defenderID].purchaseBouwstenen);
						for(int numAttack = 0; numAttack < RoundChoices.NUM_ATTACKS_PER_TEAM; numAttack++)
							if(nextState.attacks[winner][numAttack].get(GameState.DEFENDER_ID_FIELD, -1) == defenderID)
								nextState.attacks[winner][numAttack].put(GameState.RESULT_FIELD, 1);
					}
					int purchase = choicesFromGroups[defenderID].purchaseBouwstenen;
					if(purchase > 0)
						nextState.lostBouwstenen.put(defenderID, purchase);
				}
				
				// update defenses after battles
				nextState.numShieldsAfterAttack.put(defenderID, numSchilden);
				nextState.numRocketShieldsAfterAttack.put(defenderID, numRaketschilden);
				nextState.numAntiVirusesAfterAttack.put(defenderID, numAntivirussen);
			}
		}
		
		// update tower heights
		for (int i = 0; i < numTeams; i++) {
			int latestHeight = nextState.towerHeightBeforeLastRound.get(i,0);
			int towerGrowth = 
					choicesFromGroups[i].purchaseBouwstenen + 
					nextState.conqueredBouwstenen.get(i, 0) - 
					nextState.lostBouwstenen.get(i, 0); 
			nextState.towerGrowthDuringLastRound.put(i, towerGrowth);
			nextState.towerHeightAfterLastRound.put(i, latestHeight + towerGrowth);
		}

		// determine effects of investments in Klingsor's global trade palace
		int totalEcoScienceInvestment = 0;
		int totalCountryInvestmentFundInvestment = 0;
		int totalHighTechAsiaInvestment = 0;
		
		for(int i = 0; i < numTeams; i++) {
			totalEcoScienceInvestment += choicesFromGroups[i].ecoScienceInvestment;
			totalCountryInvestmentFundInvestment += choicesFromGroups[i].countryInvestmentTrustInvestment;
			totalHighTechAsiaInvestment += choicesFromGroups[i].highTechAsiaInvestment;
		}
		
		nextState.resourcesFromEcoScienceInLastRound = (int)(totalEcoScienceInvestment * ECO_SCIENCE_GROWTH_FACTOR);
		nextState.resourcesToCountryInvestmentTrustInLastRound = (int)(totalCountryInvestmentFundInvestment * COUNTRY_INVESTMENT_FUND_DECLINE_FACTOR);
		
		nextState.totalHighTechAsiaInvestment = latestState.totalHighTechAsiaInvestment + totalHighTechAsiaInvestment;
		int numTimesThresholdReached = nextState.totalHighTechAsiaInvestment / HIGH_TECH_ASIA_THRESHOLD;
		nextState.basePriceZwaard = Math.max(1, (int)Math.round(GameState.INIT_PRICE_ZWAARD * Math.pow(1-HIGH_TECH_ASIA_PRICE_DECREASE, numTimesThresholdReached)));
		nextState.basePriceRaket = Math.max(1, (int)Math.round(GameState.INIT_PRICE_RAKET * Math.pow(1-HIGH_TECH_ASIA_PRICE_DECREASE, numTimesThresholdReached)));
		nextState.basePriceVirus = Math.max(1, (int)Math.round(GameState.INIT_PRICE_VIRUS * Math.pow(1-HIGH_TECH_ASIA_PRICE_DECREASE, numTimesThresholdReached)));
		nextState.basePriceSchild = Math.max(1, (int)Math.round(GameState.INIT_PRICE_SCHILD * Math.pow(1-HIGH_TECH_ASIA_PRICE_DECREASE, numTimesThresholdReached)));
		nextState.basePriceRaketSchild = Math.max(1, (int)Math.round(GameState.INIT_PRICE_RAKETSCHILD * Math.pow(1-HIGH_TECH_ASIA_PRICE_DECREASE, numTimesThresholdReached)));
		nextState.basePriceAntivirus = Math.max(1, (int)Math.round(GameState.INIT_PRICE_ANTIVIRUS * Math.pow(1-HIGH_TECH_ASIA_PRICE_DECREASE, numTimesThresholdReached)));
				
		// calculate resource consumption
		int resourceConsumption = 0;
		for (int i = 0; i < numTeams; i++) {
			for(int attack = 0; attack < RoundChoices.NUM_ATTACKS_PER_TEAM; attack++) {
				resourceConsumption += choicesFromGroups[i].attackNumberZwaard[attack] * NUM_RESOURCES_PER_ZWAARD;
				resourceConsumption += choicesFromGroups[i].attackNumberRaket[attack] * NUM_RESOURCES_PER_RAKET;
				resourceConsumption += choicesFromGroups[i].attackNumberVirus[attack] * NUM_RESOURCES_PER_VIRUS;
			}
			resourceConsumption += choicesFromGroups[i].defenseNumberNewSchilden * NUM_RESOURCES_PER_SCHILD;
			resourceConsumption += choicesFromGroups[i].defenseNumberNewRaketschilden * NUM_RESOURCES_PER_RAKETSCHILD;
			resourceConsumption += choicesFromGroups[i].defenseNumberNewAntivirus * NUM_RESOURCES_PER_ANTIVIRUS;
			resourceConsumption += choicesFromGroups[i].purchaseBouwstenen * NUM_RESOURCES_PER_BOUWSTEEN;
		}
		nextState.resourcesAtStartOfLastRound = latestState.resourcesAvailableInCurrentRound;
		nextState.resourcesUsedInLastRound = resourceConsumption;
		int remainingResources = latestState.resourcesAvailableInCurrentRound - resourceConsumption;
		double naturalGrowth = norma.density(remainingResources);
		double meanNaturalGrowth = norma.density(MEAN_REGENERATE);
		nextState.resourcesNaturalGrowthInLastRound = 
				(int) Math.round(MAX_REGENERATE_PER_ROUND * naturalGrowth / meanNaturalGrowth);
		nextState.resourcesDiscoveredInLastRound = discovered;
		nextState.resourcesAvailableInCurrentRound = Math.max(0, 
				nextState.resourcesAtStartOfLastRound - 
				nextState.resourcesUsedInLastRound +
				nextState.resourcesNaturalGrowthInLastRound +
				nextState.resourcesFromEcoScienceInLastRound -
				nextState.resourcesToCountryInvestmentTrustInLastRound +
				nextState.resourcesDiscoveredInLastRound);
		
		// update resource prices depending on the resource scarcity
		IntIntMap newPrices = nextState.prices;
		
		if(nextState.resourcesAvailableInCurrentRound == 0)
			nextState.priceFactor = RESOURCE_THRESHOLD_FACTOR * (double)INIT_RESOURCES;
		else if(nextState.resourcesAvailableInCurrentRound < (RESOURCE_THRESHOLD_FACTOR * INIT_RESOURCES))
			nextState.priceFactor = 
					(RESOURCE_THRESHOLD_FACTOR * (double)INIT_RESOURCES) / 
					(double)nextState.resourcesAvailableInCurrentRound;
		else
			nextState.priceFactor = 1;

		nextState.relativePriceChange = (nextState.priceFactor - latestState.priceFactor) / latestState.priceFactor;
		
		newPrices.put(GameState.ZWAARD, (int) Math.round(nextState.basePriceZwaard * nextState.priceFactor));
		newPrices.put(GameState.RAKET, (int) Math.round(nextState.basePriceRaket * nextState.priceFactor));
		newPrices.put(GameState.VIRUS, (int) Math.round(nextState.basePriceVirus * nextState.priceFactor));
		newPrices.put(GameState.SCHILD, (int) Math.round(nextState.basePriceSchild * nextState.priceFactor));
		newPrices.put(GameState.RAKETSCHILD, (int) Math.round(nextState.basePriceRaketSchild * nextState.priceFactor));
		newPrices.put(GameState.ANTIVIRUS, (int) Math.round(nextState.basePriceAntivirus * nextState.priceFactor));
		newPrices.put(GameState.BOUWSTEEN, (int) Math.round(GameState.INIT_PRICE_BOUWSTEEN * nextState.priceFactor));
		newPrices.put(GameState.PATENT_RAKET, (int) Math.round(GameState.INIT_PRICE_PATENT_RAKET * nextState.priceFactor));
		newPrices.put(GameState.PATENT_VIRUS, (int) Math.round(GameState.INIT_PRICE_PATENT_VIRUS * nextState.priceFactor));
		newPrices.put(GameState.PATENT_RAKETSCHILD, (int) Math.round(GameState.INIT_PRICE_PATENT_RAKETSCHILD * nextState.priceFactor));
		newPrices.put(GameState.PATENT_ANTIVIRUS, (int) Math.round(GameState.INIT_PRICE_PATENT_ANTIVIRUS * nextState.priceFactor));

		// execute disasters
		for (int i = 0; i < numTeams; ++i){
			if (terrorismTargets.contains(i)){
				nextState.towerHeightAfterLastRound.put(i, 0);
			}
			if (earthquakeDamage > 0){
				nextState.towerHeightAfterLastRound.put(i, Math.max(0, nextState.towerHeightAfterLastRound.get(i, -1) - earthquakeDamage));
			}
		}
		
		// housekeeping (prepare for the next round)
		discovered = 0;
		terrorismTargets.clear();
		earthquakeDamage = 0;
		mostRecentStateIndex += 1;
		previousStates.add(nextState);
		previousChoices.add(choicesFromGroups);
		return nextState;
	}

	/**
	 * Tests the simulator without GUI or server-client communication Recommend
	 * to run in debug mode and use breakpoints
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final int facilitatorCount = 8;
		GameState latestState = new GameState();
		latestState.prepareFirst(facilitatorCount); 
		latestState.initResources(INIT_RESOURCES);
		Simulator testSim = new Simulator(latestState);
		RoundChoices[] choices = new RoundChoices[facilitatorCount];

		// START ROUND 1

		for (int j = 0; j < facilitatorCount; ++j) {
			choices[j] = new RoundChoices();
			choices[j].groupID = j;
			choices[j].groupName = "group " + j;
		}

		choices[0].attackOnTeam[0] = 1;
		choices[1].attackOnTeam[0] = 2;
		choices[2].attackOnTeam[0] = 0;
		choices[0].attackNumberZwaard[0] = 1;
		choices[1].attackNumberZwaard[0] = 2;
		choices[2].attackNumberZwaard[0] = 3;
		choices[0].purchaseBouwstenen = 1;
		choices[1].purchaseBouwstenen = 2;
		choices[2].purchaseBouwstenen = 3;

		// simulate rounds
		for(int i = 0; i < 1; i++) {
			GameState nextState = testSim.simulate(choices);
			System.out.println("battle:        " + nextState.battleResultsFromLastRound);
			System.out.println("schilden:      " + nextState.numShieldsBeforeAttack);
			System.out.println("raketschilden: " + nextState.numRocketShieldsBeforeAttack);
			System.out.println("antivirussen:  " + nextState.numAntiVirusesBeforeAttack);
			System.out.println("towers:        " + nextState.towerHeightAfterLastRound);
			System.out.println("conquered[0]:  " + nextState.conqueredBouwstenen.get(0, 0));
			System.out.println("conquered[1]:  " + nextState.conqueredBouwstenen.get(1, 0));
			System.out.println("conquered[2]:  " + nextState.conqueredBouwstenen.get(2, 0));
			System.out.println("lost[0]:       " + nextState.lostBouwstenen.get(0, 0));
			System.out.println("lost[1]:       " + nextState.lostBouwstenen.get(1, 0));
			System.out.println("lost[2]:       " + nextState.lostBouwstenen.get(2, 0));
			System.out.println("old res level: " + latestState.resourcesAvailableInCurrentRound);
			System.out.println("res used:      " + nextState.resourcesUsedInLastRound);
			System.out.println("res regen:     " + nextState.resourcesNaturalGrowthInLastRound);
			System.out.println("res disc:      " + nextState.resourcesDiscoveredInLastRound);
			System.out.println("new res level: " + nextState.resourcesAvailableInCurrentRound); 
		}

		GameState state = latestState;
		String filename = "state" + state.currentRound + ".json";
		//writeFile(filename, json.prettyPrint(state));

		Json json = new Json();

		JsonValue.PrettyPrintSettings settings = new JsonValue.PrettyPrintSettings();
		settings.outputType = JsonWriter.OutputType.json;

		System.out.println(json.prettyPrint(state, settings));
	}

	private void writeFile(String filename, String text) {

	}


}
