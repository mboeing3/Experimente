package de.mindlessbloom.suffixtree.experiment05;

import java.util.ArrayList;
import java.util.List;

import de.mindlessbloom.suffixtree.Knoten;

public class MetaKnoten {
	
	private Knoten knoten;
	private List<MetaKnoten> kindMetaKnoten = new ArrayList<MetaKnoten>();
	private Double uebereinstimmungsQuotient;
	
	public MetaKnoten(Knoten knoten) {
		super();
		this.knoten = knoten;
	}
	public Knoten getKnoten() {
		return knoten;
	}
	public void setKnoten(Knoten knoten) {
		this.knoten = knoten;
	}
	public List<MetaKnoten> getKindMetaKnoten() {
		return kindMetaKnoten;
	}
	public void setKindMetaKnoten(List<MetaKnoten> kindMetaKnoten) {
		this.kindMetaKnoten = kindMetaKnoten;
	}
	public Double getUebereinstimmungsQuotient() {
		return uebereinstimmungsQuotient;
	}
	public void setUebereinstimmungsQuotient(Double uebereinstimmungsQuotient) {
		this.uebereinstimmungsQuotient = uebereinstimmungsQuotient;
	}

}
