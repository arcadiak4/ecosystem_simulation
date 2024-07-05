	import java.awt.image.BufferedImage; 
import java.util.ArrayList;
import java.util.List;

public class Iceweasel extends Agents{

	
	protected static int VISION = 2;
	
	// Images
	String ss = "src_sprites/iceweaselSpriteSheet.png";
	private BufferedImage[] walkingUp = {Sprite.getSprite(ss, 0, 0), Sprite.getSprite(ss, 1, 0), Sprite.getSprite(ss, 2, 0), Sprite.getSprite(ss, 3, 0), Sprite.getSprite(ss, 0, 0)};
	private BufferedImage[] walkingRight = {Sprite.getSprite(ss, 0, 1), Sprite.getSprite(ss, 1, 1), Sprite.getSprite(ss, 2, 1), Sprite.getSprite(ss, 3, 1), Sprite.getSprite(ss, 0, 1)};
	private BufferedImage[] walkingDown = {Sprite.getSprite(ss, 0, 2), Sprite.getSprite(ss, 1, 2), Sprite.getSprite(ss, 2, 2), Sprite.getSprite(ss, 3, 2), Sprite.getSprite(ss, 0, 2)};
	private BufferedImage[] walkingLeft = {Sprite.getSprite(ss, 0, 3), Sprite.getSprite(ss, 1, 3), Sprite.getSprite(ss, 2, 3), Sprite.getSprite(ss, 3, 3), Sprite.getSprite(ss, 0, 3)};
	
	private BufferedImage[] sleeping = {Sprite.getSprite(ss, 0, 4), Sprite.getSprite(ss, 1, 4)};
	private BufferedImage[] eating = {Sprite.getSprite(ss, 2, 4), Sprite.getSprite(ss, 3, 4)};
	
	// Animations
	private Animation walkUp = new Animation(walkingUp);
	private Animation walkRight = new Animation(walkingRight);
	private Animation walkDown = new Animation(walkingDown);
	private Animation walkLeft = new Animation(walkingLeft);
	private Animation[] walk = {walkUp, walkRight, walkDown, walkLeft};
	private Animation eat  = new Animation(eating);
	private Animation sleep = new Animation(sleeping);
	
	
	
	//==========================================//
	//		       CONSTRUCTEUR 	         //
	//==========================================//
	
	
	
	public Iceweasel(int x, int y)
	{
		super("Iceweasel", x, y, VISION);
		this.animation = walk[orientation];
	}



	//==========================================//
	//		       CLOSEST_BUSH   	         //
	//==========================================//
	
	
	
	public Cell closestBush(World w) 
	{
		// mettre dans l'arrayList tous les buissons du champ de vision de l'agent 
		ArrayList<Cell> bushArray = new ArrayList<Cell>();
		int xtemp = 0, ytemp = 0;
		
		for (int x = this.x - vision ; x <= this.x + vision ; x++) {
			for (int y = this.y - vision ; y <= this.y + vision ; y++) {
				xtemp = (x + w.getDx()) % w.getDx();
				ytemp = (y + w.getDy()) % w.getDy();
				
				// SI bébé 
				if (!this.adult) {
					// SI buisson de la montagne (l'agent au stade bébé ne mange que des buissons de la montagne)
					if (w.getCell(xtemp, ytemp).getWorldObjectImage() == 33)
						bushArray.add(w.getCell(xtemp, ytemp));
				}
				
				// SI adulte
				else {
					// SI agent en gestation
					if (pregnancyDuration > 0) {
						// SI buisson de la montagne (l'agent en gestation ne mange que des buissons de la montagne)
						if (w.getCell(xtemp, ytemp).getWorldObjectImage() == 33)
							bushArray.add(w.getCell(xtemp, ytemp));
					}
					// SINON
					else {
						// SI buisson (peut manger n'importe quel buisson)
						if (w.getCell(xtemp, ytemp).getWorldObjectImage() == 12 || w.getCell(xtemp, ytemp).getWorldObjectImage() == 22 || w.getCell(xtemp, ytemp).getWorldObjectImage() == 33)
							bushArray.add(w.getCell(xtemp, ytemp));
					}
				}
				
			}
		}
			
		// SI il n'y a aucun buisson aux alentours
		if (bushArray.size() == 0) return null;
		
		// SINON calculer la distance de chaque buisson depuis l'agent
		// et les stocker dans un tableau d'entiers de la même taille que l'arrayList
		int [] bushFromAgent = new int[bushArray.size()];
		int i = 0;
		for (Cell bush : bushArray) {
			bushFromAgent[i] = Math.abs(x-bush.getX()) + Math.abs(y-bush.getY());
			i++;
		}
		
		// trouver l'indice du tableau d'entiers contenant la distance minimale
		int iMin = 0;
		for (int j = 1 ; j < bushArray.size(); j++)
			if (bushFromAgent[j] < bushFromAgent[iMin]) iMin = j;
				
		// renvoyer la Cell contenant le buisson le plus proche de l'agent
		return bushArray.get(iMin);
	}
	
	
	
	//==========================================//
	//		   CLOSEST_TREE IN FIRE  	    //
	//==========================================//


	
	public ArrayList<Cell> closestTree(World w) 
	{
		// SI bébé, ne peut pas arroser d'arbre
		if (!this.adult) return null;
		// SI en gestation, ne peut pas arroser d'arbre
		if (this.pregnancyDuration > 0) return null;
		
		// mettre dans l'arrayList tous les arbres du champ de vision de l'agent 
		ArrayList<Cell> treeArray = new ArrayList<Cell>();
		int xtemp = 0, ytemp = 0;
		
		for (int x = this.x - vision ; x <= this.x + vision ; x++) {
			for (int y = this.y - vision ; y <= this.y + vision ; y++) {
				xtemp = (x + w.getDx()) % w.getDx();
				ytemp = (y + w.getDy()) % w.getDy();
				// SI arbre (n'importe lequel) au stade brûlé (7 à 9)
				if (w.treeStatus[xtemp][ytemp] >= 7 && w.treeStatus[xtemp][ytemp] <= 9)
					treeArray.add(w.getCell(xtemp, ytemp));
			}
		}
					
		// SI il n'y a aucun arbres en feu aux alentours
		if (treeArray.size() == 0) return null;
		
		return treeArray;
	}
	
	
	
	//==========================================//
	//		       CLOSEST_PREY   	         //
	//==========================================//


	
	public Cell closestPrey(World w) 
	{
		// SI bébé, ne peut pas pourchasser ses ennemis
		if (!this.adult) return null;
		// SI en gestation, ne peut pas pourchasser ses ennemis
		if (this.pregnancyDuration > 0) return null;
		
		// mettre dans l'arrayList tous les Firefox du champ de vision de l'agent 
		ArrayList<Cell> preyArray = new ArrayList<Cell>();
		int xtemp = 0, ytemp = 0;
		
		for (int x = this.x - vision ; x <= this.x + vision ; x++) {
			for (int y = this.y - vision ; y <= this.y + vision ; y++) {
				xtemp = (x + w.getDx()) % w.getDx();
				ytemp = (y + w.getDy()) % w.getDy();
				
				// SI la case montagne contient un Firefox (dans le TERRITOIRE des ICEWEASEL !!!)
				if (w.getCell(xtemp, ytemp).getBiomeID() == 4 && w.getCell(xtemp, ytemp).getAgent() == 0)
					preyArray.add(w.getCell(xtemp, ytemp));
			}
		}
				
		// SI il n'y a aucun Firefox aux alentours
		if (preyArray.size() == 0) return null;
		
		// SINON calculer la distance de chaque Firefox depuis l'agent
		// et les stocker dans un tableau d'entiers de la même taille que l'arrayList
		int [] FirefoxFromAgent = new int[preyArray.size()];
		int i = 0;
		for (Cell prey : preyArray) {
			FirefoxFromAgent[i] = Math.abs(x-prey.getX()) + Math.abs(y-prey.getY());
			i++;
		}
		
		// trouver l'indice du tableau d'entiers contenant la distance minimale
		int iMin = 0;
		for (int j = 1 ; j < preyArray.size(); j++)
			if (FirefoxFromAgent[j] < FirefoxFromAgent[iMin]) 
				iMin = j;
				
		// renvoyer la Cell contenant le Firefox le plus proche de l'agent
		return preyArray.get(iMin);
	}
	
	
	
	//==========================================//
	//		    DEFAULT_DESTINATION   	    //
	//==========================================//


	
	public Cell defaultDestination(World w) 
	{
		// mettre dans l'arrayList toutes les destinations possibles du champ de vision de l'agent 
		ArrayList<Cell> destinationArray = new ArrayList<Cell>();
		int xtemp = 0, ytemp = 0;
		
		for (int x = this.x - vision ; x <= this.x + vision ; x++) {
			for (int y = this.y - vision ; y <= this.y + vision ; y++) {
				xtemp = (x + w.getDx()) % w.getDx();
				ytemp = (y + w.getDy()) % w.getDy();
				
				// SI bébé
				if (!this.adult) {
					// Ne peut se déplacer que dans le biome montagne sauf à travers les rochers
					if (w.getCell(xtemp, ytemp).getBiomeID() == 4 && w.getCell(xtemp, ytemp).getWorldObjectImage() != 13 &&  w.getCell(xtemp, ytemp).getWorldObjectImage() != 23 &&  w.getCell(xtemp, ytemp).getWorldObjectImage() != 34)	
						destinationArray.add(w.getCell(xtemp, ytemp));
				}
				
				// SI adulte
				else {
					// SI agent en gestation
					if (pregnancyDuration > 0) {
						// Ne peut se déplacer que dans le biome montagne sauf à travers les rochers
						if (w.getCell(xtemp, ytemp).getBiomeID() == 4 && w.getCell(xtemp, ytemp).getWorldObjectImage() != 13 &&  w.getCell(xtemp, ytemp).getWorldObjectImage() != 23 &&  w.getCell(xtemp, ytemp).getWorldObjectImage() != 34)
							destinationArray.add(w.getCell(xtemp, ytemp));
					}
					// SINON
					else {
						// Peut se déplacer n'importe où sauf sur l'eau et à travers les rochers
						if (w.getCell(xtemp, ytemp).getBiomeID() != 1 && w.getCell(xtemp, ytemp).getWorldObjectImage() != 13 &&  w.getCell(xtemp, ytemp).getWorldObjectImage() != 23 &&  w.getCell(xtemp, ytemp).getWorldObjectImage() != 34)		
							destinationArray.add(w.getCell(xtemp, ytemp));
					}
				}
				
			}
		}
		
		// tirer aléatoirement une Cell de l'arrayList qui sera la destination de l'agent
		//if (destinationArray.size() == 0) return null;
		int alea = (int)(Math.random()*destinationArray.size());	
		return destinationArray.get(alea);
	}
	
	
	
	//==========================================//
	//		    	    NEXT_TASK	      	    //
	//==========================================//
	
	
	
	public void nextTask(World w)
	{
		// SI l'agent mange ou dort, il ne fait rien 
		// (possible de se faire tuer par un prédateur)
		if(isBusy()) return;
		
		// SINON recherche de chemin A* en fonction des priorités ci-dessous
		PathFinding nodeMap = new PathFinding(w.getCurrentWorld(), this);
		
		// SI pourchassé (PRIORITE 1)
		if (chased != 0) { return; }

		// SI fatigué (PRIORITE 2)
		else if (energy <= Agents.LOW_ENERGY) { 
			// il se met à dormir instantanément
			sleepingDuration = Agents.SLEEPING_DURATION;		
			return;
		}
		
		// SI l'agent a faim (PRIORITE 3)
		else if (hunger <= Agents.HUNGRY) {
			// il cherche le buisson le plus proche (grâce à son odorat super développé !)
			this.bushToEat = closestBush(w);
			// SI l'agent a trouvé un buisson
			if (bushToEat != null) {
				// il suit le chemin le plus court vers ce buisson
				this.followPath = nodeMap.findPath(x, y, bushToEat.getX(), bushToEat.getY());
				// SI il a atteint le buisson
				if (x == bushToEat.getX() && y == bushToEat.getY()) {
					// il se met à manger 
					eatingDuration = Agents.EATING_DURATION;
				}
				return;
			}
		}
		
		// SI il trouve une proie sur son territoire (PRIORITE 4)
		else if (closestPrey(w) != null) {
			this.preyToKill = closestPrey(w);
			if (preyToKill != null) {
				this.followPath = nodeMap.findPath(x, y, preyToKill.getX(), preyToKill.getY());
					// SI il a atteint la proie
					if (x == preyToKill.getX() && y == preyToKill.getY()) {
						// il le tue !!!
						for (Agents agent : w.getAgentsList()) {
							if (this == agent) continue;
							else if (agent.getX() == preyToKill.getX() && agent.getY() == preyToKill.getY()) {
								w.removeAgent(agent);
								preyToKill = null;
								break;
							}
						}
					}
			}
			return;
		}
		
		// SINON il n'est pas affamé et fatigué (PRIORITE 5)
		else {
			// il cherche l'arbre le plus proche de lui (pour l'éteindre !)
			this.treeToIce = closestTree(w);
			
			// SI l'agent a trouvé des arbres en feu
			if (treeToIce != null) {
				
				// il les éteint tous
				for (int i = 0 ; i < treeToIce.size() ; i++) {
					w.treeStatus[treeToIce.get(i).getX()][treeToIce.get(i).getY()] = 5;
				}
				treeToIce = null;
				// mais malheuresement sauver des arbres a quand même un prix ...
				this.energy -= 25;
				this.hunger -= 25;
				return;
			}
			
			// SINON
			else {
				// SI il n'est pas déjà en train de suivre un chemin par défaut
				if(defaultDestination == null) {
					// il cherche une case par défaut
					this.defaultDestination = defaultDestination(w);
				}
				// SI l'agent a trouvé une case
				if (defaultDestination != null) {
					// il suit le chemin le plus court vers la case-destination par défaut
					this.followPath = nodeMap.findPath(x, y, defaultDestination.getX(), defaultDestination.getY());
					if (x == defaultDestination.getX() && y == defaultDestination.getY())
						defaultDestination = null;
				}
// TEST
				//nodeMap.printMap(followPath);
			}
			
			
		}

	}
	
	
		
	//==========================================//
	//				ACTION		  	    //
	//==========================================//
	
	
	
	public void action(World w, int iteration, int FREQ_AGENT_ACTION, int FREQ_AGENT_ANIM)
	{ 
		if (eatingDuration > 0) {
			super.eatBush(w);
			if (iteration % FREQ_AGENT_ACTION == 0) {
				this.animation = eat;
				this.animation.restart();
			}
			else if (iteration % FREQ_AGENT_ANIM == 0) {
				this.animation.updateAction();	
			}
			return;
		}
		
		if (sleepingDuration > 0) {
			super.sleep();
			if (iteration % FREQ_AGENT_ACTION == 0) {
				this.animation = sleep;
				this.animation.restart();
			}
			else if (iteration % FREQ_AGENT_ANIM == 0) {
				this.animation.updateAction();	
			}
			return;
		}
	}
	
	
	
	//==========================================//
	//			PATH_ORIENTATION	  	    //
	//==========================================//
	
	
	
	/*
	* Renvoie un entier qui donne l'orientation pour le prochaine déplacement du path
	* Utile pour les animations
	*/
	
	public int orientationPath (World w, List<Node> followPath)
	{
		// gauche
		if (followPath.get(0).getX() == w.getDx()-1 && x == 0) return 3; 
		if (followPath.get(0).getX() < x && followPath.get(0).getX() != 0) return 3;
		// droite
		if (followPath.get(0).getX() == 0 && x == w.getDx()-1) return 1; 
		if (followPath.get(0).getX() > x) return 1;
		// haut
		if (followPath.get(0).getY() == w.getDy()-1 && y == 0) return 0;
		if (followPath.get(0).getY() < y && followPath.get(0).getY() != 0) return 0;
		// bas
		if (followPath.get(0).getY() == 0 && y == w.getDy()-1) return 2;
		if (followPath.get(0).getY() > y) return 2;
		
		return this.orientation;
	}
	
	
	
	//==========================================//
	//		   	      MOVE  	              //
	//==========================================//
	
	

	public void move(World w, int iteration, int FREQ_AGENT_MOVE, int FREQ_AGENT_ACTION, int FREQ_AGENT_ANIM)
	{

		/* toutes les STEP_FREQ_AGENT_MOVE iterations on : 
		* 1. donne pour l'agent sa prochaine orientation
		* 2. définie la future animation en fonction de l'orientation 
		* 	(0 = walkUp ; 1 = walkRight ; 2 = walkDown ; 3 = walkLeft)
		* 3. réinitialise currentFrame et frameDecalage à 0 
			avec la méthode restart() qui fait un update()
		* 4. change la position de l'agent en fonction de l'orientation
		*/
		
		// SI l'agent ne trouve pas de chemin
		if (this.followPath == null || this.followPath.size() == 0) return;
		
		
		if (iteration % FREQ_AGENT_MOVE == 0) {
			// SI il est en train de manger ou dormir, il ne se déplace pas
			if (isBusy()) return;
			this.orientation = orientationPath(w, this.followPath);
			this.animation = walk[orientation];
			this.animation.restart();
			switch (this.orientation) {
				case 0:
					w.getCell(x, y).setAgent(-1);
					this.y = (y-1+w.getDy())%w.getDy();
					w.getCell(x, y).setAgent(1);
					break;
				case 1:
					w.getCell(x, y).setAgent(-1);
					this.x = (x+1+w.getDx())%w.getDx();
					w.getCell(x, y).setAgent(1);
					break;
				case 2: 
					w.getCell(x, y).setAgent(-1);
					this.y = (y+1+w.getDy())%w.getDy();
					w.getCell(x, y).setAgent(1);
					break;
				case 3:
					w.getCell(x, y).setAgent(-1);
					this.x = (x-1+w.getDx())%w.getDx();
					w.getCell(x, y).setAgent(1);
					break;
			}
		}
		
		
		else if (iteration % FREQ_AGENT_ANIM == 0) {
	
		/* toutes les STEP_FREQ_AGENT_ANIM iterations on : 
		* 1. donne pour l'agent la prochaine frame de l'animation
		* 2. plus STEP_FREQ_AGENT_ANIM est petit plus l'animation est rapide   
		* 3. à la fin de l'animation, la méthode update() se stoppe jusqu'au prochain déplacement de l'agent
		*/

			if (isBusy()) return;
			this.animation.updateMove();
		}
	}
	
}
