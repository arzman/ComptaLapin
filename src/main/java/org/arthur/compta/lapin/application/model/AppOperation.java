package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.Operation;
import org.arthur.compta.lapin.model.operation.OperationType;

/**
 * Encapsulation applicative d'une opération
 *
 */
public class AppOperation extends AppObject<Operation> {

/** Le libellé de l'opération */
protected String _libelle;
/** Le montant de l'opération */
protected double _montant;
/** Le compte source */
protected AppCompte _compteSource;
/** Etat de l'opération */
protected EtatOperation _etat;
/** le Type */
protected OperationType _type;

/**
 * Constructeur
 */
public AppOperation(Operation operation) {

setAppID(operation.getId());
_libelle = operation.getNom();
_montant = operation.getMontant();
_etat = operation.getEtat();
_compteSource = CompteManager.getInstance().getAppCompteFromId(operation.getCompteId());
_type = operation.getType();
}

/** Retourne le montant de l'opération */
public double getMontant() {
return _montant;
}

/** Retourne le compte source de l'opération */
public AppCompte getCompteSource() {
return _compteSource;
}

/** Positionne le compte source */
public void setCompteSrc(AppCompte compte) {
_compteSource = compte;
}

/** Retourne l'état de l'opération */
public EtatOperation getEtat() {
return _etat;
}

public OperationType getType() {
return _type;
}

/** Permutte l'état de l'opération */
public void switchEtat() {
switch (getEtat()) {
case PRISE_EN_COMPTE:
setEtat(EtatOperation.PREVISION);
break;
case PREVISION:
setEtat(EtatOperation.PRISE_EN_COMPTE);
break;
}
}

public void setEtat(EtatOperation etatF) {
_etat = etatF;
}

/** Retourne le libellé de l'opération */
public String getLibelle() {
return _libelle;
}

public void setLibelle(String newLib) {
_libelle = newLib;
}

public void setMontant(double newMontant) {
_montant = newMontant;
}

@Override
public Operation getDBObject() {
return new Operation(getAppId(), getType(), getCompteSource().getAppId(), getLibelle(), getMontant(), getEtat(), -1);
}

}
