package org.arthur.compta.lapin.presentation.budget.pane;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.BudgetManager;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.presentation.budget.dialog.EditBudgetDialog;
import org.arthur.compta.lapin.presentation.budget.dialog.HistoryBudgetDialog;
import org.arthur.compta.lapin.presentation.budget.dialog.UseBudgetDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Panneau d'affichage des budgets
 */
public class BudgetPane extends JPanel {

/** Le tableau des budgets */
private final JTable _table;
private final BudgetTableModel _model;

public BudgetPane() {
super(new BorderLayout(2, 2));
setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

_model = new BudgetTableModel();
_table = new JTable(_model);
_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
_table.setRowHeight(22);

// rendu des colonnes
_table.getColumnModel().getColumn(0).setCellRenderer(new NomBudgetRenderer());
_table.getColumnModel().getColumn(1).setCellRenderer(new MontantRenderer());
_table.getColumnModel().getColumn(2).setCellRenderer(new AvancementRenderer());
_table.getColumnModel().getColumn(3).setCellRenderer(new MontantRenderer());
_table.getColumnModel().getColumn(4).setCellRenderer(new MontantRenderer());

add(new JScrollPane(_table), BorderLayout.CENTER);
createContextMenu();

// mise à jour automatique lors des changements
BudgetManager.getInstance().addChangeListener(() -> {
_model.setData(BudgetManager.getInstance().getBudgetList());
});
}

private void createContextMenu() {
JPopupMenu menu = new JPopupMenu();

JMenuItem useBudget = new JMenuItem("Utiliser",
new ImageIcon(ImageLoader.getImageIcon(ImageLoader.USE_BUDGET_IMG).getImage()));
useBudget.addActionListener(e -> {
int row = _table.getSelectedRow();
if (row >= 0) {
AppBudget appB = _model.getRow(row);
UseBudgetDialog dia = new UseBudgetDialog(appB);
dia.setVisible(true);
}
});
menu.add(useBudget);

JMenuItem showHistBudget = new JMenuItem("Historique",
new ImageIcon(ImageLoader.getImageIcon(ImageLoader.HISTORY_IMG).getImage()));
showHistBudget.addActionListener(e -> {
int row = _table.getSelectedRow();
if (row >= 0) {
AppBudget appB = _model.getRow(row);
HistoryBudgetDialog dia = new HistoryBudgetDialog(appB);
dia.setVisible(true);
}
});
menu.add(showHistBudget);

JMenuItem editBudget = new JMenuItem("Editer",
new ImageIcon(ImageLoader.getImageIcon(ImageLoader.EDIT_IMG).getImage()));
editBudget.addActionListener(e -> {
int row = _table.getSelectedRow();
if (row >= 0) {
AppBudget appB = _model.getRow(row);
EditBudgetDialog dia = new EditBudgetDialog(appB);
dia.setVisible(true);
}
});
menu.add(editBudget);

JMenuItem removeBudget = new JMenuItem("Désactiver",
new ImageIcon(ImageLoader.getImageIcon(ImageLoader.OFF_IMG).getImage()));
removeBudget.addActionListener(e -> {
int row = _table.getSelectedRow();
if (row >= 0) {
AppBudget appB = _model.getRow(row);
try {
BudgetManager.getInstance().desactivateBudget(appB);
} catch (ComptaException ex) {
ExceptionDisplayService.showException(ex);
}
}
});
menu.add(removeBudget);

_table.addMouseListener(new MouseAdapter() {
@Override
public void mousePressed(MouseEvent e) {
if (SwingUtilities.isRightMouseButton(e)) {
int row = _table.rowAtPoint(e.getPoint());
if (row >= 0) _table.setRowSelectionInterval(row, row);
boolean sel = _table.getSelectedRow() >= 0;
useBudget.setEnabled(sel);
showHistBudget.setEnabled(sel);
editBudget.setEnabled(sel);
removeBudget.setEnabled(sel);
menu.show(_table, e.getX(), e.getY());
}
}
});
}

/** Modèle de tableau pour les budgets */
public static class BudgetTableModel extends AbstractTableModel {
private static final String[] COLS = {"Nom", "Objectif", "Avancement", "CL", "CC"};
private List<AppBudget> _data;

public BudgetTableModel() {
_data = BudgetManager.getInstance().getBudgetList();
}

public void setData(List<AppBudget> data) {
_data = data;
fireTableDataChanged();
}

public AppBudget getRow(int row) {
return (row >= 0 && row < _data.size()) ? _data.get(row) : null;
}

@Override public int getRowCount() { return _data.size(); }
@Override public int getColumnCount() { return COLS.length; }
@Override public String getColumnName(int col) { return COLS[col]; }

@Override
public Object getValueAt(int row, int col) {
AppBudget b = _data.get(row);
switch (col) {
case 0: return b.getNom() + "#" + b.isTermine();
case 1: return b.getObjectif();
case 2: return b.getAvancement();
case 3: return b.getMontantLivret();
case 4: return b.getMontantCourant();
default: return null;
}
}

@Override
public Class<?> getColumnClass(int col) {
if (col == 0) return String.class;
return Double.class;
}
}

/** Rendu du nom : italique grisé si terminé */
private static class NomBudgetRenderer extends DefaultTableCellRenderer {
@Override
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
boolean hasFocus, int row, int column) {
JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
if (value instanceof String) {
String[] parts = ((String) value).split("#");
lbl.setText(parts.length > 0 ? parts[0] : "");
boolean termine = parts.length > 1 && Boolean.parseBoolean(parts[1]);
if (termine && !isSelected) {
lbl.setFont(lbl.getFont().deriveFont(Font.BOLD | Font.ITALIC));
lbl.setForeground(Color.DARK_GRAY);
} else {
lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));
if (!isSelected) lbl.setForeground(Color.BLACK);
}
}
return lbl;
}
}

/** Rendu monétaire */
private static class MontantRenderer extends DefaultTableCellRenderer {
@Override
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
boolean hasFocus, int row, int column) {
JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
lbl.setHorizontalAlignment(SwingConstants.RIGHT);
if (value instanceof Double) {
lbl.setText(ApplicationFormatter.montantFormat.format((Double) value));
}
return lbl;
}
}

/** Rendu de la barre d'avancement */
private static class AvancementRenderer implements TableCellRenderer {
@Override
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
boolean hasFocus, int row, int column) {
double val = value instanceof Double ? (Double) value : 0;
JProgressBar bar = new JProgressBar(0, 100);
bar.setValue((int) Math.round(Math.min(val * 100, 100)));
bar.setString(ApplicationFormatter.pourcentFormat.format(val));
bar.setStringPainted(true);

if (val < 0.25) {
bar.setForeground(Color.RED);
} else if (val < 0.5) {
bar.setForeground(Color.ORANGE);
} else if (val < 0.75) {
bar.setForeground(Color.YELLOW);
} else {
bar.setForeground(new Color(58, 242, 75));
}

if (isSelected) {
bar.setBackground(table.getSelectionBackground());
}
return bar;
}
}

}
