package org.arthur.compta.lapin.application.service;

import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.application.model.OperationSearchResult;
import org.arthur.compta.lapin.dataaccess.db.OperationDataAccess;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.OperationType;

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
		OperationDataAccess.getInstance().updateOperation(appOp.getDBObject());

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
	public static AppOperation editOperation(AppOperation _operation, String newLib, double newMontant, AppCompte newCompteSrc, AppCompte newCompteCibles)
			throws ComptaException {

		// si l'opération est prise en compte, on la déselectionne
		boolean toSwitch = false;
		if (_operation.getEtat().equals(EtatOperation.PRISE_EN_COMPTE)) {
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
		OperationDataAccess.getInstance().updateOperation(_operation.getDBObject());

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
	public static List<OperationSearchResult> doSearch(String lib, String montant, String tolerance) throws ComptaException {

		return OperationDataAccess.getInstance().searchOperation(lib, montant, tolerance);
	}

}
