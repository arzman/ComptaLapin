package org.arthur.compta.lapin.presentation.trimestre.dialog;

import java.sql.Date;
import java.util.Calendar;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppTrimestre;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class CreateTrimestreDialog extends Dialog<AppTrimestre> {

	/**
	 * Bouton ok
	 */
	private ButtonType _buttonTypeOk;
	/**
	 * Bouton ok et charger le trimestre
	 */
	private ButtonType _buttonTypeOkAndLoad;
	/**
	 * Saisie de la date de début
	 */
	private DatePicker _dpick;

	/**
	 * La bordure rouge en cas d'erreur de saisie
	 */
	private final Border BORDER_ERROR = new Border(
			new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1)));

	public CreateTrimestreDialog() {

		setTitle("Création d'un trimestre");

		// Création des champ de saisi
		createContent();
		// création des boutons de control
		createBoutonBar();
		// verification de la saisie
		checkInput();

	}

	/**
	 * Création des champs de saisie
	 */
	private void createContent() {
		GridPane grid = new GridPane();
		getDialogPane().setContent(grid);

		// saisie de la date
		Label dateDebutLbl = new Label("Date de début : ");
		grid.add(dateDebutLbl, 0, 0);

		_dpick = new DatePicker();
		_dpick.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});
		grid.add(_dpick, 1, 0);

	}

	/**
	 * Crée les boutons OK et Cancel
	 */
	private void createBoutonBar() {

		// Création du bouton OK
		_buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().add(_buttonTypeOk);

		// Création du bouton OK
		_buttonTypeOkAndLoad = new ButtonType("Ok et charger", ButtonData.APPLY);
		getDialogPane().getButtonTypes().add(_buttonTypeOkAndLoad);

		// Création du bouton Cancel
		ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(buttonTypeCancel);

		// Retourne le Compte créé sur le OK
		setResultConverter(new Callback<ButtonType, AppTrimestre>() {

			@Override
			public AppTrimestre call(ButtonType param) {

				AppTrimestre zeReturn = null;

				// appuie sur Ok : on crée le trimestre
				if (param.getButtonData().equals(ButtonData.OK_DONE)) {

					try {
						// recup de la date de début
						Calendar deb = Calendar.getInstance();
						deb.setTime(Date.valueOf(_dpick.getValue()));
						// création
						zeReturn = TrimestreManager.getInstance().createTrimestre(deb);

					} catch (ComptaException e) {
						ExceptionDisplayService.showException(e);

					}

				}

				return zeReturn;
			}
		});

	}

	/**
	 * Vérifie la saisie
	 */
	private void checkInput() {

		// Vérif de la date
		boolean dateError = true;
		if (_dpick.getValue() != null) {

			_dpick.setBorder(null);
			dateError = false;
		} else {
			_dpick.setBorder(BORDER_ERROR);
			dateError = true;
		}

		// désactivation des boutons OK
		getDialogPane().lookupButton(_buttonTypeOk).setDisable(dateError);
		getDialogPane().lookupButton(_buttonTypeOkAndLoad).setDisable(dateError);

	}

}
