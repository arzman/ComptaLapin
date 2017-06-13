package org.arthur.compta.lapin.application.manager;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.application.model.AppCompte;
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
	/** La liste des budgets */
	private ObservableList<AppBudget> _budgetList;

	/**
	 * Constructeur par défaut
	 */
	private BudgetManager() {

		_budgetList = FXCollections.observableArrayList();

		Budget buf = new Budget();
		buf.setNom("Vacances");
		buf.setMontantUtilise(30);
		buf.setObjectif(1000);

		AppBudget appB = new AppBudget(buf);

		_budgetList.add(appB);

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
	 * Supprime le budget de l'application
	 * 
	 * @param appB
	 */
	public void desactivateBudget(AppBudget appB) throws ComptaException {

		_budgetList.remove(appB);
		// TODO supprimer de la base

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

}
