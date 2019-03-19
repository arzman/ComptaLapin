package org.arthur.compta.lapin.presentation.compte.dialog;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;

import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * Fenêtre d'édition d'un compte
 *
 */
public class EditCompteDialog extends ComptaDialog<AppCompte> {

	/** Le champ de saisi du nom */
	private TextField _nomTxt;
	/** Le champ de saisi du solde */
	private TextField _soldeTxt;
	/** Checkbox pour la saisie de : isLivret */
	private CheckBox _livretCheck;
	/** Checkbox pour la saisie de : isBudget */
	private CheckBox _budgetCheck;

	/** Id application du compte a éditié ( vide si création) */
	private AppCompte _appCompte;

	public EditCompteDialog(AppCompte appCompte) {

		super(EditCompteDialog.class.getSimpleName());

		_appCompte = appCompte;

		if (_appCompte == null) {
			setTitle("Création d'un compte");
		} else {
			setTitle("Edition d'un compte");
		}

		// Création des champ de saisi
		createContent();

		// verification de la saisie
		checkInput();

		// Retourne le Compte créé sur le OK
		setResultConverter(new Callback<ButtonType, AppCompte>() {

			@Override
			public AppCompte call(ButtonType param) {

				AppCompte zeReturn = null;

				if (param.equals(_buttonTypeOk)) {

					try {

						if (_appCompte != null) {
							// édition
							CompteManager.getInstance().editCompte(_appCompte, _nomTxt.getText().trim(), Double.parseDouble(_soldeTxt.getText().trim()),
									_livretCheck.isSelected(), _budgetCheck.isSelected());
							zeReturn = _appCompte;

						} else {
							// création
							zeReturn = CompteManager.getInstance().addCompte(_nomTxt.getText().trim(), Double.parseDouble(_soldeTxt.getText().trim()),
									_livretCheck.isSelected(), _budgetCheck.isSelected());
						}

					} catch (ComptaException e) {
						ExceptionDisplayService.showException(e);

					}

				}

				return zeReturn;
			}
		});

	}

	/**
	 * Création du contenu de la fenêtre
	 */
	private void createContent() {

		GridPane grid = new GridPane();
		getDialogPane().setContent(grid);

		// saisie du nom
		Label nomLbl = new Label("Nom : ");
		nomLbl.setTooltip(new Tooltip("le nom du compte. Caractère alphanumérique et espace"));
		_nomTxt = new TextField();
		// pré-remplissage pour l'édition
		if (_appCompte != null) {
			_nomTxt.setText(_appCompte.getNom());
		}
		// Validation sur saisie
		_nomTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});

		grid.add(nomLbl, 0, 0);
		grid.add(_nomTxt, 1, 0);

		// saisie du solde
		Label soldeLbl = new Label("Solde : ");
		_soldeTxt = new TextField();
		// pré-remplissage pour l'édition
		if (_appCompte != null) {
			_soldeTxt.setText(String.valueOf(_appCompte.getSolde()));
		}
		// Validation sur saisie
		_soldeTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});
		grid.add(soldeLbl, 0, 1);
		grid.add(_soldeTxt, 1, 1);

		// saisie de estLivret
		Label livretLbl = new Label("Est un livret : ");
		_livretCheck = new CheckBox();
		if (_appCompte != null) {
			_livretCheck.setSelected(_appCompte.isLivret());
		}
		grid.add(livretLbl, 0, 2);
		grid.add(_livretCheck, 1, 2);

		// saisie de estBudget
		Label budgetLbl = new Label("Budget : ");
		_budgetCheck = new CheckBox();
		if (_appCompte != null) {
			_budgetCheck.setSelected(_appCompte.isBudget());
		}
		grid.add(budgetLbl, 0, 3);
		grid.add(_budgetCheck, 1, 3);

	}

	/**
	 * Crée les boutons OK et Cancel
	 */
	protected void createButtonBar() {

		super.createButtonBar();

		// Création du bouton Cancel
		ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(buttonTypeCancel);

	}

	/**
	 * Vérifie la validité de la saisie
	 */
	private void checkInput() {

		// Vérif du nom
		boolean nomError = true;
		if (!_nomTxt.getText().trim().isEmpty()) {

			_nomTxt.setBorder(null);
			nomError = false;

		} else {
			_nomTxt.setBorder(BORDER_ERROR);
			nomError = true;
		}

		// Vérif du solde
		boolean soldeError = true;
		if (!_soldeTxt.getText().trim().isEmpty()) {

			try {
				Double.parseDouble(_soldeTxt.getText().trim());
				_soldeTxt.setBorder(null);
				soldeError = false;
			} catch (NumberFormatException e) {
				_soldeTxt.setBorder(BORDER_ERROR);
				soldeError = true;
			}

		}

		Node OkButton = getDialogPane().lookupButton(_buttonTypeOk);
		OkButton.setDisable(nomError || soldeError);

	}

}
