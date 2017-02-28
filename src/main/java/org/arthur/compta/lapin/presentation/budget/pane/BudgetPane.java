package org.arthur.compta.lapin.presentation.budget.pane;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Panneau d'affichage des bugets
 *
 */
public class BudgetPane extends GridPane {

	/**
	 * Constructeur par défaut
	 */
	public BudgetPane() {
		super();

		// Création du contenu
		Label txt = new Label("Panneau des budgets");
		add(txt, 0, 0);
	}

}
