package de.mindlessbloom.suffixtree.experiment04;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import de.mindlessbloom.nebenlaeufigkeit.Aktion;

public class KnotenPartnerProzessorRueckmeldeAktion extends Aktion {

	private ConcurrentHashMap<String, MetaKnoten> metaKnotenPoolNaechsterEbene;
	
	public KnotenPartnerProzessorRueckmeldeAktion(ConcurrentHashMap<String, MetaKnoten> metaKnotenPoolNaechsterEbene) {
		super();
		this.metaKnotenPoolNaechsterEbene = metaKnotenPoolNaechsterEbene;
	}

	public void ausfuehren(Object prozessErgebnis) {
		
		// Pruefen, ob Ergebnisobjekt vom richtigen Typ ist; andernfalls Warnung ausgeben
		if (!prozessErgebnis.getClass().equals(MetaKnoten.class)){
			Logger.getLogger(this.getClass().getSimpleName()).warning("Ergebnis des Vergleichprozesses ist kein MetaKnoten - und ja, das ist schlimm!");
		}
		
		// Ergebnis auf MetaKnoten casten und zum Pool der naechsten Ebene hinzufuegen
		else {
			MetaKnoten mk = (MetaKnoten) prozessErgebnis;
			metaKnotenPoolNaechsterEbene.put(mk.getKnoten().getName(),mk);
			// Meldung ausgeben
			Logger.getLogger(this.getClass().getSimpleName()).info("MetaKnoten \""+mk.getKnoten().getName()+"\" hinzugefuegt.");
		}
		
	}
}
