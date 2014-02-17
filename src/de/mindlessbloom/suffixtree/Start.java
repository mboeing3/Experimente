package de.mindlessbloom.suffixtree;

import java.awt.EventQueue;
import java.util.Arrays;

import javax.swing.tree.DefaultMutableTreeNode;

public class Start {

	public static void main(String[] args) {

		String[] token1 = new String[] { "cat", "ate", "cheese", "$" };
		String[] token2 = new String[] { "mouse", "ate", "cheese", "too", "$" };
		String[] token3 = new String[] { "cat", "ate", "mouse", "too", "$" };

		String[][] token = new String[][] { token1, token2, token3 };

		final DefaultMutableTreeNode wurzel = new DefaultMutableTreeNode("^");

		Start s = new Start();

		// Alle Tokenarrays durchlaufen
		for (int j = 0; j < token.length; j++) {
			
			// Alle Token des aktuellen Tokenarrays durchlaufen
			for (int i = 0; i < token[j].length; i++) {
				s.trieBuilder(Arrays.copyOfRange(token[j], i, token[j].length),
						wurzel);
			}
		}

		// s.trieBuilder(token1, wurzel);
		// s.trieBuilder(token2, wurzel);
		// s.trieBuilder(token3, wurzel);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TreeGui window = new TreeGui(wurzel);
					window.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void trieBuilder(String[] token, DefaultMutableTreeNode rootnode) {

		if (token == null || token.length == 0) {
			return;
		}

		// Gibt an, welcher Kindknoten zum aktuellen Token passt
		int passenderKnotenIndex = -1;

		// Kindknoten durchlaufen
		for (int j = 0; j < rootnode.getChildCount(); j++) {

			DefaultMutableTreeNode kindKnoten = (DefaultMutableTreeNode) rootnode
					.getChildAt(j);
			if (token[0].equals(((Zaehler)kindKnoten.getUserObject()).getName())) {
				passenderKnotenIndex = j;
				break;
			}
		}

		// Ggf. neuen Knoten erstellen
		if (passenderKnotenIndex < 0) {
			// passender Knoten NICHT vorhanden - neuen erstellen und rekursiv
			// aufrufen
			DefaultMutableTreeNode neuerKnoten = new DefaultMutableTreeNode(
					new Zaehler(token[0]));
			rootnode.add(neuerKnoten);
			this.trieBuilder(Arrays.copyOfRange(token, 1, token.length),
					neuerKnoten);

		} else {
			// passender Knoten vorhanden - Zaehler inkrementieren und Rekursiv aufrufen
			( (Zaehler) ( (DefaultMutableTreeNode) rootnode.getChildAt(passenderKnotenIndex) ).getUserObject() ).incWert();
			this.trieBuilder(Arrays.copyOfRange(token, 1, token.length),
					(DefaultMutableTreeNode) rootnode
							.getChildAt(passenderKnotenIndex));
		}

	}

	/*
	 * $ abbabbab$ abbabbab$ abbabbab$ abbabbab$ abbabbab$ abbabbab$ abbabbab$
	 * abbabbab$ 12345678
	 */
}
