package org.arthur.compta.lapin.application.manager;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.*;
import org.arthur.compta.lapin.application.service.OperationService;
import org.arthur.compta.lapin.application.service.TemplateService;
import org.arthur.compta.lapin.dataaccess.db.AppliDataAccess;
import org.arthur.compta.lapin.dataaccess.db.ExerciceMensuelDataAccess;
import org.arthur.compta.lapin.dataaccess.db.OperationDataAccess;
import org.arthur.compta.lapin.dataaccess.db.TrimestreDataAccess;
import org.arthur.compta.lapin.model.Trimestre;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.Operation;
import org.arthur.compta.lapin.model.operation.OperationType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * Gestionnaire des trimestres.
 *
 */
public class TrimestreManager {

    /** L'unique instance du singleton */
    private static TrimestreManager _instance;
    /** le trimestre en cours de traitement */
    private AppTrimestre _trimestreCourant;
    /** Ecouteurs sur le changement du trimestre courant */
    private final List<Consumer<AppTrimestre>> _trimestreListeners;

    /**
     * Le constructeur
     */
    private TrimestreManager() {
        _trimestreListeners = new ArrayList<>();
    }

    /** Retourne l'instance du singleton */
    public static TrimestreManager getInstance() {
        if (_instance == null) {
            _instance = new TrimestreManager();
        }
        return _instance;
    }

    /** Enregistre un écouteur sur le changement du trimestre courant */
    public void addTrimestreChangeListener(Consumer<AppTrimestre> l) {
        _trimestreListeners.add(l);
    }

    /** Notifie les écouteurs du changement de trimestre courant */
    private void fireTrimestreChanged(AppTrimestre t) {
        for (Consumer<AppTrimestre> l : _trimestreListeners) {
            l.accept(t);
        }
    }

    /** Retourne le trimestre courant */
    public AppTrimestre getTrimestreCourant() {
        return _trimestreCourant;
    }

    /**
     * Charge un trimestre courant
     */
    public void loadTrimestreCourant(int appId) throws ComptaException {
        AppTrimestre appTrimestre = loadTrimestre(appId);
        _trimestreCourant = appTrimestre;
        AppliDataAccess.getInstance().setTrimestreCourant(appId);
        CompteManager.getInstance().refreshAllPrev();
        fireTrimestreChanged(appTrimestre);
    }

    /**
     * Extrait de la base un trimestre
     */
    public AppTrimestre loadTrimestre(int appId) throws ComptaException {
        return new AppTrimestre(TrimestreDataAccess.getInstance().getTrimestreInfo(appId));
    }

    /**
     * Crée un exercice mensuel applicatif depuis la base de données
     */
    public AppExerciceMensuel loadExerciceMensuel(int id) throws ComptaException {
        return new AppExerciceMensuel(ExerciceMensuelDataAccess.getInstance().getExMensuel(id));
    }

    /**
     * Crée un trimestre applicatif
     */
    public AppTrimestre createTrimestre(LocalDate dateDeb) throws ComptaException {
        int[] createdId = new int[3];

        for (int i = 0; i < 3; i++) {
            LocalDate debut = dateDeb.plusMonths(i).withDayOfMonth(1);
            LocalDate fin = debut.withDayOfMonth(debut.lengthOfMonth());

            AppExerciceMensuel appEm = new AppExerciceMensuel(ExerciceMensuelDataAccess.getInstance()
                    .addExerciceMensuel(debut, fin, TemplateService.getPrevFromtemplate()));
            createdId[i] = appEm.getAppId();
            TemplateService.applyTtemplate(appEm, i);
        }

        return new AppTrimestre(
                TrimestreDataAccess.getInstance().addTrimestre(createdId[0], createdId[1], createdId[2]));
    }

    /**
     * Retourne une Map contenant un peu d'informations sur les trimestres en base
     */
    public HashMap<String, LocalDate> getAllTrimestreShortList() throws ComptaException {
        HashMap<String, LocalDate> res = new HashMap<>();
        ArrayList<String> ids = TrimestreDataAccess.getInstance().getAllTrimestreId();
        for (String id : ids) {
            res.put(id, TrimestreDataAccess.getInstance().getDateDebutFromTrimestre(id));
        }
        return res;
    }

    /**
     * Supprime un trimestre
     */
    public void removeTrimestre(int idTrimestre) throws ComptaException {
        if (_trimestreCourant == null || (_trimestreCourant != null && idTrimestre != _trimestreCourant.getAppId())) {
            Trimestre trimToDel = TrimestreDataAccess.getInstance().getTrimestreInfo(idTrimestre);
            ExerciceMensuelDataAccess.getInstance().removeExcerciceMensuel(trimToDel.getExerciceMensuelIds()[0]);
            ExerciceMensuelDataAccess.getInstance().removeExcerciceMensuel(trimToDel.getExerciceMensuelIds()[1]);
            ExerciceMensuelDataAccess.getInstance().removeExcerciceMensuel(trimToDel.getExerciceMensuelIds()[2]);
            TrimestreDataAccess.getInstance().removeTrimestre(idTrimestre);
        }
    }

    /** Indique si la chaîne de caractère correspond au type Transfert */
    public boolean isTransfertType(String type) {
        return OperationType.TRANSFERT.toString().equals(type);
    }

    /** Retourne la somme à ajouter au compte pour la fin de l'exercice mensuel */
    public double getDeltaForCompte(AppCompte compte, int numMois) {
        double delta = 0;

        if (_trimestreCourant != null) {
            for (AppOperation dep : _trimestreCourant.getAppExerciceMensuel(numMois).getDepenses()) {
                if (dep.getEtat().equals(EtatOperation.PREVISION) && dep.getCompteSource().equals(compte)) {
                    delta = delta - dep.getMontant();
                }
            }
            for (AppOperation res : _trimestreCourant.getAppExerciceMensuel(numMois).getRessources()) {
                if (res.getEtat().equals(EtatOperation.PREVISION) && res.getCompteSource().equals(compte)) {
                    delta = delta + res.getMontant();
                }
            }
            for (AppTransfert trans : _trimestreCourant.getAppExerciceMensuel(numMois).getTransferts()) {
                if (trans.getEtat().equals(EtatOperation.PREVISION) && trans.getCompteSource().equals(compte)) {
                    delta = delta - trans.getMontant();
                }
                if (trans.getEtat().equals(EtatOperation.PREVISION) && trans.getCompteCible().equals(compte)) {
                    delta = delta + trans.getMontant();
                }
            }
        }

        return delta;
    }

    /**
     * Charge le trimestre courant précédemment enregistré en base.
     */
    public void recoverTrimestre() throws ComptaException {
        int id = AppliDataAccess.getInstance().getTrimestreCourantId();
        if (id != -1) {
            TrimestreManager.getInstance().loadTrimestreCourant(id);
        }
    }

    /**
     * Supprime une opération du mois du trimestre courant
     */
    public void removeOperation(AppOperation appOp, Integer numMois) throws ComptaException {
        if (appOp.getEtat().equals(EtatOperation.PRISE_EN_COMPTE)) {
            OperationService.switchEtatOperation(appOp);
        }

        AppExerciceMensuel appEm = _trimestreCourant.getAppExerciceMensuel(numMois);
        if (appOp.getType().equals(OperationType.DEPENSE)) {
            appEm.getDepenses().remove(appOp);
        } else if (appOp.getType().equals(OperationType.RESSOURCE)) {
            appEm.getRessources().remove(appOp);
        } else if (appOp.getType().equals(OperationType.TRANSFERT)) {
            appEm.getTransferts().remove(appOp);
        }

        CompteManager.getInstance().calculateSoldePrev(appOp.getCompteSource());
        if (appOp instanceof AppTransfert) {
            CompteManager.getInstance().calculateSoldePrev(((AppTransfert) appOp).getCompteCible());
        }

        OperationDataAccess.getInstance().removeOperation(appOp.getAppId());
    }

    /**
     * Crée une nouvelle opération dans le mois du trimestre courant
     */
    public AppOperation addOperation(String libelle, double montant, String type, AppCompte compteSrc,
            AppCompte compteCible, int numMois) throws ComptaException {

        AppOperation appop = null;
        AppExerciceMensuel appEm = _trimestreCourant.getAppExerciceMensuel(numMois);

        if (type.equals(OperationType.DEPENSE.toString())) {
            appop = new AppOperation(OperationDataAccess.getInstance().addOperation(libelle, montant,
                    OperationType.DEPENSE, EtatOperation.PREVISION, compteSrc.getAppId(), -1, appEm.getAppId()));
        } else if (type.equals(OperationType.RESSOURCE.toString())) {
            appop = new AppOperation(OperationDataAccess.getInstance().addOperation(libelle, montant,
                    OperationType.RESSOURCE, EtatOperation.PREVISION, compteSrc.getAppId(), -1, appEm.getAppId()));
        } else if (type.equals(OperationType.TRANSFERT.toString())) {
            appop = new AppTransfert(
                    OperationDataAccess.getInstance().addOperation(libelle, montant, OperationType.TRANSFERT,
                            EtatOperation.PREVISION, compteSrc.getAppId(), compteCible.getAppId(), appEm.getAppId()));
        }

        appEm.addOperation(appop);

        CompteManager.getInstance().calculateSoldePrev(compteSrc);
        CompteManager.getInstance().calculateSoldePrev(compteCible);

        return appop;
    }

    /** Retourne la date de début de l'exercice mensuel */
    public LocalDate getDateDebut(int numMois) {
        if (_trimestreCourant != null) {
            return _trimestreCourant.getAppExerciceMensuel(numMois).getDateDebut();
        }
        return null;
    }

    /** Retourne le résultat de l'exercice mensuel */
    public double getResultat(int numMois) {
        if (_trimestreCourant != null) {
            return _trimestreCourant.getAppExerciceMensuel(numMois).getResultat();
        }
        return 0;
    }

    /** Retourne les dépenses de l'exercice mensuel */
    public List<AppOperation> getDepenses(Integer numMois) {
        if (_trimestreCourant != null) {
            return _trimestreCourant.getAppExerciceMensuel(numMois).getDepenses();
        }
        return new ArrayList<>();
    }

    /** Retourne les opérations */
    public List<Operation> getOperationForEM(int id) throws ComptaException {
        return OperationDataAccess.getInstance().getOperationInfo(id);
    }

    /** Retourne les ressources de l'exercice mensuel */
    public List<AppOperation> getRessources(Integer numMois) {
        if (_trimestreCourant != null) {
            return _trimestreCourant.getAppExerciceMensuel(numMois).getRessources();
        }
        return new ArrayList<>();
    }

    /** Retourne les transferts de l'exercice mensuel */
    public List<AppTransfert> getTransfert(Integer numMois) {
        if (_trimestreCourant != null) {
            return _trimestreCourant.getAppExerciceMensuel(numMois).getTransferts();
        }
        return new ArrayList<>();
    }

    /** Retourne le résultat prévisionnel à la création de l'exercice */
    public double getResultatPrev(int numMois) {
        if (_trimestreCourant != null) {
            return _trimestreCourant.getAppExerciceMensuel(numMois).getResultatPrev();
        }
        return 0;
    }

    /** Retourne l'identifiant applicatif d'un exercice mensuel */
    public int getExerciceMensuelId(int idTrimestre, int numMois) throws ComptaException {
        return ExerciceMensuelDataAccess.getInstance().getExerciceMensuelId(idTrimestre, numMois);
    }

    /**
     * Déplace une opération du trimestre courant
     */
    public void moveOperationFromTrimCourant(AppOperation appOp, int numMoisFrom, AppExerciceMensuelLightId appLI)
            throws ComptaException {

        AppExerciceMensuel appEm = _trimestreCourant.getAppExerciceMensuel(numMoisFrom);

        if (appOp.getType().equals(OperationType.DEPENSE)) {
            appEm.getDepenses().remove(appOp);
        } else if (appOp.getType().equals(OperationType.RESSOURCE)) {
            appEm.getRessources().remove(appOp);
        } else if (appOp.getType().equals(OperationType.TRANSFERT)) {
            appEm.getTransferts().remove(appOp);
        }

        if (appLI.getTrimestreId() == _trimestreCourant.getAppId()) {
            AppExerciceMensuel appEmTo = _trimestreCourant.getAppExerciceMensuel(appLI.getNumMois());
            if (appOp.getType().equals(OperationType.DEPENSE)) {
                appEmTo.getDepenses().add(appOp);
            } else if (appOp.getType().equals(OperationType.RESSOURCE)) {
                appEmTo.getRessources().add(appOp);
            } else if (appOp.getType().equals(OperationType.TRANSFERT)) {
                appEmTo.getTransferts().add((AppTransfert) appOp);
            }
        }

        CompteManager.getInstance().calculateSoldePrev(appOp.getCompteSource());
        if (appOp instanceof AppTransfert) {
            CompteManager.getInstance().calculateSoldePrev(((AppTransfert) appOp).getCompteCible());
        }

        OperationDataAccess.getInstance().moveOperation(appOp.getAppId(), appLI.getExerciceMensuelId());
    }

}
