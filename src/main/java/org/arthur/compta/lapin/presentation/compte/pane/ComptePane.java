package org.arthur.compta.lapin.presentation.compte.pane;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Panneau d'affichage des comptes
 *
 */
public class ComptePane extends GridPane {

	public ComptePane() {
		
		
		// Création du contenu
		Label txt = new Label("Panneau des comptes");
		add(txt, 0, 0);
	}

}
