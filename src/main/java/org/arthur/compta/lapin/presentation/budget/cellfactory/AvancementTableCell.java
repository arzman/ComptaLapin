package org.arthur.compta.lapin.presentation.budget.cellfactory;

import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class AvancementTableCell implements Callback<TableColumn<AppBudget, Number>, TableCell<AppBudget, Number>> {

	@Override
	public TableCell<AppBudget, Number> call(TableColumn<AppBudget, Number> param) {

		return new TableCell<AppBudget, Number>() {

			@Override
			protected void updateItem(Number item, boolean empty) {
				// surcharge de la mise à jour du text
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				} else {
					double val = item.doubleValue();

					// on format le texte comme une valeur monnaitaire
					ProgressBar progress = new ProgressBar();
					String style;

					if (val < 0.25) {
						style = "-fx-accent: red;";
					} else {
						if (val < 0.5) {
							style = "-fx-accent: orange;";
						} else {
							if (val < 0.75) {
								style = "-fx-accent: yellow;";
							} else {
								style = "-fx-accent: green;";
							}
						}
					}
					
					progress.setStyle(style);
					progress.setProgress(val);
					setGraphic(progress);
					
					setText(ApplicationFormatter.pourcentFormat.format(val));

				}
			}

		};

	}

}
