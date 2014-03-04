package de.mindlessbloom.suffixtree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.xml.sax.SAXException;

import de.mindlessbloom.suffixtree.experiment01.KnotenKomparator;
import de.mindlessbloom.suffixtree.experiment01.Start;
import edu.uci.ics.jung.graph.DelegateTree;


public class Test3 {

	public static void main(String[] args) throws Exception {

		/**
		 * Kommandozeilenoptionen definieren
		 */
		
		// Options-Objekt instanziieren
		Options optionen = new Options();

		// Option fuer Pfad zum Korpus hinzufuegen
		optionen.addOption("k", true, "Pfad, auf bzw. unter dem sich die Korpusdateien befinden (Option ist mehrfach anwendbar).");
		
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
		
		// Laufzeitinstanz ermitteln
		Runtime rt = Runtime.getRuntime();
		
		// Speicherinfo ausgeben
		Logger.getLogger(Test3.class.getCanonicalName()).info("Belegter Hauptspeicher: "+ (rt.totalMemory() - rt.freeMemory()));
		
		/**
		 * Korpus einlesen (Ergebnis in Objekt satzListe)
		 */
		
		// Meldung ausgeben
				Logger.getLogger(Test3.class.getCanonicalName()).info("Lese Korpus ein.");
				
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
						Logger.getLogger(Test3.class.getCanonicalName()).info("Parse "+korpusDateiListe.size()+" Korpusdateien : "+prozentFertig+"%");
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
						List<String> satz = oancParser.bereinigeUndSegmentiereSatz(rohsaetze.next(), true, true, true);
						Iterator<String> worte = satz.iterator(); 
						while(worte.hasNext()){
							System.out.print(worte.next()+" ");
						}
						System.out.println();
					}
				}
				
				

	}

}
