package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.model.ExerciceMensuel;
import org.arthur.compta.lapin.model.operation.Operation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppExerciceMensuel extends AppObject<ExerciceMensuel> {

    /** La liste des dépenses */
    private final List<AppOperation> _appDepenseList;
    /** La liste des ressources */
    private final List<AppOperation> _appRessourceList;
    /** La liste des transferts */
    private final List<AppTransfert> _appTransfertList;
    /** date de début */
    private final LocalDate _dateDebut;
    /** date de fin */
    private final LocalDate _dateFin;
    /** Resultat previsionnel */
    private final double _resPrev;

    public AppExerciceMensuel(ExerciceMensuel exerciceMensuel) throws ComptaException {

        setAppID(exerciceMensuel.getId());
        _dateDebut = exerciceMensuel.getDateDebut();
        _dateFin = exerciceMensuel.getDateFin();
        _resPrev = exerciceMensuel.getResultatPrev();

        _appDepenseList = new ArrayList<>();
        _appRessourceList = new ArrayList<>();
        _appTransfertList = new ArrayList<>();

        List<Operation> opList = TrimestreManager.getInstance().getOperationForEM(exerciceMensuel.getId());
        for (Operation op : opList) {
            switch (op.getType()) {
                case DEPENSE :
                    _appDepenseList.add(new AppOperation(op));
                    break;
                case RESSOURCE :
                    _appRessourceList.add(new AppOperation(op));
                    break;
                case TRANSFERT :
                    _appTransfertList.add(new AppTransfert(op));
                    break;
            }
        }
    }

    /** Retourne la date de début de l'exercice */
    public LocalDate getDateDebut() {
        return _dateDebut;
    }

    public double getResultat() {
        double sum = 0;
        for (AppOperation res : _appRessourceList) {
            sum = sum + res.getMontant();
        }
        for (AppOperation dep : _appDepenseList) {
            sum = sum - dep.getMontant();
        }
        return sum;
    }

    private void addDepense(AppOperation appDep) {
        _appDepenseList.add(appDep);
    }

    /** Retourne la liste des dépenses */
    public List<AppOperation> getDepenses() {
        return _appDepenseList;
    }

    private void addRessource(AppOperation appRes) {
        _appRessourceList.add(appRes);
    }

    private void addTransfert(AppTransfert apptr) {
        _appTransfertList.add(apptr);
    }

    /** Retourne la liste des ressources */
    public List<AppOperation> getRessources() {
        return _appRessourceList;
    }

    /** Retourne la liste des transferts */
    public List<AppTransfert> getTransferts() {
        return _appTransfertList;
    }

    /** Retourne le résultat prévisionnel */
    public double getResultatPrev() {
        return _resPrev;
    }

    public void addOperation(AppOperation apptr) {
        switch (apptr.getType()) {
            case DEPENSE :
                addDepense(apptr);
                break;
            case RESSOURCE :
                addRessource(apptr);
                break;
            case TRANSFERT :
                addTransfert((AppTransfert) apptr);
                break;
        }
    }

    @Override
    public ExerciceMensuel getDBObject() {
        return new ExerciceMensuel(getAppId(), getDateDebut(), getDateDebut(), getResultatPrev());
    }

    public LocalDate getDateFin() {
        return _dateFin;
    }

}
