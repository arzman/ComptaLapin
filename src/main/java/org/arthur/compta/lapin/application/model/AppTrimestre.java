package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.model.Trimestre;

import java.time.LocalDate;

/**
 * Encapsulation applicative d'un trimestre
 *
 */
public class AppTrimestre extends AppObject<Trimestre> {

    /** Premier mois du trimestre */
    private AppExerciceMensuel _premierMois;
    /** Deuxieme mois du trimestre */
    private AppExerciceMensuel _deuxiemeMois;
    /** Troisieme mois du trimestre */
    private AppExerciceMensuel _troisiemeMois;

    /**
     * Constructeur
     * 
     * @param trimestre
     *            le trimestre a encapsuler
     * @throws ComptaException
     */
    public AppTrimestre(Trimestre trimestre) throws ComptaException {
        setAppID(trimestre.getId());
        _premierMois = TrimestreManager.getInstance().loadExerciceMensuel(trimestre.getExerciceMensuelIds()[0]);
        _deuxiemeMois = TrimestreManager.getInstance().loadExerciceMensuel(trimestre.getExerciceMensuelIds()[1]);
        _troisiemeMois = TrimestreManager.getInstance().loadExerciceMensuel(trimestre.getExerciceMensuelIds()[2]);
    }

    /** Retourne la date de début */
    public LocalDate getDateDebut() {
        return _premierMois.getDateDebut();
    }

    /**
     * Positionne un exercice mensuel applicatif par sa position. 0 = premier mois,
     * 1 = deuxieme mois, 2 = troisieme mois
     */
    public void setAppExerciceMensuel(int i, AppExerciceMensuel appEm) {
        switch (i) {
            case 0 :
                _premierMois = appEm;
                break;
            case 1 :
                _deuxiemeMois = appEm;
                break;
            case 2 :
                _troisiemeMois = appEm;
                break;
            default :
                break;
        }
    }

    /**
     * Retourne l'exercice mensuel correspondant au numéro fourni
     */
    public AppExerciceMensuel getAppExerciceMensuel(int numMois) {
        switch (numMois) {
            case 0 :
                return _premierMois;
            case 1 :
                return _deuxiemeMois;
            case 2 :
                return _troisiemeMois;
            default :
                return null;
        }
    }

    /** Retourne la date de fin du trimestre */
    public LocalDate getDateFin() {
        return _troisiemeMois.getDateFin();
    }

    @Override
    public Trimestre getDBObject() {
        return new Trimestre(getAppId(), _premierMois.getAppId(), _deuxiemeMois.getAppId(), _troisiemeMois.getAppId());
    }

}
