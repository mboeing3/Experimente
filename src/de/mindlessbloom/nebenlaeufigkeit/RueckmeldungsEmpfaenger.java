package de.mindlessbloom.nebenlaeufigkeit;

/**
 * 
 * @author marcel
 *
 */
public interface RueckmeldungsEmpfaenger {

	public void empfangeRueckmeldung(Object ergebnis, RueckmeldeProzess prozess);
	
	public void empfangeRueckmeldung(Object ergebnis, RueckmeldeProzess prozess, boolean wiederholend);
	
	public void empfangeAusnahme(Exception e);
	
}
