package org.arthur.compta.lapin.application.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.AppExerciceMensuel;
import org.arthur.compta.lapin.application.model.AppExerciceMensuelLightId;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.application.model.AppTrimestre;
import org.arthur.compta.lapin.application.service.OperationService;
import org.arthur.compta.lapin.application.service.TemplateService;
import org.arthur.compta.lapin.dataaccess.db.DBManager;
import org.arthur.compta.lapin.model.ExerciceMensuel;
import org.arthur.compta.lapin.model.Trimestre;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.Operation;
import org.arthur.compta.lapin.model.operation.OperationType;
import org.arthur.compta.lapin.model.operation.TransfertOperation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
	 * @param appId l'id du trimestre a charger
	 * @throws ComptaException Echec du chargement
	 */
	public void loadTrimestreCourant(String appId) throws ComptaException {

		try {

			AppTrimestre appTrimestre = loadTrimestre(appId);

			_trimestreCourant.set(appTrimestre);
			DBManager.getInstance().setTrimestreCourant(appId);

			// avertit le CompteManager de mettre a jour les prévisions
			CompteManager.getInstance().refreshAllPrev();

		} catch (Exception e) {
			throw new ComptaException("Impossible de charger le trimestre courant", e);
		}

	}

	/**
	 * Extrait de la base un trimestre
	 * 
	 * @param appId L'id du trimestre
	 * @return
	 * @throws ComptaException Echec dans la récupération
	 */
	public AppTrimestre loadTrimestre(String appId) throws ComptaException {

		// recup des infos en base
		String[] info;
		AppTrimestre appTrimestre = null;

		try {
			// chargmement du trimestre
			info = DBManager.getInstance().getTrimestreInfo(appId);

			if (info != null) {

				// création du trimestre applicatif
				appTrimestre = new AppTrimestre(new Trimestre());
				appTrimestre.setAppID(info[0]);

				// chargement du 1er mois
				appTrimestre.premierMoisProperty().set(loadExerciceMensuel(info[1]));
				// chargement du 2eme mois
				appTrimestre.deuxiemeMoisProperty().set(loadExerciceMensuel(info[2]));
				// chargement du 3eme mois
				appTrimestre.troisiemeMoisProperty().set(loadExerciceMensuel(info[3]));
			}
		} catch (Exception e) {
			throw new ComptaException("Impossible de charger le trimestre", e);
		}

		return appTrimestre;

	}

	/**
	 * Crée un exercice mensuel applicatif depuis la base de donnée
	 * 
	 * @param id l'id de l'exercice mensuel
	 * @return l'exercice mensuel
	 * @throws ComptaException
	 */
	private AppExerciceMensuel loadExerciceMensuel(String id) throws ComptaException {

		AppExerciceMensuel appEm = null;

		try {

			if (id != null && !id.isEmpty()) {

				// récupération en base des donnée de l'exercice
				HashMap<String, ExerciceMensuel> infos = DBManager.getInstance().getExMensuelInfos(id);

				for (String key : infos.keySet()) {
					appEm = new AppExerciceMensuel(infos.get(key));
					appEm.setAppID(key);
				}

				// récupération des opérations
				HashMap<String, Operation> operations = DBManager.getInstance().getOperationInfo(appEm.getAppId());

				for (String iddep : operations.keySet()) {

					Operation oper = operations.get(iddep);

					if (oper instanceof TransfertOperation) {

						TransfertOperation trans = (TransfertOperation) oper;
						AppTransfert apptr = new AppTransfert(trans);
						apptr.setAppID(iddep);
						apptr.setCompteSrc(CompteManager.getInstance().getAppCompteFromCompte(trans.getCompte()));
						apptr.setCompteCible(
								CompteManager.getInstance().getAppCompteFromCompte(trans.getCompteCible()));
						appEm.addOperation(apptr);

					} else {

						AppOperation appOp = new AppOperation(oper);
						appOp.setAppID(iddep);
						appOp.setCompteSrc(CompteManager.getInstance().getAppCompteFromCompte(oper.getCompte()));
						appEm.addOperation(appOp);
					}

				}

			}

		} catch (

		Exception e) {
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
	 * @param dateDeb la date de début
	 * @param resPrev le résultat prévisionnel
	 * @return
	 * @throws ComptaException
	 */
	public AppTrimestre createTrimestre(LocalDate dateDeb) throws ComptaException {

		AppTrimestre appTrim = null;

		try {

			Trimestre trim = new Trimestre();
			appTrim = new AppTrimestre(trim);

			// création des exercice mensuel du trimestre
			for (int i = 0; i < 3; i++) {

				// création du modèle métier
				final ExerciceMensuel em = new ExerciceMensuel();
				// date de debut
				final LocalDate debut = dateDeb.plusMonths(i).withDayOfMonth(1);
				em.setDateDebut(debut);

				// date de fin
				final LocalDate fin = debut.withDayOfMonth(debut.lengthOfMonth());
				em.setDateFin(fin);

				// insertion de l'exercice mensuel en base
				double resPrev = TemplateService.getPrevFromtemplate();
				String idEm = DBManager.getInstance().addExerciceMensuel(debut, fin, resPrev);
				// création de l'exercice applicatif
				AppExerciceMensuel appEm = new AppExerciceMensuel(em);
				appEm.setAppID(idEm);

				// ajout des opérations du templates et calcul du prévisionnel
				TemplateService.applyTtemplate(appEm, i);

				// lien applicatif

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
	 * Retourne une Map contenant un peu d'informations sur les trimestres en base
	 * 
	 * @return une Map contenant clé:id Trimestre , value:dateDébut
	 * @throws ComptaException Erreur lors de la récupération des infos
	 */
	public HashMap<String, LocalDate> getAllTrimestreShortList() throws ComptaException {

		HashMap<String, LocalDate> res = new HashMap<>();

		try {

			ArrayList<String> ids = DBManager.getInstance().getAllTrimestreId();

			for (String id : ids) {

				// récupération de la date de début
				res.put(id, DBManager.getInstance().getDateDebutFromTrimestre(id));

			}
		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer la liste des trimestres", e);
		}

		return res;
	}

	/**
	 * Supprime un trimestre. La suppression est ignorée si le trimestre à supprimer
	 * est le trimestre courant
	 * 
	 * @param idTrimestre l'id du trimestre a supprimer
	 * @throws ComptaException Impossible de supprimer le trimestre
	 */
	public void removeTrimestre(String idTrimestre) throws ComptaException {

		if (_trimestreCourant.get() == null
				|| (_trimestreCourant.get() != null && !idTrimestre.equals(_trimestreCourant.get().getAppId()))) {

			DBManager.getInstance().removeTrimestre(idTrimestre);
		}

	}

	/**
	 * Indique si la chaine de caractère correspond au type d'opération : Transfert
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

				if (dep.getEtat().equals(EtatOperation.PREVISION.toString()) && dep.getCompteSource().equals(compte)) {
					delta = delta - dep.getMontant();
				}
			}
			// ajout des ressources
			for (AppOperation res : _trimestreCourant.getValue().getAppExerciceMensuel(numMois).get().getRessources()) {
				if (res.getEtat().equals(EtatOperation.PREVISION.toString()) && res.getCompteSource().equals(compte)) {
					delta = delta + res.getMontant();
				}

			}
			// prise en compte des transfert
			for (AppTransfert trans : _trimestreCourant.getValue().getAppExerciceMensuel(numMois).get()
					.getTransferts()) {
				// compte source : l'argent part
				if (trans.getEtat().equals(EtatOperation.PREVISION.toString())
						&& trans.getCompteSource().equals(compte)) {

					delta = delta - trans.getMontant();
				}
				// compte cible : l'argent rentre
				if (trans.getEtat().equals(EtatOperation.PREVISION.toString())
						&& trans.getCompteCible().equals(compte)) {
					delta = delta + trans.getMontant();
				}
			}
		}

		return delta;
	}

	/**
	 * Charge le trimestre courant précédemment enregistré en base. Cette méthode ne
	 * peut pas se mettre dans le constructeur car sinon il y a une boucle
	 * d'instanciation avec le CompteManager
	 * 
	 * @throws ComptaException
	 */
	public void recoverTrimestre() throws ComptaException {

		String id = DBManager.getInstance().getTrimestreCourantId();

		if (!id.isEmpty()) {
			TrimestreManager.getInstance().loadTrimestreCourant(id);
		}

	}

	/**
	 * Supprime une opération du mois du trimestre courant
	 * 
	 * @param appOp   l'opération
	 * @param numMois l'index du mois dans le trimestre
	 * @throws ComptaException
	 */
	public void removeOperation(AppOperation appOp, Integer numMois) throws ComptaException {

		// si l'opération est prise en compte, on l'annule
		if (appOp.getEtat().equals(EtatOperation.PRISE_EN_COMPTE.toString())) {
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
		DBManager.getInstance().removeOperation(appOp);

	}

	/**
	 * Crée une nouvelle opération dans le mois du trimestre courant correspondant à
	 * l'index
	 * 
	 * @param libelle     le libelle
	 * @param montant     le montant
	 * @param type        le type
	 * @param compteSrc   le compte source
	 * @param compteCible le compte cible si Transfert, null sinon
	 * @param numMois     l'index du mois
	 * @return
	 * @throws ComptaException Erreur lors de l'ajout
	 */
	public AppOperation addOperation(String libelle, double montant, String type, AppCompte compteSrc,
			AppCompte compteCible, int numMois) throws ComptaException {

		AppOperation appop = null;

		if (type.equals(OperationType.DEPENSE.toString())) {

			// dépense

			appop = OperationService.createDepense(libelle, montant, compteSrc,
					_trimestreCourant.get().getAppExerciceMensuel(numMois).get().getAppId());

		} else {

			if (type.equals(OperationType.RESSOURCE.toString())) {

				// ressource
				appop = OperationService.createRessource(libelle, montant, compteSrc,
						_trimestreCourant.get().getAppExerciceMensuel(numMois).get().getAppId());

			} else {
				if (type.equals(OperationType.TRANSFERT.toString())) {

					appop = OperationService.createTransfert(libelle, montant, compteSrc, compteCible,
							_trimestreCourant.get().getAppExerciceMensuel(numMois).get().getAppId());

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
	 * Retourne le résultat de l'excercice mensuel correspondant à l'index donné du
	 * trimestre courant
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
	 * Retourne les dépenses de l'excercice mensuel correspondant à l'index donné du
	 * trimestre courant
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
	 * Retourne les ressources de l'excercice mensuel correspondant à l'index donné
	 * du trimestre courant
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
	 * Retourne les transferts de l'excercice mensuel correspondant à l'index donné
	 * du trimestre courant
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
	 * @param idTrimestre id applicatif du trimestre
	 * @param numMois     le numéro du mois
	 * @return l'identifiant
	 * @throws ComptaException
	 */
	public String getExerciceMensuelId(String idTrimestre, int numMois) throws ComptaException {

		int idEm = DBManager.getInstance().getExerciceMensuelId(idTrimestre, numMois);

		return String.valueOf(idEm);
	}

	/**
	 * Déplacement une opération du trimestre courant
	 * 
	 * @param appOp       l'opération
	 * @param numMoisFrom le numéro du mois dans le trimestre courant
	 * @param appTrimIdTo l'id du trimestre ou elle sera déplacée
	 * @param numMoisTo   le numéro du mois ou elle sera déplacée
	 * @throws ComptaException
	 */
	public void moveOperationFromTrimCourant(AppOperation appOp, int numMoisFrom, AppExerciceMensuelLightId appLI)
			throws ComptaException {

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
		if (appLI.getTrimestreId().equals(_trimestreCourant.get().getAppId())) {

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
		DBManager.getInstance().moveOperation(appOp.getAppId(), appLI.getExerciceMensuelId());

	}

}
