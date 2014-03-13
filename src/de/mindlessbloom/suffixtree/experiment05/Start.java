package de.mindlessbloom.suffixtree.experiment05;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

import de.mindlessbloom.suffixtree.BaumBauer;
import de.mindlessbloom.suffixtree.GraphenPlotter;
import de.mindlessbloom.suffixtree.Knoten;
import de.mindlessbloom.suffixtree.OANC;
import de.mindlessbloom.suffixtree.OANCXMLParser;


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
		
		/**
		 * Kommandozeilenoptionen auswerten
		 */
		
		// Parser fuer Kommandozeilenoptionen
		CommandLineParser parser = new org.apache.commons.cli.PosixParser();
		CommandLine kommandozeile = parser.parse( optionen, args);
		
		// Pfade zum OANC.
		String[] oancSpeicherorte = new String[]{"/Users/marcel/Downloads/OANC/data/written_1/","/Users/marcel/Downloads/OANC/data/written_2/"};
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
		
		// Laufzeitinstanz ermitteln
		Runtime rt = Runtime.getRuntime();
		
		// Speicherinfo ausgeben
		Logger.getLogger(Start.class.getCanonicalName()).info("Belegter Hauptspeicher: "+ (rt.totalMemory() - rt.freeMemory()));
		
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
			double prozentFertig = Math
					.ceil(((double) korpusDateiZaehler / (double) korpusDateiListe
							.size()) * 100);
			if (korpusDateiZaehler % (korpusDateiListe.size() / 20) == 0) {
				Logger.getLogger(Start.class.getCanonicalName()).info(
						"Parse " + korpusDateiListe.size()
								+ " Korpusdateien : " + prozentFertig + "%");
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
						rohsaetze.next(), true, true, true, true));
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
		Knoten wurzel = new Knoten();
		wurzel.setName("^");

		// BaumBauer erstellen
		BaumBauer baumBauer = new BaumBauer();

		// Zaehler fur Saetze (Kosmetik)
		int satzZaehler = 0;

		Iterator<List<String>> saetze = satzListe.iterator();
		while (saetze.hasNext()) {

			satzZaehler++;

			// Meldung ausgeben
			double prozentFertig = Math
					.ceil(((double) satzZaehler / (double) satzListe.size()) * 100);
			if (satzZaehler % (satzListe.size() / 20) == 0) {
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

		// Exemplarisch einen Zweig als Graphik ausgeben
		GraphenPlotter g = new GraphenPlotter();
		g.plot(baumBauer.konstruiereGraph(wurzel.getKinder().get("walking")));

	}

}
