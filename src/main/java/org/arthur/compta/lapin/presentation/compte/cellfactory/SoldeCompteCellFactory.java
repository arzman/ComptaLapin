package org.arthur.compta.lapin.presentation.compte.cellfactory;

import java.text.NumberFormat;
import java.util.Locale;

import org.arthur.compta.lapin.application.model.AppCompte;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Usine à cellule permettant de gérer l'affichage d'un somme monétaire dans un
 * tableau.
 *
 */
public class SoldeCompteCellFactory implements Callback<TableColumn<AppCompte, Number>, TableCell<AppCompte, Number>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TableCell<AppCompte, Number> call(TableColumn<AppCompte, Number> param) {

		return new TableCell<AppCompte, Number>() {

			@Override
			protected void updateItem(Number item, boolean empty) {
				// surcharge de la mise à jour du text
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
				} else {
					// on format le texte comme une valeur monnaitaire
					setText(NumberFormat.getCurrencyInstance(Locale.FRANCE).format(item));
				}
			}

		};
	}

}
