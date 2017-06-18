package org.arthur.compta.lapin.presentation.common.cellfactory;

import java.util.Calendar;

import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Formatte une date sous forme mm/YYYY pour l'affichage dans un tableau
 *
 * @param <T>
 */
public class DateCellFactory<T> implements Callback<TableColumn<T, Calendar>, TableCell<T, Calendar>> {

	@Override
	public TableCell<T, Calendar> call(TableColumn<T, Calendar> param) {

		return new TableCell<T, Calendar>() {

			@Override
			protected void updateItem(Calendar item, boolean empty) {

				super.updateItem(item, empty);

				if (empty || item == null) {
					setText(null);
				} else {
					setText(ApplicationFormatter.databaseDateFormat.format(item.getTime()));
				}

			}

		};
	}

}
