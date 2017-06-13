package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.model.Budget;

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

	/**
	 * Consructeur par défaut
	 */
	public AppBudget(Budget budget) {

		_budget = budget;

		// caractéristique propre des budgets
		_nompProp = new SimpleStringProperty(budget.getNom());
		_objectifProp = new SimpleDoubleProperty(budget.getObjectif());

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
	 * Retourne l'objectif sous forme de propriété
	 * 
	 * @return
	 */
	public SimpleDoubleProperty objectifProperty() {

		return _objectifProp;
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

		return _budget.getMontantUtilise();
	}

	/**
	 * Positionne le montant sur compte livret
	 * 
	 * @param liv
	 */
	public void setMontantLivret(double liv) {
		montantLivretProperty().set(liv);

	}

}
