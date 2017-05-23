package org.arthur.compta.lapin.application.model.template;

import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.IMontant;
import org.arthur.compta.lapin.model.operation.OperationType;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Element d'un template d'exercice mensuel Il s'agit d'une operation ayant une
 * fréquence donnée dans un mois
 * 
 * fréquence --> occurence hebdomadaire --> numéro du jour de la semaine
 * trimestriel --> numéro du mois dans le trimestre
 *
 */
public class TrimestreTemplateElement implements IMontant {

	/** La fréquence de l'opération */
	private SimpleStringProperty _freqProp;

	/** nom */
	private SimpleStringProperty _nomProp;
	/** l'occurence */
	private SimpleIntegerProperty _occurenceProp;
	/** montant de l'opération */
	private SimpleDoubleProperty _montantProp;
	/** type de l'opération */
	private SimpleStringProperty _typeProp;
	/** compte source */
	private SimpleObjectProperty<AppCompte> _compteSource;
	/** id compte cible */
	private SimpleObjectProperty<AppCompte> _compteCible;

	/**
	 * Constructeur
	 */
	public TrimestreTemplateElement() {
		_nomProp = new SimpleStringProperty();
		_montantProp = new SimpleDoubleProperty();
		_typeProp = new SimpleStringProperty();
		_freqProp = new SimpleStringProperty();
		_occurenceProp = new SimpleIntegerProperty();
		_compteSource = new SimpleObjectProperty<AppCompte>();
		_compteCible = new SimpleObjectProperty<AppCompte>();
		
	}

	/**
	 * Retourne la fréquence de l'opération
	 * 
	 * @return
	 */
	public TrimestreTemplateElementFrequence getFreq() {
		return TrimestreTemplateElementFrequence.valueOf(_freqProp.get());
	}

	/**
	 * Retourne l'occurence
	 * 
	 * @return
	 */
	public int getOccurence() {
		return _occurenceProp.get();
	}

	/**
	 * Positionne la fréquence de l'opération
	 * 
	 * @param freq
	 */
	public void setFreq(TrimestreTemplateElementFrequence freq) {
		_freqProp.set(freq.toString());
	}

	/**
	 * Positionne l'occurence
	 * 
	 * @param occurence
	 */
	public void setOccurence(int occurence) {
		_occurenceProp.set(occurence);
	}

	/**
	 * Positionne le nom
	 * 
	 * @param nom
	 */
	public void setNom(String nom) {
		_nomProp.set(nom);

	}

	/**
	 * Positionne le montant
	 * 
	 * @param parseDouble
	 */
	public void setMontant(double montant) {
		_montantProp.set(montant);
	}

	/**
	 * Positionne le type
	 * 
	 * @param type
	 */
	public void setType(OperationType type) {
		_typeProp.set(type.toString());

	}

	/**
	 * Positionne le compte source
	 * 
	 * @param idCompte
	 */
	public void setCompteSource(AppCompte compte) {
		_compteSource.set(compte);
		;

	}

	/**
	 * Positionne l'id du compte cible Utilisé uniquement dans le cas d'un
	 * transfert
	 * 
	 * @param compte
	 */
	public void setCompteCible(AppCompte compte) {
		_compteCible.set(compte);

	}

	/**
	 * Retourne le nom sous forme de propriété
	 * 
	 * @return
	 */
	public SimpleStringProperty nomProperty() {

		return _nomProp;
	}

	/**
	 * Retourne le montant sous forme de propriété
	 * 
	 * @return
	 */
	public SimpleDoubleProperty montantProperty() {
		return _montantProp;
	}

	/**
	 * Retourne le type sous forme de propriété
	 * 
	 * @return
	 */
	public SimpleStringProperty typeProperty() {
		return _typeProp;
	}

	/**
	 * Retourne la fréquence sous forme de propriété
	 * 
	 * @return
	 */
	public SimpleStringProperty frequenceProperty() {

		return _freqProp;
	}

	/**
	 * Retourne le compte source sous forme de propriété
	 * 
	 * @return
	 */
	public SimpleObjectProperty<AppCompte> compteSourceProperty() {
		return _compteSource;
	}

	/**
	 * Retourne le compte cible sous forme de propriété
	 * 
	 * @return
	 */
	public SimpleObjectProperty<AppCompte> compteCibleProperty() {
		return _compteCible;
	}

	public SimpleIntegerProperty occurenceProperty() {
		return _occurenceProp;
	}

}
