package org.arthur.compta.lapin.presentation.budget.model;

import java.time.LocalDate;

import org.arthur.compta.lapin.application.model.AppBudget;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Encapsulation d'un AppBudget. Le But étant d'afficher les budget dans un
 * TreeView
 * 
 *
 */
public class PresBudget {

	/** Le budget */
	private AppBudget _appBudget;
	/** Un label */
	private SimpleStringProperty _nameProp;
	/** le montant */
	private SimpleDoubleProperty _montantProp;
	/** La date de récurrence */
	private SimpleObjectProperty<LocalDate> _dateProp;

	/**
	 * 
	 * @param appBudget
	 * @param name
	 */
	public PresBudget(AppBudget appBudget, String name) {

		_appBudget = appBudget;
		_nameProp = new SimpleStringProperty();
		_montantProp = new SimpleDoubleProperty();
		_dateProp = new SimpleObjectProperty<LocalDate>();

		if (_appBudget != null) {
			_nameProp.bind(appBudget.nomProperty());
			_montantProp.bind(_appBudget.objectifProperty());
			_dateProp.bind(_appBudget.dateRecurrentProp());
		} else {
			_nameProp.set(name);
			_montantProp.set(-1);
		}

	}

	public AppBudget getAppBudget() {
		return _appBudget;
	}

	public String getName() {
		return _nameProp.getValue();
	}

	public SimpleStringProperty nomProperty() {
		return _nameProp;
	}

	public SimpleDoubleProperty montantProperty() {
		return _montantProp;
	}

	public SimpleObjectProperty<LocalDate> dateRecurrentProp() {
		return _dateProp;
	}

}
