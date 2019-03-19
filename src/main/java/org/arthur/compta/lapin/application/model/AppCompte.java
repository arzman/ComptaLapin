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
public class AppCompte extends AppObject<Compte> {

	/** Le nom du compte */
	private SimpleStringProperty _nom;
	/** Le solde du compte */
	private SimpleDoubleProperty _solde;
	/** Indique si le compte est un livret */
	private SimpleBooleanProperty _isLivretProp;
	/** Indique si le compte est concerné par les budgets */
	private SimpleBooleanProperty _isBudgetProp;
	/** Le solde du compte */
	private SimpleDoubleProperty _soldePrev1;
	/** Le solde du compte */
	private SimpleDoubleProperty _soldePrev2;
	/** Le solde du compte */
	private SimpleDoubleProperty _soldePrev3;

	/**
	 * Constructeur
	 * 
	 * @param compte_
	 *            le compte métier relié
	 */
	public AppCompte(Compte compte) {

		setAppID(compte.getId());

		// encapsulation des attributs du compte
		_nom = new SimpleStringProperty(compte.getNom());
		_solde = new SimpleDoubleProperty(compte.getSolde());
		_isLivretProp = new SimpleBooleanProperty(compte.isLivret());
		_isBudgetProp = new SimpleBooleanProperty(compte.isBudgetAllowed());

		// solde prévisionnel
		_soldePrev1 = new SimpleDoubleProperty(0);
		_soldePrev2 = new SimpleDoubleProperty(0);
		_soldePrev3 = new SimpleDoubleProperty(0);

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
	 * 
	 * @return
	 */
	public SimpleStringProperty nomProperty() {
		return _nom;
	}

	/**
	 * Positionne le nom du compte
	 * 
	 * @param nom
	 */
	public void setNom(String nom) {
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
	 * 
	 * @return
	 */
	public SimpleDoubleProperty soldeProperty() {
		return _solde;
	}

	/**
	 * Positionne le solde du compte
	 * 
	 * @param solde
	 */
	public void setSolde(double soldes) {
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
	 * 
	 * @param isLivret
	 *            le flag isLivret
	 */
	public void setIsLivret(boolean isLivret) {
		_isLivretProp.set(isLivret);

	}

	/**
	 * Posiotionne le flag isBudget
	 * 
	 * @param isBudget
	 */
	public void setIsBudget(boolean isBudget) {
		_isBudgetProp.set(isBudget);

	}

	@Override
	public String toString() {

		return _nom.get();
	}

	/**
	 * Retourne le montant prévu à la fin du premier mois du trimestre sous
	 * forme de propriété
	 * 
	 * @return
	 */
	public SimpleDoubleProperty soldePrev1Property() {
		return _soldePrev1;
	}

	/**
	 * Retourne le montant prévu à la fin du deuxieme mois du trimestre sous
	 * forme de propriété
	 * 
	 * @return
	 */
	public SimpleDoubleProperty soldePrev2Property() {
		return _soldePrev2;
	}

	/**
	 * Retourne le montant prévu à la fin du troisieme mois du trimestre sous
	 * forme de propriété
	 * 
	 * @return
	 */
	public SimpleDoubleProperty soldePrev3Property() {
		return _soldePrev3;
	}

	/**
	 * Retourne le solde prévisionnel à la fin du 3eme mois du trimestre
	 * 
	 * @return
	 */
	public double getSoldePrev3() {
		return _soldePrev3.get();
	}

	@Override
	public Compte getDBObject() {
		return new Compte(getAppId(), getSolde(), getNom(), isLivret(), isBudget());
	}

}
