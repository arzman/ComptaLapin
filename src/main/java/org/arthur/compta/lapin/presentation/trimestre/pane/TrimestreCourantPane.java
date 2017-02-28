package org.arthur.compta.lapin.presentation.trimestre.pane;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;


/**
 * Panneau d'affichage du trimestre courant
 *
 */
public class TrimestreCourantPane extends GridPane {

	/**
	 * Constructeur par défaut
	 */
	public TrimestreCourantPane() {
		
		super();
		
		//Création du contenu
		Label txt = new Label("Panneau du trimestre courant");
		add(txt, 0, 0);

	}

}
