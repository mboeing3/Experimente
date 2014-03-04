package de.mindlessbloom.suffixtree;

import java.io.PrintStream;
import java.text.DecimalFormat;

/**
 * Stellt Methoden zur Anzeige von Matrizen zur Verfuegung.
 * @author marcel
 *
 */
public class MatrixPlotter {

	private PrintStream ausgabe;
	private DecimalFormat format = null;
	
	public MatrixPlotter() {
		super();
		this.ausgabe = System.out;
	}
	
	public MatrixPlotter(PrintStream ausgabe) {
		super();
		this.ausgabe = ausgabe;
	}

	public PrintStream getAusgabe() {
		return ausgabe;
	}

	public void setAusgabe(PrintStream ausgabe) {
		this.ausgabe = ausgabe;
	}
	
	public DecimalFormat getFormat() {
		return format;
	}

	public void setFormat(DecimalFormat format) {
		this.format = format;
	}

	/**
	 * Gibt einen String mit der Repraesentation der uebergebenen Matrix zurueck.
	 * @param matrix
	 * @return
	 */
	public String toString(Double[][] matrix){
		return this.toString(matrix, null);
	}

	/**
	 * Gibt einen String mit der Repraesentation der uebergebenen Matrix zurueck.
	 * @param matrix
	 * @param beschriftung String-Array mit Beschrifung
	 * @return
	 */
	public String toString(Double[][] matrix, String[] beschriftung){
		StringBuffer ergebnis = new StringBuffer();
		
		for (int i=0; beschriftung != null && i<beschriftung.length; i++){
			ergebnis.append("\t"+beschriftung[i]);
		}
		ergebnis.append("\n");
		
		for (int i=0; i<matrix.length; i++){
			
			if (beschriftung != null && i<beschriftung.length){
				ergebnis.append(beschriftung[i]+"\t");
			}
			
			for (int j=0; j<matrix[i].length; j++){
				if (matrix[i][j] != null){
					if (this.format != null){
						ergebnis.append(this.format.format(matrix[i][j]));
					} else {
						ergebnis.append(matrix[i][j]);
					}
				} else {
					ergebnis.append("-");
				}
				
				ergebnis.append("\t");
			}
			
			ergebnis.append("\n");
			
		}
		
		return ergebnis.toString();
	}
	
	/**
	 * Gibt die Repraesentation der uebergebenen Matrix aus.
	 * @param matrix
	 */
	public void plot(Double[][] matrix){
		this.ausgabe.println(this.toString(matrix));
	}
	
	/**
	 * Gibt die Repraesentation der uebergebenen Matrix aus.
	 * @param matrix
	 * @param beschriftung String-Array mit Beschrifung
	 */
	public void plot(Double[][] matrix, String[] beschriftung){
		this.ausgabe.println(this.toString(matrix, beschriftung));
	}
	
	
}
