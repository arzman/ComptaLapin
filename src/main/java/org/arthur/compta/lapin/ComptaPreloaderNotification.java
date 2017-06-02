package org.arthur.compta.lapin;

import javafx.application.Preloader.PreloaderNotification;

/**
 * Implementation d'une notification permettant d'afficher un message sur le
 * splash screen au démarrage
 *
 */
public class ComptaPreloaderNotification implements PreloaderNotification {

	/** Le message de chargement */
	private String _message;

	/**
	 * Constructeur 
	 * @param string le message
	 */
	public ComptaPreloaderNotification(String string) {

		_message = string;
	}

	/**
	 * Retourne le message de chargement
	 * 
	 * @return
	 */
	public String getMessage() {
		return _message;
	}

}
