package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.Operation;
import org.arthur.compta.lapin.model.operation.OperationType;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
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
	/** Le libellé de l'opération */
	protected SimpleStringProperty _libelleProp;
	/** Le montant de l'opération */
	protected SimpleDoubleProperty _montantProp;
	/** Le compte source */
	protected SimpleObjectProperty<AppCompte> _compteSourceProp;
	/** Etat de l'opération */
	protected SimpleStringProperty _etatProp;

	/**
	 * Constructeur
	 */
	public AppOperation(Operation operation) {

		_operation = operation;
		_libelleProp = new SimpleStringProperty(_operation.getNom());
		_montantProp = new SimpleDoubleProperty(_operation.getMontant());
		_etatProp = new SimpleStringProperty(_operation.getEtat().toString());
		_compteSourceProp = new SimpleObjectProperty<AppCompte>();

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

	/**
	 * Retourne le compte source de l'opération
	 * 
	 * @return
	 */
	public AppCompte getCompteSource() {
		return _compteSourceProp.get();
	}

	/**
	 * Retourne l'opération encapsulée
	 * 
	 * @return
	 */
	public Operation getOperation() {
		return _operation;
	}

	/**
	 * Positionne le compte source
	 * 
	 * @param compte
	 */
	public void setCompteSrc(AppCompte compte) {
		_compteSourceProp.set(compte);

	}

	/**
	 * Retourne le compte source sous forme de propriété
	 * 
	 * @return
	 */
	public SimpleObjectProperty<AppCompte> compteSourceProperty() {
		return _compteSourceProp;
	}

	public SimpleStringProperty etatProperty() {
		return _etatProp;
	}

	/**
	 * Retourne l'état de l'opération
	 * 
	 * @return
	 */
	public String getEtat() {
		return _etatProp.get();
	}

	public OperationType getType() {
		return _operation.getType();
	}

	/**
	 * Permutte l'état de l'opération
	 */
	public void switchEtat() {
		if (_operation.getEtat() == EtatOperation.PREVISION) {

			_operation.setEtat(EtatOperation.PRISE_EN_COMPTE);

		} else {

			if (_operation.getEtat() == EtatOperation.PRISE_EN_COMPTE) {

				_operation.setEtat(EtatOperation.PREVISION);

			}

		}

		_etatProp.set(_operation.getEtat().toString());
	}

	/**
	 * Retourne le libellé de l'opération
	 * 
	 * @return
	 */
	public String getLibelle() {

		return _libelleProp.get();
	}

}
