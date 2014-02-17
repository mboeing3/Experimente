package de.mindlessbloom.suffixtree;

import java.util.Arrays;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class BaumBauer {
	
    /**
     * Erzeugt einen Suffixbaum im uebergebenen Graphen ab dem uebergebenen
     * Knoten anhand der uebergebenen Token.
     * @param token Tokenarray
     * @param rootnode Startknoten
     * @param graph Graph
     * @param umgekehrt Zeigt an, ob der Baum umgekehrt erstellt werden soll (quasi als "Praefixbaum")
     * @return Die Anzahl der neu erstellten Knoten
     */
    public int baumBuilder(String[] token, TestNode rootnode, Graph<TestNode, TestEdge> graph,
	    boolean umgekehrt) {

	int knotenerstellt = 0;

	if (token == null || token.length == 0) {
	    return knotenerstellt;
	}

	int vergleichsTokenIndex = 0;
	if (umgekehrt)
	    vergleichsTokenIndex = token.length - 1;

	// passenden Kindknoten suchen

	// Ggf. neuen Knoten erstellen
	if (!rootnode.getKinder().containsKey(token[vergleichsTokenIndex])) {
	    // passender Knoten NICHT vorhanden - neuen erstellen und rekursiv
	    // aufrufen
	    TestNode neuerKnoten = new TestNode();
	    knotenerstellt++;
	    // Den Namen der Kante in der Node speichern .. fuer geordnete
	    // Ausgabe spaeter
	    neuerKnoten.setName(token[vergleichsTokenIndex].intern());
	    TestEdge neueKante = new TestEdge(
		    token[vergleichsTokenIndex].intern());
	    rootnode.getKinder().put(token[vergleichsTokenIndex], neuerKnoten);
	    if (graph != null) {
		graph.addEdge(neueKante, rootnode, neuerKnoten,
			EdgeType.DIRECTED);
	    }

	    if (umgekehrt) {
		knotenerstellt += this.baumBuilder(
			Arrays.copyOfRange(token, 0, token.length - 1),
			neuerKnoten, graph, umgekehrt);
	    } else {
		knotenerstellt += this.baumBuilder(
			Arrays.copyOfRange(token, 1, token.length),
			neuerKnoten, graph, umgekehrt);
	    }

	} else {
	    // passender Knoten vorhanden - Zaehler inkrementieren und Rekursiv
	    // aufrufen
	    TestNode zielKnoten = rootnode.getKinder().get(token[0]);
	    zielKnoten.setZaehler(zielKnoten.getZaehler() + 1);

	    if (umgekehrt) {
		knotenerstellt += this.baumBuilder(
			Arrays.copyOfRange(token, 0, token.length - 1),
			zielKnoten, graph, umgekehrt);
	    } else {
		knotenerstellt += this.baumBuilder(
			Arrays.copyOfRange(token, 1, token.length), zielKnoten,
			graph, umgekehrt);
	    }
	}

	return knotenerstellt;

    }

}
