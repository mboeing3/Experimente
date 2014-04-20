package de.mindlessbloom.suffixtree.experiment05;

import java.net.URI;
import java.net.URISyntaxException;

import de.mindlessbloom.suffixtree.experiment01_03.Knoten;
import de.mindlessbloom.suffixtree.experiment01_03.KnotenKomparator;
import de.mindlessbloom.suffixtree.neo4j.Neo4jKlient;

public class VergleichsProzess implements Runnable {
	
	private Neo4jKlient graph;
	private Double schwellwert;
	private Knoten knoten;
	private Knoten vergleichsKnoten;
	private URI knotenUri;
	private URI vergleichsKnotenUri;
	private Fortschritt fortschritt;

	
	
	public VergleichsProzess(Neo4jKlient graph, Double schwellwert,
			Knoten knoten, Knoten vergleichsKnoten, URI knotenUri,
			URI vergleichsKnotenUri, Fortschritt fortschritt) {
		super();
		this.graph = graph;
		this.schwellwert = schwellwert;
		this.knoten = knoten;
		this.vergleichsKnoten = vergleichsKnoten;
		this.knotenUri = knotenUri;
		this.vergleichsKnotenUri = vergleichsKnotenUri;
		this.fortschritt = fortschritt;
	}

	@Override
	public void run() {
		vergleiche(graph, schwellwert, knoten, vergleichsKnoten, knotenUri, vergleichsKnotenUri, fortschritt);
	}
	
	private void vergleiche(Neo4jKlient graph, Double schwellwert, Knoten knoten, Knoten vergleichsKnoten, URI knotenUri, URI vergleichsKnotenUri, Fortschritt fortschritt){
		// Komparator instanziieren
		KnotenKomparator komparator = new KnotenKomparator();
		
		// Vergleich anstellen
		Double uebereinstimmungsQuotient = komparator.vergleiche(knoten, vergleichsKnoten);

		// Ggf. Kante zwischen beiden Knoten erstellen
		if (uebereinstimmungsQuotient > schwellwert) {
			URI verknuepfungsUri = null;
			try {
				verknuepfungsUri = graph.addRelationship(knotenUri, vergleichsKnotenUri, "aehnelt", "{ }");
				graph.addMetadataToProperty(verknuepfungsUri, "gewicht", uebereinstimmungsQuotient.toString());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		
		// Fortschritt mitzaehlen (fuer Anzeige)
		fortschritt.setVerarbeitet(fortschritt.getVerarbeitet()+1);
	}

}
