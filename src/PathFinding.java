import java.util.LinkedList;
import java.util.List;

public class PathFinding {
	private int dx; 
	private int dy;
	public Node[][] nodes;

	// creer un tableau à deux dimensions ou true représente une node walkable false une node non-walkable
	public PathFinding(Cell[][] map, Agents agent)
	{
		this.dy = map[0].length;
		this.dx = map.length;
		nodes = new Node[dx][dy];
		for (int x = 0; x < dx; x++) {
			for (int y = 0; y < dy; y++) {
				// SI Firefox 
				if (agent instanceof Firefox) { 
					// SI bébé ou en gestation
					if (!agent.getAdult() || agent.getPregnancyDuration() > 0) {
						// SI biome desert et pas d'obstacle, mettre true dans le tableau à cette position
						if (map[x][y].getBiomeID() == 3 && map[x][y].getWorldObjectImage() != 13 && map[x][y].getWorldObjectImage() != 23 && map[x][y].getWorldObjectImage() != 34)
							nodes[x][y] = new Node(x, y, true);
						// SINON mettre false
						else
							nodes[x][y] = new Node(x, y, false);
					}
					// SI adulte 
					else {
						// SI pas d'obstacle mettre true dans le tableau à cette position
						if (map[x][y].getBiomeID() != 1 && map[x][y].getWorldObjectImage() != 13 && map[x][y].getWorldObjectImage() != 23 && map[x][y].getWorldObjectImage() != 34)
							nodes[x][y] = new Node(x, y, true);
						// SINON mettre false
						else 
							nodes[x][y] = new Node(x, y, false);
					}
				}
				
				// SI Iceweasel
				if (agent instanceof Iceweasel) { 
					// SI bébé ou en gestation
					if (!agent.getAdult() || agent.getPregnancyDuration() > 0) {
						// SI biome montagne et pas d'obstacle, mettre true dans le tableau à cette position
						if (map[x][y].getBiomeID() == 4 && map[x][y].getWorldObjectImage() != 13 && map[x][y].getWorldObjectImage() != 23 && map[x][y].getWorldObjectImage() != 34)
							nodes[x][y] = new Node(x, y, true);
						// SINON mettre false
						else
							nodes[x][y] = new Node(x, y, false);
					}
					// SI adulte 
					else {
						// SI pas d'obstacle mettre true dans le tableau à cette position
						if (map[x][y].getBiomeID() != 1 && map[x][y].getWorldObjectImage() != 13 && map[x][y].getWorldObjectImage() != 23 && map[x][y].getWorldObjectImage() != 34)
							nodes[x][y] = new Node(x, y, true);
						// SINON mettre false
						else 
							nodes[x][y] = new Node(x, y, false);
					}
				}
				 
			}
		}
	}

	// affiche sur la console une map avec le chemin qui mène au goal 
	// chaque node walkable n'est pas imprimé
	// chaque node non-walkable est représenté par un '#' et chaque node du path par un '@'
	public void printMap(List<Node> path)
	{
		for (int j = 0; j < dy; j++) {
			for (int i = 0; i < dx; i++) {
				if (!nodes[i][j].isWalkable()) System.out.print(" #");
				else if (path.contains(nodes[i][j])) System.out.print(" @");
				else	System.out.print("  ");
			}
			System.out.print("\n");
		}
	}

	// retourne une node avec une coordonnées spécifiques
	// si x et y ne sont pas dans la map, retourne null
	public Node getNode(int x, int y)
	{
		return nodes[(x+dx) % dx][(y+dy) % dy];
	}

	// calcule un chemin depuis les positions de départ et de fin 
	// renvoie une liste contenant tous les nodes visités SI il y a un chemin SINON une liste vide
	public List<Node> findPath(int startX, int startY, int goalX, int goalY)
	{
		// si la position de départ est la même que la position d'arrivée
		if (startX == goalX && startY == goalY) {
			// renvoie un chemin vide parce-qu'on a pas besoin de bouger du tout 
			return new LinkedList<Node>();
		}
		
		// liste des noeuds déjà visités
		List<Node> openList = new LinkedList<Node>();
		// liste des noeuds actuellement découverts mais voisins à visiter
		List<Node> closedList = new LinkedList<Node>();
		
		// ajouter le noeud de départ dans la openList
		openList.add(nodes[startX][startY]);
		
		// la boucle s'arrête quand la position du noeud courant est égale à celle du noeud d'arrivée 
		// ou quand aucun chemin n'est trouvé
		while (true) {
		
			// prendre le noeud avec le plus petit score F dans la openList 
			// il deviendra la noeud courant 
			Node current = lowestFInList(openList);
			// enlever le noeud courant de la openList
			openList.remove(current);
			// ajouter le noeud courant dans la closedList
			closedList.add(current);
			
			// SI la position du noeud courant est égale à celle du noeud d'arrivée 
			if ((current.getX() == goalX) && (current.getY() == goalY)) {
				// renvoie une liste contenant tous les noeuds visités
				return calcPath(nodes[startX][startY], current);
			}

			List<Node> adjacentNodes = getAdjacent(current, closedList);
			
			for (Node adjacent : adjacentNodes) {
				// si le noeud n'est pas dans la openList
				if (!openList.contains(adjacent)) {
					// mettre le noeud courant comme parent du noeud adjacent
					adjacent.setParent(current);
					// changer le coût H de ce noeud (coût estimé depuis ce noeud jusqu'au noeud d'arrivée)
					adjacent.setH(nodes[goalX][goalY]);
					// changer le coût G de ce noeud (coût depuis le noeud de départ jusqu'à ce noeud)
					adjacent.setG(current);
					// ajouter le noeud dans la openList
					openList.add(adjacent);
				}
			}
			
			// SI aucun chemin n'existe...				
			if (openList.isEmpty()) {
				// renvoie une liste vide
				return new LinkedList<Node>();
			}
			// mais si il y a un chemin, continuer la boucle 
		}
	}
	
	// renvoie une liste contenant tous les noeuds du chemin dans l'ordre (du départ à l'arrivée)
	// on part du noeud d'arrivée et on remonte dans l'arbre jusqu'au noeud de départ 
	// (en passant par le noeud père à chaque fois)
	private List<Node> calcPath(Node start, Node goal)
	{
		LinkedList<Node> path = new LinkedList<Node>();

		Node node = goal;
		boolean done = false;
		while (!done) {
			path.addFirst(node);
			node = node.getParent();
			if (node.equals(start)) done = true;
		}
		return path;
	}

	// renvoie le noeud avec le score F le plus petit de la liste passé en argument
	private Node lowestFInList(List<Node> list)
	{
		Node cheapest = list.get(0);
		for (int i = 0; i < list.size(); i++)
			if (list.get(i).getF() < cheapest.getF())
				cheapest = list.get(i);
		return cheapest;
	}

	// renvoie la liste des noeuds adjacents d'un noeud donné SI :
	// ils existent, sont walkable et ne sont pas déjà dans la closedList
	private List<Node> getAdjacent(Node node, List<Node> closedList)
	{
		List<Node> adjacentNodes = new LinkedList<Node>();
		int x = node.getX();
		int y = node.getY();

		Node adjacent;

		// Check le noeud à gauche
		adjacent = getNode((x - 1 + dx) % dx, y);
		if (adjacent != null && adjacent.isWalkable() && !closedList.contains(adjacent))
			adjacentNodes.add(adjacent);

		// Check le noeud à droite
		adjacent = getNode((x + 1 + dx) % dx, y);
		if (adjacent != null && adjacent.isWalkable() && !closedList.contains(adjacent))
			adjacentNodes.add(adjacent);

		// Check le noeud en haut
		adjacent = this.getNode(x, (y - 1 + dy) % dy);
		if (adjacent != null && adjacent.isWalkable() && !closedList.contains(adjacent))
			adjacentNodes.add(adjacent);

		// Check le noeud en bas
		adjacent = this.getNode(x, (y + 1 + dy) % dy);
		if (adjacent != null && adjacent.isWalkable() && !closedList.contains(adjacent))
			adjacentNodes.add(adjacent);
		return adjacentNodes;
	}

}
