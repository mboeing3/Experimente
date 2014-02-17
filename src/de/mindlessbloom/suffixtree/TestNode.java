package de.mindlessbloom.suffixtree;

import java.util.HashMap;

public class TestNode {
	
	private int zaehler;
	private String name;
	private HashMap<String,TestNode> kinder;

	public TestNode() {
		super();
		this.zaehler = 1;
		this.kinder = new HashMap<String,TestNode>();
		
	}

	public int getZaehler() {
		return zaehler;
	}

	public void setZaehler(int zaehler) {
		this.zaehler = zaehler;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, TestNode> getKinder() {
		return kinder;
	}

	public void setKinder(HashMap<String, TestNode> kinder) {
		this.kinder = kinder;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.zaehler+":"+this.kinder.size();
	}
}
