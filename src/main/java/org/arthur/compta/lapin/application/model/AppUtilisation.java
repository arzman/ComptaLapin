package org.arthur.compta.lapin.application.model;

import java.util.Calendar;

import org.arthur.compta.lapin.model.Utilisation;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Encapsulation applicative d'une Utilisation de Budget
 *
 */
public class AppUtilisation extends AppObject {

	/** L'utilisation métier */
	private Utilisation _utilisation;
	/** Le libellé */
	private SimpleStringProperty _nomProp;
	/** La date */
	private SimpleObjectProperty<Calendar> _dateProp;
	/** Le montant */
	private SimpleDoubleProperty _montantProp;

	/**
	 * Constructeur
	 * 
	 * @param utilisation
	 *            utilisation métier
	 */
	public AppUtilisation(Utilisation utilisation) {

		_utilisation = utilisation;
		_nomProp = new SimpleStringProperty(_utilisation.getLibelle());
		_dateProp = new SimpleObjectProperty<>(_utilisation.getDate());
		_montantProp = new SimpleDoubleProperty(_utilisation.getMontant());
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
	 * Retourne la date sous forme de propriété
	 * 
	 * @return
	 */
	public SimpleObjectProperty<Calendar> dateProperty() {
		return _dateProp;
	}

	/**
	 * Retourne le montant sous forme propriété
	 * 
	 * @return
	 */
	public SimpleDoubleProperty montantProperyt() {
		return _montantProp;
	}

}
