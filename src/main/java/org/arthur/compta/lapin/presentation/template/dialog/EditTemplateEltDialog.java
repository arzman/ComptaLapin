package org.arthur.compta.lapin.presentation.template.dialog;

import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElement;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElementFrequence;
import org.arthur.compta.lapin.application.service.OperationService;
import org.arthur.compta.lapin.application.service.TemplateService;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

/**
 * Fenêtre de saisie d'un élément de template de trimestre
 */
public class EditTemplateEltDialog extends ComptaDialog {

private final TrimestreTemplateElement _templateElt;
private final JTextField _nomTxt = new JTextField(20);
private final JTextField _montantTxt = new JTextField(20);
private final JComboBox<String> _typeCombo;
private final JComboBox<String> _freqCombo;
private final JComboBox<Integer> _occComb;
private final JComboBox<AppCompte> _srcCombo;
private final JComboBox<AppCompte> _cibleCombo;
private final JButton _okBtn = new JButton("Ok");
private TrimestreTemplateElement _result = null;

public EditTemplateEltDialog(TrimestreTemplateElement elt) {
super(EditTemplateEltDialog.class.getSimpleName(),
elt == null ? "Création d'un élément" : "Edition d'un élément");
_templateElt = elt;

// type combo
List<String> types = OperationService.getOperationType();
_typeCombo = new JComboBox<>(types.toArray(new String[0]));

// frequence combo
List<String> freqs = TemplateService.getTemplateEltFreq();
_freqCombo = new JComboBox<>(freqs.toArray(new String[0]));

// occurence combo
_occComb = new JComboBox<>();

// compte combos
List<AppCompte> comptes = CompteManager.getInstance().getCompteList();
ListCellRenderer<Object> cptRenderer = new DefaultListCellRenderer() {
@Override
public Component getListCellRendererComponent(JList<?> l, Object v, int idx, boolean sel, boolean focus) {
JLabel lbl = (JLabel) super.getListCellRendererComponent(l, v, idx, sel, focus);
lbl.setText(v instanceof AppCompte ? ((AppCompte) v).getNom() : "");
return lbl;
}
};
_srcCombo = new JComboBox<>(comptes.toArray(new AppCompte[0]));
_srcCombo.setRenderer(cptRenderer);
_cibleCombo = new JComboBox<>(comptes.toArray(new AppCompte[0]));
_cibleCombo.setRenderer(cptRenderer);

JPanel content = new JPanel(new GridBagLayout());
GridBagConstraints gbc = new GridBagConstraints();
gbc.insets = new Insets(4, 4, 4, 4);
gbc.anchor = GridBagConstraints.WEST;

int row = 0;
gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Nom :"), gbc);
gbc.gridx = 1; content.add(_nomTxt, gbc); row++;
gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Montant :"), gbc);
gbc.gridx = 1; content.add(_montantTxt, gbc); row++;
gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Type :"), gbc);
gbc.gridx = 1; content.add(_typeCombo, gbc); row++;
gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Fréquence :"), gbc);
gbc.gridx = 1; content.add(_freqCombo, gbc); row++;
gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Occurence :"), gbc);
gbc.gridx = 1; content.add(_occComb, gbc); row++;
gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Source :"), gbc);
gbc.gridx = 1; content.add(_srcCombo, gbc); row++;
gbc.gridx = 0; gbc.gridy = row; content.add(new JLabel("Cible :"), gbc);
gbc.gridx = 1; content.add(_cibleCombo, gbc);

initValues();

DocumentListener dl = new DocumentListener() {
public void insertUpdate(DocumentEvent e) { checkInput(false); }
public void removeUpdate(DocumentEvent e) { checkInput(false); }
public void changedUpdate(DocumentEvent e) { checkInput(false); }
};
_nomTxt.getDocument().addDocumentListener(dl);
_montantTxt.getDocument().addDocumentListener(dl);
_typeCombo.addActionListener(e -> checkInput(false));
_freqCombo.addActionListener(e -> checkInput(true));
_srcCombo.addActionListener(e -> checkInput(false));
_cibleCombo.addActionListener(e -> checkInput(false));

JButton cancelBtn = new JButton("Annuler");
_okBtn.addActionListener(e -> onOk());
cancelBtn.addActionListener(e -> dispose());

JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
btnPanel.add(_okBtn);
btnPanel.add(cancelBtn);

setLayout(new BorderLayout());
add(content, BorderLayout.CENTER);
add(btnPanel, BorderLayout.SOUTH);

checkInput(_templateElt == null);
pack();
}

private void initValues() {
if (_templateElt != null) {
_nomTxt.setText(_templateElt.getNom());
_montantTxt.setText(String.valueOf(_templateElt.getMontant()));
_typeCombo.setSelectedItem(_templateElt.getType());
_freqCombo.setSelectedItem(_templateElt.getFreq().toString());
updateOccComb();
_occComb.setSelectedItem(_templateElt.getOccurence());
_srcCombo.setSelectedItem(_templateElt.getCompteSource());
_cibleCombo.setSelectedItem(_templateElt.getCompteCible());
} else {
_nomTxt.setText("");
_montantTxt.setText("0");
if (_typeCombo.getItemCount() > 0) _typeCombo.setSelectedIndex(0);
if (_freqCombo.getItemCount() > 0) _freqCombo.setSelectedIndex(0);
updateOccComb();
if (_srcCombo.getItemCount() > 0) _srcCombo.setSelectedIndex(0);
if (_cibleCombo.getItemCount() > 0) _cibleCombo.setSelectedIndex(0);
}
}

private void updateOccComb() {
String freq = (String) _freqCombo.getSelectedItem();
_occComb.removeAllItems();
if (freq != null) {
for (Integer i : TemplateService.getOccurenceForFreq(freq)) {
_occComb.addItem(i);
}
}
}

private void onOk() {
TrimestreTemplateElement elt = _templateElt;
if (elt == null) {
elt = new TrimestreTemplateElement();
}
elt.setNom(_nomTxt.getText());
elt.setMontant(Double.parseDouble(_montantTxt.getText()));
elt.setType((String) _typeCombo.getSelectedItem());
elt.setFreq(TrimestreTemplateElementFrequence.valueOf((String) _freqCombo.getSelectedItem()));
if (!_occComb.isEnabled() || _occComb.getSelectedItem() == null) {
elt.setOccurence(0);
} else {
elt.setOccurence((Integer) _occComb.getSelectedItem());
}
elt.setCompteSource((AppCompte) _srcCombo.getSelectedItem());
if (!_cibleCombo.isEnabled()) {
elt.setCompteCible(null);
} else {
elt.setCompteCible((AppCompte) _cibleCombo.getSelectedItem());
}
_result = elt;
_confirmed = true;
dispose();
}

private void checkInput(boolean changeOcc) {
boolean nomOk = !_nomTxt.getText().trim().isEmpty();
_nomTxt.setBorder(nomOk ? UIManager.getBorder("TextField.border") : BorderFactory.createLineBorder(Color.RED));

boolean soldeOk = false;
try { Double.parseDouble(_montantTxt.getText().trim()); soldeOk = true; } catch (NumberFormatException e) {}
_montantTxt.setBorder(soldeOk ? UIManager.getBorder("TextField.border") : BorderFactory.createLineBorder(Color.RED));

String type = (String) _typeCombo.getSelectedItem();
boolean isTrans = TrimestreManager.getInstance().isTransfertType(type);
_cibleCombo.setEnabled(isTrans);

String freq = (String) _freqCombo.getSelectedItem();
if (freq != null) {
boolean isMensuel = freq.equals(TrimestreTemplateElementFrequence.MENSUEL.toString());
_occComb.setEnabled(!isMensuel);
if (changeOcc) updateOccComb();
}

_okBtn.setEnabled(nomOk && soldeOk);
}

/** Retourne l'élément édité ou créé */
public TrimestreTemplateElement getResult() {
return _result;
}

}
