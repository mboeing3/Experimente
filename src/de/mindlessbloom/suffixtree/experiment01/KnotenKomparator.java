package de.mindlessbloom.suffixtree.experiment01;

import java.util.Iterator;

import de.mindlessbloom.suffixtree.Knoten;

/**
 * Vergleicht zwei Suffix(teil)baeume miteinander.
 * 
 * @author marcel
 * 
 */
public class KnotenKomparator {

	// public static final String STELLVERTRETER = "######";
	
	private int maximaleAuswertungsEbene = -1;
	private double ebenenexponent = 0d;
	private boolean ebenenFaktorNurAufTrefferAnwenden = false;
	
	public KnotenKomparator() {
		super();
	}

	public KnotenKomparator(int maximaleAuswertungsEbene,
			double ebenenexponentpositiv, boolean ebenenFaktorNurAufTrefferAnwenden) {
		super();
		this.maximaleAuswertungsEbene = maximaleAuswertungsEbene;
		this.ebenenexponent = ebenenexponentpositiv;
		this.ebenenFaktorNurAufTrefferAnwenden = ebenenFaktorNurAufTrefferAnwenden;
	}

	public int getMaximaleAuswertungsEbene() {
		return maximaleAuswertungsEbene;
	}

	public void setMaximaleAuswertungsEbene(int maximaleAuswertungsEbene) {
		this.maximaleAuswertungsEbene = maximaleAuswertungsEbene;
	}

	public double getEbenenexponent() {
		return ebenenexponent;
	}

	public void setEbenenexponent(double ebenenexponent) {
		this.ebenenexponent = ebenenexponent;
	}

	/**
	 * Vergleicht die Baeume miteinander, deren Wurzelknoten uebergeben wurden.
	 * @param k1 Wurzelknoten 1
	 * @param k2 Wurzelknoten 2
	 * @return Anteil des Trefferwerts am Gesamtwert
	 */
	public Double vergleiche(Knoten k1, Knoten k2) {

		Knoten verschmolzenerBaum = verschmelzeBaeume(k1, k2);
		Double[] trefferWert = this.ermittleKnotenTrefferwert(verschmolzenerBaum, this.maximaleAuswertungsEbene, this.ebenenexponent);
		return new Double(trefferWert[0] / trefferWert[1]);

	}

	/**
	 * Verschmilzt die Baeume ab der ersten Kindebene des jeweils uebergebenen
	 * Knoten und gibt den Wurzelknoten des (neuen) Ergebnisbaumes zurueck.
	 * Uebereinstimmungen sind im Ergebnisbaum via isMatch() markiert; Zaehler
	 * der Knoten wurden im Ergebnisbaum aufaddiert.
	 * 
	 * @param knoten1
	 * @param knoten2
	 * @return Wurzelknoten des neuen Baumes
	 */
	public Knoten verschmelzeBaeume(Knoten knoten1, Knoten knoten2) {

		// Es wird angenommen, dass knotenTropfen == knotenMeer ist - nur die
		// Kinder werden ueberprueft.

		// Neuen Knoten erzeugen
		Knoten ergebnisKnoten = new Knoten();

		// Ggf. Werte der uebergebenen Knoten aufaddieren und Kinder hinzufuegen
		if (knoten1 != null) {
			ergebnisKnoten.setZaehler(ergebnisKnoten.getZaehler()
					+ knoten1.getZaehler());
			ergebnisKnoten.setName(knoten1.getName());

			// Schleife ueber Kinder des ersten Knotens
			Iterator<String> k1Kinder = knoten1.getKinder().keySet().iterator();
			while (k1Kinder.hasNext()) {

				// Variable fuer neuen Kindknoten definieren
				Knoten kindKnoten;

				// Name des Kindes von Knoten1 ermitteln
				String k1KindName = k1Kinder.next();

				// Pruefen, ob Knoten2 existiert und ebenfalls ein solches Kind
				// hat
				if (knoten2 != null
						&& knoten2.getKinder().containsKey(k1KindName)) {
					// Kind mit diesem Namen gefunden, steige hinab
					kindKnoten = this.verschmelzeBaeume(knoten1.getKinder()
							.get(k1KindName),
							knoten2.getKinder().get(k1KindName));

					// Markiere Knoten als Treffer
					kindKnoten.setMatch(true);

				} else {
					// Kein Kind mit diesem Namen gefunden oder Knoten2 ist
					// Null, steige hinab
					kindKnoten = this.verschmelzeBaeume(knoten1.getKinder()
							.get(k1KindName), null);

				}

				// Neuen Kindknoten an Ergebnis anfuegen
				ergebnisKnoten.getKinder().put(k1KindName, kindKnoten);

			}
		}
		if (knoten2 != null) {
			ergebnisKnoten.setZaehler(ergebnisKnoten.getZaehler()
					+ knoten2.getZaehler());

			// Namen ggf. anhaengen
			if (knoten1 != null
					&& !(knoten1.getName().equals(knoten2.getName()))) {
				ergebnisKnoten.setName(knoten1.getName() + "/"
						+ knoten2.getName());
			} else {
				ergebnisKnoten.setName(knoten2.getName());
			}

			// Schleife ueber Kinder des ersten Knotens
			Iterator<String> k2Kinder = knoten2.getKinder().keySet().iterator();
			while (k2Kinder.hasNext()) {

				// Variable fuer neuen Kindknoten definieren
				Knoten kindKnoten;

				// Name des Kindes von Knoten1 ermitteln
				String k2KindName = k2Kinder.next();

				// Falls dieser Knoten schon im Ergebnis existiert, kann
				// abgebrochen werden
				if (ergebnisKnoten.getKinder().containsKey(k2KindName)) {
					continue;
				}

				// Pruefen, ob Knoten2 existiert und ebenfalls ein solches Kind
				// hat
				if (knoten1 != null
						&& knoten1.getKinder().containsKey(k2KindName)) {
					// Kind mit diesem Namen gefunden, steige hinab
					kindKnoten = this.verschmelzeBaeume(knoten1.getKinder()
							.get(k2KindName),
							knoten2.getKinder().get(k2KindName));

					// Markiere Knoten als Treffer
					kindKnoten.setMatch(true);

				} else {
					// Kein Kind mit diesem Namen gefunden oder Knoten2 ist
					// Null, steige hinab
					kindKnoten = this.verschmelzeBaeume(null, knoten2
							.getKinder().get(k2KindName));

				}

				// Neuen Kindknoten an Ergebnis anfuegen
				ergebnisKnoten.getKinder().put(k2KindName, kindKnoten);
			}
		}

		return ergebnisKnoten;
	}

	/**
	 * Wertet die Zaehlvariable der Knoten des uebergebenen Baumes aus.
	 * 
	 * @see #ermittleKnotenTrefferwert(Knoten knoten, int ebene, int maxebene, double ebenenexponent)
	 * @param knoten Der Knoten des Baumes, ab dem ausgewertet werden soll.
	 * @return double-Array mit Trefferwert auf Index 0, Gesamtwert auf Index 1.
	 */
	public Double[] ermittleKnotenTrefferwert(Knoten knoten) {
		return this.ermittleKnotenTrefferwert(knoten, 0, this.maximaleAuswertungsEbene,
				this.ebenenexponent);
	}

	/**
	 * Wertet die Zaehlvariable der Knoten des uebergebenen Baumes aus.
	 * 
	 * @see #ermittleKnotenTrefferwert(Knoten knoten, int ebene, int maxebene, double ebenenexponent)
	 * @param knoten Der Knoten des Baumes, ab dem ausgewertet werden soll.
	 * @param maxebene Tiefste Ebene der Baumhierarchie, die noch ausgewertet werden soll.
	 * @param ebenenexponent Exponent der Ebenennummer, welche Faktor fuer die Wertung von Knoten ist.
	 * @return double-Array mit Trefferwert auf Index 0, Gesamtwert auf Index 1.
	 */
	public Double[] ermittleKnotenTrefferwert(Knoten knoten, int maxebene,
			double ebenenexponentpositiv) {
		return this.ermittleKnotenTrefferwert(knoten, 0, maxebene,
				ebenenexponentpositiv);
	}

	/**
	 * Wertet die Zaehlvariable der Knoten des uebergebenen Baumes aus -
	 * Trefferknoten werden aufaddiert, andere abgezogen. Als zweites Ergebnis
	 * werden alle Zaehlerwerte aufaddiert, unabhaengig von deren Trefferstatus.
	 * Der erste Knoten wird in der Wertung ignoriert. Der Wert der
	 * Zaehlervariable kann jeweils abhaengig von der Ebene modifiziert werden,
	 * indem man Exponenten uebergibt. Multipliziert wird der Wert dann mit
	 * Ebenennummer^Exponent, d.h. "0" bedeutet keine Aenderung.
	 * 
	 * @param knoten Der Knoten des Baumes, ab dem ausgewertet werden soll.
	 * @param ebene Die Nummer der Ebene, auf der sich der Auswertungsprozess in der Baumhierarchie befindet.
	 * @param maxebene Tiefste Ebene der Baumhierarchie, die noch ausgewertet werden soll. <0 zum Ignorieren.
	 * @param ebenenexponent Exponent der Ebenennummer, welche Faktor fuer die Wertung von Knoten ist.
	 * @return double-Array mit Trefferwert auf Index 0, Gesamtwert auf Index 1.
	 */
	private Double[] ermittleKnotenTrefferwert(Knoten knoten, int ebene,
			int maxebene, double ebenenexponent) {
		Double[] knotenMatches = new Double[] { 0d, 0d };

		if (ebene > 0){
			if (knoten.isMatch()) {
				// Treffer - zum Ergebnis addieren
				knotenMatches[0] += knoten.getZaehler()
						* Math.pow(ebene, ebenenexponent);
			}
			// Zaehlerwert zur Gesamtzahl addieren
			if (ebenenFaktorNurAufTrefferAnwenden){
				knotenMatches[1] += knoten.getZaehler();
			} else {
				knotenMatches[1] += knoten.getZaehler()* Math.pow(ebene, ebenenexponent);
			}
		}

		if (ebene < maxebene || maxebene <0) {
			// Kinder durchlaufen
			Iterator<String> kinder = knoten.getKinder().keySet().iterator();
			while (kinder.hasNext()) {
				String kindName = kinder.next();
				Double[] kindKnotenMatches = ermittleKnotenTrefferwert(knoten
						.getKinder().get(kindName), ebene + 1, maxebene,
						ebenenexponent);
				knotenMatches[0] += kindKnotenMatches[0];
				knotenMatches[1] += kindKnotenMatches[1];
			}
		}

		return knotenMatches;
	}

}
