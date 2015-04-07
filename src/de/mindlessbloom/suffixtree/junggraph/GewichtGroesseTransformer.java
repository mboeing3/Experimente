package de.mindlessbloom.suffixtree.junggraph;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import org.apache.commons.collections15.Transformer;

import de.mindlessbloom.suffixtree.experiment01_04.Knoten;

public class GewichtGroesseTransformer implements Transformer<Knoten,Shape> {
	
	private double basisGroesse = 2d;
	
	public GewichtGroesseTransformer() {
		super();
	}

	public GewichtGroesseTransformer(double basisGroesse) {
		super();
		if (basisGroesse > 0)
		this.basisGroesse = basisGroesse;
	}
	
	@Override
	public Shape transform(Knoten arg0){
        Ellipse2D circle = new Ellipse2D.Double(-15, -15, 30, 30);
        // Zur besseren Uebersichtlichkeit ist der Zusammenhang zwischen Zaehlerwert und visueller Groesse logarithmisch
        return AffineTransform.getScaleInstance(Math.log((double)arg0.getZaehler()+basisGroesse)/4d, Math.log((double)arg0.getZaehler()+basisGroesse)/4d).createTransformedShape(circle);
    }

}
