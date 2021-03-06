package org.arthur.compta.lapin.application.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.AppUtilisation;
import org.arthur.compta.lapin.dataaccess.db.BudgetDataAccess;
import org.arthur.compta.lapin.dataaccess.db.UtilisationDataAccess;
import org.arthur.compta.lapin.model.Budget;
import org.arthur.compta.lapin.model.Utilisation;

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
	/** logger */
	private final Logger logger;

	/** Comparateur de budget */

	/**
	 * Constructeur par défaut
	 */
	private BudgetManager() {

		logger = LogManager.getLogger(BudgetManager.class);

		_budgetList = FXCollections.observableArrayList();

		try {
			// récupération en base des métadonnée des budget
			List<Budget> fromPersistancy = BudgetDataAccess.getInstance().getActiveBudget();

			for (Budget budget : fromPersistancy) {

				// ajout dans l'application
				_budgetList.add(new AppBudget(budget));

			}

			calculateData();

		} catch (ComptaException e) {
			logger.fatal(e);
		}
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
		editBudget(appB, appB.getNom(), appB.getObjectif(), appB.getMontantUtilise(), false, appB.getPriority(), appB.getLabelRecurrent(),
				appB.getDateRecurrent());

	}

	/**
	 * Mets à jour les avancements et montant sur compte des budgets à jour
	 */
	public void calculateData() {

		double dispoCC = 0;
		double dispoCL = 0;

		// tri de la liste des bugdets par ordre de priorité
		List<AppBudget> tmp = new ArrayList<>();
		tmp.addAll(_budgetList);

		tmp.sort(new Comparator<AppBudget>() {

			@Override
			public int compare(AppBudget o1, AppBudget o2) {

				return Integer.compare(o1.getPriority(), o2.getPriority());
			}
		});

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

		for (final AppBudget budget : tmp) {

			if (budget.isActif()) {

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

	/**
	 * Ajoute un budget dans l'application
	 * 
	 * @param nom
	 *            le num du budget
	 * @param objectif
	 *            l'objectif a atteindre
	 * @param utilise
	 *            le montant utilise
	 * @param localDate
	 * @param labelRecurrent
	 * @return
	 * @throws ComptaException
	 *             Echec dans l'ajout du budget
	 */
	public AppBudget addBudget(String nom, double objectif, double utilise, String labelRecurrent, LocalDate dateRecurrent) throws ComptaException {

		// encapsulation applicative
		AppBudget appB = new AppBudget(BudgetDataAccess.getInstance().addBudget(nom, objectif, utilise, true, _budgetList.size(), labelRecurrent, dateRecurrent));

		// ajout dans l'application
		_budgetList.add(appB);
		calculateData();

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
	 * @param prio
	 *            la nouvelle priorité
	 * @param dateRecurrent
	 * @param labelRecurrent
	 * @return
	 * @throws ComptaException
	 */
	public AppBudget editBudget(AppBudget appBudget, String nom, double objectif, double utilise, boolean isActif, int prio, String labelRecurrent,
			LocalDate dateRecurrent) throws ComptaException {

		if (appBudget != null) {

			try {
				// modification dans l'application
				appBudget.setNom(nom);
				appBudget.setObjectif(objectif);
				appBudget.setMontantUtilise(utilise);
				appBudget.setIsActif(isActif);
				appBudget.setPriority(prio);
				appBudget.setLabelerecurrent(labelRecurrent);
				appBudget.setDateReccurent(dateRecurrent);
				// modif du prévisionnel des Budgets
				calculateData();
				// écriture en base
				BudgetDataAccess.getInstance().updateBudget(appBudget.getDBObject());

			} catch (Exception e) {
				throw new ComptaException("Impossible de mettre à jour le budget", e);
			}
		}

		return appBudget;

	}

	/**
	 * Retourne tous les budgets
	 * 
	 * @return
	 * @throws ComptaException
	 *             Echec de la récupération
	 */
	public List<AppBudget> getAllBudgets() throws ComptaException {

		ArrayList<AppBudget> list = new ArrayList<>();

		// récupération en base des métadonnée des budget
		List<Budget> fromPersistancy = BudgetDataAccess.getInstance().getAllBudget();

		for (Budget bud : fromPersistancy) {

			// encapsulation applicative et ajout dans la liste
			list.add(new AppBudget(bud));
		}

		return list;
	}

	/**
	 * Ajoute une utilisation pour le budget
	 * 
	 * @param appB
	 *            le budget
	 * @param nom
	 *            le nom de l'utilisation
	 * @param montat
	 *            le montant
	 * @param date
	 *            la date
	 * @throws ComptaException
	 */
	public void addUtilisationForBudget(AppBudget appB, String nom, double montat, LocalDate date) throws ComptaException {

		// enregistrement de l'utilisation
		UtilisationDataAccess.getInstance().addUtilisationForBudget(appB.getAppId(), nom, montat, date);

		// modification du montant utilisé
		appB.setMontantUtilise(appB.getMontantUtilise() + montat);
		// sauvegarde du budget
		BudgetDataAccess.getInstance().updateBudget(appB.getDBObject());
		// MaJ des avancements
		calculateData();

	}

	/**
	 * Retourne les utilisations du budget
	 * 
	 * @param appId
	 *            l'id du budget
	 * @return
	 * @throws ComptaException
	 */
	public List<AppUtilisation> getUtilisation(int appId) throws ComptaException {

		ArrayList<AppUtilisation> listeRes = new ArrayList<>();
		// récupération des champs en base

		List<Utilisation> fromPersist = UtilisationDataAccess.getInstance().getUtilisationInfos(appId);

		for (Utilisation util : fromPersist) {
			// Création de l'utilisation applicative et ajout au resultat
			listeRes.add(new AppUtilisation(util));

		}

		return listeRes;
	}

	/**
	 * Edite une utilisation
	 * 
	 * @param utilisation
	 * @param nom
	 * @param montant
	 * @param date
	 * @throws ComptaException
	 */
	public void editUtilisation(AppUtilisation utilisation, String nom, double montant, LocalDate date) throws ComptaException {

		// affectation des nouvelles valeurs
		utilisation.setNom(nom);
		utilisation.setMontant(montant);
		utilisation.setDate(date);

		// MaJ en base
		UtilisationDataAccess.getInstance().upDateUtilisation(utilisation);

	}

	/**
	 * Supprime une utilisation
	 * 
	 * @param util
	 * @throws ComptaException
	 */
	public void removeUtilisation(AppUtilisation util) throws ComptaException {

		UtilisationDataAccess.getInstance().removeUtilisation(util.getAppId());

	}

	/**
	 * Supprime un budget et ses utilisations
	 * 
	 * @param appB
	 * @throws ComptaException
	 */
	public void removeBudget(AppBudget appB) throws ComptaException {

		BudgetDataAccess.getInstance().removeBudget(appB);

		// suppression de la liste ( le remove ne marche pas)
		Iterator<AppBudget> iter = _budgetList.iterator();
		boolean goOn = true;
		while (iter.hasNext() && goOn) {

			AppBudget bud = iter.next();
			if (bud.getAppId() == appB.getAppId()) {
				iter.remove();
				goOn = false;
			}

		}

	}

	/**
	 * Retourne la list des budgets récurrent
	 * 
	 * @return
	 * @throws ComptaException
	 */
	public List<String> getLabelRecurrentList() throws ComptaException {

		return BudgetDataAccess.getInstance().getLabelRecurrentList();
	}

	/**
	 * Ajoute un label de budget récurrent dans l'application
	 * 
	 * @param labelRec
	 * @throws ComptaException
	 */
	public void addLabelRecurrent(String labelRec) throws ComptaException {
		BudgetDataAccess.getInstance().addLabelRecurrent(labelRec);

	}

	/**
	 * Mets a jour la liste des budgets actif (l'ordre surtout)
	 * 
	 * @param _activesBudgets
	 * @throws ComptaException
	 */
	public void updateBudgets(ObservableList<AppBudget> _activesBudgets) throws ComptaException {

		ArrayList<Budget> tmp = new ArrayList<Budget>();
		for (AppBudget appB : _activesBudgets) {
			tmp.add(appB.getDBObject());
		}

		BudgetDataAccess.getInstance().updateBudgets(tmp);

	}

}
