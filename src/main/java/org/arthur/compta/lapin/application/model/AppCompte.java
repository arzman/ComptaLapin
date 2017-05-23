package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.model.Compte;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Compte applicatif encapsule un Compte. Cet objet est disponible pour la
 * couche de présentation et la persistance
 *
 */
public class AppCompte extends AppObject  {

	/**
	 * le compte métier
	 */
	private Compte _compte;

	/**
	 * Le nom du compte
	 */
	private SimpleStringProperty _nom;

	/**
	 * Le solde du compte
	 */
	private SimpleDoubleProperty _solde;


	/**
	 * Indique si le compte est un livret
	 */
	private SimpleBooleanProperty _isLivretProp;

	/**
	 * Indique si le compte est concerné par les budgets
	 */
	private SimpleBooleanProperty _isBudgetProp;

	/**
	 * Constructeur
	 * 
	 * @param compte_
	 *            le compte métier relié
	 */
	public AppCompte(Compte compte_) {
		_compte = compte_;

		// encapsulation des attributs du compte
		_nom = new SimpleStringProperty(_compte.getNom());
		_solde = new SimpleDoubleProperty(_compte.getSolde());
		_isLivretProp = new SimpleBooleanProperty(_compte.isLivret());
		_isBudgetProp = new SimpleBooleanProperty(_compte.isBudgetAllowed());

	}

	/**
	 * Retourne le nom du compte
	 * 
	 * @return le nom du compte
	 */
	public String getNom() {
		return _nom.get();
	}

	/**
	 * Retourne la propriété observable du nom
	 * @return
	 */
	public SimpleStringProperty nomProperty() {
		return _nom;
	}
	
	/**
	 * Positionne le nom du compte
	 * @param nom
	 */
	public void setNom(String nom) {
		_compte.setNom(nom);
		_nom.set(nom);
		
	}
	
	/**
	 * Retourne le solde du compte
	 * 
	 * @return le solde du compte
	 */
	public double getSolde() {
		return _solde.get();
	}
	
	/**
	 * Retourne la propriété observable du solde
	 * @return
	 */
	public SimpleDoubleProperty soldeProperty(){
		return _solde;
	}
	
	/**
	 * Positionne le solde du compte
	 * @param solde
	 */
	public void setSolde(double soldes) {
		_compte.setSolde(soldes);
		_solde.set(soldes);
		
	}

	/**
	 * Retourne vrai si le compte est livret
	 * 
	 * @return vrai si le compte est livret
	 */
	public boolean isLivret() {
		return _isLivretProp.get();
	}

	/**
	 * Retourne vrai si le compte est concerné par les budgets
	 * 
	 * @return vrai si le compte est concerné par les budgets
	 */
	public boolean isBudget() {

		return _isBudgetProp.get();
	}

	/**
	 * Positionne le flag isLivret
	 * @param isLivret le flag isLivret
	 */
	public void setIsLivret(boolean isLivret) {
		_isLivretProp.set(isLivret);
		_compte.setLivret(isLivret);
		
	}

	/**
	 * Posiotionne le flag isBudget
	 * @param isBudget
	 */
	public void setIsBudget(boolean isBudget) {
		_isBudgetProp.set(isBudget);
		_compte.setBudgetAllowed(isBudget);
		
	}
	
	
	@Override
	public String toString() {

		return _nom.get();
	}
	

}
