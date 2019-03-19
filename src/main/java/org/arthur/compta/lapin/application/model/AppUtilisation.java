package org.arthur.compta.lapin.application.model;

import java.time.LocalDate;

import org.arthur.compta.lapin.model.Utilisation;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Encapsulation applicative d'une Utilisation de Budget
 *
 */
public class AppUtilisation extends AppObject<Utilisation> {

	/** Le libellé */
	private SimpleStringProperty _nomProp;
	/** La date */
	private SimpleObjectProperty<LocalDate> _dateProp;
	/** Le montant */
	private SimpleDoubleProperty _montantProp;

	/**
	 * Constructeur
	 * 
	 * @param utilisation
	 *            utilisation métier
	 */
	public AppUtilisation(Utilisation utilisation) {

		setAppID(utilisation.getId());
		_nomProp = new SimpleStringProperty(utilisation.getLibelle());
		_dateProp = new SimpleObjectProperty<>(utilisation.getDate());
		_montantProp = new SimpleDoubleProperty(utilisation.getMontant());
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
	public SimpleObjectProperty<LocalDate> dateProperty() {
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
	 * @param montant
	 */
	public void setMontant(double montant) {
		_montantProp.set(montant);

	}

	/**
	 * Positionne la date
	 * 
	 * @param date
	 */
	public void setDate(LocalDate date) {
		_dateProp.set(date);

	}

	/**
	 * Retourne le nom
	 * 
	 * @return
	 */
	public String getNom() {
		return _nomProp.get();
	}

	/**
	 * Retourne le montant
	 * 
	 * @return
	 */
	public double getMontant() {
		return _montantProp.get();
	}

	/**
	 * Retourne la date
	 * 
	 * @return
	 */
	public LocalDate getDate() {
		return _dateProp.get();
	}

	@Override
	public Utilisation getDBObject() {

		return new Utilisation(getAppId(), getMontant(), getNom(), getDate());
	}

}
