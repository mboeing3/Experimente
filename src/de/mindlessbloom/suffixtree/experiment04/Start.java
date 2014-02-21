package de.mindlessbloom.suffixtree.experiment04;

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

import de.mindlessbloom.suffixtree.BaumBauer;
import de.mindlessbloom.suffixtree.Kante;
import de.mindlessbloom.suffixtree.Knoten;
import de.mindlessbloom.suffixtree.KnotenDurchlaufZaehlerKomparator;
import de.mindlessbloom.suffixtree.PreParser;
import de.mindlessbloom.suffixtree.WortFilter;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class Start {

	public static void main(String[] args) throws IOException {

		/**
		 * Variablen definieren
		 */
		
		// Worttrenner (reg. Ausdruck)
		String wortTrennerRegAusdruck = " ";
		
		// Wird an jeden Satz des Korpus angehangen, bevor die Worttrennung durchgefuehrt wird.
		String satzStringSuffix = " $";
		
		// Graphische Ausgabe des Graphen
		boolean graphikAusgabe = true;
		
		// Maximal vom Korpus zu verarbeitende Saetze. (n <= 0) == unbegrenzt
		int maxAnzahlSaetzeZuBearbeiten = 0;
		
		// Pfad zur komprimierten Korpusdatei.
		String dateiPfad = "/Users/marcel/Magisterarbeit/oanc-komplett-zusammengelegt.txt.gz";

		/**
		 * Korpus einlesen
		 */
		
		// Datei einlesen und grundlegend parsen (Ausgabeformat: Satzliste).
		System.out.println("Parse Eingabedatei " + dateiPfad);
		List<String> satzListe = PreParser.parse(dateiPfad);

		// Liste fuer TokenArrays (== Ketten von Einzelworten)
		ArrayList<String[]> tokenArrayListe = new ArrayList<String[]>();

		// Eingelesene Saetze durchlaufen
		Iterator<String> saetze = satzListe.iterator();
		System.out.println("Explodiere " + satzListe.size() + " Saetze");
		while (saetze.hasNext()) {
			
			// Naechsten Satz ermitteln
			String satz = saetze.next();

			// Satz explodieren und Ergebnis zur TokenArray-Liste hinzufuegen 
			tokenArrayListe.add(satz.concat(satzStringSuffix).split(
					wortTrennerRegAusdruck));
		}

		// Baumgraphen erstellen
		DelegateForest<Knoten, Kante> graph_walk = new DelegateForest<Knoten, Kante>();
		DelegateForest<Knoten, Kante> graph_run = new DelegateForest<Knoten, Kante>();
		DelegateForest<Knoten, Kante> graph_car = new DelegateForest<Knoten, Kante>();

		ArrayList<DelegateForest<Knoten, Kante>> graphen = new ArrayList<DelegateForest<Knoten, Kante>>();
		graphen.add(graph_walk);
		graphen.add(graph_run);
		graphen.add(graph_car);

		// Wurzelknoten erstellen
		final Knoten wurzel_walk = new Knoten();
		final Knoten wurzel_run = new Knoten();
		final Knoten wurzel_car = new Knoten();

		graph_walk.setRoot(wurzel_walk);
		graph_run.setRoot(wurzel_run);
		graph_car.setRoot(wurzel_car);

		Start s = new Start();

		WortFilter wf_walk = new WortFilter();
		wf_walk.addWort("walking");
		WortFilter wf_run = new WortFilter();
		wf_run.addWort("running");
		WortFilter wf_car = new WortFilter();
		wf_car.addWort("baking");

		/*
		 * // Alle Tokenarrays durchlaufen for (int j = 0; j < token.length;
		 * j++) {
		 * 
		 * // Alle Token des aktuellen Tokenarrays durchlaufen for (int i = 0; i
		 * < token[j].length; i++) { s.trieBuilder(Arrays.copyOfRange(token[j],
		 * i, token[j].length), wurzel, graph); } }
		 */
		
		// BaumBauer erstellen
		BaumBauer baumBauer = new BaumBauer();

		s.konstruiereSuffixBaum(tokenArrayListe, maxAnzahlSaetzeZuBearbeiten,
				wf_walk, wurzel_walk, graph_walk, baumBauer);
		s.konstruiereSuffixBaum(tokenArrayListe, maxAnzahlSaetzeZuBearbeiten,
				wf_run, wurzel_run, graph_run, baumBauer);
		// s.konstruiereSuffixBaum(tokenArrayListe,
		// maxAnzahlSaetzeZuBearbeiten,wf_car, wurzel_car, graph_car);

		Knoten kind_walk = wurzel_walk.getKinder().get("walking");
		Knoten kind_run = wurzel_run.getKinder().get("running");
		Knoten kind_car = wurzel_car.getKinder().get("baking");

		// TODO: Nur Knoten mit zu definierender Mindestanzahl von Kindern
		// ausgeben (graphisch).

		// TODO: KnotenKomparator fuer alle(?) Knoten durchfuehren. Evtl. auch
		// nur fuer solche, die irgendwie aehnlich sind (Kinderanzahl;
		// Beruehrungen durch Saetze, ... )

		de.mindlessbloom.suffixtree.experiment05.KnotenKomparator kk = new de.mindlessbloom.suffixtree.experiment05.KnotenKomparator(-1,0d,0d);

		Knoten walk_run = kk.verschmelzeBaeume(kind_walk, kind_run);
		
		Knoten walk_run_car = kk.verschmelzeBaeume(walk_run, kind_car);
		
		double[] trefferwert0 = kk.ermittleKnotenTrefferwert(walk_run);
		System.out.println("Treffer0 walk_run:"+trefferwert0[0]+":"+trefferwert0[1]);
		
		double[] trefferwert1 = kk.ermittleKnotenTrefferwert(walk_run_car);
		System.out.println("Treffer1 walk_run_car:"+trefferwert1[0]+":"+trefferwert1[1]);
		
		Knoten walk_run_treffer = baumBauer.entferneNichtTrefferKnoten(walk_run,true);
		Knoten walk_run_car_treffer = baumBauer.entferneNichtTrefferKnoten(walk_run_car,true);
		
		double[] trefferwert2 = kk.ermittleKnotenTrefferwert(walk_run_treffer);
		System.out.println("Treffer2 walk_run_treffer:"+trefferwert2[0]+":"+trefferwert2[1]);
		
		double[] trefferwert3 = kk.ermittleKnotenTrefferwert(walk_run_car_treffer);
		System.out.println("Treffer1 walk_run_car_treffer:"+trefferwert3[0]+":"+trefferwert3[1]);
		
		DelegateForest<Knoten, Kante> walk_run_treffer_graph = baumBauer.konstruiereGraph(walk_run_car);
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

			Iterator<DelegateForest<Knoten, Kante>> graphenElemente = graphen
					.iterator();
			while (graphenElemente.hasNext()) {

				DelegateForest<Knoten, Kante> graph = graphenElemente
						.next();

				// The Layout<V, E> is parameterized by the vertex and edge
				// types
				// Layout<TestNode, TestEdge> layout = new TreeLayout<TestNode,
				// TestEdge>(graph, 50, 50);
				Layout<Knoten, Kante> layout = new RadialTreeLayout<Knoten, Kante>(
						graph, 50, 50);
				// layout.setSize(new Dimension(700, 700)); // sets the initial
				// size
				// of the space
				// The BasicVisualizationServer<V,E> is parameterized by the
				// edge
				// types
				// BasicVisualizationServer<TestNode, TestEdge> vv = new
				// BasicVisualizationServer<TestNode, TestEdge>(layout);
				VisualizationViewer<Knoten, Kante> vv = new VisualizationViewer<Knoten, Kante>(
						layout);
				vv.setPreferredSize(new Dimension(800, 800)); // Sets the
																// viewing
				// area size
				vv.getRenderContext().setVertexLabelTransformer(
						new ToStringLabeller<Knoten>());
				vv.getRenderContext().setEdgeLabelTransformer(
						new ToStringLabeller<Kante>());
				
				
				// Transformer maps the vertex number to a vertex property
		        Transformer<Knoten,Paint> vertexColor = new Transformer<Knoten,Paint>() {
		            public Paint transform(Knoten arg0) {
		                if(arg0.isMatch()) return Color.GREEN;
		                return Color.RED;
		            }
		        };
		        Transformer<Knoten,Shape> vertexSize = new Transformer<Knoten,Shape>(){
		            public Shape transform(Knoten arg0){
		                Ellipse2D circle = new Ellipse2D.Double(-15, -15, 30, 30);
		                // in this case, the vertex is twice as large
		                return AffineTransform.getScaleInstance(Math.log((double)arg0.getZaehler()+2d)/4d, Math.log((double)arg0.getZaehler()+2d)/4d).createTransformedShape(circle);
		            }
		        };
		        vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);
		        vv.getRenderContext().setVertexShapeTransformer(vertexSize);
				
				// Create a graph mouse and add it to the visualization
				// component
				DefaultModalGraphMouse<Knoten, Kante> gm = new DefaultModalGraphMouse<Knoten, Kante>();
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

	public void konstruiereSuffixBaum(ArrayList<String[]> tokenArrayListe,
			int maxAnzahlSaetzeZuBearbeiten, WortFilter wf, Knoten wurzel,
			DelegateForest<Knoten, Kante> graph, BaumBauer baumBauer) {
		// Saetze durchlaufen
		for (int j = 0; j < tokenArrayListe.size()
				&& (j < maxAnzahlSaetzeZuBearbeiten || maxAnzahlSaetzeZuBearbeiten <= 0); j++) {

			if (!wf.hatWort(tokenArrayListe.get(j))) {
				continue;
			}
			System.out.print("Bearbeite Satz " + (j + 1) + " von "
					+ tokenArrayListe.size() + ": ");

			// Alle Token des aktuellen Satzes durchlaufen
			for (int i = 0; i < tokenArrayListe.get(j).length; i++) {

				System.out.print(tokenArrayListe.get(j)[i] + " ");

				// Suffix-Tree bauen
				baumBauer.baueBaum(Arrays.copyOfRange(tokenArrayListe.get(j), i,
						tokenArrayListe.get(j).length), wurzel, graph, false);
			}
			System.out.println("fertig.");
		}

		// TODO: Knoten nach Anzahl der Beruehrungen geordnet auflisten.
		TreeSet<Knoten> nodesGeordnet = new TreeSet<Knoten>(
				new KnotenDurchlaufZaehlerKomparator());
		// s.fuegeNodesInTreeSetEin(wurzel, nodesGeordnet); // Brauche nur die
		// Kinder der ersten Ebene
		Iterator<String> kinder = wurzel.getKinder().keySet().iterator();
		while (kinder.hasNext()) {
			nodesGeordnet.add(wurzel.getKinder().get(kinder.next()));
		}

		Iterator<Knoten> nodes = nodesGeordnet.iterator();
		while (nodes.hasNext()) {
			Knoten node = nodes.next();
			System.out.println(node.getName()
					+ "\t Beruehrungen durch Saetze :" + node.getZaehler()
					+ "\t Kinder:" + node.getKinder().size());
		}
	}
}
