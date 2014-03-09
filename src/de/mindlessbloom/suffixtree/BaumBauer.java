package de.mindlessbloom.suffixtree;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;

import de.mindlessbloom.suffixtree.experiment03.Start;
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
		
		// "Beruehrung" des Knotens mitzaehlen
		rootnode.setZaehler(rootnode.getZaehler() + 1);

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
			kindKnoten.setName(token[vergleichsTokenIndex]);
			
			// Kind dem Elternknoten anfuegen
			rootnode.getKinder().put(token[vergleichsTokenIndex], kindKnoten);
			
			// Ggf. Graphen aktualisieren
			if (graph != null) {
				// Kante
				Kante neueKante = new Kante(token[vergleichsTokenIndex]);
				graph.addEdge(neueKante, rootnode, kindKnoten,
						EdgeType.DIRECTED);
			}

		} else {
			// passender Knoten vorhanden
			kindKnoten = rootnode.getKinder().get(token[vergleichsTokenIndex]);
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
			Kante neueKante = new Kante(kind.getName());
			graph.addEdge(neueKante, knoten, kind, EdgeType.DIRECTED);
			konstruiereGraph(kind, graph);
		}

		return graph;
	}
	
	/**
	 * Filtert eine Satzliste (Liste einer Liste von Strings) anhand eines Wortfilters und erstellt im uebergebenen Graphen einen Baum.
	 * @param wortTyp
	 * @param satzListe
	 * @param wf
	 * @param graph
	 * @param praefixGraph
	 * @param vergleichAufVergleichswortzweigBeschraenken
	 * @param praefixBaumErstellen
	 */
	public void erstelleGraphenFuerWorttyp(String wortTyp,
			List<List<String>> satzListe, WortFilter wf,
			DelegateTree<Knoten, Kante> graph,
			DelegateTree<Knoten, Kante> praefixGraph,
			boolean vergleichAufVergleichswortzweigBeschraenken,
			boolean praefixBaumErstellen) {
		// Saetze aus Korpus durchlaufen, Treffer mitzaehlen (fuer Anzeige)
		int saetzeDurchlaufen = 0;
		int saetzeGefunden = 0;
		Iterator<List<String>> saetze = satzListe.iterator();
		while (saetze.hasNext()) {

			// Naechsten Satz ermitteln
			List<String> satz = saetze.next();

			// Pruefen, ob WortFilter greift
			if (wf.hatWort(satz)) {

				// Ggf. nur Trie ab dem Vergleichswort bauen
				if (vergleichAufVergleichswortzweigBeschraenken) {

					// Ermitteln, an welchen Stellen im Satz das Vergleichswort
					// vorkommt
					Integer[] vergleichsWortIndices = wf.getWortIndices(satz);

					// Indices durchlaufen
					for (int j = 0; j < vergleichsWortIndices.length; j++) {
						// Satz in Array konvertieren
						String[] satzArray = satz.toArray(new String[satz
								.size()]);
						// Satz in den Baum/Graphen hineinbauen
						this.baueTrie(Arrays.copyOfRange(satzArray,
								vergleichsWortIndices[j], satzArray.length),
								graph.getRoot(), graph, false);
						// Ggf. Satz ebenfalls in den Praefixbaum/-graphen
						// hineinbauen
						if (praefixGraph != null && praefixBaumErstellen) {
							this.baueTrie(Arrays.copyOfRange(satzArray, 0,
									vergleichsWortIndices[j] + 1), praefixGraph
									.getRoot(), praefixGraph, true);
						}
					}

				} else {
					// Satz in den Baum/Graphen hineinbauen
					this.baueBaum(satz.toArray(new String[satz.size()]),
							graph.getRoot(), graph, false);
				}

				// Treffer mitzaehlen
				saetzeGefunden++;
			}

			// Durchlaufenen Satz mitzaehlen
			saetzeDurchlaufen++;

			// Meldung ausgeben
			double prozentFertig = Math
					.ceil(((double) saetzeDurchlaufen / (double) satzListe
							.size()) * 100);
			if ((satzListe.size() / 20) != 0
					&& saetzeDurchlaufen % (satzListe.size() / 20) == 0) {
				Logger.getLogger(
						Start.class.getCanonicalName())
						.info("Ermittle Saetze, die Wort '" + wortTyp
								+ "' beinhalten: " + saetzeDurchlaufen + "/"
								+ satzListe.size() + " (" + saetzeGefunden
								+ ") " + prozentFertig + "%");
			}

		}
		// Zeilenumbruch in Anzeige ausgeben
		System.out.println();
	}
	
	/**
	 * Filtert eine Satzliste (Liste einer Liste von Strings) anhand eines Wortfilters und erstellt im uebergebenen Knoten einen Baum.
	 * @param wortTyp
	 * @param satzListe
	 * @param wf
	 * @param wurzel
	 * @param praefixwurzel
	 * @param vergleichAufVergleichswortzweigBeschraenken
	 * @param praefixBaumErstellen
	 */
	public void erstelleGraphenFuerWorttyp(String wortTyp,
			List<List<String>> satzListe, WortFilter wf, Knoten wurzel, Knoten praefixwurzel,
			boolean vergleichAufVergleichswortzweigBeschraenken,
			boolean praefixBaumErstellen) {
		// Saetze aus Korpus durchlaufen, Treffer mitzaehlen (fuer Anzeige)
		int saetzeDurchlaufen = 0;
		int saetzeGefunden = 0;
		Iterator<List<String>> saetze = satzListe.iterator();
		while (saetze.hasNext()) {

			// Naechsten Satz ermitteln
			List<String> satz = saetze.next();

			// Pruefen, ob WortFilter greift
			if (wf.hatWort(satz)) {

				// Ggf. nur Trie ab dem Vergleichswort bauen
				if (vergleichAufVergleichswortzweigBeschraenken) {

					// Ermitteln, an welchen Stellen im Satz das Vergleichswort
					// vorkommt
					Integer[] vergleichsWortIndices = wf.getWortIndices(satz);

					// Indices durchlaufen
					for (int j = 0; j < vergleichsWortIndices.length; j++) {
						// Satz in Array konvertieren
						String[] satzArray = satz.toArray(new String[satz
								.size()]);
						// Satz in den Baum/Graphen hineinbauen
						this.baueTrie(Arrays.copyOfRange(satzArray,
								vergleichsWortIndices[j], satzArray.length),
								wurzel, null, false);
						// Ggf. Satz ebenfalls in den Praefixbaum/-graphen
						// hineinbauen
						if (praefixwurzel != null && praefixBaumErstellen) {
							this.baueTrie(Arrays.copyOfRange(satzArray, 0,
									vergleichsWortIndices[j] + 1), praefixwurzel, null, true);
						}
					}

				} else {
					// Satz in den Baum/Graphen hineinbauen
					this.baueBaum(satz.toArray(new String[satz.size()]),
							wurzel, null, false);
				}

				// Treffer mitzaehlen
				saetzeGefunden++;
			}

			// Durchlaufenen Satz mitzaehlen
			saetzeDurchlaufen++;

			// Meldung ausgeben
			double prozentFertig = Math
					.ceil(((double) saetzeDurchlaufen / (double) satzListe
							.size()) * 100);
			if ((satzListe.size() / 20) != 0
					&& saetzeDurchlaufen % (satzListe.size() / 20) == 0) {
				Logger.getLogger(
						Start.class.getCanonicalName())
						.info("Ermittle Saetze, die Wort '" + wortTyp
								+ "' beinhalten: " + saetzeDurchlaufen + "/"
								+ satzListe.size() + " (" + saetzeGefunden
								+ ") " + prozentFertig + "%");
			}

		}
		// Zeilenumbruch in Anzeige ausgeben
		System.out.println();
	}

}
