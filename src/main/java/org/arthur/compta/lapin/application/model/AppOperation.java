package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.application.manager.CompteManager;
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
public class AppOperation extends AppObject<Operation> {

	/** Le libellé de l'opération */
	protected SimpleStringProperty _libelleProp;
	/** Le montant de l'opération */
	protected SimpleDoubleProperty _montantProp;
	/** Le compte source */
	protected SimpleObjectProperty<AppCompte> _compteSourceProp;
	/** Etat de l'opération */
	protected SimpleObjectProperty<EtatOperation> _etatProp;
	/** le Type */
	protected SimpleObjectProperty<OperationType> _typeProp;

	/**
	 * Constructeur
	 */
	public AppOperation(Operation operation) {

		setAppID(operation.getId());
		_libelleProp = new SimpleStringProperty(operation.getNom());
		_montantProp = new SimpleDoubleProperty(operation.getMontant());
		_etatProp = new SimpleObjectProperty<EtatOperation>(operation.getEtat());
		_compteSourceProp = new SimpleObjectProperty<AppCompte>(CompteManager.getInstance().getAppCompteFromId(operation.getCompteId()));
		_typeProp = new SimpleObjectProperty<OperationType>(operation.getType());

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

	public SimpleObjectProperty<EtatOperation> etatProperty() {
		return _etatProp;
	}

	/**
	 * Retourne l'état de l'opération
	 * 
	 * @return
	 */
	public EtatOperation getEtat() {
		return _etatProp.get();
	}

	public OperationType getType() {
		return _typeProp.get();
	}

	/**
	 * Permutte l'état de l'opération
	 */
	public void switchEtat() {

		switch (getEtat()) {

		case PRISE_EN_COMPTE:
			setEtat(EtatOperation.PREVISION);
			break;
		case PREVISION:
			setEtat(EtatOperation.PRISE_EN_COMPTE);
			break;

		}

	}

	public void setEtat(EtatOperation etatF) {

		_etatProp.set(etatF);

	}

	/**
	 * Retourne le libellé de l'opération
	 * 
	 * @return
	 */
	public String getLibelle() {

		return _libelleProp.get();
	}

	public void setLibelle(String newLib) {
		_libelleProp.set(newLib);

	}

	public void setMontant(double newMontant) {
		_montantProp.set(newMontant);

	}

	@Override
	public Operation getDBObject() {
		return new Operation(getAppId(), getType(), getCompteSource().getAppId(), getLibelle(), getMontant(), getEtat(), -1);
	}

}
