package org.arthur.compta.lapin.presentation.trimestre.dialog;

import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElement;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElementFrequence;
import org.arthur.compta.lapin.presentation.trimestre.cellfactory.CompteCellComboFactory;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * Fenêtre de saisie d'un élément de template de trimestre
 *
 */
public class EditTemplateEltDialog extends Dialog<TrimestreTemplateElement> {

	private TrimestreTemplateElement _templateElt;

	/**
	 * La bordure rouge en cas d'erreur de saisi
	 */
	private final Border BORDER_ERROR = new Border(
			new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1)));

	/** champ de saisie du nom */
	private TextField _nomTxt;
	/** champ de saisie du montant */
	private TextField _montantTxt;
	/** champ de saisie du type */
	private ComboBox<String> _typeCombo;
	/** saisie de la frequence */
	private ComboBox<String> _freqCombo;
	/** Saisie de l'occurence */
	private TextField _occtxt;
	/** Saisie du compte source */
	private ComboBox<AppCompte> _srcCombo;
	/** Saisie du compte cible */
	private ComboBox<AppCompte> _cibleCombo;
	/** le bouton ok */
	private ButtonType _okButton;

	/**
	 * Constructeur
	 * 
	 * @param elt
	 *            l'element a éditer , null si création
	 */
	public EditTemplateEltDialog(TrimestreTemplateElement elt) {

		_templateElt = elt;

		if (_templateElt == null) {
			setTitle("Création d'un élément");
		} else {
			setTitle("Edition d'un élément");
		}

		// création des zones de saisie
		createContent();

		// création des bouton ok/cancel
		createButtonBar();
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
		_nomTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});
		if (_templateElt != null) {
			_nomTxt.setText(_templateElt.getNom());
		}
		root.add(_nomTxt, 1, 0);

		// saisie du montant
		Label montantLdl = new Label("Montant :");
		root.add(montantLdl, 0, 1);
		_montantTxt = new TextField();
		_montantTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});
		if (_templateElt != null) {
			_montantTxt.setText(String.valueOf(_templateElt.getMontant()));
		}
		root.add(_montantTxt, 1, 1);

		// saisie du type
		Label typeLbl = new Label("Type :");
		root.add(typeLbl, 0, 2);
		_typeCombo = new ComboBox<String>();
		_typeCombo.setItems(TrimestreManager.getInstance().getTemplateEltType());
		if (_templateElt != null) {
			_typeCombo.getSelectionModel().select(String.valueOf(_templateElt.getType()));
		} else {
			_typeCombo.getSelectionModel().select(0);
		}
		_typeCombo.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});
		root.add(_typeCombo, 1, 2);

		// saisie de la frequence
		Label freqLbl = new Label("Fréquence :");
		root.add(freqLbl, 0, 3);
		_freqCombo = new ComboBox<String>();
		_freqCombo.setItems(TrimestreManager.getInstance().getTemplateEltFreq());
		if (_templateElt != null) {
			_freqCombo.getSelectionModel().select(String.valueOf(_templateElt.getType()));
		} else {
			_freqCombo.getSelectionModel().select(0);
		}
		_freqCombo.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});
		root.add(_freqCombo, 1, 3);

		// saisie de l'occurence
		Label occLbl = new Label("Occurence :");
		root.add(occLbl, 0, 4);
		_occtxt = new TextField();
		if (_templateElt != null) {
			_occtxt.setText(String.valueOf(_templateElt.getOccurence()));
		}
		_occtxt.textProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});
		root.add(_occtxt, 1, 4);

		// saisie du compte source
		Label srcLbl = new Label("Source :");
		root.add(srcLbl, 0, 5);
		_srcCombo = new ComboBox<AppCompte>();
		_srcCombo.setItems(CompteManager.getInstance().getCompteList());
		_srcCombo.setCellFactory(new CompteCellComboFactory());
		if (_templateElt != null) {
			_srcCombo.getSelectionModel().select(_templateElt.getCompteSource());
		} else {
			_srcCombo.getSelectionModel().select(0);
		}
		_srcCombo.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});
		_srcCombo.getSelectionModel().select(0);
		root.add(_srcCombo, 1, 5);

		// saisie du compte cible
		Label cibleLbl = new Label("Cible");
		root.add(cibleLbl, 0, 6);
		_cibleCombo = new ComboBox<AppCompte>();
		_cibleCombo.setItems(CompteManager.getInstance().getCompteList());
		_cibleCombo.setCellFactory(new CompteCellComboFactory());
		if (_templateElt != null) {
			_cibleCombo.getSelectionModel().select(_templateElt.getCompteSource());
		} else {
			_cibleCombo.getSelectionModel().select(0);
		}
		_cibleCombo.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});
		_cibleCombo.getSelectionModel().select(0);
		root.add(_cibleCombo, 1, 6);

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

		// Vérif du montant
		boolean soldeError = true;
		if (!_montantTxt.getText().trim().isEmpty()) {

			try {
				Double.parseDouble(_montantTxt.getText().trim());
				_montantTxt.setBorder(null);
				soldeError = false;
			} catch (NumberFormatException e) {
				_montantTxt.setBorder(BORDER_ERROR);
				soldeError = true;
			}

		}

		// Vérif de la frequence
		_occtxt.setDisable(
				_freqCombo.getSelectionModel().getSelectedItem().equals(TrimestreTemplateElementFrequence.MENSUEL.toString()));
		
		//Vérif de l'occurence
		boolean occError = true;
		if(!_occtxt.isDisable()){
			try {
				Integer.parseInt(_occtxt.getText().trim());
				_occtxt.setBorder(null);
				occError = false;
			} catch (NumberFormatException e) {
				_occtxt.setBorder(BORDER_ERROR);
				occError = true;
			}
			
			
		}else{
			_occtxt.setBorder(null);
			occError = false;
		}

		Node OkButton = getDialogPane().lookupButton(_okButton);
		OkButton.setDisable(nomError || soldeError || occError );
	}

}
