package de.mindlessbloom.suffixtree.experiment06;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
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
import de.mindlessbloom.suffixtree.oanc.WortAnnotationTupel;


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

		// Option fuer Graphenkommunikation hinzufuegen
		optionen.addOption("G", true, "Pfad zum Neo4j-Graphen.");

		// Option fuer Vergleiche hinzufuegen
		optionen.addOption("P", false, "Auch Praefixbaeume erstellen und Vergleichen.");
		
		
		
		/**
		 * Kommandozeilenoptionen auswerten
		 */
		
		// Parser fuer Kommandozeilenoptionen
		CommandLineParser parser = new org.apache.commons.cli.PosixParser();
		CommandLine kommandozeile = parser.parse( optionen, args);
		
		// Ggf. Hilfetext anzeigen
		if (kommandozeile.hasOption("h")) {
			HelpFormatter lvFormater = new HelpFormatter();
			lvFormater.printHelp("java [-server -d64 -Xms500m -Xmx7500m] -jar Experiment6.jar <Optionen>", optionen);
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
			if (Double.parseDouble(kommandozeile.getOptionValue("s")) >= 0.0)
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
		
		// Pfad zur Neo4j-Graphendatei
		String neo4jPfad = "/home/marcel/opt/neo4j-community-2.0.1/data/graph.db";
		if(kommandozeile.hasOption("G")) {
			if (!kommandozeile.getOptionValue("G").isEmpty())
				neo4jPfad = kommandozeile.getOptionValue("G");
		}
		
		// Nur den mit dem Vergleichswort beginnenden Zweig des jeweiligen Suffixbaumes zum Vergleich heranziehen.
		boolean praefixBaumErstellen = (kommandozeile.hasOption("Z") && kommandozeile.hasOption("P"));
		
		// Startobjekt instanziieren
		Start start = new Start();
		
		// Experiment durchfuehren
		start.experiment(oancSpeicherorte, maximaleBaumTiefe, gleichzeitigeProzesse, behalteNurTreffer, schwellwert, minWortvorkommen, maxBegriffsVerbindungen, neo4jPfad, praefixBaumErstellen);
		
	}
	
	public void experiment(String[] oancSpeicherorte, int maximaleBaumTiefe, int gleichzeitigeProzesse, boolean behalteNurTreffer, Double schwellwertEingabe, int minWortvorkommen, int maxBegriffsVerbindungenEingabe, String neo4jPfad, boolean praefixBaumErstellen) throws Exception{
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
		List<List<WortAnnotationTupel>> satzListe = new ArrayList<List<WortAnnotationTupel>>();

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
			
			// Satzgrenzen- und Annotationsdatei auf null setzen; der oancParser ermittelt dann automatisch ihren Namen
			oancParser.setSatzGrenzenXMLDatei(null);
			oancParser.setAnnotationsXMLDatei(null);

			// Datei parsen und Saetze zur Ergebnisliste hinzufuegen
			satzListe.addAll(oancParser.parseQuellDateiMitAnnotationen(true));
		}

		// Meldung ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info(
				"Liste mit " + satzListe.size() + " Saetzen erstellt.");

		// Speicherinfo ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info(
				"Belegter Hauptspeicher: "
						+ (rt.totalMemory() - rt.freeMemory()));

		/**
		 * Map mit allen Begriffen und deren Annotationen erstellen
		 */
		// Map fuer Orthographische Repraesentation - Annotationen
		Map<String,SortedSet<String>> wortAnnotationsMap = new HashMap<String,SortedSet<String>>();
		
		// Satzliste durchlaufen
		Iterator<List<WortAnnotationTupel>> satzlistenIterator1 = satzListe.iterator();
		while (satzlistenIterator1.hasNext()){
			
			// Liste der Worte des Satzes ermitteln
			List<WortAnnotationTupel> wortListe = satzlistenIterator1.next();
			
			// Wortliste durchlaufen
			Iterator<WortAnnotationTupel> worte = wortListe.iterator();
			while(worte.hasNext()){
				
				// Naechstes Wort ermitteln
				WortAnnotationTupel wort = worte.next();
				
				// Pruefen, ob Wort bereits in Map existiert
				if (wortAnnotationsMap.containsKey(wort.getWort())){
					
					// Wort existiert - Annotation zur existierenden Liste hinzufuegen
					wortAnnotationsMap.get(wort.getWort()).add(wort.getAnnotation());
					
				} else {
					
					// Wort existiert noch nicht - neue sortierte Liste fuer Annotationen anlegen
					TreeSet<String> annotationen = new TreeSet<String>();
					annotationen.add(wort.getAnnotation());
					wortAnnotationsMap.put(wort.getWort(), annotationen);
					
				}
			}
		}
		
		/**
		 * Datenstrukturen erstellen. Zur besseren Uebersichtlichkeit werden die
		 * Arbeitsschritte nicht schon in der obigen Schleife durchgefuehrt,
		 * sondern getrennt davon in einer eigenen.
		 */

		// Meldung ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info(
				"Erstelle Suffixbaum (und ggf. Praefixbaum).");

		// Wurzelknoten fuer Suffixbaum erstellen
		final Knoten suffixBaumWurzel = new Knoten();
		suffixBaumWurzel.setName("^");

		// Wurzelknoten fuer Praefixbaum erstellen
		final Knoten praefixBaumWurzel = new Knoten();
		praefixBaumWurzel.setName("$");

		// BaumBauer erstellen
		BaumBauer baumBauer = new BaumBauer();

		// Zaehler fur Saetze (Kosmetik)
		int satzZaehler = 0;

		// Liste der Saetze durchlaufen
		Iterator<List<WortAnnotationTupel>> satzlistenIterator2 = satzListe.iterator();
		while (satzlistenIterator2.hasNext()) {

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

			// Naechste Wort-Annotations-Liste ermitteln
			List<WortAnnotationTupel> satz = satzlistenIterator2.next();
			
			// In String-Array wandeln
			String[] wortArray = new String[satz.size()];
			for (int i=0; i<wortArray.length; i++){
				wortArray[i] = satz.get(i).getWort();
			}
			
			// Satz in Suffixbaum einfuegen
			baumBauer.baueBaum(wortArray, suffixBaumWurzel,null, false, maximaleBaumTiefe);
			
			// Ggf. Satz auch in Praefixbaum einfuegen
			if (praefixBaumErstellen){
				baumBauer.baueBaum(wortArray, praefixBaumWurzel,null, true, maximaleBaumTiefe);
			}
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

		// SBauminfo ausgeben
		if (praefixBaumErstellen){
			Logger.getLogger(Start.class.getCanonicalName()).info(
					"Praefixbaumgroesse: " + praefixBaumWurzel.getZaehler());
		}
		Logger.getLogger(Start.class.getCanonicalName()).info(
				"Suffixbaumgroesse: " + suffixBaumWurzel.getZaehler());
		
		// Die Anzahl der Zweige von Suffix- und Praefixbaum sollte gleich sein
		if (praefixBaumErstellen && suffixBaumWurzel.getKinder().size() != praefixBaumWurzel.getKinder().size()){
			Logger.getLogger(Start.class.getCanonicalName()).warning("Die Anzahl der Zweige von Suffix- und Praefixbaum ist nicht gleich (S:"+suffixBaumWurzel.getKinder().size()+", P:"+praefixBaumWurzel.getKinder().size()+") - ich kann nicht fortfahren, Entschuldigung.");
			System.exit(1);
		}
		
		/*
		 *  Suffixbaumzweige (u. ggf. Praefixbaumzweige) vergleichen und zum Graphen hinzufuegen
		 */
		
		// Graph-Datenbank-Klienten instanziieren
		final Neo4jLokalKlient graph = new Neo4jLokalKlient(neo4jPfad);
		
		// Liste der bereits in der Graphendatenbank angelegten Knoten
		Map<String,Node> angelegteKnoten = new HashMap<String,Node>();
		
		// Meldung anzeigen
		Logger.getLogger(Start.class.getCanonicalName()).info("Der Suffixbaum hat "+suffixBaumWurzel.getKinder().size()+" Zweige.");
		
		
		// Map der Suffixbaumzweige in Liste wandeln
		final List<Knoten> zweige = new ArrayList<Knoten>();
		
		// Fortschritt nachhalten
		final Fortschritt knotenInGraphenFortschritt = new Fortschritt(suffixBaumWurzel.getKinder().size());
		
		// Fortschrittsanzeigeprozess
		Thread fortschrittsAnzeigerKnotenZuGraphen = new Thread() {
			@Override
			public void run() {
				try {
					while (suffixBaumWurzel.getKinder().size()>0){
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
		
		Iterator<String> kinder = suffixBaumWurzel.getKinder().keySet().iterator();
		while(kinder.hasNext()){
			
			// Namen des naechsten Kindelements ermitteln 
			String kindName = kinder.next();
			
			// Knoten ermitteln
			Knoten kind = suffixBaumWurzel.getKinder().get(kindName);
			
			// Pruefen, ob wortvorkommen ueberhalb des Schwellwerts liegen
			if (kind.getZaehler() > minWortvorkommen){
				// Kind in Liste aufnehmen
				zweige.add(kind);
				
				// Knoten in Graphen einfuegen
				Map<String,Node> knotenListe = graph.fuegeKnotenErstellungZurWarteschlangeHinzu(kind.getName(),kind.getZaehler(),wortAnnotationsMap.get(kind.getName()));
				if (knotenListe != null)
				angelegteKnoten.putAll(knotenListe);
			}
			
			// Fortschritt mitzaehlen
			knotenInGraphenFortschritt.setVerarbeitet(knotenInGraphenFortschritt.getVerarbeitet()+1);
		}
		// Ggf. verbleibende Transaktionen anstossen
		angelegteKnoten.putAll(graph.starteTransaktionKnoten());
		
		// Werte aus Map loeschen
		suffixBaumWurzel.getKinder().clear();
		
		
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
				Runnable prozess;
				if (praefixBaumErstellen){
					prozess = new VergleichsProzess(schwellwert, knoten, vergleichsKnoten, praefixBaumWurzel.getKinder().get(knoten.getName()), praefixBaumWurzel.getKinder().get(vergleichsKnoten.getName()), verknuepfungen, fortschritt);
				} else {
					prozess = new VergleichsProzess(schwellwert, knoten, vergleichsKnoten, verknuepfungen, fortschritt);
				}
				
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
