package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.model.Compte;

/**
 * Compte applicatif encapsule un Compte. Cet objet est disponible pour la
 * couche de présentation et la persistance
 *
 */
public class AppCompte extends AppObject<Compte> {

    /** Le nom du compte */
    private String _nom;
    /** Le solde du compte */
    private double _solde;
    /** Indique si le compte est un livret */
    private boolean _isLivret;
    /** Indique si le compte est concerné par les budgets */
    private boolean _isBudget;
    /** Le solde prévisionnel fin du 1er mois */
    private double _soldePrev1;
    /** Le solde prévisionnel fin du 2eme mois */
    private double _soldePrev2;
    /** Le solde prévisionnel fin du 3eme mois */
    private double _soldePrev3;

    /**
     * Constructeur
     * 
     * @param compte
     *            le compte métier relié
     */
    public AppCompte(Compte compte) {

        setAppID(compte.getId());

        _nom = compte.getNom();
        _solde = compte.getSolde();
        _isLivret = compte.isLivret();
        _isBudget = compte.isBudgetAllowed();
        _soldePrev1 = 0;
        _soldePrev2 = 0;
        _soldePrev3 = 0;
    }

    /** Retourne le nom du compte */
    public String getNom() {
        return _nom;
    }

    /** Positionne le nom du compte */
    public void setNom(String nom) {
        _nom = nom;
    }

    /** Retourne le solde du compte */
    public double getSolde() {
        return _solde;
    }

    /** Positionne le solde du compte */
    public void setSolde(double solde) {
        _solde = solde;
    }

    /** Retourne vrai si le compte est livret */
    public boolean isLivret() {
        return _isLivret;
    }

    /** Retourne vrai si le compte est concerné par les budgets */
    public boolean isBudget() {
        return _isBudget;
    }

    /** Positionne le flag isLivret */
    public void setIsLivret(boolean isLivret) {
        _isLivret = isLivret;
    }

    /** Positionne le flag isBudget */
    public void setIsBudget(boolean isBudget) {
        _isBudget = isBudget;
    }

    /** Retourne le solde prévisionnel fin du 1er mois */
    public double getSoldePrev1() {
        return _soldePrev1;
    }

    /** Positionne le solde prévisionnel fin du 1er mois */
    public void setSoldePrev1(double v) {
        _soldePrev1 = v;
    }

    /** Retourne le solde prévisionnel fin du 2eme mois */
    public double getSoldePrev2() {
        return _soldePrev2;
    }

    /** Positionne le solde prévisionnel fin du 2eme mois */
    public void setSoldePrev2(double v) {
        _soldePrev2 = v;
    }

    /** Retourne le solde prévisionnel fin du 3eme mois */
    public double getSoldePrev3() {
        return _soldePrev3;
    }

    /** Positionne le solde prévisionnel fin du 3eme mois */
    public void setSoldePrev3(double v) {
        _soldePrev3 = v;
    }

    @Override
    public String toString() {
        return _nom;
    }

    @Override
    public Compte getDBObject() {
        return new Compte(getAppId(), getSolde(), getNom(), isLivret(), isBudget());
    }

}
