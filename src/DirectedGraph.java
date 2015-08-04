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
 *
 * 
 * @author rebeccahong
 *
 */
public class DirectedGraph {
	private static final String NEWLINE = System.getProperty("line.separator");
	private ArrayList<Node> nodes = new ArrayList<Node>();
	private Map<String, Node> lookup = new HashMap<String, Node>();
	private boolean iscycle = false;
	
	public ArrayList<Node> getNodes(){
		return this.nodes;
	}
	/**
	 *Shortest Path- Dijkstra's Algorithm implementation
	 * 
	 * @param startLabel
	 * @param destLabel
	 * @return an arrayList of nodes on the shortest path
	 */
	public synchronized ArrayList<Node> shortestPath(String startLabel, String destLabel) {
		reset();
		Node start = lookup.get(startLabel);
		Node dest = lookup.get(destLabel);
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
		//print result to console
		System.out.println("Village\tCost\tVisited\tPath\tPathes list");
		for(Node n:tempNodes){
			if(n.isVisited()){
				updatePath(start,n,n.getPaths());
				//the paths figured out in the recursive updatePath() method are in a reverse order
				Collections.reverse(n.getPaths());
				//add the node itself for GUI's convenience 
				n.getPaths().add(n);
			}
		}
		for (int i = 0; i < tempNodes.size(); i++) {
			System.out.println(nodes.get(i).getLabel() + "\t"
					+ nodes.get(i).getMinDist() + "\t"
					+ nodes.get(i).isVisited()+"\t"+nodes.get(i).getPath()+"\t"+nodes.get(i).getPaths().toString());
		}
		ArrayList<Node> paths = new ArrayList<Node>();
		for(Node n:tempNodes){
			if(n==dest){
				paths= n.getPaths();
			}
		}
		return paths;
	}
	/**
	 * Recursive function to find all the nodes on the shortest path for GUI Display
	 * @param start
	 * @param target
	 * @param paths
	 */
	private void updatePath(Node start, Node target, ArrayList<Node> paths){
		if(target==start) return;
		for(Node n:nodes){
			if(n==target.getPath()){
				paths.add(n);
				updatePath(start,n,paths);
			}
		}
	}
	/**
	 * Assistance method in Dijkstra's algorithm
	 * 
	 * @param src
	 * @return
	 */
	private Node getMinDest(ArrayList<Node> src) {
		int min = Integer.MAX_VALUE;
		Node minNode = null;
		for (Node n : src) {
			second: for (Edge e : n.getEdges()) {
				if (e.getDest().isVisited()) {
					continue second;
				} else {
					if (e.getDest().getMinDist() > n.getMinDist() + e.getCost()) {
						e.getDest().setMinDist(n.getMinDist() + e.getCost());
						e.getDest().setPath(n);
					}
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
		updateIndegree();
		ArrayList<Node> temp = new ArrayList<Node>();
		ArrayList<Node> sorted = new ArrayList<Node>();

		for (Node n : nodes) {
			temp.add(n);
		}
		while (!nodes.isEmpty()) {
			Node tempNode = findNode();
			if (tempNode == null) {
				System.out.println("Error: Found a cycle.");
				nodes = temp;
				this.iscycle=true;
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
	 * @return
	 */
	private Node findNode() {
		for (Node n : this.nodes) {
			if (n.getIndegree() == 0)
				return n;
		}
		return null;
	}

	public Node addNode(String label) {
		if (lookup.containsKey(label))
			return lookup.get(label);

		Node newNode = new Node(label);
		newNode.setIndegree(0);
		lookup.put(label, newNode);
		nodes.add(newNode);

		return newNode;
	}

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
	 * @param n
	 */
	public void remove(Node n){
		this.nodes.remove(n);
	}
	/**
	 * deletion of individual villages. After the deletion:
	 * 
	 * (1)any roads that went through the village's route to other villages are direct
	 * (2)Roads cost are summed up
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
			//Create temporary list for backup
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
						//add the integrated road with updated(sum) cost
						addEdge(n.getLabel(), tempDests.get(i).getLabel(),
								tempCosts.get(i) + tempcost);
					}
				}
			}
		}
		nodes.remove(temp);
		//update the indegree
		updateIndegree();
		System.out.println("Removed node " + temp.getLabel() + ".");
		return temp;

	}
	/**
	 * update the indegree of node on the graph after some calculations performed in tpsort method
	 */
	private void updateIndegree() {
		this.iscycle=false;
		for (Node n : nodes) {
			n.setIndegree(0);
		}
		for (Node n : nodes) {
			for (Edge e : n.getEdges()) {

				e.getDest().setIndegree(e.getDest().getIndegree() + 1);

			}
		}
	}
	public void reset(){
		for(Node n:nodes){
			n.reset();
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
	
	public static void main(String[] args){
	   DirectedGraph g = new DirectedGraph();
 		System.out.println("Enter the name of input file:");
 		Scanner sc = new Scanner(System.in);
 		String fileName = sc.nextLine();
 		sc.close();
 		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

 			String sCurrentLine;

 			while ((sCurrentLine = br.readLine()) != null) {
 				String[] str = sCurrentLine.split(" ");
 				g.addEdge(str[0], str[1], Integer.parseInt(str[2]));
 			}

 		} catch (IOException e) {
 			System.out.println("No such file.");
 			return;
 		}
 		g.tpSortBFS();
 		System.out.println(g);
 		g.shortestPath("1", "8");
 		System.out.println(g);
 		
	}
	public boolean isIscycle() {
		return iscycle;
	}
	public void setIscycle(boolean iscycle) {
		this.iscycle = iscycle;
	}

	
}
