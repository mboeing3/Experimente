package de.mindlessbloom.suffixtree.experiment05;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import de.mindlessbloom.suffixtree.experiment01_03.BaumBauer;
import de.mindlessbloom.suffixtree.experiment01_03.Knoten;
import de.mindlessbloom.suffixtree.neo4j.Neo4jKlient;
import de.mindlessbloom.suffixtree.oanc.OANC;
import de.mindlessbloom.suffixtree.oanc.OANCXMLParser;


public class Start {
			
	
	public static void main(String[] args) throws Exception {

		/**
		 * Kommandozeilenoptionen definieren
		 */
		
		// Options-Objekt instanziieren
		Options optionen = new Options();

		// Option fuer Pfad zum Korpus hinzufuegen
		optionen.addOption("k", true, "Pfad, auf bzw. unter dem sich die Korpusdateien befinden (Option ist mehrfach anwendbar).");
		
		// Option fuer Datenstruktur hinzufuegen
		optionen.addOption("m", true, "Maximale Tiefe der erstellten Baeume (inkl. Wurzel). <0 = ignorieren (Standard)");
		
		// Option fuer Programmausfuehrung hinzufuegen
		optionen.addOption("p", true, "Anzahl der gleichzeitig auszufuehrenden Prozesse (Standard: 4)");
		
		// Option fuer Vergleiche hinzufuegen
		optionen.addOption("t", false, "Nur Trefferknoten als Vergleichsbaeume verwenden.");
		
		// Option fuer Hilfstext anzeigen
		optionen.addOption("h", false, "Gibt Information zur Benutzung des Programms aus.");
		
		// Option fuer Datenstrukturkonstruktion hinzufuegen
		optionen.addOption("s", true, "Uebereinstimmungsquotienten-Schwellwert fuer die Erstellung einer Verbindung zwischen zwei Knoten im Graphen (Standard 0.1).");
		
		/**
		 * Kommandozeilenoptionen auswerten
		 */
		
		// Parser fuer Kommandozeilenoptionen
		CommandLineParser parser = new org.apache.commons.cli.PosixParser();
		CommandLine kommandozeile = parser.parse( optionen, args);
		
		// Ggf. Hilfetext anzeigen
		if (kommandozeile.hasOption("h")) {
			HelpFormatter lvFormater = new HelpFormatter();
			lvFormater.printHelp("java [-d64 -Xms7500m -Xmx7500m] -jar Experiment4.jar <Optionen>", optionen);
			System.exit(0);
		}
		
		// Pfade zum OANC.
		//String[] oancSpeicherorte = new String[]{"/Users/marcel/Downloads/OANC/data/written_1/","/Users/marcel/Downloads/OANC/data/written_2/"};
		String[] oancSpeicherorte = new String[]{"/Users/marcel/testkorpus/"};
		if(kommandozeile.hasOption("k")) {
			oancSpeicherorte = kommandozeile.getOptionValues("k");
		}
		
		// Maximale Baumtiefe
		int maximaleBaumTiefe = -1;
		if(kommandozeile.hasOption("m")) {
			maximaleBaumTiefe = Integer.parseInt(kommandozeile.getOptionValue("m"));
		}
		
		// Anzahl gleichzeitiger Prozesse
		int gleichzeitigeProzesse = 4;
		if(kommandozeile.hasOption("p")) {
			if (Integer.parseInt(kommandozeile.getOptionValue("p")) > 0)
			gleichzeitigeProzesse = Integer.parseInt(kommandozeile.getOptionValue("p"));
		}
		
		boolean behalteNurTreffer = kommandozeile.hasOption("t");
		
		// Schwellwert fuer Graphenkonstruktion
		Double schwellwert = 0.1d;
		if(kommandozeile.hasOption("s")) {
			if (Double.parseDouble(kommandozeile.getOptionValue("s")) > 0.0)
				schwellwert = Double.parseDouble(kommandozeile.getOptionValue("s"));
		}
		
		// Startobjekt instanziieren
		Start start = new Start();
		
		// Experiment durchfuehren
		start.experiment(oancSpeicherorte, maximaleBaumTiefe, gleichzeitigeProzesse, behalteNurTreffer, schwellwert);
		
	}
	
	public void experiment(String[] oancSpeicherorte, int maximaleBaumTiefe, int gleichzeitigeProzesse, boolean behalteNurTreffer, Double schwellwertEingabe) throws Exception{
		// Laufzeitinstanz ermitteln
		Runtime rt = Runtime.getRuntime();
		
		// Speicherinfo ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info("Belegter Hauptspeicher: "+ (rt.totalMemory() - rt.freeMemory()));
		
		// Schwellwert in finale Variable schreiben
		final Double schwellwert = schwellwertEingabe;
		
		/**
		 * Korpus einlesen (Ergebnis in Objekt satzListe)
		 */
		
		// Meldung ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info(
				"Lese Korpus ein.");

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
		while (korpusDateien.hasNext()) {

			korpusDateiZaehler++;

			// Naechste Korpusdatei ermitteln
			File korpusDatei = korpusDateien.next();

			// Meldung ausgeben
			double prozentFertig = Math.ceil(((double)korpusDateiZaehler / (double)korpusDateiListe.size())*100);
			if ((korpusDateiListe.size()/20!=0) && korpusDateiZaehler % (korpusDateiListe.size()/20) == 0){
				Logger.getLogger(Start.class.getCanonicalName()).info("Parse "+korpusDateiListe.size()+" Korpusdateien : "+prozentFertig+"%");
			}

			// Aktuelle Korpusdatei als Quelle fuer Parser setzen
			oancParser.setQuellDatei(korpusDatei);

			// Satzgrenzendatei auf null setzen; der oancParser ermittelt dann
			// automatisch ihren Namen
			oancParser.setSatzGrenzenXMLDatei(null);

			// Datei parsen und Rohsaetze ermitteln
			List<String> rohsatzListe = oancParser.parseQuellDatei();

			// Liste der Rohsaetze durchlaufen
			Iterator<String> rohsaetze = rohsatzListe.iterator();
			while (rohsaetze.hasNext()) {

				// Rohsatz bereinigen und zu Ergebnisliste hinzufuegen
				satzListe.add(oancParser.bereinigeUndSegmentiereSatz(
						rohsaetze.next(), false, true, true, true));
			}
		}

		// Meldung ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info(
				"Liste mit " + satzListe.size() + " Saetzen erstellt.");

		// Speicherinfo ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info(
				"Belegter Hauptspeicher: "
						+ (rt.totalMemory() - rt.freeMemory()));

		/**
		 * Datenstrukturen erstellen
		 */

		// Meldung ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info(
				"Erstelle Suffixbaum.");

		// Wurzelknoten erstellen
		final Knoten wurzel = new Knoten();
		wurzel.setName("^");

		// BaumBauer erstellen
		BaumBauer baumBauer = new BaumBauer();

		// Zaehler fur Saetze (Kosmetik)
		int satzZaehler = 0;

		Iterator<List<String>> saetze = satzListe.iterator();
		while (saetze.hasNext()) {

			satzZaehler++;

			// Meldung ausgeben
			double prozentFertig = Math.ceil(((double)satzZaehler / (double)satzListe.size())*100);
			if ((satzListe.size()/20!=0) && satzZaehler % (satzListe.size()/20) == 0){
				Logger.getLogger(Start.class.getCanonicalName()).info(
						"Fuege " + satzListe.size()
								+ " Saetze zu Suffixbaum hinzu : "
								+ prozentFertig + "%");// Speicherinfo ausgeben
				Logger.getLogger(Start.class.getCanonicalName()).info(
						"Belegter Hauptspeicher: "
								+ (rt.totalMemory() - rt.freeMemory()));
			}

			List<String> satz = saetze.next();
			baumBauer.baueBaum(satz.toArray(new String[satz.size()]), wurzel,
					null, false, maximaleBaumTiefe);

			// Speicher freigeben
			satz = null;
		}

		// Meldung ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info(
				"Suffixbaum erstellt.");

		// Speicherinfo ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info(
				"Belegter Hauptspeicher: "
						+ (rt.totalMemory() - rt.freeMemory()));

		// Die Satzliste wird ab hier nicht mehr gebraucht - der Heap-Speicher,
		// den sie belegt, kann freigegeben werden.
		Logger.getLogger(Start.class.getCanonicalName()).info(
				"Loesche Satzliste.");
		satzListe = null;
		System.gc();

		// Speicherinfo ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info(
				"Belegter Hauptspeicher: "
						+ (rt.totalMemory() - rt.freeMemory()));

		// Speicherinfo ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info(
				"Suffixbaumgroesse: " + wurzel.getZaehler());
		
		/*
		 *  Suffixbaumzweige vergleichen und zum Graphen hinzufuegen
		 */
		
		// Graph-Datenbank-Klienten instanziieren
		final Neo4jKlient graph = new Neo4jKlient();
		
		// Liste der bereits in der Graphendatenbank angelegten Knoten
		Map<String,URI> angelegteKnoten = new HashMap<String,URI>();
		
		// Meldung anzeigen
		Logger.getLogger(Start.class.getCanonicalName()).info("Fuege "+wurzel.getKinder().size()+" Knoten zu Graphen hinzu.");
		
		
		
		// Map der Suffixbaumzweige in Liste wandeln
		final List<Knoten> zweige = new ArrayList<Knoten>();
		
		// Fortschrittsanzeigeprozess
		Thread fortschrittsAnzeigerKnotenZuGraphen = new Thread() {
			@Override
			public void run() {
				try {
					while (wurzel.getKinder().size()>0){
						Logger.getLogger(Start.class.getCanonicalName()).info(zweige.size()+"/"+wurzel.getKinder().size());
						Thread.sleep(5000);
					}
					Logger.getLogger(Start.class.getCanonicalName()).info("fertig.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		fortschrittsAnzeigerKnotenZuGraphen.start();
		
		Iterator<String> kinder = wurzel.getKinder().keySet().iterator();
		while(kinder.hasNext()){
			
			// Namen des naechsten Kindelements ermitteln 
			String kindName = kinder.next();
			
			// Knoten ermitteln
			Knoten kind = wurzel.getKinder().get(kindName);
			
			// Kind in Liste aufnehmen
			zweige.add(kind);
			
			// Knoten in Graphen einfuegen
			URI knotenUri = graph.erstelleKnoten(kind.getName());
			angelegteKnoten.put(kind.getName(),knotenUri);
		}
		// Werte aus Map loeschen
		wurzel.getKinder().clear();
		
		
		// Berechnungen fuer Fortschrittsanzeige
		
		final Fortschritt fortschritt = new Fortschritt(zweige.size());
		
		Thread fortschrittsAnzeiger = new Thread() {
			@Override
			public void run() {
				try {
					while (fortschritt.getVerbleibend()>0){
						Thread.sleep(5000);
						Logger.getLogger(Start.class.getCanonicalName()).info("Noch miteinander zu vergleichen: "+fortschritt.getVerbleibend()+" ("+fortschritt.getVerarbeitet()*12+" vpm)");
						fortschritt.setVerarbeitet(0l);
					}
					Logger.getLogger(Start.class.getCanonicalName()).info("fertig.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		fortschrittsAnzeiger.start();
		
		
		// Wiederholen, bis die Liste der Suffixbaumzweige leer ist
		while (!zweige.isEmpty()) {

			// Erstes Element aus Liste extrahieren
			final Knoten knoten = zweige.remove(0);

			// URI des Knotens im Graphen ermitteln
			final URI knotenUri = angelegteKnoten.get(knoten.getName());

			// Neuen Exekutor instanziieren
			ExecutorService exekutor = Executors.newFixedThreadPool(gleichzeitigeProzesse);
			
			// Liste der verbleibenden Suffixbaumzweige durchlaufen
			Iterator<Knoten> vergleichsKnotenIterator = zweige.iterator();
			while (vergleichsKnotenIterator.hasNext()) {

				// Naechsten Vergleichsknoten ermitteln
				final Knoten vergleichsKnoten = vergleichsKnotenIterator.next();

				// Auf leere Knoten pruefen (sollte EIGENTLICH nicht vorkommen)
				if (vergleichsKnoten == null || vergleichsKnoten.getName().isEmpty())
					continue;

				// URI des Vergleichsknotens im Graphen ermitteln
				final URI vergleichsKnotenUri = angelegteKnoten.get(vergleichsKnoten.getName());

				// Neuen Vergleichsprozess instanziieren und an Exekutor uebergeben
				Runnable prozess = new VergleichsProzess(graph, schwellwert, knoten, vergleichsKnoten, knotenUri, vergleichsKnotenUri, fortschritt);
		        exekutor.execute(prozess);
			}
			
			// Exekutor stoppen
			exekutor.shutdown();
	        while (!exekutor.isTerminated()) {
	        	//Logger.getLogger(Start.class.getCanonicalName()).info("");
	        	Thread.sleep(5000l);
	        }

			// Fortschritt mitzaehlen (fuer Anzeige)
			fortschritt.setVerbleibend(fortschritt.getVerbleibend()-1);

		}
		

	}

}
