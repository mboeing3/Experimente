package de.mindlessbloom.suffixtree.experiment05;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import de.mindlessbloom.nebenlaeufigkeit.RueckmeldeProzess;
import de.mindlessbloom.nebenlaeufigkeit.RueckmeldungsEmpfaenger;
import de.mindlessbloom.suffixtree.Knoten;
import de.mindlessbloom.suffixtree.KnotenKomparator;

/**
 * Ermittelt den am besten passendsten (Meta)Knoten aus einem Pool und gibt
 * den Vergleichsbaum aus beiden an den RueckmeldungsEmpfaenger zurueck.
 * @author marcel
 *
 */
public class KnotenPartnerProzessor implements RueckmeldeProzess {
	
	private RueckmeldungsEmpfaenger rueckmeldungsEmpfaenger;
	private MetaKnoten einsamerKnoten;
	private ConcurrentHashMap<String, MetaKnoten> partnerPool;
	private KnotenKomparator komparator;
	
	// Zeigt an, ob im Kombinationsbaum nur die Trefferknoten enthalten sein sollen
	private boolean behalteNurTreffer;

	public KnotenPartnerProzessor(RueckmeldungsEmpfaenger rueckmeldungsEmpfaenger,
			MetaKnoten einsamerKnoten, ConcurrentHashMap<String, MetaKnoten> partnerPool,
			KnotenKomparator komparator, boolean behalteNurTreffer) {
		super();
		this.rueckmeldungsEmpfaenger = rueckmeldungsEmpfaenger;
		this.einsamerKnoten = einsamerKnoten;
		this.partnerPool = partnerPool;
		this.komparator = komparator;
		this.behalteNurTreffer = behalteNurTreffer;
	}

	public MetaKnoten getEinsamerKnoten() {
		return einsamerKnoten;
	}

	public void setEinsamerKnoten(MetaKnoten einsamerKnoten) {
		this.einsamerKnoten = einsamerKnoten;
	}

	public ConcurrentHashMap<String, MetaKnoten> getPartnerPool() {
		return partnerPool;
	}

	public void setPartnerPool(ConcurrentHashMap<String, MetaKnoten> partnerPool) {
		this.partnerPool = partnerPool;
	}

	public KnotenKomparator getKomparator() {
		return komparator;
	}

	public void setKomparator(KnotenKomparator komparator) {
		this.komparator = komparator;
	}

	public boolean isBehalteNurTreffer() {
		return behalteNurTreffer;
	}

	public void setBehalteNurTreffer(boolean behalteNurTreffer) {
		this.behalteNurTreffer = behalteNurTreffer;
	}

	@Override
	public void run() {
		
		try {

			// Variable fuer bisherig besten Vergleichswert
			Double besterVergleichswert = 0d;
			
			// Variable fuer Bestpassendsten Knoten
			MetaKnoten besterPartner = null;
			
			// Variable fuer Kombinationsbaum aus beiden Knoten
			Knoten besterKombinationsBaumWurzel = null;
			
			// Knoten des Partnerpools durchlaufen
			Iterator<MetaKnoten> partnerKnoten = this.partnerPool.values().iterator();
			while (partnerKnoten.hasNext()){
				
				// Aktuellen Knoten ermitteln
				MetaKnoten knoten = partnerKnoten.next();
				
				// Vergleich mit sich selbst ausschliessen
				if (knoten.equals(this.einsamerKnoten)){
					continue;
				}
				
				// Baeume miteinander kombinieren
				Knoten kombinationsBaumWurzel = this.komparator.verschmelzeBaeume(einsamerKnoten.getKnoten(), knoten.getKnoten());
				Double[] trefferWert = this.komparator.ermittleKnotenTrefferwert(kombinationsBaumWurzel);
				Double vergleichswert =  new Double(trefferWert[0] / trefferWert[1]);
				
				// Ergebnis auswerten
				if (vergleichswert > besterVergleichswert){
					besterVergleichswert = vergleichswert;
					besterPartner = knoten;
					besterPartner.setUebereinstimmungsQuotient(vergleichswert);
					besterKombinationsBaumWurzel = kombinationsBaumWurzel;
				}
				
			}
			
			// Den einsamen Knoten zurueckgeben, wenn GAR KEINE Uebereinstimmung gefunden wurde
			if (besterPartner == null){
				// Ergebnis an RueckmeldungsEmpfaenger zurueckgeben
				this.rueckmeldungsEmpfaenger.empfangeRueckmeldung(this.einsamerKnoten, this);
			}
			
			// Ansonsten werden entsprechende MetaKnoten geschaffen und als Kombination zurueckgegeben
			else {
				
				// Ggf. Vergleichsbaum auf Trefferknoten beschraenken
				if (this.behalteNurTreffer){
					besterKombinationsBaumWurzel = this.komparator.trefferBaum(besterKombinationsBaumWurzel);
				}
				
				// Metaknoten mit dem kombinierten Vergleichsbaum erstellen
				MetaKnoten vergleichsbaumMetaKnoten = new MetaKnoten(besterKombinationsBaumWurzel);
				vergleichsbaumMetaKnoten.getKindMetaKnoten().add(this.einsamerKnoten);
				vergleichsbaumMetaKnoten.getKindMetaKnoten().add(besterPartner);
				vergleichsbaumMetaKnoten.setUebereinstimmungsQuotient(besterVergleichswert);
				
				// Ergebnis an RueckmeldungsEmpfaenger zurueckgeben
				this.rueckmeldungsEmpfaenger.empfangeRueckmeldung(vergleichsbaumMetaKnoten, this);
			}
			
		} catch (Exception e){
			this.rueckmeldungsEmpfaenger.empfangeAusnahme(e);
		}
		
		
	}

	@Override
	public RueckmeldungsEmpfaenger getRueckmeldungsEmpfaenger() {
		return this.rueckmeldungsEmpfaenger;
	}

	@Override
	public void setRueckmeldungsEmpfaenger(RueckmeldungsEmpfaenger rueckmeldungsEmpfaenger) {
		this.rueckmeldungsEmpfaenger = rueckmeldungsEmpfaenger;
	}


}
