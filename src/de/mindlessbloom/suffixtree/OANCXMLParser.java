package de.mindlessbloom.suffixtree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class OANCXMLParser {
	
	public static final String TERMINIERSYMBOL="$";
	public static final String SATZGRENZENDATEISUFFIX="-s";
	public static final String WORTTRENNERREGEX = "[\\ \\n\\t]+";
	public static final String ZUENTFERNENDEZEICHENREGEX = "[\\.\\,\\;\\\"]*";
	private File quellDatei;
	private File satzGrenzenXMLDatei;
	
	public OANCXMLParser() {
		super();
	}
	
	public OANCXMLParser(File quellDatei) throws IOException {
		this(quellDatei, null);
	}
	
	public OANCXMLParser(File quellDatei, File satzGrenzenXMLDatei) throws IOException {
		super();
		this.quellDatei = quellDatei;
		this.setSatzGrenzenXMLDatei(satzGrenzenXMLDatei);
	}
	public File getQuellDatei() {
		return quellDatei;
	}
	public void setQuellDatei(File quellDatei) {
		this.quellDatei = quellDatei;
	}
	public File getSatzGrenzenXMLDatei() {
		return satzGrenzenXMLDatei;
	}
	public void setSatzGrenzenXMLDatei(File satzGrenzenXMLDatei) {
		if (satzGrenzenXMLDatei != null){
			this.satzGrenzenXMLDatei = satzGrenzenXMLDatei;
		} else {
			this.satzGrenzenXMLDatei = new File(quellDatei.getAbsolutePath().substring(0, quellDatei.getAbsolutePath().lastIndexOf('.'))+SATZGRENZENDATEISUFFIX+".xml");
		}
	}
	
	
	/**
	 * Parst die Quell- und Satzgrenzendatei und gibt eine Liste von (Roh)Saetzen zurueck
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public List<String> parseQuellDatei() throws SAXException, IOException, ParserConfigurationException{
		
		// Zugriff auf Dateien pruefen
		if (!this.quellDatei.canRead()){
			throw new IOException("Kann Quelldatei nicht lesen: "+this.quellDatei.getAbsolutePath());
		}
		if (!this.satzGrenzenXMLDatei.canRead()){
			throw new IOException("Kann Satzgrenzendatei nicht lesen: "+this.satzGrenzenXMLDatei.getAbsolutePath());
		}
		
		// Liste fuer Ergebnis
		ArrayList<String> ergebnisListe = new ArrayList<String>();
		
		// XML-Satzgrenzendatei parsen
		SAXParserFactory parserFactor = SAXParserFactory.newInstance();
	    SAXParser parser = parserFactor.newSAXParser();
	    OANCXMLHandler handler = new OANCXMLHandler();
	    InputStream satzgrenzenInputStream = new FileInputStream(satzGrenzenXMLDatei);
	    parser.parse(satzgrenzenInputStream, handler);

	    // Quelldatei oeffnen
	    FileReader datei = new FileReader(this.quellDatei);
	    
	    // Markierung fuer Leselposition in der Quelldatei
    	int position = 0;
	    
	    // Liste der Satzgrenzen durchlaufen
	    Iterator<OANCXMLSatzgrenze> satzgrenzen = handler.getSatzgrenzen().iterator();
	    while(satzgrenzen.hasNext()){
	    	
	    	// Naechste Satzgrenze ermitteln
	    	OANCXMLSatzgrenze satzgrenze = satzgrenzen.next();
	    	
	    	// Laenge des zu lesenden Satzes ermitteln
	    	int satzlaenge = satzgrenze.getBis() - satzgrenze.getVon();
	    	
	    	// Zeichenarray mit entsprechender Laenge erstellen
	    	char[] satzZeichenArray = new char[satzlaenge];
	    	
	    	//System.out.println("Lese Zeichen "+satzgrenze.getVon()+" bis "+satzgrenze.getBis() +", Laenge:"+satzlaenge);
	    	
	    	// Ggf. in der Quelldatei zum naechsten Satzanfang springen
	    	if (satzgrenze.getVon()>position){
	    		datei.skip(satzgrenze.getVon()-position);
	    		position = satzgrenze.getVon();
	    	}
	    	
	    	// Zeichen aus Quelldatei in ZeichenArray einlesen
	    	if (datei.ready() && position<satzgrenze.getBis()){
	    		datei.read(satzZeichenArray, 0, satzlaenge);
	    		position = satzgrenze.getBis();
	    	}
	    	
	    	
	    	// Zeichenarray in String umwandeln und in Ergebnisliste speichern
	    	ergebnisListe.add(String.copyValueOf(satzZeichenArray));
	    	
	    }
	    
	    
	    // Quelldatei schliessen
		datei.close();
	    
		// Ergebnisliste zurueckgeben
		return ergebnisListe;
	}
	
	/**
	 * Bereinigt und segmentiert den uebergebenen Satz. Entfernt Zeilenumbrueche, Tabulatoren, Leerzeichen, Punktiuation.
	 * Fuegt ggf. am Ende das Terminiersymbol ein.
	 * @param rohsatz
	 * @return Wortliste
	 */
	public List<String> bereinigeUndSegmentiereSatz(String rohsatz, boolean fuegeTerminierSymbolEin, boolean wandleZuKleinbuchstaben){
		List<String> ergebnisListe = new ArrayList<String>();
		
		// Satz segmentieren
		String[] segmente = rohsatz.split(OANCXMLParser.WORTTRENNERREGEX);
		
		// Segmente durchlaufen
		for (int i=0; i<segmente.length; i++){
			// Segment bereinigen und in Ergebnis speichern
			String segment = segmente[i].replaceAll(ZUENTFERNENDEZEICHENREGEX, "").trim();
			// Ggf. zu Kleinbuchstaben wandeln
			if (wandleZuKleinbuchstaben){
				segment = segment.toLowerCase();
			}
			if (!segment.isEmpty())
				ergebnisListe.add(segment.intern());
		}
		
		// Ggf. Terminiersymbol einfuegen
		if (fuegeTerminierSymbolEin){
			ergebnisListe.add(OANCXMLParser.TERMINIERSYMBOL);
		}
		
		// Ergebnisliste zurueckgeben
		return ergebnisListe;
	}

}
