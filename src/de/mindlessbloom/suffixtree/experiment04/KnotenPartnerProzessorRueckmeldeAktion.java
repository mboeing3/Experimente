package de.mindlessbloom.suffixtree.experiment04;

import java.util.List;
import java.util.logging.Logger;

import de.mindlessbloom.nebenlaeufigkeit.Aktion;

public class KnotenPartnerProzessorRueckmeldeAktion extends Aktion {

	private List<MetaKnoten> metaKnotenPoolNaechsterEbene;
	
	public KnotenPartnerProzessorRueckmeldeAktion(List<MetaKnoten> metaKnotenPoolNaechsterEbene) {
		super();
		this.metaKnotenPoolNaechsterEbene = metaKnotenPoolNaechsterEbene;
	}

	public void ausfuehren(Object prozessErgebnis) {
		
		// Pruefen, ob Ergebnisobjekt vom richtigen Typ ist; andernfalls Warnung ausgeben
		if (!prozessErgebnis.getClass().equals(MetaKnoten.class)){
			Logger.getLogger(this.getClass().getSimpleName()).warning("Ergebnis des Vergleichprozesses ist kein MetaKnoten.");
		}
		
		// Ergebnis auf MetaKnoten casten und zum Pool der naechsten Ebene hinzufuegen
		else {
			MetaKnoten mk = (MetaKnoten) prozessErgebnis;
			synchronized(this){
				metaKnotenPoolNaechsterEbene.add(mk);
			}
			// Meldung ausgeben
			Logger.getLogger(this.getClass().getSimpleName()).info("MetaKnoten \""+mk.getKnoten().getName()+"\" hinzugefuegt.");
		}
		
	}
}
