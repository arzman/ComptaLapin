package org.arthur.compta.lapin.presentation.compte.dialog;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;

import javax.swing.*;
import java.awt.*;

/**
 * Fenêtre d'édition d'un compte
 */
public class EditCompteDialog extends ComptaDialog {

private final JTextField _nomTxt = new JTextField(20);
private final JTextField _soldeTxt = new JTextField(20);
private final JCheckBox _livretCheck = new JCheckBox();
private final JCheckBox _budgetCheck = new JCheckBox();
private final JButton _okBtn = new JButton("Ok");
private final AppCompte _appCompte;

public EditCompteDialog(AppCompte appCompte) {
super(EditCompteDialog.class.getSimpleName(),
appCompte == null ? "Création d'un compte" : "Edition d'un compte");
_appCompte = appCompte;

JPanel content = new JPanel(new GridBagLayout());
GridBagConstraints gbc = new GridBagConstraints();
gbc.insets = new Insets(4, 4, 4, 4);
gbc.anchor = GridBagConstraints.WEST;

int row = 0;
gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Nom : "), gbc);
gbc.gridx = 1; content.add(_nomTxt, gbc);
row++;

gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Solde : "), gbc);
gbc.gridx = 1; content.add(_soldeTxt, gbc);
row++;

gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Est un livret : "), gbc);
gbc.gridx = 1; content.add(_livretCheck, gbc);
row++;

gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Budget : "), gbc);
gbc.gridx = 1; content.add(_budgetCheck, gbc);

// pré-remplissage
if (_appCompte != null) {
_nomTxt.setText(_appCompte.getNom());
_soldeTxt.setText(String.valueOf(_appCompte.getSolde()));
_livretCheck.setSelected(_appCompte.isLivret());
_budgetCheck.setSelected(_appCompte.isBudget());
}

// listeners de validation
_nomTxt.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
public void insertUpdate(javax.swing.event.DocumentEvent e) { checkInput(); }
public void removeUpdate(javax.swing.event.DocumentEvent e) { checkInput(); }
public void changedUpdate(javax.swing.event.DocumentEvent e) { checkInput(); }
});
_soldeTxt.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
public void insertUpdate(javax.swing.event.DocumentEvent e) { checkInput(); }
public void removeUpdate(javax.swing.event.DocumentEvent e) { checkInput(); }
public void changedUpdate(javax.swing.event.DocumentEvent e) { checkInput(); }
});

// boutons
JButton cancelBtn = new JButton("Annuler");
_okBtn.addActionListener(e -> {
try {
if (_appCompte != null) {
CompteManager.getInstance().editCompte(_appCompte, _nomTxt.getText().trim(),
Double.parseDouble(_soldeTxt.getText().trim()), _livretCheck.isSelected(), _budgetCheck.isSelected());
} else {
CompteManager.getInstance().addCompte(_nomTxt.getText().trim(),
Double.parseDouble(_soldeTxt.getText().trim()), _livretCheck.isSelected(), _budgetCheck.isSelected());
}
_confirmed = true;
dispose();
} catch (ComptaException ex) {
ExceptionDisplayService.showException(ex);
}
});
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

private void checkInput() {
boolean nomOk = !_nomTxt.getText().trim().isEmpty();
_nomTxt.setBorder(nomOk ? UIManager.getBorder("TextField.border") : BorderFactory.createLineBorder(Color.RED));

boolean soldeOk = false;
try {
Double.parseDouble(_soldeTxt.getText().trim());
soldeOk = true;
_soldeTxt.setBorder(UIManager.getBorder("TextField.border"));
} catch (NumberFormatException e) {
_soldeTxt.setBorder(BorderFactory.createLineBorder(Color.RED));
}

_okBtn.setEnabled(nomOk && soldeOk);
}

}
