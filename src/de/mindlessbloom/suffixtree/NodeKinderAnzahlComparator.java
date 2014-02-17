package de.mindlessbloom.suffixtree;

import java.util.Comparator;

public class NodeKinderAnzahlComparator implements Comparator<TestNode> {

	@Override
	public int compare(TestNode o1, TestNode o2) {
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
