/**
 * 
 * @author rebeccahong
 *
 */
public class Edge {
	private int cost;
	private Node dest;
	private Road road;

	private boolean belongsToMST = false;// help in GUI, highlight the edges
											// which belong to minimum spanning
											// tree

	public Edge(Node dest, int cost) {
		this.dest = dest;
		this.cost = cost;
	}

	public Edge() {

	}

	public Node getDest() {
		return this.dest;
	}

	public void setDest(Node newDest) {
		this.dest = newDest;
	}

	public int getCost() {
		return this.cost;
	}

	public Road getRoad() {
		return this.road;
	}

	public void setRoad(Road road) {
		this.road = road;
	}

	public void addToMST() {
		this.belongsToMST = true;
	}

	public void reset() {
		this.belongsToMST = false;
	}
}
