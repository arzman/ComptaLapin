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
 * @author Arthur
 *
 */
public class MainScene extends Scene {

	/**
	 * Constructeur par défaut
	 */
	public MainScene() {
		// appel du constructeur parent
		super(new BorderPane());

		//ajout de la barre de menu
		((BorderPane) getRoot()).setTop(new ComptaMenuBar());

		//panneau principal de l'appli
		StackPane stack = new StackPane();
		((BorderPane) getRoot()).setCenter(stack);
		
		// ajout d'une séparation gauche-droite de la fenêtre
		SplitPane splitgaucheDroite = new SplitPane();
		splitgaucheDroite.setDividerPosition(0, ConfigurationManager.getInstance().getgdPosition());
		splitgaucheDroite.setOrientation(Orientation.HORIZONTAL);
		stack.getChildren().add(splitgaucheDroite);

		// Ajout à gauche de l'affichage du trimestre courant
		splitgaucheDroite.getItems().add(new TrimestreCourantPane());

		// séparation haut-bas de la partie droite
		SplitPane splitHautBas = new SplitPane();
		splitHautBas.setOrientation(Orientation.VERTICAL);
		splitHautBas.setDividerPosition(0, ConfigurationManager.getInstance().gethbPosition());
		splitgaucheDroite.getItems().add(splitHautBas);

		// Ajout de l'affichage des budgets
		splitHautBas.getItems().add(new BudgetPane());

		// Ajout de l'affichage des comptes
		splitHautBas.getItems().add(new ComptePane());
		
		
		// enregistrement de la position
		splitgaucheDroite.getDividers().get(0).positionProperty().addListener( (observable, oldValue, newValue) -> ConfigurationManager.getInstance().setgdPosition(newValue));
		splitHautBas.getDividers().get(0).positionProperty().addListener( (observable, oldValue, newValue) -> ConfigurationManager.getInstance().sethbPosition(newValue));
		
	}
	
	
	
	
	
	

}
