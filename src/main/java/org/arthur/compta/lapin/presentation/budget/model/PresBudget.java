package org.arthur.compta.lapin.presentation.budget.model;

import org.arthur.compta.lapin.application.model.AppBudget;

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

	/**
	 * 
	 * @param appBudget
	 * @param name
	 */
	public PresBudget(AppBudget appBudget, String name) {

		_appBudget = appBudget;
		_nameProp = new SimpleStringProperty();

		if (_appBudget != null) {
			_nameProp.bind(appBudget.nomProperty());
		} else {
			_nameProp.set(name);
		}

	}

	public AppBudget getAppBudget() {
		return _appBudget;
	}

	public String getName() {
		return _nameProp.getName();
	}

	public SimpleStringProperty nomProperty() {
		return _nameProp;
	}

}
