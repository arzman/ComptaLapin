package org.arthur.compta.lapin.presentation.budget.cellfactory;

import org.arthur.compta.lapin.presentation.budget.model.PresBudget;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public class MontantPresBudgetCellFactory
		implements Callback<TreeTableColumn<PresBudget, Number>, TreeTableCell<PresBudget, Number>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TreeTableCell<PresBudget, Number> call(TreeTableColumn<PresBudget, Number> param) {

		return new TreeTableCell<PresBudget, Number>() {

			@Override
			protected void updateItem(Number item, boolean empty) {
				// surcharge de la mise à jour du text
				super.updateItem(item, empty);

				setText(getTextFromItem(item, empty));

			}

		};
	}

	protected String getTextFromItem(Number item, boolean empty) {

		String res;
		if (empty || item == null) {
			res = "";
		} else {
			// on format le texte comme une valeur monnaitaire
			if (item.doubleValue() >= 0) {
				res = ApplicationFormatter.montantFormat.format(item);
			} else {
				res = "";
			}
		}

		return res;
	}

}
