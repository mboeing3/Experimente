package de.mindlessbloom.suffixtree.experiment05;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.neo4j.graphdb.Node;

import de.mindlessbloom.suffixtree.experiment01_03.BaumBauer;
import de.mindlessbloom.suffixtree.experiment01_03.Knoten;
import de.mindlessbloom.suffixtree.neo4j.Neo4jLokalKlient;
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
		
		// Option fuer Datenstrukturkonstruktion hinzufuegen
		optionen.addOption("v", true, "Schwellwert fuer minimale Anzahl der Wortvorkommen, unterhalb dessen Begriffe nicht verarbeitet werden (Standard 0).");
		
		// Option fuer Datenstrukturkonstruktion hinzufuegen
		optionen.addOption("a", true, "Maximale Anzahl an Verbindungen, die fuer einen Begriff jeweils geknuepft werden (Standard 0=ignorieren).");
		
		// Option fuer Graphenkommunikation hinzufuegen
		optionen.addOption("T", true, "Maximale Anzahl an Elementen, die in einer Transaktion an den Graphen uebermittelt werden (Standard 1000).");
		
		
		
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
		
		// Schwellwert fuer Wortvorkommen
		int minWortvorkommen = 0;
		if(kommandozeile.hasOption("v")) {
			if (Integer.parseInt(kommandozeile.getOptionValue("v")) > 0)
				minWortvorkommen = Integer.parseInt(kommandozeile.getOptionValue("v"));
		}
		
		// Max. Anzahl an Verbindungen eines Begriffs
		int maxBegriffsVerbindungen = 0;
		if(kommandozeile.hasOption("a")) {
			if (Integer.parseInt(kommandozeile.getOptionValue("a")) > 0)
				maxBegriffsVerbindungen = Integer.parseInt(kommandozeile.getOptionValue("a"));
		}
		
		// Startobjekt instanziieren
		Start start = new Start();
		
		// Experiment durchfuehren
		start.experiment(oancSpeicherorte, maximaleBaumTiefe, gleichzeitigeProzesse, behalteNurTreffer, schwellwert, minWortvorkommen, maxBegriffsVerbindungen);
		
	}
	
	public void experiment(String[] oancSpeicherorte, int maximaleBaumTiefe, int gleichzeitigeProzesse, boolean behalteNurTreffer, Double schwellwertEingabe, int minWortvorkommen, int maxBegriffsVerbindungenEingabe) throws Exception{
		// Laufzeitinstanz ermitteln
		Runtime rt = Runtime.getRuntime();
		
		// Speicherinfo ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info("Belegter Hauptspeicher: "+ (rt.totalMemory() - rt.freeMemory()));
		
		// Werte fuer Parallelprozessverarbeitung in finale Variablen schreiben
		final Double schwellwert = schwellwertEingabe;
		final int maxBegriffsVerbindungen = maxBegriffsVerbindungenEingabe;
		
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
		final Neo4jLokalKlient graph = new Neo4jLokalKlient("/home/marcel/opt/neo4j-community-2.0.1/data/graph.db");
		
		// Liste der bereits in der Graphendatenbank angelegten Knoten
		Map<String,Node> angelegteKnoten = new HashMap<String,Node>();
		
		// Meldung anzeigen
		Logger.getLogger(Start.class.getCanonicalName()).info("Der Suffixbaum hat "+wurzel.getKinder().size()+" Zweige.");
		
		
		
		// Map der Suffixbaumzweige in Liste wandeln
		final List<Knoten> zweige = new ArrayList<Knoten>();
		
		// Fortschritt nachhalten
		final Fortschritt knotenInGraphenFortschritt = new Fortschritt(wurzel.getKinder().size());
		
		// Fortschrittsanzeigeprozess
		Thread fortschrittsAnzeigerKnotenZuGraphen = new Thread() {
			@Override
			public void run() {
				try {
					while (wurzel.getKinder().size()>0){
						Logger.getLogger(Start.class.getCanonicalName()).info(knotenInGraphenFortschritt.getVerarbeitet()+"/"+knotenInGraphenFortschritt.getVerbleibend());
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
			
			// Pruefen, ob wortvorkommen ueberhalb des Schwellwerts liegen
			if (kind.getZaehler() > minWortvorkommen){
				// Kind in Liste aufnehmen
				zweige.add(kind);
				
				// Knoten in Graphen einfuegen
				Map<String,Node> knotenListe = graph.fuegeKnotenErstellungZurWarteschlangeHinzu(kind.getName(),kind.getZaehler());
				if (knotenListe != null)
				angelegteKnoten.putAll(knotenListe);
			}
			
			// Fortschritt mitzaehlen
			knotenInGraphenFortschritt.setVerarbeitet(knotenInGraphenFortschritt.getVerarbeitet()+1);
		}
		// Ggf. verbleibende Transaktionen anstossen
		angelegteKnoten.putAll(graph.starteTransaktionKnoten());
		
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
		
		/**
		 * Die Liste der Suffixbaumzweige wird durchlaufen, um jedes Element mit jedem zu vergleichen.
		 * Die Wurzel eines jeden Suffixbaumzweigs bildet an dieser Stelle einen Begriff ab.
		 */
		
		// Wiederholen, bis die Liste der Suffixbaumzweige leer ist
		while (!zweige.isEmpty()) {

			// Erstes Element aus Liste extrahieren
			final Knoten knoten = zweige.remove(0);
			
			// Map fuer Verknuepfungen
			final ConcurrentHashMap<String,Double> verknuepfungen = new ConcurrentHashMap<String,Double>();

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

				// Neuen Vergleichsprozess instanziieren und an Exekutor uebergeben
				Runnable prozess = new VergleichsProzess(schwellwert, knoten, vergleichsKnoten, verknuepfungen, fortschritt);
		        exekutor.execute(prozess);
			}
			
			// Exekutor stoppen
			exekutor.shutdown();
			
			// Auf Abschluss der Prozesse warten
	        while (!exekutor.isTerminated()) {
	        	Thread.sleep(100l);
	        }
	        
	        /**
	         * Die Ergebnisliste mit allen Uebereinstimmungsquotienten fuer den aktuellen Knoten
	         * wird im Folgenden durchlaufen und ggf. auf eine Maximalanzahl beschraenkt, wobei
	         * die jeweils staerksten Uebereinstimmungsquotienten Vorrang haben.
	         */
	        
	        // Map fuer staerkste Verknuepfungen erstellen (ggf. limitiert)
	        TreeMap<Double,String> staerksteVerknuepfungen = new TreeMap<Double,String>();
	        
	        // Ergebnisliste durchlaufen
	        Iterator<String> verknuepfteBegriffe = verknuepfungen.keySet().iterator();
	        while(verknuepfteBegriffe.hasNext()){
	        	
	        	// Namen des naechsten Knotens ermitteln
	        	String verknuepfterKnotenName = verknuepfteBegriffe.next();
	        	
	        	// Uebereinstimmungsquotienten des naechsten Knotens ermitteln
	        	Double verknuepfterKnotenUebereinstimmungsquotient = verknuepfungen.get(verknuepfterKnotenName);
	        	
	        	// Pruefen, ob Limit an Verknuepfungen existiert, bzw. Limit noch nicht erreich ist, bzw. der UeQ. des aktuellen Vergleichsknotens hoeher ist, als der niedrigste in der Liste
	        	if (maxBegriffsVerbindungen <= 0 || maxBegriffsVerbindungen > staerksteVerknuepfungen.size() || verknuepfterKnotenUebereinstimmungsquotient > staerksteVerknuepfungen.firstKey()){
	        		// Knoten in Liste aufnehmen
	        		staerksteVerknuepfungen.put(verknuepfterKnotenUebereinstimmungsquotient, verknuepfterKnotenName);
	        		// Ggf. schwaechste Verknuepfung aus der Liste loeschen
	        		if (maxBegriffsVerbindungen != 0 && maxBegriffsVerbindungen < staerksteVerknuepfungen.size()){
	        			staerksteVerknuepfungen.remove(staerksteVerknuepfungen.firstKey());
	        		}
	        	}
	        }

			/**
			 * Die Liste der verbleibenden Verknuepfungen wird durchlaufen und an die Graphen-DB uebertragen. 
			 */
	         
	        // Verknuepfungsliste durchlaufen
	        Iterator<Double> staerksteVerknuepfungsWerte = staerksteVerknuepfungen.keySet().iterator();
	        while (staerksteVerknuepfungsWerte.hasNext()){
	        	
	        	Double wert = staerksteVerknuepfungsWerte.next();
	        	String name = staerksteVerknuepfungen.get(wert);
	        	
	        	// Kante an Graph uebermitteln
	        	graph.fuegeKantenErstellungZurWarteschlangeHinzu(angelegteKnoten.get(knoten.getName()), angelegteKnoten.get(name), wert);
	        	
	        }
	        // Ggf. verbleibende Transaktionen anstossen
	        graph.starteTransaktionKanten();

			// Fortschritt mitzaehlen (fuer Anzeige)
			fortschritt.setVerbleibend(fortschritt.getVerbleibend()-1);

		}
		
		// Graphen schliessen
		graph.beenden();
		

	}

}
