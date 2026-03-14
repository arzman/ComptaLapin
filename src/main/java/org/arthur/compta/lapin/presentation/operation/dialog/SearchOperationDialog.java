package org.arthur.compta.lapin.presentation.operation.dialog;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.OperationSearchResult;
import org.arthur.compta.lapin.application.service.OperationService;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Fenêtre de recherche d'opération
 */
public class SearchOperationDialog extends ComptaDialog {

    private final JTextField _libTxt = new JTextField(15);
    private final JTextField _montantTxt = new JTextField(15);
    private final JTextField _toleranceTxt = new JTextField(15);
    private final SearchResultModel _model;

    public SearchOperationDialog() {
        super(SearchOperationDialog.class.getSimpleName(), "Recherche une opération");

        _model = new SearchResultModel();
        JTable table = new JTable(_model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel criteriaPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        criteriaPanel.add(new JLabel("Libellé contient : "), gbc);
        gbc.gridx = 1;
        criteriaPanel.add(_libTxt, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        criteriaPanel.add(new JLabel("Montant égale : "), gbc);
        gbc.gridx = 1;
        criteriaPanel.add(_montantTxt, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        criteriaPanel.add(new JLabel(" + / - : "), gbc);
        gbc.gridx = 1;
        criteriaPanel.add(_toleranceTxt, gbc);
        row++;

        JButton searchBtn = new JButton("Rechercher");
        searchBtn.addActionListener(e -> doSearch());
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        criteriaPanel.add(searchBtn, gbc);

        JButton closeBtn = new JButton("Fermer");
        closeBtn.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(closeBtn);

        setLayout(new BorderLayout(5, 5));
        add(criteriaPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        setSize(600, 400);
    }

    private void doSearch() {
        boolean allEmpty = _libTxt.getText().trim().isEmpty() && _montantTxt.getText().trim().isEmpty()
                && _toleranceTxt.getText().trim().isEmpty();

        if (allEmpty) {
            int res = JOptionPane.showConfirmDialog(this,
                    "Attention, les champs de recherche sont vides, l'intégralité des opérations sera remontée.\nVoulez-vous continuer ?",
                    "Confirmer la recherche", JOptionPane.YES_NO_OPTION);
            if (res != JOptionPane.YES_OPTION)
                return;
        }

        try {
            List<OperationSearchResult> results = OperationService.doSearch(_libTxt.getText(), _montantTxt.getText(),
                    _toleranceTxt.getText());
            _model.setData(results);
        } catch (ComptaException e) {
            ExceptionDisplayService.showException(e);
        }
    }

    private static class SearchResultModel extends AbstractTableModel {
        private static final String[] COLS = {"Libellé", "Montant", "Mois"};
        private List<OperationSearchResult> _data = new ArrayList<>();

        public void setData(List<OperationSearchResult> data) {
            _data = data;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return _data.size();
        }
        @Override
        public int getColumnCount() {
            return COLS.length;
        }
        @Override
        public String getColumnName(int col) {
            return COLS[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            OperationSearchResult r = _data.get(row);
            switch (col) {
                case 0 :
                    return r.getLibelle();
                case 1 :
                    return ApplicationFormatter.montantFormat.format(r.getMontant());
                case 2 :
                    return r.getMois() != null ? ApplicationFormatter.moiAnneedateFormat.format(r.getMois()) : "";
                default :
                    return null;
            }
        }
    }

}
