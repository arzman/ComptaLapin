package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.model.Budget;

import java.time.LocalDate;

public class AppBudget extends AppObject<Budget> {

    /** Le nom */
    private String _nom;
    /** L'objectif */
    private double _objectif;
    /** L'avancement */
    private double _avancement;
    /** Le montant sur compte courant */
    private double _montantCourant;
    /** Le montant sur compte livret */
    private double _montantLivret;
    /** est actif ou pas */
    private boolean _isActif;
    /** Le montant utilise */
    private double _montantUtilise;
    /** La priorité 0 = priorité la plus haute */
    private int _priority;
    /** Libellé du budget récurrent (peut être vide) */
    private String _labelRecurrent;
    /** Date du budget récurrent (peut être nulle) */
    private LocalDate _dateRecurrent;

    /**
     * Constructeur par défaut
     */
    public AppBudget(Budget budget) {

        setAppID(budget.getId());

        _nom = budget.getNom();
        _objectif = budget.getObjectif();
        _montantUtilise = budget.getMontantUtilise();
        _isActif = budget.isActif();
        _priority = budget.getPriority();
        _labelRecurrent = budget.getLabelRecurrent();
        _dateRecurrent = budget.getDateRecurrent();
        _avancement = 0;
        _montantCourant = 0;
        _montantLivret = 0;
    }

    /** Retourne le nom */
    public String getNom() {
        return _nom;
    }

    /** Positionne le nom */
    public void setNom(String nom) {
        _nom = nom;
    }

    /** Retourne l'objectif du budget */
    public double getObjectif() {
        return _objectif;
    }

    /** Positionne l'objectif du budget */
    public void setObjectif(double objectif) {
        _objectif = objectif;
    }

    /** Retourne l'avancement du budget en % */
    public double getAvancement() {
        return _avancement;
    }

    /** Positionne l'avancement */
    public void setAvancement(double av) {
        _avancement = av;
    }

    /** Retourne le montant sur compte courant */
    public double getMontantCourant() {
        return _montantCourant;
    }

    /** Positionne le montant sur solde courant */
    public void setMontantCourant(double cour) {
        _montantCourant = cour;
    }

    /** Retourne le montant sur compte livret */
    public double getMontantLivret() {
        return _montantLivret;
    }

    /** Positionne le montant sur compte livret */
    public void setMontantLivret(double liv) {
        _montantLivret = liv;
    }

    /** Retourne le montant utilisé du budget */
    public double getMontantUtilise() {
        return _montantUtilise;
    }

    /** Positionne le montant utilisé */
    public void setMontantUtilise(double utilise) {
        _montantUtilise = utilise;
    }

    /** Retourne si le budget est terminé */
    public boolean isTermine() {
        return _montantUtilise == _objectif;
    }

    /** Retourne true si le budget est actif */
    public boolean isActif() {
        return _isActif;
    }

    /** Positionne l'état d'activation du budget */
    public void setIsActif(boolean isActif) {
        _isActif = isActif;
    }

    /** Retourne la priorité */
    public int getPriority() {
        return _priority;
    }

    /** Positionne la priorité */
    public void setPriority(int prio) {
        _priority = prio;
    }

    /** Retourne vrai si le budget est récurrent */
    public boolean isRecurrent() {
        return _labelRecurrent != null && !_labelRecurrent.isEmpty();
    }

    /** Retourne le libellé récurrent */
    public String getLabelRecurrent() {
        return _labelRecurrent != null ? _labelRecurrent : "";
    }

    /** Retourne la date récurrente */
    public LocalDate getDateRecurrent() {
        return _dateRecurrent;
    }

    public void setLabelerecurrent(String labelRecurrent) {
        _labelRecurrent = labelRecurrent;
    }

    public void setDateReccurent(LocalDate dateRecurrent) {
        _dateRecurrent = dateRecurrent;
    }

    @Override
    public String toString() {
        return getNom() + " " + getObjectif() + "€";
    }

    @Override
    public Budget getDBObject() {
        return new Budget(getAppId(), getObjectif(), getMontantUtilise(), getNom(), isActif(), getPriority(),
                getLabelRecurrent(), getDateRecurrent());
    }

}
