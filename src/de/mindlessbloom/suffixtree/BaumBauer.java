package de.mindlessbloom.suffixtree;

import java.util.Arrays;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class BaumBauer {
	
    /**
     * Erzeugt einen Suffixbaum im uebergebenen Graphen ab dem uebergebenen
     * Knoten anhand der uebergebenen Token. Inkrementiert die Zaehlvariable
     * eines jeden Knotens um eins fuer jede "Beruehrung".
     * @param token String-Array mit Token (Woerter, Buchstaben, Symbole, ... egal was)
     * @param rootnode Startknoten Wurzelknoten des zu konstruierenden Baumes
     * @param graph Graph Darf null sein
     * @param umgekehrt Zeigt an, ob der Baum umgekehrt erstellt werden soll (quasi als "Praefixbaum")
     * @return Die Anzahl der neu erstellten Knoten
     */
    public int baueBaum(String[] token, Knoten rootnode, Graph<Knoten, Kante> graph, boolean umgekehrt) {

    	// Variable zum Mitzaehlen der erstellten Knoten
		int knotenerstellt = 0;

		if (token == null || token.length == 0) {
			return knotenerstellt;
		}

		int vergleichsTokenIndex = 0;
		if (umgekehrt) {
			vergleichsTokenIndex = token.length - 1;
		}

		// Variable fuer Kindknoten definieren
		Knoten kindKnoten;

		// Ggf. neuen Knoten erstellen
		if (!rootnode.getKinder().containsKey(token[vergleichsTokenIndex])) {
			// passender Knoten NICHT vorhanden - neuen erstellen
			kindKnoten = new Knoten();
			
			// Zaehler fuer erstellte Knoten inkrementieren
			knotenerstellt++;
			
			// Den Namen der Kante in der Node speichern .. um spaeter bei Bedarf die Knoten geordnet ausgeben zu koennen (debug)
			kindKnoten.setName(token[vergleichsTokenIndex].intern());
			
			// Kind dem Elternknoten anfuegen
			rootnode.getKinder().put(token[vergleichsTokenIndex], kindKnoten);
			
			// Ggf. Graphen aktualisieren
			if (graph != null) {
				// Kante
				Kante neueKante = new Kante(token[vergleichsTokenIndex].intern());
				graph.addEdge(neueKante, rootnode, kindKnoten,
						EdgeType.DIRECTED);
			}

		} else {
			// passender Knoten vorhanden
			kindKnoten = rootnode.getKinder().get(token[0]);
			
			// "Beruehrung" des existierenden Knotens mitzaehlen
			kindKnoten.setZaehler(kindKnoten.getZaehler() + 1);
		}

		// Pruefen, ob der Baum "umgekehrt" erstellt werden soll
		if (umgekehrt) {
			// Rekursiver Aufruf mit Token 0 bis n-1
			knotenerstellt += this.baueBaum(
					Arrays.copyOfRange(token, 0, token.length - 1),
					kindKnoten, graph, umgekehrt);
		} else {
			// Rekursiver Aufruf mit Token 1 bis n
			knotenerstellt += this.baueBaum(
					Arrays.copyOfRange(token, 1, token.length),
					kindKnoten, graph, umgekehrt);
		}

		// Anzahl der neu erstellten Knoten zurueckgeben
		return knotenerstellt;

    }

}
