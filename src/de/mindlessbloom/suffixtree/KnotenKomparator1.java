package de.mindlessbloom.suffixtree;

import java.util.Iterator;

/**
 * Vergleicht zwei Suffix(teil)baeume miteinander.
 * @author marcel
 *
 */
public class KnotenKomparator1 {
	
	public int vergleiche(Knoten knoten, Knoten vergleichsknoten){
		
		return fuegeKnotenInAnderenBaumEin(knoten, vergleichsknoten, true);
		
	}
	
	// TODO: Gibt unterschiedliche Werte aus fuer A - B und B - A . Pruefen!
	private int fuegeKnotenInAnderenBaumEin(Knoten knotenTropfen, Knoten knotenMeer, boolean nurZaehlen){
		int einfuegeOperationen = 0;
		
		// Es wird angenommen, dass knotenTropfen == knotenMeer ist - nur die Kinder werden ueberprueft.
		
		// Schleife ueber Kinder des Tropfens
		Iterator<String> tropfenKinder = knotenTropfen.getKinder().keySet().iterator();
		while(tropfenKinder.hasNext()){
			
			String tropfenKindName = tropfenKinder.next();
			
			// Pruefen, ob der Meeresknoten existiert und bereits ein solches Kind hat
			if (knotenMeer != null && knotenMeer.getKinder().containsKey(tropfenKindName)){
				// Kind mit diesem Namen gefunden, steige hinab
				einfuegeOperationen += fuegeKnotenInAnderenBaumEin(knotenTropfen.getKinder().get(tropfenKindName), knotenMeer.getKinder().get(tropfenKindName), nurZaehlen);
			} else {
				// Kein Kind mit diesem Namen gefunden
				if (nurZaehlen){
					// Es wird kein Kind erzeugt, sondern nur gezaehlt
					//dieser Zweig ist "tot" und zaehlt mit jedem folgenden Knoten als Einfuegeoperation
					einfuegeOperationen += 1+fuegeKnotenInAnderenBaumEin(knotenTropfen.getKinder().get(tropfenKindName), null, nurZaehlen);
					
				} else {
					// Kind einfuegen (liesse sich abkuerzen, wenn nicht gezaehlt wuerde)
					knotenMeer.getKinder().put(tropfenKindName, knotenTropfen.getKinder().get(tropfenKindName));
					einfuegeOperationen += 1+fuegeKnotenInAnderenBaumEin(knotenTropfen.getKinder().get(tropfenKindName), knotenMeer.getKinder().get(tropfenKindName), nurZaehlen);
				}
				
			}
			
			
		}
		
		return einfuegeOperationen;
	}

}
