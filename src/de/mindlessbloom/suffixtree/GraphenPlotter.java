package de.mindlessbloom.suffixtree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class GraphenPlotter {

	/**
	 * Gibt graphische Repraesentation des uebergebenen Graphen aus (via Swing/JUNG2)
	 * @param graph
	 */
	public void plot(DelegateTree<Knoten, Kante> graph){
		this.plot(graph, 1);
	}
	
	/**
	 * Gibt graphische Repraesentation des uebergebenen Graphen aus (via Swing/JUNG2)
	 * @param graph
	 * @param layoutNr 1: RadialTreeLayout, 2:BalloonLayout
	 */
	public void plot(DelegateTree<Knoten, Kante> graph, int layoutNr){

		// Layout des Graphen instanziieren
		Layout<Knoten, Kante> layout = null;
		if (layoutNr==1){
			layout = new RadialTreeLayout<Knoten, Kante>(graph, 50, 50);
		} else if (layoutNr==2){
			layout = new BalloonLayout<Knoten, Kante>(graph);
		}
		
		
		// Ausgabeklasse instanziieren
		VisualizationViewer<Knoten, Kante> vv = new VisualizationViewer<Knoten, Kante>(layout);
		vv.setPreferredSize(new Dimension(800, 800)); // Sets the
														// viewing
		// Etikette definieren
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Knoten>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<Kante>());
		
		// Visuelle Merkmale der Knoten konfigurieren (Treffer = gruen, andere = rot)
        Transformer<Knoten,Paint> vertexColor = new Transformer<Knoten,Paint>() {
            public Paint transform(Knoten arg0) {
                if(arg0.isMatch()) return Color.GREEN;
                return Color.RED;
            }
        };
        // Visuelle Merkmale der Knoten konfigurieren (Je hoeher der Beruehrungszaehlerwert des Knoten, desto groesser wird dieser dargestellt)
        Transformer<Knoten,Shape> vertexSize = new Transformer<Knoten,Shape>(){
            public Shape transform(Knoten arg0){
                Ellipse2D circle = new Ellipse2D.Double(-15, -15, 30, 30);
                // Zur besseren Uebersichtlichkeit ist der Zusammenhang zwischen Zaehlerwert und visueller Groesse logarithmisch
                return AffineTransform.getScaleInstance(Math.log((double)arg0.getZaehler()+2d)/4d, Math.log((double)arg0.getZaehler()+2d)/4d).createTransformedShape(circle);
            }
        };
        // Merkmalsdefinitionen an Ausgabeklasse anfuegen
        vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);
        vv.getRenderContext().setVertexShapeTransformer(vertexSize);
		
		// Mausinteraktion festlegen
		DefaultModalGraphMouse<Knoten, Kante> gm = new DefaultModalGraphMouse<Knoten, Kante>();
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(gm);
		
		// Hintergrundfarbe festlegen
		vv.setBackground(Color.WHITE);
		
		// Neues GUI-Fenster
		JFrame frame = new JFrame(graph.getRoot().getName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}
	
}
