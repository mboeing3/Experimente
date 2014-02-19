package de.mindlessbloom.suffixtree;

import java.util.Iterator;

/**
 * Vergleicht zwei Suffix(teil)baeume miteinander.
 * @author marcel
 *
 */
public class KnotenKomparator3 {
	
	public static final String STELLVERTRETER = "######";
	
	public Double vergleiche(TestNode k1, TestNode k2){
		
		//TestNode angeglichenerBaum1 = ersetzeKnotenDurchStellvertreter(ersetzeKnotenDurchStellvertreter(k1,k2.getName()),k1.getName());
		//TestNode angeglichenerBaum2 = ersetzeKnotenDurchStellvertreter(ersetzeKnotenDurchStellvertreter(k2,k1.getName()),k2.getName());
		
		TestNode verschmolzenerBaum = verschmelzeBaeume(k1, k2);
		int[] trefferWert = this.ermittleKnotenTrefferwert(verschmolzenerBaum);
		System.out.println(trefferWert[0]+":"+trefferWert[1]);
		return new Double((double)trefferWert[1]/(double)trefferWert[0]);
		
	}
	
	/**
	 * Gibt Kopie des uebergebenen Baumes zurueck, in dem alle Vorkommen des uebergebenen Knotennamen durch den Wert der statischen Variable STELLVERTRETER ersetzt wurden.
	 * @param knoten
	 * @param knotenName
	 * @return
	 */
	/*public TestNode ersetzeKnotenDurchStellvertreter(TestNode knoten, String knotenName){
		
		// Neuen Knoten erstellen
		TestNode neuerKnoten = new TestNode();
		
		// Werte uebernehmen
		neuerKnoten.setZaehler(knoten.getZaehler());
		neuerKnoten.setMatch(knoten.isMatch());
		
		// Namen pruefen und ggf. ersetzen
		if (knoten.getName().equals(knotenName)){
			neuerKnoten.setName(STELLVERTRETER);
		} else {
			neuerKnoten.setName(knoten.getName());
		}
		
		// Kinder rekursiv aufrufen
		Iterator<String> kinder = knoten.getKinder().keySet().iterator();
		while(kinder.hasNext()){
			TestNode neuesKind = ersetzeKnotenDurchStellvertreter(knoten.getKinder().get(kinder.next()), knotenName);
			
			// Ggf. Knoten verschmelzen (falls bereits Knoten mit Stellvertreternamen vorhanden)
			if (neuesKind.getName().equals(STELLVERTRETER) && neuerKnoten.getKinder().containsKey(STELLVERTRETER)){
				neuesKind = verschmelzeBaeume(neuesKind, neuerKnoten.getKinder().get(neuesKind.getName()));
			}
			
			neuerKnoten.getKinder().put(neuesKind.getName(), neuesKind);
		}
		
		// Neu erstellten Knoten zurueckgeben
		return neuerKnoten;
		
	}*/
	
	/**
	 * Verschmilzt die Baeume ab der ersten Kindebene des jeweils uebergebenen Knoten und gibt den Wurzelknoten des (neuen) Ergebnisbaumes zurueck.
	 * Uebereinstimmungen sind im Ergebnisbaum via isMatch() markiert; Zaehler der Knoten wurden im Ergebnisbaum aufaddiert. 
	 * @param knoten1
	 * @param knoten2
	 * @param gleichZuSetzendeWoerter Array von Woertern, die nicht als unterschiedlich gewertet werden sollen. Diese werden im Ergebnisbaum mit dem Wert der Variable WORTPLATZHALTER eingefuegt.
	 * @return Wurzelknoten des neuen Baumes
	 */
	public TestNode verschmelzeBaeume(TestNode knoten1, TestNode knoten2){
		
		// Es wird angenommen, dass knotenTropfen == knotenMeer ist - nur die Kinder werden ueberprueft.
		
		// Neuen Knoten erzeugen
		TestNode ergebnisKnoten = new TestNode();
		
		// Ggf. Werte der uebergebenen Knoten aufaddieren und Kinder hinzufuegen
		if (knoten1 != null){
			ergebnisKnoten.setZaehler(ergebnisKnoten.getZaehler()+knoten1.getZaehler());
			ergebnisKnoten.setName(knoten1.getName());
			
			// Schleife ueber Kinder des ersten Knotens
			Iterator<String> k1Kinder = knoten1.getKinder().keySet().iterator();
			while(k1Kinder.hasNext()){
				
				// Variable fuer neuen Kindknoten definieren
				TestNode kindKnoten;
				
				// Name des Kindes von Knoten1 ermitteln
				String k1KindName = k1Kinder.next();
				
				// Pruefen, ob Knoten2 existiert und ebenfalls ein solches Kind hat
				if (knoten2 != null && knoten2.getKinder().containsKey(k1KindName)){
					// Kind mit diesem Namen gefunden, steige hinab
					kindKnoten = this.verschmelzeBaeume(knoten1.getKinder().get(k1KindName), knoten2.getKinder().get(k1KindName));
					
					// Markiere Knoten als Treffer
					kindKnoten.setMatch(true);
					
				} else {
					// Kein Kind mit diesem Namen gefunden oder Knoten2 ist Null, steige hinab
					kindKnoten = this.verschmelzeBaeume(knoten1.getKinder().get(k1KindName), null);
					
				}
				
				// Neuen Kindknoten an Ergebnis anfuegen
				ergebnisKnoten.getKinder().put(k1KindName, kindKnoten);
				
			}
		}
		if (knoten2 != null){
			ergebnisKnoten.setZaehler(ergebnisKnoten.getZaehler()+knoten2.getZaehler());
			
			// Namen ggf. anhaengen
			if (knoten1 != null && !(knoten1.getName().equals(knoten2.getName()))){
				ergebnisKnoten.setName(knoten1.getName()+"/"+knoten2.getName());
			} else {
				ergebnisKnoten.setName(knoten2.getName());
			}
			
			
			// Schleife ueber Kinder des ersten Knotens
			Iterator<String> k2Kinder = knoten2.getKinder().keySet().iterator();
			while(k2Kinder.hasNext()){
				
				// Variable fuer neuen Kindknoten definieren
				TestNode kindKnoten;
				
				// Name des Kindes von Knoten1 ermitteln
				String k2KindName = k2Kinder.next();
				
				// Falls dieser Knoten schon im Ergebnis existiert, kann abgebrochen werden
				if (ergebnisKnoten.getKinder().containsKey(k2KindName)){
					continue;
				}
				
				// Pruefen, ob Knoten2 existiert und ebenfalls ein solches Kind hat
				if (knoten1 != null && knoten1.getKinder().containsKey(k2KindName)){
					// Kind mit diesem Namen gefunden, steige hinab
					kindKnoten = this.verschmelzeBaeume(knoten1.getKinder().get(k2KindName), knoten2.getKinder().get(k2KindName));
					
					// Markiere Knoten als Treffer
					kindKnoten.setMatch(true);
					
				} else {
					// Kein Kind mit diesem Namen gefunden oder Knoten2 ist Null, steige hinab
					kindKnoten = this.verschmelzeBaeume(null, knoten2.getKinder().get(k2KindName));
					
				}
				
				// Neuen Kindknoten an Ergebnis anfuegen
				ergebnisKnoten.getKinder().put(k2KindName, kindKnoten);
			}
		}
		
		return ergebnisKnoten;
	}
	
	/**
	 * Wertet die Zaehlvariable der Knoten des uebergebenen Baumes aus - Trefferknoten werden aufaddiert, andere abgezogen.
	 * Als zweites Ergebnis werden alle Zaehlerwerte aufaddiert, unabhaengig von deren Trefferstatus. 
	 * @param knoten
	 * @return Int-Array mit Trefferwert auf Index 0, Gesamtwert auf Index 1.
	 */
	public int[] ermittleKnotenTrefferwert(TestNode knoten){
		int[] knotenMatches = new int[]{0,0};
		
		if (knoten.isMatch()){
			// Treffer - zum Ergebnis addieren
			knotenMatches[0] += knoten.getZaehler();
		} else {
			// Kein Treffer - vom Ergebnis subtrahieren
			knotenMatches[0] -= knoten.getZaehler();
		}
		
		// Zaehlerwert zur Gesamtzahl addieren
		knotenMatches[1] += knoten.getZaehler();
		
		// Kinder durchlaufen
		Iterator<String> kinder = knoten.getKinder().keySet().iterator();
		while(kinder.hasNext()){
			String kindName = kinder.next();
			int[] kindKnotenMatches = ermittleKnotenTrefferwert(knoten.getKinder().get(kindName));
			knotenMatches[0] += kindKnotenMatches[0];
			knotenMatches[1] += kindKnotenMatches[1];
		}
		
		return knotenMatches;
	}

}
