package org.arthur.compta.lapin.presentation.trimestre.pane;

import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

/**
 * Panneau permettant d'afficher le mois et le résultat pour un exercice mensuel
 */
public class ExerciceHeaderPane extends JPanel {

/** Label du mois */
private final JLabel _moisLbl;
/** Label du résultat */
private final JLabel _resultatLbl;
/** Résultat prévisionnel */
private double _prevRes;
/** Pas de date défini */
private static final String NO_CONTENT_DATE_STR = "######";

public ExerciceHeaderPane(double prev) {
super(new GridLayout(1, 2, 5, 0));
_prevRes = prev;

_moisLbl = new JLabel();
_moisLbl.setFont(new Font("Verdana", Font.PLAIN, 14));
add(_moisLbl);

_resultatLbl = new JLabel();
_resultatLbl.setHorizontalAlignment(SwingConstants.CENTER);
add(_resultatLbl);

setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
}

/**
 * Affiche la date sous forme mois année
 */
public void setMois(LocalDate time) {
if (time != null) {
_moisLbl.setText(ApplicationFormatter.moiAnneedateFormat.format(time));
} else {
_moisLbl.setText(NO_CONTENT_DATE_STR);
}
}

/**
 * Affiche le résultat
 */
public void setResultat(double res) {
if (res < 0) {
_resultatLbl.setForeground(Color.RED);
} else if (res > _prevRes) {
_resultatLbl.setForeground(new Color(0, 150, 0));
} else {
_resultatLbl.setForeground(Color.ORANGE.darker());
}
_resultatLbl.setText(ApplicationFormatter.montantFormat.format(res) + " / "
+ ApplicationFormatter.montantFormat.format(_prevRes));
}

/**
 * Positionne le résultat prévisionnel
 */
public void setResultatPrev(double resultatPrev) {
_prevRes = resultatPrev;
}

}
