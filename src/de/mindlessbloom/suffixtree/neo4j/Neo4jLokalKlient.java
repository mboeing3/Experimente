package de.mindlessbloom.suffixtree.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Neo4jLokalKlient {
	
	private int transaktionsschwelle = 1000;
	private String datenbankpfad;
	private static GraphDatabaseService graphDb = null;
	private List<NameHaeufigkeitsTupel> knotenWarteschlange = new ArrayList<NameHaeufigkeitsTupel>();
	private List<KnotenVerknuepfungsTripel> kantenWarteschlange = new ArrayList<KnotenVerknuepfungsTripel>();
	private RelationshipType kantenTyp = new Uebereinstimmungsquotientenverbindungstyp();

	public Neo4jLokalKlient(String datenbankpfad) {
		super();
		this.datenbankpfad = datenbankpfad;
		if (Neo4jLokalKlient.graphDb == null)
		Neo4jLokalKlient.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( datenbankpfad );
	}

	public Neo4jLokalKlient(int transaktionsschwelle, String datenbankpfad) {
		super();
		this.transaktionsschwelle = transaktionsschwelle;
		this.datenbankpfad = datenbankpfad;
		if (Neo4jLokalKlient.graphDb == null)
		Neo4jLokalKlient.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( datenbankpfad );
	}

	public int getTransaktionsschwelle() {
		return transaktionsschwelle;
	}

	public void setTransaktionsschwelle(int transaktionsschwelle) {
		this.transaktionsschwelle = transaktionsschwelle;
	}

	public String getDatenbankpfad() {
		return datenbankpfad;
	}
	
	/**
	 * Fuegt einen Knotennamen der Transaktionswarteschlange hinzu. Ist die Warteschlange voll,
	 * werden die enthaltenen Knoten erstellt und als Liste zurueckgegeben, ansonsten null. 
	 * @param knotenName Name des zu erstellenden Knotens
	 * @return Liste der erstellten Knoten oder Null
	 */
	public Map<String,Node> fuegeKnotenErstellungZurWarteschlangeHinzu(String knotenName, long haeufigkeit){
		knotenWarteschlange.add(new NameHaeufigkeitsTupel(knotenName,haeufigkeit));
		Map<String,Node> erstellteKnoten;
		if (this.transaktionsschwelle >0 && knotenWarteschlange.size() >= this.transaktionsschwelle){
			erstellteKnoten = this.starteTransaktionKnoten();
		} else {
			erstellteKnoten = null;
		}
		return erstellteKnoten;
	}
	
	/**
	 * Fuehrt die Transaktion der in der Warteschlange enthaltenen Knotennamen durch.
	 * @return Liste der erstellten Knoten
	 */
	public Map<String,Node> starteTransaktionKnoten(){
		Map<String,Node> uebertrageneElemente = new HashMap<String,Node>();
		Transaction tx = graphDb.beginTx();
		 try
		 {
			// Warteschlange durchlaufen
			 Iterator<NameHaeufigkeitsTupel> knotenTupel = this.knotenWarteschlange.iterator();
			 while(knotenTupel.hasNext()){
				 NameHaeufigkeitsTupel tupel = knotenTupel.next();
				 Node n = graphDb.createNode();
			     n.setProperty("Label", tupel.getName());
			     n.setProperty("Haeufigkeit", tupel.getVorkommensHaufigkeit());
			     uebertrageneElemente.put(tupel.getName(),n);
			 }
		 
		     tx.success();
		     this.knotenWarteschlange.clear();
		 }
		 finally
		 {
		     tx.close();
		 }
		 return uebertrageneElemente;
	}
	
	/**
	 * Fuehrt die Transaktion der in der Warteschlange enthaltenen Kanten durch.
	 * @return Liste der erstellten Kanten
	 */
	public List<Relationship> starteTransaktionKanten(){
		List<Relationship> uebertrageneElemente = new ArrayList<Relationship>();
		Transaction tx = graphDb.beginTx();
		 try
		 {
			// Warteschlange durchlaufen
			 Iterator<KnotenVerknuepfungsTripel> kanten = this.kantenWarteschlange.iterator();
			 while(kanten.hasNext()){
				 
				 KnotenVerknuepfungsTripel tripel = kanten.next();
				 Relationship kante = tripel.getKnotenQuelle().createRelationshipTo(tripel.getKnotenZiel(), this.kantenTyp);
				 kante.setProperty("Weight", tripel.getGewicht());
			     uebertrageneElemente.add(kante);
			 }
			 
		     tx.success();
		     this.kantenWarteschlange.clear();
		 }
		 finally
		 {
		     tx.close();
		 }
		 return uebertrageneElemente;
	}
	
	/**
	 * Fuegt eine Kante der Transaktionswarteschlange hinzu. Ist die Warteschlange voll,
	 * werden die enthaltenen Kanten erstellt und als Liste zurueckgegeben, ansonsten null.
	 * @param knotenQuelle Ursprung der Kante
	 * @param knotenZiel Ziel der Kante
	 * @param gewicht Gewicht der Kante
	 * @return Liste der erstellten Kanten oder Null
	 */
	public List<Relationship> fuegeKantenErstellungZurWarteschlangeHinzu(Node knotenQuelle, Node knotenZiel, Double gewicht){
		kantenWarteschlange.add(new KnotenVerknuepfungsTripel(knotenQuelle,knotenZiel,gewicht));
		List<Relationship> erstellteKanten;
		if (this.transaktionsschwelle >0 && kantenWarteschlange.size() >= this.transaktionsschwelle){
			erstellteKanten = this.starteTransaktionKanten();
		} else {
			erstellteKanten = null;
		}
		return erstellteKanten;
	}

	public static void main(String[] args) {
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( "/home/marcel/opt/neo4j-community-2.0.1/data/graph.db" );
		
		Transaction tx = graphDb.beginTx();
		 try
		 {
		     Node n = graphDb.createNode();
		     n.setProperty("name", "bladibladiblah");
		     
		     Node m = graphDb.createNode();
		     m.setProperty("name", "blodiblodibloh");
		     
		     RelationshipType rt = new Uebereinstimmungsquotientenverbindungstyp();
		     
		     Relationship r = n.createRelationshipTo(m, rt);
		     
		     r.setProperty("uebereinstimmung", new Double(0.5d));
		 
		     tx.success();
		 }
		 finally
		 {
		     tx.close();
		 }
		graphDb.shutdown();
	}
	
	public void beenden(){
		Neo4jLokalKlient.graphDb.shutdown();
	}

}
