package org.arthur.compta.lapin.application.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.model.AppExerciceMensuel;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplate;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElement;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElementFrequence;
import org.arthur.compta.lapin.dataaccess.db.DBManager;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.Operation;
import org.arthur.compta.lapin.model.operation.OperationType;
import org.arthur.compta.lapin.model.operation.TransfertOperation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Accès aux services des templates de trimestre dans l'applicatoin
 *
 */
public class TemplateService {

	/**
	 * Retourne le template de trimestre
	 * 
	 * @return
	 */
	public static TrimestreTemplate getTrimestreTemplate() {

		TrimestreTemplate tmp = new TrimestreTemplate();
		try {
			HashMap<String, String[]> tmpInfo = DBManager.getInstance().loadTemplateInfo();

			for (String key : tmpInfo.keySet()) {
				// récupération des infos
				String[] info = tmpInfo.get(key);

				TrimestreTemplateElement elt = new TrimestreTemplateElement();
				elt.setNom(info[0]);
				elt.setMontant(Double.parseDouble(info[1]));
				elt.setType(info[2]);
				elt.setFreq(TrimestreTemplateElementFrequence.valueOf(info[3]));
				elt.setOccurence(Integer.parseInt(info[4]));
				elt.setCompteSource(CompteManager.getInstance().getCompte(info[5]));
				elt.setCompteCible(CompteManager.getInstance().getCompte(info[6]));

				tmp.addElement(elt);

			}

		} catch (ComptaException e) {
			e.printStackTrace();
			// Impossible de récupérer le template, on en génère un vide
		}

		return tmp;
	}

	/**
	 * Ajoute les opérations du template au mois
	 * 
	 * @param exMen
	 *            l'exercice mensuel
	 * @param num
	 *            l'indice du mois dans le trimestre
	 * @throws ComptaException
	 */
	public static void applyTtemplate(AppExerciceMensuel exMen, int num) throws ComptaException {

		TrimestreTemplate tmp = getTrimestreTemplate();

		for (TrimestreTemplateElement elt : tmp.getElements()) {

			int count = 0;

			// hebdomadaire
			if (elt.getFreq().equals(TrimestreTemplateElementFrequence.HEBDOMADAIRE)) {

				// on se place au début du mois
				final Calendar cal = Calendar.getInstance();
				cal.set(exMen.getDateDebut().get(Calendar.YEAR), exMen.getDateDebut().get(Calendar.MONTH), 1);

				for (int j = 0; j < cal.getActualMaximum(Calendar.DAY_OF_MONTH); j++) {

					cal.roll(Calendar.DAY_OF_MONTH, 1);
					// on obtient tombe sur le jour
					if (cal.get(Calendar.DAY_OF_WEEK) == elt.getOccurence()) {
						count++;
					}

				}
			}
			// mensuel
			if (elt.getFreq().equals(TrimestreTemplateElementFrequence.MENSUEL)) {

				count++;

			}
			// trimestriel
			if (elt.getFreq().equals(TrimestreTemplateElementFrequence.TRIMESTRIEL)) {

				if (elt.getOccurence() == num) {
					count++;
				}

			}
			// on ajout l'opération autant de fois que nécessaire
			for (int i = 0; i < count; i++) {
				createOperationFromTmpElt(exMen, elt);
			}

		}

	}

	/**
	 * Crée et ajoute une opération dans l'exercice depuis un élément de
	 * template
	 * 
	 * @param exMen
	 *            l'exercice
	 * @param elt
	 *            l'element
	 * @throws ComptaException
	 */
	private static void createOperationFromTmpElt(AppExerciceMensuel exMen, TrimestreTemplateElement elt)
			throws ComptaException {
		if (elt.getType().equals(OperationType.DEPENSE.toString())) {

			// création
			Operation dep = new Operation(OperationType.DEPENSE, elt.getCompteSource().getCompte(), elt.getNom(),
					elt.getMontant(), EtatOperation.PREVISION);
			String idOp = DBManager.getInstance().createOperation(dep, elt.getCompteSource().getAppId(), null,
					exMen.getAppId());

			// ajout dans l'application
			AppOperation appDep = new AppOperation(dep);
			appDep.setAppID(idOp);
			appDep.setCompteSrc(elt.getCompteSource());
			exMen.addDepense(appDep);

		}
		if (elt.getType().equals(OperationType.RESSOURCE.toString())) {

			// création
			Operation res = new Operation(OperationType.RESSOURCE, elt.getCompteSource().getCompte(), elt.getNom(),
					elt.getMontant(), EtatOperation.PREVISION);
			String idOp = DBManager.getInstance().createOperation(res, elt.getCompteSource().getAppId(), null,
					exMen.getAppId());
			// ajout dans l'application
			AppOperation appRes = new AppOperation(res);
			appRes.setAppID(idOp);
			appRes.setCompteSrc(elt.getCompteSource());
			exMen.addRessource(appRes);

		}
		if (elt.getType().equals(OperationType.TRANSFERT.toString())) {
			// création
			TransfertOperation trans = new TransfertOperation(elt.getCompteSource().getCompte(), elt.getNom(),
					elt.getMontant(), EtatOperation.PREVISION, elt.getCompteCible().getCompte());
			String idOp = DBManager.getInstance().createOperation(trans, elt.getCompteSource().getAppId(),
					elt.getCompteCible().getAppId(), exMen.getAppId());
			// ajout dans l'application
			AppTransfert apptr = new AppTransfert(trans);
			apptr.setAppID(idOp);
			apptr.setCompteSrc(elt.getCompteSource());
			apptr.setCompteCible(elt.getCompteCible());
			exMen.addTransfert(apptr);
		}

	}

	/**
	 * Met à jour le modèle de trimestre
	 * 
	 * @param elementList
	 * @throws ComptaException
	 *             Echec
	 */
	public static void updateTrimestreTemplate(List<TrimestreTemplateElement> elementList) throws ComptaException {

		DBManager.getInstance().clearTrimTemplate();
		DBManager.getInstance().addTrimstreTempElts(elementList);

	}


	/**
	 * Retourne les fréquences possibles pour un élément de template
	 * 
	 * @return
	 */
	public static ObservableList<String> getTemplateEltFreq() {
		// creation de la liste
		ObservableList<String> res = FXCollections.observableArrayList();
		// on récupère les valeurs de l'enum
		for (TrimestreTemplateElementFrequence freq : TrimestreTemplateElementFrequence.values()) {
			res.add(freq.toString());
		}

		return res;
	}
	
	/**
	 * Retourne les occurences possible en fonction de la fréquence choisie
	 * 
	 * @param freq
	 * @return
	 */
	public static Integer[] getOccurenceForFreq(String freq) {

		Integer[] res = null;

		if (freq.equals(TrimestreTemplateElementFrequence.HEBDOMADAIRE.toString())) {
			res = new Integer[] { 2, 3, 4, 5, 6, 7, 1 };
		}
		if (freq.equals(TrimestreTemplateElementFrequence.TRIMESTRIEL.toString())) {
			res = new Integer[] { 0, 1, 2 };
		}
		if (freq.equals(TrimestreTemplateElementFrequence.MENSUEL.toString())) {
			res = new Integer[] {};
		}

		return res;
	}

}
