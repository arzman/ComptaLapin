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
 * Cette classe est utilisé pour le mapping avec la base de donnée
 * 
 * @author Arthur
 * 
 */
public class Budget {

	/** id */
	private int _id;
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

	public Budget(int id, double objectif_, double montantUtilise, String nom, boolean isActif, int priority, String labelRec, LocalDate dateRecurrent) {

		_id = id;
		_objectif = objectif_;
		_montantUtilise = montantUtilise;
		_nom = nom;
		_isActif = isActif;
		_priority = priority;
		if (labelRec == null) {
			_labelRec = "";
		} else {
			_labelRec = labelRec;
		}

		if (dateRecurrent == null) {
			_dateRecurrent = LocalDate.of(1986, 6, 27);
		} else {
			_dateRecurrent = dateRecurrent;
		}

	}

	/**
	 * Retourne l'id
	 * 
	 * @return
	 */
	public int getId() {
		return _id;
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
	 * Retourne True si le budget est actif
	 * 
	 * @return
	 */
	public boolean isActif() {
		return _isActif;
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

}
