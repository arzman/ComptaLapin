package org.arthur.compta.lapin.presentation.budget.cellfactory;

import org.arthur.compta.lapin.presentation.budget.model.PresBudget;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

public class NomPresBudgetTableCell
		implements Callback<TreeTableColumn<PresBudget, String>, TreeTableCell<PresBudget, String>> {

	@Override
	public TreeTableCell<PresBudget, String> call(TreeTableColumn<PresBudget, String> arg0) {

		return new TreeTableCell<PresBudget, String>() {

			@Override
			protected void updateItem(String item, boolean empty) {
				// surcharge de la mise à jour du text
				super.updateItem(item, empty);

				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				} else {

					// le nom est de la forme nomBudget#true : le boolean indique si le budget est
					// terminé

					String[] splitted = item.split("#");
					setText(splitted[0]);

					if (Boolean.valueOf(splitted[1])) {

						setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 12));
						setTextFill(Color.DARKSLATEGREY);

					} else {

						setFont(Font.getDefault());

					}

				}
			}

		};

	}

}
