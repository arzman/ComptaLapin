package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.model.Compte;

import javafx.beans.property.SimpleDoubleProperty;
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
	 * Le solde du compte
	 */
	private SimpleDoubleProperty _soldeProp;
	
	
	
	/**
	 * Constructeur
	 * @param compte_ le compte métier relié
	 */
	public AppCompte(Compte compte_) {
		_compte = compte_;
		
		_nomProp = new SimpleStringProperty(_compte.getNom());
		_soldeProp = new SimpleDoubleProperty(_compte.getSolde());
		
	}
	
	/**
	 * Retourne le nom du compte
	 * @return le nom du compte
	 */
	public String getNomProp(){
		return _nomProp.get();
	}
	
	/**
	 * Retourne le solde du compte
	 * @return le solde du compte
	 */
	public double getSoldeProp(){
		return _soldeProp.get();
	}
	
	

	

}
