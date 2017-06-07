package org.arthur.compta.lapin.application.manager;

import java.sql.SQLException;
import java.util.HashMap;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.dataaccess.db.DBManager;
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

	/**
	 * Instance unique du singleton
	 */
	private static CompteManager _instance;

	/**
	 * La liste des comptes géré par l'application
	 */
	private ObservableList<AppCompte> _compteList;

	/**
	 * Constructeur
	 */
	private CompteManager() {

		// liste observable de compte, le contenu est observé également
		_compteList = FXCollections.observableArrayList();

		try {
			// récupération en base des métadonnée des comptes
			HashMap<String, String[]> fromPersistancy = DBManager.getInstance().getAllCompte();

			for (String id : fromPersistancy.keySet()) {

				// création du modèle
				Compte compte = new Compte(fromPersistancy.get(id)[0]);
				compte.setSolde(Double.parseDouble(fromPersistancy.get(id)[1]));
				compte.setLivret(Boolean.parseBoolean(fromPersistancy.get(id)[2]));
				compte.setBudgetAllowed(Boolean.parseBoolean(fromPersistancy.get(id)[3]));
				// encapsulation applicative
				AppCompte appC = new AppCompte(compte);
				appC.setAppID(id);

				// ajout dans l'application
				_compteList.add(appC);

			}
		} catch (SQLException e) {
			e.printStackTrace();
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
	public AppCompte createCompte(String nom, double solde, boolean livret, boolean budgetAllowed)
			throws ComptaException {

		AppCompte appC = null;

		try {
			// ajout en base
			String id = DBManager.getInstance().addCompte(nom, solde, livret, budgetAllowed);

			// vérification naive du retour
			if (id.trim().isEmpty()) {
				throw new ComptaException("Impossible de créer le compte : id applicatif vide");
			}

			// création du modèle
			Compte compte = new Compte(nom);
			compte.setSolde(solde);
			compte.setLivret(livret);
			compte.setBudgetAllowed(budgetAllowed);
			// encapsulation applicative
			appC = new AppCompte(compte);
			appC.setAppID(id);
			// ajout dans l'application
			_compteList.add(appC);
		} catch (Exception e) {
			throw new ComptaException("Impossible de créer le compte", e);
		}

		return appC;
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

			try {
				// suppression en base
				DBManager.getInstance().removeCompte(appCompte.getAppId());
				// suppression de l'appli
				_compteList.remove(appCompte);
			} catch (Exception e) {
				throw new ComptaException("Impossible de supprimer le compte", e);
			}

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
	public AppCompte updateCompte(AppCompte appCompte, String nom, double solde, boolean isLivret, boolean isBudget)
			throws ComptaException {

		if (appCompte != null) {

			try {
				// modification dans l'application
				appCompte.setNom(nom);
				appCompte.setSolde(solde);
				appCompte.setIsLivret(isLivret);
				appCompte.setIsBudget(isBudget);
				// modif du prévisionnel
				calculateSoldePrev(appCompte);
				// écriture en base
				DBManager.getInstance().updateCompte(appCompte);

			} catch (Exception e) {
				throw new ComptaException("Impossible de mettre à jour le compte", e);
			}
		}

		return appCompte;
	}

	/**
	 * Retourne le compte correspondant à l'id
	 * 
	 * @param id
	 * @return
	 */
	public AppCompte getCompte(String id) {

		AppCompte res = null;

		boolean stop = false;
		for (int i = 0; i < _compteList.size() && !stop; i++) {

			if (_compteList.get(i).getAppId().equals(id)) {
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
		if (appOp.getEtat().equals(EtatOperation.PRISE_EN_COMPTE.toString())) {
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

		DBManager.getInstance().updateCompte(appOp.getCompteSource());

		// compte cible si transfert
		if (appOp instanceof AppTransfert) {
			double soldeCibleInit = ((AppTransfert) appOp).getCompteCible().getSolde();
			((AppTransfert) appOp).getCompteCible().setSolde(soldeCibleInit - delta);
			DBManager.getInstance().updateCompte(((AppTransfert) appOp).getCompteCible());
		}

	}

}
