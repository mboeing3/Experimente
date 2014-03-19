package de.mindlessbloom.suffixtree.experiment01_03;

public class Kante {
	
	private String wort;

	public Kante(String wort) {
		super();
		this.wort = wort;
	}

	public String getWort() {
		return wort;
	}

	public void setWort(String wort) {
		this.wort = wort;
	}

	@Override
	public String toString() {
		return this.wort;
	}
	

}
