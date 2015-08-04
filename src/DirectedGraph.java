import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Directed Graph
 * This graph implements:
 * 
 * 1.Prim's algorithm to find minimum spanning tree(directed graph is regarded as undirected)
 * 2.Dijkstra's Algorithm to find shortest path
 * 3.Topological sort
 *
 * 
 * @author rebeccahong
 *
 */
public class DirectedGraph {
	
	private static final String NEWLINE = System.getProperty("line.separator");
	private ArrayList<Node> nodes = new ArrayList<Node>();// nodes list
	private Map<String, Node> lookup = new HashMap<String, Node>();
	private boolean iscycle = false;

	/**
	 * Minimum Spanning Tree by Prim's Algrithm
	 * this graph is regarded as undirected
	 * @param startLabel
	 */
	public void MST(String startLabel) {
		reset();

		// create a set of visited nodes
		ArrayList<Node> visited = new ArrayList<Node>();
		Node start = lookup.get(startLabel);

		populateAdj();

		for (Node n : this.nodes) {
			System.out.println(n.getAdj().toString());
		}
		while (visited.size() < this.nodes.size()) {
			if (start == null) {
				break;
			}
			visited.add(start);
			start.visit();
			start = getMinSpan(visited);
		}
		
		System.out.println(visited.toString());
		for (Node n : visited) {
			for (Edge e : n.getEdges()) {
				if (e.belongsToMST()) {
					System.out.println(n.getLabel() + " "
							+ e.getDest().getLabel());
				}
			}
		}

	}
	/**
	 * Populate Adjacency List of each node
	 */
	private void populateAdj() {
		for (Node n : this.nodes) {
			for (Edge e : n.getEdges()) {
				n.getAdj().add(e.getDest());
				e.getDest().getAdj().add(n);
			}
		}
	}
	/**
	 * assistant method in MST
	 * @param src
	 * @return the next Node in algorithm
	 */
	private Node getMinSpan(ArrayList<Node> src) {
		int min = Integer.MAX_VALUE;
		Node dest = null;
		Edge temp = new Edge();
		for (Node n : src) {
			//for every edges coming out of n
			for (Edge e : n.getEdges()) {
				//Prim's Algorithm:
				// Only if the destination node is less than minimum and is not
				// visited(if visited generates a circle), take this edge into consideration
				if (e.getCost() < min && !e.getDest().isVisited()) {
					min = e.getCost();
					dest = e.getDest();
					temp = e;
				}
			}
			//for every m that is adjacent to n, check the m's edge that is toward n
			//(for treating it as undirected graph's sake)
			for (Node m : n.getAdj()) {
				//if m is not visited then consider the edge
				if (!m.isVisited()) {
					//every edges of m
					for (Edge e : m.getEdges()) {
						//see if there is an edge to n
						if (e.getDest() == n) {
							//if the cost is less than min
							if (e.getCost() < min) {
								//update local min
								dest = m;
								min = e.getCost();
								temp = e;
							}
						}
					}
				}
			}
		}
		//turn on this edge's switch
		temp.addToMST();
		return dest;
	}

	/**
	 * Shortest Path- Dijkstra's Algorithm implementation
	 * 
	 * @param startLabel
	 * @param destLabel
	 * @return an arrayList of nodes on the shortest path
	 */
	public synchronized ArrayList<Node> shortestPath(String startLabel,
			String destLabel) {
		reset();
		Node start = lookup.get(startLabel);
		Node dest = lookup.get(destLabel);
		// create copy of nodes for back up
		ArrayList<Node> tempNodes = new ArrayList<Node>();
		ArrayList<Node> visited = new ArrayList<Node>();

		for (int i = 0; i < nodes.size(); i++) {
			tempNodes.add(nodes.get(i));
			if (tempNodes.get(i) == start) {
				tempNodes.get(i).setMinDist(0);
			}
		}
		while (visited.size() < nodes.size()) {
			if (start == null) {
				break;
			}
			visited.add(start);
			start.visit();

			start = getMinDest(visited);

		}
		// print result to console
		System.out.println("Village\tCost\tVisited\tPath\tPathes list");

		// populate paths property in node class for GUI use
		for (Node n : tempNodes) {
			if (n.isVisited()) {
				updatePath(start, n, n.getPaths());
				// the paths figured out in the recursive updatePath() method
				// are in a reverse order
				Collections.reverse(n.getPaths());
				// add the node itself for GUI's convenience
				n.getPaths().add(n);
			}
		}
		for (int i = 0; i < tempNodes.size(); i++) {
			System.out.println(nodes.get(i).getLabel() + "\t"
					+ nodes.get(i).getMinDist() + "\t"
					+ nodes.get(i).isVisited() + "\t" + nodes.get(i).getPath()
					+ "\t" + nodes.get(i).getPaths().toString());
		}

		// get the destination node's paths list
		ArrayList<Node> paths = new ArrayList<Node>();
		for (Node n : tempNodes) {
			if (n == dest) {
				paths = n.getPaths();
			}
		}
		return paths;
	}

	/**
	 * Recursive function to find all the nodes on the shortest path for GUI
	 * Display
	 * 
	 * @param start
	 *            -the start node
	 * @param target
	 *            -target node
	 * @param paths
	 */
	private void updatePath(Node start, Node target, ArrayList<Node> paths) {
		if (target == start)
			return;
		for (Node n : nodes) {
			if (n == target.getPath()) {
				paths.add(n);
				updatePath(start, n, paths);
			}
		}
	}

	/**
	 * Assistant method in Dijkstra's algorithm
	 * 
	 * @param src
	 *            - the visited nodes list
	 * @return the unvisited node that has the minimum accumulated cost from any
	 *         visited node in visited nodes list
	 */
	private Node getMinDest(ArrayList<Node> src) {
		int min = Integer.MAX_VALUE;
		Node minNode = null;
		// for every visited node
		for (Node n : src) {
			// for every edge in this node's outgoing edges list
			second: for (Edge e : n.getEdges()) {
				// if the edge's destination is a visited node, ignore this edge
				if (e.getDest().isVisited()) {
					continue second;
				} else {
					// if this edge's destination node's new minimum cost is
					// less than existing minimum cost
					if (e.getDest().getMinDist() > n.getMinDist() + e.getCost()) {
						// update the minimum cost
						e.getDest().setMinDist(n.getMinDist() + e.getCost());
						// set the immediate path node to be this edge's source
						// node
						e.getDest().setPath(n);
					}
					// update local minimum cost-by Dijkstra's Algorithm
					if (e.getDest().getMinDist() < min) {
						min = e.getDest().getMinDist();
						minNode = e.getDest();
					}
				}
			}
		}
		return minNode;
	}

	/**
	 * Topological sort Breadth-first-search
	 */
	public void tpSortBFS() {
		reset();
		ArrayList<Node> temp = new ArrayList<Node>();
		ArrayList<Node> sorted = new ArrayList<Node>();

		for (Node n : nodes) {
			temp.add(n);
		}
		while (!nodes.isEmpty()) {
			Node tempNode = findNode();
			if (tempNode == null) {
				System.out
						.println("Error: Found a cycle.Topological sort is interrupted.");
				nodes = temp;
				this.iscycle = true;
				updateIndegree();
				return;
			}
			sorted.add(tempNode);
			this.remove(tempNode);
		}
		nodes = sorted;
		updateIndegree();
	}

	/**
	 * find the node with indegree of 0-help in topological sort
	 * 
	 * @return
	 */
	private Node findNode() {
		for (Node n : this.nodes) {
			if (n.getIndegree() == 0)
				return n;
		}
		return null;
	}

	/**
	 * getter method
	 * 
	 * @return
	 */
	public ArrayList<Node> getNodes() {
		return this.nodes;
	}

	public void setNodes(ArrayList<Node> list) {
		this.nodes = list;
	}

	/**
	 * add a node to graph
	 * 
	 * @param label
	 * @return
	 */
	public Node addNode(String label) {
		if (lookup.containsKey(label))
			return lookup.get(label);

		Node newNode = new Node(label);
		newNode.setIndegree(0);
		lookup.put(label, newNode);
		nodes.add(newNode);

		return newNode;
	}

	/**
	 * Add edge and error checking
	 * 
	 * @param srcLabel
	 * @param destLabel
	 * @param cost
	 * @return
	 */
	public Edge addEdge(String srcLabel, String destLabel, int cost) {
		Node srcNode = addNode(srcLabel);
		Node destNode = addNode(destLabel);
		if (srcNode.contains(destNode))
			return null;

		Edge newEdge = new Edge(destNode, cost);
		srcNode.getEdges().add(newEdge);
		destNode.setIndegree(destNode.getIndegree() + 1);
		return newEdge;
	}

	/**
	 * simply remove the node from list
	 * 
	 * @param n
	 */
	public void remove(Node n) {
		this.nodes.remove(n);
	}

	/**
	 * deletion of individual villages. After the deletion:
	 * 
	 * (1)any roads that went through the village's route to other villages are
	 * direct (2)Roads cost are summed up
	 * 
	 * @param label
	 * @return
	 */
	public Node removeNode(String label) {
		Node temp = lookup.get(label);

		if (temp.getEdges().isEmpty()) {
			// remove all the edges to temp
			for (Node n : nodes) {
				n.removeEdge(temp);
			}
		} else {
			// Create temporary list for backup
			ArrayList<Node> tempDests = new ArrayList<Node>();
			ArrayList<Integer> tempCosts = new ArrayList<Integer>();
			for (Edge e : temp.getEdges()) {
				tempDests.add(e.getDest());
				tempCosts.add(e.getCost());
			}
			for (Node n : nodes) {
				if (n.contains(temp)) {
					int tempcost = n.removeEdge(temp).getCost();
					for (int i = 0; i < tempDests.size(); i++) {
						// add the integrated road with updated(sum) cost
						addEdge(n.getLabel(), tempDests.get(i).getLabel(),
								tempCosts.get(i) + tempcost);
					}
				}
			}
		}
		nodes.remove(temp);
		// update the indegree
		updateIndegree();
		System.out.println("Removed node " + temp.getLabel() + ".");
		return temp;

	}

	/**
	 * update the indegree of node on the graph after some calculations
	 * performed in tpsort method
	 */
	private void updateIndegree() {

		for (Node n : nodes) {
			n.setIndegree(0);
		}
		for (Node n : nodes) {
			for (Edge e : n.getEdges()) {

				e.getDest().setIndegree(e.getDest().getIndegree() + 1);

			}
		}
	}

	/**
	 * reset all the help properties in MST/TPsort/Shortest path methods
	 */
	public void reset() {
		this.iscycle = false;
		for (Node n : nodes) {
			n.reset();
			for (Edge e : n.getEdges()) {
				e.reset();
			}
		}
	}

	public String toString() {
		StringBuilder str = new StringBuilder();

		for (Node n : nodes) {
			str.append(n.getLabel() + ":");
			for (Edge e : n.getEdges()) {
				str.append(e.getDest().getLabel() + " ");

			}
			str.append(NEWLINE);
		}
		str.append("The order is: ");
		for (Node n : nodes) {
			str.append(n.getLabel() + " ");
		}
		return str.toString();

	}

	public void writeToFile(String fileName) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(fileName);

		for (Node n : nodes) {
			for (Edge e : n.getEdges()) {
				out.println(n.getLabel() + " " + e.getDest().getLabel() + " "
						+ e.getCost());
			}
		}

		out.close();
	}

	public void displayDotFile(String fileName) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(fileName);
		out.println("digraph G {");
		for (Node n : nodes) {
			for (Edge e : n.getEdges()) {

				out.println(n.getLabel() + "->" + e.getDest().getLabel()
						+ "[label=\"" + e.getCost() + "\"];");

			}
		}
		out.println("}");
		out.close();

	}

	public boolean isIscycle() {
		return iscycle;
	}

	public void setIscycle(boolean iscycle) {
		this.iscycle = iscycle;
	}

	public Map<String, Node> getLookUp() {
		return this.lookup;
	}

	/**
	 * test client code for directed graph
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		DirectedGraph g = new DirectedGraph();

		try (BufferedReader br = new BufferedReader(new FileReader("graph"))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				String[] str = sCurrentLine.split(" ");
				g.addEdge(str[0], str[1], Integer.parseInt(str[2]));
			}

		} catch (IOException e) {
			System.out.println("No such file.");
			return;
		}
		System.out.println(g);
		g.tpSortBFS();
		g.shortestPath("4", "1");
		g.MST("1");
		g.displayDotFile("g.dot");
	}
}
