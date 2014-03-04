package de.mindlessbloom.suffixtree;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import org.apache.commons.collections15.Transformer;

public class GewichtGroesseTransformer implements Transformer<Knoten,Shape> {

	@Override
	public Shape transform(Knoten arg0){
        Ellipse2D circle = new Ellipse2D.Double(-15, -15, 30, 30);
        // Zur besseren Uebersichtlichkeit ist der Zusammenhang zwischen Zaehlerwert und visueller Groesse logarithmisch
        return AffineTransform.getScaleInstance(Math.log((double)arg0.getZaehler()+2d)/4d, Math.log((double)arg0.getZaehler()+2d)/4d).createTransformedShape(circle);
    }

}
