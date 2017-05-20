package org.arthur.compta.lapin.presentation.menu;

import java.util.Optional;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.presentation.compte.dialog.EditCompteDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;
import org.arthur.compta.lapin.presentation.trimestre.dialog.CreateTrimestreDialog;
import org.arthur.compta.lapin.presentation.trimestre.dialog.SelectTrimestreDialog;

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

		// création du menu Trimestre
		Menu trimMenu = new Menu("Trimestre");

		// ajout de l'action créé trimestre
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

		// ajout de l'action de sélection du trimestre courant
		MenuItem selectItem = new MenuItem("Sélectionner trimestre");
		selectItem.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.SELECT_TRIM_IMG)));
		selectItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// ouverture de la fenêtre de saisie
				SelectTrimestreDialog dia = new SelectTrimestreDialog();
				Optional<String> id = dia.showAndWait();

				if (id.isPresent() && !id.get().isEmpty()) {
					try {
						// si un trimestre est choisi on ordonne le changement
						TrimestreManager.getInstance().loadTrimestreCourant(id.get());
					} catch (ComptaException e) {
						ExceptionDisplayService.showException(e);
					}
				}

			}
		});

		trimMenu.getItems().add(selectItem);

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
