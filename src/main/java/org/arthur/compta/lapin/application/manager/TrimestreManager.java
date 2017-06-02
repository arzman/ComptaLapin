package org.arthur.compta.lapin.application.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppExerciceMensuel;
import org.arthur.compta.lapin.application.model.AppTrimestre;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplate;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElement;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElementFrequence;
import org.arthur.compta.lapin.dataaccess.db.DBManager;
import org.arthur.compta.lapin.model.ExerciceMensuel;
import org.arthur.compta.lapin.model.Trimestre;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.Operation;
import org.arthur.compta.lapin.model.operation.OperationType;
import org.arthur.compta.lapin.model.operation.TransfertOperation;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Gestionnaire des trimestres.
 *
 */
public class TrimestreManager {

	/**
	 * L'unique instance du singleton
	 */
	private static TrimestreManager _instance;

	private SimpleObjectProperty<AppTrimestre> _trimestreCourant;

	/**
	 * Le constructeur
	 */
	private TrimestreManager() {

		_trimestreCourant = new SimpleObjectProperty<AppTrimestre>();

		try {
			String[] info = DBManager.getInstance().getTrimestreCourantId();
			if (info != null && info.length == 1 && info[0] != null && !info[0].isEmpty()) {
				loadTrimestreCourant(info[0]);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Retourne l'instance du singleton
	 * 
	 * @return
	 */
	public static TrimestreManager getInstance() {

		if (_instance == null) {

			_instance = new TrimestreManager();
		}
		return _instance;
	}

	/**
	 * Charge un trimestre courant
	 * 
	 * @param appId
	 *            l'id du trimestre a charger
	 * @throws ComptaException
	 *             Echec du chargement
	 */
	public void loadTrimestreCourant(String appId) throws ComptaException {

		// recup des infos en base
		String[] info;

		try {
			// chargmement du trimestre
			info = DBManager.getInstance().getTrimestreInfo(appId);

			if (info != null) {

				// création du trimestre applicatif
				AppTrimestre appTrimestre = new AppTrimestre(new Trimestre());
				appTrimestre.setAppID(info[0]);

				// chargement du 1er mois
				appTrimestre.premierMoisProperty().set(loadExerciceMensuel(info[1]));
				// chargement du 2eme mois
				appTrimestre.deuxiemeMoisProperty().set(loadExerciceMensuel(info[2]));
				// chargement du 3eme mois
				appTrimestre.troisiemeMoisProperty().set(loadExerciceMensuel(info[3]));

				_trimestreCourant.set(appTrimestre);
				DBManager.getInstance().setTrimestreCourant(appId);

			}
		} catch (Exception e) {
			throw new ComptaException("Impossible de charger le trimestre courant", e);
		}

	}

	/**
	 * Crée un exercice mensuel applicatif depuis la base de donnée
	 * 
	 * @param id
	 *            l'id de l'exercice mensuel
	 * @return l'exercice mensuel
	 * @throws ComptaException
	 */
	private AppExerciceMensuel loadExerciceMensuel(String id) throws ComptaException {

		AppExerciceMensuel appEm = null;

		try {

			if (id != null && !id.isEmpty()) {

				// récupération en base des donnée de l'exercice
				String[] infos = DBManager.getInstance().getExMensuelInfos(id);
				ExerciceMensuel em = new ExerciceMensuel();

				// date de début
				Calendar deb = Calendar.getInstance();
				deb.setTime(ApplicationFormatter.databaseDateFormat.parse(infos[1]));
				em.setDateDebut(deb);

				appEm = new AppExerciceMensuel(em);
				appEm.setAppID(infos[0]);

			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de charger l'exercice mensuel", e);
		}

		return appEm;
	}

	/**
	 * Retourne le trimestre courant sous forme de propriété
	 * 
	 * @return
	 */
	public ObjectProperty<AppTrimestre> trimestreCourantProperty() {
		return _trimestreCourant;

	}

	/**
	 * Crée un trimestre applicatif
	 * 
	 * @param dateDeb
	 * @return
	 * @throws ComptaException
	 */
	public AppTrimestre createTrimestre(Calendar dateDeb) throws ComptaException {

		AppTrimestre appTrim = null;

		try {

			Trimestre trim = new Trimestre();
			appTrim = new AppTrimestre(trim);

			final int numMoi = dateDeb.get(Calendar.MONTH);

			// création des exercice mensuel du trimestre
			for (int i = 0; i < 3; i++) {

				final ExerciceMensuel em = new ExerciceMensuel();
				// date de debut
				final Calendar debut = Calendar.getInstance();
				debut.set(Calendar.DAY_OF_MONTH, 1);
				debut.set(Calendar.MONTH, (i + numMoi) % 12);
				debut.set(Calendar.YEAR, dateDeb.get(Calendar.YEAR) + ((i + numMoi) / 12));
				em.setDateDebut(debut);
				// date de fin
				final Calendar fin = Calendar.getInstance();
				fin.set(Calendar.DAY_OF_MONTH, debut.getActualMaximum(Calendar.DAY_OF_MONTH));
				fin.set(Calendar.MONTH, (i + numMoi) % 12);
				fin.set(Calendar.YEAR, dateDeb.get(Calendar.YEAR) + ((i + numMoi) / 12));
				em.setDateFin(fin);

				// insertion de l'exercice mensuel en base
				String idEm = DBManager.getInstance().addExerciceMensuel(debut, fin);

				// création de l'exercice applicatif
				AppExerciceMensuel appEm = new AppExerciceMensuel(em);
				appEm.setAppID(idEm);
				// ajout des opérations du templates
				applyTtemplate(appEm, i);

				appTrim.setAppExerciceMensuel(i, appEm);

			}

			// insertion du trimestre en base
			String idTrim = DBManager.getInstance().addTrimestre(appTrim.premierMoisProperty().get().getAppId(),
					appTrim.deuxiemeMoisProperty().get().getAppId(), appTrim.troisiemeMoisProperty().get().getAppId());

			appTrim.setAppID(idTrim);

		} catch (Exception e) {
			throw new ComptaException("Impossible de créer le trimestre", e);
		}

		return appTrim;
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
	private void applyTtemplate(AppExerciceMensuel exMen, int num) throws ComptaException {

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
	private void createOperationFromTmpElt(AppExerciceMensuel exMen, TrimestreTemplateElement elt)
			throws ComptaException {
		if (elt.getType().equals(OperationType.DEPENSE.toString())) {

			// création
			String compteId = elt.getCompteSource().getAppId();
			Operation dep = new Operation(OperationType.DEPENSE,
					CompteManager.getInstance().getCompte(compteId).getCompte(), elt.getNom(), elt.getMontant(),
					EtatOperation.PREVISION);
			String idOp = DBManager.getInstance().createOperation(dep, compteId, null, exMen.getAppId());
			// ajout dans l'application
			exMen.addDepense(dep, idOp);

		}
		if (elt.getType().equals(OperationType.RESSOURCE.toString())) {

			// création
			String compteId = elt.getCompteSource().getAppId();
			Operation res = new Operation(OperationType.RESSOURCE,
					CompteManager.getInstance().getCompte(compteId).getCompte(), elt.getNom(), elt.getMontant(),
					EtatOperation.PREVISION);
			String idOp = DBManager.getInstance().createOperation(res, compteId, null, exMen.getAppId());
			// ajout dans l'application
			exMen.addDepense(res, idOp);

		}
		if (elt.getType().equals(OperationType.TRANSFERT.toString())) {
			// création
			String compteSrcId = elt.getCompteSource().getAppId();
			String compteCibleId = elt.getCompteCible().getAppId();
			TransfertOperation res = new TransfertOperation(CompteManager.getInstance().getCompte(compteSrcId).getCompte(), elt.getNom(), elt.getMontant(),
					EtatOperation.PREVISION,CompteManager.getInstance().getCompte(compteCibleId).getCompte());
			String idOp = DBManager.getInstance().createOperation(res, compteSrcId, compteCibleId, exMen.getAppId());
			// ajout dans l'application
			exMen.addDepense(res, idOp);
		}

	}

	/**
	 * Retourne une Map contenant un peu d'informations sur les trimestres en
	 * base
	 * 
	 * @return une Map contenant clé:id Trimestre , value:dateDébut
	 * @throws ComptaException
	 *             Erreur lors de la récupération des infos
	 */
	public HashMap<String, Calendar> getAllTrimestreShortList() throws ComptaException {

		HashMap<String, Calendar> res = new HashMap<>();

		try {

			ArrayList<String> ids = DBManager.getInstance().getAllTrimestreId();

			for (String id : ids) {

				// récupération de la date de début
				Calendar cal = Calendar.getInstance();
				cal.setTime(DBManager.getInstance().getDateDebutFromTrimestre(id));

				res.put(id, cal);

			}
		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer la liste des trimestres", e);
		}

		return res;
	}

	/**
	 * Supprime un trimestre. La suppression est ignorée si le trimestre à
	 * supprimer est le trimestre courant
	 * 
	 * @param idTrimestre
	 *            l'id du trimestre a supprimer
	 * @throws ComptaException
	 *             Impossible de supprimer le trimestre
	 */
	public void removeTrimestre(String idTrimestre) throws ComptaException {

		if (_trimestreCourant.get() != null && !idTrimestre.equals(_trimestreCourant.get().getAppId())) {

			DBManager.getInstance().removeTrimestre(idTrimestre);
		}

	}

	/**
	 * Retourne le template de trimestre
	 * 
	 * @return
	 */
	public TrimestreTemplate getTrimestreTemplate() {

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
	 * Retourne les types possibles pour un élément de template
	 * 
	 * @return
	 */
	public ObservableList<String> getTemplateEltType() {
		// creation de la liste
		ObservableList<String> res = FXCollections.observableArrayList();
		// on récupère les valeurs de l'enum
		for (OperationType opeType : OperationType.values()) {
			res.add(opeType.toString());
		}

		return res;
	}

	/**
	 * Retourne les fréquences possibles pour un élément de template
	 * 
	 * @return
	 */
	public ObservableList<String> getTemplateEltFreq() {
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
	public Integer[] getOccurenceForFreq(String freq) {

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
	 * Indique si la chaine de caractère correspond au type d'opération :
	 * Transfert
	 * 
	 * @param type
	 * @return
	 */
	public boolean isTransfertType(String type) {

		return OperationType.TRANSFERT.toString().equals(type);
	}

	/**
	 * Met à jour le modèle de trimestre
	 * 
	 * @param elementList
	 * @throws ComptaException
	 *             Echec
	 */
	public void updateTrimestreTemplate(List<TrimestreTemplateElement> elementList) throws ComptaException {

		DBManager.getInstance().clearTrimTemplate();
		DBManager.getInstance().addTrimstreTempElts(elementList);

	}

}
