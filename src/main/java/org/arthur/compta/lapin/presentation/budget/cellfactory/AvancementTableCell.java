package org.arthur.compta.lapin.presentation.budget.cellfactory;

import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
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
					CustomizedProgressBar progress = new CustomizedProgressBar();
					// progress.setStyle(style);
					progress._percentProp.set(val);
					setGraphic(progress);

				}
			}

		};

	}
	/**
	 * 
	 * ProgressBar customisée : affichage de la progression en % et changement de couleur suivant la valeur
	 * 
	 *
	 */
	private class CustomizedProgressBar extends StackPane {

		final private ProgressBar bar = new ProgressBar();
		final private Text text = new Text();
		final SimpleDoubleProperty _percentProp;

		final private static int DEFAULT_LABEL_PADDING = 5;

		public CustomizedProgressBar() {

			_percentProp = new SimpleDoubleProperty(0);

			syncProgress();
			_percentProp.addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					syncProgress();

				}
			});

			bar.setMaxWidth(Double.MAX_VALUE); 
			getChildren().setAll(bar, text);
		}

		// synchronizes the progress indicated with the work done.
		private void syncProgress() {

			double val = _percentProp.doubleValue();

			if (val < 0) {
				text.setText(ApplicationFormatter.pourcentFormat.format(0));
				bar.setProgress(0);
			} else {
				text.setText(ApplicationFormatter.pourcentFormat.format(val));
				bar.setProgress(val);
			}

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
						style = "-fx-accent: #3AF24B;";
					}
				}
			}
			bar.setStyle(style);

			bar.setMinHeight(text.getBoundsInLocal().getHeight() + DEFAULT_LABEL_PADDING * 2);
			bar.setMinWidth(text.getBoundsInLocal().getWidth() + DEFAULT_LABEL_PADDING * 2);
		}
	}

}
