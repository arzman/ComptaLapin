package org.arthur.compta.lapin.application.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.AppExerciceMensuel;
import org.arthur.compta.lapin.application.model.AppExerciceMensuelLightId;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.application.model.AppTrimestre;
import org.arthur.compta.lapin.application.service.OperationService;
import org.arthur.compta.lapin.application.service.TemplateService;
import org.arthur.compta.lapin.dataaccess.db.AppliDataAccess;
import org.arthur.compta.lapin.dataaccess.db.ExerciceMensuelDataAccess;
import org.arthur.compta.lapin.dataaccess.db.OperationDataAccess;
import org.arthur.compta.lapin.dataaccess.db.TrimestreDataAccess;
import org.arthur.compta.lapin.model.Trimestre;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.Operation;
import org.arthur.compta.lapin.model.operation.OperationType;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

/**
 * Gestionnaire des trimestres.
 *
 */
public class TrimestreManager {

	/** L'unique instance du singleton */
	private static TrimestreManager _instance;
	/** le trimestre en cours de traitement */
	private SimpleObjectProperty<AppTrimestre> _trimestreCourant;

	/**
	 * Le constructeur
	 */
	private TrimestreManager() {

		_trimestreCourant = new SimpleObjectProperty<AppTrimestre>();

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
	public void loadTrimestreCourant(int appId) throws ComptaException {

		AppTrimestre appTrimestre = loadTrimestre(appId);

		_trimestreCourant.set(appTrimestre);
		AppliDataAccess.getInstance().setTrimestreCourant(appId);

		// avertit le CompteManager de mettre a jour les prévisions
		CompteManager.getInstance().refreshAllPrev();

	}

	/**
	 * Extrait de la base un trimestre
	 * 
	 * @param appId
	 *            L'id du trimestre
	 * @return
	 * @throws ComptaException
	 *             Echec dans la récupération
	 */
	public AppTrimestre loadTrimestre(int appId) throws ComptaException {

		return new AppTrimestre(TrimestreDataAccess.getInstance().getTrimestreInfo(appId));

	}

	/**
	 * Crée un exercice mensuel applicatif depuis la base de donnée
	 * 
	 * @param id
	 *            l'id de l'exercice mensuel
	 * @return l'exercice mensuel
	 * @throws ComptaException
	 */
	public AppExerciceMensuel loadExerciceMensuel(int id) throws ComptaException {

		return new AppExerciceMensuel(ExerciceMensuelDataAccess.getInstance().getExMensuel(id));

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
	 *            la date de début
	 * @param resPrev
	 *            le résultat prévisionnel
	 * @return
	 * @throws ComptaException
	 */
	public AppTrimestre createTrimestre(LocalDate dateDeb) throws ComptaException {

		int[] createdId = new int[3];

		// création des exercice mensuel du trimestre
		for (int i = 0; i < 3; i++) {

			// création du modèle métier
			// date de debut
			LocalDate debut = dateDeb.plusMonths(i).withDayOfMonth(1);
			// date de fin
			LocalDate fin = debut.withDayOfMonth(debut.lengthOfMonth());

			AppExerciceMensuel appEm = new AppExerciceMensuel(
					ExerciceMensuelDataAccess.getInstance().addExerciceMensuel(debut, fin, TemplateService.getPrevFromtemplate()));
			createdId[i] = appEm.getAppId();

			// ajout des opérations du templates et calcul du prévisionnel
			TemplateService.applyTtemplate(appEm, i);

		}

		return new AppTrimestre(TrimestreDataAccess.getInstance().addTrimestre(createdId[0], createdId[1], createdId[2]));
	}

	/**
	 * Retourne une Map contenant un peu d'informations sur les trimestres en
	 * base
	 * 
	 * @return une Map contenant clé:id Trimestre , value:dateDébut
	 * @throws ComptaException
	 *             Erreur lors de la récupération des infos
	 */
	public HashMap<String, LocalDate> getAllTrimestreShortList() throws ComptaException {

		HashMap<String, LocalDate> res = new HashMap<>();

		ArrayList<String> ids = TrimestreDataAccess.getInstance().getAllTrimestreId();

		for (String id : ids) {

			// récupération de la date de début
			res.put(id, TrimestreDataAccess.getInstance().getDateDebutFromTrimestre(id));

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
	public void removeTrimestre(int idTrimestre) throws ComptaException {

		if (_trimestreCourant.get() == null || (_trimestreCourant.get() != null && idTrimestre != _trimestreCourant.get().getAppId())) {

			Trimestre trimToDel = TrimestreDataAccess.getInstance().getTrimestreInfo(idTrimestre);

			ExerciceMensuelDataAccess.getInstance().removeExcerciceMensuel(trimToDel.getExerciceMensuelIds()[0]);
			ExerciceMensuelDataAccess.getInstance().removeExcerciceMensuel(trimToDel.getExerciceMensuelIds()[1]);
			ExerciceMensuelDataAccess.getInstance().removeExcerciceMensuel(trimToDel.getExerciceMensuelIds()[2]);
			TrimestreDataAccess.getInstance().removeTrimestre(idTrimestre);
		}

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
	 * Retourne la somme a ajouter au compte pour la fin de l'exercice mensuel
	 * 
	 * @param idCompte
	 * @param numMois
	 * @return
	 */
	public double getDeltaForCompte(AppCompte compte, int numMois) {

		double delta = 0;

		if (_trimestreCourant.get() != null) {
			// retrait des dépenses
			for (AppOperation dep : _trimestreCourant.getValue().getAppExerciceMensuel(numMois).get().getDepenses()) {

				if (dep.getEtat().equals(EtatOperation.PREVISION) && dep.getCompteSource().equals(compte)) {
					delta = delta - dep.getMontant();
				}
			}
			// ajout des ressources
			for (AppOperation res : _trimestreCourant.getValue().getAppExerciceMensuel(numMois).get().getRessources()) {
				if (res.getEtat().equals(EtatOperation.PREVISION) && res.getCompteSource().equals(compte)) {
					delta = delta + res.getMontant();
				}

			}
			// prise en compte des transfert
			for (AppTransfert trans : _trimestreCourant.getValue().getAppExerciceMensuel(numMois).get().getTransferts()) {
				// compte source : l'argent part
				if (trans.getEtat().equals(EtatOperation.PREVISION) && trans.getCompteSource().equals(compte)) {

					delta = delta - trans.getMontant();
				}
				// compte cible : l'argent rentre
				if (trans.getEtat().equals(EtatOperation.PREVISION) && trans.getCompteCible().equals(compte)) {
					delta = delta + trans.getMontant();
				}
			}
		}

		return delta;
	}

	/**
	 * Charge le trimestre courant précédemment enregistré en base. Cette
	 * méthode ne peut pas se mettre dans le constructeur car sinon il y a une
	 * boucle d'instanciation avec le CompteManager
	 * 
	 * @throws ComptaException
	 */
	public void recoverTrimestre() throws ComptaException {

		int id = AppliDataAccess.getInstance().getTrimestreCourantId();

		// charge le trimestre courant ( -1 si pas de trimestre
		// courant...n'arrive qu'au tt debut)
		if (id != -1) {
			TrimestreManager.getInstance().loadTrimestreCourant(id);
		}

	}

	/**
	 * Supprime une opération du mois du trimestre courant
	 * 
	 * @param appOp
	 *            l'opération
	 * @param numMois
	 *            l'index du mois dans le trimestre
	 * @throws ComptaException
	 */
	public void removeOperation(AppOperation appOp, Integer numMois) throws ComptaException {

		// si l'opération est prise en compte, on l'annule
		if (appOp.getEtat().equals(EtatOperation.PRISE_EN_COMPTE)) {
			OperationService.switchEtatOperation(appOp);
		}

		// récupération de l'exercice mensuel
		AppExerciceMensuel appEm = _trimestreCourant.get().getAppExerciceMensuel(numMois).get();
		// suppression de l'opération dans l'application
		if (appOp.getType().equals(OperationType.DEPENSE)) {
			appEm.getDepenses().remove(appOp);
		} else {
			if (appOp.getType().equals(OperationType.RESSOURCE)) {
				appEm.getRessources().remove(appOp);
			} else {
				if (appOp.getType().equals(OperationType.TRANSFERT)) {
					appEm.getTransferts().remove(appOp);
				}
			}
		}

		// Maj prévisionnel
		CompteManager.getInstance().calculateSoldePrev(appOp.getCompteSource());
		if (appOp instanceof AppTransfert) {
			CompteManager.getInstance().calculateSoldePrev(((AppTransfert) appOp).getCompteCible());
		}

		// suppression de l'opération en base
		OperationDataAccess.getInstance().removeOperation(appOp.getAppId());

	}

	/**
	 * Crée une nouvelle opération dans le mois du trimestre courant
	 * correspondant à l'index
	 * 
	 * @param libelle
	 *            le libelle
	 * @param montant
	 *            le montant
	 * @param type
	 *            le type
	 * @param compteSrc
	 *            le compte source
	 * @param compteCible
	 *            le compte cible si Transfert, null sinon
	 * @param numMois
	 *            l'index du mois
	 * @return
	 * @throws ComptaException
	 *             Erreur lors de l'ajout
	 */
	public AppOperation addOperation(String libelle, double montant, String type, AppCompte compteSrc, AppCompte compteCible, int numMois)
			throws ComptaException {

		AppOperation appop = null;

		if (type.equals(OperationType.DEPENSE.toString())) {

			// dépense

			appop = new AppOperation(OperationDataAccess.getInstance().addOperation(libelle, montant, OperationType.DEPENSE, EtatOperation.PREVISION,
					compteSrc.getAppId(), -1, _trimestreCourant.get().getAppExerciceMensuel(numMois).get().getAppId()));

		} else {

			if (type.equals(OperationType.RESSOURCE.toString())) {

				// ressource
				appop = new AppOperation(OperationDataAccess.getInstance().addOperation(libelle, montant, OperationType.RESSOURCE, EtatOperation.PREVISION,
						compteSrc.getAppId(), -1, _trimestreCourant.get().getAppExerciceMensuel(numMois).get().getAppId()));

			} else {
				if (type.equals(OperationType.TRANSFERT.toString())) {

					appop = new AppTransfert(OperationDataAccess.getInstance().addOperation(libelle, montant, OperationType.TRANSFERT, EtatOperation.PREVISION,
							compteSrc.getAppId(), compteCible.getAppId(), _trimestreCourant.get().getAppExerciceMensuel(numMois).get().getAppId()));

				}
			}

		}

		_trimestreCourant.get().getAppExerciceMensuel(numMois).get().addOperation(appop);

		// refresh du previsionnel
		CompteManager.getInstance().calculateSoldePrev(compteSrc);
		CompteManager.getInstance().calculateSoldePrev(compteCible);

		return appop;
	}

	/**
	 * Retourne la date de début de l'excercice mensuel correspondant à l'index
	 * donné du trimestre courant
	 * 
	 * @param numMois
	 * @return
	 */
	public LocalDate getDateDebut(int numMois) {

		LocalDate dat = null;

		if (_trimestreCourant.get() != null) {

			dat = _trimestreCourant.get().getAppExerciceMensuel(numMois).get().getDateDebut();

		}

		return dat;
	}

	/**
	 * Retourne le résultat de l'excercice mensuel correspondant à l'index donné
	 * du trimestre courant
	 * 
	 * @param numMois
	 * @return
	 */
	public double getResultat(int numMois) {

		double res = 0;

		if (_trimestreCourant.get() != null) {

			res = _trimestreCourant.get().getAppExerciceMensuel(numMois).get().getResultat();

		}

		return res;
	}

	/**
	 * Retourne les dépenses de l'excercice mensuel correspondant à l'index
	 * donné du trimestre courant
	 * 
	 * @param numMois
	 * @return
	 */
	public ObservableList<AppOperation> getDepenses(Integer numMois) {

		ObservableList<AppOperation> res = null;

		if (_trimestreCourant.get() != null) {

			res = _trimestreCourant.get().getAppExerciceMensuel(numMois).get().getDepenses();

		}

		return res;
	}

	/**
	 * Retourne les opéations
	 * 
	 * @param id
	 * @return
	 * @throws ComptaException
	 */
	public List<Operation> getOperationForEM(int id) throws ComptaException {
		return OperationDataAccess.getInstance().getOperationInfo(id);

	}

	/**
	 * Retourne les ressources de l'excercice mensuel correspondant à l'index
	 * donné du trimestre courant
	 * 
	 * @param numMois
	 * @return
	 */
	public ObservableList<AppOperation> getRessources(Integer numMois) {
		ObservableList<AppOperation> res = null;

		if (_trimestreCourant.get() != null) {

			res = _trimestreCourant.get().getAppExerciceMensuel(numMois).get().getRessources();

		}

		return res;
	}

	/**
	 * Retourne les transferts de l'excercice mensuel correspondant à l'index
	 * donné du trimestre courant
	 * 
	 * @param numMois
	 * @return
	 */
	public ObservableList<AppTransfert> getTransfert(Integer numMois) {
		ObservableList<AppTransfert> res = null;

		if (_trimestreCourant.get() != null) {

			res = _trimestreCourant.get().getAppExerciceMensuel(numMois).get().getTransferts();

		}

		return res;
	}

	/**
	 * Retourne le résultat prévisionnel à la création de l'exercice
	 * 
	 * @param numMois
	 * @return
	 */
	public double getResultatPrev(int numMois) {
		double res = 0;

		if (_trimestreCourant.get() != null) {

			res = _trimestreCourant.get().getAppExerciceMensuel(numMois).get().getResultatPrev();

		}

		return res;
	}

	/**
	 * Retourne l'identifiant applicatif d'un exercice mensuel
	 * 
	 * @param idTrimestre
	 *            id applicatif du trimestre
	 * @param numMois
	 *            le numéro du mois
	 * @return l'identifiant
	 * @throws ComptaException
	 */
	public int getExerciceMensuelId(int idTrimestre, int numMois) throws ComptaException {

		return ExerciceMensuelDataAccess.getInstance().getExerciceMensuelId(idTrimestre, numMois);
	}

	/**
	 * Déplacement une opération du trimestre courant
	 * 
	 * @param appOp
	 *            l'opération
	 * @param numMoisFrom
	 *            le numéro du mois dans le trimestre courant
	 * @param appTrimIdTo
	 *            l'id du trimestre ou elle sera déplacée
	 * @param numMoisTo
	 *            le numéro du mois ou elle sera déplacée
	 * @throws ComptaException
	 */
	public void moveOperationFromTrimCourant(AppOperation appOp, int numMoisFrom, AppExerciceMensuelLightId appLI) throws ComptaException {

		// on supprime l'opération de l'EM source
		AppExerciceMensuel appEm = _trimestreCourant.get().getAppExerciceMensuel(numMoisFrom).get();

		// suppression de l'opération dans l'application
		if (appOp.getType().equals(OperationType.DEPENSE)) {
			appEm.getDepenses().remove(appOp);
		} else {
			if (appOp.getType().equals(OperationType.RESSOURCE)) {
				appEm.getRessources().remove(appOp);
			} else {
				if (appOp.getType().equals(OperationType.TRANSFERT)) {
					appEm.getTransferts().remove(appOp);
				}
			}

		}

		// on la rajoute si besoin
		if (appLI.getTrimestreId() == _trimestreCourant.get().getAppId()) {

			AppExerciceMensuel appEmTo = _trimestreCourant.get().getAppExerciceMensuel(appLI.getNumMois()).get();
			// suppression de l'opération dans l'application
			if (appOp.getType().equals(OperationType.DEPENSE)) {
				appEmTo.getDepenses().add(appOp);
			} else {
				if (appOp.getType().equals(OperationType.RESSOURCE)) {
					appEmTo.getRessources().add(appOp);
				} else {
					if (appOp.getType().equals(OperationType.TRANSFERT)) {
						appEmTo.getTransferts().add((AppTransfert) appOp);
					}
				}

			}

		}

		// Maj prévisionnel
		CompteManager.getInstance().calculateSoldePrev(appOp.getCompteSource());
		if (appOp instanceof AppTransfert) {
			CompteManager.getInstance().calculateSoldePrev(((AppTransfert) appOp).getCompteCible());
		}

		// changement en base
		OperationDataAccess.getInstance().moveOperation(appOp.getAppId(), appLI.getExerciceMensuelId());

	}

}
