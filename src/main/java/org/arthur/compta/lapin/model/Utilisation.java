package org.arthur.compta.lapin.model;

import java.time.LocalDate;

/**
 * Modélise une utilisation d'un budget. Soit une dépense (libellé) d'un montant
 * et une date
 *
 * Cette classe est utilisée directement pour être persistée
 */
public class Utilisation {

	/** id */
	private int _id;
	/** le montant */
	private double _montant;

	/** le libelle */
	private String _libelle;
	/** la date */
	private LocalDate _date;

	/**
	 * Constructeur de l'utilisation
	 * 
	 * @param montant le montant utilisé
	 * @param libelle le nom de l'utilisation
	 * @param date    la date de l'utilisation
	 */
	public Utilisation(int id, double montant, String libelle, LocalDate date) {

		_id = id;
		_montant = montant;
		_libelle = libelle;
		_date = date;
	}

	public double getMontant() {
		return _montant;
	}

	/**
	 * Retourne le libellé de l'utilisation
	 * 
	 * @return
	 */
	public String getLibelle() {
		return _libelle;
	}

	/**
	 * Retourne la date de l'utilisation
	 * 
	 * @return
	 */
	public LocalDate getDate() {
		return _date;
	}

	/**
	 * Retourne l'id
	 * 
	 * @return
	 */
	public int getId() {
		return _id;
	}

}
