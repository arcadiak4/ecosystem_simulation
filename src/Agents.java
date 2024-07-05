import java.util.List;
import java.util.ArrayList;

public abstract class Agents{

	//==========================================//
	//		   	PARAMETRES GLOBAUX  	    //
	//==========================================//
	
	protected static int MAX_HEALTH = 100;			//vie de l'agent, 0=mort

	protected static int MAX_HUNGER = 100;			// la faim de l'agent
	protected static int HUNGRY = 20;				// commence à avoir faim
	protected static int HUNGER_DMG = 2;			// dégâts recu si hunger<=0
	protected static int FREQ_DMG_HUNGER = 3;		// toutes les FREQ_DMG_HUNGER l'agent s'affame
	protected static int EATING_DURATION = 15;		// nombre cycle requis pour manger
	
	protected static int MAX_ENERGY = 100;			// energie lié au sommeil
	protected static int LOW_ENERGY = 20;			// commence à vouloir dormir
	protected static int SLEEP_DMG = 2;				// dégats si energie <= 0
	protected static int FREQ_DMG_SLEEP = 3;		// toutes les FREQ_DMG_SLEEP l'agent se fatigue
	protected static int SLEEPING_DURATION = 30;		// nombre cycle requis un sommeil complet
	
	protected static int ADULT_AGE = 400;			// nombre cycle avant maturation
	protected static int PREGNANCY_DURATION = 200;	// nombre de cycle avant de pouvoir à nouveau avoir un bébé
	public static double P_REPRODUCTION = 0.01;		// probabilité de reproduction

	//==========================================//
	//		   	   ATTRIBUTS  	              //
	//==========================================//
	
	protected Animation animation;
	protected List<Node> followPath;
	protected Cell bushToEat;
	protected Cell treeToFire;
	protected ArrayList<Cell> treeToIce;
	protected Cell preyToKill;
	protected Cell defaultDestination;
	protected String type;			//type of animal
	protected int x;
	protected int y;
	protected int orientation;		//0=haut 1=droit 2=bas 3=gauche
	protected int vision;			//portée de la vision cf predateur
	
	protected boolean dead;			//agent vivant ou mort
	protected boolean adult;			//baby = false; adulte = true
	protected boolean isFemale;		// true: female false: male

	protected int age;				//age en nb cycles depuis la naissance
	protected int chased;			//0=false, position du predateur 1=Nord, 2=Est, 3=Sud, 4=Ouest
	protected int health;			//vie, 0=dead
	protected int energy;			//fatigue, 0 = perte de vie
	protected int hunger;			//niveau de satiété, 0 = perte de vie
	protected int sleepingDuration;	//nb cycle avant le l'éveil
	protected int eatingDuration;		//nb cycle restant en mangeant
	protected int pregnancyDuration;
	
	//==========================================//
	//		   	  CONSTRUCTEUR  	         //
	//==========================================//
	
	public Agents(String type, int x, int y, int vision)
	{
		this.animation = null;
		this.followPath = null;
		this.bushToEat = null;
		this.treeToFire = null;
		this. treeToIce = null;
		this.preyToKill = null;
		this.defaultDestination = null;
		this.type = type;
		this.x = x;
		this.y = y;
		this.orientation = 0;
		this.vision = vision;
		this.dead = false;
		this.adult = false;
		this.isFemale = Math.random() < 0.5 ? true : false;
		
		this.age = 1;
		this.chased = 0;
		this.health = MAX_HEALTH;
		this.energy = MAX_ENERGY;	
		this.sleepingDuration = 0;
		this.hunger = MAX_HUNGER;
		this.eatingDuration = 0;
		this.pregnancyDuration = 0;
	}

	//==========================================//
	//             GETTERS & SETTERS  	         //
	//==========================================//
	
	public String getType() { return type; }
	public int getX() { return x; }
	public int getY() { return y; }
	public int getOrientation() { return orientation; }
	public int getVision() { return vision; }
	public boolean getDead() { return dead; }
	public boolean getAdult() { return adult; }
	public boolean getIsFemale() { return isFemale; }
	public int getAge() { return age; }
	public int getHealth() { return health; }
	public int getChased() { return chased; }
	public int getEnergy() { return energy; }
	public int getSleepingDuration() { return sleepingDuration; }
	public int getHunger() { return hunger; }
	public int getEatingDuration() { return eatingDuration; }
	public int getPregnancyDuration() { return pregnancyDuration; }
	
	public void setX(int val) { x = val; }
	public void setY(int val) { y = val; }
	public void setOrientation(int val) { orientation = val; }
	public void setVision(int val) { vision = val; }
	public void setDead(boolean val) { dead = val; }
	public void setAdult(boolean val) { adult = val; }
	public void setChased(int val) { chased = val; }
	public void setEnergy(int val) { energy = val; }
	public void setHunger(int val) { hunger = val; }
	public void setPregnancyDuration(int val) { pregnancyDuration = val; }


	//==========================================//
	//		METHODES COMMUNES AUX AGENTS	    //
	//==========================================//
	
	private void grow()
	{ 
		age++; 
		if (age == ADULT_AGE) adult = true; 
	}
	
	private void hungerDmg()
	{ 
		health -= HUNGER_DMG; 
		if (health <= 0) dead = true;
	}
	
	private void sleepDmg()
	{
		health -= SLEEP_DMG; 
		if (health <= 0) dead = true; 
	}
	
	/*
	*	Fonction appelé par action() dans Firefox/ Iceweasel
	*	Elle est appelée à chaque fois tant que l'agent n'a pas fini de manger
	*/
	protected void eatBush(World w)
	{ 
		if (hunger <= MAX_HUNGER) hunger += (int)(MAX_HUNGER/EATING_DURATION);
		else hunger = MAX_HUNGER;
		eatingDuration--;
		// SI l'agent a fini de manger, on fait disparaitre le buisson de la map
		if (eatingDuration == 0) w.getCell(bushToEat.getX(), bushToEat.getY()).setWorldObjectImage(0);
	}
	
	/*
	*	Fonction appelé par action() dans Firefox/ Iceweasel
	*	Elle est appelée à chaque fois tant que l'agent n'a pas fini de dormir
	*/
	protected void sleep()
	{
		if (energy <= MAX_ENERGY) energy += (int)(MAX_HUNGER/SLEEPING_DURATION);
		else energy = MAX_ENERGY;
		sleepingDuration--;
	}
	
	protected void babyToBorn(World w)
	{
		// SI bébé
		if (!adult) return;
		// SI mâle
		if (!isFemale) return;
		// SI n'est pas en gestation
		if (pregnancyDuration == 0) return;
		// SI prêt à faire naître le bébé
		if (pregnancyDuration == 1) {
			if (this instanceof Firefox)
				w.getAgentsList().add(new Firefox(x, y));
			if (this instanceof Iceweasel)
				w.getAgentsList().add(new Iceweasel(x, y));
			pregnancyDuration--;
			return;
		}
		// SINON 
		pregnancyDuration--;
	}
	
	protected boolean isBusy()
	{
		if (eatingDuration > 0 ) return true;
		if (sleepingDuration > 0) return true;
		bushToEat = null;
		return false;
	}
	
	public void updateStatus(World w, int iteration)
	{
		babyToBorn(w);
		grow();
		if (iteration % FREQ_DMG_HUNGER == 0) {
			if (hunger > 0) hunger--;
			//si barre de faim à 0 et ne mange pas DMG
			else hungerDmg();
		}
		if (iteration % FREQ_DMG_SLEEP == 0) {
			if (energy > 0) energy--;
			//si barre d'energie à 0 et ne dort pas DMG
			else sleepDmg();
		}
		chased = predatorClose(w);
	}

	//==========================================//
	//		   METHODES ABSTRAITES  	         //
	//==========================================//
	
	public abstract Cell closestBush(World w);

	public abstract void nextTask(World w);
	
	public abstract void action(World w, int iteration, int FREQ_AGENT_ACTION, int FREQ_AGENT_ANIM);

	public abstract int orientationPath (World w, List<Node> followPath);

	public abstract void move(World w, int iteration, int FREQ_AGENT_MOVE, int FREQ_AGENT_ACTION, int FREQ_AGENT_ANIM);
	
	//==========================================//
	//		      PREDATOR_CLOSE	  	    //
	//==========================================//	

	//donne une valeur a chased a chaque updateStatus, indique la direction d'un predateur ou 0 si aucun predator en vue
	public int predatorClose(World w)
	{
		/*if ( (this.getType() == "Firefox" && w.getCell(x,y).getBiomeID() != 4) || (this.getType() == "Iceweasel" && w.getCell(x,y).getBiomeID() != 3) )
			return 0;
		for (Agents a : w.getAgentsList())
		{
			if (a.getType() != this.type)
			{
				//predateur au Nord
				if (a.getX() == this.x && a.getY() >= (y - vision + w.getDx())% w.getDy() && a.getY() <= this.y)
					return 1;
				// predateur au Sud
				if (a.getX() == this.x && a.getY() >= (y + vision + w.getDx())% w.getDy() && a.getY() <= this.y)
					return 3;
				//predateur a l'Ouest
				if (a.getX() >= (x - vision + w.getDx())% w.getDx() && a.getX() <= this.x && a.getY() == this.y)
					return 4;
				//predateur a l'Est
				if (a.getX() <= (x + vision + w.getDx())% w.getDx() && a.getX() >= this.x && a.getY() == this.y)
					return 2;
				//predateur au NO
				if (a.getX() >= (x - vision + w.getDx())% w.getDx() && a.getX() < this.x && 
					a.getY() >= (y - vision + w.getDx())% w.getDy() && a.getY() < this.y)
					if (Math.random()<0.5) return 1; else return 4;
				//predateur au NE
				if (a.getX() > this.x && a.getX() <= (x + vision + w.getDx())% w.getDx() && 
					a.getY() >= (y - vision + w.getDx())% w.getDy() && a.getY() < this.y)
					if (Math.random()<0.5) return 1; else return 2;
				//predateur au SE
				if (a.getX() > this.x && a.getX() <= (x + vision + w.getDx())% w.getDx() && 
					a.getY() > this.y && a.getY() <= (y + vision +w.getDy())% w.getDy())
					if (Math.random()<0.5) return 3; else return 2;
				//predateur au SO
				if (a.getX() >= (x - vision + w.getDx())% w.getDx() && a.getX() < this.x && 
					a.getY() > this.y && a.getY() <= (y + vision +w.getDy())% w.getDy())
					if (Math.random()<0.5) return 3; else return 4;
			}
		}*/
		return 0;
	}

}
