
package org.arthur.compta.lapin.model;

/**
 * Modélise un compte. Il contient une somme d'argent qui peut être utilisé pour
 * les budget ou non.
 *
 */
public class Compte {

	/** Id en base */
	private int _id;
	/** Le solde du compte */
	private double _solde;
	/** Le nom du compte */
	private String _nom;
	/** Indique s'il s'agit d'un compte "livret" */
	private boolean _isLivret;
	/**
	 * Indique si la somme sur le compte peut être utilisé pour remplir les budgets
	 */
	private boolean _budgetAllowed;

	/**
	 * Constructeur par défaut
	 */
	public Compte(int id, double solde, String nom, boolean isLivret, boolean budgetAllowed) {
		_id = id;
		_nom = nom;
		_solde = solde;
		_isLivret = isLivret;
		_budgetAllowed = budgetAllowed;
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
	 * Retourne le solde du compte
	 * 
	 * @return le nom du compte
	 */
	public String getNom() {
		return _nom;
	}

	/**
	 * Retourne le solde du compte
	 * 
	 * @return le solde du compte
	 */
	public double getSolde() {
		return _solde;
	}

	/**
	 * Retourne si le compte peut remplir les budgets
	 * 
	 * @return True si le compte peut remplir les budgets
	 */
	public boolean isBudgetAllowed() {
		return _budgetAllowed;
	}

	/**
	 * Retourne s'il s'agit d'un compte livret
	 * 
	 * @return True si le compte est "livret"
	 */
	public boolean isLivret() {
		return _isLivret;
	}

}
