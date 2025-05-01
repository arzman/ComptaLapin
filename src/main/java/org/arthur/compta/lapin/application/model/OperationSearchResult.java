package org.arthur.compta.lapin.application.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

/**
 * Objet permettant l'affichage d'un résultat de recherche d'opération
 *
 */
public class OperationSearchResult {

	/** Le libelle de l'opération */
	private final SimpleStringProperty _libelleProp;
	/** Le montant de l'opération */
	private final SimpleDoubleProperty _montantProp;
	/** Le mois de l'opération */
	private final SimpleObjectProperty<LocalDate> _moisProp;

	/**
	 * Constructeur
	 * 
	 * @param libelle
	 *            le libelle
	 * @param montant
	 *            le montant
	 * @param date
	 *            le mois
	 */
	public OperationSearchResult(String libelle, double montant, LocalDate date) {

		// positionnement du libelle
		_libelleProp = new SimpleStringProperty(libelle);
		// positionnement du montant
		_montantProp = new SimpleDoubleProperty(montant);
		// positionnement du mois
		_moisProp = new SimpleObjectProperty<LocalDate>(date);
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
