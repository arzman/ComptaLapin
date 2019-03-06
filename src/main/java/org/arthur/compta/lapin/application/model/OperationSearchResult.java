package org.arthur.compta.lapin.application.model;

import java.time.LocalDate;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Objet permettant l'affichage d'un résultat de recherche d'opération
 *
 */
public class OperationSearchResult {

	/** Le libelle de l'opération */
	private SimpleStringProperty _libelleProp;
	/** Le montant de l'opération */
	private SimpleDoubleProperty _montantProp;
	/** Le mois de l'opération */
	private SimpleObjectProperty<LocalDate> _moisProp;

	/**
	 * Constructeur
	 * 
	 * @param libelle le libelle
	 * @param montant le montant
	 * @param date    le mois
	 */
	public OperationSearchResult(String libelle, double montant, LocalDate date) {

		// positionnement du libelle
		_libelleProp = new SimpleStringProperty(libelle);
		// positionnement du montant
		_montantProp = new SimpleDoubleProperty(montant);
		// positionnement du mois
		LocalDate cal = LocalDate.now();
		_moisProp = new SimpleObjectProperty<LocalDate>(cal);
	}

	/**
	 * Retourne le libelle sous forme de propriété
	 * 
	 * @return
	 */
	public SimpleStringProperty libelleProperty() {
		return _libelleProp;
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
	 * Retourne la date de début de l'exercice de l'opération
	 * 
	 * @return
	 */
	public SimpleObjectProperty<LocalDate> getMoisProperty() {
		return _moisProp;
	}

}
