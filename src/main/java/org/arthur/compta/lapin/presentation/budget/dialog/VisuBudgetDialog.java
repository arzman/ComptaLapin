package org.arthur.compta.lapin.presentation.budget.dialog;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.BudgetManager;
import org.arthur.compta.lapin.application.model.AppBudget;
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
import java.util.Iterator;
import java.util.List;

/**
 * Fenêtre de visualisation de tous les budgets
 */
public class VisuBudgetDialog extends ComptaDialog {

private final SimpleBudgetTableModel _nonRecModel;
private final SimpleBudgetTableModel _recModel;
private final JTable _tableNonRec;
private final JTable _tableRec;

public VisuBudgetDialog() {
super(VisuBudgetDialog.class.getSimpleName(), "Visualiser les budgets");

_nonRecModel = new SimpleBudgetTableModel();
_recModel = new SimpleBudgetTableModel();
_tableNonRec = new JTable(_nonRecModel);
_tableRec = new JTable(_recModel);

initValues();
createContextMenu(_tableNonRec, _nonRecModel, false);
createContextMenu(_tableRec, _recModel, true);

JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
new JScrollPane(_tableNonRec), new JScrollPane(_tableRec));
split.setResizeWeight(0.5);

JButton closeBtn = new JButton("Fermer");
closeBtn.addActionListener(e -> dispose());
JPanel btnPanel = new JPanel(new FlowLayout());
btnPanel.add(closeBtn);

setLayout(new BorderLayout());
add(split, BorderLayout.CENTER);
add(btnPanel, BorderLayout.SOUTH);
setSize(700, 400);
}

private void initValues() {
try {
List<AppBudget> nonRec = new ArrayList<>();
List<AppBudget> rec = new ArrayList<>();
for (AppBudget b : BudgetManager.getInstance().getAllBudgets()) {
if (b.getLabelRecurrent().isEmpty()) nonRec.add(b);
else rec.add(b);
}
_nonRecModel.setData(nonRec);
_recModel.setData(rec);
} catch (ComptaException e) {
ExceptionDisplayService.showException(e);
}
}

private void createContextMenu(JTable table, SimpleBudgetTableModel model, boolean isRec) {
JPopupMenu menu = new JPopupMenu();
JMenuItem delItem = new JMenuItem("Supprimer", new ImageIcon(ImageLoader.getImageIcon(ImageLoader.DEL_IMG).getImage()));
delItem.addActionListener(e -> {
int row = table.getSelectedRow();
if (row >= 0) {
AppBudget item = model.getRow(row);
if (item != null) {
try {
BudgetManager.getInstance().removeBudget(item);
List<AppBudget> data = new ArrayList<>(model._data);
Iterator<AppBudget> iter = data.iterator();
while (iter.hasNext()) {
if (iter.next().getAppId() == item.getAppId()) {
iter.remove();
break;
}
}
model.setData(data);
} catch (ComptaException ex) {
ExceptionDisplayService.showException(ex);
}
}
}
});
menu.add(delItem);
table.addMouseListener(new MouseAdapter() {
@Override
public void mousePressed(MouseEvent e) {
if (SwingUtilities.isRightMouseButton(e)) {
int row = table.rowAtPoint(e.getPoint());
if (row >= 0) table.setRowSelectionInterval(row, row);
delItem.setEnabled(table.getSelectedRow() >= 0);
menu.show(table, e.getX(), e.getY());
}
}
});
}

private static class SimpleBudgetTableModel extends AbstractTableModel {
private static final String[] COLS = {"Nom", "Objectif", "Label"};
List<AppBudget> _data = new ArrayList<>();

public void setData(List<AppBudget> data) { _data = data; fireTableDataChanged(); }
public AppBudget getRow(int row) { return (row >= 0 && row < _data.size()) ? _data.get(row) : null; }

@Override public int getRowCount() { return _data.size(); }
@Override public int getColumnCount() { return COLS.length; }
@Override public String getColumnName(int col) { return COLS[col]; }

@Override
public Object getValueAt(int row, int col) {
AppBudget b = _data.get(row);
switch (col) {
case 0: return b.getNom();
case 1: return ApplicationFormatter.montantFormat.format(b.getObjectif());
case 2: return b.getLabelRecurrent();
default: return null;
}
}
}

}
