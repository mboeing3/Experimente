package de.mindlessbloom.suffixtree.oanc;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class OANCXMLParserTest {

	@Test
	public void testOANCXMLParserFile() {
		OANCXMLParser p = null;
		try {
			p = new OANCXMLParser(new File("/Users/marcel/Downloads/OANC/data/written_1/fiction/eggan/TheStory.txt"));
		} catch (IOException e) {
			fail(e.toString());
		}
		
		System.out.println("Satzgrenzendatei: "+p.getSatzGrenzenXMLDatei().getAbsolutePath());
		assertTrue(p.getSatzGrenzenXMLDatei().getAbsolutePath().equals("/Users/marcel/Downloads/OANC/data/written_1/fiction/eggan/TheStory-s.xml"));
		
	}

	@Test
	public void testParseQuellDatei() {
		OANCXMLParser p = null;
		try {
			p = new OANCXMLParser(new File("/Users/marcel/Downloads/OANC/data/written_1/fiction/eggan/TheStory.txt"));
			List<String> saetze = p.parseQuellDatei();
			
			// Es sind in der Satzgrenzendatei 4460 Saetze ausgewiesen.
			assertTrue(saetze.size()==4460);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	@Test
	public void testBereinigeUndSegmentiereSatz() {
		OANCXMLParser p = null;
		try {
			p = new OANCXMLParser(new File("/Users/marcel/Downloads/OANC/data/written_1/fiction/eggan/TheStory.txt"));
			
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
