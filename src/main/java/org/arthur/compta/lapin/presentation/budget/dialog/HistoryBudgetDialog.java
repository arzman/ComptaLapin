package org.arthur.compta.lapin.presentation.budget.dialog;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.BudgetManager;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.application.model.AppUtilisation;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Fenêtre de visualisation de l'historique d'un budget
 */
public class HistoryBudgetDialog extends ComptaDialog {

private final AppBudget _appBudget;
private final UtilisationTableModel _model;
private final JTable _table;

public HistoryBudgetDialog(AppBudget app) {
super(HistoryBudgetDialog.class.getSimpleName(), "Historique du budget");
_appBudget = app;

_model = new UtilisationTableModel();
_table = new JTable(_model);
_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

try {
_model.setData(BudgetManager.getInstance().getUtilisation(_appBudget.getAppId()));
} catch (ComptaException e) {
ExceptionDisplayService.showException(e);
}

JPanel header = new JPanel(new GridLayout(3, 2, 5, 2));
header.add(new JLabel("Nom : ")); header.add(new JLabel(_appBudget.getNom()));
header.add(new JLabel("Objectif : ")); header.add(new JLabel(ApplicationFormatter.montantFormat.format(_appBudget.getObjectif())));
header.add(new JLabel("Utilisé : ")); header.add(new JLabel(ApplicationFormatter.montantFormat.format(_appBudget.getMontantUtilise())));

// menu contextuel
JPopupMenu menu = new JPopupMenu();
JMenuItem editItem = new JMenuItem("Editer", new ImageIcon(ImageLoader.getImageIcon(ImageLoader.EDIT_IMG).getImage()));
editItem.addActionListener(e -> {
int row = _table.getSelectedRow();
if (row >= 0) {
AppUtilisation util = _model.getRow(row);
EditUtilisationDialog dia = new EditUtilisationDialog(util);
dia.setVisible(true);
_model.fireTableRowsUpdated(row, row);
}
});
menu.add(editItem);

JMenuItem delItem = new JMenuItem("Supprimer", new ImageIcon(ImageLoader.getImageIcon(ImageLoader.DEL_IMG).getImage()));
delItem.addActionListener(e -> {
int row = _table.getSelectedRow();
if (row >= 0) {
AppUtilisation util = _model.getRow(row);
try {
BudgetManager.getInstance().removeUtilisation(util);
List<AppUtilisation> data = new ArrayList<>(_model._data);
data.remove(util);
_model.setData(data);
} catch (ComptaException ex) {
ExceptionDisplayService.showException(ex);
}
}
});
menu.add(delItem);

_table.addMouseListener(new MouseAdapter() {
@Override
public void mousePressed(MouseEvent e) {
if (SwingUtilities.isRightMouseButton(e)) {
int row = _table.rowAtPoint(e.getPoint());
if (row >= 0) _table.setRowSelectionInterval(row, row);
boolean sel = _table.getSelectedRow() >= 0;
editItem.setEnabled(sel);
delItem.setEnabled(sel);
menu.show(_table, e.getX(), e.getY());
}
}
});

JButton closeBtn = new JButton("Fermer");
closeBtn.addActionListener(e -> dispose());
JPanel btnPanel = new JPanel(new FlowLayout());
btnPanel.add(closeBtn);

setLayout(new BorderLayout(5, 5));
add(header, BorderLayout.NORTH);
add(new JScrollPane(_table), BorderLayout.CENTER);
add(btnPanel, BorderLayout.SOUTH);

setSize(500, 400);
}

private static class UtilisationTableModel extends AbstractTableModel {
private static final String[] COLS = {"Libellé", "Date", "Montant"};
List<AppUtilisation> _data = new ArrayList<>();

public void setData(List<AppUtilisation> data) { _data = data; fireTableDataChanged(); }
public AppUtilisation getRow(int row) { return (row >= 0 && row < _data.size()) ? _data.get(row) : null; }

@Override public int getRowCount() { return _data.size(); }
@Override public int getColumnCount() { return COLS.length; }
@Override public String getColumnName(int col) { return COLS[col]; }

@Override
public Object getValueAt(int row, int col) {
AppUtilisation u = _data.get(row);
switch (col) {
case 0: return u.getNom();
case 1: return u.getDate() != null ? ApplicationFormatter.databaseDateFormat.format(u.getDate()) : "";
case 2: return ApplicationFormatter.montantFormat.format(u.getMontant());
default: return null;
}
}
}

}
