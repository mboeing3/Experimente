package de.mindlessbloom.suffixtree.experiment07;

import java.awt.Color;
import java.awt.Paint;

import org.apache.commons.collections15.Transformer;


public class RotGruenTransformer implements Transformer<Knoten,Paint> {
	
	private int[] gruen_rgbwerte = new int[] {105,169,41};
	private int[] rot_rgbwerte = new int[] {145,18,43};
	
	public RotGruenTransformer() {
		super();
	}

	public RotGruenTransformer(int gbplus) {
		super();
		for (int i=0; i<3; i++){
			if (gruen_rgbwerte[i]+gbplus>255){
				gruen_rgbwerte[i] = 255;
			} else if (gruen_rgbwerte[i]+gbplus<0){
				gruen_rgbwerte[i] = 0;
			} else {
				gruen_rgbwerte[i] += gbplus;
			}
			if (rot_rgbwerte[i]+gbplus>255){
				rot_rgbwerte[i] = 255;
			} else if (gruen_rgbwerte[i]+gbplus<0){
				rot_rgbwerte[i] = 0;
			} else {
				rot_rgbwerte[i] += gbplus;
			}
		}
	}
	@Override
	public Paint transform(Knoten arg0) {
        if(arg0.isMatch()) return new Color(gruen_rgbwerte[0],gruen_rgbwerte[1],gruen_rgbwerte[2]);
        return new Color(rot_rgbwerte[0],rot_rgbwerte[1],rot_rgbwerte[2]);
    }

}
