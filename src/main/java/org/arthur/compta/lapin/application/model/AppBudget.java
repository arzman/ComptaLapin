package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.model.Budget;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class AppBudget extends AppObject {

	/** Le budget métier encapsulé */
	private Budget _budget;

	/** Le nom */
	private SimpleStringProperty _nompProp;
	/** L'objectif */
	private SimpleDoubleProperty _objectifProp;
	/** L'avancement */
	private SimpleDoubleProperty _avancementProp;
	/** Le montant sur compte courant */
	private SimpleDoubleProperty _montantCourantProp;
	/** Le montant sur compte livret */
	private SimpleDoubleProperty _montantLivretProp;
	/** est actif ou pas */
	private SimpleBooleanProperty _isActifProp;
	/** Le montant utilise */
	private SimpleDoubleProperty _montantUtiliseProp;

	/**
	 * Consructeur par défaut
	 */
	public AppBudget(Budget budget) {

		_budget = budget;

		// caractéristique propre des budgets
		_nompProp = new SimpleStringProperty(budget.getNom());
		_objectifProp = new SimpleDoubleProperty(budget.getObjectif());
		_montantUtiliseProp = new SimpleDoubleProperty(budget.getMontantUtilise());
		_isActifProp = new SimpleBooleanProperty(budget.isActif());

		// a calculer par l'application
		_avancementProp = new SimpleDoubleProperty();
		_montantCourantProp = new SimpleDoubleProperty();
		_montantLivretProp = new SimpleDoubleProperty();

	}

	/**
	 * Retourne le nom sous forme de propriété
	 * 
	 * @return
	 */
	public SimpleStringProperty nomProperty() {
		return _nompProp;
	}

	/**
	 * Retourne le nom
	 * 
	 * @return
	 */
	public String getNom() {
		return _nompProp.get();
	}

	/**
	 * Positionne le nom
	 * 
	 * @param nom
	 */
	public void setNom(String nom) {

		_nompProp.set(nom);
		_budget.setNom(nom);

	}

	/**
	 * Retourne l'objectif sous forme de propriété
	 * 
	 * @return
	 */
	public SimpleDoubleProperty objectifProperty() {

		return _objectifProp;
	}

	/**
	 * Retourne l'objectif du budget
	 * 
	 * @param objectif
	 */
	public void setObjectif(double objectif) {
		_objectifProp.set(objectif);
		_budget.setObjectif(objectif);

	}

	/**
	 * Retourne l'objectif
	 * 
	 * @return
	 */
	public double getObjectif() {
		return _objectifProp.doubleValue();
	}

	/**
	 * Retourne l'avancement du budget en %
	 * 
	 * @return
	 */
	public SimpleDoubleProperty avancementProperty() {
		return _avancementProp;
	}

	/**
	 * Positionne l'avancement
	 * 
	 * @param av
	 */
	public void setAvancement(double av) {
		_avancementProp.set(av);

	}

	/**
	 * Retourne le montant sur compte courant du budget
	 * 
	 * @return
	 */
	public SimpleDoubleProperty montantCourantProperty() {
		return _montantCourantProp;
	}

	/**
	 * Positionne le montant sur solde courant
	 * 
	 * @param cour
	 */
	public void setMontantCourant(double cour) {
		_montantCourantProp.set(cour);

	}

	/**
	 * Retourne le montant sur compte livret du budget
	 * 
	 * @return
	 */
	public SimpleDoubleProperty montantLivretProperty() {
		return _montantLivretProp;
	}

	/**
	 * Retourne le montant utilisé du budget
	 * 
	 * @return
	 */
	public double getMontantUtilise() {

		return _montantUtiliseProp.get();
	}

	/**
	 * Positionne le montant utilise
	 * 
	 * @param utilise
	 */
	public void setMontantUtilise(double utilise) {
		_montantUtiliseProp.set(utilise);
		_budget.setMontantUtilise(utilise);

	}

	/**
	 * Positionne le montant sur compte livret
	 * 
	 * @param liv
	 */
	public void setMontantLivret(double liv) {
		montantLivretProperty().set(liv);

	}

	/**
	 * Retourne true si le budget est actif
	 * 
	 * @return
	 */
	public boolean isActif() {

		return _isActifProp.get();
	}

	/**
	 * Positionne l'état d'activation du buget
	 * 
	 * @param isActif
	 */
	public void setIsActif(boolean isActif) {
		_isActifProp.set(isActif);
		_budget.setIsActif(isActif);

	}

}