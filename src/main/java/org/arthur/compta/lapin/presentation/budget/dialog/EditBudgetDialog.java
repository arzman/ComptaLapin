package org.arthur.compta.lapin.presentation.budget.dialog;

import org.arthur.compta.lapin.application.manager.BudgetManager;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * Fenetre de création ou édition de budget
 *
 */
public class EditBudgetDialog extends ComptaDialog<AppBudget> {

	/** Le budget créé ou édité */
	private AppBudget _budget;

	/** Le champ de saisi du nom */
	private TextField _nomTxt;
	/** Le champ de saisi de l'objectif */
	private TextField _objTxt;
	/** Le champ de saisi du montant utilisé */
	private TextField _utilsTxt;
	/** active le budget recurrent */
	private CheckBox _isReccurentChckB;
	/** Liste des budget recurrent */
	private ComboBox<String> _listBudRecuCB;
	/** Date du budget récurrent */
	private DatePicker _dateBudgetDP;

	/**
	 * Constructeur
	 * 
	 * @param budget le budget a éditer, null si création
	 */
	public EditBudgetDialog(AppBudget budget) {

		super(EditBudgetDialog.class.getSimpleName());

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
									Double.parseDouble(_utilsTxt.getText().trim()),
									_listBudRecuCB.getSelectionModel().getSelectedItem(), _dateBudgetDP.getValue());
						} catch (Exception e) {
							ExceptionDisplayService.showException(e);
						}

					} else {
						// édition
						try {
							_budget = BudgetManager.getInstance().editBudget(_budget, _nomTxt.getText().trim(),
									Double.parseDouble(_objTxt.getText().trim()),
									Double.parseDouble(_utilsTxt.getText().trim()), _budget.isActif(),
									_budget.getPriority(), _listBudRecuCB.getSelectionModel().getSelectedItem(),
									_dateBudgetDP.getValue());
						} catch (Exception e) {
							ExceptionDisplayService.showException(e);
						}
					}

				}

				return _budget;
			}
		});

		// vérif initiale
		checkInput();

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

		// rattachement a un budget récurrent
		_isReccurentChckB = new CheckBox("Budget récurrent");
		root.add(_isReccurentChckB, 0, 3);

		_dateBudgetDP = new DatePicker();
		root.add(_dateBudgetDP, 1, 3);

		// liste des budgets récurrent
		_listBudRecuCB = new ComboBox<>();
		root.add(_listBudRecuCB, 0, 4, 2, 1);
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
		if (!_utilsTxt.getText().trim().isEmpty()) {

			try {
				Double.parseDouble(_utilsTxt.getText().trim());
				_utilsTxt.setBorder(null);
				utilEsrror = false;
			} catch (NumberFormatException e) {
				_utilsTxt.setBorder(BORDER_ERROR);
				utilEsrror = true;
			}

		}

		// verif de la récurrence
		boolean isReccurent = _isReccurentChckB.isSelected();
		boolean recError = true;
		_dateBudgetDP.setDisable(!isReccurent);
		_listBudRecuCB.setDisable(!isReccurent);

		if (isReccurent) {

			if (_dateBudgetDP.getValue() != null && _listBudRecuCB.getSelectionModel().getSelectedItem()!=null) {
				recError = false;
			}

		} else {
			recError = false;
		}

		Node OkButton = getDialogPane().lookupButton(_buttonTypeOk);
		OkButton.setDisable(nomError || objError || utilEsrror || recError);

	}

	/**
	 * Initialise les valeurs par défaut
	 */
	private void initValues() {

		try {

			_listBudRecuCB
					.setItems(FXCollections.observableArrayList(BudgetManager.getInstance().getLabelRecurrentList()));

			if (_budget != null) {

				_nomTxt.setText(_budget.getNom());
				_objTxt.setText(String.valueOf(_budget.getObjectif()));
				_utilsTxt.setText(String.valueOf(_budget.getMontantUtilise()));
				_isReccurentChckB.setSelected(_budget.isRecurrent());
				if (_budget.isRecurrent()) {
					_listBudRecuCB.getSelectionModel().select(_budget.getLabelRecurrent());
					_dateBudgetDP.setValue(_budget.getDateRecurrent());
				}

			}
		} catch (Exception e) {
			ExceptionDisplayService.showException(e);
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

		_isReccurentChckB.selectedProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();

		});
		_listBudRecuCB.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});

		_dateBudgetDP.valueProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});

	}

	/**
	 * Création des boutons
	 */
	protected void createButtonBar() {
		// bouton ok
		super.createButtonBar();
		ButtonType close = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(close);

	}

}
