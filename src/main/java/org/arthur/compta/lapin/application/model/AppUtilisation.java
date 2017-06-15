package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.model.Utilisation;

/**
 * Encapsulation applicative d'une Utilisation de Budget
 *
 */
public class AppUtilisation extends AppObject {

	/** L'utilisation métier */
	private Utilisation _utilisation;

	public AppUtilisation(Utilisation utilisation) {

		_utilisation = utilisation;
	}

}
