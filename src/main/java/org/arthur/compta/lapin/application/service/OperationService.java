package org.arthur.compta.lapin.application.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.application.model.OperationSearchResult;
import org.arthur.compta.lapin.dataaccess.db.DBManager;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.Operation;
import org.arthur.compta.lapin.model.operation.OperationType;
import org.arthur.compta.lapin.model.operation.TransfertOperation;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class OperationService {

	/**
	 * Permutte l'état d'une opération et répercute les conséquences (
	 * sauvegarde DB + re-calcul du solde compte)
	 * 
	 * @param appOp
	 *            l'operation
	 * @throws ComptaException
	 *             Echec de la mise à jour
	 */
	public static void switchEtatOperation(AppOperation appOp) throws ComptaException {

		// Maj de l'état dans l'application
		appOp.switchEtat();
		// re-calcul du solde du compte
		CompteManager.getInstance().operationSwitched(appOp);
		// sauvegarde en base
		DBManager.getInstance().updateOperation(appOp);

	}

	/**
	 * Retourne les types possibles pour une opération
	 * 
	 * @return
	 */
	public static ObservableList<String> getOperationType() {
		// creation de la liste
		ObservableList<String> res = FXCollections.observableArrayList();
		// on récupère les valeurs de l'enum
		for (OperationType opeType : OperationType.values()) {
			res.add(opeType.toString());
		}

		return res;
	}

	/**
	 * Crée une dépense applicative et la sauve en base
	 * 
	 * @param compteSrc
	 *            le compte source de l'opération
	 * @param libelle
	 *            le libelle
	 * @param montant
	 *            le montant
	 * @param appMoisId
	 *            l'id applicatif de l'exercice mensuel
	 * @return
	 * @throws ComptaException
	 */
	public static AppOperation createDepense(String libelle, double montant, AppCompte compteSrc, String appMoisId)
			throws ComptaException {

		Operation op = new Operation(OperationType.DEPENSE, compteSrc.getCompte(), libelle, montant,
				EtatOperation.PREVISION);

		AppOperation appop = new AppOperation(op);
		appop.setCompteSrc(compteSrc);
		String id = DBManager.getInstance().addOperation(op, appop.getCompteSource().getAppId(), null, appMoisId);
		appop.setAppID(id);

		return appop;
	}

	/**
	 * Crée une ressource applicative et la sauve en base
	 * 
	 * @param compteSrc
	 *            le compte source de l'opération
	 * @param libelle
	 *            le libelle
	 * @param montant
	 *            le montant
	 * @param appMoisId
	 *            l'id applicatif de l'exercice mensuel
	 * @return
	 * @throws ComptaException
	 */
	public static AppOperation createRessource(String libelle, double montant, AppCompte compteSrc, String appId)
			throws ComptaException {

		Operation op = new Operation(OperationType.RESSOURCE, compteSrc.getCompte(), libelle, montant,
				EtatOperation.PREVISION);

		AppOperation appop = new AppOperation(op);
		appop.setCompteSrc(compteSrc);
		String id = DBManager.getInstance().addOperation(op, appop.getCompteSource().getAppId(), null, appId);
		appop.setAppID(id);

		return appop;
	}

	public static AppOperation createTransfert(String libelle, double montant, AppCompte compteSrc,
			AppCompte compteCible, String appId) throws ComptaException {

		TransfertOperation trans = new TransfertOperation(compteSrc.getCompte(), libelle, montant,
				EtatOperation.PREVISION, compteCible.getCompte());
		AppTransfert appop = new AppTransfert(trans);
		appop.setCompteSrc(compteSrc);
		((AppTransfert) appop).setCompteCible(compteCible);
		String id = DBManager.getInstance().addOperation(trans, compteSrc.getAppId(), compteCible.getAppId(), appId);
		appop.setAppID(id);

		return appop;
	}

	/**
	 * Edite l'opération passé en paramètre
	 * 
	 * @param _operation
	 * @param newLib
	 * @param newMontant
	 * @param newCompteSrc
	 * @param newCompteCibles
	 * @return
	 * @throws ComptaException
	 */
	public static AppOperation editOperation(AppOperation _operation, String newLib, double newMontant,
			AppCompte newCompteSrc, AppCompte newCompteCibles) throws ComptaException {

		// si l'opération est prise en compte, on la déselectionne
		boolean toSwitch = false;
		if (_operation.getEtat().equals(EtatOperation.PRISE_EN_COMPTE.toString())) {
			toSwitch = true;
			switchEtatOperation(_operation);
		}

		// modification de l'opération
		_operation.setLibelle(newLib);
		_operation.setMontant(newMontant);
		_operation.setCompteSrc(newCompteSrc);

		if (_operation instanceof AppTransfert) {
			((AppTransfert) _operation).setCompteCible(newCompteCibles);
		}

		// enregistrement en base
		DBManager.getInstance().updateOperation(_operation);

		if (toSwitch) {
			switchEtatOperation(_operation);
		}

		// refresh du previsionnel
		CompteManager.getInstance().calculateSoldePrev(newCompteSrc);
		CompteManager.getInstance().calculateSoldePrev(newCompteCibles);

		return _operation;
	}

	/**
	 * Effectue une recherche sur les opérations.
	 * 
	 * Retourne les operation dont le libellé contient lib et dont le montant
	 * est égale à montant +- tolerance
	 * 
	 * Si un des champs est vide, alors il est ignoré
	 * 
	 * @param lib
	 * @param montant
	 * @param tolerance
	 * @return
	 * @throws ComptaException
	 */
	public static List<OperationSearchResult> doSearch(String lib, String montant, String tolerance)
			throws ComptaException {

		ArrayList<OperationSearchResult> res = new ArrayList<>();

		try {
			
			HashMap<String, String[]> infos = DBManager.getInstance().searchOperation(lib, montant, tolerance);

			for (String idOp : infos.keySet()) {

				String[] info = infos.get(idOp);
				OperationSearchResult opRes = new OperationSearchResult(info[0], Double.parseDouble(info[1]),
						ApplicationFormatter.databaseDateFormat.parse(info[2]));
				res.add(opRes);

			}
		} catch (Exception e) {
			throw new ComptaException("Echec de la recherche", e);
		}

		return res;
	}

}
