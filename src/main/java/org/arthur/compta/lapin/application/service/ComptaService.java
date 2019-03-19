package org.arthur.compta.lapin.application.service;

import java.time.LocalDate;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.dataaccess.db.AppliDataAccess;

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

		return AppliDataAccess.getInstance().getDateDerVerif();

	}

	/**
	 * Positionne la date de dernière vérif
	 * 
	 * @param date
	 * @throws ComptaException
	 *             Echec de l'opération
	 */
	public static void setDateDerVerif(LocalDate date) throws ComptaException {
		AppliDataAccess.getInstance().setDateDerVerif(date);

	}
}
