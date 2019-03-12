package org.arthur.compta.lapin.model.operation;

/**
 * Modélise une opération sur un compte. Une opération possède un état (
 * comptabilisé ou non) et un montant. Afin de distinguer une opération, on lui
 * attribut un nom
 * 
 * Cette classe est utilisé pour le mapping avec la BD
 */
public class Operation {

	/** l'id */
	private int _id;
	/** Etat de l'opération */
	private EtatOperation _etat;
	/** Le montant de l'opération */
	private double _montant;
	/** Le nom */
	protected String _nom;
	/** Le type d'operation */
	protected OperationType _type;
	/** Le compte sur lequel l'opération est faite */
	protected int _srcCompteId;

	/**
	 * Construction par défaut
	 */
	public Operation(int id, OperationType type, int srcCompteId, String nom, double montant, EtatOperation etat) {
		_id = id;
		_type = type;
		_etat = etat;
		_srcCompteId = srcCompteId;
		_nom = nom;
		_montant = montant;

	}

	/**
	 * Retourne le compte
	 * 
	 * @return le compte
	 */
	public int getCompteId() {
		return _srcCompteId;
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
	 * Duplique l'opération
	 * 
	 * @return
	 */
	public Operation duplicate() {

		return new Operation(_id, _type, _srcCompteId, _nom, _montant, _etat);

	}

}
