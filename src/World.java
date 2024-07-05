import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage; 
import java.awt.image.ImageObserver;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

import java.awt.Toolkit;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class World extends JPanel {



	/* ====================================== */
	/*	        PARAMETRES DU MONDE          */
	/* ====================================== */
	
	
	
	// NE PAS OUBLIER : de make clean quand on change SPRITE_LENGTH
	public static int SPRITE_LENGTH = 32;
	public static double BIOME_SIZE_EQUALITY_RATE = 0.3;	// 0: forte variabilité des surface 1: biome identiques
	public static double P_FIREFOX = 0.1;
	public static double P_ICEWEASEL = 0.1;



	/* ====================================== */
	/*	             ATTRIBUTS               */
	/* ====================================== */



	// Environnement
	private JFrame frame;
	private Cell[][] currentWorld;
	public int[][] treeStatus;
	private final int dx;
	private final int dy;
	private int nbCellsTotal;
	private BiomeOcean biomeOcean;
	private BiomeTemperate biomeTemperate;
	private BiomeDesert biomeDesert;
	private BiomeMountain biomeMountain;
	
	private int color;
	private BufferedImage waterSprite = Sprite.loadSprite("src_sprites/water.png");

	private BufferedImage grassSprite = Sprite.loadSprite("src_sprites/grass.png");
	private BufferedImage temperateBushSprite = Sprite.loadSprite("src_sprites/temperateBush.png");
	private BufferedImage temperateRockSprite = Sprite.loadSprite("src_sprites/temperateRock.png");

	private BufferedImage sandSprite = Sprite.loadSprite("src_sprites/sand.png");
	private BufferedImage desertBushSprite = Sprite.loadSprite("src_sprites/desertBush.png");
	private BufferedImage desertRockSprite = Sprite.loadSprite("src_sprites/desertRock.png");
	
	private BufferedImage snowSprite = Sprite.loadSprite("src_sprites/snow.png");
	private BufferedImage mountainBushSprite = Sprite.loadSprite("src_sprites/mountainBush.png");
	private BufferedImage mountainRockSprite = Sprite.loadSprite("src_sprites/mountainRock.png");
	
	private BufferedImage temperateTreeSpriteSheet  = Sprite.loadSprite("src_sprites/temperateTreeSpriteSheet.png");
	private BufferedImage desertTreeSpriteSheet = Sprite.loadSprite("src_sprites/desertTreeSpriteSheet.png");
	private BufferedImage mountainTreeSpriteSheet = Sprite.loadSprite("src_sprites/mountainTreeSpriteSheet.png");
	private BufferedImage[] treeSprite = { temperateTreeSpriteSheet.getSubimage(0*49, 0, 49, 105),
								    temperateTreeSpriteSheet.getSubimage(1*49, 0, 49, 105),
								    temperateTreeSpriteSheet.getSubimage(2*49, 0, 49, 105),
								    temperateTreeSpriteSheet.getSubimage(3*49, 0, 49, 105),
								    temperateTreeSpriteSheet.getSubimage(4*49, 0, 49, 105),
								    temperateTreeSpriteSheet.getSubimage(5*49, 0, 49, 105),
								    temperateTreeSpriteSheet.getSubimage(6*49, 0, 49, 105),
								    temperateTreeSpriteSheet.getSubimage(7*49, 0, 49, 105),
								    
								    desertTreeSpriteSheet.getSubimage(0*90, 0, 90, 105),
								    desertTreeSpriteSheet.getSubimage(1*90, 0, 90, 105),
								    desertTreeSpriteSheet.getSubimage(2*90, 0, 90, 105),
								    desertTreeSpriteSheet.getSubimage(3*90, 0, 90, 105),
								    
								    mountainTreeSpriteSheet.getSubimage(0*67, 0, 67, 107),
								    mountainTreeSpriteSheet.getSubimage(1*67, 0, 67, 107),
								    mountainTreeSpriteSheet.getSubimage(2*67, 0, 67, 107),
								    mountainTreeSpriteSheet.getSubimage(3*67, 0, 67, 107)
								    };
	
	// Agents 
	private List<Agents> agentsList;
	private BufferedImage mating = Sprite.loadSprite("src_sprites/mating.png");

/* ID images
*
* [0-9] Autres
* 0 null; 1 water(ocean); 2 water(lake); 3 empty(lake);
* 
* [10-19] biome Temperate
* 10 herbe; 11 arbre; 12 temperateBush; 13 temperateRock;
* 
* [20-29] biome Desert
* 20 sable; 21 cactus; 22 desertBush; 23 desertRock;
* [30-39] biome Mountain
* 30 neige; 31 sapin(sans_neige); 32 sapin(avec neige); 33 moutainBush; 34 mountainRock;
* 
* [40-49] Agents
* 40 babyfox; 41 firefox; 42 iceweasel;
* 
* [50-59] Meteo
* 50 pluie; 51 neige;
* 
*/



	/* ====================================== */
	/*	           CONSTRUCTEURS              */
	/* ====================================== */
	
	
	
	public World (int dx, int dy)
	{
	
		this.dx = dx; 
		this.dy = dy;
		this.nbCellsTotal = dx * dy; 
		currentWorld = new Cell[dx][dy];
		treeStatus = new int[dx][dy];
		
		// initialisation des biomes
		biomeOcean = new BiomeOcean(1);
		biomeTemperate = new BiomeTemperate(2);
		biomeDesert = new BiomeDesert(3);
		biomeMountain = new BiomeMountain(4);
		
		for (int x = 0; x < dx; x++){
			for (int y = 0; y < dy; y++){
				currentWorld[x][y] = new Cell();
				currentWorld[x][y].setX(x);
				currentWorld[x][y].setY(y);
				currentWorld[x][y].setAgent(-1);
			}
		}
		
		// liste d'agents
		agentsList = new CopyOnWriteArrayList<Agents>();
	}
	

	/* ====================================== */
	/*	      	    GETTERS                */
	/* ====================================== */
	
	
	
	public int getDx() { return dx; }
	public int getDy() { return dy; }
	public Cell[][] getCurrentWorld() { return currentWorld; }
	public Cell getCell(int x, int y) { return currentWorld[x][y]; }
	public List<Agents> getAgentsList() { return agentsList; }


	
	/* ====================================== */
	/*	    CREATION DE L'ENVIRONNEMENT      */
	/* ====================================== */
	
	
	
	/* initialisation des biomes
	 0. modulable si l'on souhaite ajouter/retirer des biomes
	 1. genere un nombre de cells
	 2. genere un x,y aleatoire
	 3. prend un biome random et appel son spread */
	public void generateBiomes() 
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		int xTemp, yTemp, cellsRemain, baseCells, bonusCells;
		boolean approval = true;	//false si le dernier biome (avant ocean) n'a pas assez de place pour se spread
		int k=0;
		// modifier ici si +/- biomes
		list.add(2);	// ajout de Temperate
		list.add(3);	// ajout de Desert
		list.add(4);	// ajout de Mountain
		// ocean est genere en dernier
		
		cellsRemain = nbCellsTotal;
		// chaque biome à au moins une demi-part de territoire
		baseCells = (nbCellsTotal/((list.size()+1)*2))-1;
		//System.out.println("Attribution de baseCells : "+baseCells);
		while (list.isEmpty() == false)
		{
			if (k > 100) approval = false;
			if (k > 200){
				list.clear();
				break;
			}
			k++;
			// déterminer le nb de cells à affecter au futur biome
			//System.out.println("Entrée dans while (liste) cellsRemain : " + cellsRemain);
			// BONUS 1 grossisement
			bonusCells = 0;
			if (Math.random() > BIOME_SIZE_EQUALITY_RATE && (cellsRemain-(baseCells*2)) > 0 && approval)
				bonusCells = baseCells;
				
			// BONUS 2 grossissement avec 2x moins de chance +(Math.random() < ((1-BIOME_SIZE_EQUALITY_RATE)/2))+
			if (Math.random() > (BIOME_SIZE_EQUALITY_RATE) && (cellsRemain-(bonusCells+(2*baseCells))) > 0 && approval)
				bonusCells += baseCells;
			
			// determiner un x,y aleatoire qui n'est pas deja un biome
			try{
				do {
					xTemp = (int)(Math.random()*dx);
					yTemp = (int)(Math.random()*dy);
				} while (currentWorld[xTemp][yTemp].getBiomeID() != 0);
				//System.out.println("xTemp="+xTemp+" yTemp="+yTemp);
				// faire repandre les biomes dans un ordre aleatoire
				// ça commence à 2: le premier biome
				switch (list.get((int)(Math.random()*list.size()))) {
					case 2:
						//System.out.println("Choix biomeTemperate : "+baseCells+" "+bonusCells);
						biomeTemperate.spreadBiome(xTemp, yTemp, dx, dy, currentWorld, baseCells+bonusCells);
						list.remove(Integer.valueOf(2));
						cellsRemain -= (baseCells + bonusCells);
						break;
					case 3:
						//System.out.println("Choix biomeDesert : "+baseCells+" "+bonusCells);
						biomeDesert.spreadBiome(xTemp, yTemp, dx, dy, currentWorld, baseCells+bonusCells);
						list.remove(Integer.valueOf(3));
						cellsRemain -= (baseCells + bonusCells);
						break;
					case 4:
						//System.out.println("Choix biomeMoutain : "+baseCells+" "+bonusCells);
						biomeMountain.spreadBiome(xTemp, yTemp, dx, dy, currentWorld, baseCells+bonusCells);
						list.remove(Integer.valueOf(4));
						cellsRemain -= (baseCells + bonusCells);
						break;
				}
				//System.out.println("fin de while(list) cellsRemain : "+cellsRemain);
			}catch (FailSpreadException e){
				//System.out.println("Exception thrown  : " + e.getMessage());
			}
		}
		
		// generation du dernier biome Ocean
		//System.out.println("Distribution OCEAN cellsRemain: "+cellsRemain);
		biomeOcean.spreadBiome(-1, -1, dx, dy, currentWorld, cellsRemain);
		
		
		// initialisation de l'état des arbres
		for (int x = 0; x < dx; x++)
		{
			for (int y = 0; y < dy; y++)
			{
				// SI c'est un arbre
				if (currentWorld[x][y].getWorldObjectImage() == 11 || currentWorld[x][y].getWorldObjectImage() == 21 || currentWorld[x][y].getWorldObjectImage() == 31)
					// état initial de l'arbre : générer un chiffre aléatoire entre 1 et 7
					treeStatus[x][y] = (int)((Math.random()*6)+1);
				// SINON
				else 
					treeStatus[x][y] = 0;
			}
		}
	}


	
	/* ====================================== */
	/*	        CREATION DES AGENTS          */
	/* ====================================== */


	
	public void generateAgents()
	{
		for (int x = 0; x < dx; x++)
			for (int y = 0; y < dy; y++) {
				// créer un firefox si la case est un desert et qu'il n'y a pas de rochers
				if (currentWorld[x][y].getBiomeID() == 3 && currentWorld[x][y].getWorldObjectImage() != 13 && Math.random() < P_FIREFOX) {
					agentsList.add(new Firefox(x, y));
					currentWorld[x][y].setAgent(0);
				}
				// créer un iceweasel si la case est une montagne et qu'il n'y a pas de rochers
				if (currentWorld[x][y].getBiomeID() == 4 && currentWorld[x][y].getWorldObjectImage() != 34 && Math.random() < P_ICEWEASEL) {
					agentsList.add(new Iceweasel(x, y));
					currentWorld[x][y].setAgent(1);
				}
			}
	}


	
	/* ==================================== */
	/*			STEP_WORLD		     */
	/* ==================================== */


	
	public void stepWorld(int iteration, int freq_world)
	{		
		// mise a jour sur l'état des arbres et feu de forêt
		treesEvolution();
		
		// repousse des buissons et des arbres
		for (int x = 0 ; x < dx; x++)
		{
			for (int y = 0; y < dy ; y++)
			{
				if (currentWorld[x][y].getBiomeID() > 1 && currentWorld[x][y].getWorldObjectImage() == 0)
				{
					switch (currentWorld[x][y].getBiomeID())
					{
						case 2:
							if (Math.random() < 0.5) biomeTemperate.respawnTrees(this, x, y, iteration, freq_world);
							else biomeTemperate.respawnBushes(this, x, y, iteration, freq_world);
							break;

						case 3:
							if (Math.random() < 0.5) biomeDesert.respawnTrees(this, x, y, iteration, freq_world);
							else biomeDesert.respawnBushes(this, x, y, iteration, freq_world);
							break;

						case 4:
							if (Math.random() < 0.5) biomeMountain.respawnTrees(this, x, y, iteration, freq_world);
							else biomeMountain.respawnBushes(this, x, y, iteration, freq_world);
							break;
					}
				}
			}
		}
	}



	/* ==================================== */
	/*			STEP_AGENTS	      	*/
	/* ==================================== */
	
	
	
	public void stepAgents(int iteration, int FREQ_AGENT_MOVE, int FREQ_AGENT_ACTION, int FREQ_AGENT_ANIM)
	{
		for (Agents agent : agentsList) {	
			agent.updateStatus(this, iteration);
			if (agent.getDead()) removeAgent(agent);
			agent.nextTask(this);
			agent.move(this, iteration, FREQ_AGENT_MOVE, FREQ_AGENT_ACTION, FREQ_AGENT_ANIM);
			agent.action(this, iteration, FREQ_AGENT_ACTION, FREQ_AGENT_ANIM);
			reproduction(agent, agentsList);
		}
	
	}
	
	
	
	/* ==================================== */
	/*		   ETATS DES ARBRES	      	*/
	/* ==================================== */
	
	
	
	//forestFire est un tableau contenant l'état des arbres (jeune pousse, normal, burning, mort, cendre)
	// entre 1 et 6 = en train de pousser
	// entre 7 et 9 = en train de brûler
	// 10 = mort
	public void treesEvolution() {
		int[][] newTreeStatus = new int[dx][dy];
		int burningTree = 0;
		
		for (int x = 0 ; x < dx ; x ++) {
			for (int y = 0 ; y < dy ; y ++) {			
				
				// l'arbre pousse (1-6) tant qu'il n'a pas atteint son stade normal (6)
				if (treeStatus[x][y] > 0 && treeStatus[x][y] < 6) {
					newTreeStatus[x][y] = ++treeStatus[x][y];
				}
				
				// l'arbre continue de brûler (7-9) si un firefox a déclenché son feu (7)
				else if (treeStatus[x][y] >= 7 && treeStatus[x][y] < 9) {
					newTreeStatus[x][y] = ++treeStatus[x][y];
				}
				
				// SI c'est un arbre au stade normal 
				else if (treeStatus[x][y] == 5 || treeStatus[x][y] == 6) {
					// REGLES FEU DE FORET : SI l'arbre a au moins 1 un arbre en feu (7-9) en voisin, il le devient aussi (7) 
					// haut
					if (treeStatus[x][((y-1)+dy)%dy] >= 7 && treeStatus[((x-1)+dx)%dx][y] <= 9) newTreeStatus[x][y] = 7;
					// droite
					else if (treeStatus[((x+1)+dx)%dx][y] >= 7 && treeStatus[((x-1)+dx)%dx][y] <= 9) newTreeStatus[x][y] = 7;				
					// bas
					else if (treeStatus[x][((y+1)+dy)%dy] >= 7 && treeStatus[((x-1)+dx)%dx][y] <= 9) newTreeStatus[x][y] = 7;
					// gauche
					else if (treeStatus[((x-1)+dx)%dx][y] >= 7 && treeStatus[((x-1)+dx)%dx][y] <= 9) newTreeStatus[x][y] = 7;

					// SINON il reste tel quel
					else newTreeStatus[x][y] = treeStatus[x][y];
				}
				
				// SI l'arbre a fini de brûler (9), il devient mort (10)
				else if (treeStatus[x][y] == 9) {
					newTreeStatus[x][y] = 10;
				}
				
				// SI l'arbre est mort (10), il disparait (0)
				else if (treeStatus[x][y] == 10) {
					newTreeStatus[x][y] = 0;
					currentWorld[x][y].setWorldObjectImage(0);
				}
								
				// SI un nouvel arbre spawn, il apparait comme jeune pousse (1)
				else if (treeStatus[x][y] == 0 && (currentWorld[x][y].getWorldObjectImage() == 11 || currentWorld[x][y].getWorldObjectImage() == 21) || currentWorld[x][y].getWorldObjectImage() == 31) {
					newTreeStatus[x][y] = 1;
				}
			}
		}
		
		// mise a jour du tableau d'arbres
		for (int x = 0 ; x < dx ; x ++) {
			for (int y = 0 ; y < dy ; y ++) {	
				treeStatus[x][y] = newTreeStatus[x][y];
			}
		}
		
		newTreeStatus = null;
		
	}
	
	
	
	/* ==================================== */
	/*		      REPRODUCTION		     */
	/* ==================================== */
	
	
	
	public void reproduction (Agents a1, List<Agents> agentsList) 
	{
		
		// SI bébé (il ne peut se reproduire)
		if (!a1.getAdult()) return;
		// SI mâle (ne peut porter de petit)
		if (!a1.getIsFemale()) return;
		// SI la femelle porte déjà un petit
		if (a1.getPregnancyDuration() != 0) return;
		
		
		// SI l'agent femelle est un Firefox
		if (a1 instanceof Firefox) {
			// SI il n'est pas dans son biome desert
			if (currentWorld[a1.getX()][a1.getY()].getBiomeID() != 3) {
				return;
			}
		}
		
		// SI l'agent femelle est un Iceweasel
		if (a1 instanceof Iceweasel) {
			// SI il n'est pas dans son biome montagne
			if (currentWorld[a1.getX()][a1.getY()].getBiomeID() != 4) {
				return;
			}
		}
		
		// SI adulte
		int nbAgent = 0;
		for (Agents a2 : agentsList) {
			// SI c'est le même agent
			if (a1 == a2) continue;
			
			// SI ils n'ont pas le même type d'agent
			if (!a1.getType().equals(a2.getType())) continue;
			
			// on compte le nombre mâle dans son voisinage
			// haut
			if (a1.getX() == a2.getX() && (a1.getY()-1+dy) % dy == a2.getY() && !a2.getIsFemale()) nbAgent++;
			// droite
			if ((a1.getX()+1+dx) % dx == a2.getX() && a1.getY() == a2.getY() && !a2.getIsFemale()) nbAgent++;
			// bas 
			if (a1.getX() == a2.getX() && (a1.getY()+1+dy) % dy == a2.getY() && !a2.getIsFemale()) nbAgent++;
			// gauche
			if ((a1.getX()-1+dx) % dx == a2.getX() && a1.getY() == a2.getY() && !a2.getIsFemale()) nbAgent++;
		}
			
		// SI l'agent femelle peut avoir une portée
		if (a1.getPregnancyDuration() == 0)
			// REGLES : SI il y a exactement un agent mâle dans son voisinage
			if (nbAgent == 1 && Math.random() < Agents.P_REPRODUCTION) {
				a1.setPregnancyDuration(Agents.PREGNANCY_DURATION);
		}
	}
	
	

	/* ====================================== */
	/*	     SUPPRESSION DES AGENTS          */
	/* ====================================== */
	
	
	
	public void removeAgent (Agents agentToRemove) { 
		// on l'enlève du monde et de la List
		Agents aTmp = null; 
		for (Agents agent : agentsList)
			if (agent.equals(agentToRemove))
				aTmp = agent;
		agentsList.remove(aTmp);
	}
	
	
	
	/* ====================================== */
	/*	      METHODES POUR AFFICHAGE        */
	/* ====================================== */


	
	// affichage de la matrice
	public void showMatrice(boolean bool)
	{
		if (bool)
			for (int y = 0; y < dy; y++) {
				for (int x = 0; x < dx; x++) {
					System.out.print(currentWorld[x][y].getBiomeID()+" ");
				}
			System.out.println();
			}
	}	
		
	// Afficher la fenetre
	public void showWindow(int frameSizeX, int frameSizeY)
	{
		frame = new JFrame("Ecosysteme");
		frame.add(this);
		frame.setSize(frameSizeX,frameSizeY);
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		//frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);	 	
	}

	// affichage des textures
	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;

		for (int x = 0; x < dx; x++) {
			for (int y = 0; y < dy; y++) {
				 
// GroundImage		 
				// water
				if (currentWorld[x][y].getGroundImage() == 1)
					g2.drawImage(waterSprite, SPRITE_LENGTH*x, SPRITE_LENGTH*y, SPRITE_LENGTH, SPRITE_LENGTH, frame);
				// grass
				if (currentWorld[x][y].getGroundImage() == 10)
					g2.drawImage(grassSprite, SPRITE_LENGTH*x, SPRITE_LENGTH*y, SPRITE_LENGTH, SPRITE_LENGTH, frame);
				// sand
				if (currentWorld[x][y].getGroundImage() == 20)
					g2.drawImage(sandSprite, SPRITE_LENGTH*x, SPRITE_LENGTH*y, SPRITE_LENGTH, SPRITE_LENGTH, frame);
				// snow
				if (currentWorld[x][y].getGroundImage() == 30)
					g2.drawImage(snowSprite, SPRITE_LENGTH*x, SPRITE_LENGTH*y, SPRITE_LENGTH, SPRITE_LENGTH, frame);
				
// WorldObjectImage
				// temperateBush
				if (currentWorld[x][y].getWorldObjectImage() == 12)
					g2.drawImage(temperateBushSprite, SPRITE_LENGTH*x, SPRITE_LENGTH*y, SPRITE_LENGTH, SPRITE_LENGTH, frame);
				// temperateRock
				if (currentWorld[x][y].getWorldObjectImage() == 13)
					g2.drawImage(temperateRockSprite, SPRITE_LENGTH*x, SPRITE_LENGTH*y, SPRITE_LENGTH, SPRITE_LENGTH, frame);
					
				// desertBush
				if (currentWorld[x][y].getWorldObjectImage() == 22)
					g2.drawImage(desertBushSprite, SPRITE_LENGTH*x, SPRITE_LENGTH*y, SPRITE_LENGTH, SPRITE_LENGTH, frame);
				// desertRock
				if (currentWorld[x][y].getWorldObjectImage() == 23)
					g2.drawImage(desertRockSprite, SPRITE_LENGTH*x, SPRITE_LENGTH*y, SPRITE_LENGTH, SPRITE_LENGTH, frame);
				
				// mountainBush 
				if (currentWorld[x][y].getWorldObjectImage() == 33)
					g2.drawImage(mountainBushSprite, SPRITE_LENGTH*x, SPRITE_LENGTH*y, SPRITE_LENGTH, SPRITE_LENGTH, frame);
				// mountainRock
				if (currentWorld[x][y].getWorldObjectImage() == 34)
					g2.drawImage(mountainRockSprite, SPRITE_LENGTH*x, SPRITE_LENGTH*y, SPRITE_LENGTH, SPRITE_LENGTH, frame);
			}
		}

// weatherImage
		
// agentImage
		boolean showStats = true;
		boolean showVision = false;
		boolean showEmote = true;
		
		for (Agents agent : agentsList) {
			switch (agent.getOrientation()) {
				case 0:
					drawEmote(g2, agent, showEmote);
					drawVision(g2, agent, showVision);
					g2.drawImage(agent.animation.getSprite(), agent.getX()*SPRITE_LENGTH, (agent.getY()+1)*SPRITE_LENGTH-agent.animation.getFrameShift(), SPRITE_LENGTH, SPRITE_LENGTH, frame);
					drawStats(g2, agent, showStats);
					break;
				case 1:
					drawEmote(g2, agent, showEmote);
					drawVision(g2, agent, showVision);
					g2.drawImage(agent.animation.getSprite(), (agent.getX()-1)*SPRITE_LENGTH+agent.animation.getFrameShift(), agent.getY()*SPRITE_LENGTH, SPRITE_LENGTH, SPRITE_LENGTH, frame);
					drawStats(g2, agent, showStats);
					break;
				case 2:
					drawEmote(g2, agent, showEmote);
					drawVision(g2, agent, showVision);
					g2.drawImage(agent.animation.getSprite(), agent.getX()*SPRITE_LENGTH, (agent.getY()-1)*SPRITE_LENGTH+agent.animation.getFrameShift(), SPRITE_LENGTH, SPRITE_LENGTH, frame); 
					drawStats(g2, agent, showStats);				
					break;
				case 3:
					drawEmote(g2, agent, showEmote);
					drawVision(g2, agent, showVision);
					g2.drawImage(agent.animation.getSprite(), (agent.getX()+1)*SPRITE_LENGTH-agent.animation.getFrameShift(), agent.getY()*SPRITE_LENGTH, SPRITE_LENGTH, SPRITE_LENGTH, frame);
					drawStats(g2, agent, showStats);				
					break;
			}
		}
		
		// Affichage des arbres
		for (int x = 0 ; x < dx ; x++){
			for (int y = 0 ; y < dy ; y++){
				// temperateTree
				if (currentWorld[x][y].getWorldObjectImage() == 11) {
					int width = (int)(SPRITE_LENGTH*1.53);
					int length = (int)(SPRITE_LENGTH*3.3);
					// en train de pousser
					if (treeStatus[x][y] == 1) g2.drawImage(treeSprite[0], x*SPRITE_LENGTH, y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 2) g2.drawImage(treeSprite[1], x*SPRITE_LENGTH, y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 3) g2.drawImage(treeSprite[2], x*SPRITE_LENGTH, y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 4) g2.drawImage(treeSprite[3], x*SPRITE_LENGTH, y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 5) g2.drawImage(treeSprite[4], x*SPRITE_LENGTH, y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 6) g2.drawImage(treeSprite[5], x*SPRITE_LENGTH, y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					// en train de brûler
					else if (treeStatus[x][y] == 7 || treeStatus[x][y] == 8 || treeStatus[x][y] == 9) g2.drawImage(treeSprite[6], x*SPRITE_LENGTH, y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 10) g2.drawImage(treeSprite[7], x*SPRITE_LENGTH, y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
				}
				// desertTree
				if (currentWorld[x][y].getWorldObjectImage() == 21) {
					int width = (int)(SPRITE_LENGTH*2.82);
					int length = (int)(SPRITE_LENGTH*3.3);
					// en train de pousser
					if (treeStatus[x][y] == 1) g2.drawImage(treeSprite[8], (int)(x*SPRITE_LENGTH-SPRITE_LENGTH*0.625), y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 2) g2.drawImage(treeSprite[8], (int)(x*SPRITE_LENGTH-SPRITE_LENGTH*0.625), y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 3) g2.drawImage(treeSprite[9], (int)(x*SPRITE_LENGTH-SPRITE_LENGTH*0.625), y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 4) g2.drawImage(treeSprite[9], (int)(x*SPRITE_LENGTH-SPRITE_LENGTH*0.625), y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 5) g2.drawImage(treeSprite[10], (int)(x*SPRITE_LENGTH-SPRITE_LENGTH*0.625), y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 6) g2.drawImage(treeSprite[10], (int)(x*SPRITE_LENGTH-SPRITE_LENGTH*0.625), y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					// en train de brûler
					else if (treeStatus[x][y] == 7 || treeStatus[x][y] == 8 || treeStatus[x][y] == 9) g2.drawImage(treeSprite[11], (int)(x*SPRITE_LENGTH-SPRITE_LENGTH*0.625), y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 10) g2.drawImage(treeSprite[11], (int)(x*SPRITE_LENGTH-SPRITE_LENGTH*0.625), y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
				}
				// mountainTree
				if (currentWorld[x][y].getWorldObjectImage() == 31) {
					int width = (int)(SPRITE_LENGTH*2.1);//(int)(SPRITE_LENGTH*2.5);
					int length = (int)(SPRITE_LENGTH*3.34);//(int)(SPRITE_LENGTH*4.0);
					// en train de pousser
					if (treeStatus[x][y] == 1) g2.drawImage(treeSprite[12], x*SPRITE_LENGTH, y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 2) g2.drawImage(treeSprite[12], x*SPRITE_LENGTH, y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 3) g2.drawImage(treeSprite[13], x*SPRITE_LENGTH, y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 4) g2.drawImage(treeSprite[13], x*SPRITE_LENGTH, y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 5) g2.drawImage(treeSprite[14], x*SPRITE_LENGTH, y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 6) g2.drawImage(treeSprite[14], x*SPRITE_LENGTH, y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					// en train de brûler
					else if (treeStatus[x][y] == 7 || treeStatus[x][y] == 8 || treeStatus[x][y] == 9) g2.drawImage(treeSprite[15], x*SPRITE_LENGTH, y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
					else if (treeStatus[x][y] == 10) g2.drawImage(treeSprite[15], x*SPRITE_LENGTH, y*SPRITE_LENGTH-(length-SPRITE_LENGTH), width, length, frame);
				}
			}
		}
		Toolkit.getDefaultToolkit().sync();
	}
	
	public void drawEmote(Graphics2D g2, Agents agent, boolean showEmote)
	{
		if (!showEmote) return;
		int x, y;
		int v, w;
		if (agent.orientation == 0) { 
			x = 0; y = 1; v = 0; w = -agent.animation.getFrameShift();
		}
		else if (agent.orientation == 1) { 
			x = -1; y = 0; v = agent.animation.getFrameShift(); w = 0;
		}
		else if (agent.orientation == 2) { 
			x = 0; y = -1; v = 0; w = agent.animation.getFrameShift();
		}
		else { 
			x = 1;  y = 0; v = -agent.animation.getFrameShift(); w = 0;
		}
		
		if (agent.getPregnancyDuration() > Agents.PREGNANCY_DURATION-15)
			g2.drawImage(mating, (agent.getX()+x)*SPRITE_LENGTH+v, (agent.getY()+y)*SPRITE_LENGTH+w-SPRITE_LENGTH, SPRITE_LENGTH, SPRITE_LENGTH, frame);
	}
	
	public void drawVision(Graphics2D g2, Agents agent, boolean showVision)
	{
		if (!showVision) return;
		for (int i = agent.getX()-agent.getVision() ; i <= agent.getX()+agent.getVision() ; i++)
			for (int j = agent.getY()-agent.getVision() ; j <= agent.getY()+agent.getVision() ; j++) {
				g2.setColor(Color.RED);
		    		g2.drawRect(((i+dx)%dx)*SPRITE_LENGTH, ((j+dy)%dy)*SPRITE_LENGTH, SPRITE_LENGTH, SPRITE_LENGTH);		
			}
	}
	
	public void drawStats(Graphics2D g2, Agents agent, boolean showStats)
	{
		if (!showStats) return;
		int x, y;
		int v, w;
		if (agent.orientation == 0) { 
			x = 0; y = 1; v = 0; w = -agent.animation.getFrameShift();
		}
		else if (agent.orientation == 1) { 
			x = -1; y = 0; v = agent.animation.getFrameShift(); w = 0;
		}
		else if (agent.orientation == 2) { 
			x = 0; y = -1; v = 0; w = agent.animation.getFrameShift();
		}
		else { 
			x = 1;  y = 0; v = -agent.animation.getFrameShift(); w = 0;
		}
		// barre de vie
		g2.setColor(Color.RED);
		g2.drawRect((agent.getX()+x)*SPRITE_LENGTH+v, (agent.getY()+y)*SPRITE_LENGTH+w-(3*SPRITE_LENGTH/7), SPRITE_LENGTH, SPRITE_LENGTH/7);
		g2.fillRect((agent.getX()+x)*SPRITE_LENGTH+v, (agent.getY()+y)*SPRITE_LENGTH+w-(3*SPRITE_LENGTH/7), agent.getHealth()*SPRITE_LENGTH/Agents.MAX_HEALTH, SPRITE_LENGTH/7);
		// barre de faim
		g2.setColor(Color.GREEN);
		g2.drawRect((agent.getX()+x)*SPRITE_LENGTH+v, (agent.getY()+y)*SPRITE_LENGTH+w-(2*SPRITE_LENGTH/7), SPRITE_LENGTH, SPRITE_LENGTH/7);				
		g2.fillRect((agent.getX()+x)*SPRITE_LENGTH+v, (agent.getY()+y)*SPRITE_LENGTH+w-(2*SPRITE_LENGTH/7), agent.getHunger()*SPRITE_LENGTH/Agents.MAX_HUNGER, SPRITE_LENGTH/7);
		// barre d'energie
		g2.setColor(Color.BLUE);
		g2.drawRect((agent.getX()+x)*SPRITE_LENGTH+v, (agent.getY()+y)*SPRITE_LENGTH+w-(SPRITE_LENGTH/7), SPRITE_LENGTH, SPRITE_LENGTH/7);		
		g2.fillRect((agent.getX()+x)*SPRITE_LENGTH+v, (agent.getY()+y)*SPRITE_LENGTH+w-(SPRITE_LENGTH/7),  agent.getEnergy()*SPRITE_LENGTH/Agents.MAX_ENERGY, SPRITE_LENGTH/7);
		// barre de durée de gestation
		if (agent.getIsFemale()) {
			g2.setColor(Color.PINK);
			g2.drawRect((agent.getX()+x)*SPRITE_LENGTH+v, (agent.getY()+y)*SPRITE_LENGTH+w, SPRITE_LENGTH, SPRITE_LENGTH/7);		
			g2.fillRect((agent.getX()+x)*SPRITE_LENGTH+v, (agent.getY()+y)*SPRITE_LENGTH+w,  agent.getPregnancyDuration()*SPRITE_LENGTH/Agents.PREGNANCY_DURATION, SPRITE_LENGTH/7);
		}
	
	}
	
	
	
	/* ==================================== */
	/*			EXTRACT_DATA		     */ 
	/* ==================================== */



	//cette version n'est appelée qu'une fois et extrait les données statiques du monde
	public void extractData()
	{
		int nbOceanCells = 0;
		int nbTemperateCells = 0;
		int nbDesertCells = 0;
		int nbMountainCells = 0;

		for (int y=0; y<dy; y++){
			for (int x=0; x<dx; x++){
				switch ( currentWorld[x][y].getBiomeID() )
				{
					case 1:
						nbOceanCells++;
						break;
					case 2:
						nbTemperateCells++;
						break;
					case 3:
						nbDesertCells++;
						break;
					case 4:
						nbMountainCells++;
						break;
				}
			}
		}
		//création ou écrasement du fichier biomes_data.csv
		try {
			File fileBiomesData = new File("./data/biomes_data.csv");
		   	if ( fileBiomesData.createNewFile() )
		   		System.out.println("./data/biomes_data.csv créé");
			else
				System.out.println("./data/biomes_data.csv existant");
		} catch ( IOException ioe ) { /*ignorer*/ }

		//création ou écrasement du fichier data.csv
		try {
			File fileData = new File("./data/data.csv");
		   	if ( fileData.createNewFile() )
		   		System.out.println("./data/data.csv créé");
			else
				System.out.println("./data/data.csv existant");
		} catch ( IOException ioe ) { /*ignorer*/ }
		
		//écriture des données sur biomes_data.csv
		try{
			PrintWriter writer = new PrintWriter("./data/biomes_data.csv", "utf-8");
			writer.println(nbOceanCells+";"+nbTemperateCells+";"+nbDesertCells+";"+nbMountainCells);
			writer.close();
		}catch( FileNotFoundException e ){/* ignorer */}
		catch(UnsupportedEncodingException e){ System.out.println("Erreur utf-8 non supporter"); }
	}


	//cette version extracte les données fluctuantes de la faune & flore
	public void extractData(int iteration)
	{
		int nbTemperateTrees = 0, nbTemperateBushes = 0;
		int nbDesertTrees = 0, nbDesertBushes = 0;
		int nbMountainTrees = 0, nbMountainBushes = 0;

		int nbBabyFF = 0, nbBabyIW = 0;
		int nbTemperateFF = 0, nbTemperateIW = 0;
		int nbDesertFF = 0, nbDesertIW = 0;
		int nbMountainFF = 0, nbMountainIW = 0;

		int nbAllTrees = 0, nbAllBushes = 0;
		int nbAllFF = 0, nbAllIW = 0;

		// recolte des data ecosysteme		
		for (int y=0; y<dy; y++){
			for (int x=0; x<dx; x++){
				switch ( currentWorld[x][y].getBiomeID() )
				{
					case 2:
						switch ( currentWorld[x][y].getWorldObjectImage() )
						{
							case 11:
								nbTemperateTrees++;
								break;
							case 12:
								nbTemperateBushes++;
								break;
						}
						break;
					case 3:
						switch ( currentWorld[x][y].getWorldObjectImage() )
						{
							case 21:
								nbDesertTrees++;
								break;
							case 22:
								nbDesertBushes++;
								break;
						}
						break;
					case 4:
						switch ( currentWorld[x][y].getWorldObjectImage() )
						{
							case 31:
								nbMountainTrees++;
								break;
							case 33:
								nbMountainBushes++;
								break;
						}
						break;
				}	
			}
		}
		// récolte des data agents
		for (Agents a : agentsList)
		{
			if (a.getAdult() == false){
				if (a.getType() == "Firefox") nbBabyFF++;
				else nbBabyIW++;
			}
			else
			{
				switch ( currentWorld[a.getX()][a.getY()].getBiomeID() )
				{
					case 2:
						if (a.getType() == "Firefox") nbTemperateFF++;
						else nbTemperateIW++;
						break;
					case 3:
						if (a.getType() == "Firefox") nbDesertFF++;
						else nbDesertIW++;
						break;
					case 4:
						if (a.getType() == "Firefox") nbMountainFF++;
						else nbMountainIW++;
						break;
				}
			}
		}
		nbAllTrees = nbTemperateTrees + nbDesertTrees + nbMountainTrees;
		nbAllBushes = nbTemperateBushes + nbDesertBushes + nbMountainBushes;
		nbAllFF = nbBabyFF + nbTemperateFF + nbDesertFF + nbMountainFF;
		nbAllIW = nbBabyIW + nbTemperateIW + nbDesertIW + nbMountainIW;
		//écriture des données sur data.csv
		try (PrintWriter pw = new PrintWriter(new FileWriter("./data/data.csv", true))) {
		    pw.println(iteration+";"+nbTemperateTrees+";"+nbTemperateBushes+";"+nbDesertTrees+";"+nbDesertBushes+";"+
				nbMountainTrees+";"+nbMountainBushes+";"+nbBabyFF+";"+nbBabyIW+";"+nbTemperateFF+";"+
				nbTemperateIW+";"+nbDesertFF+";"+nbDesertIW+";"+nbMountainFF+";"+nbMountainIW+";"+
				nbAllTrees+";"+nbAllBushes+";"+nbAllFF+";"+nbAllIW);
			pw.close();
		} catch (IOException e) {
		    System.out.println("Error in writing data_.csv");
		    e.printStackTrace();
		}
	}

}
