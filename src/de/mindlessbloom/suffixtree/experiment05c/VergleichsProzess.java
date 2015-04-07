package de.mindlessbloom.suffixtree.experiment05c;

import java.util.concurrent.ConcurrentHashMap;

import de.mindlessbloom.suffixtree.experiment01_04.Knoten;
import de.mindlessbloom.suffixtree.experiment01_04.KnotenKomparator;

public class VergleichsProzess implements Runnable {

	private Double schwellwert;
	private Knoten knoten;
	private Knoten vergleichsKnoten;
	private Knoten knoten2;
	private Knoten vergleichsKnoten2;
	private ConcurrentHashMap<String,Double> verknuepfungen;
	private Fortschritt fortschritt;


	public VergleichsProzess(Double schwellwert, Knoten knoten,
			Knoten vergleichsKnoten,
			ConcurrentHashMap<String, Double> verknuepfungen,
			Fortschritt fortschritt) {
		this(schwellwert, knoten, vergleichsKnoten, null, null, verknuepfungen, fortschritt);
	}
	
	public VergleichsProzess(Double schwellwert, Knoten knoten,
			Knoten vergleichsKnoten, Knoten knoten2,
			Knoten vergleichsKnoten2,
			ConcurrentHashMap<String, Double> verknuepfungen,
			Fortschritt fortschritt) {
		super();
		this.schwellwert = schwellwert;
		this.knoten = knoten;
		this.vergleichsKnoten = vergleichsKnoten;
		this.knoten2 = knoten2;
		this.vergleichsKnoten2 = vergleichsKnoten2;
		this.verknuepfungen = verknuepfungen;
		this.fortschritt = fortschritt;
	}

	@Override
	public void run() {
		if (this.vergleichsKnoten2 != null && this.knoten2 != null){
			vergleicheMulti(schwellwert, knoten, vergleichsKnoten, knoten2, vergleichsKnoten2, verknuepfungen);
		} else {
			vergleiche(schwellwert, knoten, vergleichsKnoten, verknuepfungen);
		}
	}
	
	private void vergleiche(Double schwellwert, Knoten knoten, Knoten vergleichsKnoten, ConcurrentHashMap<String,Double> verknuepfungen){
		// Komparator instanziieren
		KnotenKomparator komparator = new KnotenKomparator();
		
		// Vergleich anstellen
		Double uebereinstimmungsQuotient = komparator.vergleiche(knoten, vergleichsKnoten);

		// Ggf. Kante zwischen beiden Knoten erstellen
		if (uebereinstimmungsQuotient > schwellwert) {
			verknuepfungen.put(vergleichsKnoten.getName(), uebereinstimmungsQuotient);
		}
		
		// Fortschritt nachhalten
		fortschritt.setVerarbeitet(fortschritt.getVerarbeitet()+1);
	}
	
	private void vergleicheMulti(Double schwellwert, Knoten knoten1, Knoten vergleichsKnoten1, Knoten knoten2, Knoten vergleichsKnoten2, ConcurrentHashMap<String,Double> verknuepfungen){
		// Komparator instanziieren
		KnotenKomparator komparator = new KnotenKomparator();
		
		// Vergleiche anstellen
		Double uebereinstimmungsQuotient = (komparator.vergleiche(knoten1, vergleichsKnoten1) + komparator.vergleiche(knoten2, vergleichsKnoten2)) / 2d;

		// Ggf. Kante zwischen beiden Knoten erstellen
		if (uebereinstimmungsQuotient > schwellwert) {
			verknuepfungen.put(vergleichsKnoten1.getName(), uebereinstimmungsQuotient);
		}
		
		// Fortschritt nachhalten
		fortschritt.setVerarbeitet(fortschritt.getVerarbeitet()+1);
	}

}
