package org.arthur.compta.lapin.presentation.common.cellfactory;

import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Usine à cellule permettant de gérer l'affichage d'un somme monétaire dans un
 * tableau.
 */
public class MontantCellFactory<T> implements Callback<TableColumn<T, Number>, TableCell<T, Number>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TableCell<T, Number> call(TableColumn<T, Number> param) {

		return new TableCell<T, Number>() {

			@Override
			protected void updateItem(Number item, boolean empty) {
				// surcharge de la mise à jour du text
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					// on format le texte comme une valeur monnaitaire
					setText(ApplicationFormatter.montantFormat.format(item));
				}
			}

		};
	}

}
