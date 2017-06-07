package org.arthur.compta.lapin.presentation.scene;

import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.presentation.budget.pane.BudgetPane;
import org.arthur.compta.lapin.presentation.compte.pane.ComptePane;
import org.arthur.compta.lapin.presentation.menu.ComptaMenuBar;
import org.arthur.compta.lapin.presentation.trimestre.pane.TrimestreCourantPane;

import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * Unique Scène de l'IHM, elle contiendra les éléments graphiques
 *
 */
public class MainScene extends Scene {

	/** Séparation haut/bas (à droite) */
	private SplitPane splitHautBas;
	/** Séparation gauche/droite*/
	private SplitPane splitgaucheDroite;

	/**
	 * Constructeur par défaut
	 */
	public MainScene() {
		// appel du constructeur parent
		super(new BorderPane());

		// ajout de la barre de menu
		((BorderPane) getRoot()).setTop(new ComptaMenuBar(this));

		// panneau principal de l'appli
		StackPane stack = new StackPane();
		((BorderPane) getRoot()).setCenter(stack);

		// ajout d'une séparation gauche-droite de la fenêtre
		splitgaucheDroite = new SplitPane();
		splitgaucheDroite.setDividerPosition(0,
				Double.parseDouble(ConfigurationManager.getInstance().getProp("MainScene.splitgaucheDroite.pos", "0.5")));
		splitgaucheDroite.setOrientation(Orientation.HORIZONTAL);
		stack.getChildren().add(splitgaucheDroite);

		// Ajout à gauche de l'affichage du trimestre courant
		splitgaucheDroite.getItems().add(new TrimestreCourantPane());

		// séparation haut-bas de la partie droite
		splitHautBas = new SplitPane();
		splitHautBas.setOrientation(Orientation.VERTICAL);
		splitHautBas.setDividerPosition(0,
				Double.parseDouble(ConfigurationManager.getInstance().getProp("MainScene.splitHautBas.pos", "0.5")));
		splitgaucheDroite.getItems().add(splitHautBas);

		// Ajout de l'affichage des budgets
		splitHautBas.getItems().add(new BudgetPane());

		// Ajout de l'affichage des comptes
		splitHautBas.getItems().add(new ComptePane());

	}

	/**
	 * Sauvegarde la position des séparations
	 */
	public void saveSplitPosition() {
		ConfigurationManager.getInstance().setProp("MainScene.splitHautBas.pos",
				String.valueOf(splitHautBas.getDividerPositions()[0]));
		
		ConfigurationManager.getInstance().setProp("MainScene.splitgaucheDroite.pos",
				String.valueOf(splitgaucheDroite.getDividerPositions()[0]));

	}

}
