package org.arthur.compta.lapin.presentation.budget.dialog;

import org.arthur.compta.lapin.application.manager.BudgetManager;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * Fenetre permettant d'utiliser un budget
 */
public class UseBudgetDialog extends ComptaDialog<ButtonData> {

	/** Le budget */
	private AppBudget _budget;


	/** champ de saisie du montant */
	private TextField _montantTxt;
	/** champ de saisie du libelle */
	private TextField _nomTxt;
	/** champ de saisie de la date */
	private DatePicker _datePck;

	/**
	 * Le constructeur
	 * 
	 * @param appB
	 */
	public UseBudgetDialog(AppBudget appB) {

		super(UseBudgetDialog.class.getSimpleName());
		_budget = appB;

		setTitle("Utiliser le budget");

		GridPane root = new GridPane();
		root.setHgap(5);
		root.setVgap(5);
		getDialogPane().setContent(root);

		// saisie du libellé
		Label nomLbl = new Label("Libellé : ");
		root.add(nomLbl, 0, 0);
		_nomTxt = new TextField();
		root.add(_nomTxt, 1, 0);

		// saisie du montant
		Label montantLbl = new Label("Montant : ");
		root.add(montantLbl, 0, 1);
		_montantTxt = new TextField();
		root.add(_montantTxt, 1, 1);

		// saisie de la date
		Label dateLbl = new Label("Date : ");
		root.add(dateLbl, 0, 2);
		_datePck = new DatePicker();
		root.add(_datePck, 1, 2);

		// ajout de l'écoute sur la modif des entrées
		hookListener();

		// vérif initiale
		checkInput();

		setResultConverter(new Callback<ButtonType, ButtonData>() {

			@Override
			public ButtonData call(ButtonType param) {

				if (param == _buttonTypeOk) {

					try {
						BudgetManager.getInstance().addUtilisationForBudget(_budget, _nomTxt.getText().trim(),
								Double.parseDouble(_montantTxt.getText().trim()), _datePck.getValue());
					} catch (Exception e) {
						ExceptionDisplayService.showException(e);
					}
				}

				return param.getButtonData();
			}
		});

	}

	/**
	 * Attache des listeners aux champs de saisies
	 */
	private void hookListener() {

		_nomTxt.textProperty().addListener((observable, oldValue, newValue) -> checkInput());
		_montantTxt.textProperty().addListener((observable, oldValue, newValue) -> checkInput());
		_datePck.valueProperty().addListener((observable, oldValue, newValue) -> checkInput());

	}

	/**
	 * Vérifie les entrées
	 */
	private void checkInput() {

		// vérif du nom...ne doit pas être vide et doit contenir des caractere
		// alphanum
		boolean nomError = true;
		if (!_nomTxt.getText().trim().isEmpty()) {

			_nomTxt.setBorder(null);
			nomError = false;

		} else {
			_nomTxt.setBorder(BORDER_ERROR);
			nomError = true;
		}

		// Vérif du montant
		boolean montantError = true;
		if (!_montantTxt.getText().trim().isEmpty()) {

			try {
				Double.parseDouble(_montantTxt.getText().trim());
				_montantTxt.setBorder(null);
				montantError = false;
			} catch (NumberFormatException e) {
				_montantTxt.setBorder(BORDER_ERROR);
				montantError = true;
			}

		}

		// vérif de la date
		boolean dateError = true;
		if (_datePck.getValue() != null) {
			dateError = false;
			_datePck.setBorder(null);
		} else {
			dateError = true;
			_datePck.setBorder(BORDER_ERROR);
		}

		getDialogPane().lookupButton(_buttonTypeOk).setDisable(nomError || montantError || dateError);

	}

	/**
	 * Création des boutons
	 */
	protected void createButtonBar() {
		// bouton close
		ButtonType close = new ButtonType("Fermer", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(close);

	}

}
