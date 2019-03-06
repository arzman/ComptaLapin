package org.arthur.compta.lapin.presentation.common.cellfactory;

import java.time.LocalDate;

import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Formatte une date sous forme mm/YYYY pour l'affichage dans un tableau
 *
 * @param <T>
 */
public class DateCellFactory<T> implements Callback<TableColumn<T, LocalDate>, TableCell<T, LocalDate>> {

	@Override
	public TableCell<T, LocalDate> call(TableColumn<T, LocalDate> param) {

		return new TableCell<T, LocalDate>() {

			@Override
			protected void updateItem(LocalDate item, boolean empty) {

				super.updateItem(item, empty);

				if (empty || item == null) {
					setText(null);
				} else {
					setText(ApplicationFormatter.databaseDateFormat.format(item));
				}

			}

		};
	}

}
