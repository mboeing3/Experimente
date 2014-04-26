package de.mindlessbloom.suffixtree.oanc;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class OANCXMLParserTest {
	
	private static String DATEIPFAD = "/home/marcel/data/written_1/journal/verbatim/VOL15_1.txt";
	private static String SGDATEIPFAD = "/home/marcel/data/written_1/journal/verbatim/VOL15_1-s.xml";

	@Test
	public void testOANCXMLParserFile() {
		OANCXMLParser p = null;
		try {
			p = new OANCXMLParser(new File(DATEIPFAD));
		} catch (IOException e) {
			fail(e.toString());
		}
		
		System.out.println("Satzgrenzendatei: "+p.getSatzGrenzenXMLDatei().getAbsolutePath());
		assertTrue(p.getSatzGrenzenXMLDatei().getAbsolutePath().equals(SGDATEIPFAD));
		
	}

	@Test
	public void testParseQuellDatei() {
		OANCXMLParser p = null;
		try {
			p = new OANCXMLParser(new File(DATEIPFAD));
			List<String> saetze = p.parseQuellDatei();
			
			// Es sind in der Satzgrenzendatei 4460 Saetze ausgewiesen.
			assertTrue(saetze.size()==713);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	@Test
	public void testParseQuellDateiAlternativ() {
		OANCXMLParser p = null;
		try {
			System.out.println("Erwartet werden 713 Saetze.");
			p = new OANCXMLParser(new File(DATEIPFAD));
			
			List<List<WortAnnotationTupel>> saetze = p.parseQuellDateiMitAnnotationen(true);
			
			// Es sind in der Satzgrenzendatei 4460 Saetze ausgewiesen.
			System.out.println(saetze.size());
			
			assertTrue(saetze.size()==713);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testBereinigeUndSegmentiereSatz() {
		OANCXMLParser p = null;
		try {
			p = new OANCXMLParser(new File(DATEIPFAD));
			
			String rohsatz = " WortA-WortB: \n\t\t (WortC, WortD),\nWortE  WortF.  ";
			List<String> wortliste = p.bereinigeUndSegmentiereSatz(rohsatz, true, true, true, true);
			
			System.out.println("Anzahl ermittelter Worte: "+wortliste.size());
			assertTrue(wortliste.size()==15);
			
			Iterator<String> worte = wortliste.iterator();
			while(worte.hasNext()){
				System.out.println(worte.next());
			}
			
			
			
		} catch (IOException e) {
			fail(e.toString());
		}
	}

}
