package de.mindlessbloom.suffixtree;

import java.util.Iterator;

/**
 * Vergleicht zwei Suffix(teil)baeume miteinander.
 * @author marcel
 *
 */
public class KnotenKomparator2 {
	
	public Double vergleiche(TestNode tropfen, TestNode meer){
		
		fuegeKnotenInAnderenBaumEin(tropfen, meer, false);
		int[] knotenMatches = this.zaehleKnotenMatches(meer);
		System.out.println(knotenMatches[1]+":"+knotenMatches[0]);
		return new Double((double)knotenMatches[1]/(double)knotenMatches[0]);
		
	}
	
	// TODO: Gibt unterschiedliche Werte aus fuer A - B und B - A . Pruefen!
	private int fuegeKnotenInAnderenBaumEin(TestNode knotenTropfen, TestNode knotenMeer, boolean nurZaehlen){
		int einfuegeOperationen = 0;
		
		// Es wird angenommen, dass knotenTropfen == knotenMeer ist - nur die Kinder werden ueberprueft.
		
		// Schleife ueber Kinder des Tropfens
		Iterator<String> tropfenKinder = knotenTropfen.getKinder().keySet().iterator();
		while(tropfenKinder.hasNext()){
			
			String tropfenKindName = tropfenKinder.next();
			
			// Pruefen, ob der Meeresknoten existiert und bereits ein solches Kind hat
			if (knotenMeer != null && knotenMeer.getKinder().containsKey(tropfenKindName)){
				// Kind mit diesem Namen gefunden, markiere Knoten und steige hinab
				knotenTropfen.getKinder().get(tropfenKindName).setMatch(true);
				knotenMeer.getKinder().get(tropfenKindName).setMatch(true);
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
	
	private int[] zaehleKnotenMatches(TestNode knoten){
		int[] knotenMatches = new int[]{0,0};
		
		// Kinder durchlaufen
		Iterator<String> kinder = knoten.getKinder().keySet().iterator();
		while(kinder.hasNext()){
			String kindName = kinder.next();
			int[] kindKnotenMatches = zaehleKnotenMatches(knoten.getKinder().get(kindName));
			knotenMatches[0] += kindKnotenMatches[0];
			knotenMatches[1] += kindKnotenMatches[1];
		}
		
		knotenMatches[0]++; // Gesamtanzahl Knoten
		if (knoten.isMatch()){
			knotenMatches[1]++; // Anzahl Treffer
			knoten.setMatch(false);
		}
		
		return knotenMatches;
	}

}
