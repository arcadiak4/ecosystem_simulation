public class BiomeTemperate extends Biomes{

	public static double TREE_DENSITY = 0.9;		// 0.0 densité faible à 1.0 densité forte
	public static double BUSH_DENSITY = 0.3;		// 0.0 densité faible à 1.0 densité forte
	public static double ROCK_DENSITY = 0.3;		// 0.0 densité faible à 1.0 densité forte
	public static int TREE_REGROWTH_RATE = 2;		// 1:rapide, 2,3,...: +en+ lent
	public static int BUSH_REGROWTH_RATE = 3;		// 1:rapide, 2,3,...: +en+ lent
	
	// constructor
	// les arguments: int biomeID, int nbCells, double treeDensity, double rockDensity, double bushDensity
	// double treeRegrowthRate, double bushRegrowthRate
	public BiomeTemperate(int biomeID){
		super(biomeID,0 , TREE_DENSITY, ROCK_DENSITY, BUSH_DENSITY, TREE_REGROWTH_RATE, BUSH_REGROWTH_RATE);
	}

	// redefinition des methodes abstract (mère)
	
	// spread repand le biome et les textures ground, trees, rocks, bushes
	public void spreadBiome(int xinit, int yinit, int dx, int dy, Cell[][] currentWorld, int reste) throws FailSpreadException
	{
		this.nbCells = reste;
		currentWorld[xinit][yinit].setBiomeID(2);
		currentWorld[xinit][yinit].setGroundImage(10);
		reste--;
		int xtemp, ytemp;
		int stuck=0;
			
		while(reste > 0 && stuck < 50)
		{
			stuck++;
			for (int x = 0; x < dx; x++)
			{
				for (int y = 0; y < dy; y++)
				{
					if (reste<=0) break;
					if (reste>0 && isAlone(x, y, dx, dy, currentWorld) == this.biomeID)
					{
						currentWorld[x][y].setBiomeID(2);
						currentWorld[x][y].setGroundImage(10);
						reste--; stuck=0;
						switch ( (int)(Math.random()*3)  ){
							case 0:		// generate a tree
								if (Math.random()<treeDensity) currentWorld[x][y].setWorldObjectImage(11);
								break;
							case 1:		// generate a bush
								if (Math.random()<bushDensity) currentWorld[x][y].setWorldObjectImage(12);
								break;
							case 2:		// generate a rock
								if (Math.random()<rockDensity) currentWorld[x][y].setWorldObjectImage(13);
								break;						
						}
					}
					else if (currentWorld[x][y].getBiomeID() == this.biomeID)
					{
						switch ( (int)(Math.random()*4) ){

						// Nord
						case 0:
							if (reste>0 && isEmpty(x,(y-1+dy)%dy, currentWorld))
							{
								ytemp = (y-1+dy)%dy;
								currentWorld[x][ytemp].setBiomeID(2);
								currentWorld[x][ytemp].setGroundImage(10);
								reste--; stuck=0;
								switch ( (int)(Math.random()*3)  ){
									case 0:		// generate a tree
										if (Math.random()<treeDensity) currentWorld[x][ytemp].setWorldObjectImage(11);
										break;
									case 1:		// generate a bush
										if (Math.random()<bushDensity) currentWorld[x][ytemp].setWorldObjectImage(12);
										break;
									case 2:		// generate a rock
										if (Math.random()<rockDensity) currentWorld[x][ytemp].setWorldObjectImage(13);
										break;						
								}
							} break;

						// Est
						case 1:
							if (reste>0 && isEmpty((x+1+dx)%dx,y, currentWorld))
							{
								xtemp = (x+1+dx)%dx;
								currentWorld[xtemp][y].setBiomeID(2);
								currentWorld[xtemp][y].setGroundImage(10);
								reste--; stuck=0;
								switch ( (int)(Math.random()*3)  ){
									case 0:		// generate a tree
										if (Math.random()<treeDensity) currentWorld[xtemp][y].setWorldObjectImage(11);
										break;
									case 1:		// generate a bush
										if (Math.random()<bushDensity) currentWorld[xtemp][y].setWorldObjectImage(12);
										break;
									case 2:		// generate a rock
										if (Math.random()<rockDensity) currentWorld[xtemp][y].setWorldObjectImage(13);
										break;						
								}
							} break;

						// Sud
						case 2:
							if (reste>0 && isEmpty(x,(y+1+dy)%dy, currentWorld))
							{
								ytemp = (y+1+dy)%dy;
								currentWorld[x][ytemp].setBiomeID(2);
								currentWorld[x][ytemp].setGroundImage(10);
								reste--; stuck=0;
								switch ( (int)(Math.random()*3)  ){
									case 0:		// generate a tree
										if (Math.random()<treeDensity) currentWorld[x][ytemp].setWorldObjectImage(11);
										break;
									case 1:		// generate a bush
										if (Math.random()<bushDensity) currentWorld[x][ytemp].setWorldObjectImage(12);
										break;
									case 2:		// generate a rock
										if (Math.random()<rockDensity) currentWorld[x][ytemp].setWorldObjectImage(13);
										break;						
								}
							} break;

						// Ouest
						case 3:
							if (reste>=1 && isEmpty((x-1+dx)%dx,y, currentWorld))
							{
								xtemp = (x-1+dx)%dx;
								currentWorld[xtemp][y].setBiomeID(2);
								currentWorld[xtemp][y].setGroundImage(10);
								reste--; stuck=0;
								switch ( (int)(Math.random()*3)  ){
									case 0:		// generate a tree
										if (Math.random()<treeDensity) currentWorld[xtemp][y].setWorldObjectImage(11);
										break;
									case 1:		// generate a bush
										if (Math.random()<bushDensity) currentWorld[xtemp][y].setWorldObjectImage(12);
										break;
									case 2:		// generate a rock
										if (Math.random()<rockDensity) currentWorld[xtemp][y].setWorldObjectImage(13);
										break;						
								}
							} break;
						}	
					}
				}
			}
		}
		if (stuck > 0){
			for (int x = 0; x < dx; x++)
			{
				for (int y = 0; y < dy; y++)
				{
					if (currentWorld[x][y].getBiomeID() == this.biomeID){
						currentWorld[x][y].setBiomeID(0);
						currentWorld[x][y].setGroundImage(0);
						currentWorld[x][y].setWorldObjectImage(0);
					}
				}
			}
		throw new FailSpreadException("BiomeTemperate");
		}
	}

	public void respawnTrees(World w, int x, int y, int iteration, int freq){
		if (iteration % (treeRegrowthRate*freq) == 0)
			if (Math.random() < treeDensity) w.getCell(x,y).setWorldObjectImage(11);
	}
	public void respawnBushes(World w, int x, int y, int iteration, int freq){
		if (iteration % (bushRegrowthRate*freq) == 0)
			if (Math.random() < bushDensity) w.getCell(x,y).setWorldObjectImage(12);
	}
	
}
