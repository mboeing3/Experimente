package de.mindlessbloom.suffixtree;

public class Zaehler {

	private String name;
	private int wert;
	
	public Zaehler(String name){
		this.name = name;
		this.wert = 1;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getWert() {
		return wert;
	}
	public void setWert(int wert) {
		this.wert = wert;
	}
	
	public void incWert(){
		this.wert++;
	}

	@Override
	public String toString() {
		return this.name+" : "+this.wert;
	}
	
	
}
