package de.mindlessbloom.suffixtree;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class StartJung {

    public static void main(String[] args) throws IOException {

	String wortTrennerRegAusdruck = " "; // Worttrenner (reg. Ausdruck)
	String satzStringSuffix = " $"; // Wird an jeden Satz angehangen, bevor
					// die Worttrennung durchgefuehrt wird.
	boolean graphikAusgabe = false;
	int maxAnzahlSaetzeZuBearbeiten = 0; // (n <= 0) == unbegrenzt
	// String dateiPfad =
	// "/Users/marcel/Magister/schokolade-minikorpus-bereinigt.txt"; // Ein
	// Satz pro Zeile, am besten ohne Zeichensetzung.
	String dateiPfad = "/Users/marcel/Magister/oanc-komplett-zusammengelegt.txt";

	// Datei einlesen
	System.out.println("Parse Eingabedatei " + dateiPfad);
	List<String> satzListe = PreParser.parse(new File(dateiPfad));

	ArrayList<String[]> tokenArrayListe = new ArrayList<String[]>();

	Iterator<String> saetze = satzListe.iterator();
	System.out.println("Explodiere " + satzListe.size() + " Saetze");
	while (saetze.hasNext()) {
	    String satz = saetze.next();

	    tokenArrayListe.add(satz.concat(satzStringSuffix).split(
		    wortTrennerRegAusdruck));
	}

	// Baumgraphen erstellen
	DelegateForest<TestNode, TestEdge> graph = new DelegateForest<TestNode, TestEdge>();

	/*
	 * String[] token1 = new String[] { "cat", "ate", "cheese", "$" };
	 * String[] token2 = new String[] { "mouse", "ate", "cheese", "too", "$"
	 * }; String[] token3 = new String[] { "cat", "ate", "mouse", "too", "$"
	 * }; String[][] token = new String[][] { token1, token2, token3 };
	 */

	final TestNode wurzel = new TestNode();
	graph.setRoot(wurzel);

	StartJung s = new StartJung();

	WortFilter wf = new WortFilter();
	wf.addWort("walk");
	wf.addWort("run");
	wf.addWort("car");

	/*
	 * // Alle Tokenarrays durchlaufen for (int j = 0; j < token.length;
	 * j++) {
	 * 
	 * // Alle Token des aktuellen Tokenarrays durchlaufen for (int i = 0; i
	 * < token[j].length; i++) { s.trieBuilder(Arrays.copyOfRange(token[j],
	 * i, token[j].length), wurzel, graph); } }
	 */

	// Saetze durchlaufen
	for (int j = 0; j < tokenArrayListe.size()
		&& (j < maxAnzahlSaetzeZuBearbeiten || maxAnzahlSaetzeZuBearbeiten <= 0); j++) {

	    if (!wf.hatWort(tokenArrayListe.get(j))) {
		System.out.println("Ueberspringe Satz " + (j + 1) + " von "
			+ tokenArrayListe.size() + ", Wort # ");
		continue;
	    }
	    System.out.print("Bearbeite Satz " + (j + 1) + " von "
		    + tokenArrayListe.size() + ", Wort # ");

	    // Alle Token des aktuellen Satzes durchlaufen
	    for (int i = 0; i < tokenArrayListe.get(j).length; i++) {

		System.out.print(i + " ");

		// Suffix-Tree bauen
		BaumBauer b = new BaumBauer();
		b.baumBuilder(Arrays.copyOfRange(tokenArrayListe.get(j), i,
			tokenArrayListe.get(j).length), wurzel, graph, false);
	    }
	    System.out.println("fertig.");
	}

	// TODO: Knoten nach Kinderanzahl geordnet auflisten.
	TreeSet<TestNode> nodesGeordnet = new TreeSet<TestNode>(
		new NodeDurchlaufZaehlerComparator());
	// s.fuegeNodesInTreeSetEin(wurzel, nodesGeordnet); // Brauche nur die
	// Kinder der ersten Ebene
	Iterator<String> kinder = wurzel.getKinder().keySet().iterator();
	while (kinder.hasNext()) {
	    nodesGeordnet.add(wurzel.getKinder().get(kinder.next()));
	}

	Iterator<TestNode> nodes = nodesGeordnet.iterator();
	while (nodes.hasNext()) {
	    TestNode node = nodes.next();
	    System.out.println(node.getName()
		    + "\t Beruehrungen durch Saetze :" + node.getZaehler()
		    + "\t Kinder:" + node.getKinder().size());
	}
	// TODO: Nur Knoten mit zu definierender Mindestanzahl von Kindern
	// ausgeben (graphisch).

	// TODO: KnotenKomparator fuer alle(?) Knoten durchfuehren. Evtl. auch nur fuer solche, die irgendwie aehnlich sind (Kinderanzahl; Beruehrungen durch Saetze, ... )

	KnotenKomparator kk = new KnotenKomparator();
	
	int vergleich_run_walk = kk.vergleiche(wurzel.getKinder().get("run"), wurzel.getKinder().get("walk"));
	int vergleich_walk_run = kk.vergleiche(wurzel.getKinder().get("walk"), wurzel.getKinder().get("run")); // zur Pruefung
	int vergleich_run_walk2 = kk.vergleiche(wurzel.getKinder().get("run"), wurzel.getKinder().get("walk")); // ...
	int vergleich_car_walk = kk.vergleiche(wurzel.getKinder().get("car"), wurzel.getKinder().get("walk"));
	int vergleich_car_run = kk.vergleiche(wurzel.getKinder().get("car"), wurzel.getKinder().get("run"));
	int vergleich_walk_car = kk.vergleiche(wurzel.getKinder().get("walk"), wurzel.getKinder().get("car"));
	int vergleich_run_car = kk.vergleiche(wurzel.getKinder().get("run"), wurzel.getKinder().get("car"));
	
	System.out.println("Vergleich run-walk:"+vergleich_run_walk);
	System.out.println("Vergleich walk-run:"+vergleich_walk_run);
	System.out.println("Vergleich run-walk 2:"+vergleich_run_walk2);
	System.out.println("Vergleich car-walk:"+vergleich_car_walk);
	System.out.println("Vergleich car-run:"+vergleich_car_run);
	System.out.println("Vergleich walk-car:"+vergleich_walk_car);
	System.out.println("Vergleich run-car:"+vergleich_run_car);
	
	if (graphikAusgabe) {
	    // The Layout<V, E> is parameterized by the vertex and edge types
	    Layout<TestNode, TestEdge> layout = new TreeLayout<TestNode, TestEdge>(graph, 50, 50);
	    // layout.setSize(new Dimension(700, 700)); // sets the initial size
	    // of the space
	    // The BasicVisualizationServer<V,E> is parameterized by the edge
	    // types
	    BasicVisualizationServer<TestNode, TestEdge> vv = new BasicVisualizationServer<TestNode, TestEdge>(
		    layout);
	    vv.setPreferredSize(new Dimension(800, 800)); // Sets the viewing
							  // area size
	    vv.getRenderContext().setVertexLabelTransformer(
		    new ToStringLabeller<TestNode>());
	    vv.getRenderContext().setEdgeLabelTransformer(
		    new ToStringLabeller<TestEdge>());
	    JFrame frame = new JFrame("Simple Graph View");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.getContentPane().add(vv);
	    frame.pack();
	    frame.setVisible(true);
	}

    }

    public void fuegeNodesInTreeSetEin(TestNode wurzel,
	    TreeSet<TestNode> treeSet) {
	Iterator<String> kinder = wurzel.getKinder().keySet().iterator();
	while (kinder.hasNext()) {
	    fuegeNodesInTreeSetEin(wurzel.getKinder().get(kinder.next()),
		    treeSet);
	}
	treeSet.add(wurzel);
    }



    /*
     * $ abbabbab$ abbabbab$ abbabbab$ abbabbab$ abbabbab$ abbabbab$ abbabbab$
     * abbabbab$ 12345678
     */
}
