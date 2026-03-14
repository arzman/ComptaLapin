package org.arthur.compta.lapin.presentation.synth;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.service.SyntheseService;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Fenêtre traçant la synthèse sur l'année sous forme de tableau
 */
public class SynthAnnuelleDialog extends ComptaDialog {

private final JComboBox<Integer> _yearCombo;
private final SynthTableModel _model;

public SynthAnnuelleDialog() {
super(SynthAnnuelleDialog.class.getSimpleName(), "Synthèse Annuelle");

_model = new SynthTableModel();
JTable table = new JTable(_model);
table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

_yearCombo = new JComboBox<>();
_yearCombo.addActionListener(e -> {
Integer year = (Integer) _yearCombo.getSelectedItem();
if (year != null) {
try {
fillTable(year);
} catch (ComptaException ex) {
ExceptionDisplayService.showException(ex);
}
}
});

try {
List<Integer> annees = SyntheseService.getAnnees();
for (Integer a : annees) _yearCombo.addItem(a);
} catch (ComptaException e) {
ExceptionDisplayService.showException(e);
}

JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
topPanel.add(new JLabel("Sélection de l'année :"));
topPanel.add(_yearCombo);

JButton closeBtn = new JButton("Fermer");
closeBtn.addActionListener(e -> dispose());
JPanel btnPanel = new JPanel(new FlowLayout());
btnPanel.add(closeBtn);

setLayout(new BorderLayout(5, 5));
add(topPanel, BorderLayout.NORTH);
add(new JScrollPane(table), BorderLayout.CENTER);
add(btnPanel, BorderLayout.SOUTH);
setSize(700, 400);
}

private void fillTable(int year) throws ComptaException {
List<String[]> rows = new ArrayList<>();
for (int month = 1; month < 13; month++) {
LocalDate date = LocalDate.of(year, month, 1);
double dep = SyntheseService.getDepenseForMonth(date);
double res = SyntheseService.getRessourceForMonth(date);
double bud = SyntheseService.getBudgetUsageForMonth(date);
rows.add(new String[]{
ApplicationFormatter.moisFormat.format(date),
ApplicationFormatter.montantFormat.format(dep),
ApplicationFormatter.montantFormat.format(res),
ApplicationFormatter.montantFormat.format(bud)
});
}
_model.setData(rows);
}

private static class SynthTableModel extends AbstractTableModel {
private static final String[] COLS = {"Mois", "Dépenses", "Ressources", "Budget utilisé"};
private List<String[]> _data = new ArrayList<>();

public void setData(List<String[]> data) { _data = data; fireTableDataChanged(); }

@Override public int getRowCount() { return _data.size(); }
@Override public int getColumnCount() { return COLS.length; }
@Override public String getColumnName(int col) { return COLS[col]; }

@Override
public Object getValueAt(int row, int col) {
if (row < _data.size() && col < _data.get(row).length) return _data.get(row)[col];
return null;
}
}

}
