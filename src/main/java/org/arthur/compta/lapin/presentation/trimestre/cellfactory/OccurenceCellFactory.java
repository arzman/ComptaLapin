package org.arthur.compta.lapin.presentation.trimestre.cellfactory;

import java.text.DateFormatSymbols;

import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElementFrequence;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Combo de choix d'un occurrence. Suivant le choix de la fréquence, l'affichage
 * n'est pas le meme.
 *
 */
public class OccurenceCellFactory implements Callback<ListView<Integer>, ListCell<Integer>> {

	/** La combo de choix de la fréquence */
	private ComboBox<String> _freqCombo;

	public OccurenceCellFactory(ComboBox<String> freqCombo) {
		_freqCombo = freqCombo;
	}

	@Override
	public ListCell<Integer> call(ListView<Integer> param) {

		return new ListCell<Integer>() {
			@Override
			protected void updateItem(Integer item, boolean empty) {

				super.updateItem(item, empty);
				if (!empty) {

					if (_freqCombo.getSelectionModel().getSelectedItem()
							.equals(TrimestreTemplateElementFrequence.HEBDOMADAIRE.toString())) {

						// afichage du jour de la semaine
						setText(DateFormatSymbols.getInstance().getWeekdays()[item]);
					} else {
						//affichage du mois
						setText("Mois n°" + (item.intValue() + 1));
					}

				} else {
					setText(null);

				}
			}
		};

	}

}
