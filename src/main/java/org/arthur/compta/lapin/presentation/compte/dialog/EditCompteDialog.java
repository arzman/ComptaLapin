package org.arthur.compta.lapin.presentation.compte.dialog;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;

import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * Fenêtre d'édition d'un compte
 *
 */
public class EditCompteDialog extends Dialog<AppCompte> {

	/**
	 * Le champ de saisi du nom
	 */
	private TextField _nomTxt;
	/**
	 * Le champ de saisi du solde
	 */
	private TextField _soldeTxt;
	/**
	 * Checkbox pour la saisie de : isLivret
	 */
	private CheckBox _livretCheck;
	/**
	 * Checkbox pour la saisie de : isBudget
	 */
	private CheckBox _budgetCheck;

	/**
	 * Le bouton OK
	 */
	private ButtonType _buttonTypeOk;

	/**
	 * Id application du compte a éditié ( vide si création)
	 */
	private AppCompte _appCompte;

	private final Border BORDER_ERROR = new Border(
			new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1)));

	public EditCompteDialog(AppCompte appCompte) {

		_appCompte = appCompte;

		if (_appCompte == null) {
			setTitle("Création d'un compte");
		} else {
			setTitle("Edition d'un compte");
		}

		// Création des champ de saisi
		createContent(appCompte);

		// Création de la barre des boutons
		createBoutonBar();

		// verification de la saisie
		checkInput();

	}

	/**
	 * Création du contenu de la fenêtre
	 */
	private void createContent(AppCompte appCompte) {

		GridPane grid = new GridPane();
		getDialogPane().setContent(grid);

		// saisie du nom
		Label nomLbl = new Label("Nom : ");
		nomLbl.setTooltip(new Tooltip("le nom du compte. Caractère alphanumérique et espace"));
		_nomTxt = new TextField();
		// pré-remplissage pour l'édition
		if (_appCompte != null) {
			_nomTxt.setText(appCompte.getNom());
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
			_soldeTxt.setText(String.valueOf(appCompte.getSolde()));
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
			_livretCheck.setSelected(appCompte.getIsLivretProp());
		}
		grid.add(livretLbl, 0, 2);
		grid.add(_livretCheck, 1, 2);

		// saisie de estBudget
		Label budgetLbl = new Label("Budget : ");
		_budgetCheck = new CheckBox();
		if (_appCompte != null) {
			_budgetCheck.setSelected(appCompte.getIsBudget());
		}
		grid.add(budgetLbl, 0, 3);
		grid.add(_budgetCheck, 1, 3);

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
		setResultConverter(new Callback<ButtonType, AppCompte>() {

			@Override
			public AppCompte call(ButtonType param) {

				AppCompte zeReturn = null;

				if (param.getButtonData().equals(ButtonData.OK_DONE)) {

					try {

						if (_appCompte != null) {
							// édition
							zeReturn = CompteManager.getInstance().updateCompte(_appCompte, _nomTxt.getText().trim(),
									Double.parseDouble(_soldeTxt.getText().trim()), _livretCheck.isSelected(),
									_budgetCheck.isSelected());

						} else {
							// création
							zeReturn = CompteManager.getInstance().createCompte(_nomTxt.getText().trim(),
									Double.parseDouble(_soldeTxt.getText().trim()), _livretCheck.isSelected(),
									_budgetCheck.isSelected());
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
	 * Vérifie la validité de la saisie
	 */
	private void checkInput() {

		// Vérif du nom
		boolean nomError = true;
		if (!_nomTxt.getText().trim().isEmpty()) {

			if (_nomTxt.getText().matches("[a-zA-Z123456789 ]+")) {
				_nomTxt.setBorder(null);
				nomError = false;
			} else {
				_nomTxt.setBorder(BORDER_ERROR);
				nomError = true;
			}

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
