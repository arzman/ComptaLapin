package org.arthur.compta.lapin.presentation.menu;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.service.ComptaService;
import org.arthur.compta.lapin.presentation.budget.dialog.ConfigBudgetDialog;
import org.arthur.compta.lapin.presentation.budget.dialog.EditBudgetDialog;
import org.arthur.compta.lapin.presentation.budget.dialog.VisuBudgetDialog;
import org.arthur.compta.lapin.presentation.common.dialog.DateDialog;
import org.arthur.compta.lapin.presentation.compte.dialog.EditCompteDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.operation.dialog.SearchOperationDialog;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;
import org.arthur.compta.lapin.presentation.scene.MainPane;
import org.arthur.compta.lapin.presentation.synth.RapportTrimDialog;
import org.arthur.compta.lapin.presentation.synth.SynthAnnuelleDialog;
import org.arthur.compta.lapin.presentation.template.dialog.ConfigureTemplateDialog;
import org.arthur.compta.lapin.presentation.trimestre.dialog.CreateTrimestreDialog;
import org.arthur.compta.lapin.presentation.trimestre.dialog.ManageTrimestreCourantDialog;

import javax.swing.*;
import java.time.LocalDate;

/**
 * Barre de menu de l'application
 */
public class ComptaMenuBar extends JMenuBar {

private final MainPane _mainPane;
/** Menu affichant la date de dernière vérif */
private JMenu _datMenu;

public ComptaMenuBar(MainPane mainPane) {
super();
_mainPane = mainPane;

createSystemMenu();
createTrimestreMenu();
createOperationMenu();
createBudgetMenu();
createCompteMenu();
createSynthMenu();
createDerVerifItem();
}

private void createSynthMenu() {
JMenu synthMenu = new JMenu("Synthèse");
synthMenu.setIcon(ImageLoader.getImageIcon(ImageLoader.CHART_IMG));
add(synthMenu);

JMenuItem syAn = new JMenuItem("Graphique annuel", ImageLoader.getImageIcon(ImageLoader.CALENDRIER_IMG));
syAn.addActionListener(e -> new SynthAnnuelleDialog().setVisible(true));
synthMenu.add(syAn);

JMenuItem trimRap = new JMenuItem("Rapport Trimestriel", ImageLoader.getImageIcon(ImageLoader.BOOK_IMG));
trimRap.addActionListener(e -> new RapportTrimDialog().setVisible(true));
synthMenu.add(trimRap);
}

private void createSystemMenu() {
JMenu sysMenu = new JMenu("Système");
sysMenu.setIcon(ImageLoader.getImageIcon(ImageLoader.SYSTEM_IMG));

JMenuItem prefItem = new JMenuItem("Sauver taille", ImageLoader.getImageIcon(ImageLoader.PREF_IMG));
prefItem.addActionListener(e -> _mainPane.saveSplitPosition());
sysMenu.add(prefItem);
add(sysMenu);
}

private void createTrimestreMenu() {
JMenu trimMenu = new JMenu("Trimestre");
trimMenu.setIcon(ImageLoader.getImageIcon(ImageLoader.TRIMESTRE_IMG));

JMenuItem addItem = new JMenuItem("Créer un trimestre", ImageLoader.getImageIcon(ImageLoader.ADD_IMG));
addItem.addActionListener(e -> new CreateTrimestreDialog().setVisible(true));
trimMenu.add(addItem);

JMenuItem selectItem = new JMenuItem("Sélectionner trimestre", ImageLoader.getImageIcon(ImageLoader.SELECT_TRIM_IMG));
selectItem.addActionListener(e -> {
ManageTrimestreCourantDialog dia = new ManageTrimestreCourantDialog();
dia.setVisible(true);
Integer id = dia.getResult();
if (id != null && id != -1) {
try {
TrimestreManager.getInstance().loadTrimestreCourant(id);
} catch (ComptaException ex) {
ExceptionDisplayService.showException(ex);
}
}
});
trimMenu.add(selectItem);

JMenuItem configItem = new JMenuItem("Configurer modèle", ImageLoader.getImageIcon(ImageLoader.CONFIG_TMP_IMG));
configItem.addActionListener(e -> new ConfigureTemplateDialog().setVisible(true));
trimMenu.add(configItem);

add(trimMenu);
}

private void createOperationMenu() {
JMenu opMenu = new JMenu("Opération");
opMenu.setIcon(ImageLoader.getImageIcon(ImageLoader.OPERATION_IMG));

JMenuItem searchItem = new JMenuItem("Rechercher", ImageLoader.getImageIcon(ImageLoader.LOUPE_IMG));
searchItem.addActionListener(e -> new SearchOperationDialog().setVisible(true));
opMenu.add(searchItem);

add(opMenu);
}

private void createBudgetMenu() {
JMenu budMenu = new JMenu("Budget");
budMenu.setIcon(ImageLoader.getImageIcon(ImageLoader.BUDGET_IMG));
add(budMenu);

JMenuItem addItem = new JMenuItem("Créer un budget", ImageLoader.getImageIcon(ImageLoader.ADD_IMG));
addItem.addActionListener(e -> new EditBudgetDialog(null).setVisible(true));
budMenu.add(addItem);

JMenuItem gestItem = new JMenuItem("Gestion des budgets", ImageLoader.getImageIcon(ImageLoader.CONFIG_TMP_IMG));
gestItem.addActionListener(e -> new ConfigBudgetDialog().setVisible(true));
budMenu.add(gestItem);

JMenuItem visiItem = new JMenuItem("Visualiser les budgets", ImageLoader.getImageIcon(ImageLoader.CYCLE_IMG));
visiItem.addActionListener(e -> new VisuBudgetDialog().setVisible(true));
budMenu.add(visiItem);
}

private void createCompteMenu() {
JMenu compteMenu = new JMenu("Compte");
compteMenu.setIcon(ImageLoader.getImageIcon(ImageLoader.COMPTE_IMG));

JMenuItem addItem = new JMenuItem("Créer un compte", ImageLoader.getImageIcon(ImageLoader.ADD_IMG));
addItem.addActionListener(e -> new EditCompteDialog(null).setVisible(true));
compteMenu.add(addItem);
add(compteMenu);
}

private void createDerVerifItem() {
try {
String dat = ComptaService.getDateDerVerif();
_datMenu = new JMenu("Vérif : " + dat);
_datMenu.setIcon(ImageLoader.getImageIcon(ImageLoader.VERIF_IMG));

JMenuItem verifNow = new JMenuItem("Vérifier", ImageLoader.getImageIcon(ImageLoader.VALID_IMG));
verifNow.addActionListener(e -> {
try {
ComptaService.setDateDerVerif(LocalDate.now());
_datMenu.setText("Vérif : " + ComptaService.getDateDerVerif());
} catch (ComptaException ex) {
ExceptionDisplayService.showException(ex);
}
});
_datMenu.add(verifNow);

JMenuItem modVerif = new JMenuItem("Changer la date", ImageLoader.getImageIcon(ImageLoader.CALENDRIER_IMG));
modVerif.addActionListener(e -> {
try {
DateDialog dia = new DateDialog(null);
dia.setVisible(true);
LocalDate res = dia.getResult();
if (res != null) {
ComptaService.setDateDerVerif(res);
_datMenu.setText("Vérif : " + ComptaService.getDateDerVerif());
}
} catch (ComptaException ex) {
ExceptionDisplayService.showException(ex);
}
});
_datMenu.add(modVerif);

add(_datMenu);
} catch (ComptaException e) {
ExceptionDisplayService.showException(e);
}
}

}
