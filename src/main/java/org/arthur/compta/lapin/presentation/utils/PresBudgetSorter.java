package org.arthur.compta.lapin.presentation.utils;

import org.arthur.compta.lapin.presentation.budget.model.PresBudget;

import java.util.Comparator;

public class PresBudgetSorter implements Comparator<PresBudget> {

@Override
public int compare(PresBudget o1, PresBudget o2) {
if (o1.getAppBudget() == null && o2.getAppBudget() == null) {
return o1.getName().compareTo(o2.getName());
}
if (o1.getAppBudget() == null) return -1;
if (o2.getAppBudget() == null) return 1;
return -o1.getAppBudget().getDateRecurrent().compareTo(o2.getAppBudget().getDateRecurrent());
}

}
