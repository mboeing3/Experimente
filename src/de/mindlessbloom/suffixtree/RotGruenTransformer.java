package de.mindlessbloom.suffixtree;

import java.awt.Color;
import java.awt.Paint;

import org.apache.commons.collections15.Transformer;

public class RotGruenTransformer implements Transformer<Knoten,Paint> {

	@Override
	public Paint transform(Knoten arg0) {
        if(arg0.isMatch()) return new Color(105,169,41);
        return new Color(145,18,43);
    }

}
