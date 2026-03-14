package org.arthur.compta.lapin.application.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.dataaccess.db.CompteDataAcces;
import org.arthur.compta.lapin.model.Compte;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.OperationType;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton de gestion des AppCompte
 *
 */
public class CompteManager {

/** Instance unique du singleton */
private static CompteManager _instance;

/** La liste des comptes gérés par l'application */
private final List<AppCompte> _compteList;

/** Listeners notifiés lors de tout changement */
private final List<Runnable> _listeners;

/** logger */
private final Logger _logger;

/**
 * Constructeur
 */
private CompteManager() {

_compteList = new ArrayList<>();
_listeners = new ArrayList<>();
_logger = LogManager.getLogger(CompteDataAcces.class);

try {
List<Compte> fromPersistancy = CompteDataAcces.getInstance().getAllCompte();
for (Compte cpt : fromPersistancy) {
_compteList.add(new AppCompte(cpt));
}
} catch (ComptaException e) {
_logger.fatal(e);
}
}

/** Retourne l'instance unique du singleton */
public static CompteManager getInstance() {
if (_instance == null) {
_instance = new CompteManager();
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

/**
 * Retourne la liste des comptes applicatifs
 */
public List<AppCompte> getCompteList() {
return _compteList;
}

/**
 * Création d'un nouveau compte
 */
public AppCompte addCompte(String nom, double solde, boolean livret, boolean budgetAllowed) throws ComptaException {
AppCompte appCpt = new AppCompte(CompteDataAcces.getInstance().addCompte(nom, solde, livret, budgetAllowed));
_compteList.add(appCpt);
fireChanged();
return appCpt;
}

/**
 * Suppression d'un compte
 */
public void removeCompte(AppCompte appCompte) throws ComptaException {
if (appCompte != null) {
CompteDataAcces.getInstance().removeCompte(appCompte.getAppId());
_compteList.remove(appCompte);
fireChanged();
} else {
_logger.warn("Tentativement de suppression d'un compte null");
}
}

/**
 * Met à jour le compte
 */
public void editCompte(AppCompte appCompte, String nom, double solde, boolean isLivret, boolean isBudget) throws ComptaException {
if (appCompte != null) {
appCompte.setNom(nom);
appCompte.setSolde(solde);
appCompte.setIsLivret(isLivret);
appCompte.setIsBudget(isBudget);
calculateSoldePrev(appCompte);
CompteDataAcces.getInstance().updateCompte(appCompte.getDBObject());
fireChanged();
} else {
_logger.warn("Tentative d'édition d'un compte null");
}
}

public AppCompte getAppCompteFromId(int appId) {
for (AppCompte cpt : _compteList) {
if (cpt.getAppId() == appId) {
return cpt;
}
}
return null;
}

/**
 * Met à jour les prévisions de tous les comptes
 */
public void refreshAllPrev() {
for (AppCompte compte : _compteList) {
calculateSoldePrev(compte);
}
fireChanged();
}

/**
 * Calcule les différents soldes prévisionnels pour le compte donné
 */
public void calculateSoldePrev(AppCompte compte) {
if (compte != null) {
double delta1 = TrimestreManager.getInstance().getDeltaForCompte(compte, 0);
compte.setSoldePrev1(compte.getSolde() + delta1);
double delta2 = TrimestreManager.getInstance().getDeltaForCompte(compte, 1);
compte.setSoldePrev2(compte.getSolde() + delta1 + delta2);
double delta3 = TrimestreManager.getInstance().getDeltaForCompte(compte, 2);
compte.setSoldePrev3(compte.getSolde() + delta1 + delta2 + delta3);
BudgetManager.getInstance().calculateData();
}
}

/**
 * Prise en compte du changement d'état d'une opération
 */
public void operationSwitched(AppOperation appOp) throws ComptaException {

double etatMod;
if (appOp.getEtat().equals(EtatOperation.PRISE_EN_COMPTE)) {
etatMod = -1.0;
} else {
etatMod = 1.0;
}

double typeMode;
if (appOp.getType().equals(OperationType.RESSOURCE)) {
typeMode = -1.0;
} else {
typeMode = 1.0;
}

double soldeSrcInit = appOp.getCompteSource().getSolde();
double delta = etatMod * typeMode * appOp.getMontant();
appOp.getCompteSource().setSolde(soldeSrcInit + delta);

CompteDataAcces.getInstance().updateCompte(appOp.getCompteSource().getDBObject());

if (appOp instanceof AppTransfert) {
double soldeCibleInit = ((AppTransfert) appOp).getCompteCible().getSolde();
((AppTransfert) appOp).getCompteCible().setSolde(soldeCibleInit - delta);
CompteDataAcces.getInstance().updateCompte(((AppTransfert) appOp).getCompteCible().getDBObject());
}

fireChanged();
}

/**
 * Retourne la liste des noms de compte déjà existants
 */
public List<String> getCompteNameList() {
List<String> res = new ArrayList<>();
for (AppCompte cpt : _compteList) {
res.add(cpt.getNom());
}
return res;
}

}
