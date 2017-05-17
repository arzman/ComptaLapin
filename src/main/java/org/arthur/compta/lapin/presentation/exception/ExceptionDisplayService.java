package org.arthur.compta.lapin.presentation.exception;

/**
 * Service gérant l'affichage des exceptions
 *
 */
public class ExceptionDisplayService {

	/**
	 * Affichage l'exception dans une pop-up
	 * @param e l'exception a afficher
	 */
	public static void showException(Exception e) {

		if (e != null) {
			ExceptionDialog excD = new ExceptionDialog(e);
			excD.showAndWait();
		}

	}

}
