package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.model.operation.TransfertOperation;

import javafx.beans.property.SimpleStringProperty;

/**
 * Encapsulation application d'une opération de transfert
 *
 */
public class AppTransfert extends AppOperation<TransfertOperation> {

	/**
	 * le compte source
	 */
	private SimpleStringProperty _sourceProp;
	/**
	 * Le compte cible
	 */
	private SimpleStringProperty _cibleProp;

	/**
	 * Constructeur
	 * 
	 * @param transfert
	 *            le transfert à encapsuler
	 */
	public AppTransfert(TransfertOperation transfert) {

		super(transfert);

		_sourceProp = new SimpleStringProperty(_operation.getCompte().getNom());
		_cibleProp = new SimpleStringProperty(_operation.getCompteCible().getNom());

	}

	/**
	 * Retourne Le compte source sous forme de property
	 * 
	 * @return
	 */
	public SimpleStringProperty sourceProperty() {
		return _sourceProp;
	}

	/**
	 * Retourne le compte cible sous forme de property
	 * 
	 * @return
	 */
	public SimpleStringProperty cibleProperty() {
		return _cibleProp;
	}

}
