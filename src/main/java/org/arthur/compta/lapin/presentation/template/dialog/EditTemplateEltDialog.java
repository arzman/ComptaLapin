package org.arthur.compta.lapin.presentation.template.dialog;

import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElement;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElementFrequence;
import org.arthur.compta.lapin.application.service.OperationService;
import org.arthur.compta.lapin.application.service.TemplateService;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.template.cellfactory.CompteCellComboFactory;
import org.arthur.compta.lapin.presentation.template.cellfactory.OccurenceCellFactory;

import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * Fenêtre de saisie d'un élément de template de trimestre
 *
 */
public class EditTemplateEltDialog extends ComptaDialog<TrimestreTemplateElement> {

	/** L'élément de template */
	private TrimestreTemplateElement _templateElt;

	/** champ de saisie du nom */
	private TextField _nomTxt;
	/** champ de saisie du montant */
	private TextField _montantTxt;
	/** champ de saisie du type */
	private ComboBox<String> _typeCombo;
	/** saisie de la frequence */
	private ComboBox<String> _freqCombo;
	/** Saisie de l'occurence */
	private ComboBox<Integer> _occComb;
	/** Saisie du compte source */
	private ComboBox<AppCompte> _srcCombo;
	/** Saisie du compte cible */
	private ComboBox<AppCompte> _cibleCombo;

	/**
	 * Constructeur
	 * 
	 * @param elt
	 *            l'element a éditer , null si création
	 */
	public EditTemplateEltDialog(TrimestreTemplateElement elt) {

		super(EditTemplateEltDialog.class.getSimpleName());

		_templateElt = elt;

		if (_templateElt == null) {
			setTitle("Création d'un élément");
		} else {
			setTitle("Edition d'un élément");
		}

		// création des zones de saisie
		createContent();

		// initialisation des valeurs
		initValues();
		// mise en place des listener sur les modifications
		hookListeners();
		// vérif initiale
		if (_templateElt == null) {
			checkInput(true);
		} else {
			checkInput(false);
		}

		// crée l'élement de template après appuis sur Ok
		setResultConverter(new Callback<ButtonType, TrimestreTemplateElement>() {

			@Override
			public TrimestreTemplateElement call(ButtonType param) {

				TrimestreTemplateElement elt = _templateElt;
				// appuis sur ok
				if (param.equals(_buttonTypeOk)) {

					if (elt == null) {
						elt = new TrimestreTemplateElement();
					}
					elt.setNom(_nomTxt.getText());
					elt.setMontant(Double.parseDouble(_montantTxt.getText()));
					elt.setType(_typeCombo.getSelectionModel().getSelectedItem());
					elt.setFreq(TrimestreTemplateElementFrequence.valueOf(_freqCombo.getSelectionModel().getSelectedItem()));
					if (!_occComb.isDisable()) {
						elt.setOccurence(_occComb.getSelectionModel().getSelectedItem());
					}
					elt.setCompteSource(_srcCombo.getSelectionModel().getSelectedItem());
					if (!_cibleCombo.isDisable()) {
						elt.setCompteCible(_cibleCombo.getSelectionModel().getSelectedItem());
					}

				}

				return elt;
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
		Label nomLdl = new Label("Nom :");
		root.add(nomLdl, 0, 0);
		_nomTxt = new TextField();
		root.add(_nomTxt, 1, 0);

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

		// saisie de la frequence
		Label freqLbl = new Label("Fréquence :");
		root.add(freqLbl, 0, 3);
		_freqCombo = new ComboBox<String>();
		_freqCombo.setItems(TemplateService.getTemplateEltFreq());
		root.add(_freqCombo, 1, 3);

		// saisie de l'occurence le contenu est positionné par checkInput()
		Label occLbl = new Label("Occurence :");
		root.add(occLbl, 0, 4);
		_occComb = new ComboBox<Integer>();
		_occComb.setCellFactory(new OccurenceCellFactory(_freqCombo));
		root.add(_occComb, 1, 4);

		// saisie du compte source
		Label srcLbl = new Label("Source :");
		root.add(srcLbl, 0, 5);
		_srcCombo = new ComboBox<AppCompte>();
		_srcCombo.setItems(CompteManager.getInstance().getCompteList());
		_srcCombo.setCellFactory(new CompteCellComboFactory());
		root.add(_srcCombo, 1, 5);

		// saisie du compte cible
		Label cibleLbl = new Label("Cible");
		root.add(cibleLbl, 0, 6);
		_cibleCombo = new ComboBox<AppCompte>();
		_cibleCombo.setItems(CompteManager.getInstance().getCompteList());
		_cibleCombo.setCellFactory(new CompteCellComboFactory());
		root.add(_cibleCombo, 1, 6);
	}

	/**
	 * Positionne les valeurs de l'ihm à partir de l'élément de template
	 */
	private void initValues() {

		if (_templateElt != null) {
			// édition
			_nomTxt.setText(_templateElt.getNom());
			_montantTxt.setText(String.valueOf(_templateElt.getMontant()));
			_typeCombo.getSelectionModel().select(String.valueOf(_templateElt.getType()));
			_freqCombo.getSelectionModel().select(String.valueOf(_templateElt.getFreq()));
			_occComb.getItems().addAll(TemplateService.getOccurenceForFreq(_freqCombo.getSelectionModel().getSelectedItem()));
			_occComb.getSelectionModel().select(new Integer(_templateElt.getOccurence()));
			_srcCombo.getSelectionModel().select(_templateElt.getCompteSource());
			_cibleCombo.getSelectionModel().select(_templateElt.getCompteCible());

		} else {
			// création
			_nomTxt.setText("");
			_montantTxt.setText("0");
			_typeCombo.getSelectionModel().select(0);
			_freqCombo.getSelectionModel().select(0);
			_occComb.getSelectionModel().select(0);
			_srcCombo.getSelectionModel().select(0);
			_cibleCombo.getSelectionModel().select(0);
		}

	}

	/**
	 * Création des boutons
	 */
	protected void createButtonBar() {
		super.createButtonBar();
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
		_nomTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			checkInput(false);
		});
		// montant
		_montantTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			checkInput(false);
		});
		// type
		_typeCombo.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			checkInput(false);
		});
		// frequence
		_freqCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			checkInput(true);
		});
		// compte source
		_srcCombo.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			checkInput(false);
		});
		// compte cible
		_cibleCombo.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			checkInput(false);
		});

	}

	/**
	 * Vérifie la validité de la saisie
	 */
	private void checkInput(boolean changeOcc) {

		// Vérif du nom
		boolean nomError = true;

		if (!_nomTxt.getText().trim().isEmpty()) {
			_nomTxt.setBorder(null);
			nomError = false;
		} else {
			_nomTxt.setBorder(BORDER_ERROR);
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

		_cibleCombo.setDisable(!TrimestreManager.getInstance().isTransfertType(_typeCombo.getSelectionModel().getSelectedItem()));

		// Vérif de la frequence
		if (_freqCombo.getSelectionModel().getSelectedItem() != null) {
			_occComb.setDisable(_freqCombo.getSelectionModel().getSelectedItem().equals(TrimestreTemplateElementFrequence.MENSUEL.toString()));

			if (changeOcc) {
				_occComb.getItems().clear();
				_occComb.getItems().addAll(TemplateService.getOccurenceForFreq(_freqCombo.getSelectionModel().getSelectedItem()));
				_occComb.getSelectionModel().select(0);
			}

		}

		if (_buttonTypeOk != null) {
			Node OkButton = getDialogPane().lookupButton(_buttonTypeOk);
			OkButton.setDisable(nomError || soldeError);
		}
	}

}
