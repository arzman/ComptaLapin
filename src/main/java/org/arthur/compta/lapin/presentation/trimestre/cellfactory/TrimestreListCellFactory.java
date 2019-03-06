package org.arthur.compta.lapin.presentation.trimestre.cellfactory;

import java.time.LocalDate;
import java.util.HashMap;

import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Callback permettant de personaliser l'affichage de l'ihm de sélection du
 * trimestre courant. Pour chaque trimestre ( l'id du trimestre) on affichage sa
 * date de début et fin.
 *
 */
public class TrimestreListCellFactory implements Callback<ListView<String>, ListCell<String>> {

	/**
	 * Map permettant de liée l'id du trimstre à sa date de début
	 */
	private HashMap<String, LocalDate> _resumeTrimestre;

	/**
	 * Constructeur
	 * 
	 * @param resumeTrimestre les id des trimestres et leur date de début
	 */
	public TrimestreListCellFactory(HashMap<String, LocalDate> resumeTrimestre) {

		_resumeTrimestre = resumeTrimestre;

	}

	/**
	 * @see javafx.util.Callback#call(java.lang.Object)
	 */
	@Override
	public ListCell<String> call(ListView<String> param) {

		ListCell<String> cell = new ListCell<String>() {

			@Override
			protected void updateItem(String item, boolean empty) {

				super.updateItem(item, empty);

				if (!empty) {

					// De <mois année de début> à < mois année de fin>
					StringBuilder text = new StringBuilder("De ");
					LocalDate deb = _resumeTrimestre.get(item);
					text.append(ApplicationFormatter.moiAnneedateFormat.format(deb));
					text.append(" à ");

					text.append(ApplicationFormatter.moiAnneedateFormat.format(deb.plusMonths(2)));
					setText(text.toString());

				} else {
					// pas de donnée = pas de texte
					setText("");
				}

			}
		};

		return cell;
	}

}
