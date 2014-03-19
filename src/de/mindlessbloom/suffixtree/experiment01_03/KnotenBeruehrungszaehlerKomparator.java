package de.mindlessbloom.suffixtree.experiment01_03;

import java.util.Comparator;

public class KnotenBeruehrungszaehlerKomparator implements Comparator<Knoten> {

	@Override
	public int compare(Knoten o1, Knoten o2) {
		return o1.getZaehler() - o2.getZaehler();
	}

}
