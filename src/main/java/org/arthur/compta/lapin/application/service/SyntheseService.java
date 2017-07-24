package org.arthur.compta.lapin.application.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.dataaccess.db.DBManager;

public class SyntheseService {

	/**
	 * Retourne la liste des années des exercice mensuels sous forme d'entier
	 * 
	 * @return
	 * @throws ComptaException
	 *             Echec
	 */
	public static List<Integer> getAnnees() throws ComptaException {

		ArrayList<Integer> res = new ArrayList<>();

		for (String st : DBManager.getInstance().getAllAnnees()) {

			res.add(Integer.parseInt(st));

		}

		return res;
	}

	/**
	 * Retourne la somme des dépenses pour le mois donné
	 * 
	 * @param date
	 * @return
	 * @throws ComptaException
	 */
	public static double getRessourceForMonth(Calendar date) throws ComptaException {

		double res = 0;
		
		for (double dou : DBManager.getInstance().getOperationForMonth("RESSOURCE",date)) {
			res = res + dou;
		}

		return res;
	}

	public static double getDepenseForMonth(Calendar date) throws ComptaException {

		double res = 0;

		for (double dou : DBManager.getInstance().getOperationForMonth("DEPENSE",date)) {
			res = res + dou;
		}

		return res;
	}

}
