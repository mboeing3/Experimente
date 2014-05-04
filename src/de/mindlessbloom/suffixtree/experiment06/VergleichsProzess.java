package de.mindlessbloom.suffixtree.experiment06;

import java.util.concurrent.ConcurrentHashMap;

import de.mindlessbloom.suffixtree.experiment01_03.Knoten;
import de.mindlessbloom.suffixtree.experiment01_03.KnotenKomparator;

public class VergleichsProzess implements Runnable {

	private Double schwellwert;
	private Knoten knoten;
	private Knoten vergleichsKnoten;
	private ConcurrentHashMap<String,Double> verknuepfungen;
	private Fortschritt fortschritt;


	public VergleichsProzess(Double schwellwert, Knoten knoten,
			Knoten vergleichsKnoten,
			ConcurrentHashMap<String, Double> verknuepfungen,
			Fortschritt fortschritt) {
		super();
		this.schwellwert = schwellwert;
		this.knoten = knoten;
		this.vergleichsKnoten = vergleichsKnoten;
		this.verknuepfungen = verknuepfungen;
		this.fortschritt = fortschritt;
	}

	@Override
	public void run() {
		vergleiche(schwellwert, knoten, vergleichsKnoten, verknuepfungen);
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

}
