package de.mindlessbloom.suffixtree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class StartJung {

	public static void main(String[] args) throws IOException {

		String wortTrennerRegAusdruck = " "; // Worttrenner (reg. Ausdruck)
		String satzStringSuffix = " $"; // Wird an jeden Satz angehangen, bevor
		// die Worttrennung durchgefuehrt wird.
		boolean graphikAusgabe = true;
		int maxAnzahlSaetzeZuBearbeiten = 0; // (n <= 0) == unbegrenzt
		// String dateiPfad =
		// "/Users/marcel/Magisterarbeit/schokolade-minikorpus-bereinigt.txt.gz";
		// // Ein
		// Satz pro Zeile, am besten ohne Zeichensetzung.
		String dateiPfad = "/Users/marcel/Magisterarbeit/oanc-komplett-zusammengelegt.txt.gz";

		// Datei einlesen
		System.out.println("Parse Eingabedatei " + dateiPfad);
		List<String> satzListe = PreParser.parse(dateiPfad);

		ArrayList<String[]> tokenArrayListe = new ArrayList<String[]>();

		Iterator<String> saetze = satzListe.iterator();
		System.out.println("Explodiere " + satzListe.size() + " Saetze");
		while (saetze.hasNext()) {
			String satz = saetze.next();

			tokenArrayListe.add(satz.concat(satzStringSuffix).split(
					wortTrennerRegAusdruck));
		}

		// Baumgraphen erstellen
		DelegateForest<TestNode, TestEdge> graph_walk = new DelegateForest<TestNode, TestEdge>();
		DelegateForest<TestNode, TestEdge> graph_run = new DelegateForest<TestNode, TestEdge>();
		DelegateForest<TestNode, TestEdge> graph_car = new DelegateForest<TestNode, TestEdge>();

		ArrayList<DelegateForest<TestNode, TestEdge>> graphen = new ArrayList<DelegateForest<TestNode, TestEdge>>();
		graphen.add(graph_walk);
		graphen.add(graph_run);
		graphen.add(graph_car);

		/*
		 * String[] token1 = new String[] { "cat", "ate", "cheese", "$" };
		 * String[] token2 = new String[] { "mouse", "ate", "cheese", "too", "$"
		 * }; String[] token3 = new String[] { "cat", "ate", "mouse", "too", "$"
		 * }; String[][] token = new String[][] { token1, token2, token3 };
		 */

		final TestNode wurzel_walk = new TestNode();
		final TestNode wurzel_run = new TestNode();
		final TestNode wurzel_car = new TestNode();

		graph_walk.setRoot(wurzel_walk);
		graph_run.setRoot(wurzel_run);
		graph_car.setRoot(wurzel_car);

		StartJung s = new StartJung();

		WortFilter wf_walk = new WortFilter();
		wf_walk.addWort("walking");
		WortFilter wf_run = new WortFilter();
		wf_run.addWort("running");
		WortFilter wf_car = new WortFilter();
		wf_car.addWort("car");

		/*
		 * // Alle Tokenarrays durchlaufen for (int j = 0; j < token.length;
		 * j++) {
		 * 
		 * // Alle Token des aktuellen Tokenarrays durchlaufen for (int i = 0; i
		 * < token[j].length; i++) { s.trieBuilder(Arrays.copyOfRange(token[j],
		 * i, token[j].length), wurzel, graph); } }
		 */

		s.konstruiereSuffixBaum(tokenArrayListe, maxAnzahlSaetzeZuBearbeiten,
				wf_walk, wurzel_walk, graph_walk);
		s.konstruiereSuffixBaum(tokenArrayListe, maxAnzahlSaetzeZuBearbeiten,
				wf_run, wurzel_run, graph_run);
		// s.konstruiereSuffixBaum(tokenArrayListe,
		// maxAnzahlSaetzeZuBearbeiten,wf_car, wurzel_car, graph_car);

		TestNode kind_walk = wurzel_walk.getKinder().get("walking");
		TestNode kind_run = wurzel_run.getKinder().get("running");
		TestNode kind_car = wurzel_car.getKinder().get("car");

		// TODO: Nur Knoten mit zu definierender Mindestanzahl von Kindern
		// ausgeben (graphisch).

		// TODO: KnotenKomparator fuer alle(?) Knoten durchfuehren. Evtl. auch
		// nur fuer solche, die irgendwie aehnlich sind (Kinderanzahl;
		// Beruehrungen durch Saetze, ... )

		KnotenKomparator3 kk = new KnotenKomparator3();

		TestNode walk_run = kk.verschmelzeBaeume(kind_walk, kind_run);
		
		int[] trefferwert0 = kk.ermittleKnotenTrefferwert(walk_run);
		System.out.println("Treffer0 walk_run_treffer:"+trefferwert0[0]+":"+trefferwert0[1]);
		
		TestNode walk_run_treffer = s.entferneNichtTrefferKnoten(walk_run,true);
		
		int[] trefferwert2 = kk.ermittleKnotenTrefferwert(walk_run_treffer);
		System.out.println("Treffer2 walk_run_treffer:"+trefferwert2[0]+":"+trefferwert2[1]);
		
		DelegateForest<TestNode, TestEdge> walk_run_treffer_graph = s
				.addGraphEdges(walk_run_treffer, null);
		graphen.clear();
		graphen.add(walk_run_treffer_graph);
		
		

		// Double vergleich_run_walk = kk.vergleiche(kind_run, kind_walk);
		// Double vergleich_walk_run = kk.vergleiche(kind_walk, kind_run); //
		// zur
		// Pruefung
		// Double vergleich_run_walk2 = kk.vergleiche(kind_run, kind_walk); //
		// ...
		// Double vergleich_car_walk = kk.vergleiche(kind_car, kind_walk);
		// Double vergleich_car_run = kk.vergleiche(kind_car, kind_run);
		// Double vergleich_walk_car = kk.vergleiche(kind_walk, kind_car);
		// Double vergleich_run_car = kk.vergleiche(kind_run, kind_car);

		// System.out.println("Vergleich run-walk:" + vergleich_run_walk);
		// System.out.println("Vergleich walk-run:" + vergleich_walk_run);
		// System.out.println("Vergleich run-walk 2:" + vergleich_run_walk2);
		// System.out.println("Vergleich car-walk:" + vergleich_car_walk);
		// System.out.println("Vergleich car-run:" + vergleich_car_run);
		// System.out.println("Vergleich walk-car:" + vergleich_walk_car);
		// System.out.println("Vergleich run-car:" + vergleich_run_car);

		if (graphikAusgabe) {

			Iterator<DelegateForest<TestNode, TestEdge>> graphenElemente = graphen
					.iterator();
			while (graphenElemente.hasNext()) {

				DelegateForest<TestNode, TestEdge> graph = graphenElemente
						.next();

				// The Layout<V, E> is parameterized by the vertex and edge
				// types
				// Layout<TestNode, TestEdge> layout = new TreeLayout<TestNode,
				// TestEdge>(graph, 50, 50);
				Layout<TestNode, TestEdge> layout = new RadialTreeLayout<TestNode, TestEdge>(
						graph, 50, 50);
				// layout.setSize(new Dimension(700, 700)); // sets the initial
				// size
				// of the space
				// The BasicVisualizationServer<V,E> is parameterized by the
				// edge
				// types
				// BasicVisualizationServer<TestNode, TestEdge> vv = new
				// BasicVisualizationServer<TestNode, TestEdge>(layout);
				VisualizationViewer<TestNode, TestEdge> vv = new VisualizationViewer<TestNode, TestEdge>(
						layout);
				vv.setPreferredSize(new Dimension(800, 800)); // Sets the
																// viewing
				// area size
				vv.getRenderContext().setVertexLabelTransformer(
						new ToStringLabeller<TestNode>());
				vv.getRenderContext().setEdgeLabelTransformer(
						new ToStringLabeller<TestEdge>());
				
				
				// Transformer maps the vertex number to a vertex property
		        Transformer<TestNode,Paint> vertexColor = new Transformer<TestNode,Paint>() {
		            public Paint transform(TestNode arg0) {
		                if(arg0.isMatch()) return Color.GREEN;
		                return Color.RED;
		            }
		        };
		        Transformer<TestNode,Shape> vertexSize = new Transformer<TestNode,Shape>(){
		            public Shape transform(TestNode arg0){
		                Ellipse2D circle = new Ellipse2D.Double(-15, -15, 30, 30);
		                // in this case, the vertex is twice as large
		                return AffineTransform.getScaleInstance(Math.log((double)arg0.getZaehler()+2d)/4d, Math.log((double)arg0.getZaehler()+2d)/4d).createTransformedShape(circle);
		            }
		        };
		        vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);
		        vv.getRenderContext().setVertexShapeTransformer(vertexSize);
				
				// Create a graph mouse and add it to the visualization
				// component
				DefaultModalGraphMouse<TestNode, TestEdge> gm = new DefaultModalGraphMouse<TestNode, TestEdge>();
				gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
				vv.setGraphMouse(gm);
				JFrame frame = new JFrame("Simple Graph View");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(vv);
				frame.pack();
				frame.setVisible(true);
			}

		}

	}

	/**
	 * Gibt einen neuen Baum zurueck ohne die Knoten, die nicht als Treffer
	 * markiert waren.
	 * 
	 * @param knoten
	 * @return
	 */
	public TestNode entferneNichtTrefferKnoten(TestNode knoten, boolean ignoriereErstenKnoten) {

		if (knoten == null || !(knoten.isMatch() || ignoriereErstenKnoten)) {
			return null;
		}

		TestNode neuerKnoten = new TestNode();
		neuerKnoten.setName(knoten.getName());
		neuerKnoten.setZaehler(knoten.getZaehler());
		neuerKnoten.setMatch(true);

		Iterator<String> kinder = knoten.getKinder().keySet().iterator();
		while (kinder.hasNext()) {
			String kindName = kinder.next();
			TestNode kind = entferneNichtTrefferKnoten(knoten.getKinder().get(
					kindName),false);
			if (kind != null)
				neuerKnoten.getKinder().put(kindName, kind);
		}

		return neuerKnoten;

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

	/**
	 * Gibt einen Graphen mit dem uebergebenen Baum zurueck
	 * 
	 * @param knoten
	 * @param graph
	 * @return
	 */
	public DelegateForest<TestNode, TestEdge> addGraphEdges(TestNode knoten,
			DelegateForest<TestNode, TestEdge> graph) {
		
		System.out.println("addGraphEdges:"+knoten.getName());

		if (graph == null) {
			graph = new DelegateForest<TestNode, TestEdge>();
			graph.setRoot(knoten);
		}

		Iterator<String> kinder = knoten.getKinder().keySet().iterator();
		while (kinder.hasNext()) {
			TestNode kind = knoten.getKinder().get(kinder.next());
			System.out.println("addGraphEdges:"+knoten.getName()+" --> "+kind.getName());
			TestEdge neueKante = new TestEdge(kind.getName().intern());
			graph.addEdge(neueKante, knoten, kind, EdgeType.DIRECTED);
			addGraphEdges(kind, graph);
		}

		return graph;
	}

	public void konstruiereSuffixBaum(ArrayList<String[]> tokenArrayListe,
			int maxAnzahlSaetzeZuBearbeiten, WortFilter wf, TestNode wurzel,
			DelegateForest<TestNode, TestEdge> graph) {
		// Saetze durchlaufen
		for (int j = 0; j < tokenArrayListe.size()
				&& (j < maxAnzahlSaetzeZuBearbeiten || maxAnzahlSaetzeZuBearbeiten <= 0); j++) {

			if (!wf.hatWort(tokenArrayListe.get(j))) {
				// System.out.println("Ueberspringe Satz " + (j + 1) + " von "+
				// tokenArrayListe.size() + ", Wort # ");
				continue;
			}
			System.out.print("Bearbeite Satz " + (j + 1) + " von "
					+ tokenArrayListe.size() + ": ");

			// Alle Token des aktuellen Satzes durchlaufen
			for (int i = 0; i < tokenArrayListe.get(j).length; i++) {

				System.out.print(tokenArrayListe.get(j)[i] + " ");

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
	}

	/*
	 * $ abbabbab$ abbabbab$ abbabbab$ abbabbab$ abbabbab$ abbabbab$ abbabbab$
	 * abbabbab$ 12345678
	 */
}
