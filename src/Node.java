import java.util.ArrayList;

/**
 * 
 * @author rebeccahong
 *
 */
public class Node {

	private String label;
	private ArrayList<Edge> edges;

	private ArrayList<Node> adj = new ArrayList<Node>();// for MST, change to
														// undirected graph
	private int indegree;// used in tpsort
	private boolean visited = false;// used in dijkstra's shortest path
									// algorithm
	private int minDistance = Integer.MAX_VALUE;// dijkstra's shortest path cost
	private ArrayList<Node> paths = new ArrayList<Node>();// the list of all
															// nodes on the
															// shortest path
	private Node path;// the immediate last node on shortest path
	private Village data;// the village data

	public Node(String label) {
		this.label = label;
		edges = new ArrayList<Edge>();
	}

	public boolean isVisited() {
		return this.visited;
	}

	public void visit() {
		this.visited = true;
		System.out.println("set" + this.label + " visited.");
	}

	public ArrayList<Node> getPaths() {
		return this.paths;
	}

	public void setMinDist(int newMin) {
		this.minDistance = newMin;
	}

	public int getMinDist() {
		return this.minDistance;
	}

	public int getIndegree() {
		return this.indegree;
	}

	public void setIndegree(int newIndegree) {
		this.indegree = newIndegree;
	}

	public String getLabel() {
		return this.label;
	}

	/**
	 * remove the edge to destination node dest
	 * 
	 * @param dest
	 */
	public Edge removeEdge(Node dest) {
		Edge temp = new Edge();
		for (int i = 0; i < edges.size(); i++) {
			if (edges.get(i).getDest() == dest) {
				temp = edges.get(i);
				edges.remove(i);
			}
		}
		return temp;
	}

	public boolean contains(Node dest) {
		for (Edge e : edges) {
			if (e.getDest() == dest)
				return true;
		}
		return false;
	}

	public ArrayList<Edge> getEdges() {
		return this.edges;
	}

	public Village getData() {
		return this.data;
	}

	public String toString() {
		return this.label;
	}

	public void reset() {
		this.visited = false;
		this.minDistance = Integer.MAX_VALUE;
		this.path = null;
		this.paths = new ArrayList<Node>();
		this.adj = new ArrayList<Node>();
	}

	public void setVillage(Village data) {
		this.data = data;

	}

	public Node getPath() {
		return this.path;
	}

	public void setPath(Node path) {
		this.path = path;
	}

	public ArrayList<Node> getAdj() {
		return this.adj;
	}

	public void setAdj(ArrayList<Node> newadj) {
		this.adj = newadj;
	}
}
