package de.mindlessbloom.suffixtree.oanc;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import de.mindlessbloom.suffixtree.hilfsmittel.RegAusdruckDateiFilter;
import de.mindlessbloom.suffixtree.hilfsmittel.VerzeichnisFilter;

/**
 * Gibt zentralen Zugriff auf die lokal gespeicherten Daten des OANC
 * @author marcel
 *
 */
public class OANC {

	private String[] oancSpeicherorte = new String[]{"/Users/marcel/Downloads/OANC/data/written_1/","/Users/marcel/Downloads/OANC/data/written_2/"};
	private FileFilter verzeichnisFilter = new VerzeichnisFilter();
	private FileFilter quellDateiFilter = new RegAusdruckDateiFilter(".+\\.txt");
	
	public String[] getOancSpeicherorte() {
		return oancSpeicherorte;
	}

	public void setOancSpeicherorte(String[] oancSpeicherorte) {
		this.oancSpeicherorte = oancSpeicherorte;
	}

	public FileFilter getVerzeichnisFilter() {
		return verzeichnisFilter;
	}

	public void setVerzeichnisFilter(FileFilter verzeichnisFilter) {
		this.verzeichnisFilter = verzeichnisFilter;
	}

	public FileFilter getQuellDateiFilter() {
		return quellDateiFilter;
	}

	public void setQuellDateiFilter(FileFilter quellDateiFilter) {
		this.quellDateiFilter = quellDateiFilter;
	}

	/**
	 * Findet alle Textdateien, die sich am oder unterhalb der OANC-Speicherpfade befinden und gibt sie als Liste zurueck.
	 * @return Liste mit Textdateien
	 * @throws Exception Falls Dateien o. Verzeichnissse nicht gefunden werden oder nicht lesbar sind
	 */
	public List<File> sucheQuellDateien() throws Exception {
		
		// Liste fuer Ergebnis anlegen
		List<File> quellDateiListe = new ArrayList<File>();
		
		// Speicherorte durchlaufen
		for (int i=0; i<this.oancSpeicherorte.length; i++){
			
			// Speicherort-Verzeichnis ermitteln
			File verzeichnis = new File(oancSpeicherorte[i]);
			
			// Rekursive Variante dieser Methode aufrufen
			quellDateiListe.addAll(this.sucheQuellDateien(verzeichnis));
			
		}
		
		// Ergebnisliste zurueckgeben
		return quellDateiListe;
	}
	
	/**
	 * Findet alle Textdateien, die sich im oder unterhalb des uebergebenen Verzeichnis befinden und gibt sie als Liste zurueck.
	 * @param verzeichnis
	 * @return
	 * @throws Exception
	 */
	public List<File> sucheQuellDateien(File verzeichnis) throws Exception {
		
		// Liste fuer Ergebnis anlegen
		List<File> quellDateiListe = new ArrayList<File>();
			
		// Pruefen, ob existent, Verzeichnis und lesbar
		if (!(verzeichnis.isDirectory() && verzeichnis.canRead())) {
			throw new Exception("Der Pfad " + verzeichnis.getAbsolutePath() + " ist nicht lesbar bzw. kein Verzeichnis.");
		}
		
		// Quelldateien suchen
		File[] quelldateien = verzeichnis.listFiles(this.quellDateiFilter);
		for (int i=0; i<quelldateien.length; i++){
			// Fund zur Ergebnisliste hinzufuegen
			quellDateiListe.add(quelldateien[i]);
		}

		// Unterverzeichnisse durchlaufen
		File[] unterverzeichnisse = verzeichnis.listFiles(this.verzeichnisFilter);
		for (int i=0; i<unterverzeichnisse.length; i++){
			// Funktion rekursiv aufrufen
			quellDateiListe.addAll(this.sucheQuellDateien(unterverzeichnisse[i]));
		}
		
		return quellDateiListe;
	}
}
