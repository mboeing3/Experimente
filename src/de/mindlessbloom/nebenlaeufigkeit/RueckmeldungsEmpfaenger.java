package de.mindlessbloom.nebenlaeufigkeit;

/**
 * 
 * @author marcel
 *
 */
public interface RueckmeldungsEmpfaenger {

	public void receiveCallback(Object ergebnis, RueckmeldeProzess prozess);
	
	public void receiveCallback(Object ergebnis, RueckmeldeProzess prozess, boolean wiederholend);
	
	public void receiveException(Exception e);
	
}
