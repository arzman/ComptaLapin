package org.arthur.compta.lapin.presentation.trimestre.cellfactory;

import java.util.Calendar;
import java.util.Date;
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
	private HashMap<String, Calendar> _resumeTrimestre;

	/**
	 * Constructeur
	 * 
	 * @param resumeTrimestre
	 *            les id des trimestres et leur date de début
	 */
	public TrimestreListCellFactory(HashMap<String, Calendar> resumeTrimestre) {

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
					Calendar deb = _resumeTrimestre.get(item);
					text.append(ApplicationFormatter.moiAnneedateFormat.format(deb.getTime()));
					text.append(" à ");
					Calendar fin = Calendar.getInstance();
					fin.setTime(new Date(deb.getTime().getTime()));
					fin.add(Calendar.MONTH, 2);
					text.append(ApplicationFormatter.moiAnneedateFormat.format(fin.getTime()));
					setText(text.toString());

				}else{
					// pas de donnée = pas de texte
					setText("");
				}

			}
		};

		return cell;
	}

}
