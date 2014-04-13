import org.ubiety.ubigraph.UbigraphClient;

public class Example {

	public static void main(String[] args) {
		UbigraphClient graph = new UbigraphClient(
				"http://192.168.99.52:20738/RPC2");

		graph.clear();
		

		int N = 10;
		int[] vertices = new int[N];

		for (int i = 0; i < N; ++i) {
			vertices[i] = graph.newVertex();
			//graph.setVertexAttribute(vertices[i], "fontfamily", "Times Roman");
			//graph.setVertexAttribute(vertices[i], "fontsize", "14");
			graph.setVertexAttribute(vertices[i], "label", "Hallo"+i);
			graph.setVertexAttribute(vertices[i], "color", "#ff0000");
		}

		for (int i = 0; i < N; ++i) {
			int edgeId = graph.newEdge(vertices[i], vertices[(i + 1) % N]);
			graph.setEdgeAttribute(edgeId, "oriented", "true");
		}

	}

}

