package org.arthur.compta.lapin.application.template;

import org.arthur.compta.lapin.model.operation.OperationType;

/**
 * Element d'un template d'exercice mensuel Il s'agit d'une operation ayant une
 * fréquence donnée dans un mois
 * 
 * fréquence  --> occurence
 * hebdomadaire --> numéro du jour de la semaine
 * trimestriel --> numéro du mois dans le trimestre
 *
 */
public class TrimestreTemplateElement {

	/** La fréquence de l'opération */
	private TrimestreTemplateElementFrequence _freq;

	/** nom */
	private String nom;
	/** l'occurence */
	private int _occurence;
	/** montant de l'opération */
	private double _montant ;
	/** type de l'opération */
	private OperationType _type;
	/** id compte source */
	private String _idCompteSource;
	/** id compte cible */
	private String _idCompteCible;

	/**
	 * Constructeur
	 */
	public TrimestreTemplateElement() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Retourne la fréquence de l'opération
	 * 
	 * @return
	 */
	public TrimestreTemplateElementFrequence getFreq() {
		return _freq;
	}

	/**
	 * Retourne l'occurence
	 * 
	 * @return
	 */
	public int getOccurence() {
		return _occurence;
	}


	/**
	 * Positionne la fréquence de l'opération
	 * 
	 * @param freq
	 */
	public void setFreq(TrimestreTemplateElementFrequence freq) {
		_freq = freq;
	}

	/**
	 * Positionne l'occurence
	 * 
	 * @param occurence
	 */
	public void setOccurence(int occurence) {
		_occurence = occurence;
	}

	

	
}
