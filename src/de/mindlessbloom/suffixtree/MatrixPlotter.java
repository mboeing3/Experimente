package de.mindlessbloom.suffixtree;

import java.io.PrintStream;

/**
 * Stellt Methoden zur Anzeige von Matrizen zur Verfuegung.
 * @author marcel
 *
 */
public class MatrixPlotter {

	private PrintStream ausgabe;
	
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

	/**
	 * Gibt einen String mit der Repraesentation der uebergebenen Matrix zurueck.
	 * @param matrix
	 * @return
	 */
	public String toString(Double[][] matrix){
		StringBuffer ergebnis = new StringBuffer();
		
		for (int i=0; i<matrix.length; i++){
			
			for (int j=0; j<matrix[i].length; j++){
				if (matrix[i][j] != null){
					ergebnis.append(matrix[i][j]);
				} else {
					ergebnis.append("X");
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
	
	
}
