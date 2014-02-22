package de.mindlessbloom.suffixtree;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Test {

	public static void main(String[] args) {
		
		try {
			OANCXMLParser p = new OANCXMLParser(
					new File("/Users/marcel/Downloads/OANC/data/written_1/fiction/eggan/TheStory.txt"));
			
			System.out.println("Satzgrenzendatei: "+p.getSatzGrenzenXMLDatei().getAbsolutePath());
			
			List<String> saetze = p.parseQuellDatei();
			
			Iterator<String> saetzeIt = saetze.iterator();
			while(saetzeIt.hasNext()){
				List<String> satz = p.bereinigeUndSegmentiereSatz(saetzeIt.next());
				Iterator<String> worte = satz.iterator();
				while(worte.hasNext()){
					System.out.print(worte.next()+" ");
				}
				System.out.println(".");
			}
			
		} catch (IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}

	}

}
