package org.arthur.compta.lapin.presentation.trimestre.dialog;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Fenêtre de sélection d'un trimestre courant
 */
public class ManageTrimestreCourantDialog extends ComptaDialog {

private final DefaultListModel<String> _listModel;
private final JList<String> _listV;
private final HashMap<String, LocalDate> _resumeTrimestre;
private Integer _result = null;

public ManageTrimestreCourantDialog() {
super(ManageTrimestreCourantDialog.class.getSimpleName(), "Sélection du trimestre courant");

_listModel = new DefaultListModel<>();
_listV = new JList<>(_listModel);
_listV.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
_listV.setCellRenderer(new TrimestreListCellRenderer());

_resumeTrimestre = new HashMap<>();
try {
HashMap<String, LocalDate> map = TrimestreManager.getInstance().getAllTrimestreShortList();
_resumeTrimestre.putAll(map);
for (String id : map.keySet()) {
_listModel.addElement(id);
}
} catch (ComptaException e) {
ExceptionDisplayService.showException(e);
}

// menu contextuel
JPopupMenu ctxMenu = new JPopupMenu();
JMenuItem delItem = new JMenuItem("Supprimer", new ImageIcon(ImageLoader.getImageIcon(ImageLoader.DEL_IMG).getImage()));
delItem.addActionListener(e -> {
String sel = _listV.getSelectedValue();
if (sel != null) {
try {
TrimestreManager.getInstance().removeTrimestre(Integer.parseInt(sel));
_listModel.removeElement(sel);
} catch (ComptaException ex) {
ExceptionDisplayService.showException(ex);
}
}
});
ctxMenu.add(delItem);
_listV.setComponentPopupMenu(ctxMenu);

// boutons
JButton okBtn = new JButton("Ok");
okBtn.addActionListener(e -> {
String sel = _listV.getSelectedValue();
if (sel != null) {
_result = Integer.parseInt(sel);
_confirmed = true;
}
dispose();
});
JButton cancelBtn = new JButton("Annuler");
cancelBtn.addActionListener(e -> dispose());

setLayout(new BorderLayout());
add(new JScrollPane(_listV), BorderLayout.CENTER);
JPanel btnPanel = new JPanel(new FlowLayout());
btnPanel.add(okBtn);
btnPanel.add(cancelBtn);
add(btnPanel, BorderLayout.SOUTH);
pack();
}

/** Retourne l'id du trimestre sélectionné, ou null */
public Integer getResult() {
return _result;
}

/** Rendu de la liste avec dates */
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

}
