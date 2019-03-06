package org.arthur.compta.lapin.application.service;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.dataaccess.db.DBManager;

public class SyntheseService {

	/**
	 * Retourne la liste des années des exercice mensuels sous forme d'entier
	 * 
	 * @return
	 * @throws ComptaException Echec
	 */
	public static List<Integer> getAnnees() throws ComptaException {

		ArrayList<Integer> res = new ArrayList<>();

		for (String st : DBManager.getInstance().getAllAnnees()) {

			res.add(Integer.parseInt(st));

		}

		return res;
	}

	/**
	 * Retourne la somme des ressources pour le mois donné
	 * 
	 * @param date
	 * @return
	 * @throws ComptaException
	 */
	public static double getRessourceForMonth(LocalDate date) throws ComptaException {

		double res = 0;
		// on récupère les ressources...et on somme
		for (double dou : DBManager.getInstance().getOperationForMonth("RESSOURCE", date)) {
			res = res + dou;
		}

		return res;
	}

	/**
	 * Retourne la somme des utilisations de budget pour le mois donné
	 * 
	 * @param date
	 * @return
	 * @throws ComptaException
	 */
	public static double getBudgetUsageForMonth(LocalDate date) throws ComptaException {

		double res = 0;
		// on récupère les ressources...et on somme
		for (double dou : DBManager.getInstance().getBudgetUsageForMonth(date)) {
			res = res + dou;
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
	public static double getDepenseForMonth(LocalDate date) throws ComptaException {

		double res = 0;
		// on récupère les dépenses...et on somme
		for (double dou : DBManager.getInstance().getOperationForMonth("DEPENSE", date)) {
			res = res + dou;
		}

		return res;
	}

	/**
	 * Crée un PDF avec les dépenses/ressources/transfert du trimestre
	 * 
	 * @param idTrim l'id du trimestre
	 * @param file   le chemin du fichier a exporter
	 * @throws ComptaException Echec de l'export
	 */
	public static void writeRapportForTrim(String idTrim, File file) throws ComptaException {

		RapportTrimWriter writer = new RapportTrimWriter(idTrim);
		writer.writeRapport(file);

	}

}
