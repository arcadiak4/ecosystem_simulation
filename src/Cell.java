public class Cell{
	
	private int x;
	private int y;
	private int agent; // 0: contient un Firefox et 1: contient un Iceweasel
	
	private int altitude;
	/* -3 = ocean
	 * -2 = lac(vide)
	 * -1 = lac(remplie)
	 *  0 = sol
	 * 1+ = altitude */
	 
	private int biomeID;
	/* 0 = case vierge
	 * 1 = ocean
	 * 2 = temperate
	 * 3 = desert
	 * 4 = mountain */
	 
	// ordre d'affichage
	// 0 = null
	private int groundImage;
	private int worldObjectImage;
	private int weatherImage;
	
	// constructor
	public Cell(){
		altitude = 0;
		biomeID = 0;
		groundImage = 0;
		worldObjectImage = 0;
		weatherImage = 0;
	}
	
	// methodes set
	public void setX(int val) {x = val;}
	public void setY(int val) {y = val;}
	public void setAgent(int val) {agent = val;}
	public void setAltitude(int val) {altitude = val;}
	public void setBiomeID(int val) {biomeID = val;}	
	public void setGroundImage(int val) {groundImage = val;}
	public void setWorldObjectImage(int val) {worldObjectImage = val;}
	public void setWeatherImage(int val) {weatherImage = val;}
	
	// methodes get
	public int getX() {return x;}
	public int getY() {return y;}
	public int getAgent() {return agent;}
	public int getAltitude() {return altitude;}
	public int getBiomeID() {return biomeID;}
	public int getGroundImage() {return groundImage;}
	public int getWorldObjectImage() {return worldObjectImage;}
	public int getWeatherImage() {return weatherImage;}

}
