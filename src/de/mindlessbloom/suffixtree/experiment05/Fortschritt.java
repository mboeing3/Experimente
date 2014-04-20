package de.mindlessbloom.suffixtree.experiment05;

class Fortschritt {
	private long verbleibend;
	private long verarbeitet;
	public Fortschritt(long verbleibend) {
		this(verbleibend, 0l);
	}
	public Fortschritt(long verbleibend, long verarbeitet) {
		super();
		this.verbleibend = verbleibend;
		this.verarbeitet = verarbeitet;
	}
	public synchronized long getVerbleibend(){
		return verbleibend;
	}
	public synchronized long getVerarbeitet(){
		return verarbeitet;
	}
	public synchronized void setVerbleibend(long verbleibend){
		this.verbleibend = verbleibend;
	}
	public synchronized void setVerarbeitet(long verarbeitet){
		this.verarbeitet = verarbeitet;
	}
}
