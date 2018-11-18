package OpenGLTests;

import movement.mathDS.Graph;

public class GraphTest {

	public static void main(String[] args) {
	Graph<String, String> test = new Graph<String,String>();
	test.addVertex("hello");
	test.addVertex("goodbye");
	test.addEdge("hello", "goodbye");
	test.addEdge("goodbye", "hello");
	System.out.println(test.getUnsortedVerticesList());
	test.removeCycles();
	System.out.println(test.getSortedVertices());
	}

}
