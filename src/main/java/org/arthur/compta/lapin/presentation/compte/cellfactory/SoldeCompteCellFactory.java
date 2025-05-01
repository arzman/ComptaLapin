package org.arthur.compta.lapin.presentation.compte.cellfactory;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

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

					if (item.doubleValue() < 0) {
						setTextFill(Color.RED);
					} else {
						setTextFill(Color.BLUE);
					}

					setText(ApplicationFormatter.montantFormat.format(item));
				}
			}

		};
	}

}
