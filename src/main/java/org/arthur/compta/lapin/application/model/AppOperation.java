package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.model.operation.Operation;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Encapsulation applicative d'une opération
 *
 */
public class AppOperation extends AppObject implements IMontant {

	/**
	 * L'operation
	 */
	protected Operation _operation;
	/**
	 * Le libellé de l'opération
	 */
	protected SimpleStringProperty _libelleProp;

	/**
	 * Le montant de l'opération
	 */
	protected SimpleDoubleProperty _montantProp;

	/**
	 * Constructeur
	 */
	public AppOperation(Operation operation) {

		_operation = operation;

		_libelleProp = new SimpleStringProperty(_operation.getNom());
		_montantProp = new SimpleDoubleProperty(_operation.getMontant());

	}

	/**
	 * Retourne le libelle de l'opération sous forme de propriété
	 * 
	 * @return
	 */
	public SimpleStringProperty libelleProperty() {
		return _libelleProp;
	}

	/**
	 * Retourne le montant de l'opération sous forme de propriété
	 * 
	 * @return
	 */
	public SimpleDoubleProperty montantProperty() {

		return _montantProp;
	}

	/**
	 * Retourne le montant de l'opération
	 * 
	 * @return
	 */
	public double getMontant() {
		return _montantProp.doubleValue();
	}

}
