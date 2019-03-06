package org.arthur.compta.lapin.application.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.AppUtilisation;
import org.arthur.compta.lapin.dataaccess.db.DBManager;
import org.arthur.compta.lapin.model.Budget;
import org.arthur.compta.lapin.model.Utilisation;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

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

	/** Comparateur de budget */

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
				budget.setMontantUtilise(Double.parseDouble(info[2]));
				budget.setObjectif(Double.parseDouble(info[1]));
				budget.setPriority(Integer.parseInt(info[3]));
				budget.setIsActif(true);
				budget.setLabelRecurrent(info[4]);
				budget.setDateRecurrent(LocalDate.parse(info[5], ApplicationFormatter.databaseDateFormat));

				// encapsulation applicative
				AppBudget appB = new AppBudget(budget);
				appB.setAppID(id);

				// ajout dans l'application
				_budgetList.add(appB);

			}

			calculateData();

		} catch (ComptaException e) {
			e.printStackTrace();
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
		editBudget(appB, appB.getNom(), appB.getObjectif(), appB.getMontantUtilise(), false, appB.getPriority(),
				appB.getLabelRecurrent(), appB.getDateRecurrent());

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
	 * @param nom            le num du budget
	 * @param objectif       l'objectif a atteindre
	 * @param utilise        le montant utilise
	 * @param localDate
	 * @param labelRecurrent
	 * @return
	 * @throws ComptaException Echec dans l'ajout du budget
	 */
	public AppBudget addBudget(String nom, double objectif, double utilise, String labelRecurrent,
			LocalDate dateRecurrent) throws ComptaException {

		AppBudget appB = null;

		try {
			// création du modèle
			Budget budget = new Budget();
			budget.setNom(nom);
			budget.setMontantUtilise(utilise);
			budget.setObjectif(objectif);
			budget.setIsActif(true);
			budget.setPriority(_budgetList.size());
			budget.setLabelRecurrent(labelRecurrent);
			budget.setDateRecurrent(dateRecurrent);

			// encapsulation applicative
			appB = new AppBudget(budget);
			String id = DBManager.getInstance().addBudget(nom, objectif, utilise, true, _budgetList.size(),
					labelRecurrent, dateRecurrent);
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
	 * @param appBudget      le budget
	 * @param nom            le nouveau nom
	 * @param objectif       le nouvel objectif
	 * @param utilise        le nouveau montant utilise
	 * @param isActif        la nouvelle valeur actif
	 * @param prio           la nouvelle priorité
	 * @param dateRecurrent
	 * @param labelRecurrent
	 * @return
	 * @throws ComptaException
	 */
	public AppBudget editBudget(AppBudget appBudget, String nom, double objectif, double utilise, boolean isActif,
			int prio, String labelRecurrent, LocalDate dateRecurrent) throws ComptaException {

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
				DBManager.getInstance().updateBudget(appBudget);

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
	 * @throws ComptaException Echec de la récupération
	 */
	public List<AppBudget> getAllBudgets() throws ComptaException {

		ArrayList<AppBudget> list = new ArrayList<>();

		// récupération en base des métadonnée des budget
		HashMap<String, Budget> fromPersistancy = DBManager.getInstance().getAllBudget();

		for (String id : fromPersistancy.keySet()) {

			// encapsulation applicative
			AppBudget appB = new AppBudget(fromPersistancy.get(id));
			appB.setAppID(id);

			// ajout dans la liste
			list.add(appB);
		}

		return list;
	}

	/**
	 * Ajoute une utilisation pour le budget
	 * 
	 * @param appB   le budget
	 * @param nom    le nom de l'utilisation
	 * @param montat le montant
	 * @param date   la date
	 * @throws ComptaException
	 */
	public void addUtilisationForBudget(AppBudget appB, String nom, double montat, LocalDate date)
			throws ComptaException {

		// enregistrement de l'utilisation
		String id = DBManager.getInstance().addUtilisationForBudget(appB.getAppId(), nom, montat, date);

		if (id != null && !id.trim().isEmpty()) {
			// modification du montant utilisé
			appB.setMontantUtilise(appB.getMontantUtilise() + montat);
			// sauvegarde du budget
			DBManager.getInstance().updateBudget(appB);
			// MaJ des avancements
			calculateData();
		}

	}

	/**
	 * Retourne les utilisations du budget
	 * 
	 * @param appId l'id du budget
	 * @return
	 * @throws ComptaException
	 */
	public List<AppUtilisation> getUtilisation(String appId) throws ComptaException {

		ArrayList<AppUtilisation> listeRes = new ArrayList<>();
		// récupération des champs en base
		try {

			HashMap<String, String[]> infos = DBManager.getInstance().getUtilisationInfos(appId);

			for (String id : infos.keySet()) {
				// pour l'utilisation
				String[] info = infos.get(id);

				// parsing de la date et création du modèle métier
				LocalDate date = LocalDate.parse(info[2], ApplicationFormatter.databaseDateFormat);
				Utilisation util = new Utilisation(Double.parseDouble(info[1]), info[0], date);
				// Création de l'utilisation applicative
				AppUtilisation appUtil = new AppUtilisation(util);
				appUtil.setAppID(id);
				// ajout au resultat
				listeRes.add(appUtil);

			}
		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer les utilisations", e);
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
	public void editUtilisation(AppUtilisation utilisation, String nom, double montant, LocalDate date)
			throws ComptaException {

		// affectation des nouvelles valeurs
		utilisation.setNom(nom);
		utilisation.setMontant(montant);
		utilisation.setDate(date);

		// MaJ en base
		DBManager.getInstance().upDateUtilisation(utilisation);

	}

	/**
	 * Supprime une utilisation
	 * 
	 * @param util
	 * @throws ComptaException
	 */
	public void removeUtilisation(AppUtilisation util) throws ComptaException {

		DBManager.getInstance().removeUtilisation(util);

	}

	/**
	 * Supprime un budget et ses utilisations
	 * 
	 * @param appB
	 * @throws ComptaException
	 */
	public void removeBudget(AppBudget appB) throws ComptaException {

		DBManager.getInstance().removeBudget(appB);

		// suppression de la liste ( le remove ne marche pas)
		Iterator<AppBudget> iter = _budgetList.iterator();
		boolean goOn = true;
		while (iter.hasNext() && goOn) {

			AppBudget bud = iter.next();
			if (bud.getAppId().equals(appB.getAppId())) {
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

		return DBManager.getInstance().getLabelRecurrentList();
	}

	/**
	 * Ajoute un label de budget récurrent dans l'application
	 * 
	 * @param labelRec
	 * @throws ComptaException
	 */
	public void addLabelRecurrent(String labelRec) throws ComptaException {
		DBManager.getInstance().addLabelRecurrent(labelRec);

	}

}
