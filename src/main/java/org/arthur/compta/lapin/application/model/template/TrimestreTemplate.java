package org.arthur.compta.lapin.application.model.template;

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
	 * Ajout un élément dans le template
	 * 
	 * @param elt
	 */
	public void addElement(TrimestreTemplateElement elt) {
		_elementList.add(elt);

	}

	/**
	 * Retourne la liste des éléments du template
	 * 
	 * @return
	 */
	public ObservableList<TrimestreTemplateElement> getElements() {

		return _elementList;
	}

}
