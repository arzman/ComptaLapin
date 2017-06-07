package org.arthur.compta.lapin.application.service;

import java.util.Calendar;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.dataaccess.db.DBManager;

/**
 * Regroupe les services "inclassables" de l'application
 *
 */
public class ComptaService {

	/**
	 * Retourne la date de dernière vérification de la compta
	 * 
	 * @return
	 * @throws ComptaException
	 *             Echec de la récupération
	 */
	public static String getDateDerVerif() throws ComptaException {

		return DBManager.getInstance().getDateDerVerif();

	}

	/**
	 * Positionne la date de dernière vérif
	 * 
	 * @param date
	 * @throws ComptaException
	 *             Echec de l'opération
	 */
	public static void setDateDerVerif(Calendar date) throws ComptaException {
		DBManager.getInstance().setDateDerVerif(date);

	}
}
