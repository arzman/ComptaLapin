package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.model.Compte;

import javafx.beans.property.SimpleStringProperty;

/**
 * Compte applicatif encapsule un {@link Compte}. Cet objet est disponible pour
 * la couche de présentation et de persistance
 *
 */
public class AppCompte {
	
	/**
	 * le compte métier
	 */
	private Compte _compte;
	
	/**
	 * Le nom du compte
	 */
	private SimpleStringProperty _nomProp;
	
	/**
	 * Constructeur
	 * @param compte_ le compte métier relié
	 */
	public AppCompte(Compte compte_) {
		_compte = compte_;
		
		_nomProp = new SimpleStringProperty(_compte.getNom());
		
	}
	
	
	public String getNomProp(){
		return _nomProp.get();
	}
	
	

	

}
