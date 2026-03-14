package org.arthur.compta.lapin.presentation.trimestre.dialog;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppTrimestre;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 * Dialogue de création d'un trimestre
 */
public class CreateTrimestreDialog extends ComptaDialog {

private final JSpinner _dateSpinner;
private final JButton _okBtn = new JButton("Ok");
private final JButton _okLoadBtn = new JButton("Ok et charger");

public CreateTrimestreDialog() {
super(CreateTrimestreDialog.class.getSimpleName(), "Création d'un trimestre");

JPanel content = new JPanel(new GridBagLayout());
GridBagConstraints gbc = new GridBagConstraints();
gbc.insets = new Insets(5, 5, 5, 5);

// date de début
Calendar cal = Calendar.getInstance();
javax.swing.SpinnerDateModel model = new javax.swing.SpinnerDateModel(cal.getTime(), null, null, Calendar.DAY_OF_MONTH);
_dateSpinner = new JSpinner(model);
_dateSpinner.setEditor(new JSpinner.DateEditor(_dateSpinner, "dd/MM/yyyy"));

gbc.gridx = 0; gbc.gridy = 0; content.add(new JLabel("Date de début : "), gbc);
gbc.gridx = 1; content.add(_dateSpinner, gbc);

// boutons
JButton cancelBtn = new JButton("Annuler");
_okBtn.addActionListener(e -> createTrimestre(false));
_okLoadBtn.addActionListener(e -> createTrimestre(true));
cancelBtn.addActionListener(e -> dispose());

JPanel btnPanel = new JPanel(new FlowLayout());
btnPanel.add(_okBtn);
btnPanel.add(_okLoadBtn);
btnPanel.add(cancelBtn);

setLayout(new BorderLayout());
add(content, BorderLayout.CENTER);
add(btnPanel, BorderLayout.SOUTH);
pack();
}

private void createTrimestre(boolean loadAfter) {
Date d = (Date) _dateSpinner.getValue();
Calendar c = Calendar.getInstance();
c.setTime(d);
LocalDate dateDeb = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
try {
AppTrimestre newTrim = TrimestreManager.getInstance().createTrimestre(dateDeb);
if (loadAfter) {
TrimestreManager.getInstance().loadTrimestreCourant(newTrim.getAppId());
}
_confirmed = true;
dispose();
} catch (ComptaException ex) {
ExceptionDisplayService.showException(ex);
}
}

}
