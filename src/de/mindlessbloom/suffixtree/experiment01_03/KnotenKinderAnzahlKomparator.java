package de.mindlessbloom.suffixtree.experiment01_03;

import java.util.Comparator;

public class KnotenKinderAnzahlKomparator implements Comparator<Knoten> {

	@Override
	public int compare(Knoten o1, Knoten o2) {
		int o1kinderanzahl = 0;
		int o2kinderanzahl = 0;
		if (o1.getKinder() != null){
			o1kinderanzahl = o1.getKinder().size();
		}
		if (o2.getKinder() != null){
			o2kinderanzahl = o2.getKinder().size();
		}
		return o1kinderanzahl - o2kinderanzahl;
	}

}
