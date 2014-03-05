package de.mindlessbloom.suffixtree.experiment01;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import de.mindlessbloom.suffixtree.BaumBauer;
import de.mindlessbloom.suffixtree.GraphenPlotter;
import de.mindlessbloom.suffixtree.Kante;
import de.mindlessbloom.suffixtree.Knoten;
import de.mindlessbloom.suffixtree.MatrixPlotter;
import de.mindlessbloom.suffixtree.OANC;
import de.mindlessbloom.suffixtree.OANCXMLParser;
import de.mindlessbloom.suffixtree.WortFilter;
import edu.uci.ics.jung.graph.DelegateTree;

public class Start {

	public static void main(String[] args) throws Exception {
		
		/**
		 * Kommandozeilenoptionen definieren
		 */
		
		// Options-Objekt instanziieren
		Options optionen = new Options();

		// Option fuer graphische Ausgabe hinzufuegen
		optionen.addOption("g", false, "Graphik anzeigen.");

		// Option fuer Hilfstext anzeigen
		optionen.addOption("h", false, "Gibt Information zur Benutzung des Programms aus.");

		// Option fuer Pfad zum Korpus hinzufuegen
		optionen.addOption("k", true, "Pfad, auf bzw. unter dem sich die Korpusdateien befinden (Option ist mehrfach anwendbar).");

		// Option fuer Layout der graphischen Ausgabe hinzufuegen
		optionen.addOption("l", true, "Layout des anzuzeigenden Graphen; \"radial\" (Standard) oder \"balloon\" (erfordert Option -g).");

		// Option fuer Parsing hinzufuegen
		optionen.addOption("p", false, "Punktuation nicht entfernen und als eigenstaendige Woerter bzw. Token behandeln.");
				
		// Option fuer Vergleichsreduktion hinzufuegen
		optionen.addOption("r", false, "Vergleiche auf das Notwendige reduzieren.");

		// Option fuer Anzeigefilter der graphischen Ausgabe hinzufuegen
		optionen.addOption("t", false, "Nur Trefferknoten in der Graphik anzeigen (erfordert Option -g).");

		// Option fuer zu vergleichende Worte hinzufuegen
		optionen.addOption("w", true, "Zu vergleichendes Wort (Option ist mehrfach anwendbar).");

		// Option fuer Vergleiche hinzufuegen
		optionen.addOption("E", true, "Exponent der Ebenenzahl, mit der die Werte der Knoten fuer den Vergleich multipliziert werden ( Vergleichswert=Knotenwert*Ebenenzahl^X; 0 = kein Effekt (Standard), <0 = Wertung geringer, je tiefer die Ebene; >0 dito hoeher ). Nur sinnvoll mit Option -Z.");

		// Option fuer Korpuswandlung hinzufuegen
		optionen.addOption("K", false, "Worte des Korpus beim Einlesen in Kleinbuchstaben wandeln.");

		// Option fuer Vergleiche hinzufuegen
		optionen.addOption("M", true, "Maximale Ebene, bis zu der Baeume miteinander verglichen werden (-1 = ignorieren (Standard)).");

		// Option fuer Vergleiche hinzufuegen
		optionen.addOption("P", false, "Auch Praefixbaeume erstellen und Vergleichen (nur mit Option -Z verwendbar).");
				
		// Option fuer Korpuswandlung hinzufuegen
		optionen.addOption("S", false, "Saetzen des Korpus beim Einlesen Terminiersymbol ($) anfuegen.");

		// Option fuer Vergleiche hinzufuegen
		optionen.addOption("T", false, "Ebenenfaktor beim Vergleich nur auf Trefferknoten anwenden.");
		
		// Option fuer Vergleiche hinzufuegen
		optionen.addOption("Z", false, "Nur den mit dem Vergleichswort beginnenden Zweig des jeweiligen Suffixbaumes zum Vergleich heranziehen.");
		
		/**
		 * Kommandozeilenoptionen auswerten
		 */
		
		// Parser fuer Kommandozeilenoptionen
		CommandLineParser parser = new org.apache.commons.cli.PosixParser();
		CommandLine kommandozeile = parser.parse( optionen, args);
		
		// Ggf. Hilfetext anzeigen
		if(kommandozeile.hasOption("h")) {
			HelpFormatter lvFormater = new HelpFormatter();
			lvFormater.printHelp("java [-d64 -Xms7500m -Xmx7500m] -jar Experiment.jar <Optionen>", optionen);
			System.exit(0);
		}
		
		// Zu vergleichende Worte festlegen
		String[] vergleichWorte = new String[]{"running","walking", "car"};
		if(kommandozeile.hasOption("w")) {
			vergleichWorte = kommandozeile.getOptionValues("w");
		}

		// Graphische Ausgabe des Graphen
		boolean graphikAusgabe = kommandozeile.hasOption("g");
		boolean zeigeNurTrefferKnoten = kommandozeile.hasOption("t");
		
		// Vergleiche ggf. nur auf Notwendige reduzieren
		boolean reduziereVergleicheAufNotwendige = kommandozeile.hasOption("r");
		
		// Typ des anzuzeigenden Layouts, 1: RadialTreeLayout, 2:BalloonLayout
		int layoutTyp = 1;
		if(kommandozeile.hasOption("l")) {
			layoutTyp = Integer.parseInt(kommandozeile.getOptionValue("l"));
			if (layoutTyp <1 || layoutTyp >2) layoutTyp = 1;
		}
		
		// Pfade zum OANC.
		String[] oancSpeicherorte = new String[]{"/Users/marcel/Downloads/OANC/data/written_1/","/Users/marcel/Downloads/OANC/data/written_2/"};
		if(kommandozeile.hasOption("k")) {
			oancSpeicherorte = kommandozeile.getOptionValues("k");
		}
		
		// Worte des Korpus beim Einlesen in Kleinbuchstaben wandeln
		boolean wandleInKleinbuchstaben = kommandozeile.hasOption("K");
		
		// Saetzen des Korpus beim Einlesen Terminiersymbol anfuegen
		boolean fuegeTerminierSymbolHinzu = kommandozeile.hasOption("S");
		
		// Maximale Entfernung des Kontextes vom Vergleichswort, unter derer er noch verglichen wird (-1 = ignorieren).
		int maximaleKontextEntfernungVonWort = -1;
		
		// Exponent der Ebenenzahl, mit der die Vergleichsergebnisse multipliziert werden ( 0 = kein Effekt; <0 = Wertung geringer, je weiter von Vergleichswort entfernt; >0 dito hoeher )
		double ebenenFaktorExponent = 0d;
		
		// Steuert, ob obiger Ebenenfaktor nur auf die Trefferknoten angewandt werden soll.
		boolean ebenenFaktorNurAufTrefferAnwenden = kommandozeile.hasOption("T");
		
		// Nur den mit dem Vergleichswort beginnenden Zweig des jeweiligen Suffixbaumes zum Vergleich heranziehen.
		boolean vergleichAufVergleichswortzweigBeschraenken = kommandozeile.hasOption("Z");
		
		// Nur den mit dem Vergleichswort beginnenden Zweig des jeweiligen Suffixbaumes zum Vergleich heranziehen.
		boolean praefixBaumeErstellen = (kommandozeile.hasOption("Z") && kommandozeile.hasOption("P"));
		
		// Punktuation nicht entfernen und als eigenstaendige Woerter bzw. Token behandeln.
		boolean behaltePunktuation = kommandozeile.hasOption("p");

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
			double prozentFertig = Math.ceil(((double)korpusDateiZaehler / (double)korpusDateiListe.size())*100);
			if (korpusDateiZaehler % (korpusDateiListe.size()/20) == 0){
				Logger.getLogger(Start.class.getCanonicalName()).info("Parse "+korpusDateiListe.size()+" Korpusdateien : "+prozentFertig+"%");
			}
			
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
				satzListe.add(oancParser.bereinigeUndSegmentiereSatz(rohsaetze.next(), fuegeTerminierSymbolHinzu, wandleInKleinbuchstaben, behaltePunktuation));
			}
		}
		
		// Meldung ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info("Liste mit "+satzListe.size()+" Saetzen erstellt.");
		
		/**
		 * Datenstrukturen erstellen
		 */
		
		// Meldung ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info("Erstelle Baumgraphen.");
		
		// Graphenliste fuer Suffixbaeume erstellen
		ArrayList<DelegateTree<Knoten, Kante>> graphenListe = new ArrayList<DelegateTree<Knoten, Kante>>();
		
		// Ggf. Graphenliste fuer Praefixbaume erstellen 
		ArrayList<DelegateTree<Knoten, Kante>> praefixGraphenListe = null;
		if (praefixBaumeErstellen){
			praefixGraphenListe = new ArrayList<DelegateTree<Knoten, Kante>>();
		}
		
		// Zu vergleichende Worte durchlaufen
		for (int i=0; i<vergleichWorte.length; i++){
			// Neuer Graph
			DelegateTree<Knoten, Kante> graph = new DelegateTree<Knoten, Kante>();
			// Wurzelknoten hinzufuegen
			Knoten wurzel = new Knoten();
			wurzel.setName(vergleichWorte[i]);
			graph.setRoot(wurzel);
			// Graph zur Liste hinzufuegen
			graphenListe.add(graph);
			
			// Ggf. Praefixgraphen erstellen
			if (praefixBaumeErstellen){
				// Neuer Graph
				DelegateTree<Knoten, Kante> praefixGraph = new DelegateTree<Knoten, Kante>();
				// Wurzelknoten hinzufuegen
				Knoten praefixWurzel = new Knoten();
				praefixWurzel.setName(vergleichWorte[i]);
				praefixGraph.setRoot(praefixWurzel);
				// Graph zur Liste hinzufuegen
				praefixGraphenListe.add(praefixGraph);
			}
		}

		// Filter einrichten, mit denen die relevanten Teile des Korpus ermittelt werden sollen
		WortFilter[] wortFilter = new WortFilter[vergleichWorte.length];
		for (int i=0; i<vergleichWorte.length; i++){
			wortFilter[i] = new WortFilter();
			wortFilter[i].addWort(vergleichWorte[i]);
		}
		
		// BaumBauer instanziieren
		BaumBauer baumBauer = new BaumBauer();
		
		// Vergleichswortliste durchlaufen
		for (int i=0; i<vergleichWorte.length; i++){
			
			// Saetze aus Korpus durchlaufen, Treffer mitzaehlen (fuer Anzeige)
			int saetzeDurchlaufen = 0;
			int saetzeGefunden = 0;
			Iterator<List<String>> saetze = satzListe.iterator();
			while(saetze.hasNext()){
				
				// Naechsten Satz ermitteln
				List<String> satz = saetze.next();
				
				// Pruefen, ob WortFilter greift
				if (wortFilter[i].hatWort(satz)){
					// Satz in den Baum/Graphen hineinbauen
					baumBauer.baueBaum(satz.toArray(new String[satz.size()]), graphenListe.get(i).getRoot(), graphenListe.get(i), false);
					
					// Ggf. Satz ebenfalls in den Praefixbaum/-graphen hineinbauen
					if (praefixBaumeErstellen){
						baumBauer.baueBaum(satz.toArray(new String[satz.size()]), praefixGraphenListe.get(i).getRoot(), praefixGraphenListe.get(i), true);
					}
					
					// Treffer mitzaehlen
					saetzeGefunden++;
				}
				
				// Durchlaufenen Satz mitzaehlen
				saetzeDurchlaufen++;
				
				// Meldung ausgeben
				double prozentFertig = Math.ceil(((double)saetzeDurchlaufen / (double)satzListe.size())*100);
				if (saetzeDurchlaufen % (satzListe.size()/20) == 0){
					Logger.getLogger(Start.class.getCanonicalName()).info("Ermittle Saetze, die Wort '"+vergleichWorte[i]+"' beinhalten: "+saetzeDurchlaufen+"/"+satzListe.size()+" ("+saetzeGefunden+") "+prozentFertig+"%");
				}
				
			}
			// Zeilenumbruch in Anzeige ausgeben
			System.out.println();
			
		}
		
		// Die Satzliste wird ab hier nicht mehr gebraucht - der Heap-Speicher, den sie belegt, kann freigegeben werden.
		satzListe = null;
		System.gc();

		/**
		 * Graphen vergleichen
		 * 
		 * Um sicherzustellen, dass die Vergleichsmethoden korrekt funktionieren,
		 * werden ALLE moeglichen Zweierkombinationen ueberprueft, also auch
		 * Wort1<->Wort1, Wort1<->Wort2, Wort2<->Wort1, Wort2<->Wort2, ... 
		 * Dies laesst sich mit der Variable reduziereVergleicheAufNotwendige
		 * (Option -r) abschalten.
		 */
		
		// Komparator instanziieren
		KnotenKomparator kk = new KnotenKomparator(maximaleKontextEntfernungVonWort, ebenenFaktorExponent, ebenenFaktorNurAufTrefferAnwenden);

		// Ggf. GraphenPlotter erstellen
		GraphenPlotter gp = null;
		if (graphikAusgabe){
			gp = new GraphenPlotter();
		}

		// Vergleichsmatrix erstellen
		Double[][] vergleichsmatrix = kk.vergleicheAlle(graphenListe, reduziereVergleicheAufNotwendige, vergleichAufVergleichswortzweigBeschraenken, gp, zeigeNurTrefferKnoten, layoutTyp);
		// Ggf. Vergleichsmatrix fuer Praefixgraphen erstellen
		Double[][] praefixvergleichsmatrix = null;
		if (praefixBaumeErstellen){
			praefixvergleichsmatrix = kk.vergleicheAlle(praefixGraphenListe, reduziereVergleicheAufNotwendige, vergleichAufVergleichswortzweigBeschraenken, gp, zeigeNurTrefferKnoten, layoutTyp);
		}
		
		/**
		 * Ausgabe der Ergebnisse
		 */
		
		// Matrixplotter instanziieren
		MatrixPlotter plotter = new MatrixPlotter();
		
		// Ausgabeformat festlegen
		plotter.setFormat(new DecimalFormat("0.000"));
		
		// Matrix ausgeben (auf Konsole)
		plotter.plot(vergleichsmatrix, vergleichWorte);
		
		// Ggf. auch Praefixbaumvergleichsmatrix ausgeben
		if (praefixBaumeErstellen){
			plotter.plot(praefixvergleichsmatrix, vergleichWorte);
		}
	}
}
