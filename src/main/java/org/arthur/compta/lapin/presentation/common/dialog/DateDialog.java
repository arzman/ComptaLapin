package org.arthur.compta.lapin.presentation.common.dialog;

import org.arthur.compta.lapin.presentation.common.ComptaDialog;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Date;

/**
 * Fenêtre permettant de choisir une date
 */
public class DateDialog extends ComptaDialog {

    private LocalDate _result;
    private final JSpinner _dateSpinner;

    public DateDialog(LocalDate date) {
        super(DateDialog.class.getSimpleName(), "Choisissez une date");

        LocalDate initial = date != null ? date : LocalDate.now();

        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Spinner de date
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(initial.getYear(), initial.getMonthValue() - 1, initial.getDayOfMonth());
        javax.swing.SpinnerDateModel model = new javax.swing.SpinnerDateModel(cal.getTime(), null, null,
                java.util.Calendar.DAY_OF_MONTH);
        _dateSpinner = new JSpinner(model);
        _dateSpinner.setEditor(new JSpinner.DateEditor(_dateSpinner, "dd/MM/yyyy"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        content.add(new JLabel("Date : "), gbc);
        gbc.gridx = 1;
        content.add(_dateSpinner, gbc);

        // boutons
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton okBtn = new JButton("Ok");
        JButton cancelBtn = new JButton("Annuler");
        okBtn.addActionListener(e -> {
            Date d = (Date) _dateSpinner.getValue();
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTime(d);
            _result = LocalDate.of(c.get(java.util.Calendar.YEAR), c.get(java.util.Calendar.MONTH) + 1,
                    c.get(java.util.Calendar.DAY_OF_MONTH));
            _confirmed = true;
            dispose();
        });
        cancelBtn.addActionListener(e -> dispose());
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);

        setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        pack();
    }

    /** Retourne la date sélectionnée, ou null si annulé */
    public LocalDate getResult() {
        return _result;
    }

}
