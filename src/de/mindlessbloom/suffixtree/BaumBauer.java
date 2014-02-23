package de.mindlessbloom.suffixtree;

import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;

import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Stellt Methoden zum Konstruieren und Modifizieren von Suffixbaeumen zur Verfuegung.
 * @author marcel
 *
 */
public class BaumBauer {
	
	/**
     * Erzeugt einen Suffixbaum ab dem uebergebenen Knoten anhand der uebergebenen Token.
     * Inkrementiert die Zaehlvariable eines jeden Knotens um eins fuer jede "Beruehrung".
     * @param token String-Array mit Token (Woerter, Buchstaben, Symbole, ... egal was)
     * @param rootnode Startknoten Wurzelknoten des zu konstruierenden Baumes
     * @param umgekehrt Zeigt an, ob der Baum umgekehrt erstellt werden soll (quasi als "Praefixbaum")
     * @return Die Anzahl der neu erstellten Knoten
     */
	public int baueBaum(String[] token, Knoten rootnode, boolean umgekehrt) {
		return this.baueBaum(token, rootnode, null, umgekehrt);
	}
	
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
		
		// Zaehler fuer eingefuegte Knoten
		int knotenEingefuegt = 0;
		
		// Tokenarray durchlaufen
		for (int i=0; i<token.length; i++){
			
			// Bereich fuer naechsten Suffix-Trie ermitteln
			int von = i;
			int bis = token.length;
			if (umgekehrt){
				von = 0;
				bis = token.length -i;
			}
			
			// Naechsten Trie in Baum einfuegen
			knotenEingefuegt += this.baueTrie(Arrays.copyOfRange(token, von, bis), rootnode, graph, umgekehrt);
			
		}
		
		// Anzahl der eingefuegten Knoten zurueckgeben
		return knotenEingefuegt;
		
	}
	
	/**
     * Erzeugt einen Suffixtrie ab dem uebergebenen Knoten anhand der uebergebenen Token.
     * Inkrementiert die Zaehlvariable eines jeden Knotens um eins fuer jede "Beruehrung".
     * @param token String-Array mit Token (Woerter, Buchstaben, Symbole, ... egal was)
     * @param rootnode Startknoten Wurzelknoten des zu konstruierenden Baumes
     * @param umgekehrt Zeigt an, ob der Baum umgekehrt erstellt werden soll (quasi als "Praefixbaum")
     * @return Die Anzahl der neu erstellten Knoten
     */
	public int baueTrie(String[] token, Knoten rootnode, boolean umgekehrt) {
		return this.baueTrie(token, rootnode, null, umgekehrt);
	}
	
    /**
     * Erzeugt einen Suffixtrie im uebergebenen Graphen ab dem uebergebenen
     * Knoten anhand der uebergebenen Token. Inkrementiert die Zaehlvariable
     * eines jeden Knotens um eins fuer jede "Beruehrung".
     * @param token String-Array mit Token (Woerter, Buchstaben, Symbole, ... egal was)
     * @param rootnode Startknoten Wurzelknoten des zu konstruierenden Baumes
     * @param graph Graph Darf null sein
     * @param umgekehrt Zeigt an, ob der Baum umgekehrt erstellt werden soll (quasi als "Praefixbaum")
     * @return Die Anzahl der neu erstellten Knoten
     */
    public int baueTrie(String[] token, Knoten rootnode, Graph<Knoten, Kante> graph, boolean umgekehrt) {

    	// Variable zum Mitzaehlen der erstellten Knoten
		int knotenerstellt = 0;

		if (token == null || token.length == 0) {
			return knotenerstellt;
		}

		// Index des als naechstes zu vergleichenden Tokens ermitteln
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
			knotenerstellt += this.baueTrie(
					Arrays.copyOfRange(token, 0, token.length - 1),
					kindKnoten, graph, umgekehrt);
		} else {
			// Rekursiver Aufruf mit Token 1 bis n
			knotenerstellt += this.baueTrie(
					Arrays.copyOfRange(token, 1, token.length),
					kindKnoten, graph, umgekehrt);
		}

		// Anzahl der neu erstellten Knoten zurueckgeben
		return knotenerstellt;

    }

	/**
	 * Gibt eine Kopie des uebergebenen Baumes zurueck, aber ohne die Knoten, die nicht als Treffer
	 * markiert waren.
	 * @param knoten Wurzelknoten des zu kopierenden Baumes
	 * @param ignoriereErstenKnoten Gibt vor, ob der Wurzelknoten ignoriert werden soll (nuetzlich fuer Kontextvergleichsbaeume)
	 * @return Wurzel des neu erstellten Baumes
	 */
	public Knoten entferneNichtTrefferKnoten(Knoten knoten, boolean ignoriereErstenKnoten) {

		// Bearbeitung ggf. Abbrechen
		if (knoten == null || !(knoten.isMatch() || ignoriereErstenKnoten)) {
			return null;
		}

		// Neuen Knoten erstellen und Werte uebertragen
		Knoten neuerKnoten = new Knoten();
		neuerKnoten.setName(knoten.getName());
		neuerKnoten.setZaehler(knoten.getZaehler());
		neuerKnoten.setMatch(true);

		// Aufruf fuer Kindknoten rekursiv wiederholen
		Iterator<String> kinder = knoten.getKinder().keySet().iterator();
		while (kinder.hasNext()) {
			String kindName = kinder.next();
			Knoten kind = entferneNichtTrefferKnoten(knoten.getKinder().get(
					kindName),false);
			if (kind != null)
				neuerKnoten.getKinder().put(kindName, kind);
		}

		// Neuen Knoten zurueckgeben
		return neuerKnoten;

	}

	/**
	 * Fuegt alle Elemente und Unterelemente des uebergebenen Baumes dem uebergebenen TreeSet hinzu. 
	 * @param wurzel
	 * @param treeSet
	 */
	public void fuegeNodesInTreeSetEin(Knoten wurzel, TreeSet<Knoten> treeSet) {
		Iterator<String> kinder = wurzel.getKinder().keySet().iterator();
		while (kinder.hasNext()) {
			fuegeNodesInTreeSetEin(wurzel.getKinder().get(kinder.next()),
					treeSet);
		}
		treeSet.add(wurzel);
	}
	
	/**
	 * Gibt einen Graphen mit dem uebergebenen Baum zurueck
	 * 
	 * @param knoten
	 * @return
	 */
	public DelegateTree<Knoten, Kante> konstruiereGraph(Knoten knoten) {

		return this.konstruiereGraph(knoten, null);
		
	}

	/**
	 * Gibt einen Graphen mit dem uebergebenen Baum zurueck
	 * 
	 * @param knoten
	 * @param graph
	 * @return
	 */
	private DelegateTree<Knoten, Kante> konstruiereGraph(Knoten knoten,
			DelegateTree<Knoten, Kante> graph) {

		if (graph == null) {
			graph = new DelegateTree<Knoten, Kante>();
			graph.setRoot(knoten);
		}

		Iterator<String> kinder = knoten.getKinder().keySet().iterator();
		while (kinder.hasNext()) {
			Knoten kind = knoten.getKinder().get(kinder.next());
			Kante neueKante = new Kante(kind.getName().intern());
			graph.addEdge(neueKante, knoten, kind, EdgeType.DIRECTED);
			konstruiereGraph(kind, graph);
		}

		return graph;
	}

}
