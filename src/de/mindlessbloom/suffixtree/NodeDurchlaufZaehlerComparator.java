package de.mindlessbloom.suffixtree;

import java.util.Comparator;

public class NodeDurchlaufZaehlerComparator implements Comparator<TestNode> {

	@Override
	public int compare(TestNode o1, TestNode o2) {
		return o1.getZaehler() - o2.getZaehler();
	}

}
