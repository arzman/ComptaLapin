package org.arthur.compta.lapin.application.model.template;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.arthur.compta.lapin.model.operation.Operation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Template d'un trimestre
 * 
 * @author Arthur
 *
 */
public class TrimestreTemplate {

	/**
	 * Template des opérations
	 */
	private final ObservableList<TrimestreTemplateElement> _elementList;

	/**
	 * Constructeur
	 */
	public TrimestreTemplate() {

		_elementList = FXCollections.observableArrayList();

	}

	public void generateTrimestre() {

	}

	/**
	 * Crée une liste d'opération hebdomadaire à partir d'elt de template
	 * 
	 * @param eltList
	 * @param year
	 *            l'année sur laquelle les opérations sont créées
	 * @param numMonthInYeah
	 *            mois sur lequel les opérations sont créées
	 * @return
	 */
	private List<Operation> createHebdoOperation(final List<TrimestreTemplateElement> eltList, final int year,
			final int numMonthInYeah) {

		final List<Operation> listToFill = new ArrayList<Operation>();

		for (final TrimestreTemplateElement elt : eltList) {

			if (elt.getFreq() == TrimestreTemplateElementFrequence.HEBDOMADAIRE) {

				// on se place au début du mois
				final Calendar cal = Calendar.getInstance();
				cal.set(year, numMonthInYeah, 1);

				// on compte le nombre de jour
				int compteur = 0;
				for (int i = 0; i < cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {

					cal.roll(Calendar.DAY_OF_MONTH, 1);
					// on obtient tombe sur le jour
					if (cal.get(Calendar.DAY_OF_WEEK) == elt.getOccurence()) {
						compteur++;
					}
				}

				for (int j = 0; j < compteur; j++) {

					//<<listToFill.add(elt.createOperation());

				}

			}

		}

		return listToFill;

	}

	/**
	 * Crée une liste d'opération mensuelle à partir des élements template passé
	 * en paramètre.
	 * 
	 * @param eltList
	 * @return
	 */
	private List<Operation> createMensuelOperation(final List<TrimestreTemplateElement> eltList) {

		final List<Operation> listToFill = new ArrayList<Operation>();

		for (final TrimestreTemplateElement elt : eltList) {

			if (elt.getFreq() == TrimestreTemplateElementFrequence.MENSUEL) {

				// listToFill.add(elt.createOperation());
			}

		}
		return listToFill;

	}

	/**
	 * Créée une liste d'opération trimestriel à partir d'un template
	 * 
	 * @param eltList
	 *            les opération template
	 * @param numMoi
	 *            le numéro de moi dans le trimestre qui aura l'opération
	 * @return
	 */
	private List<Operation> createTrimestrielOperation(final List<TrimestreTemplateElement> eltList, final int numMoi) {
		final List<Operation> listToFill = new ArrayList<Operation>();

		for (final TrimestreTemplateElement elt : eltList) {

			if (elt.getFreq() == TrimestreTemplateElementFrequence.TRIMESTRIEL) {

				if (elt.getOccurence() == numMoi) {

					// listToFill.add( elt.getOperation().clone());

				}
			}
		}

		return listToFill;
	}

	/**
	 * Ajout un élément dans le template
	 * @param elt
	 */
	public void addElement(TrimestreTemplateElement elt) {
		_elementList.add(elt);
		
	}

	/**
	 * Retourne la liste des éléments du template
	 * @return
	 */
	public ObservableList<TrimestreTemplateElement> getElements() {
		
		return _elementList;
	}

}
