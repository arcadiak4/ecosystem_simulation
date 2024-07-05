import javax.swing.JFrame;
import java.util.ArrayList;

public class MainWorld {
	public static void main(String[] args) {
		
		
		
	/* ====================================== */
	/*	        PARAMETRES GLOBAUX           */
	/* ====================================== */
	
	
	
		int dx = 55;	// salles TME 59 - laptop 55
		int dy = 25; 	// salles TME 28 - laptop 25
		boolean SHOW_MATRICE = false;
		
		int ITERATIONS = 10000;
		int DELAY = 70;
		int FREQ_AGENT_MOVE = 6;
		int FREQ_AGENT_ACTION = 2;
		int FREQ_AGENT_ANIM = 1;
		int FREQ_DATA = 20;
		int FREQ_WORLD = 15;


			
	/* ====================================== */
	/*	        CREATION DU MONDE            */
	/* ====================================== */
		
		
		
		World myWorld = new World(dx, dy);
		myWorld.generateBiomes();
		myWorld.showMatrice(SHOW_MATRICE);
		myWorld.generateAgents();
		
		
		
	/* ====================================== */
	/*	        AFFICHAGE GRAPHIQUE          */
	/* ====================================== */
		
		
		
		int frameSizeX = dx*World.SPRITE_LENGTH;	// salles TME 1980 
		int frameSizeY = dy*World.SPRITE_LENGTH;	// salles TME 1184
		myWorld.showWindow(frameSizeX, frameSizeY);
		
		
		
	/* ====================================== */
	/*	       ITERATION SUR LE MONDE        */
	/* ====================================== */
	
	
	
		System.out.println("Cycle du monde : 0");
		myWorld.extractData();

		for(int i = 1 ; i < ITERATIONS ; i++){
			System.out.println("Cycle du monde : " + i);
			myWorld.repaint();
			if (i % FREQ_DATA == 0)
				myWorld.extractData(i);
			if (i % FREQ_WORLD == 0)
				myWorld.stepWorld(i, FREQ_WORLD);
			
			myWorld.stepAgents(i, FREQ_AGENT_MOVE, FREQ_AGENT_ACTION, FREQ_AGENT_ANIM);
			try { Thread.sleep(DELAY); } 
			catch (InterruptedException e){ }
		}
		
	}
}
