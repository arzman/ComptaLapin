package org.arthur.compta.lapin.presentation.trimestre.dialog;

import java.time.LocalDate;
import java.util.HashMap;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppExerciceMensuelLightId;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.trimestre.cellfactory.NumMoisCellComboFactory;
import org.arthur.compta.lapin.presentation.trimestre.cellfactory.TrimestreListCellFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * Dialog permettant de retourner un appId d'un excercice mensuel
 *
 */
public class SelectExerciceMensuelDialog extends ComptaDialog<AppExerciceMensuelLightId> {

	/**
	 * Liste des trimestres
	 */
	private ListView<String> _listV;

	/**
	 * Combo de sélection du mois
	 */
	private ComboBox<Integer> _moisCombo;

	public SelectExerciceMensuelDialog() {
		super(SelectExerciceMensuelDialog.class.getSimpleName());
		setTitle("Sélectionner un exercice mensuel");

		// Création des champ de saisi
		createContent();

		hookListeners();

		checkInput();
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
		ObservableList<String> _trimDdList = FXCollections.observableArrayList();

		// affichage de la liste
		_listV = new ListView<>();
		_listV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		try {
			HashMap<String, LocalDate> _resumeTrimestre = TrimestreManager.getInstance().getAllTrimestreShortList();
			_trimDdList.addAll(_resumeTrimestre.keySet());
			// callback permettant de customiser l'affichage
			_listV.setCellFactory(new TrimestreListCellFactory(_resumeTrimestre));

			_listV.setItems(_trimDdList);
		} catch (ComptaException e) {

			ExceptionDisplayService.showException(e);
		}

		grid.add(_listV, 0, 0);

		// affichage de la combo
		_moisCombo = new ComboBox<>();
		_moisCombo.setCellFactory(new NumMoisCellComboFactory());
		ObservableList<Integer> moisList = FXCollections.observableArrayList();
		moisList.add(0);
		moisList.add(1);
		moisList.add(2);
		_moisCombo.setItems(moisList);
		_moisCombo.getSelectionModel().select(0);

		grid.add(_moisCombo, 0, 1);

	}

	/**
	 * Crée les boutons OK et Cancel
	 */
	@Override
	protected void createButtonBar() {

		super.createButtonBar();

		// Création du bouton Cancel
		ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(buttonTypeCancel);

		// Retourne le Compte créé sur le OK
		setResultConverter(new Callback<ButtonType, AppExerciceMensuelLightId>() {

			@Override
			public AppExerciceMensuelLightId call(ButtonType param) {

				AppExerciceMensuelLightId zeReturn = null;

				// appuie sur Ok : on crée le trimestre
				if (param.getButtonData().equals(ButtonData.OK_DONE)) {

					String id = _listV.getSelectionModel().getSelectedItem();
					int num = _moisCombo.getSelectionModel().getSelectedItem();
					if (id != null && !id.isEmpty()) {

						try {
							zeReturn = new AppExerciceMensuelLightId(TrimestreManager.getInstance().getExerciceMensuelId(Integer.parseInt(id), num),
									Integer.parseInt(id), num);
						} catch (ComptaException e) {
							ExceptionDisplayService.showException(e);
						}

					}

				}

				return zeReturn;
			}
		});

	}

	/**
	 * Vérifie la saisie
	 * 
	 * @return
	 */
	private void checkInput() {

		boolean listKO = _listV.getSelectionModel().getSelectedItem() == null;
		if (listKO) {
			_listV.setBorder(BORDER_ERROR);
		} else {
			_listV.setBorder(null);
		}
		boolean comboKO = _moisCombo.getSelectionModel().getSelectedItem() == null;
		if (comboKO) {
			_moisCombo.setBorder(BORDER_ERROR);
		} else {
			_moisCombo.setBorder(null);
		}

		if (_buttonTypeOk != null) {
			Node OkButton = getDialogPane().lookupButton(_buttonTypeOk);
			OkButton.setDisable(listKO || comboKO);
		}

	}

	/**
	 * Affecte des écouteurs de modification sur les champs de saisie. Ces
	 * écouteurs déclenchent la vérification de la saisie
	 */
	private void hookListeners() {

		// num Mois
		_moisCombo.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});
		// selection du trimestre
		_listV.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});

	}

}
