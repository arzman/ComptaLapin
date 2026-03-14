package org.arthur.compta.lapin.presentation.operation.dialog;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.application.service.OperationService;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

/**
 * Fenêtre permettant la création et l'édition d'opération
 */
public class CreateOperationDialog extends ComptaDialog {

private AppOperation _operation;
private final int _numMois;
private final JTextField _libTxt = new JTextField(20);
private final JTextField _montantTxt = new JTextField(20);
private final JComboBox<String> _typeCombo;
private final JComboBox<AppCompte> _srcCombo;
private final JComboBox<AppCompte> _cibleCombo;
private final JButton _okBtn = new JButton("Ok");

public CreateOperationDialog(AppOperation op, int numMois, String typeOpe) {
super(CreateOperationDialog.class.getSimpleName(),
op == null ? "Création d'une opération" : "Edition d'une opération");
_operation = op;
_numMois = numMois;

// type combo
List<String> types = OperationService.getOperationType();
_typeCombo = new JComboBox<>(types.toArray(new String[0]));

// compte combos
List<AppCompte> comptes = CompteManager.getInstance().getCompteList();
DefaultComboBoxModel<AppCompte> cptModel = new DefaultComboBoxModel<>(comptes.toArray(new AppCompte[0]));
_srcCombo = new JComboBox<>(cptModel);
_cibleCombo = new JComboBox<>(new DefaultComboBoxModel<>(comptes.toArray(new AppCompte[0])));

// renderer pour les comptes
ListCellRenderer<Object> cptRenderer = new DefaultListCellRenderer() {
@Override
public Component getListCellRendererComponent(JList<?> l, Object v, int idx, boolean sel, boolean focus) {
JLabel lbl = (JLabel) super.getListCellRendererComponent(l, v, idx, sel, focus);
lbl.setText(v instanceof AppCompte ? ((AppCompte) v).getNom() : "");
return lbl;
}
};
_srcCombo.setRenderer(cptRenderer);
_cibleCombo.setRenderer(cptRenderer);

JPanel content = new JPanel(new GridBagLayout());
GridBagConstraints gbc = new GridBagConstraints();
gbc.insets = new Insets(4, 4, 4, 4);
gbc.anchor = GridBagConstraints.WEST;

int row = 0;
gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Libellé :"), gbc);
gbc.gridx = 1; content.add(_libTxt, gbc); row++;
gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Montant :"), gbc);
gbc.gridx = 1; content.add(_montantTxt, gbc); row++;
gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Type :"), gbc);
gbc.gridx = 1; content.add(_typeCombo, gbc); row++;
gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Source :"), gbc);
gbc.gridx = 1; content.add(_srcCombo, gbc); row++;
gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Cible :"), gbc);
gbc.gridx = 1; content.add(_cibleCombo, gbc);

initValues(typeOpe);

DocumentListener dl = new DocumentListener() {
public void insertUpdate(DocumentEvent e) { checkInput(); }
public void removeUpdate(DocumentEvent e) { checkInput(); }
public void changedUpdate(DocumentEvent e) { checkInput(); }
};
_libTxt.getDocument().addDocumentListener(dl);
_montantTxt.getDocument().addDocumentListener(dl);
_typeCombo.addActionListener(e -> checkInput());

JButton cancelBtn = new JButton("Annuler");
_okBtn.addActionListener(e -> onOk());
cancelBtn.addActionListener(e -> dispose());

JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
btnPanel.add(_okBtn);
btnPanel.add(cancelBtn);

setLayout(new BorderLayout());
add(content, BorderLayout.CENTER);
add(btnPanel, BorderLayout.SOUTH);

checkInput();
pack();
}

private void initValues(String typeOpe) {
if (_operation != null) {
_libTxt.setText(_operation.getLibelle());
_montantTxt.setText(String.valueOf(_operation.getMontant()));
_typeCombo.setSelectedItem(_operation.getType().toString());
_typeCombo.setEnabled(false);
_srcCombo.setSelectedItem(_operation.getCompteSource());
if (_operation instanceof AppTransfert) {
_cibleCombo.setSelectedItem(((AppTransfert) _operation).getCompteCible());
}
} else {
_libTxt.setText("");
_montantTxt.setText("0");
if (typeOpe != null) _typeCombo.setSelectedItem(typeOpe);
if (_srcCombo.getItemCount() > 0) _srcCombo.setSelectedIndex(0);
if (_cibleCombo.getItemCount() > 0) _cibleCombo.setSelectedIndex(0);
}
}

private void onOk() {
try {
String type = (String) _typeCombo.getSelectedItem();
AppCompte src = (AppCompte) _srcCombo.getSelectedItem();
AppCompte cible = (AppCompte) _cibleCombo.getSelectedItem();
double montant = Double.parseDouble(_montantTxt.getText().trim());

if (_operation == null) {
_operation = TrimestreManager.getInstance().addOperation(
_libTxt.getText(), montant, type, src, cible, _numMois);
} else {
OperationService.editOperation(_operation, _libTxt.getText(), montant, src, cible);
}
_confirmed = true;
dispose();
} catch (Exception e) {
ExceptionDisplayService.showException(e);
}
}

private void checkInput() {
boolean libOk = !_libTxt.getText().trim().isEmpty();
_libTxt.setBorder(libOk ? UIManager.getBorder("TextField.border") : BorderFactory.createLineBorder(Color.RED));

boolean montOk = false;
try { Double.parseDouble(_montantTxt.getText().trim()); montOk = true; } catch (NumberFormatException e) {}
_montantTxt.setBorder(montOk ? UIManager.getBorder("TextField.border") : BorderFactory.createLineBorder(Color.RED));

String type = (String) _typeCombo.getSelectedItem();
boolean isTrans = TrimestreManager.getInstance().isTransfertType(type);
_cibleCombo.setEnabled(isTrans);

_okBtn.setEnabled(libOk && montOk);
}

}
