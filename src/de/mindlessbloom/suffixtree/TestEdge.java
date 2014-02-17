package de.mindlessbloom.suffixtree;

public class TestEdge {
	
	private String wort;

	public TestEdge(String wort) {
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
