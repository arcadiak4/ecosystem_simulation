public class Node {

	private int x;
	private int y;
	// le cout pour aller d'un noeud à un autre
	protected static final int MOVEMENT_COST = 1;
	private boolean walkable;
	private Node parent;
	// le cout pour aller du noeud de depart à ce noeud
	private int g;
	// l'heuristique qui donne une estimation du chemin le moins couteux du noeud vers le noeud d'arrivé
	private int h;

	//==========================================//
	//		       CONSTRUCTEUR 	         //
	//==========================================//
	
	public Node(int x, int y, boolean walkable) {
		this.x = x;
		this.y = y;
		this.walkable = walkable;
	}
	
	//==========================================//
	//		       GETTERS SETTERS 	         //
	//==========================================//
	
	// Setter pour le score G basé sur le score G du noeud parent + le coût de mouvement
	public void setG(Node parent) {
		g = (parent.getG() + MOVEMENT_COST);
	}

	// calcule et renvoie le score G, sans le changer dans la classe
	public int calculateG(Node parent) {
		return (parent.getG() + MOVEMENT_COST);
	}

	// Setter pour le score H basé sur la distance entre le noeud d'arrivée et ce noeud 
	public void setH(Node goal) {
		h = (Math.abs(getX() - goal.getX()) + Math.abs(getY() - goal.getY())) * MOVEMENT_COST;
	}

	public int getX() { return x; }
	public void setX(int x) { this.x = x; }
	public int getY() { return y; }
	public void setY(int y) { this.y = y; }

	public boolean isWalkable() { return walkable; }
	public void setWalkable(boolean walkable) { this.walkable = walkable; }

	public Node getParent() { return parent; }
	public void setParent(Node parent) { this.parent = parent; }

	public int getF() { return g + h; }
	public int getG() { return g; }
	public int getH() { return h; }
	
	//==========================================//
	//		       METHODE EQUALS 	         //
	//==========================================//
	
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Node))
			return false;
		if (o == this)
			return true;

		Node n = (Node) o;
		if (n.getX() == x && n.getY() == y && n.isWalkable() == walkable)
			return true;
		return false;
	}

}
