package de.mindlessbloom.suffixtree;

import java.awt.Color;
import java.awt.Paint;

import org.apache.commons.collections15.Transformer;

public class SchwarzWeissTransformer implements Transformer<Knoten,Paint> {

	@Override
	public Paint transform(Knoten arg0) {
        if(arg0.isMatch()) return Color.GRAY;
        return Color.WHITE;
    }

}
