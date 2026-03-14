package org.arthur.compta.lapin.application.model.template;

import org.arthur.compta.lapin.application.model.AppCompte;

/**
 * Element d'un template d'exercice mensuel.
 * Il s'agit d'une opération ayant une fréquence donnée dans un mois.
 */
public class TrimestreTemplateElement {

/** nom */
private String _nom;
/** montant de l'opération */
private double _montant;
/** type de l'opération */
private String _type;
/** La fréquence de l'opération */
private String _freq;
/** l'occurence */
private int _occurence;
/** compte source */
private AppCompte _compteSource;
/** compte cible */
private AppCompte _compteCible;

/** Constructeur */
public TrimestreTemplateElement() {
}

/** Retourne la fréquence de l'opération */
public TrimestreTemplateElementFrequence getFreq() {
return TrimestreTemplateElementFrequence.valueOf(_freq);
}

/** Retourne l'occurence */
public int getOccurence() {
return _occurence;
}

/** Positionne la fréquence de l'opération */
public void setFreq(TrimestreTemplateElementFrequence freq) {
_freq = freq.toString();
}

/** Positionne l'occurence */
public void setOccurence(int occurence) {
_occurence = occurence;
}

/** Positionne le nom */
public void setNom(String nom) {
_nom = nom;
}

/** Positionne le montant */
public void setMontant(double montant) {
_montant = montant;
}

/** Positionne le type */
public void setType(String type) {
_type = type;
}

/** Positionne le compte source */
public void setCompteSource(AppCompte compte) {
_compteSource = compte;
}

/** Positionne le compte cible */
public void setCompteCible(AppCompte compte) {
_compteCible = compte;
}

/** Retourne le nom de l'élément */
public String getNom() {
return _nom;
}

/** Retourne le montant associé à l'élément */
public double getMontant() {
return _montant;
}

/** Retourne le type */
public String getType() {
return _type;
}

/** Retourne le compte source */
public AppCompte getCompteSource() {
return _compteSource;
}

/** Retourne le compte cible */
public AppCompte getCompteCible() {
return _compteCible;
}

}
