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
import java.util.List;

/**
 * Fenêtre de création ou édition de budget
 */
public class EditBudgetDialog extends ComptaDialog {

    private final AppBudget _budget;
    private final JTextField _nomTxt = new JTextField(20);
    private final JTextField _objTxt = new JTextField(20);
    private final JTextField _utilsTxt = new JTextField(20);
    private final JCheckBox _isRecurrentChk = new JCheckBox("Budget récurrent");
    private final JComboBox<String> _listBudRecuCB = new JComboBox<>();
    private final JSpinner _dateBudgetSpinner;
    private final JButton _okBtn = new JButton("Ok");

    public EditBudgetDialog(AppBudget budget) {
        super(EditBudgetDialog.class.getSimpleName(), budget == null ? "Création d'un budget" : "Edition d'un budget");
        _budget = budget;

        // spinner de date
        Calendar cal = Calendar.getInstance();
        javax.swing.SpinnerDateModel model = new javax.swing.SpinnerDateModel(cal.getTime(), null, null,
                Calendar.DAY_OF_MONTH);
        _dateBudgetSpinner = new JSpinner(model);
        _dateBudgetSpinner.setEditor(new JSpinner.DateEditor(_dateBudgetSpinner, "dd/MM/yyyy"));

        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        content.add(new JLabel("Nom : "), gbc);
        gbc.gridx = 1;
        content.add(_nomTxt, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        content.add(new JLabel("Objectif : "), gbc);
        gbc.gridx = 1;
        content.add(_objTxt, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        content.add(new JLabel("Utilisé : "), gbc);
        gbc.gridx = 1;
        content.add(_utilsTxt, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        content.add(_isRecurrentChk, gbc);
        row++;

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        content.add(new JLabel("Date récurrence : "), gbc);
        gbc.gridx = 1;
        content.add(_dateBudgetSpinner, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        content.add(_listBudRecuCB, gbc);

        // initialisation des valeurs
        initValues();

        // listeners
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
        _objTxt.getDocument().addDocumentListener(dl);
        _utilsTxt.getDocument().addDocumentListener(dl);
        _isRecurrentChk.addActionListener(e -> checkInput());
        _listBudRecuCB.addActionListener(e -> checkInput());
        _dateBudgetSpinner.addChangeListener(e -> checkInput());

        // boutons
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

    private void initValues() {
        try {
            List<String> labels = BudgetManager.getInstance().getLabelRecurrentList();
            for (String lbl : labels) {
                _listBudRecuCB.addItem(lbl);
            }
        } catch (Exception e) {
            ExceptionDisplayService.showException(e);
        }

        if (_budget != null) {
            _nomTxt.setText(_budget.getNom());
            _objTxt.setText(String.valueOf(_budget.getObjectif()));
            _utilsTxt.setText(String.valueOf(_budget.getMontantUtilise()));
            _isRecurrentChk.setSelected(_budget.isRecurrent());
            if (_budget.isRecurrent()) {
                _listBudRecuCB.setSelectedItem(_budget.getLabelRecurrent());
                if (_budget.getDateRecurrent() != null) {
                    Calendar c = Calendar.getInstance();
                    c.set(_budget.getDateRecurrent().getYear(), _budget.getDateRecurrent().getMonthValue() - 1,
                            _budget.getDateRecurrent().getDayOfMonth());
                    _dateBudgetSpinner.setValue(c.getTime());
                }
            }
        }
    }

    private void onOk() {
        try {
            String lblRec = _isRecurrentChk.isSelected() ? (String) _listBudRecuCB.getSelectedItem() : "";
            LocalDate dateRec = LocalDate.of(1986, 6, 27);
            if (_isRecurrentChk.isSelected()) {
                Date d = (Date) _dateBudgetSpinner.getValue();
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                dateRec = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
            }

            if (_budget == null) {
                BudgetManager.getInstance().addBudget(_nomTxt.getText().trim(),
                        Double.parseDouble(_objTxt.getText().trim()), Double.parseDouble(_utilsTxt.getText().trim()),
                        lblRec, dateRec);
            } else {
                BudgetManager.getInstance().editBudget(_budget, _nomTxt.getText().trim(),
                        Double.parseDouble(_objTxt.getText().trim()), Double.parseDouble(_utilsTxt.getText().trim()),
                        _budget.isActif(), _budget.getPriority(), lblRec, dateRec);
            }
            _confirmed = true;
            dispose();
        } catch (Exception e) {
            ExceptionDisplayService.showException(e);
        }
    }

    private void checkInput() {
        boolean nomOk = !_nomTxt.getText().trim().isEmpty();
        _nomTxt.setBorder(nomOk ? UIManager.getBorder("TextField.border") : BorderFactory.createLineBorder(Color.RED));

        boolean objOk = false;
        try {
            Double.parseDouble(_objTxt.getText().trim());
            objOk = true;
        } catch (NumberFormatException e) {
        }
        _objTxt.setBorder(objOk ? UIManager.getBorder("TextField.border") : BorderFactory.createLineBorder(Color.RED));

        boolean utilOk = false;
        try {
            Double.parseDouble(_utilsTxt.getText().trim());
            utilOk = true;
        } catch (NumberFormatException e) {
        }
        _utilsTxt.setBorder(
                utilOk ? UIManager.getBorder("TextField.border") : BorderFactory.createLineBorder(Color.RED));

        boolean recOk = !_isRecurrentChk.isSelected() || (_listBudRecuCB.getSelectedItem() != null);

        boolean isRec = _isRecurrentChk.isSelected();
        _dateBudgetSpinner.setEnabled(isRec);
        _listBudRecuCB.setEnabled(isRec);

        _okBtn.setEnabled(nomOk && objOk && utilOk && recOk);
    }

}
