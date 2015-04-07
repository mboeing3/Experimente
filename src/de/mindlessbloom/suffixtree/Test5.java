package de.mindlessbloom.suffixtree;

import de.mindlessbloom.suffixtree.experiment01_04.BaumBauer;
import de.mindlessbloom.suffixtree.experiment01_04.Kante;
import de.mindlessbloom.suffixtree.experiment01_04.Knoten;
import de.mindlessbloom.suffixtree.experiment01_04.WortFilter;
import de.mindlessbloom.suffixtree.junggraph.GraphenPlotterEinfach;
import edu.uci.ics.jung.graph.DelegateTree;


public class Test5 {

	public static void main(String[] args) {
		
		String[][] eingabe = new String[][]{
				new String[]{"Brot"}
		};
		
		/*String[] eingabe2 = new String[]{"Erwin liebt Anna .",
				"Thomas liebt Anna auch .",
				"liebt Anna auch Thomas ?"};*/
		
		BaumBauer b = new BaumBauer();
		//WortFilter wf = new WortFilter();
		//wf.addWort("backt");
		Knoten wurzel = new Knoten();
		wurzel.setName("suffix--tree");
		DelegateTree<Knoten, Kante> graph = new DelegateTree<Knoten, Kante>();
		graph.setRoot(wurzel);
		for (int i=0; i<eingabe.length; i++){
			
			String[] worte = eingabe[i];
			//Integer[] indices = wf.getWortIndices(worte);
			
			//for (int j=0; j<indices.length; j++){
				b.baueTrie(worte, wurzel, graph, false);
				//b.baueBaum(worte, wurzel, graph, false);
			//}
			
		}
		//KnotenKomparator kk = new KnotenKomparator();
		
		GraphenPlotterEinfach g = new GraphenPlotterEinfach();
		
		g.plot(graph, 2);
		//g.plot(graph2, 2);
		
		//UbigraphClient ugraph = new UbigraphClient("http://192.168.99.52:20738/RPC2");

		//ugraph.clear();
		
		
		//b.konstruiereUbiGraph(wurzel, ugraph);
		  
		

	}

}
