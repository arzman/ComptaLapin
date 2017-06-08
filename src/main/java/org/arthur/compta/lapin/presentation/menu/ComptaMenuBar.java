package org.arthur.compta.lapin.presentation.menu;

import java.util.Calendar;
import java.util.Optional;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.service.ComptaService;
import org.arthur.compta.lapin.presentation.common.dialog.DateDialog;
import org.arthur.compta.lapin.presentation.compte.dialog.EditCompteDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;
import org.arthur.compta.lapin.presentation.scene.MainScene;
import org.arthur.compta.lapin.presentation.trimestre.dialog.ConfigureTemplateDialog;
import org.arthur.compta.lapin.presentation.trimestre.dialog.CreateTrimestreDialog;
import org.arthur.compta.lapin.presentation.trimestre.dialog.ManageTrimestreCourantDialog;

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

	private MainScene _scene;

	public ComptaMenuBar(MainScene mainScene) {
		super();

		_scene = mainScene;

		// menu Système
		createSystemMenu();
		// menu Trimestre
		createTrimestreMenu();
		// menu Compte
		createCompteMenu();
		// item date derniere vérif
		createDerVerifItem();
	}

	private void createSystemMenu() {

		// création du menu Trimestre
		Menu sysMenu = new Menu("Système");

		// ajout de l'action créé trimestre
		MenuItem prefItem = new MenuItem("Sauver taille");
		prefItem.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.PREF_IMG)));
		prefItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// ouverture de la fenêtre de saisie
				_scene.saveSplitPosition();

			}
		});
		sysMenu.getItems().add(prefItem);
		getMenus().add(sysMenu);

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
				ManageTrimestreCourantDialog dia = new ManageTrimestreCourantDialog();
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

		// ajout de l'action de configuration du template de trimestre
		MenuItem configItem = new MenuItem("Configurer modèle");
		configItem.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.CONFIG_TMP_IMG)));
		configItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// ouverture de la fenêtre de saisie
				ConfigureTemplateDialog dia = new ConfigureTemplateDialog();
				dia.showAndWait();

			}
		});
		trimMenu.getItems().add(configItem);

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

	private void createDerVerifItem() {

		try {
			String dat = ComptaService.getDateDerVerif();

			Menu datMenu = new Menu();
			datMenu.setText("Vérif : " + dat);

			// vérifier tout de suite
			MenuItem verifNow = new MenuItem("Vérifier");
			verifNow.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.VALID_IMG)));
			verifNow.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {

					try {
						// positionne la date de derniere verification à la date
						// courant
						ComptaService.setDateDerVerif(Calendar.getInstance());
						datMenu.setText("Vérif : " + ComptaService.getDateDerVerif());
					} catch (ComptaException e) {
						ExceptionDisplayService.showException(e);
					}

				}
			});
			datMenu.getItems().add(verifNow);

			// changement de la date de derniere verif
			MenuItem modVerif = new MenuItem("Changer la date");
			modVerif.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.CALENDRIER_IMG)));
			modVerif.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {

					try {
						// ouverture de la fenetre de choix de date
						DateDialog dia = new DateDialog(null);
						// récupération du choix
						Optional<Calendar> res = dia.showAndWait();

						if (res.isPresent()) {
							// positionnement de la nouvelle date
							ComptaService.setDateDerVerif(res.get());
							datMenu.setText("Vérif : " + ComptaService.getDateDerVerif());

						}

					} catch (ComptaException e) {
						ExceptionDisplayService.showException(e);
					}

				}
			});
			datMenu.getItems().add(modVerif);

			getMenus().add(datMenu);

		} catch (ComptaException e) {
			ExceptionDisplayService.showException(e);
		}

	}

}
