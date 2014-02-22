package de.mindlessbloom.suffixtree.experiment04;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import de.mindlessbloom.suffixtree.BaumBauer;
import de.mindlessbloom.suffixtree.GraphenPlotter;
import de.mindlessbloom.suffixtree.Kante;
import de.mindlessbloom.suffixtree.Knoten;
import de.mindlessbloom.suffixtree.MatrixPlotter;
import de.mindlessbloom.suffixtree.OANC;
import de.mindlessbloom.suffixtree.OANCXMLParser;
import de.mindlessbloom.suffixtree.WortFilter;
import edu.uci.ics.jung.graph.DelegateForest;

public class Start {

	public static void main(String[] args) throws Exception {

		/**
		 * Variablen definieren
		 */
		
		// Zu vergleichende Worte festlegen
		//String[] vergleichWorte = new String[]{"running","walking", "car"};
		String[] vergleichWorte = new String[]{"running","walking"};
		
		// Graphische Ausgabe des Graphen
		boolean graphikAusgabe = true;
		
		// Vergleiche ggf. nur auf Notwendige reduzieren
		boolean reduziereVergleicheAufNotwendige = true;
		
		// Pfade zum OANC.
		String[] oancSpeicherorte = new String[]{"/Users/marcel/Downloads/OANC/data/written_1/","/Users/marcel/Downloads/OANC/data/written_2/"};

		/**
		 * Korpus einlesen (Ergebnis in Objekt satzListe)
		 */
		
		// Meldung ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info("Lese Korpus ein.");
		
		// OANC-Verwaltungsinstanz erstellen
		OANC oanc = new OANC();
		oanc.setOancSpeicherorte(oancSpeicherorte);
		
		// Korpusdateien ermitteln
		List<File> korpusDateiListe = oanc.sucheQuellDateien();
		
		// Korpusparser erstellen
		OANCXMLParser oancParser = new OANCXMLParser();
		
		// Liste fuer Ergebnis erstellen
		ArrayList<List<String>> satzListe = new ArrayList<List<String>>();
		
		// Zaehler fur Korpusdateien (Kosmetik)
		int korpusDateiZaehler = 0;
		
		// Korpusdateiliste durchlaufen
		Iterator<File> korpusDateien = korpusDateiListe.iterator();
		while(korpusDateien.hasNext()){
			
			korpusDateiZaehler++;
			
			// Naechste Korpusdatei ermitteln
			File korpusDatei = korpusDateien.next();
			
			// Meldung ausgeben
			Logger.getLogger(Start.class.getCanonicalName()).info("Parse Korpusdatei "+korpusDateiZaehler+"/"+korpusDateiListe.size()+" : "+korpusDatei.getAbsolutePath());
			
			// Aktuelle Korpusdatei als Quelle fuer Parser setzen
			oancParser.setQuellDatei(korpusDatei);
			
			// Satzgrenzendatei auf null setzen; der oancParser ermittelt dann automatisch ihren Namen
			oancParser.setSatzGrenzenXMLDatei(null);
			
			// Datei parsen und Rohsaetze ermitteln
			List<String> rohsatzListe = oancParser.parseQuellDatei();
			
			// Liste der Rohsaetze durchlaufen
			Iterator<String> rohsaetze = rohsatzListe.iterator();
			while (rohsaetze.hasNext()){
				
				// Rohsatz bereinigen und zu Ergebnisliste hinzufuegen
				satzListe.add(oancParser.bereinigeUndSegmentiereSatz(rohsaetze.next()));
			}
		}
		
		// Meldung ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info("Liste mit "+satzListe.size()+" Saetzen erstellt.");
		
		/**
		 * Datenstrukturen erstellen
		 */
		
		// Meldung ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info("Erstelle Baumgraphen.");
		
		// Baumgraphen erstellen
		ArrayList<DelegateForest<Knoten, Kante>> graphenListe = new ArrayList<DelegateForest<Knoten, Kante>>();
		for (int i=0; i<vergleichWorte.length; i++){
			// Neuer Graph
			DelegateForest<Knoten, Kante> graph = new DelegateForest<Knoten, Kante>();
			// Wurzelknoten hinzufuegen
			Knoten wurzel = new Knoten();
			wurzel.setName(vergleichWorte[i]);
			graph.setRoot(wurzel);
			// Graph zur Liste hinzufuegen
			graphenListe.add(graph);
		}

		// Filter einrichten, mit denen die relevanten Teile des Korpus ermittelt werden sollen
		WortFilter[] wortFilter = new WortFilter[vergleichWorte.length];
		for (int i=0; i<vergleichWorte.length; i++){
			wortFilter[i] = new WortFilter();
			wortFilter[i].addWort(vergleichWorte[i]);
		}
		
		// BaumBauer erstellen
		BaumBauer baumBauer = new BaumBauer();
		
		// Vergleichswortliste durchlaufen
		for (int i=0; i<vergleichWorte.length; i++){
			
			// Saetze aus Korpus durchlaufen
			Iterator<List<String>> saetze = satzListe.iterator();
			while(saetze.hasNext()){
				
				// Naechsten Satz ermitteln
				List<String> satz = saetze.next();
				
				// Pruefen, ob WortFilter greift
				if (wortFilter[i].hatWort(satz)){
					// Satz in den Baum/Graphen hineinbauen
					baumBauer.baueBaum(satz.toArray(new String[satz.size()]), graphenListe.get(i).getRoots().toArray(new Knoten[1])[0], graphenListe.get(i), false);
				}
				
			}
			
		}

		/**
		 * Graphen vergleichen
		 * 
		 * Um sicherzustellen, dass die Vergleichsmethoden korrekt funktionieren,
		 * werden ALLE moeglichen Zweierkombinationen ueberprueft, also auch
		 * Wort1<->Wort1, Wort1<->Wort2, Wort2<->Wort1, Wort2<->Wort2, ... 
		 * Dies laesst sich mit der Variable reduziereVergleicheAufNotwendige
		 * abschalten.
		 */

		// Vergleichsmatrix erstellen
		Double[][] vergleichsmatrix = new Double[vergleichWorte.length][vergleichWorte.length];
		
		// Komparator instanziieren
		KnotenKomparator kk = new KnotenKomparator();
		
		// Liste der Graphen durchlaufen
		for (int i=0; i<graphenListe.size(); i++){
			
			// Zweite Dimension durchlaufen
			for (int j=0; j<graphenListe.size(); j++){
				
				// Ggf. unnoetige Vergleiche ueberspringen
				if (reduziereVergleicheAufNotwendige && j<=i){
					vergleichsmatrix[i][j] = null;
					continue;
				}
				
				// Baeume miteinander vergleichen (Wird weiter unten schrittweise ausgefuehrt, um die kombinierten Baeume graphisch ausgeben zu koennen)
				//vergleichsmatrix[i][j] = kk.vergleiche(graphenListe.get(i).getRoots().toArray(new Knoten[1])[0], graphenListe.get(j).getRoots().toArray(new Knoten[1])[0]);
				
				// Baeume der zu vergleichenden Worte miteinander kombinieren
				Knoten verschmolzenerBaum = kk.verschmelzeBaeume(graphenListe.get(i).getRoots().toArray(new Knoten[1])[0], graphenListe.get(j).getRoots().toArray(new Knoten[1])[0]);
				
				// Uebereinstimmungswerte ermitteln
				Double[] trefferWert = kk.ermittleKnotenTrefferwert(verschmolzenerBaum, -1, 0d);
				
				// DEBUG
				System.out.println(trefferWert[0] + ":" + trefferWert[1]);
				
				// Uebereinstimmungswerte auf Anteilswert reduzieren und in Matrix speichern
				vergleichsmatrix[i][j] = trefferWert[0] / trefferWert[1];
				
				// Ggf. Graphikausgabe der Graphen mittels JUNG2-API
				if (graphikAusgabe) {
					GraphenPlotter gp = new GraphenPlotter();
					gp.plot(baumBauer.konstruiereGraph(baumBauer.entferneNichtTrefferKnoten(verschmolzenerBaum,true)));
				}
			}
		}
		
		/**
		 * Ausgabe der Ergebnisse
		 */
		
		// Matrixplotter instanziieren
		MatrixPlotter plotter = new MatrixPlotter();
		
		// Matrix ausgeben (auf Konsole)
		plotter.plot(vergleichsmatrix);

		

	}
}
