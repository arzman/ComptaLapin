package org.arthur.compta.lapin.presentation.trimestre.dialog;

import java.util.Calendar;
import java.util.HashMap;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.trimestre.cellfactory.TrimestreListCellFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * Fenêtre de sélection d'un trimestre : affiche la date de début/fin du
 * trimestre
 *
 */
public class SelectTrimestreDialog extends Dialog<String> {

	/**
	 * Les id des trimestre à afficher ainsi que leur date de début.
	 */
	private HashMap<String, Calendar> _resumeTrimestre;
	/**
	 * Affichage de la liste des trimestres
	 */
	private ListView<String> _listV;

	public SelectTrimestreDialog() {

		setTitle("Sélection du trimestre courant");

		// création de la zone de sélection
		GridPane content = new GridPane();
		getDialogPane().setContent(content);

		try {
			// récupération des trimestres de l'application ainsi que leur date
			// de début
			_resumeTrimestre = TrimestreManager.getInstance().getAllTrimestreShortList();

			// Création de la liste des trimestres à afficher
			ObservableList<String> trimidlist = FXCollections.observableArrayList();
			trimidlist.addAll(_resumeTrimestre.keySet());

			// affichage de la liste
			_listV = new ListView<>();
			_listV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			// callback permettant de customiser l'affichage
			_listV.setCellFactory(new TrimestreListCellFactory(_resumeTrimestre));

			_listV.setItems(trimidlist);
			content.add(_listV, 0, 0);

		} catch (ComptaException e) {
			ExceptionDisplayService.showException(e);
		}

		// création des boutons
		createBoutonBar();

	}

	/**
	 * Crée les boutons OK et Cancel
	 */
	private void createBoutonBar() {

		// Création du bouton OK
		ButtonType _buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().add(_buttonTypeOk);

		// Création du bouton Cancel
		ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(buttonTypeCancel);

		// Retourne le Compte créé sur le OK
		setResultConverter(new Callback<ButtonType, String>() {

			@Override
			public String call(ButtonType param) {

				String zeReturn = null;

				// appuie sur Ok : on crée le trimestre
				if (param.getButtonData().equals(ButtonData.OK_DONE)) {

					// recup du trimestre
					zeReturn = _listV.getSelectionModel().getSelectedItem();

				}

				return zeReturn;
			}
		});
	}

}
