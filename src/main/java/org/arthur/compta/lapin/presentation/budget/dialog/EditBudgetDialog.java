package org.arthur.compta.lapin.presentation.budget.dialog;

import org.arthur.compta.lapin.application.manager.BudgetManager;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;

import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
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
 * Fenetre de création ou édition de budget
 *
 */
public class EditBudgetDialog extends Dialog<AppBudget> {

	/** Le budget créé ou édité */
	private AppBudget _budget;

	/** Le champ de saisi du nom */
	private TextField _nomTxt;
	/** Le champ de saisi de l'objectif */
	private TextField _objTxt;
	/** Le champ de saisi du montant utilisé */
	private TextField _utilsTxt;

	/** Le bouton OK */
	private ButtonType _buttonTypeOk;

	/** La bordure rouge en cas d'erreur de saisi */
	private final Border BORDER_ERROR = new Border(
			new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1)));

	/**
	 * Constructeur
	 * 
	 * @param budget
	 *            le budget a éditer, null si création
	 */
	public EditBudgetDialog(AppBudget budget) {

		_budget = budget;
		if (_budget == null) {
			setTitle("Création d'un budget");
		} else {
			setTitle("Edition d'un budget");
		}

		// création des zones de saisies
		createContent();
		// initialisation des valeurs
		initValues();

		// création des boutons
		createButtonBar();
		// attachement des écouteurs pour la validation des modifs
		hookListener();

		// crée ou édite l'élement de template après appuis sur Ok
		setResultConverter(new Callback<ButtonType, AppBudget>() {

			@Override
			public AppBudget call(ButtonType param) {

				// appuis sur ok
				if (param.equals(_buttonTypeOk)) {

					// création
					if (_budget == null) {

						try {
							_budget = BudgetManager.getInstance().addBudget(_nomTxt.getText().trim(),
									Double.parseDouble(_objTxt.getText().trim()),
									Double.parseDouble(_utilsTxt.getText().trim()));
						} catch (Exception e) {
							ExceptionDisplayService.showException(e);
						}

					} else {
						// édition
						try {
							_budget = BudgetManager.getInstance().editBudget(_budget, _nomTxt.getText().trim(),
									Double.parseDouble(_objTxt.getText().trim()),
									Double.parseDouble(_utilsTxt.getText().trim()), _budget.isActif());
						} catch (Exception e) {
							ExceptionDisplayService.showException(e);
						}
					}

				}

				return _budget;
			}
		});

	}

	/**
	 * Création des zones de saisies
	 */
	private void createContent() {

		// configuration du layout
		GridPane root = new GridPane();
		getDialogPane().setContent(root);
		root.setVgap(2.0);

		// saisie du nom
		Label nomLbl = new Label("Nom : ");
		root.add(nomLbl, 0, 0);
		nomLbl.setTooltip(new Tooltip("Le nom du budget. Caractère alphanumérique et espace"));
		_nomTxt = new TextField();
		root.add(_nomTxt, 1, 0);

		// saisie de l'objectif
		Label objLbl = new Label("Objectif : ");
		root.add(objLbl, 0, 1);
		nomLbl.setTooltip(new Tooltip("L'objectif à atteindre. Nombre réel"));
		_objTxt = new TextField();
		root.add(_objTxt, 1, 1);

		// saisie du montant utilisé
		Label utilLbl = new Label("Utilisé : ");
		root.add(utilLbl, 0, 2);
		nomLbl.setTooltip(new Tooltip("Le montant déjà utilisé. Nombre réel"));
		_utilsTxt = new TextField();
		root.add(_utilsTxt, 1, 2);

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

		// Vérif de l'objectif
		boolean objError = true;
		if (!_objTxt.getText().trim().isEmpty()) {

			try {
				Double.parseDouble(_objTxt.getText().trim());
				_objTxt.setBorder(null);
				objError = false;
			} catch (NumberFormatException e) {
				_objTxt.setBorder(BORDER_ERROR);
				objError = true;
			}

		}

		// Vérif du montant utilisé
		boolean utilEsrror = true;
		if (!_objTxt.getText().trim().isEmpty()) {

			try {
				Double.parseDouble(_objTxt.getText().trim());
				_objTxt.setBorder(null);
				utilEsrror = false;
			} catch (NumberFormatException e) {
				_objTxt.setBorder(BORDER_ERROR);
				utilEsrror = true;
			}

		}

		Node OkButton = getDialogPane().lookupButton(_buttonTypeOk);
		OkButton.setDisable(nomError || objError || utilEsrror);

	}

	/**
	 * Initialise les valeurs par défaut
	 */
	private void initValues() {

		if (_budget != null) {

			_nomTxt.setText(_budget.getNom());
			_objTxt.setText(String.valueOf(_budget.getObjectif()));
			_utilsTxt.setText(String.valueOf(_budget.getMontantUtilise()));

		}

	}

	/**
	 * Ajout de la vérification de la saisie
	 */
	private void hookListener() {

		_nomTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});

		_objTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});

		_utilsTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});

	}

	/**
	 * Création des boutons
	 */
	private void createButtonBar() {
		// bouton ok
		_buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().add(_buttonTypeOk);
		ButtonType close = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(close);

	}

}
