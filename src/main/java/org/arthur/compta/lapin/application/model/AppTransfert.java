package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.model.operation.Operation;

import javafx.beans.property.SimpleObjectProperty;

/**
 * Encapsulation application d'une opération de transfert
 *
 */
public class AppTransfert extends AppOperation {

	/** Le compte cible */
	private SimpleObjectProperty<AppCompte> _compteCibleProp;

	/**
	 * Constructeur
	 * 
	 * @param transfert
	 *            le transfert à encapsuler
	 */
	public AppTransfert(Operation transfert) {

		super(transfert);
		_compteCibleProp = new SimpleObjectProperty<AppCompte>(CompteManager.getInstance().getAppCompteFromId(transfert.getCibleCompteId()));

	}

	/**
	 * Retourne le compte cible sous forme de property
	 * 
	 * @return
	 */
	public SimpleObjectProperty<AppCompte> compteCibleProperty() {
		return _compteCibleProp;
	}

	/**
	 * Retourne le compte cible du transfert
	 * 
	 * @return
	 */
	public AppCompte getCompteCible() {
		return _compteCibleProp.get();
	}

	/**
	 * Positionne le compte cible
	 * 
	 * @param compte
	 */
	public void setCompteCible(AppCompte compte) {
		_compteCibleProp.set(compte);

	}

	@Override
	public Operation getDBObject() {

		return new Operation(getAppId(), getType(), getCompteSource().getAppId(), getLibelle(), getMontant(), getEtat(), getCompteCible().getAppId());
	}

}
