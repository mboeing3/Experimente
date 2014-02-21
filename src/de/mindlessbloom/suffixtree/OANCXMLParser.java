package de.mindlessbloom.suffixtree;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class OANCXMLParser {
	
	public static final String SATZGRENZENDATEISUFFIX="-s";
	private File quellDatei;
	private File satzGrenzenXMLDatei;
	
	public OANCXMLParser(File quellDatei) throws IOException {
		this(quellDatei, null);
	}
	
	public OANCXMLParser(File quellDatei, File satzGrenzenXMLDatei) throws IOException {
		super();
		this.quellDatei = quellDatei;
		if (satzGrenzenXMLDatei != null){
			this.satzGrenzenXMLDatei = satzGrenzenXMLDatei;
		} else {
			this.satzGrenzenXMLDatei = new File(quellDatei.getAbsolutePath().substring(0, quellDatei.getAbsolutePath().lastIndexOf('.'))+SATZGRENZENDATEISUFFIX+".xml");
		}

		if (!this.quellDatei.canRead()){
			throw new IOException("Kann Quelldatei nicht lesen: "+this.quellDatei.getAbsolutePath());
		}
		if (!this.satzGrenzenXMLDatei.canRead()){
			throw new IOException("Kann Satzgrenzendatei nicht lesen: "+this.satzGrenzenXMLDatei.getAbsolutePath());
		}
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
		this.satzGrenzenXMLDatei = satzGrenzenXMLDatei;
	}
	
	
	/**
	 * Parst die Quell- und Satzgrenzendatei und gibt eine Liste von Saetzen zurueck
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public List<String> parseQuellDatei() throws SAXException, IOException, ParserConfigurationException{
		ArrayList<String> ergebnisListe = new ArrayList<String>();
		
		// XML-Satzgrenzendatei parsen
		SAXParserFactory parserFactor = SAXParserFactory.newInstance();
	    SAXParser parser = parserFactor.newSAXParser();
	    OANCXMLHandler handler = new OANCXMLHandler();
	    parser.parse(ClassLoader.getSystemResourceAsStream(satzGrenzenXMLDatei.getAbsolutePath()), 
	                 handler);

	    // Quelldatei oeffnen
	    FileReader datei = new FileReader(this.quellDatei);
	    
	    // Liste der Satzgrenzen durchlaufen
	    Iterator<OANCXMLSatzgrenze> satzgrenzen = handler.getSatzgrenzen().iterator();
	    while(satzgrenzen.hasNext()){
	    	
	    	// Naechste Satzgrenze ermitteln
	    	OANCXMLSatzgrenze satzgrenze = satzgrenzen.next();
	    	
	    	// Laenge des zu lesenden Satzes ermitteln
	    	int satzlaenge = satzgrenze.getBis() - satzgrenze.getVon();
	    	
	    	// Zeichenarray mit entsprechender Laenge erstellen
	    	char[] satzZeichenArray = new char[satzlaenge];
	    	
	    	// Zeichen aus Quelldatei in ZeichenArray einlesen
	    	datei.read(satzZeichenArray, satzgrenze.getVon(), satzlaenge);
	    	
	    	// Zeichenarray in String umwandeln und in Ergebnisliste speichern
	    	ergebnisListe.add(String.copyValueOf(satzZeichenArray));
	    	
	    }
	    
	    
	    // Quelldatei schliessen
		datei.close();
	    
		// Ergebnisliste zurueckgeben
		return ergebnisListe;
	}

}
