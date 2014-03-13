package de.mindlessbloom.nebenlaeufigkeit;

public interface RueckmeldeProzess extends Runnable {

	public RueckmeldungsEmpfaenger getCallbackReceiver();
	public void setCallbackReceiver(RueckmeldungsEmpfaenger callbackReceiver);
	
}
