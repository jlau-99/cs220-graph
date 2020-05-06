package graph.impl;

import java.util.*;

import graph.IGraph;
import graph.INode;
import graph.NodeVisitor;

/**
 * A basic representation of a graph that can perform BFS, DFS, Dijkstra, and
 * Prim-Jarnik's algorithm for a minimum spanning tree.
 * 
 * @author jspacco
 *
 */
public class Graph implements IGraph {
	private Map<String, INode> nodes = new HashMap<>();

	/**
	 * Return the {@link Node} with the given name.
	 * 
	 * If no {@link Node} with the given name exists, create a new node with the
	 * given name and return it. Subsequent calls to this method with the same name
	 * should then return the node just created.
	 * 
	 * @param name
	 * @return
	 */
	public INode getOrCreateNode(String name) {
		if (nodes.containsKey(name)) {
			return nodes.get(name);
		}
		INode n = new Node(name);
		nodes.put(name, n);
		return n;
	}

	/**
	 * Return true if the graph contains a node with the given name, and false
	 * otherwise.
	 * 
	 * @param name
	 * @return
	 */
	public boolean containsNode(String name) {
		return nodes.containsKey(name);
	}

	/**
	 * Return a collection of all of the nodes in the graph.
	 * 
	 * @return
	 */
	public Collection<INode> getAllNodes() {
		return nodes.values();
	}

	public int size() {
		return nodes.size();
	}

	/**
	 * Perform a breadth-first search on the graph, starting at the node with the
	 * given name. The visit method of the {@link NodeVisitor} should be called on
	 * each node the first time we visit the node.
	 * 
	 * 
	 * @param startNodeName
	 * @param v
	 */
	public void breadthFirstSearch(String startNodeName, NodeVisitor v) {
		Set<INode> visited = new HashSet<>();
		Queue<INode> toVisit = new LinkedList<>();
		toVisit.add(nodes.get(startNodeName));
		while (!toVisit.isEmpty()) {
			INode x = toVisit.remove();
			if (visited.contains(x))
				continue;
			v.visit(x);
			visited.add(x);
			for (INode n : x.getNeighbors()) {
				if (!visited.contains(n))
					toVisit.add(n);
			}
		}
	}

	/**
	 * Perform a depth-first search on the graph, starting at the node with the
	 * given name. The visit method of the {@link NodeVisitor} should be called on
	 * each node the first time we visit the node.
	 * 
	 * 
	 * @param startNodeName
	 * @param v
	 */
	public void depthFirstSearch(String startNodeName, NodeVisitor v) {
		Set<INode> visited = new HashSet<>();
		Stack<INode> toVisit = new Stack<>();
		toVisit.add(nodes.get(startNodeName));
		while (!toVisit.isEmpty()) {
			INode x = toVisit.pop();
			if (visited.contains(x))
				continue;
			v.visit(x);
			visited.add(x);
			for (INode n : x.getNeighbors()) {
				if (!visited.contains(n))
					toVisit.add(n);
			}
		}
	}

	/**
	 * Perform Dijkstra's algorithm for computing the cost of the shortest path to
	 * every node in the graph starting at the node with the given name. Return a
	 * mapping from every node in the graph to the total minimum cost of reaching
	 * that node from the given start node.
	 * 
	 * <b>Hint:</b> Creating a helper class called Path, which stores a destination
	 * (String) and a cost (Integer), and making it implement Comparable, can be
	 * helpful. Well, either than or repeated linear scans.
	 * 
	 * @param startName
	 * @return
	 */
	class Path implements Comparable {
		public int cost;
		public String dest;

		public Path(String s, int i) {
			dest = s;
			cost = i;
		}

		@Override
		public int compareTo(Object o) {
			Path p = (Path) o;
			return cost - p.cost;
		}
	}

	public Map<INode, Integer> dijkstra(String startName) {
		Map<INode, Integer> result = new HashMap<>();
		PriorityQueue<Path> todo = new PriorityQueue<>();
		todo.add(new Path(startName, 0));
		while (result.size() < nodes.size()) {
			Path nextPath = todo.remove();
			INode node = nodes.get(nextPath.dest);
			if (result.containsKey(node))
				continue;
			int cost = nextPath.cost;
			result.put(node, cost);
			for (INode n : node.getNeighbors()) {
				todo.add(new Path(n.getName(), cost + node.getWeight(n)));
			}
		}

		return result;
	}

	/**
	 * Perform Prim-Jarnik's algorithm to compute a Minimum Spanning Tree (MST).
	 * 
	 * The MST is itself a graph containing the same nodes and a subset of the edges
	 * from the original graph.
	 * 
	 * @return
	 */
	class Edge implements Comparable {
		public INode A, B;
		public int w;

		public Edge(INode n1, INode n2, int i) {
			A = n1;
			B = n2;
			w = i;
		}

		@Override
		public int compareTo(Object o) {
			Edge e = (Edge) o;
			return w - e.w;
		}
	}

	public IGraph primJarnik() {
		IGraph result = new Graph();
		INode start = (INode) nodes.values().toArray()[0];
		result.getOrCreateNode(start.getName());
		Edge e;
		Set<INode> solved = new HashSet<>();
		PriorityQueue<Edge> frontier = new PriorityQueue<>();
		while (result.getAllNodes().size() < nodes.size()) {
			solved.add(start);
			for (INode n : start.getNeighbors()) {
				if (!solved.contains(n))
					frontier.add(new Edge(start, n, start.getWeight(n)));
			}
			while (solved.contains(frontier.peek().B))
				e = frontier.remove();
			e = frontier.remove();
			result.getOrCreateNode(e.B.getName()).addUndirectedEdgeToNode(result.getOrCreateNode(e.A.getName()), e.w);
			start = e.B;
		}
		return result;
	}

}