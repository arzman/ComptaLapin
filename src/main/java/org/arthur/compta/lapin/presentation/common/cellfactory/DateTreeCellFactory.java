package org.arthur.compta.lapin.presentation.common.cellfactory;

import java.time.LocalDate;

import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

/**
 * Formatte une date sous forme mm/YYYY pour l'affichage dans un tableau
 *
 * @param <T>
 */
public class DateTreeCellFactory<T> implements Callback<TreeTableColumn<T, LocalDate>, TreeTableCell<T, LocalDate>> {

	@Override
	public TreeTableCell<T, LocalDate> call(TreeTableColumn<T, LocalDate> param) {

		return new TreeTableCell<T, LocalDate>() {

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
