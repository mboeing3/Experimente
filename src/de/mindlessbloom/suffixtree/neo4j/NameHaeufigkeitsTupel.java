package de.mindlessbloom.suffixtree.neo4j;

public class NameHaeufigkeitsTupel {
	
	private String name;
	private long vorkommensHaufigkeit;
	
	public NameHaeufigkeitsTupel(String name, long vorkommensHaufigkeit) {
		super();
		this.name = name;
		this.vorkommensHaufigkeit = vorkommensHaufigkeit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getVorkommensHaufigkeit() {
		return vorkommensHaufigkeit;
	}

	public void setVorkommensHaufigkeit(long vorkommensHaufigkeit) {
		this.vorkommensHaufigkeit = vorkommensHaufigkeit;
	}

}
