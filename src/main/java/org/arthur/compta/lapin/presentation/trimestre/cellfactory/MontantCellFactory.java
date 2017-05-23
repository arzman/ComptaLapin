package org.arthur.compta.lapin.presentation.trimestre.cellfactory;

import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Usine à cellule permettant de gérer l'affichage d'un somme monétaire dans un
 * tableau.
 *
 */
public class MontantCellFactory<IMontant>
		implements Callback<TableColumn<IMontant, Number>, TableCell<IMontant, Number>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TableCell<IMontant, Number> call(TableColumn<IMontant, Number> param) {

		return new TableCell<IMontant, Number>() {

			@Override
			protected void updateItem(Number item, boolean empty) {
				// surcharge de la mise à jour du text
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
				} else {
					// on format le texte comme une valeur monnaitaire
					setText(ApplicationFormatter.montantFormat.format(item));
				}
			}

		};
	}

}
