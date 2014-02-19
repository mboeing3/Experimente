package de.mindlessbloom.suffixtree;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class PreParser {
	
	public static final String ZEICHENSATZENDEREGEX = "(?<=\\.)";
	public static final String WORTTRENNER = " ";
	
	public static List<String> parse(String filename) throws IOException{
		ArrayList<String> ergebnisListe = new ArrayList<String>();
		
		/*
		 * Datei einlesen
		 */
		BufferedReader b = null;
		try {
			
			b = new BufferedReader(
					new InputStreamReader(
					new GZIPInputStream(
					new FileInputStream( filename ) ) ) );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		StringBuffer satzUebertrag = new StringBuffer();
		while (b.ready()) {

			String zeile = b.readLine().trim();
			
			if (zeile.isEmpty() || zeile.equals("\n")){
				continue;
			}
			
			String[] punktGetrennt = zeile.split(ZEICHENSATZENDEREGEX);
			
			for (int i=0; i<punktGetrennt.length-1; i++){
				satzUebertrag.append(punktGetrennt[i].trim());
				
				if (punktGetrennt[i].trim().endsWith(".")){
					// Satz zuende
					ergebnisListe.add(satzUebertrag.toString());
					satzUebertrag = new StringBuffer();
				} else {
					satzUebertrag.append(WORTTRENNER);
				}
			}
		}
		
		
		b.close();
		return ergebnisListe;
	}

}
