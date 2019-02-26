package org.arthur.compta.lapin.presentation.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service gérant l'affichage des exceptions
 *
 */
public class ExceptionDisplayService {

	private static Logger logger = LogManager.getLogger(ExceptionDisplayService.class);

	/**
	 * Affichage l'exception dans une pop-up
	 * 
	 * @param e l'exception a afficher
	 */
	public static void showException(Exception e) {

		if (e != null) {
			// envoi a log4j
			logger.error("Erreur inattendue",e);
			// on montre
			ExceptionDialog excD = new ExceptionDialog(e);
			excD.showAndWait();
		}

	}

}
