package org.arthur.compta.lapin.application.model;

import org.arthur.compta.lapin.model.Utilisation;

import java.time.LocalDate;

/**
 * Encapsulation applicative d'une Utilisation de Budget
 *
 */
public class AppUtilisation extends AppObject<Utilisation> {

    /** Le libellé */
    private String _nom;
    /** La date */
    private LocalDate _date;
    /** Le montant */
    private double _montant;

    /**
     * Constructeur
     * 
     * @param utilisation
     *            utilisation métier
     */
    public AppUtilisation(Utilisation utilisation) {
        setAppID(utilisation.getId());
        _nom = utilisation.getLibelle();
        _date = utilisation.getDate();
        _montant = utilisation.getMontant();
    }

    /** Positionne le nom */
    public void setNom(String nom) {
        _nom = nom;
    }

    /** Positionne le montant */
    public void setMontant(double montant) {
        _montant = montant;
    }

    /** Positionne la date */
    public void setDate(LocalDate date) {
        _date = date;
    }

    /** Retourne le nom */
    public String getNom() {
        return _nom;
    }

    /** Retourne le montant */
    public double getMontant() {
        return _montant;
    }

    /** Retourne la date */
    public LocalDate getDate() {
        return _date;
    }

    @Override
    public Utilisation getDBObject() {
        return new Utilisation(getAppId(), getMontant(), getNom(), getDate());
    }

}
