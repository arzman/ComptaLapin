package org.arthur.compta.lapin.presentation.utils;

import java.util.Comparator;

import org.arthur.compta.lapin.presentation.budget.model.PresBudget;

import javafx.scene.control.TreeItem;

public class PresBudgetSorter implements Comparator<TreeItem<PresBudget>> {

	@Override
	public int compare(TreeItem<PresBudget> o1, TreeItem<PresBudget> o2) {

		int res = 0;

		PresBudget pb1 = o1.getValue();
		PresBudget pb2 = o2.getValue();

		if (pb1.getAppBudget() == null && pb2.getAppBudget() == null) {

			res = pb1.getName().compareTo(pb2.getName());
		} else {

			res = -pb1.getAppBudget().getDateRecurrent().compareTo(pb2.getAppBudget().getDateRecurrent());

		}

		return res;
	}

}
