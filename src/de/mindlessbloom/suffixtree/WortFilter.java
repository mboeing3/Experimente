package de.mindlessbloom.suffixtree;

import java.util.ArrayList;
import java.util.Iterator;
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
	
	/**
	 * Gibt true zurueck, wenn der uebergebene Satz auf den Filter passt.
	 * @param satz
	 * @return
	 */
	public boolean hatWort(String[] satz){
		for (int i=0; i<satz.length; i++){
			if (this.filterWoerter.contains(satz[i])) return true;
		}
		return false;
	}
	
	/**
	 * Gibt true zurueck, wenn der uebergebene Satz auf den Filter passt.
	 * @param satz
	 * @return
	 */
	public boolean hatWort(List<String> satz){
		Iterator<String> worte = satz.iterator();
		while(worte.hasNext()){
			if (this.filterWoerter.contains(worte.next())) return true;
		}
		return false;
	}

}
