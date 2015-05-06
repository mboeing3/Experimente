package de.mindlessbloom.suffixtree.experiment07;

import java.util.Comparator;

public class KnotenBeruehrungszaehlerKomparator implements Comparator<Knoten> {

	@Override
	public int compare(Knoten o1, Knoten o2) {
		if (o1.getZaehler() > o2.getZaehler())
			return 1;
		else if (o1.getZaehler() < o2.getZaehler())
			return -1;
		else
			return 0;
	}

}
