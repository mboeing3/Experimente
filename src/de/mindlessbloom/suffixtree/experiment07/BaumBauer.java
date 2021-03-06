package de.mindlessbloom.suffixtree.experiment07;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.ubiety.ubigraph.UbigraphClient;

import de.mindlessbloom.suffixtree.experiment01_04.Kante;
import de.mindlessbloom.suffixtree.experiment01_04.WortFilter;
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
		return this.baueBaum(token, rootnode, graph, umgekehrt, -1);
	}
	
	/**
     * Erzeugt einen Suffixbaum im uebergebenen Graphen ab dem uebergebenen
     * Knoten anhand der uebergebenen Token. Inkrementiert die Zaehlvariable
     * eines jeden Knotens um eins fuer jede "Beruehrung".
     * @param token String-Array mit Token (Woerter, Buchstaben, Symbole, ... egal was)
     * @param rootnode Startknoten Wurzelknoten des zu konstruierenden Baumes
     * @param graph Graph Darf null sein
     * @param umgekehrt Zeigt an, ob der Baum umgekehrt erstellt werden soll (quasi als "Praefixbaum")
     * @param maxLaenge Die maximale Tiefe des zu erstellenden Baumes, inklusive des Wurzelknotens (<0 = ignorieren)
     * @return Die Anzahl der neu erstellten Knoten
     */
	public int baueBaum(String[] token, Knoten rootnode, Graph<Knoten, Kante> graph, boolean umgekehrt, int maxLaenge) {
		
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
			double inkrement = 1d/Math.pow(2,i);
			knotenEingefuegt += this.baueTrie(Arrays.copyOfRange(token, von, bis), rootnode, graph, umgekehrt, maxLaenge, inkrement);
			
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
	public int baueTrie(String[] token, Knoten rootnode, boolean umgekehrt, double inkrement) {
		return this.baueTrie(token, rootnode, null, umgekehrt, inkrement);
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
    public int baueTrie(String[] token, Knoten rootnode, Graph<Knoten, Kante> graph, boolean umgekehrt, double inkrement) {
    	return this.baueTrie(token, rootnode, graph, umgekehrt, -1, inkrement);
    }
	
    /**
     * Erzeugt einen Suffixtrie im uebergebenen Graphen ab dem uebergebenen
     * Knoten anhand der uebergebenen Token. Inkrementiert die Zaehlvariable
     * eines jeden Knotens um eins fuer jede "Beruehrung", jeweils halbiert
     * fuer jeden Entfernungsschritt des verarbeiteten Suffix.
     * @param token String-Array mit Token (Woerter, Buchstaben, Symbole, ... egal was)
     * @param rootnode Startknoten Wurzelknoten des zu konstruierenden Baumes
     * @param graph Graph Darf null sein
     * @param umgekehrt Zeigt an, ob der Baum umgekehrt erstellt werden soll (quasi als "Praefixbaum")
     * @param maxLaenge Die maximale Anzahl an Token, die dem Trie hinzugefuegt werden soll (<0 = ignorieren).
     * @return Die Anzahl der neu erstellten Knoten
     */
    public int baueTrie(String[] token, Knoten rootnode, Graph<Knoten, Kante> graph, boolean umgekehrt, int maxLaenge, double inkrement) {

    	// Variable zum Mitzaehlen der erstellten Knoten
		int knotenerstellt = 0;
		
		// "Beruehrung" des Knotens mitzaehlen
		rootnode.setZaehler(rootnode.getZaehler() + inkrement);

		// Wenn keine Token mehr vorhanden sind bzw. die maximal hinzuzufuegende Anzahl an Token ueberschritten wird, wird abgebrochen
		if (token == null || token.length == 0 || maxLaenge==0) {
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
					kindKnoten, graph, umgekehrt, maxLaenge-1, inkrement);
		} else {
			// Rekursiver Aufruf mit Token 1 bis n
			knotenerstellt += this.baueTrie(
					Arrays.copyOfRange(token, 1, token.length),
					kindKnoten, graph, umgekehrt, maxLaenge-1, inkrement);
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
	 * Gibt einen UbiGraphen mit dem uebergebenen Baum zurueck
	 * 
	 * @param knoten
	 * @return
	 */
	public UbigraphClient konstruiereUbiGraph(Knoten knoten) {

		return this.konstruiereUbiGraph(knoten, null, 0);
		
	}
	
	/**
	 * Gibt einen UbiGraphen mit dem uebergebenen Baum zurueck
	 * 
	 * @param knoten
	 * @return
	 */
	public UbigraphClient konstruiereUbiGraph(Knoten knoten, UbigraphClient graph) {
		
		graph.newVertex(0);
		graph.setVertexAttribute(0, "label", knoten.getName());

		return this.konstruiereUbiGraph(knoten, graph, 0);
		
	}

	/**
	 * Gibt einen UbiGraphen mit dem uebergebenen Baum zurueck
	 * 
	 * @param knoten
	 * @param graph
	 * @return
	 */
	public UbigraphClient konstruiereUbiGraph(Knoten knoten,
			UbigraphClient graph, int vaterKnotenId) {

		if (graph == null) {
			graph = new UbigraphClient();
			graph.newVertex(0);
			graph.setVertexAttribute(0, "label", knoten.getName());
			vaterKnotenId = 0;
		}

		Iterator<String> kinder = knoten.getKinder().keySet().iterator();
		while (kinder.hasNext()) {
			Knoten kind = knoten.getKinder().get(kinder.next());
			int neuerKnotenId = graph.newVertex();
			graph.setVertexAttribute(neuerKnotenId, "label", kind.getName());
			double groesse = Math.log((double)kind.getZaehler()+2d)/4d;
			graph.setVertexAttribute(neuerKnotenId, "size", Double.toString(groesse));
			int neueKanteId = graph.newEdge(vaterKnotenId, neuerKnotenId);
			graph.setEdgeAttribute(neueKanteId, "oriented", "true");
			//graph.setEdgeAttribute(neueKanteId, "label", kind.getName());
			konstruiereUbiGraph(kind, graph, neuerKnotenId);
		}

		return graph;
	}
	
	/**
	 * @deprecated
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
								graph.getRoot(), graph, false, 0);
						// Ggf. Satz ebenfalls in den Praefixbaum/-graphen
						// hineinbauen
						if (praefixGraph != null && praefixBaumErstellen) {
							this.baueTrie(Arrays.copyOfRange(satzArray, 0,
									vergleichsWortIndices[j] + 1), praefixGraph
									.getRoot(), praefixGraph, true, 0);
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
						BaumBauer.class.getCanonicalName())
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
	 * @return Anzahl der in den Saetzen gefundenen Wortvorkommen. 
	 */
	public int baueTrieAusSaetzenMitWorttyp(String wortTyp,
			List<List<String>> satzListe, WortFilter wf, Knoten wurzel, Knoten praefixwurzel,
			boolean vergleichAufVergleichswortzweigBeschraenken,
			boolean praefixBaumErstellen, boolean ausfuehrlicheFortschrittsMeldungen) {
		// Saetze aus Korpus durchlaufen, Treffer mitzaehlen (fuer Anzeige)
		int saetzeDurchlaufen = 0;
		int saetzeGefunden = 0;
		int vorkommenGefunden = 0;
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
					
					// Anzahl der gefundenen Vorkommen mitzaehlen
					vorkommenGefunden += vergleichsWortIndices.length;

					// Indices durchlaufen
					for (int j = 0; j < vergleichsWortIndices.length; j++) {
						// Satz in Array konvertieren
						String[] satzArray = satz.toArray(new String[satz
								.size()]);
						// Satz in den Baum/Graphen hineinbauen
						this.baueTrie(Arrays.copyOfRange(satzArray,
								vergleichsWortIndices[j], satzArray.length),
								wurzel, null, false, 0);
						// Ggf. Satz ebenfalls in den Praefixbaum/-graphen
						// hineinbauen
						if (praefixwurzel != null && praefixBaumErstellen) {
							this.baueTrie(Arrays.copyOfRange(satzArray, 0,
									vergleichsWortIndices[j] + 1), praefixwurzel, null, true, 0);
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

			// ggf. Meldung ausgeben
			if (ausfuehrlicheFortschrittsMeldungen){
				double prozentFertig = Math
						.ceil(((double) saetzeDurchlaufen / (double) satzListe
								.size()) * 100);
				if ((satzListe.size() / 20) != 0
						&& saetzeDurchlaufen % (satzListe.size() / 20) == 0) {
					Logger.getLogger(
							BaumBauer.class.getCanonicalName())
							.info("Ermittle Saetze, die Wort '" + wortTyp
									+ "' beinhalten: " + saetzeDurchlaufen + "/"
									+ satzListe.size() + " (" + saetzeGefunden
									+ ") " + prozentFertig + "%");
				}
			}
		}
		
		// Zeilenumbruch in Anzeige ausgeben
		if (ausfuehrlicheFortschrittsMeldungen){
			System.out.println();
		}
		
		// Anzahl der gefundenen Wortvorkommen zurueckgeben
		return vorkommenGefunden;
	}
	
	/**
	 * Filtert eine Satzliste (Liste einer Liste von Strings) anhand eines Wortfilters und erstellt im uebergebenen Knoten einen Baum.
	 * @param wortTyp
	 * @param satzListe
	 * @param wf
	 * @param wurzel
	 * @param praefixwurzel
	 * @param praefixBaumErstellen
	 * @param maxLaenge Die maximale Tiefe des zu erstellenden Baumes, inklusive des Wurzelknotens (<0 = ignorieren)
	 * @param vergleichsworteNichtInBaumMitAufnehmen Schliesst die Vergleichsworte von den konstruierten Baeumen aus (Eingabesaetze werden entsprechend gekuerzt). 
	 * @return Anzahl der in den Saetzen gefundenen Wortvorkommen. 
	 */
	public int baueBaumAusSaetzenMitWorttyp(String wortTyp,
			List<List<String>> satzListe, WortFilter wf, Knoten wurzel, Knoten praefixwurzel,
			boolean praefixBaumErstellen, boolean ausfuehrlicheFortschrittsMeldungen, int maxLaenge, boolean vergleichsworteNichtInBaumMitAufnehmen) {
		// Saetze aus Korpus durchlaufen, Treffer mitzaehlen (fuer Anzeige)
		int saetzeDurchlaufen = 0;
		int saetzeGefunden = 0;
		int vorkommenGefunden = 0;
		Iterator<List<String>> saetze = satzListe.iterator();
		while (saetze.hasNext()) {

			// Naechsten Satz ermitteln
			List<String> satz = saetze.next();

			// Pruefen, ob WortFilter greift
			if (wf.hatWort(satz)) {

				// Ermitteln, an welchen Stellen im Satz das Vergleichswort
				// vorkommt
				Integer[] vergleichsWortIndices = wf.getWortIndices(satz);

				// Anzahl der gefundenen Vorkommen mitzaehlen
				vorkommenGefunden += vergleichsWortIndices.length;

				// Indices durchlaufen
				for (int j = 0; j < vergleichsWortIndices.length; j++) {
					// Satz in Array konvertieren
					String[] satzArray = satz.toArray(new String[satz.size()]);
					// Satz in den Baum/Graphen hineinbauen
					int index = vergleichsWortIndices[j];
					if (vergleichsworteNichtInBaumMitAufnehmen)
						index = vergleichsWortIndices[j]+1;
					if (index<satzArray.length){
						this.baueBaum(Arrays.copyOfRange(satzArray,
								index, satzArray.length),
								wurzel, null, false, maxLaenge);
					}
						
					
					// Ggf. Satz ebenfalls in den Praefixbaum/-graphen
					// hineinbauen
					index = vergleichsWortIndices[j];
					if (vergleichsworteNichtInBaumMitAufnehmen)
						index = vergleichsWortIndices[j]-1;
					if (index>=0)
						if (praefixwurzel != null && praefixBaumErstellen) {
							this.baueBaum(Arrays.copyOfRange(satzArray, 0,
									index), praefixwurzel,
									null, true, maxLaenge);
						}
				}

				// Treffer mitzaehlen
				saetzeGefunden++;
			}

			// Durchlaufenen Satz mitzaehlen
			saetzeDurchlaufen++;

			// ggf. Meldung ausgeben
			if (ausfuehrlicheFortschrittsMeldungen){
				double prozentFertig = Math
						.ceil(((double) saetzeDurchlaufen / (double) satzListe
								.size()) * 100);
				if ((satzListe.size() / 20) != 0
						&& saetzeDurchlaufen % (satzListe.size() / 20) == 0) {
					Logger.getLogger(
							BaumBauer.class.getCanonicalName())
							.info("Ermittle Saetze, die Wort '" + wortTyp
									+ "' beinhalten: " + saetzeDurchlaufen + "/"
									+ satzListe.size() + " (" + saetzeGefunden
									+ ") " + prozentFertig + "%");
				}
			}
		}
		
		// Zeilenumbruch in Anzeige ausgeben
		if (ausfuehrlicheFortschrittsMeldungen){
			System.out.println();
		}
		
		// Anzahl der gefundenen Wortvorkommen zurueckgeben
		return vorkommenGefunden;
	}

}
