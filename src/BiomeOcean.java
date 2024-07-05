public class BiomeOcean extends Biomes{
	
	// constructor
	public BiomeOcean(int biomeID){
		super(biomeID);
	}
	
	// redefinition des methodes abstract (mère)

	// spread repand le biome et les textures ground, trees, rocks, bushes
	public void spreadBiome(int xinit, int yinit, int dx, int dy, Cell[][] currentWorld, int reste){
		this.nbCells = reste;
		for (int x = 0; x < dx; x++)
			for (int y = 0; y < dy; y++)
				if (currentWorld[x][y].getBiomeID() == 0){
					currentWorld[x][y].setBiomeID(1);
					currentWorld[x][y].setGroundImage(1);
				}
	}	

	public void respawnTrees(World w, int x, int y, int iteration, int freq){}
	public void respawnBushes(World w, int x, int y, int iteration, int freq){}
}
