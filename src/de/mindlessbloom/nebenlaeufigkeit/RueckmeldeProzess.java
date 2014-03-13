package de.mindlessbloom.nebenlaeufigkeit;

public interface RueckmeldeProzess extends Runnable {

	public RueckmeldungsEmpfaenger getRueckmeldungsEmpfaenger();
	public void setRueckmeldungsEmpfaenger(RueckmeldungsEmpfaenger rueckmeldungsEmpfaenger);
	
}
