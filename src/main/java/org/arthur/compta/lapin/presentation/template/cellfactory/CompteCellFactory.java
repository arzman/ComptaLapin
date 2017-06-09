package org.arthur.compta.lapin.presentation.template.cellfactory;

import org.arthur.compta.lapin.application.model.AppCompte;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Usine à cellule permettant de gérer l'affichage d'un compte dans tableau.
 *
 */
public class CompteCellFactory<T>
		implements Callback<TableColumn<T, AppCompte>, TableCell<T, AppCompte>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TableCell<T, AppCompte> call(TableColumn<T, AppCompte> param) {

		return new TableCell<T, AppCompte>() {

			@Override
			protected void updateItem(AppCompte item, boolean empty) {
				// surcharge de la mise à jour du text
				super.updateItem(item, empty);
				if (item==null || empty) {
					setText(null);
				} else {
					// on affiche son nom
					setText(item.getNom());
				}
			}

		};
	}

}
