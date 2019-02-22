package org.arthur.compta.lapin.presentation.trimestre.dialog;

import java.util.Calendar;
import java.util.HashMap;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.trimestre.cellfactory.TrimestreListCellFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class SelectMoisInTrimestreDialog extends ComptaDialog<String> {

	/**
	 * Bouton ok
	 */
	private ButtonType _buttonTypeOk;

	private ObservableList<String> _trimDdList;

	private ListView<String> _listV;

	public SelectMoisInTrimestreDialog() {
		super(SelectMoisInTrimestreDialog.class.getSimpleName());
		setTitle("Création d'un trimestre");

		// Création des champ de saisi
		createContent();
		// création des boutons de control
		createBoutonBar();

	}

	/**
	 * Création des champs de saisie
	 */
	private void createContent() {
		GridPane grid = new GridPane();
		getDialogPane().setContent(grid);

		// récupération des trimestres de l'application ainsi que leur date
		// de début

		// Création de la liste des trimestres à afficher
		_trimDdList = FXCollections.observableArrayList();

		// affichage de la liste
		_listV = new ListView<>();
		_listV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		try {
			HashMap<String, Calendar> _resumeTrimestre = TrimestreManager.getInstance().getAllTrimestreShortList();
			_trimDdList.addAll(_resumeTrimestre.keySet());
			// callback permettant de customiser l'affichage
			_listV.setCellFactory(new TrimestreListCellFactory(_resumeTrimestre));

			_listV.setItems(_trimDdList);
		} catch (ComptaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		grid.add(_listV, 0, 0);

	}

	/**
	 * Crée les boutons OK et Cancel
	 */
	private void createBoutonBar() {

		// Création du bouton OK
		_buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
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

					String id = _listV.getSelectionModel().getSelectedItem();
					if (id != null && !id.isEmpty()) {

						zeReturn = TrimestreManager.getInstance().getExerciceMensuelId(id, 1);

					}

				}

				return zeReturn;
			}
		});

	}

}
