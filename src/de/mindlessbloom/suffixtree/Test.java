package de.mindlessbloom.suffixtree;

import de.mindlessbloom.suffixtree.experiment01.KnotenKomparator;
import edu.uci.ics.jung.graph.DelegateTree;


public class Test {

	public static void main(String[] args) {
		
		String[] eingabe = new String[]{"Erwin mag Anna .",
		"Thomas mag Anna auch .",
		"mag Anna auch Thomas ?"};
		
		String[] eingabe2 = new String[]{"Erwin liebt Anna .",
				"Thomas liebt Anna auch .",
				"liebt Anna auch Thomas ?"};
		
		BaumBauer b = new BaumBauer();
		Knoten wurzel = new Knoten();
		Knoten wurzel2 = new Knoten();
		wurzel.setName("^");
		wurzel2.setName("^");
		DelegateTree<Knoten, Kante> graph = new DelegateTree<Knoten, Kante>();
		graph.setRoot(wurzel);
		DelegateTree<Knoten, Kante> graph2 = new DelegateTree<Knoten, Kante>();
		graph2.setRoot(wurzel2);
		for (int i=0; i<eingabe.length; i++){
			//b.baueBaum(eingabe[i].split(" "), wurzel, graph, false);
			b.baueBaum(eingabe[i].split(" "), wurzel, graph, false);
		}
		for (int i=0; i<eingabe2.length; i++){
			//b.baueBaum(eingabe2[i].split(" "), wurzel2, graph2, false);
			b.baueBaum(eingabe2[i].split(" "), wurzel2, graph2, false);
		}
		KnotenKomparator kk = new KnotenKomparator();
		Knoten vergleichsbaum = kk.verschmelzeBaeume(wurzel, wurzel2);
		
		// Uebereinstimmungswerte ermitteln
		Double[] trefferWert = kk.ermittleKnotenTrefferwert(vergleichsbaum);
		
		Double d = trefferWert[0] / trefferWert[1];
		
		System.out.println("Treffer: "+trefferWert[0] +"/"+ trefferWert[1] +" ("+d+").");
		
		GraphenPlotter g = new GraphenPlotter();
		
		g.plot(b.konstruiereGraph(wurzel), 2);
		g.plot(b.konstruiereGraph(wurzel2), 2);
		g.plot(b.konstruiereGraph(vergleichsbaum), 2);

	}

}
