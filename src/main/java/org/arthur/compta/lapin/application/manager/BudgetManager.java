package org.arthur.compta.lapin.application.manager;

import java.util.HashMap;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.dataaccess.db.DBManager;
import org.arthur.compta.lapin.model.Budget;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Gestionnaire des budgets
 *
 */
public class BudgetManager {

	/** Instance unique du singleton */
	private static BudgetManager _instance;
	/** La liste des budgets actifs */
	private ObservableList<AppBudget> _budgetList;

	/**
	 * Constructeur par défaut
	 */
	private BudgetManager() {

		_budgetList = FXCollections.observableArrayList();

		try {
			// récupération en base des métadonnée des budget
			HashMap<String, String[]> fromPersistancy = DBManager.getInstance().getActiveBudget();

			for (String id : fromPersistancy.keySet()) {

				String[] info = fromPersistancy.get(id);
				// création du modèle
				Budget budget = new Budget();
				budget.setNom(info[0]);
				budget.setMontantUtilise(Double.parseDouble(info[1]));
				budget.setObjectif(Double.parseDouble(info[2]));

				// encapsulation applicative
				AppBudget appB = new AppBudget(budget);
				appB.setAppID(id);

				// ajout dans l'application
				_budgetList.add(appB);

			}
		} catch (ComptaException e) {
			e.printStackTrace();
		}

		// on calcule les avancements
		calculateData();

	}

	/**
	 * Retourne l'instance unique du singleton
	 * 
	 * @return
	 */
	public static BudgetManager getInstance() {

		if (_instance == null) {
			_instance = new BudgetManager();
		}

		return _instance;

	}

	/**
	 * Retourne la liste des budgets
	 * 
	 * @return
	 */
	public ObservableList<AppBudget> getBudgetList() {

		return _budgetList;
	}

	/**
	 * Désactive le budget de l'application
	 * 
	 * @param appB
	 */
	public void desactivateBudget(AppBudget appB) throws ComptaException {

		_budgetList.remove(appB);
		editBudget(appB, appB.getNom(), appB.getObjectif(), appB.getMontantUtilise(), appB.isActif());

	}

	/**
	 * Mets à jour les avancements et montant sur compte des budgets à jour
	 */
	public void calculateData() {

		double dispoCC = 0;
		double dispoCL = 0;

		// on calcul le solde disponible sur les différents comptes à la fin du
		// trimestre
		for (final AppCompte compte : CompteManager.getInstance().getCompteList()) {

			if (compte.isBudget()) {
				if (compte.isLivret()) {
					dispoCL = dispoCL + compte.getSoldePrev3();
				} else {
					dispoCC = dispoCC + compte.getSoldePrev3();
				}
			}

		}

		for (final AppBudget budget : _budgetList) {

			double cour = 0;
			double liv = 0;

			// montant déjà utilisé on le comptabilise comme faisant déjà
			// partie du budget
			double inBudget = budget.getMontantUtilise();

			// on vérifie si les livrets peuvent remplir le budget
			if ((dispoCL - (budget.getObjectif() - inBudget)) >= 0) {
				// oui , on déduit le montant du budget
				dispoCL = dispoCL - (budget.getObjectif() - inBudget);
				// calcul du montant sur compte livret ( tout le restant)
				liv = budget.getObjectif() - inBudget;
				// calcul du montant dans le budget
				inBudget = inBudget + liv;
			} else {
				// non, le montant sur livret est donc le solde restant sur
				// les livrets
				liv = dispoCL;
				// on complète avec les comptes courants
				dispoCL = 0;
				// solde de départ dans le budget
				inBudget = inBudget + liv;

				// on vérifie si les courant peuvent remplir le budget
				if ((dispoCC - (budget.getObjectif() - inBudget)) >= 0) {
					// oui, on déduit le montant du budget au solde des
					// courants
					dispoCC = dispoCC - (budget.getObjectif() - inBudget);
					// calcul du montant sur compte livret ( tout le
					// restant)
					cour = budget.getObjectif() - inBudget;
					// calcul du montant dans le budget
					inBudget = inBudget + cour;
				} else {

					// on mets le reste du solde des courants dans le solde
					// sur courant du budget
					cour = dispoCC;
					dispoCC = 0;
					inBudget = inBudget + cour;
				}

			}

			budget.setAvancement(inBudget / budget.getObjectif());
			budget.setMontantCourant(cour);
			budget.setMontantLivret(liv);

		}

	}

	/**
	 * Ajoute un budget dans l'application
	 * 
	 * @param nom
	 *            le num du budget
	 * @param objectif
	 *            l'objectif a atteindre
	 * @param utilise
	 *            le montant utilise
	 * @return
	 * @throws ComptaException
	 *             Echec dans l'ajout du budget
	 */
	public AppBudget addBudget(String nom, double objectif, double utilise) throws ComptaException {

		AppBudget appB = null;

		try {
			// création du modèle
			Budget budget = new Budget();
			budget.setNom(nom);
			budget.setMontantUtilise(objectif);
			budget.setObjectif(utilise);
			budget.setIsActif(true);

			// encapsulation applicative
			appB = new AppBudget(budget);
			String id = DBManager.getInstance().addBudget(nom, objectif, utilise, true);
			appB.setAppID(id);

			// ajout dans l'application
			_budgetList.add(appB);
			calculateData();
		} catch (Exception e) {
			throw new ComptaException("Impossible d'ajouter le compte dans l'application", e);
		}

		return appB;
	}

	/**
	 * Modifie le budget
	 * 
	 * @param appBudget
	 *            le budget
	 * @param nom
	 *            le nouveau nom
	 * @param objectif
	 *            le nouvel objectif
	 * @param utilise
	 *            le nouveau montant utilise
	 * @param isActif
	 *            la nouvelle valeur actif
	 * @return
	 * @throws ComptaException
	 */
	public AppBudget editBudget(AppBudget appBudget, String nom, double objectif, double utilise, boolean isActif)
			throws ComptaException {

		if (appBudget != null) {

			try {
				// modification dans l'application
				appBudget.setNom(nom);
				appBudget.setObjectif(objectif);
				appBudget.setMontantUtilise(utilise);
				appBudget.setIsActif(isActif);
				// modif du prévisionnel  des Budgets
				calculateData();
				// écriture en base
				DBManager.getInstance().editBudget(appBudget);

			} catch (Exception e) {
				throw new ComptaException("Impossible de mettre à jour le budget", e);
			}
		}

		return appBudget;

	}

}
