package org.arthur.compta.lapin.presentation.trimestre.dialog;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppExerciceMensuelLightId;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;

/**
 * Dialog permettant de retourner un appId d'un exercice mensuel
 */
public class SelectExerciceMensuelDialog extends ComptaDialog {

private final DefaultListModel<String> _listModel;
private final JList<String> _listV;
private final JComboBox<Integer> _moisCombo;
private AppExerciceMensuelLightId _result = null;
private final HashMap<String, LocalDate> _resumeTrimestre;

public SelectExerciceMensuelDialog() {
super(SelectExerciceMensuelDialog.class.getSimpleName(), "Sélectionner un exercice mensuel");

_resumeTrimestre = new HashMap<>();
_listModel = new DefaultListModel<>();
_listV = new JList<>(_listModel);
_listV.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
_listV.setCellRenderer(new TrimestreListCellRenderer());

try {
HashMap<String, LocalDate> map = TrimestreManager.getInstance().getAllTrimestreShortList();
_resumeTrimestre.putAll(map);
for (String id : map.keySet()) {
_listModel.addElement(id);
}
} catch (ComptaException e) {
ExceptionDisplayService.showException(e);
}

// combo de sélection du mois
_moisCombo = new JComboBox<>(new Integer[]{0, 1, 2});
_moisCombo.setRenderer(new NumMoisComboRenderer());

// boutons
JButton okBtn = new JButton("Ok");
okBtn.addActionListener(e -> {
String id = _listV.getSelectedValue();
Integer num = (Integer) _moisCombo.getSelectedItem();
if (id != null && !id.isEmpty() && num != null) {
try {
_result = new AppExerciceMensuelLightId(
TrimestreManager.getInstance().getExerciceMensuelId(Integer.parseInt(id), num),
Integer.parseInt(id), num);
_confirmed = true;
} catch (ComptaException ex) {
ExceptionDisplayService.showException(ex);
}
}
dispose();
});
JButton cancelBtn = new JButton("Annuler");
cancelBtn.addActionListener(e -> dispose());

JPanel content = new JPanel(new BorderLayout(5, 5));
content.add(new JScrollPane(_listV), BorderLayout.CENTER);
content.add(_moisCombo, BorderLayout.SOUTH);

JPanel btnPanel = new JPanel(new FlowLayout());
btnPanel.add(okBtn);
btnPanel.add(cancelBtn);

setLayout(new BorderLayout());
add(content, BorderLayout.CENTER);
add(btnPanel, BorderLayout.SOUTH);
pack();
}

/** Retourne l'exercice mensuel sélectionné */
public AppExerciceMensuelLightId getResult() {
return _result;
}

private class TrimestreListCellRenderer extends DefaultListCellRenderer {
@Override
public Component getListCellRendererComponent(JList<?> list, Object value, int index,
boolean isSelected, boolean cellHasFocus) {
JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
String id = (String) value;
if (id != null && !id.isEmpty() && _resumeTrimestre.containsKey(id)) {
LocalDate deb = _resumeTrimestre.get(id);
lbl.setText("De " + ApplicationFormatter.moiAnneedateFormat.format(deb)
+ " à " + ApplicationFormatter.moiAnneedateFormat.format(deb.plusMonths(2)));
}
return lbl;
}
}

private static class NumMoisComboRenderer extends DefaultListCellRenderer {
@Override
public Component getListCellRendererComponent(JList<?> list, Object value, int index,
boolean isSelected, boolean cellHasFocus) {
JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
if (value instanceof Integer) {
switch ((Integer) value) {
case 0: lbl.setText("1er Mois"); break;
case 1: lbl.setText("2ème Mois"); break;
case 2: lbl.setText("3ème Mois"); break;
default: lbl.setText("#ERROR#");
}
}
return lbl;
}
}

}
