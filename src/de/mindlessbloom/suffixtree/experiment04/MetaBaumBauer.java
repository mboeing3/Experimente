package de.mindlessbloom.suffixtree.experiment04;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import de.mindlessbloom.nebenlaeufigkeit.Aktion;
import de.mindlessbloom.nebenlaeufigkeit.RueckmeldeProzess;
import de.mindlessbloom.nebenlaeufigkeit.RueckmeldungsEmpfaenger;
import de.mindlessbloom.suffixtree.experiment01_03.Knoten;
import de.mindlessbloom.suffixtree.experiment01_03.KnotenKomparator;

/**
 * Stellt Methoden zur Erstellung und Manipulation von Metabaeumen bereit.
 * @author marcel
 *
 */
public class MetaBaumBauer implements RueckmeldungsEmpfaenger {
	
	/*
	 * Variablen
	 */
	
	// Liste der laufenden Prozesse
	private HashMap<KnotenPartnerProzessor, Aktion> rueckmeldeAktionen = new HashMap<KnotenPartnerProzessor, Aktion>();

	// Knotenkomparator
	private KnotenKomparator komparator = new KnotenKomparator();

	// Liste fuer MetaKnoten der naechsten Ebene; wird von den Vergleichsprozessen befuellt
	private List<MetaKnoten> metaKnotenPoolNaechsterEbene;
	
	// Zeigt an, welches Element des Metaknotenpools als naechstes verarbeitet werden soll.
	private int metaKnotenPoolVerarbeitungsIndex;

	// Metaknotenliste
	private List<MetaKnoten> metaKnotenPool = null;

	// Nur Trefferknoten in Vergleichsbaeumen abbilden
	private boolean behalteNurTreffer;
	
	// Gleichzeitig auszufuehrende Prozesse
	private int gleichzeitigeProzesse = 1;
	
	// Variable fuer Ergebnis
	private List<MetaKnoten> ergebnisListe = null;

	/*
	 * Konstruktor
	 */
	public MetaBaumBauer(KnotenKomparator komparator,
			List<MetaKnoten> metaKnotenPool, boolean behalteNurTreffer, int gleichzeitigeProzesse) {
		super();
		this.komparator = komparator;
		this.metaKnotenPool = metaKnotenPool;
		this.behalteNurTreffer = behalteNurTreffer;
		this.gleichzeitigeProzesse = gleichzeitigeProzesse;
	}

	/*
	 * Getter und Setter
	 */
	public KnotenKomparator getKomparator() {
		return komparator;
	}

	public void setKomparator(KnotenKomparator komparator) {
		this.komparator = komparator;
	}

	public List<MetaKnoten> getMetaKnotenPool() {
		return metaKnotenPool;
	}

	public void setMetaKnotenPool(List<MetaKnoten> metaKnotenPool) {
		this.metaKnotenPool = metaKnotenPool;
	}

	public boolean isBehalteNurTreffer() {
		return behalteNurTreffer;
	}

	public void setBehalteNurTreffer(boolean behalteNurTreffer) {
		this.behalteNurTreffer = behalteNurTreffer;
	}
	
	public int getGleichzeitigeProzesse() {
		return gleichzeitigeProzesse;
	}

	public void setGleichzeitigeProzesse(int gleichzeitigeProzesse) {
		this.gleichzeitigeProzesse = gleichzeitigeProzesse;
	}

	/**
	 * Gibt den Ergebnisbaum des juengsten Aufrufs von baueBaum() zurueck.
	 * @return
	 */
	public List<MetaKnoten> getErgebnisListe() {
		return ergebnisListe;
	}

	/*
	 * Baumerstellung und -manipulation
	 */

	/**
	 * Konstruiert neue Ebene von Metaknoten.
	 * @return Liste der neuen Metaknoten
	 */
	public List<MetaKnoten> baueBaum() {
		
		// Ergebnisvariable loeschen
		this.ergebnisListe = null;
		
		// Neue Instanz fuer Metaknotenpool der naechsten Ebene
		metaKnotenPoolNaechsterEbene = new ArrayList<MetaKnoten>();
		
		// Verarbeitungsindex auf erstes Element setzen, das nach den initialen Prozessen verarbeitet werden soll.
		metaKnotenPoolVerarbeitungsIndex = gleichzeitigeProzesse;
		
		// Schleife ueber Anzahl der maximalen Parallelprozesse
		for (int i=0; (i<gleichzeitigeProzesse && i<metaKnotenPool.size()); i++){
			
			// Rueckmelde-Aktion definieren
			KnotenPartnerProzessorRueckmeldeAktion aktion = new KnotenPartnerProzessorRueckmeldeAktion(metaKnotenPoolNaechsterEbene);
			
			// Naechsten Metaknoten ermitteln, falls vorhanden (Es ist bei sehr kleinen Korpora moeglich, dass die bereits gestarteten Prozesse zu diesem Zeitpunkt alles schon abgearbeitet haben).
			MetaKnoten naechsterKnoten = metaKnotenPool.get(i);
			
			if (naechsterKnoten != null){
				// Prozessor instanziieren und mit Aktion in Liste aufnehmen
				KnotenPartnerProzessor prozessor = new KnotenPartnerProzessor(this, naechsterKnoten, metaKnotenPool, komparator, behalteNurTreffer);
				this.rueckmeldeAktionen.put(prozessor, aktion);
				
				// Prozessor in Prozess kapseln und nebenlaeufig starten
				Thread prozess = new Thread(prozessor);
				prozess.start();
			}
		}
		
		// Auf Ergebnisse warten und derweil Meldungen ausgeben
		while(this.ergebnisListe == null){
			
			// Prozess schlafen legen
			try {
				Thread.sleep(1500l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Meldung ausgeben
			Logger.getLogger(this.getClass().getSimpleName()).info("Konstruiere Metaknotenbaum. Laufende Prozesse: "+this.rueckmeldeAktionen.size());
		}
		
		return this.ergebnisListe;
	}

	@Override
	public void empfangeRueckmeldung(Object ergebnis, RueckmeldeProzess prozess) {
		
		// Falls Ergebnis Null ist, abbrechen
		if (ergebnis == null){
			Logger.getLogger(Start.class.getCanonicalName()).warning("Null-Ergebnis empfangen (wird ignoriert).");
			return;
		}
		
		// Aktion ermitteln und aus Liste loeschen
		Aktion ergebnisAktion = this.rueckmeldeAktionen.get(prozess);
		
		// Pruefen, ob Aktion gefunden wurde
		if (ergebnisAktion != null){
			
			// Prozess
			
			// Mit Prozess assoziierte Aktion ausfuehren
			ergebnisAktion.ausfuehren(ergebnis);
			

			synchronized (this) {
				
				// Verarbeitungsindex erhoehen
				this.metaKnotenPoolVerarbeitungsIndex++;

				// Pruefen, ob noch Metaknoten im Pool verbleiben
				if (this.metaKnotenPoolVerarbeitungsIndex<this.metaKnotenPool.size()) {

					// Pruefen, ob weitere MetaKnoten existieren, die
					// verarbeitet werden sollen
					MetaKnoten naechsterKnoten = null;
					try {
						naechsterKnoten = this.metaKnotenPool.get(metaKnotenPoolVerarbeitungsIndex);

					} catch (NoSuchElementException e) {
						e.printStackTrace();
					}

					// Falls noch ein Knoten existiert, wird der naechste
					// Prozessor initiiert und gestartet
					if (naechsterKnoten != null) {
						
						Logger.getLogger(Start.class.getCanonicalName()).info("Erstelle naechsten Prozess ("+this.metaKnotenPoolVerarbeitungsIndex+" / "+this.metaKnotenPool.size());
						
						// Rueckmelde-Aktion definieren
						KnotenPartnerProzessorRueckmeldeAktion aktion = new KnotenPartnerProzessorRueckmeldeAktion(
								metaKnotenPoolNaechsterEbene);

						// Prozessor instanziieren und mit Aktion in Liste
						// aufnehmen
						KnotenPartnerProzessor prozessor = new KnotenPartnerProzessor(
								this, naechsterKnoten, metaKnotenPool,
								komparator, behalteNurTreffer);
						this.rueckmeldeAktionen.put(prozessor, aktion);

						// Prozessor in Prozess kapseln und nebenlaeufig starten
						Thread naechsterProzess = new Thread(prozessor);
						naechsterProzess.start();
					}
				} else {
					// Keine Knoten mehr verbleibend - pruefen, ob noch Prozesse
					// laufen, oder die Bearbeitung der aktuellen Beumebene
					// (bzw. des aktuellen Metaknotenpools) abgeschlossen ist.

					if (this.rueckmeldeAktionen.size() <= 1) {
						// Meldung ausgeben
						Logger.getLogger(Start.class.getCanonicalName()).info(
								"Bearbeitung des aktuellen Pools abgeschlossen. Der neue Pool hat "
										+ this.metaKnotenPoolNaechsterEbene
												.size() + " Elemente.");

						// Ergebnis speichern
						this.ergebnisListe = metaKnotenPoolNaechsterEbene;
					}
				}

				// Prozess aus Liste entfernen
				this.rueckmeldeAktionen.remove(prozess);

			}
			
		} else {
			// Keine Aktion zu empfangenem Prozess gefunden; Meldung ausgeben
			Logger.getLogger(Start.class.getCanonicalName()).warning("Ergebnis von unbekanntem Prozess empfangen (wird ignoriert).");
		}
		
	}

	@Override
	public void empfangeRueckmeldung(Object ergebnis,
			RueckmeldeProzess prozess, boolean wiederholend) {
		this.empfangeRueckmeldung(ergebnis, prozess);
		
	}

	@Override
	public void empfangeAusnahme(Exception e) {
		Logger.getLogger(this.getClass().getSimpleName()).warning(e.toString());
		e.printStackTrace();
	}
	
	/**
	 * Bildet eine Metaknotenstruktur mit Knoten ab (verwirft also die Tokendimension).
	 * Der Uebereinstimmungsquotient des Metaknotens wird dabei in Promille im Zaehler
	 * des Knotens angegeben.
	 * @param mk
	 * @return Wurzel des neuen Baumes
	 */
	public Knoten konvertiereMetaKnotenZuKnoten(MetaKnoten mk){
		if (mk == null || mk.getKnoten() == null) return null;
		Knoten k = new Knoten();
		k.setName(mk.getKnoten().getName());
		if (mk.getUebereinstimmungsQuotient() != null){
			k.setZaehler((int) (mk.getUebereinstimmungsQuotient()*1000d));
			k.setMatch(true);
		}
		
		Iterator<MetaKnoten> kinder = mk.getKindMetaKnoten().iterator();
		while(kinder.hasNext()){
			Knoten kind = konvertiereMetaKnotenZuKnoten(kinder.next());
			if (kind != null)
			k.getKinder().put(kind.getName(), kind);
		}
		
		return k;
	}
	
}
