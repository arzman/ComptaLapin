package org.arthur.compta.lapin.application.manager;

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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Gestionnaire des budgets
 *
 */
public class BudgetManager {

    /** Instance unique du singleton */
    private static BudgetManager _instance;
    /** La liste des budgets actifs */
    private final List<AppBudget> _budgetList;
    /** Listeners notifiés lors de tout changement */
    private final List<Runnable> _listeners;
    /** logger */
    private final Logger logger;

    /**
     * Constructeur par défaut
     */
    private BudgetManager() {

        logger = LogManager.getLogger(BudgetManager.class);
        _budgetList = new ArrayList<>();
        _listeners = new ArrayList<>();

        try {
            List<Budget> fromPersistancy = BudgetDataAccess.getInstance().getActiveBudget();
            for (Budget budget : fromPersistancy) {
                _budgetList.add(new AppBudget(budget));
            }
            calculateData();
        } catch (ComptaException e) {
            logger.fatal(e);
        }
    }

    /** Retourne l'instance unique du singleton */
    public static BudgetManager getInstance() {
        if (_instance == null) {
            _instance = new BudgetManager();
        }
        return _instance;
    }

    /** Enregistre un écouteur de changement */
    public void addChangeListener(Runnable l) {
        _listeners.add(l);
    }

    /** Notifie tous les écouteurs */
    private void fireChanged() {
        for (Runnable l : _listeners) {
            l.run();
        }
    }

    /** Retourne la liste des budgets */
    public List<AppBudget> getBudgetList() {
        return _budgetList;
    }

    /** Désactive le budget de l'application */
    public void desactivateBudget(AppBudget appB) throws ComptaException {
        _budgetList.remove(appB);
        editBudget(appB, appB.getNom(), appB.getObjectif(), appB.getMontantUtilise(), false, appB.getPriority(),
                appB.getLabelRecurrent(), appB.getDateRecurrent());
        fireChanged();
    }

    /**
     * Mets à jour les avancements et montants sur compte des budgets
     */
    public void calculateData() {

        double dispoCC = 0;
        double dispoCL = 0;

        List<AppBudget> tmp = new ArrayList<>(_budgetList);
        tmp.sort(Comparator.comparingInt(AppBudget::getPriority));

        for (final AppCompte compte : CompteManager.getInstance().getCompteList()) {
            if (compte.isBudget()) {
                if (compte.isLivret()) {
                    dispoCL += compte.getSoldePrev3();
                } else {
                    dispoCC += compte.getSoldePrev3();
                }
            }
        }

        for (final AppBudget budget : tmp) {
            if (budget.isActif()) {
                double cour = 0;
                double liv = 0;
                double inBudget = budget.getMontantUtilise();

                if ((dispoCL - (budget.getObjectif() - inBudget)) >= 0) {
                    dispoCL = dispoCL - (budget.getObjectif() - inBudget);
                    liv = budget.getObjectif() - inBudget;
                    inBudget = inBudget + liv;
                } else {
                    liv = dispoCL;
                    dispoCL = 0;
                    inBudget = inBudget + liv;

                    if ((dispoCC - (budget.getObjectif() - inBudget)) >= 0) {
                        dispoCC = dispoCC - (budget.getObjectif() - inBudget);
                        cour = budget.getObjectif() - inBudget;
                        inBudget = inBudget + cour;
                    } else {
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

        fireChanged();
    }

    /**
     * Ajoute un budget dans l'application
     */
    public AppBudget addBudget(String nom, double objectif, double utilise, String labelRecurrent,
            LocalDate dateRecurrent) throws ComptaException {
        AppBudget appB = new AppBudget(BudgetDataAccess.getInstance().addBudget(nom, objectif, utilise, true,
                _budgetList.size(), labelRecurrent, dateRecurrent));
        _budgetList.add(appB);
        calculateData();
        return appB;
    }

    /**
     * Modifie le budget
     */
    public AppBudget editBudget(AppBudget appBudget, String nom, double objectif, double utilise, boolean isActif,
            int prio, String labelRecurrent, LocalDate dateRecurrent) throws ComptaException {
        if (appBudget != null) {
            try {
                appBudget.setNom(nom);
                appBudget.setObjectif(objectif);
                appBudget.setMontantUtilise(utilise);
                appBudget.setIsActif(isActif);
                appBudget.setPriority(prio);
                appBudget.setLabelerecurrent(labelRecurrent);
                appBudget.setDateReccurent(dateRecurrent);
                calculateData();
                BudgetDataAccess.getInstance().updateBudget(appBudget.getDBObject());
            } catch (Exception e) {
                throw new ComptaException("Impossible de mettre à jour le budget", e);
            }
        }
        return appBudget;
    }

    /**
     * Retourne tous les budgets
     */
    public List<AppBudget> getAllBudgets() throws ComptaException {
        List<AppBudget> list = new ArrayList<>();
        List<Budget> fromPersistancy = BudgetDataAccess.getInstance().getAllBudget();
        for (Budget bud : fromPersistancy) {
            list.add(new AppBudget(bud));
        }
        return list;
    }

    /**
     * Ajoute une utilisation pour le budget
     */
    public void addUtilisationForBudget(AppBudget appB, String nom, double montat, LocalDate date)
            throws ComptaException {
        UtilisationDataAccess.getInstance().addUtilisationForBudget(appB.getAppId(), nom, montat, date);
        appB.setMontantUtilise(appB.getMontantUtilise() + montat);
        BudgetDataAccess.getInstance().updateBudget(appB.getDBObject());
        calculateData();
    }

    /**
     * Retourne les utilisations du budget
     */
    public List<AppUtilisation> getUtilisation(int appId) throws ComptaException {
        List<AppUtilisation> listeRes = new ArrayList<>();
        List<Utilisation> fromPersist = UtilisationDataAccess.getInstance().getUtilisationInfos(appId);
        for (Utilisation util : fromPersist) {
            listeRes.add(new AppUtilisation(util));
        }
        return listeRes;
    }

    /**
     * Edite une utilisation
     */
    public void editUtilisation(AppUtilisation utilisation, String nom, double montant, LocalDate date)
            throws ComptaException {
        utilisation.setNom(nom);
        utilisation.setMontant(montant);
        utilisation.setDate(date);
        UtilisationDataAccess.getInstance().upDateUtilisation(utilisation);
    }

    /**
     * Supprime une utilisation
     */
    public void removeUtilisation(AppUtilisation util) throws ComptaException {
        UtilisationDataAccess.getInstance().removeUtilisation(util.getAppId());
    }

    /**
     * Supprime un budget et ses utilisations
     */
    public void removeBudget(AppBudget appB) throws ComptaException {
        BudgetDataAccess.getInstance().removeBudget(appB);
        Iterator<AppBudget> iter = _budgetList.iterator();
        while (iter.hasNext()) {
            AppBudget bud = iter.next();
            if (bud.getAppId() == appB.getAppId()) {
                iter.remove();
                break;
            }
        }
        fireChanged();
    }

    /**
     * Retourne la liste des budgets récurrents
     */
    public List<String> getLabelRecurrentList() throws ComptaException {
        return BudgetDataAccess.getInstance().getLabelRecurrentList();
    }

    /**
     * Ajoute un label de budget récurrent dans l'application
     */
    public void addLabelRecurrent(String labelRec) throws ComptaException {
        BudgetDataAccess.getInstance().addLabelRecurrent(labelRec);
    }

    /**
     * Mets à jour la liste des budgets actifs (l'ordre surtout)
     */
    public void updateBudgets(List<AppBudget> activesBudgets) throws ComptaException {
        List<Budget> tmp = new ArrayList<>();
        for (AppBudget appB : activesBudgets) {
            tmp.add(appB.getDBObject());
        }
        BudgetDataAccess.getInstance().updateBudgets(tmp);
        fireChanged();
    }

}
