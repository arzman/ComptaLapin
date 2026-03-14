package org.arthur.compta.lapin.application.model;

import java.time.LocalDate;

/**
 * Objet permettant l'affichage d'un résultat de recherche d'opération
 *
 */
public class OperationSearchResult {

    /** Le libelle de l'opération */
    private final String _libelle;
    /** Le montant de l'opération */
    private final double _montant;
    /** Le mois de l'opération */
    private final LocalDate _mois;

    /**
     * Constructeur
     * 
     * @param libelle
     *            le libelle
     * @param montant
     *            le montant
     * @param date
     *            le mois
     */
    public OperationSearchResult(String libelle, double montant, LocalDate date) {
        _libelle = libelle;
        _montant = montant;
        _mois = date;
    }

    /** Retourne le libellé */
    public String getLibelle() {
        return _libelle;
    }

    /** Retourne le montant */
    public double getMontant() {
        return _montant;
    }

    /** Retourne la date de début de l'exercice de l'opération */
    public LocalDate getMois() {
        return _mois;
    }

}
