
package org.arthur.compta.lapin.model.operation;

import org.arthur.compta.lapin.model.Compte;

/**
 * Modélise une opération de transfert de fond entre deux comptes
 *
 */
public class TransfertOperation extends Operation {

	/**
	 * Le compte qui va recevoir le transfert
	 */
	private Compte _compteCible;

	/**
	 * Constructeur
	 * 
	 * @param prevision
	 * 
	 * @param ope
	 *            L'opération
	 * @param etat
	 *            l'état de prise en compte
	 * @param compteCible
	 */
	public TransfertOperation(Compte compteSource, String nom, double montant, EtatOperation etat, Compte compteCible) {

		super(OperationType.TRANSFERT, compteSource, nom, montant, etat);
		_compteCible = compteCible;

	}

	/**
	 * Retourne le compte cible
	 * 
	 * @return le compte cible
	 */
	public Compte getCompteCible() {
		return _compteCible;
	}

}
