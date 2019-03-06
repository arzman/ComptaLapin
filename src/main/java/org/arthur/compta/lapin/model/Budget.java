/**
 * 
 */
package org.arthur.compta.lapin.model;

import java.time.LocalDate;

/**
 * Modélise un Budget. Soit un montant à atteindre et qui peut être utilisé. On
 * doit garder en mémoire les Utilisation qui ont été faite.
 * 
 * Un budget récurrent est un qui sera rattaché a un label et une date. Cette
 * fonctionnalité permet de suivre l'evolution des budgets qui ont le meme label
 * 
 * @author Arthur
 * 
 */
public class Budget {

	/** Montant totale du bugdet */
	private double _objectif;

	/** Montant déjà utilisé */
	private double _montantUtilise;

	/** Le nom du budget */
	private String _nom;

	/** Vrai si le budget est actif */
	private boolean _isActif;

	/** La priorité du budget */
	private int _priority;

	/** Label recurrent */
	private String _labelRec;

	/** Date récurrente */
	private LocalDate _dateRecurrent;

	/**
	 * Constructeur
	 */
	public Budget() {

		_objectif = 0;
		_montantUtilise = 0;
		_nom = "Nouveau Budget";
		_isActif = true;
		_priority = 0;
		_labelRec = "";
	}

	/**
	 * @return the montantUtilise
	 */
	public double getMontantUtilise() {
		return _montantUtilise;
	}

	public String getNom() {
		return _nom;
	}

	public double getObjectif() {
		return _objectif;
	}

	/**
	 * @param montantUtilise the montantUtilise to set
	 */
	public void setMontantUtilise(double montantUtilise) {
		_montantUtilise = montantUtilise;
	}

	/**
	 * Modifie le nom du budget
	 * 
	 * @param nom le nouveau nom
	 */
	public void setNom(String nom) {
		_nom = nom;
	}

	/**
	 * Modifie le montant de l'objectif
	 * 
	 * @param montantTotal le nouveau montant
	 */
	public void setObjectif(double montantTotal) {
		_objectif = montantTotal;
	}

	/**
	 * Retourne True si le budget est actif
	 * 
	 * @return
	 */
	public boolean isActif() {
		return _isActif;
	}

	/**
	 * Active ou non le budget
	 * 
	 * @param isActif
	 */
	public void setIsActif(boolean isActif) {
		_isActif = isActif;

	}

	/**
	 * Positionne la priorité du budget
	 * 
	 * @param prio
	 */
	public void setPriority(int prio) {
		_priority = prio;

	}

	/**
	 * Retourne la priorité
	 * 
	 * @return
	 */
	public int getPriority() {

		return _priority;
	}

	/**
	 * Retourne le label recurrent
	 * 
	 * @return
	 */
	public String getLabelRecurrent() {

		return _labelRec;
	}

	/***
	 * Retourne la date récurrente
	 * 
	 * @return
	 */
	public LocalDate getDateRecurrent() {
		return _dateRecurrent;
	}

	public void setLabelRecurrent(String labelRecurrent) {
		_labelRec = labelRecurrent;

	}

	public void setDateRecurrent(LocalDate dateRecurrent) {
		_dateRecurrent = dateRecurrent;

	}

}
