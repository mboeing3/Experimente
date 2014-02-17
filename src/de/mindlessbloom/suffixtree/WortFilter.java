package de.mindlessbloom.suffixtree;

import java.util.ArrayList;
import java.util.List;

public class WortFilter {
	
	private ArrayList<String> filterWoerter;

	public WortFilter() {
		super();
		this.filterWoerter = new ArrayList<String>();
	}

	public List<String> getFilterWoerter() {
		return filterWoerter;
	}

	public boolean addWort(String wort) {
		return this.filterWoerter.add(wort);
	}
	
	public boolean hatWort(String[] satz){
		for (int i=0; i<satz.length; i++){
			if (this.filterWoerter.contains(satz[i])) return true;
		}
		return false;
	}

}
