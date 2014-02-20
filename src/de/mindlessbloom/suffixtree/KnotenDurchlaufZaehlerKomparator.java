package de.mindlessbloom.suffixtree;

import java.util.Comparator;

public class KnotenDurchlaufZaehlerKomparator implements Comparator<Knoten> {

	@Override
	public int compare(Knoten o1, Knoten o2) {
		return o1.getZaehler() - o2.getZaehler();
	}

}
