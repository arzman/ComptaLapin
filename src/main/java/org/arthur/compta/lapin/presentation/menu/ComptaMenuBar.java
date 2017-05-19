package org.arthur.compta.lapin.presentation.menu;

import org.arthur.compta.lapin.presentation.compte.dialog.EditCompteDialog;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;
import org.arthur.compta.lapin.presentation.trimestre.dialog.CreateTrimestreDialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

/**
 * Barre de menu de l'application
 *
 */
public class ComptaMenuBar extends MenuBar {

	public ComptaMenuBar() {
		super();

		// menu Trimestre
		createTrimestreMenu();
		// menu Compte
		createCompteMenu();
	}

	/**
	 * Crée le menu associé aux trimestres
	 */
	private void createTrimestreMenu() {

		Menu trimMenu = new Menu("Trimestre");

		MenuItem addItem = new MenuItem("Créer un trimestre");
		addItem.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.ADD_IMG)));
		addItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// ouverture de la fenêtre de saisie
				CreateTrimestreDialog dia = new CreateTrimestreDialog();
				dia.showAndWait();

			}
		});

		trimMenu.getItems().add(addItem);
		getMenus().add(trimMenu);
	}

	/**
	 * Crée le menu associé aux comptes
	 */
	private void createCompteMenu() {

		Menu compteMenu = new Menu("Compte");

		MenuItem addItem = new MenuItem("Créer un compte");
		addItem.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.ADD_IMG)));
		addItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// ouverture de la fenêtre de saisie
				EditCompteDialog dia = new EditCompteDialog(null);
				dia.showAndWait();

			}
		});

		compteMenu.getItems().add(addItem);
		getMenus().add(compteMenu);

	}

}
