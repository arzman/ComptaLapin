package org.arthur.compta.lapin.application.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.AppExerciceMensuel;
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
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

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

				// avertit le CompteManager de mettre a jour les prévisions
				CompteManager.getInstance().refreshAllPrev();

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
				// date fin
				Calendar fin = Calendar.getInstance();
				fin.setTime(ApplicationFormatter.databaseDateFormat.parse(infos[2]));
				em.setDateFin(fin);

				appEm = new AppExerciceMensuel(em);
				appEm.setAppID(infos[0]);

				// récupération des opérations
				HashMap<String, String[]> infosDep = DBManager.getInstance().getOperationInfo(appEm.getAppId());

				for (String iddep : infosDep.keySet()) {

					String[] infodep = infosDep.get(iddep);

					// depense
					if (infodep[2].equals(OperationType.DEPENSE.toString())) {
						Operation dep = new Operation(OperationType.DEPENSE,
								CompteManager.getInstance().getCompte(infodep[4]).getCompte(), infodep[0],
								Double.parseDouble(infodep[1]), EtatOperation.valueOf(infodep[3]));

						AppOperation appDep = new AppOperation(dep);
						appDep.setAppID(iddep);
						appDep.setCompteSrc(CompteManager.getInstance().getCompte(infodep[4]));

						appEm.addDepense(appDep);
					} else {
						// ressource
						if (infodep[2].equals(OperationType.RESSOURCE.toString())) {
							Operation res = new Operation(OperationType.RESSOURCE,
									CompteManager.getInstance().getCompte(infodep[4]).getCompte(), infodep[0],
									Double.parseDouble(infodep[1]), EtatOperation.valueOf(infodep[3]));

							// Ajout dans l'appli
							AppOperation appRes = new AppOperation(res);
							appRes.setAppID(iddep);
							appRes.setCompteSrc(CompteManager.getInstance().getCompte(infodep[4]));
							appEm.addRessource(appRes);
						} else {
							// transfert
							if (infodep[2].equals(OperationType.TRANSFERT.toString())) {
								TransfertOperation trans = new TransfertOperation(
										CompteManager.getInstance().getCompte(infodep[4]).getCompte(), infodep[0],
										Double.parseDouble(infodep[1]), EtatOperation.valueOf(infodep[3]),
										CompteManager.getInstance().getCompte(infodep[5]).getCompte());

								AppTransfert apptr = new AppTransfert(trans);
								apptr.setAppID(iddep);
								apptr.setCompteSrc(CompteManager.getInstance().getCompte(infodep[4]));
								apptr.setCompteCible(CompteManager.getInstance().getCompte(infodep[5]));

								appEm.addTransfert(apptr);
							}
						}
					}

				}

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
				TemplateService.applyTtemplate(appEm, i);

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
	 * Charge le trimestre courant précédemment enregistré en base. Cette
	 * méthode ne peut pas se mettre dans le constructeur car sinon il y a une
	 * boucle d'instanciation avec le CompteManager
	 */
	public void recoverTrimestre() {
		try {
			String[] info = DBManager.getInstance().getTrimestreCourantId();
			if (info != null && info.length == 1 && info[0] != null && !info[0].isEmpty()) {
				TrimestreManager.getInstance().loadTrimestreCourant(info[0]);
			}

		} catch (Exception e) {
			e.printStackTrace();
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
	public AppOperation addOperation(String libelle, double montant, String type, AppCompte compteSrc,
			AppCompte compteCible, int numMois) throws ComptaException {

		AppOperation appop = null;

		if (type.equals(OperationType.DEPENSE.toString())) {

			// dépense

			appop = OperationService.createDepense(libelle, montant, compteSrc,
					_trimestreCourant.get().getAppExerciceMensuel(numMois).get().getAppId());
			_trimestreCourant.get().getAppExerciceMensuel(numMois).get().addDepense(appop);

		} else {

			if (type.equals(OperationType.RESSOURCE.toString())) {

				// ressource
				appop = OperationService.createRessource(libelle, montant, compteSrc,
						_trimestreCourant.get().getAppExerciceMensuel(numMois).get().getAppId());
				_trimestreCourant.get().getAppExerciceMensuel(numMois).get().addRessource(appop);

			} else {
				if (type.equals(OperationType.TRANSFERT.toString())) {

					appop = OperationService.createTransfert(libelle, montant, compteSrc, compteCible,
							_trimestreCourant.get().getAppExerciceMensuel(numMois).get().getAppId());
					_trimestreCourant.get().getAppExerciceMensuel(numMois).get().addTransfert((AppTransfert) appop);

				}
			}

		}

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
	public Date getDateDebut(int numMois) {

		Date dat = null;

		if (_trimestreCourant != null) {

			dat = _trimestreCourant.get().getAppExerciceMensuel(numMois).get().getDateDebut().getTime();

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

		if (_trimestreCourant != null) {

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

		if (_trimestreCourant != null) {

			res = _trimestreCourant.get().getAppExerciceMensuel(numMois).get().getDepenses();

		}

		return res;
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

		if (_trimestreCourant != null) {

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

		if (_trimestreCourant != null) {

			res = _trimestreCourant.get().getAppExerciceMensuel(numMois).get().getTransferts();

		}

		return res;
	}

}
