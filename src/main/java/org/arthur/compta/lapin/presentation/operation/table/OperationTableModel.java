package org.arthur.compta.lapin.presentation.operation.table;

import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle de tableau pour les opérations
 */
public class OperationTableModel<T extends AppOperation> extends AbstractTableModel {

protected static final String[] COLUMNS = {"", "Libellé", "Montant"};
protected List<T> _data;

public OperationTableModel() {
_data = new ArrayList<>();
}

@Override
public int getRowCount() {
return _data.size();
}

@Override
public int getColumnCount() {
return COLUMNS.length;
}

@Override
public String getColumnName(int col) {
return COLUMNS[col];
}

@Override
public Object getValueAt(int row, int col) {
if (row < 0 || row >= _data.size()) return null;
T op = _data.get(row);
switch (col) {
case 0:
return op.getEtat();
case 1:
return op.getLibelle();
case 2:
return ApplicationFormatter.montantFormat.format(op.getMontant());
default:
return null;
}
}

@Override
public Class<?> getColumnClass(int col) {
if (col == 0) return EtatOperation.class;
return String.class;
}

/** Remplace les données du modèle */
public void setData(List<? extends T> data) {
_data = new ArrayList<>();
if (data != null) {
_data.addAll(data);
}
fireTableDataChanged();
}

/** Retourne l'opération à la ligne donnée */
public T getRow(int row) {
if (row >= 0 && row < _data.size()) {
return _data.get(row);
}
return null;
}

}
