package org.arthur.compta.lapin.presentation.budget.dialog;

import org.arthur.compta.lapin.application.manager.BudgetManager;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 * Fenêtre permettant d'utiliser un budget
 */
public class UseBudgetDialog extends ComptaDialog {

    private final AppBudget _budget;
    private final JTextField _nomTxt = new JTextField(20);
    private final JTextField _montantTxt = new JTextField(20);
    private final JSpinner _dateSpinner;
    private final JButton _okBtn = new JButton("Ok");

    public UseBudgetDialog(AppBudget appB) {
        super(UseBudgetDialog.class.getSimpleName(), "Utiliser le budget");
        _budget = appB;

        Calendar cal = Calendar.getInstance();
        javax.swing.SpinnerDateModel model = new javax.swing.SpinnerDateModel(cal.getTime(), null, null,
                Calendar.DAY_OF_MONTH);
        _dateSpinner = new JSpinner(model);
        _dateSpinner.setEditor(new JSpinner.DateEditor(_dateSpinner, "dd/MM/yyyy"));

        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        content.add(new JLabel("Libellé : "), gbc);
        gbc.gridx = 1;
        content.add(_nomTxt, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        content.add(new JLabel("Montant : "), gbc);
        gbc.gridx = 1;
        content.add(_montantTxt, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        content.add(new JLabel("Date : "), gbc);
        gbc.gridx = 1;
        content.add(_dateSpinner, gbc);

        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                checkInput();
            }
            public void removeUpdate(DocumentEvent e) {
                checkInput();
            }
            public void changedUpdate(DocumentEvent e) {
                checkInput();
            }
        };
        _nomTxt.getDocument().addDocumentListener(dl);
        _montantTxt.getDocument().addDocumentListener(dl);

        JButton closeBtn = new JButton("Fermer");
        _okBtn.addActionListener(e -> {
            Date d = (Date) _dateSpinner.getValue();
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            LocalDate date = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                    c.get(Calendar.DAY_OF_MONTH));
            try {
                BudgetManager.getInstance().addUtilisationForBudget(_budget, _nomTxt.getText().trim(),
                        Double.parseDouble(_montantTxt.getText().trim()), date);
                _confirmed = true;
                dispose();
            } catch (Exception ex) {
                ExceptionDisplayService.showException(ex);
            }
        });
        closeBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(_okBtn);
        btnPanel.add(closeBtn);

        setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        checkInput();
        pack();
    }

    private void checkInput() {
        boolean nomOk = !_nomTxt.getText().trim().isEmpty();
        _nomTxt.setBorder(nomOk ? UIManager.getBorder("TextField.border") : BorderFactory.createLineBorder(Color.RED));

        boolean montOk = false;
        try {
            Double.parseDouble(_montantTxt.getText().trim());
            montOk = true;
        } catch (NumberFormatException e) {
        }
        _montantTxt.setBorder(
                montOk ? UIManager.getBorder("TextField.border") : BorderFactory.createLineBorder(Color.RED));

        _okBtn.setEnabled(nomOk && montOk);
    }

}
