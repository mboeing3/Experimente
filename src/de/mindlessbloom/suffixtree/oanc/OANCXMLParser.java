package de.mindlessbloom.suffixtree.oanc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class OANCXMLParser {

	public static final String STARTSYMBOL = "^";
	public static final String TERMINIERSYMBOL="$";
	public static final String SATZGRENZENDATEISUFFIX="-s";
	public static final String ANNOTATIONSDATEISUFFIX="-hepple";
	public static final String WORTTRENNERREGEX = "[\\ \\n\\t\u200B]+";
	public static final Pattern WORTTRENNERREGEXMUSTER = Pattern.compile(WORTTRENNERREGEX);
	//public static final String ZEICHENSETZUNGSREGEX = "((?<=[\\[\\]\\(\\)\\?\\!\\-\\/\\.\\,\\;\\:\\\"\\'\\…])|(?=[\\[\\]\\(\\)\\?\\!\\-\\/\\.\\,\\;\\:\\\"\\'\\…]))"; // <String>.split() trennt hiermit Zeichen ab und behaelt sie als Elemente
	public static final String SATZZEICHENABTRENNERREGEX = "((?<=[\\[\\(\\]\\)]|[^(\\p{L}\\p{M}*+)])|(?=[\\[\\(\\]\\)]|[^(\\p{L}\\p{M}*+)]))";
	public static final Pattern SATZZEICHENABTRENNERREGEXMUSTER = Pattern.compile(SATZZEICHENABTRENNERREGEX);
	public static final String ZUENTFERNENDEZEICHENREGEX = "[^(\\p{L}\\p{M}*+)]*";
	public static final Pattern ZUENTFERNENDEZEICHENREGEXMUSTER = Pattern.compile(ZUENTFERNENDEZEICHENREGEX);
	// Annotationsbezeichner
	public static final String XML_PENNTAG_BEZEICHNER = "msd";
	public static final String XML_BEGRIFF_BEZEICHNER = "base";
	private File quellDatei;
	private File satzGrenzenXMLDatei;
	private File annotationsXMLDatei;
	
	public OANCXMLParser() {
		super();
	}
	
	public OANCXMLParser(File quellDatei) throws IOException {
		this(quellDatei, null, null);
	}
	
	public OANCXMLParser(File quellDatei, File satzGrenzenXMLDatei, File annotationsXMLDatei) throws IOException {
		super();
		this.quellDatei = quellDatei;
		this.setSatzGrenzenXMLDatei(satzGrenzenXMLDatei);
		this.setAnnotationsXMLDatei(annotationsXMLDatei);
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
	
	
	public File getAnnotationsXMLDatei() {
		return annotationsXMLDatei;
	}

	public void setAnnotationsXMLDatei(File annotationsXMLDatei) {
		if (annotationsXMLDatei != null){
			this.annotationsXMLDatei = annotationsXMLDatei;
		} else {
			this.annotationsXMLDatei = new File(quellDatei.getAbsolutePath().substring(0, quellDatei.getAbsolutePath().lastIndexOf('.'))+ANNOTATIONSDATEISUFFIX+".xml");
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
	    OANCSatzgrenzenXMLHandler handler = new OANCSatzgrenzenXMLHandler();
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
	    	ergebnisListe.add(String.copyValueOf(satzZeichenArray).intern());
	    	
	    }
	    
	    
	    // Quelldatei schliessen
		datei.close();
	    
		// Ergebnisliste zurueckgeben
		return ergebnisListe;
	}
	
	/**
	 * Bereinigt und segmentiert den uebergebenen Satz. Entfernt Zeilenumbrueche, Tabulatoren, Leerzeichen, Punktuation.
	 * Fuegt ggf. am Ende das Terminiersymbol ein.
	 * @param rohsatz
	 * @return Wortliste
	 */
	public List<String> bereinigeUndSegmentiereSatz(String rohsatz, boolean fuegeStartSymbolEin, boolean fuegeTerminierSymbolEin, boolean wandleZuKleinbuchstaben, boolean behalteSatzzeichenAlsToken){
		List<String> ergebnisListe = new ArrayList<String>();
		
		// Satz segmentieren
		String[] segmente = OANCXMLParser.WORTTRENNERREGEXMUSTER.split(rohsatz);
		
		// Ggf. Startsymbol einfuegen
		if (fuegeStartSymbolEin){
			ergebnisListe.add(OANCXMLParser.STARTSYMBOL);
		}
		
		// Segmente durchlaufen
		for (int i=0; i<segmente.length; i++){
			
			// Wort, ggf. mit Zeichensetzung, daher als Array
			String[] segment;
			
			// Satzzeigen als Token behalten oder entfernen?
			if (behalteSatzzeichenAlsToken){
				// Zeichensetzung trennen
				segment = SATZZEICHENABTRENNERREGEXMUSTER.split(segmente[i]);
			} else {
				// Segment bereinigen und in Ergebnis speichern
				segment = new String[]{segmente[i].replaceAll(ZUENTFERNENDEZEICHENREGEX, "").trim()};
			}
			
			// Schleife ueber Token des Segments
			for (int j=0; j<segment.length; j++){
				// Ggf. zu Kleinbuchstaben wandeln
				if (wandleZuKleinbuchstaben){
					segment[j] = segment[j].toLowerCase().intern();
				}
				if (!segment[j].isEmpty())
					ergebnisListe.add(segment[j].intern());
			}
			
			
		}
		
		// Ggf. Terminiersymbol einfuegen
		if (fuegeTerminierSymbolEin){
			ergebnisListe.add(OANCXMLParser.TERMINIERSYMBOL);
		}
		
		// Ergebnisliste zurueckgeben
		return ergebnisListe;
	}
	
	/**
	 * Parst die Quell-, Annotations- und Satzgrenzendatei und gibt eine Liste von Saetzen mit annotierten Worten zurueck.
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public List<List<WortAnnotationTupel>> parseQuellDateiMitAnnotationen() throws SAXException, IOException, ParserConfigurationException{
		
		// Zugriff auf Dateien pruefen
		if (!this.quellDatei.canRead()){
			throw new IOException("Kann Quelldatei nicht lesen: "+this.quellDatei.getAbsolutePath());
		}
		if (!this.satzGrenzenXMLDatei.canRead()){
			throw new IOException("Kann Satzgrenzendatei nicht lesen: "+this.satzGrenzenXMLDatei.getAbsolutePath());
		}
		
		// Liste fuer Ergebnis
		ArrayList<List<WortAnnotationTupel>> ergebnisListe = new ArrayList<List<WortAnnotationTupel>>();
		
		// XML-Satzgrenzendatei parsen
		SAXParserFactory satzgrenzenParserFactory = SAXParserFactory.newInstance();
	    SAXParser parser = satzgrenzenParserFactory.newSAXParser();
	    OANCSatzgrenzenXMLHandler satzgrenzenHandler = new OANCSatzgrenzenXMLHandler();
	    InputStream satzgrenzenInputStream = new FileInputStream(satzGrenzenXMLDatei);
	    parser.parse(satzgrenzenInputStream, satzgrenzenHandler);
		
		// XML-Annotationsdatei parsen
		SAXParserFactory annotationsParserFactory = SAXParserFactory.newInstance();
	    SAXParser annotationsParser = annotationsParserFactory.newSAXParser();
	    OANCAnnotationsXMLHandler annotationsHandler = new OANCAnnotationsXMLHandler();
	    InputStream annotationsInputStream = new FileInputStream(annotationsXMLDatei);
	    annotationsParser.parse(annotationsInputStream, annotationsHandler);
	    
	    // Liste der geparsten Wortannotationen (mit deren Position im Text!)
	    List<OANCXMLAnnotation> annotationsListe = annotationsHandler.getAnnotationen();

	    // Quelldatei oeffnen
	    FileReader datei = new FileReader(this.quellDatei);
	    
	    // Markierung fuer Leselposition in der Quelldatei
    	int position = 0;

    	// Iterator fuer Wortannotationen
    	Iterator<OANCXMLAnnotation> annotationen = annotationsListe.iterator();
    	
    	// Falls keine Wortannotation vorhanden ist, wird die leere Ergebnisliste zurueckgegeben
    	if (!annotationen.hasNext()){
    		// Quelldatei schliessen
    		datei.close();
    		// Leere Liste zurueckgeben
    		return ergebnisListe;
    	}
    	
    	// Erste Wortannotation ermitteln
    	OANCXMLAnnotation annotation = annotationen.next();
    	
	    // Liste der Satzgrenzen durchlaufen
	    Iterator<OANCXMLSatzgrenze> satzgrenzen = satzgrenzenHandler.getSatzgrenzen().iterator();
	    while(satzgrenzen.hasNext()){
	    	
	    	// Liste fuer neuen Satz
	    	List<WortAnnotationTupel> satz = new ArrayList<WortAnnotationTupel>();
	    	
	    	// Naechste Satzgrenze ermitteln
	    	OANCXMLSatzgrenze satzgrenze = satzgrenzen.next();
	    	
	    	while (annotation != null && annotation.getVon() < satzgrenze.getBis()){
	    		
	    		// Laenge des zu lesenden Wortes ermitteln
		    	int wortlaenge = annotation.getBis() - annotation.getVon();
		    	
		    	// Zeichenarray mit entsprechender Laenge erstellen
		    	char[] wortZeichenArray = new char[wortlaenge];
	    		
	    		// Ggf. in der Quelldatei zum Wortanfang springen
		    	if (annotation.getVon()>position){
		    		datei.skip(annotation.getVon()-position);
		    		position = annotation.getVon();
		    	}
	    		
		    	// Zeichen aus Quelldatei in ZeichenArray einlesen
		    	if (datei.ready() && position<annotation.getBis()){
		    		datei.read(wortZeichenArray, 0, wortlaenge);
		    		position = satzgrenze.getBis();
		    	}
		    	
		    	// Zeichenkette umwandeln und internalisieren (letzteres wichtig fuer Minimierung der Speicherauslastung)
		    	String wortString = String.copyValueOf(wortZeichenArray).intern();
	    		
		    	// Annotiertes Wort zum Satz hinzufuegen
	    		satz.add(new WortAnnotationTupel(wortString,annotation.getAnnotationswerte().get(XML_PENNTAG_BEZEICHNER),annotation.getAnnotationswerte().get(XML_BEGRIFF_BEZEICHNER)));
	    		
	    		// Naechstes Wort ermitteln
	    		annotation = annotationen.next();
	    	}
	    	
	    	// Abgeschlossenen Satz in Ergebnisliste speichern
	    	ergebnisListe.add(satz);
	    	
	    }
	    
	    // Quelldatei schliessen
		datei.close();
	    
		// Ergebnisliste zurueckgeben
		return ergebnisListe;
	}

}
