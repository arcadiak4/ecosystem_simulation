public abstract class Biomes{
	
	/* 0 = case vierge
	 * 1 = ocean
	 * 2 = temperate
	 * 3 = desert
	 * 4 = mountain */
	protected int biomeID;
	protected int nbCells;
	protected boolean specialEvent;
	
	// attributs paramètres
	protected double treeDensity;
	protected double rockDensity;
	protected double bushDensity;
	protected double treeRegrowthRate;
	protected double bushRegrowthRate;
	
	// constructeur
	public Biomes(int biomeID, int nbCells, double treeDensity, double rockDensity, double bushDensity,
	double treeRegrowthRate, double bushRegrowthRate){
		this.nbCells = nbCells;
		this.biomeID = biomeID;
		this.treeDensity = treeDensity;
		this.rockDensity = rockDensity;
		this.bushDensity = bushDensity;
		this.treeRegrowthRate = treeRegrowthRate;
		this.bushRegrowthRate = bushRegrowthRate;
	}
	
	public Biomes(int biomeID){
		this.biomeID = biomeID;
		this.treeDensity = 0;
		this.rockDensity = 0;
		this.bushDensity = 0;
		this.treeRegrowthRate = 0;
		this.bushRegrowthRate = 0;
	}
	
	// methodes set
	public void setBiomeParameters(double treeDensity, double rockDensity, double bushDensity,
	double treeRegrowthRate, double bushRegrowthRate){
		this.treeDensity = treeDensity;
		this.rockDensity = rockDensity;
		this.bushDensity = bushDensity;
		this.treeRegrowthRate = treeRegrowthRate;
		this.bushRegrowthRate = bushRegrowthRate;
	}
	
	public void setBiomeID(int value) {biomeID = value;}
	public void setTreeDensity(double value) {treeDensity = value;}
	public void setRockDensity(double value) {rockDensity = value;}
	public void setBushDensity(double value) {bushDensity = value;}
	public void setTreeRegrowthRate(double value) {treeRegrowthRate = value;}
	public void setBushRegrowthRate(double value) {bushRegrowthRate = value;}

	// methodes get
	public int getNbCells() {return nbCells;}
	public int getBiomeID() {return biomeID;}
	public boolean getSpecialEvent() {return specialEvent;}
	
	public double getTreeDensity() {return treeDensity;}
	public double getRockDensity() {return rockDensity;}
	public double getBushDensity() {return bushDensity;}
	public double getTreeRegrowthRate() {return treeRegrowthRate;}
	public double getBushRegrowthRate() {return bushRegrowthRate;}

	// methods non abstract

	// fonction utile de spreadBiome, verif si case vierge
	public boolean isEmpty(int x, int y, Cell[][] currentWorld){
		return currentWorld[x][y].getBiomeID() == 0;
	}

	// verifie si les cells voisinnes (neumann) sont du même biome
	// if true then biomeID else 0
	public int isAlone(int x, int y, int dx, int dy, Cell[][] currentWorld){
		int val;
		if (currentWorld[x][y].getBiomeID() == 0){
			// val = north
			val = currentWorld[x][(y-1+dy)%dy].getBiomeID();
			if (val == currentWorld[(x+1+dx)%dx][y].getBiomeID() &&
				val == currentWorld[x][(y+1+dy)%dy].getBiomeID() &&
				val == currentWorld[(x-1+dx)%dx][y].getBiomeID())
					return val;
			return 0;
		}
		return 0;
	}
	
	// methodes abstract (pour classes filles)
	protected abstract void spreadBiome(int x, int y, int dx, int dy, Cell[][] currentWorld, int nbCells) throws FailSpreadException;
	protected abstract void respawnTrees(World w, int x, int y, int iteration, int freq);
	protected abstract void respawnBushes(World w, int x, int y, int iteration, int freq);
	
}
