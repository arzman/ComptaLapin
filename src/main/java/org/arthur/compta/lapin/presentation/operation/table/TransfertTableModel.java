package org.arthur.compta.lapin.presentation.operation.table;

import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

/**
 * Modèle de tableau pour les transferts
 */
public class TransfertTableModel extends OperationTableModel<AppTransfert> {

private static final String[] TRANS_COLUMNS = {"", "Libellé", "Montant", "Source", "Cible"};

@Override
public int getColumnCount() {
return TRANS_COLUMNS.length;
}

@Override
public String getColumnName(int col) {
return TRANS_COLUMNS[col];
}

@Override
public Object getValueAt(int row, int col) {
if (row < 0 || row >= _data.size()) return null;
AppTransfert op = _data.get(row);
switch (col) {
case 0:
return op.getEtat();
case 1:
return op.getLibelle();
case 2:
return ApplicationFormatter.montantFormat.format(op.getMontant());
case 3:
return op.getCompteSource() != null ? op.getCompteSource().getNom() : "";
case 4:
return op.getCompteCible() != null ? op.getCompteCible().getNom() : "";
default:
return null;
}
}

@Override
public Class<?> getColumnClass(int col) {
if (col == 0) return EtatOperation.class;
return String.class;
}

}
