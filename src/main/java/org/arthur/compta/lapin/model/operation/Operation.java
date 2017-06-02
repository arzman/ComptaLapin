package org.arthur.compta.lapin.model.operation;

import org.arthur.compta.lapin.model.Compte;

/**
 * Modélise une opération sur un compte. Une opération possède un état (
 * comptabilisé ou non) et un montant. Afin de distinguer une opération, on lui
 * attribut un nom
 */
public class Operation {

	/**
	 * Etat de l'opération
	 */
	private EtatOperation _etat;
	/**
	 * Le montant de l'opération
	 */
	private double _montant;

	/**
	 * Le nom
	 */
	protected String _nom;

	/**
	 * Le type d'operation
	 */
	protected OperationType _type;

	/**
	 * Le compte sur lequel l'opération est faite
	 */
	protected Compte _compte;

	/**
	 * Construction par défaut
	 */
	public Operation(OperationType type, Compte leCompte, String nom, double montant, EtatOperation etat) {
		setEtat(etat);
		_type = type;
		setCompte(leCompte);
		setNom(nom);
		setMontant(montant);

	}

	/**
	 * Retourne le compte
	 * 
	 * @return le compte
	 */
	public Compte getCompte() {
		return _compte;
	}

	/**
	 * Retourne l'état de l'opération
	 * 
	 * @return
	 */
	public EtatOperation getEtat() {
		return _etat;
	}

	/**
	 * Retourne le montant de l'opération
	 * 
	 * @return le montant de l'opération
	 */
	public double getMontant() {
		return _montant;
	}

	/**
	 * Retourne le nom de l'opération
	 * 
	 * @return le nom de l'opération
	 */
	public String getNom() {
		return _nom;
	}

	/**
	 * Retourne le type de l'opération
	 * 
	 * @return le type de l'opération
	 */
	public OperationType getType() {
		return _type;
	}

	/**
	 * Modifie le compte lié à l'opération
	 * 
	 * @param compte
	 *            le nouveau compte
	 */
	public void setCompte(Compte compte) {
		_compte = compte;
	}

	/**
	 * @param etat
	 *            the etat to set
	 */
	public void setEtat(EtatOperation etat) {
		_etat = etat;
	}

	/**
	 * @param montant
	 *            the montant to set
	 */
	public void setMontant(double montant) {
		_montant = montant;
	}

	/**
	 * @param nom
	 *            the nom to set
	 */
	public void setNom(String nom) {
		_nom = nom;
	}
	
	/**
	 * Duplique l'opération
	 * @return
	 */
	public Operation duplicate(){
		
		Operation ope = new Operation(_type, _compte, _nom, _montant, _etat);
		return ope;
		
	}

}
