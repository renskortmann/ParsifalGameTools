package parsifalgame.state;

public class RoundChoices {
	
	public static final int NUM_ATTACKS_PER_TEAM = 2;

	public int groupID = -1;
	public String groupName;
	public boolean hacked = false;

	public int[] attackOnTeam = new int[NUM_ATTACKS_PER_TEAM];
	public int[] attackNumberZwaard = new int[NUM_ATTACKS_PER_TEAM];
	public int[] attackNumberRaket = new int[NUM_ATTACKS_PER_TEAM];
	public int[] attackNumberVirus = new int[NUM_ATTACKS_PER_TEAM];
	
	public int defenseNumberNewSchilden = 0;
	public int defenseNumberNewRaketschilden = 0;
	public int defenseNumberNewAntivirus = 0;
	
	public int purchaseBouwstenen = 0;
	
	public boolean patentRaket = false;
	public boolean patentRaketschild = false;
	public boolean patentVirus = false;
	public boolean patentAntivirus = false;
	
	public int ecoScienceInvestment = 0;
	public int countryInvestmentTrustInvestment = 0;
	public int highTechAsiaInvestment = 0;
	
	public RoundChoices() {
		for(int i = 0; i < NUM_ATTACKS_PER_TEAM; i++)
			attackOnTeam[i] = -1;
	}
	
}
