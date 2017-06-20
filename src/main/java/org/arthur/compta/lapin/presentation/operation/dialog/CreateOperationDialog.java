package org.arthur.compta.lapin.presentation.operation.dialog;

import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.application.service.OperationService;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.template.cellfactory.CompteCellComboFactory;

import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * Fenêtre permettant la création et l'édition d'opération
 *
 */
public class CreateOperationDialog extends ComptaDialog<String> {

	/** L'opération a créer ou éditer */
	private AppOperation _operation;
	/** l'index du mois dans lequel l'opération sera créée */
	private int _numMois;
	/** La bordure rouge en cas d'erreur de saisi */
	private final Border BORDER_ERROR = new Border(
			new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1)));

	/** champ de saisie du nom */
	private TextField _libTxt;
	/** champ de saisie du montant */
	private TextField _montantTxt;
	/** champ de saisie du type */
	private ComboBox<String> _typeCombo;
	/** Saisie du compte source */
	private ComboBox<AppCompte> _srcCombo;
	/** Saisie du compte cible */
	private ComboBox<AppCompte> _cibleCombo;
	/** le bouton ok */
	private ButtonType _okButton;

	/**
	 * Constructeur
	 * 
	 * @param op
	 *            l'opération à éditer, null si création
	 */
	public CreateOperationDialog(AppOperation op, int numMois) {

		super(CreateOperationDialog.class.getSimpleName());

		setTitle("Création d'un opération");
		_operation = op;
		_numMois = numMois;
		// création du contenu
		createContent();
		// initialisation des valeurs
		initValues();
		// Création des boutons
		createButtonBar();
		// ajout des listeners sur les champs
		hookListeners();

		// crée ou édite l'élement de template après appuis sur Ok
		setResultConverter(new Callback<ButtonType, String>() {

			@Override
			public String call(ButtonType param) {

				String res = "Cancel";

				// appuis sur ok
				if (param.equals(_okButton)) {

					// création
					if (_operation == null) {

						try {
							_operation = TrimestreManager.getInstance().addOperation(_libTxt.getText(),
									Double.parseDouble(_montantTxt.getText()),
									_typeCombo.getSelectionModel().getSelectedItem(),
									_srcCombo.getSelectionModel().getSelectedItem(),
									_cibleCombo.getSelectionModel().getSelectedItem(), _numMois);
						} catch (Exception e) {
							ExceptionDisplayService.showException(e);
						}

					} else {
						// édition
						try {
							_operation = OperationService.editOperation(_operation, _libTxt.getText(),
									Double.parseDouble(_montantTxt.getText()),
									_srcCombo.getSelectionModel().getSelectedItem(),
									_cibleCombo.getSelectionModel().getSelectedItem());
						} catch (Exception e) {
							ExceptionDisplayService.showException(e);
						}
					}

				}

				return res;
			}
		});

	}

	/**
	 * Création des champ de saisi
	 */
	private void createContent() {

		GridPane root = new GridPane();
		getDialogPane().setContent(root);

		// saisie du nom
		Label nomLdl = new Label("Libellé :");
		root.add(nomLdl, 0, 0);
		_libTxt = new TextField();
		root.add(_libTxt, 1, 0);

		// saisie du montant
		Label montantLdl = new Label("Montant :");
		root.add(montantLdl, 0, 1);
		_montantTxt = new TextField();
		root.add(_montantTxt, 1, 1);

		// saisie du type
		Label typeLbl = new Label("Type :");
		root.add(typeLbl, 0, 2);
		_typeCombo = new ComboBox<String>();
		_typeCombo.setItems(OperationService.getOperationType());
		root.add(_typeCombo, 1, 2);

		// saisie du compte source
		Label srcLbl = new Label("Source :");
		root.add(srcLbl, 0, 3);
		_srcCombo = new ComboBox<AppCompte>();
		_srcCombo.setItems(CompteManager.getInstance().getCompteList());
		_srcCombo.setCellFactory(new CompteCellComboFactory());
		root.add(_srcCombo, 1, 3);

		// saisie du compte cible
		Label cibleLbl = new Label("Cible");
		root.add(cibleLbl, 0, 4);
		_cibleCombo = new ComboBox<AppCompte>();
		_cibleCombo.setItems(CompteManager.getInstance().getCompteList());
		_cibleCombo.setCellFactory(new CompteCellComboFactory());
		root.add(_cibleCombo, 1, 4);
	}

	/**
	 * Positionne les valeurs de l'ihm à partir de l'élément de template
	 */
	private void initValues() {

		if (_operation != null) {
			// édition
			_libTxt.setText(_operation.getLibelle());
			_montantTxt.setText(String.valueOf(_operation.getMontant()));
			_typeCombo.getSelectionModel().select(String.valueOf(_operation.getType()));
			_typeCombo.setDisable(true);
			_srcCombo.getSelectionModel().select(_operation.getCompteSource());
			if (_operation instanceof AppTransfert) {
				_cibleCombo.getSelectionModel().select(((AppTransfert) _operation).getCompteCible());
			}

		} else {
			// création
			_libTxt.setText("");
			_montantTxt.setText("0");
			_typeCombo.getSelectionModel().select(0);
			_srcCombo.getSelectionModel().select(0);
			_cibleCombo.getSelectionModel().select(0);
		}

	}

	/**
	 * Création des boutons
	 */
	private void createButtonBar() {
		// bouton ok
		_okButton = new ButtonType("Ok", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().add(_okButton);
		// bouton annuler
		ButtonType cancelButton = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(cancelButton);
	}

	/**
	 * Affecte des écouteurs de modification sur les champs de saisie. Ces
	 * écouteurs déclenchent la vérification de la saisie
	 */
	private void hookListeners() {
		// nom
		_libTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});
		// montant
		_montantTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});
		// type
		_typeCombo.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});
		// compte source
		_srcCombo.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});
		// compte cible
		_cibleCombo.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});

	}

	/**
	 * Vérifie la validité de la saisie
	 */
	private void checkInput() {

		// Vérif du libellé
		boolean nomError = true;

		if (!_libTxt.getText().trim().isEmpty()) {
			_libTxt.setBorder(null);
			nomError = false;
		} else {
			_libTxt.setBorder(BORDER_ERROR);
			nomError = true;
		}

		// Vérif du montant
		boolean soldeError = true;
		try {
			Double.parseDouble(_montantTxt.getText().trim());
			_montantTxt.setBorder(null);
			soldeError = false;
		} catch (NumberFormatException e) {
			_montantTxt.setBorder(BORDER_ERROR);
			soldeError = true;
		}
		// vérif du type

		_cibleCombo.setDisable(
				!TrimestreManager.getInstance().isTransfertType(_typeCombo.getSelectionModel().getSelectedItem()));

		if (_okButton != null) {
			Node OkButton = getDialogPane().lookupButton(_okButton);
			OkButton.setDisable(nomError || soldeError);
		}
	}

}
