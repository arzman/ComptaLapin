package org.arthur.compta.lapin.presentation.trimestre.pane;

import org.arthur.compta.lapin.application.manager.TrimestreManager;

import javax.swing.*;
import java.awt.*;

/**
 * Panneau d'affichage du trimestre courant - composé des affichages des trois
 * ExerciceMensuel
 */
public class TrimestreCourantPane extends JPanel {

    /** Affichage du premier mois */
    private final ExerciceMensuelPane _premMoisPane;
    /** Affichage du deuxième mois */
    private final ExerciceMensuelPane _deuxMoisPane;
    /** Affichage du troisième mois */
    private final ExerciceMensuelPane _troisMoisPane;

    public TrimestreCourantPane() {
        super(new GridLayout(1, 3, 2, 2));
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        // panneau du premier mois
        _premMoisPane = new ExerciceMensuelPane("un", 0);
        add(_premMoisPane);

        // panneau du deuxième mois
        _deuxMoisPane = new ExerciceMensuelPane("deux", 1);
        add(_deuxMoisPane);

        // panneau du troisième mois
        _troisMoisPane = new ExerciceMensuelPane("trois", 2);
        add(_troisMoisPane);

        // écoute sur le changement du trimestre courant
        TrimestreManager.getInstance().addTrimestreChangeListener(newValue -> updateExerciceMensuelPane());

        // chargement initial
        updateExerciceMensuelPane();
    }

    /** Met à jour le contenu des IHM des exercices mensuels */
    private void updateExerciceMensuelPane() {
        _premMoisPane.changeBind();
        _deuxMoisPane.changeBind();
        _troisMoisPane.changeBind();
    }

}
