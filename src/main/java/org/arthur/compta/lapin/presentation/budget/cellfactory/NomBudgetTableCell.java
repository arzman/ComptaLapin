package org.arthur.compta.lapin.presentation.budget.cellfactory;

import org.arthur.compta.lapin.application.model.AppBudget;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

public class NomBudgetTableCell implements Callback<TableColumn<AppBudget, String>, TableCell<AppBudget, String>> {

	@Override
	public TableCell<AppBudget, String> call(TableColumn<AppBudget, String> arg0) {

		return new TableCell<AppBudget, String>() {

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
