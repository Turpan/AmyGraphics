package movement.mathDS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

public class Graph<T, U> implements Cloneable{	//simple linkedList implementation of a directed graph, (Now with super hacked together edge values!!!)
	private ArrayList<LinkedList<T>> adjList;	//adjacenyList. A graph concept. A list of linked lists. Each vertex is the head of a linked list, and all the vertices it's connected to are also listed in the linked list.
	private ArrayList<Edge> edgeList;
	public Graph() {
		setAdjList(new ArrayList<LinkedList<T>>());
		setEdgeList(new ArrayList<Edge>());
	}
	public void setAdjList(ArrayList<LinkedList<T>> aL) {
		adjList = aL;
	}
	public ArrayList<LinkedList<T>> getAdjList(){
		return adjList;
	}
	public void addVertex(T vertex) {
		LinkedList<T> tmp = new LinkedList<T>();
		tmp.add(vertex);
		getAdjList().add(tmp);
	}
	public void addVertices (Collection<? extends T> c) {
		for (T vertex : c) {
			addVertex(vertex);
		}
	}
	public void removeVertex(T vertex) {
		LinkedList<T> LLToRemove = null;
		for (LinkedList<T> LL : getAdjList()) {
			if (LL.peek() == vertex) {
				LLToRemove = LL;
			}else {
			removeEdge(LL.peek(), vertex);
			}
		}
		getAdjList().remove(LLToRemove);
	}
	public ArrayList<T> getUnsortedVerticesList(){
		ArrayList<T> output = new ArrayList<T>(); 
		for (LinkedList<T> aL :getAdjList()){
			output.add(aL.peek());
		}
		return output;
	}	
	public LinkedList<T> getVertexLinkedList(T vertex) {	//NOTENOTENOTE!!!!: This actually gives you the linked list for the vertex. The head of this /is/ the vertex itself!
		for (LinkedList<T> LL : getAdjList()) {
			if (LL.peek() == vertex) {
				return LL;
			}
		}
		return null;
	}
	public ArrayList<T> getVertexConnections(T vertex){
		ArrayList<T> output = new ArrayList<T>();
		var iter = getVertexLinkedList(vertex).listIterator(1);
		while(iter.hasNext()){
			output.add(iter.next());
		}
		return output;
	}
	public ArrayList<T> getVerticesConnectedTo(T vertex){
		ArrayList<T> output= new ArrayList<T>();
		for (Edge contact :getEdgeList()) {
			if (contact.getDest() == vertex) {
				output.add(contact.getSource());
			}
		}
		return output;
	}
	
	public void addEdge(T from, T to, U value) {
		getVertexLinkedList(from).add(to);
		getEdgeList().add(new Edge(from, to, value));
	}
	public void addEdge(T from, T to) {
		addEdge(from, to, null);
	}
	public void removeEdge(T from, T to) {
		for (LinkedList<T> LL : getAdjList()) {
			if (LL.peek() == from) {
				LL.remove(to);
			}
		}
		getEdgeList().remove(new Edge (from, to));
	}
	public U getEdgeValue(T from, T to) {
		return getEdgeList().get(getEdgeList().indexOf(new Edge(from, to))).getValue();
	}
    private ArrayList<Edge> getEdgeList() {
		return edgeList;
	}
	private void setEdgeList(ArrayList<Edge> edgeList) {
		this.edgeList = edgeList;
	}
	@Override
	public Graph<T,U> clone() {
		var output = new Graph<T, U>();
		for (LinkedList<T> LL : getAdjList()) {
			output.addVertex(LL.peek());
		}for (Edge edge : getEdgeList()) {
			output.addEdge(edge.getSource(), edge.getDest(), edge.getValue());
		}
		return output;
	}
	
	public void prune() {//Remove Leaves! :D ALso, removes any vertices that become leaves when the leaves are removed, etc.
		boolean hasLeaf = true;
		ArrayList<T> leaves = new ArrayList<T>();
		while (getAdjList().size() != 0 && hasLeaf == true) {
			hasLeaf = false;
			
			for (LinkedList<T> LL :getAdjList()) {
				if (LL.size() == 1) {
					hasLeaf = true;
					leaves.add(LL.peek());
				}
				
			}
			for (T leaf : leaves) {
				removeVertex(leaf);
			}
			leaves.clear();
		}
	}
	public boolean isAcyclic() {
		var testGraph = clone();
		boolean hasLeaf = true;
		ArrayList<T> leaves = new ArrayList<T>();
		while (testGraph.getAdjList().size() != 0 && hasLeaf == true) {
			hasLeaf = false;
			
			for (LinkedList<T> LL : testGraph.getAdjList()) {
				if (LL.size() == 1) {
					hasLeaf = true;
					leaves.add(LL.peek());
				}
				
			}
			for (T leaf : leaves) {
				testGraph.removeVertex(leaf);
			}
			leaves.clear();
			
		}
		return hasLeaf;
	}
	public boolean isConnected(T from, T to) {
		boolean visited[] = new boolean[getAdjList().size()];
		LinkedList<T> queue = new LinkedList<T>();
		visited[getAdjList().indexOf(getVertexLinkedList(from))] = true;
		queue.add(from);
		while (queue.size() != 0) {
			from = queue.poll();
			for (T vertex : getVertexLinkedList(from)) {
				if (vertex == to) {
					return true;
				}
				var vertexIndex = getAdjList().indexOf(getVertexLinkedList(vertex)); 	
				if (!visited[vertexIndex]) {
					visited[vertexIndex] = true;
					queue.add(vertex);
				}
			}
		}
		return false;
	}
	public boolean isDoubleBonded(T from, T to) {
		return (getVertexLinkedList(from).contains(to) && getVertexLinkedList(to).contains(from));
	}
   
    private void getSortedVerticesUtil(T vertex, boolean visited[], Stack<T> stack){ // A recursive function used by topologicalSort
        // Mark the current node as visited.
        visited[getAdjList().indexOf(getVertexLinkedList(vertex))] = true;

        // Recur for all the vertices adjacent to this vertex
        var it = getVertexLinkedList(vertex).listIterator(1);
        T v;
        while (it.hasNext()){
        	v = it.next();
            if (!visited[getAdjList().indexOf(getVertexLinkedList(v))]) {
                getSortedVerticesUtil(v, visited, stack);
            }
        }
        // Push current vertex to stack which stores result
        stack.push(vertex);
        
    }
 
    // The function to do Topological Sort. It uses recursive getSortedVerticesUtil()
    public ArrayList<T> getSortedVertices(){		//returns sorted list of vertices, where vertex A > B if A has an edge to B, returns null if cyclic
    	ArrayList<T> output = null;					//Code a refactord version of the algorithm for topological sorting on GeeksforGeeks.org! All credit goes to them!
    	if (isAcyclic()) {
	        Stack<T> stack = new Stack<T>();
	 
	        // Mark all the vertices as not visited
	        boolean visited[] = new boolean[getAdjList().size()];
	        // Call the recursive helper function to store Topological Sort starting from all vertices one by one
	        for (int i = 0; i < getAdjList().size(); i++) {
	            if (!visited[i])
	                getSortedVerticesUtil(getAdjList().get(i).peek(), visited, stack);
    		}
	        output = new ArrayList<T>(stack);
//	        Collections.reverse(output);	reverses the direction. I dunno if I might want to change the direction of this later.... I'm just gonna leave it commented out.
    	}
    	return output;
	}
    public void removeCycles() { 	
    //forces the graph to become acyclic. For vertices A, B if Ǝ a path from A to B before this method,
    //I /try/ to maintain a path after the method (for distinct A,B), but sometimes, like in a simple ring, such a path
	//is inherently cyclical, amd the path is destroyed, rather than leaving in a ring
    	if (!isAcyclic()) {
			var testGraph = clone();
			testGraph.prune();
	    	T tmp1;
	    	T tmp2;
	    	U tmp12Value;
	    	int i;
	    	while (testGraph.getAdjList().size() > 1 && !testGraph.isAcyclic()) {
	    		i = 1;
	    		tmp1 = testGraph.getAdjList().get(0).peek();
	    		tmp2 = testGraph.getAdjList().get(0).get(i);
	    		tmp12Value = testGraph.getEdgeValue(tmp2, tmp1);
				testGraph.removeEdge(tmp1, tmp2);	//remove an arbitrary edge. This one is guaranteed to exist, so long as the graph is pruned & size > 1;
				if (!testGraph.isConnected(tmp1, tmp2) && i + 1 < testGraph.getAdjList().get(0).size()){
					testGraph.addEdge(tmp1, tmp2, tmp12Value);	//sometimes removing an arbitrary edge will break paths. I can't let that happen, so this. It's inefficient, but I frankly don't know enough about graphs to know if there was an analytic solution .
					i++;
				} else {
					removeEdge(tmp1, tmp2);
					testGraph.prune();
				}
	    	}
    	}
    	
    }
    public String toString() {
    	var output = "";
    	String tmp;
    	for (LinkedList<T> LL : getAdjList()) {
    		tmp = "";
    		tmp += LL.peek().toString() + " :  ";
    		for (T v :getVertexConnections(LL.peek())){
    			tmp += v.toString() + ", ";
    		}
    		output += tmp.substring(0,tmp.length()-2) + "... ";
    	}
    	return output;
    }

	private class Edge{
    	@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((dest == null) ? 0 : dest.hashCode());
			result = prime * result + ((source == null) ? 0 : source.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			Edge other = (Edge) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (dest == null) {
				if (other.dest != null)
					return false;
			} else if (!dest.equals(other.dest))
				return false;
			if (source == null) {
				if (other.source != null)
					return false;
			} else if (!source.equals(other.source))
				return false;
			return true;
		}
		private T source;
    	private T dest;
    	private U value;
    	
    	public Edge (T source, T dest) {
    		this(source, dest, null);
    	}
    	
    	public Edge (T source, T dest, U value) {
    		setSource(source);
    		setDest(dest);
    		setValue(value);
    	}

		public T getSource() {
			return source;
		}

		public void setSource(T source) {
			this.source = source;
		}

		public T getDest() {
			return dest;
		}

		public void setDest(T dest) {
			this.dest = dest;
		}

		public U getValue() {
			return value;
		}

		public void setValue(U value) {
			this.value = value;
		}
		private Graph<T, U> getOuterType() {
			return Graph.this;
		}
    }
}
