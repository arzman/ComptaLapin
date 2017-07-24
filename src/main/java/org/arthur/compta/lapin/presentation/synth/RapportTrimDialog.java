package org.arthur.compta.lapin.presentation.synth;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.service.SyntheseService;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.trimestre.cellfactory.TrimestreListCellFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

public class RapportTrimDialog extends ComptaDialog<ButtonData> {

	/**
	 * Les id des trimestre à afficher ainsi que leur date de début.
	 */
	private ObservableList<String> _trimDdList;
	/**
	 * Affichage de la liste des trimestres
	 */
	private ListView<String> _listV;

	public RapportTrimDialog() {
		super(RapportTrimDialog.class.getSimpleName());

		setTitle("Rapport Trimestriel");

		createContent();

		// création des boutons
		createBoutonBar();

	}

	/**
	 * Création du contenu de la fenêtre
	 */
	private void createContent() {

		// noeud racine
		GridPane root = new GridPane();
		getDialogPane().setContent(root);

		try {
			// récupération des trimestres de l'application ainsi que leur date
			// de début
			HashMap<String, Calendar> _resumeTrimestre = TrimestreManager.getInstance().getAllTrimestreShortList();

			// Création de la liste des trimestres à afficher
			_trimDdList = FXCollections.observableArrayList();
			_trimDdList.addAll(_resumeTrimestre.keySet());

			// affichage de la liste
			_listV = new ListView<>();
			_listV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			// callback permettant de customiser l'affichage
			_listV.setCellFactory(new TrimestreListCellFactory(_resumeTrimestre));

			_listV.setItems(_trimDdList);
			root.add(_listV, 0, 0);

		} catch (ComptaException e) {
			ExceptionDisplayService.showException(e);
		}

	}

	/**
	 * Crée les boutons OK et Cancel
	 */
	private void createBoutonBar() {

		// Création du bouton Fermer
		getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

		// Création du bouton Exporter
		ButtonType buttonTypeCancel = new ButtonType("Exporter", ButtonData.APPLY);
		getDialogPane().getButtonTypes().add(buttonTypeCancel);

		// Retourne le Compte créé sur le OK
		setResultConverter(new Callback<ButtonType, ButtonData>() {

			@Override
			public ButtonData call(ButtonType param) {

				ButtonData zeReturn = param.getButtonData();

				// appuie sur Ok : on crée le trimestre
				if (param.getButtonData().equals(ButtonData.APPLY)) {

					// recup du trimestre
					String idTrim = _listV.getSelectionModel().getSelectedItem();

					FileChooser fc = new FileChooser();
					ExtensionFilter extFilter = new ExtensionFilter("PDF files (*.pdf)", "*.pdf");
					fc.getExtensionFilters().add(extFilter);
					File file = fc.showSaveDialog(getOwner());

					if (file != null) {

						SyntheseService.writeRapportForTrim(idTrim, file);

					}

				}

				return zeReturn;
			}
		});
	}

}
