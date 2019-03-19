package org.arthur.compta.lapin.application.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.dataaccess.db.CompteDataAcces;
import org.arthur.compta.lapin.model.Compte;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.OperationType;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Singleton de gestion des AppCompte
 *
 */
public class CompteManager {

	/** Instance unique du singleton */
	private static CompteManager _instance;

	/** La liste des comptes géré par l'application */
	private ObservableList<AppCompte> _compteList;

	/** logger */
	private final Logger _logger;


	/**
	 * Constructeur
	 */
	private CompteManager() {

		// liste observable de compte, le contenu est observé également
		_compteList = FXCollections.observableArrayList();

		_logger = LogManager.getLogger(CompteDataAcces.class);

		try {
			// récupération en base des métadonnée des comptes
			List<Compte> fromPersistancy = CompteDataAcces.getInstance().getAllCompte();

			for (Compte cpt : fromPersistancy) {

				// ajout dans l'application
				_compteList.add(new AppCompte(cpt));

			}
		} catch (ComptaException e) {
			_logger.fatal(e);
		}

	}

	/**
	 * Retourne l'instance unique du singleton
	 * 
	 * @return l'instance du singleton
	 */
	public static CompteManager getInstance() {

		if (_instance == null) {
			_instance = new CompteManager();
		}

		return _instance;
	}

	/**
	 * Retourne la liste des comptes applicatif
	 * 
	 * @return la liste des comptes applicatif
	 */
	public ObservableList<AppCompte> getCompteList() {
		return _compteList;
	}

	/**
	 * Création d'un nouveau compte
	 * 
	 * @param nom
	 *            le nom du compte
	 * @param solde
	 *            le solde du compte
	 * @param livret
	 *            est livret ?
	 * @param budgetAllowed
	 *            budget autorisé ?
	 * @return le compte application créé
	 * @throws ComptaException
	 *             Exception si la création a échoué
	 */
	public AppCompte addCompte(String nom, double solde, boolean livret, boolean budgetAllowed) throws ComptaException {

		// encapsulation applicative et ajout dans l'application
		AppCompte appCpt = new AppCompte(CompteDataAcces.getInstance().addCompte(nom, solde, livret, budgetAllowed));
		_compteList.add(appCpt);

		return appCpt;

	}

	/**
	 * Suppression d'un compte
	 * 
	 * @param appCompte
	 *            le compte a supprimer
	 * @throws ComptaException
	 *             La suppression a échouer
	 */
	public void removeCompte(AppCompte appCompte) throws ComptaException {

		if (appCompte != null) {

			// suppression en base
			CompteDataAcces.getInstance().removeCompte(appCompte.getAppId());
			// suppression de l'appli
			_compteList.remove(appCompte);

		} else {
			_logger.warn("Tentativement de suppression d'un compte null");
		}

	}

	/**
	 * Met à jour le compte
	 * 
	 * @param appCompte
	 *            le compte application associé
	 * @param nom
	 *            le nouveau nom
	 * @param solde
	 *            le nouveau solde
	 * @param isLivret
	 *            le nouveau flag isLivret
	 * @param isBudget
	 *            le nouveau floag isBudget
	 * @return le compte applicatif mis à jour
	 * @throws ComptaException
	 *             La mise à jour a échouée
	 */
	public void editCompte(AppCompte appCompte, String nom, double solde, boolean isLivret, boolean isBudget) throws ComptaException {

		if (appCompte != null) {

			// modification dans l'application
			appCompte.setNom(nom);
			appCompte.setSolde(solde);
			appCompte.setIsLivret(isLivret);
			appCompte.setIsBudget(isBudget);
			// modif du prévisionnel et notification des Budgets
			calculateSoldePrev(appCompte);
			// écriture en base
			CompteDataAcces.getInstance().updateCompte(appCompte.getDBObject());

		} else {
			_logger.warn("Tentative d'édition d'un compte null");
		}

	}

	public AppCompte getAppCompteFromId(int appId) {

		AppCompte res = null;

		boolean stop = false;
		for (int i = 0; i < _compteList.size() && !stop; i++) {

			if (_compteList.get(i).getAppId() == appId) {
				res = _compteList.get(i);
				stop = true;
			}

		}

		return res;
	}

	/**
	 * Met à jour les prévisions de tout les comptes
	 */
	public void refreshAllPrev() {

		for (AppCompte compte : _compteList) {
			// calcul des soldes prévisionnels
			calculateSoldePrev(compte);
		}

	}

	/**
	 * Calcule les différents solde prévisionnel pour le compte donné
	 * 
	 * @param compte
	 *            le compte , peut être null
	 */
	public void calculateSoldePrev(AppCompte compte) {
		// ajout des prévisions
		if (compte != null) {

			double delta1 = TrimestreManager.getInstance().getDeltaForCompte(compte, 0);
			compte.soldePrev1Property().set(compte.getSolde() + delta1);
			double delta2 = TrimestreManager.getInstance().getDeltaForCompte(compte, 1);
			compte.soldePrev2Property().set(compte.getSolde() + delta1 + delta2);
			double delta3 = TrimestreManager.getInstance().getDeltaForCompte(compte, 2);
			compte.soldePrev3Property().set(compte.getSolde() + delta1 + delta2 + delta3);

			// Les soldes prévisionnels ont p-e changé, on notifie le
			// BudgetManager
			BudgetManager.getInstance().calculateData();
		}

	}

	/**
	 * Prise en compte du changement d'état d'une opération
	 * 
	 * @param appOp
	 *            l'opération
	 * @throws SQLException
	 *             Erreur lors de l'opération
	 */
	public void operationSwitched(AppOperation appOp) throws ComptaException {

		double etatMod;
		if (appOp.getEtat().equals(EtatOperation.PRISE_EN_COMPTE)) {
			etatMod = -1.0;
		} else {
			etatMod = 1.0;
		}

		double typeMode;
		if (appOp.getType().equals(OperationType.RESSOURCE)) {
			typeMode = -1.0;
		} else {
			typeMode = 1.0;
		}

		double soldeSrcInit = appOp.getCompteSource().getSolde();
		double delta = etatMod * typeMode * appOp.getMontant();
		appOp.getCompteSource().setSolde(soldeSrcInit + delta);

		CompteDataAcces.getInstance().updateCompte(appOp.getCompteSource().getDBObject());

		// compte cible si transfert
		if (appOp instanceof AppTransfert) {
			double soldeCibleInit = ((AppTransfert) appOp).getCompteCible().getSolde();
			((AppTransfert) appOp).getCompteCible().setSolde(soldeCibleInit - delta);
			CompteDataAcces.getInstance().updateCompte(((AppTransfert) appOp).getCompteCible().getDBObject());
		}

	}

	/**
	 * Retourne la liste des noms de compte déjà existant
	 * 
	 * @return
	 */
	public List<String> getCompteNameList() {

		ArrayList<String> res = new ArrayList<>();

		for (AppCompte cpt : _compteList) {
			res.add(cpt.getNom());
		}

		return res;
	}

}
