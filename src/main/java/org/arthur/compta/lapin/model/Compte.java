
package org.arthur.compta.lapin.model;

/**
 * Modélise un compte. Il contient une somme d'argent qui peut être utilisé pour
 * les budget ou non.
 *
 */
public class Compte {

	/**
	 * Le solde du compte
	 */
	private double _solde;

	/**
	 * Le nom du compte
	 */
	private String _nom;

	/**
	 * Indique s'il s'agit d'un compte "livret"
	 */
	private boolean _isLivret;

	/**
	 * Indique si la somme sur le compte peut être utilisé pour remplir les budgets
	 */
	private boolean _budgetAllowed;

	/**
	 * Constructeur par défaut
	 */
	public Compte() {
		_nom = "";
		setSolde(0.0);
		_isLivret = false;
		_budgetAllowed = true;
	}

	/**
	 * Constructeur du Compte
	 * 
	 * @param aNom le nom du Compte
	 */
	public Compte(String aNom) {
		this();
		setNom(aNom);

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

	/**
	 * Modifie l'autorisation de remplissage des budgets
	 * 
	 * @param budgetAllowed True si le compte est autorisé à remplir les budgets
	 */
	public void setBudgetAllowed(boolean budgetAllowed) {
		_budgetAllowed = budgetAllowed;
	}

	/**
	 * Modifie le caractère "livret" du compte
	 * 
	 * @param isLivret True si le compte est un "livret"
	 */
	public void setLivret(boolean isLivret) {
		_isLivret = isLivret;
	}

	/**
	 * Modifie le nom du compte
	 * 
	 * @param nom le nouveau nom
	 */
	public void setNom(String nom) {
		_nom = nom;
	}

	/**
	 * Modifie le solde du compte
	 * 
	 * @param _olde le nouveau solde
	 */
	public void setSolde(double solde) {
		_solde = solde;
	}

}
