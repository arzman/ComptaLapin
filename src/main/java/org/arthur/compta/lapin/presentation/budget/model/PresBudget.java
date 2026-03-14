package org.arthur.compta.lapin.presentation.budget.model;

import org.arthur.compta.lapin.application.model.AppBudget;

import java.time.LocalDate;

/**
 * Encapsulation d'un AppBudget pour la présentation
 */
public class PresBudget {

    /** Le budget */
    private final AppBudget _appBudget;
    /** Un label */
    private final String _name;

    public PresBudget(AppBudget appBudget, String name) {
        _appBudget = appBudget;
        _name = name;
    }

    public AppBudget getAppBudget() {
        return _appBudget;
    }

    public String getName() {
        return _appBudget != null ? _appBudget.getNom() : _name;
    }

    public double getMontant() {
        return _appBudget != null ? _appBudget.getObjectif() : -1;
    }

    public LocalDate getDateRecurrent() {
        return _appBudget != null ? _appBudget.getDateRecurrent() : null;
    }

}
