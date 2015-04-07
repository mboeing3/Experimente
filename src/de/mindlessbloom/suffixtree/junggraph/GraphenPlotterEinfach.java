package de.mindlessbloom.suffixtree.junggraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import de.mindlessbloom.suffixtree.experiment01_04.Kante;
import de.mindlessbloom.suffixtree.experiment01_04.Knoten;
import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ConstantDirectionalEdgeValueTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class GraphenPlotterEinfach {

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
		Transformer<Context<Graph<Knoten,Kante>,Kante>,Shape> edgeShapeTr = new Transformer<Context<Graph<Knoten,Kante>,Kante>,Shape>(){

			@Override
			public Shape transform(Context<Graph<Knoten, Kante>, Kante> arg0) {
				Line2D linie = new Line2D.Double(0, -10, 0, 10);
				return linie;
			}
			
		};
		//vv.getRenderContext().setEdgeShapeTransformer(edgeShapeTr);
		// Etikette definieren
		//vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Knoten>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<Kante>());
		
		// Visuelle Merkmale der Knoten konfigurieren (Treffer = gruen, andere = rot)
        Transformer<Knoten,Paint> vertexColor = new Transformer<Knoten, Paint>(){

			@Override
			public Paint transform(Knoten arg0) {
				return new Color(255,255,255);
			}};
		
		Transformer<Knoten,Shape> vertexSize = new Transformer<Knoten,Shape>(){

			@Override
			public Shape transform(Knoten arg0) {
				Ellipse2D circle = new Ellipse2D.Double(-5, -5, 10, 10);
		        // Zur besseren Uebersichtlichkeit ist der Zusammenhang zwischen Zaehlerwert und visueller Groesse logarithmisch
		        return AffineTransform.getScaleInstance(0.2d,0.2d).createTransformedShape(circle);
			}};
        
        //Transformer<Knoten,Paint> vertexColor = new SchwarzWeissTransformer();
        // Visuelle Merkmale der Knoten konfigurieren (Je hoeher der Beruehrungszaehlerwert des Knoten, desto groesser wird dieser dargestellt)
        //Transformer<Knoten,Shape> vertexSize = new GewichtGroesseTransformer(4d);
        // Merkmalsdefinitionen an Ausgabeklasse anfuegen
        vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);
        vv.getRenderContext().setVertexShapeTransformer(vertexSize);
        //Transformer<Knoten,Paint> vertexRandColor = new RotGruenTransformer();
        //vv.getRenderContext().setVertexDrawPaintTransformer(vertexRandColor);
        DefaultVertexLabelRenderer etikettRenderer = new DefaultVertexLabelRenderer(Color.white);
        etikettRenderer.setForeground(Color.white); // Keine Auswirkung?
        vv.getRenderContext().setVertexLabelRenderer(etikettRenderer);
        
        // Knotenetikette mittig im Knoten darstellen
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
        
        // Kantenetikette mittig platzieren
        ConstantDirectionalEdgeValueTransformer<Knoten, Kante> kantenEtikettTransformer = new ConstantDirectionalEdgeValueTransformer<Knoten, Kante>(0.5d, 0.5d);
		vv.getRenderContext().setEdgeLabelClosenessTransformer(kantenEtikettTransformer);
		
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
