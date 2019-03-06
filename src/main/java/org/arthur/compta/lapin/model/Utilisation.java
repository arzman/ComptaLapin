package org.arthur.compta.lapin.model;

import java.time.LocalDate;

/**
 * Modélise une utilisation d'un budget. Soit une dépense (libellé) d'un montant
 * et une date
 *
 */
public class Utilisation {

	/**
	 * le montant
	 */
	private double _montant;

	/**
	 * le libell�
	 */
	private String _libelle;

	/**
	 * la date
	 */
	private LocalDate _date;

	/**
	 * Constructeur de l'utilisation
	 * 
	 * @param montant le montant utilisé
	 * @param libelle le nom de l'utilisation
	 * @param date    la date de l'utilisation
	 */
	public Utilisation(double montant, String libelle, LocalDate date) {

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
	 * Positionne le libelle
	 * 
	 * @param nom
	 */
	public void setLibelle(String nom) {
		_libelle = nom;

	}

	/**
	 * Positionne le montant
	 * 
	 * @param montant
	 */
	public void setMontant(double montant) {
		_montant = montant;

	}

	/**
	 * Positionne la date
	 * 
	 * @param date
	 */
	public void setDate(LocalDate date) {
		_date = date;
	}
}
