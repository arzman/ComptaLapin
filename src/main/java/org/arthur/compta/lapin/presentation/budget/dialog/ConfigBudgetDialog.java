package org.arthur.compta.lapin.presentation.budget.dialog;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.BudgetManager;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Fenêtre permettant de gérer les budgets : supprimer et modifier leur ordre
 */
public class ConfigBudgetDialog extends ComptaDialog {

private final DefaultListModel<AppBudget> _listModel;

public ConfigBudgetDialog() {
super(ConfigBudgetDialog.class.getSimpleName(), "Gestion des budgets");

_listModel = new DefaultListModel<>();

// chargement des budgets actifs triés par priorité
List<AppBudget> budgets = new ArrayList<>(BudgetManager.getInstance().getBudgetList());
budgets.sort(Comparator.comparingInt(AppBudget::getPriority));
for (AppBudget b : budgets) {
_listModel.addElement(b);
}

JList<AppBudget> list = new JList<>(_listModel);
list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
list.setCellRenderer(new DefaultListCellRenderer() {
@Override
public Component getListCellRendererComponent(JList<?> l, Object value, int index, boolean sel, boolean focus) {
JLabel lbl = (JLabel) super.getListCellRendererComponent(l, value, index, sel, focus);
if (value instanceof AppBudget) {
lbl.setText(((AppBudget) value).getNom());
}
return lbl;
}
});

// boutons monter / descendre
JButton upBtn = new JButton(new ImageIcon(ImageLoader.getImageIcon(ImageLoader.UP_IMG).getImage()));
upBtn.addActionListener(e -> {
int sel = list.getSelectedIndex();
if (sel > 0) {
AppBudget item = _listModel.remove(sel);
_listModel.insertElementAt(item, sel - 1);
list.setSelectedIndex(sel - 1);
}
});
JButton downBtn = new JButton(new ImageIcon(ImageLoader.getImageIcon(ImageLoader.DOWN_IMG).getImage()));
downBtn.addActionListener(e -> {
int sel = list.getSelectedIndex();
if (sel >= 0 && sel < _listModel.size() - 1) {
AppBudget item = _listModel.remove(sel);
_listModel.insertElementAt(item, sel + 1);
list.setSelectedIndex(sel + 1);
}
});

JPanel orderPanel = new JPanel(new GridLayout(2, 1, 0, 2));
orderPanel.add(upBtn);
orderPanel.add(downBtn);

JButton okBtn = new JButton("Ok");
okBtn.addActionListener(e -> {
for (int i = 0; i < _listModel.size(); i++) {
_listModel.getElementAt(i).setPriority(i);
}
BudgetManager.getInstance().calculateData();
List<AppBudget> ordered = new ArrayList<>();
for (int i = 0; i < _listModel.size(); i++) {
ordered.add(_listModel.getElementAt(i));
}
try {
BudgetManager.getInstance().updateBudgets(ordered);
} catch (ComptaException ex) {
ExceptionDisplayService.showException(ex);
}
_confirmed = true;
dispose();
});
JButton cancelBtn = new JButton("Annuler");
cancelBtn.addActionListener(e -> dispose());

JPanel center = new JPanel(new BorderLayout());
center.add(new JScrollPane(list), BorderLayout.CENTER);
center.add(orderPanel, BorderLayout.EAST);

JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
btnPanel.add(okBtn);
btnPanel.add(cancelBtn);

setLayout(new BorderLayout());
add(center, BorderLayout.CENTER);
add(btnPanel, BorderLayout.SOUTH);
setSize(300, 400);
}

}
