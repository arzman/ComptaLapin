package org.arthur.compta.lapin.presentation.scene;

import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.presentation.budget.pane.BudgetPane;
import org.arthur.compta.lapin.presentation.compte.pane.ComptePane;
import org.arthur.compta.lapin.presentation.trimestre.pane.TrimestreCourantPane;

import javax.swing.*;
import java.awt.*;

/**
 * Panneau principal de l'IHM
 */
public class MainPane extends JPanel {

/** Séparation gauche/droite */
private final JSplitPane _splitGaucheDroite;
/** Séparation haut/bas (à droite) */
private final JSplitPane _splitHautBas;

public MainPane() {
super(new BorderLayout());

// positions des séparateurs
int posGD = Integer.parseInt(ConfigurationManager.getInstance().getProp("MainScene.splitgaucheDroite.pos", "600"));
int posHB = Integer.parseInt(ConfigurationManager.getInstance().getProp("MainScene.splitHautBas.pos", "300"));

// séparation haut-bas à droite
_splitHautBas = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new BudgetPane(), new ComptePane());
_splitHautBas.setDividerLocation(posHB);

// séparation gauche-droite
_splitGaucheDroite = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new TrimestreCourantPane(), _splitHautBas);
_splitGaucheDroite.setDividerLocation(posGD);

add(_splitGaucheDroite, BorderLayout.CENTER);
}

/**
 * Sauvegarde la position des séparateurs
 */
public void saveSplitPosition() {
ConfigurationManager.getInstance().setProp("MainScene.splitHautBas.pos",
String.valueOf(_splitHautBas.getDividerLocation()));
ConfigurationManager.getInstance().setProp("MainScene.splitgaucheDroite.pos",
String.valueOf(_splitGaucheDroite.getDividerLocation()));
}

}
