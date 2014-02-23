package de.mindlessbloom.suffixtree;

import edu.uci.ics.jung.graph.DelegateTree;


public class Test {

	public static void main(String[] args) {
		
		String[] eingabe = new String[]{"Erwin mag Anna $",
		"Thomas mag Anna auch $",
		"mag Anna auch Thomas $"};
		
		BaumBauer b = new BaumBauer();
		Knoten wurzel = new Knoten();
		wurzel.setName("^");
		DelegateTree<Knoten, Kante> graph = new DelegateTree<Knoten, Kante>();
		graph.setRoot(wurzel);
		for (int i=0; i<eingabe.length; i++){
			b.baueBaum(eingabe[i].split(" "), wurzel, graph, false);
		}
		
		
		GraphenPlotter g = new GraphenPlotter();
		
		g.plot(graph, 2);

	}

}
