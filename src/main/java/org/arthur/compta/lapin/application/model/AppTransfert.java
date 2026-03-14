package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.model.operation.Operation;

/**
 * Encapsulation applicative d'une opération de transfert
 *
 */
public class AppTransfert extends AppOperation {

    /** Le compte cible */
    private AppCompte _compteCible;

    /**
     * Constructeur
     * 
     * @param transfert
     *            le transfert à encapsuler
     */
    public AppTransfert(Operation transfert) {
        super(transfert);
        _compteCible = CompteManager.getInstance().getAppCompteFromId(transfert.getCibleCompteId());
    }

    /** Retourne le compte cible du transfert */
    public AppCompte getCompteCible() {
        return _compteCible;
    }

    /** Positionne le compte cible */
    public void setCompteCible(AppCompte compte) {
        _compteCible = compte;
    }

    @Override
    public Operation getDBObject() {
        return new Operation(getAppId(), getType(), getCompteSource().getAppId(), getLibelle(), getMontant(), getEtat(),
                getCompteCible().getAppId());
    }

}
