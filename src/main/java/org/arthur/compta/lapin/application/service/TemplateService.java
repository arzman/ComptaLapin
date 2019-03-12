package org.arthur.compta.lapin.application.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
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
			HashMap<String, TrimestreTemplateElement> tmpInfo = DBManager.getInstance().loadTemplateInfo();

			for (String key : tmpInfo.keySet()) {
				// récupération des infos

				tmp.addElement(tmpInfo.get(key));

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
	 * @param exMen l'exercice mensuel
	 * @param num   l'indice du mois dans le trimestre
	 * @return
	 * @throws ComptaException
	 */
	public static void applyTtemplate(AppExerciceMensuel exMen, int num) throws ComptaException {

		TrimestreTemplate tmp = getTrimestreTemplate();

		for (TrimestreTemplateElement elt : tmp.getElements()) {

			int count = 0;

			// hebdomadaire
			if (elt.getFreq().equals(TrimestreTemplateElementFrequence.HEBDOMADAIRE)) {

				// on se place au début du mois
				LocalDate deb = LocalDate.of(exMen.getDateDebut().getYear(), exMen.getDateDebut().getMonthValue(), 1);

				for (int j = 0; j < deb.lengthOfMonth(); j++) {

					// on obtient tombe sur le jour
					if (deb.getDayOfWeek().getValue() == elt.getOccurence()) {
						count++;
					}

					deb = deb.plusDays(1);

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
	 * Crée et ajoute une opération dans l'exercice depuis un élément de template
	 * 
	 * @param exMen l'exercice
	 * @param elt   l'element
	 * @throws ComptaException
	 */
	private static void createOperationFromTmpElt(AppExerciceMensuel exMen, TrimestreTemplateElement elt)
			throws ComptaException {
		if (elt.getType().equals(OperationType.DEPENSE.toString())
				|| elt.getType().equals(OperationType.RESSOURCE.toString())) {

			// création
			Operation op = new Operation(OperationType.valueOf(elt.getType()), elt.getCompteSource().getCompte(),
					elt.getNom(), elt.getMontant(), EtatOperation.PREVISION);
			String idOp = DBManager.getInstance().addOperation(op, elt.getCompteSource().getAppId(), null,
					exMen.getAppId());

			// ajout dans l'application
			AppOperation appDep = new AppOperation(op);
			appDep.setAppID(idOp);
			appDep.setCompteSrc(elt.getCompteSource());
			exMen.addOperation(appDep);

		}

		if (elt.getType().equals(OperationType.TRANSFERT.toString())) {
			// création
			TransfertOperation trans = new TransfertOperation(elt.getCompteSource().getCompte(), elt.getNom(),
					elt.getMontant(), EtatOperation.PREVISION, elt.getCompteCible().getCompte());
			String idOp = DBManager.getInstance().addOperation(trans, elt.getCompteSource().getAppId(),
					elt.getCompteCible().getAppId(), exMen.getAppId());
			// ajout dans l'application
			AppTransfert apptr = new AppTransfert(trans);
			apptr.setAppID(idOp);
			apptr.setCompteSrc(elt.getCompteSource());
			apptr.setCompteCible(elt.getCompteCible());
			exMen.addOperation(apptr);
		}

	}

	/**
	 * Met à jour le modèle de trimestre
	 * 
	 * @param elementList
	 * @throws ComptaException Echec
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

	/**
	 * Retourne le gain moyen mensuel d'une liste d'élément de template
	 * 
	 * @param _elementList
	 * @return
	 */
	public static double getGainMoyen(List<TrimestreTemplateElement> _elementList) {

		double gain = 0;

		for (TrimestreTemplateElement elt : _elementList) {

			double mont;
			// mensualisation du montant
			switch (elt.getFreq()) {
			case HEBDOMADAIRE:
				mont = elt.getMontant() * 52 / 12.0;
				break;
			case MENSUEL:
				mont = elt.getMontant();
				break;
			case TRIMESTRIEL:
				mont = elt.getMontant() / 3.0;
				break;
			default:
				mont = 0;
				break;
			}
			// prise en compte dans le gain
			switch (OperationType.valueOf(elt.getType())) {

			case DEPENSE:
				gain = gain - mont;
				break;
			case RESSOURCE:
				gain = gain + mont;
				break;
			default:
				break;

			}

		}

		return gain;
	}

	public static double getPrevFromtemplate() {

		return getGainMoyen(getTrimestreTemplate().getElements());
	}

}
