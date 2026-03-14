package org.arthur.compta.lapin.presentation.trimestre.pane;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppExerciceMensuelLightId;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.operation.dialog.CreateOperationDialog;
import org.arthur.compta.lapin.presentation.operation.table.EtatCellRenderer;
import org.arthur.compta.lapin.presentation.operation.table.OperationTableModel;
import org.arthur.compta.lapin.presentation.operation.table.TransfertTableModel;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;
import org.arthur.compta.lapin.presentation.trimestre.dialog.SelectExerciceMensuelDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panneau de présentation d'un exercice mensuel : tableau des dépenses,
 * tableau des ressources et tableau des transferts.
 */
public class ExerciceMensuelPane extends JPanel {

/** En-tête avec date et résultat */
private final ExerciceHeaderPane _title;
/** Onglets pour les 3 types d'opérations */
private final JTabbedPane _tabs;
/** Tableau des dépenses */
private final JTable _depenseTable;
private final OperationTableModel<AppOperation> _depenseModel;
/** Tableau des ressources */
private final JTable _ressourceTable;
private final OperationTableModel<AppOperation> _ressourceModel;
/** Tableau des transferts */
private final JTable _transfertTable;
private final TransfertTableModel _transfertModel;
/** Numéro du mois présenté */
private final int _numMois;

public ExerciceMensuelPane(String id, int numMois) {
super(new BorderLayout(0, 2));
_numMois = numMois;

// en-tête
_title = new ExerciceHeaderPane(TrimestreManager.getInstance().getResultatPrev(_numMois));
add(_title, BorderLayout.NORTH);

// création des modèles de tableaux
_depenseModel = new OperationTableModel<>();
_ressourceModel = new OperationTableModel<>();
_transfertModel = new TransfertTableModel();

// création des tableaux
_depenseTable = createOperationTable(_depenseModel);
_ressourceTable = createOperationTable(_ressourceModel);
_transfertTable = createOperationTable(_transfertModel);

// onglets
_tabs = new JTabbedPane();
_tabs.addTab("Dépenses", new JScrollPane(_depenseTable));
_tabs.addTab("Ressources", new JScrollPane(_ressourceTable));
_tabs.addTab("Transferts", new JScrollPane(_transfertTable));
add(_tabs, BorderLayout.CENTER);

// bordure bleue
setBorder(BorderFactory.createLineBorder(Color.CYAN.darker(), 1));

// menus contextuels
createContextMenu(_depenseTable, _depenseModel, "DEPENSE");
createContextMenu(_ressourceTable, _ressourceModel, "RESSOURCE");
createContextMenu(_transfertTable, _transfertModel, "TRANSFERT");
}

private <T extends AppOperation> JTable createOperationTable(OperationTableModel<T> model) {
JTable table = new JTable(model);
table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
table.getColumnModel().getColumn(0).setCellRenderer(new EtatCellRenderer());
table.getColumnModel().getColumn(0).setMaxWidth(30);

// double-clic pour changer l'état
table.addMouseListener(new MouseAdapter() {
@Override
public void mouseClicked(MouseEvent e) {
if (e.getClickCount() == 2) {
int row = table.getSelectedRow();
if (row >= 0) {
T op = model.getRow(row);
if (op != null) {
try {
org.arthur.compta.lapin.application.service.OperationService.switchEtatOperation(op);
model.fireTableRowsUpdated(row, row);
} catch (ComptaException ex) {
ExceptionDisplayService.showException(ex);
}
}
}
}
}
});
return table;
}

private <T extends AppOperation> void createContextMenu(JTable table, OperationTableModel<T> model, String defaultType) {
JPopupMenu menu = new JPopupMenu();

// Ajouter
JMenuItem addOp = new JMenuItem("Ajouter", new ImageIcon(ImageLoader.getImageIcon(ImageLoader.ADD_IMG).getImage()));
addOp.addActionListener(e -> {
CreateOperationDialog cod = new CreateOperationDialog(null, _numMois, defaultType);
cod.setVisible(true);
refreshAfterChange();
});
menu.add(addOp);

// Éditer
JMenuItem editOp = new JMenuItem("Editer", new ImageIcon(ImageLoader.getImageIcon(ImageLoader.EDIT_IMG).getImage()));
editOp.addActionListener(e -> {
int row = table.getSelectedRow();
if (row >= 0) {
T appOp = model.getRow(row);
if (appOp != null) {
CreateOperationDialog cod = new CreateOperationDialog(appOp, _numMois, appOp.getType().toString());
cod.setVisible(true);
refreshAfterChange();
}
}
});
menu.add(editOp);

// Supprimer
JMenuItem removeOp = new JMenuItem("Supprimer", new ImageIcon(ImageLoader.getImageIcon(ImageLoader.DEL_IMG).getImage()));
removeOp.addActionListener(e -> {
int row = table.getSelectedRow();
if (row >= 0) {
T appOp = model.getRow(row);
if (appOp != null) {
try {
TrimestreManager.getInstance().removeOperation(appOp, _numMois);
refreshAfterChange();
} catch (ComptaException ex) {
ExceptionDisplayService.showException(ex);
}
}
}
});
menu.add(removeOp);

// Transférer
JMenuItem transOp = new JMenuItem("Transférer", new ImageIcon(ImageLoader.getImageIcon(ImageLoader.TRANSFERT_IMG).getImage()));
transOp.addActionListener(e -> {
int row = table.getSelectedRow();
if (row >= 0) {
T appOp = model.getRow(row);
if (appOp != null) {
SelectExerciceMensuelDialog semd = new SelectExerciceMensuelDialog();
semd.setVisible(true);
AppExerciceMensuelLightId lightId = semd.getResult();
if (lightId != null) {
try {
TrimestreManager.getInstance().moveOperationFromTrimCourant(appOp, _numMois, lightId);
refreshAfterChange();
} catch (ComptaException ex) {
ExceptionDisplayService.showException(ex);
}
}
}
}
});
menu.add(transOp);

// Affichage du menu contextuel sur clic droit
table.addMouseListener(new MouseAdapter() {
@Override
public void mousePressed(MouseEvent e) {
if (SwingUtilities.isRightMouseButton(e)) {
int row = table.rowAtPoint(e.getPoint());
if (row >= 0) {
table.setRowSelectionInterval(row, row);
}
boolean hasSelection = table.getSelectedRow() >= 0;
T op = hasSelection ? model.getRow(table.getSelectedRow()) : null;
editOp.setEnabled(hasSelection);
removeOp.setEnabled(hasSelection);
transOp.setEnabled(hasSelection && op != null && !op.getEtat().equals(EtatOperation.PRISE_EN_COMPTE));
menu.show(table, e.getX(), e.getY());
}
}
});
}

/** Rafraîchit les données après une modification */
private void refreshAfterChange() {
changeBind();
_title.setResultat(TrimestreManager.getInstance().getResultat(_numMois));
}

/**
 * Change l'affichage des mois
 */
public void changeBind() {
_title.setMois(TrimestreManager.getInstance().getDateDebut(_numMois));
_title.setResultatPrev(TrimestreManager.getInstance().getResultatPrev(_numMois));
_title.setResultat(TrimestreManager.getInstance().getResultat(_numMois));

_depenseModel.setData(TrimestreManager.getInstance().getDepenses(_numMois));
_ressourceModel.setData(TrimestreManager.getInstance().getRessources(_numMois));
_transfertModel.setData(TrimestreManager.getInstance().getTransfert(_numMois));
}

}
