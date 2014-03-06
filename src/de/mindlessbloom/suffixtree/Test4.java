package de.mindlessbloom.suffixtree;

import java.util.Arrays;

import de.mindlessbloom.suffixtree.experiment01.KnotenKomparator;
import edu.uci.ics.jung.graph.DelegateTree;


public class Test4 {

	public static void main(String[] args) {
		
		String[] eingabe = new String[]{
				"^ Erwin mag Anna . $",
				"^ Anna mag Thomas , aber Anna mag nicht Erwin . $",
				"^ Thomas mag Anna auch . $",
				"^ mag Anna auch Thomas ? $"
		};
		
		String[] eingabe2 = new String[]{"Erwin liebt Anna .",
				"Thomas liebt Anna auch .",
				"liebt Anna auch Thomas ?"};
		
		BaumBauer b = new BaumBauer();
		WortFilter wf = new WortFilter();
		wf.addWort("mag");
		Knoten wurzel = new Knoten();
		wurzel.setName("suffixe von \"mag\"");
		DelegateTree<Knoten, Kante> graph = new DelegateTree<Knoten, Kante>();
		graph.setRoot(wurzel);
		Knoten wurzel2 = new Knoten();
		wurzel2.setName("praefixe von \"mag\"");
		DelegateTree<Knoten, Kante> graph2 = new DelegateTree<Knoten, Kante>();
		graph2.setRoot(wurzel2);
		for (int i=0; i<eingabe.length; i++){
			
			String[] worte = eingabe[i].split(" ");
			Integer[] indices = wf.getWortIndices(worte);
			
			for (int j=0; j<indices.length; j++){
				b.baueTrie(Arrays.copyOfRange(worte, indices[j]+1, worte.length), wurzel, graph, false);
				b.baueTrie(Arrays.copyOfRange(worte, 0, indices[j]), wurzel2, graph2, true);
			}
			
		}
		//KnotenKomparator kk = new KnotenKomparator();
		
		GraphenPlotter g = new GraphenPlotter();
		
		g.plot(graph, 2);
		g.plot(graph2, 2);
		

	}

}
