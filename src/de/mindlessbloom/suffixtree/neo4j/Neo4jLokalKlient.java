package de.mindlessbloom.suffixtree.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Neo4jLokalKlient {
	
	private int transaktionsschwelle;
	private String datenbankpfad;

	public Neo4jLokalKlient(int transaktionsschwelle, String datenbankpfad) {
		super();
		this.transaktionsschwelle = transaktionsschwelle;
		this.datenbankpfad = datenbankpfad;
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

	public void setDatenbankpfad(String datenbankpfad) {
		this.datenbankpfad = datenbankpfad;
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

}
