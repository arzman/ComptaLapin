package org.arthur.compta.lapin.presentation.template.dialog;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplate;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElement;
import org.arthur.compta.lapin.application.service.TemplateService;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Fenêtre de configuration du template de trimestre
 */
public class ConfigureTemplateDialog extends ComptaDialog {

    private final TemplateTableModel _model;
    private final JTable _table;
    private final JLabel _gainLabel;

    public ConfigureTemplateDialog() {
        super(ConfigureTemplateDialog.class.getSimpleName(),
                "Configuration du modèle de trimestre");

        _model = new TemplateTableModel();
        _table = new JTable(_model);
        _table.setFillsViewportHeight(true);
        _table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        _table.setShowGrid(true);
        _table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // chargement du template
        try {
            TrimestreTemplate tmp = TemplateService.getTrimestreTemplate();
            _model.setData(tmp.getElements());
        } catch (ComptaException e) {
            ExceptionDisplayService.showException(e);
        }

        _gainLabel = new JLabel("Gain moyen : " + _model.computeGain());

        // menu contextuel
        JPopupMenu menu = new JPopupMenu();
        _table.setComponentPopupMenu(menu);

        JMenuItem addItem = new JMenuItem("Ajouter",
                new ImageIcon(ImageLoader.getImageIcon(ImageLoader.ADD_IMG).getImage()));
        addItem.addActionListener(e -> {
            EditTemplateEltDialog dia = new EditTemplateEltDialog(null);
            dia.setVisible(true);
            TrimestreTemplateElement res = dia.getResult();
            if (res != null) {
                _model.addElement(res);
                _gainLabel.setText("Gain moyen : " + _model.computeGain());
            }
        });
        menu.add(addItem);

        JMenuItem editItem = new JMenuItem("Editer",
                new ImageIcon(ImageLoader.getImageIcon(ImageLoader.EDIT_IMG).getImage()));
        editItem.addActionListener(e -> {
            int row = _table.getSelectedRow();
            if (row >= 0) {
                TrimestreTemplateElement elt = _model.getRow(row);
                EditTemplateEltDialog dia = new EditTemplateEltDialog(elt);
                dia.setVisible(true);
                _model.fireTableRowsUpdated(row, row);
                _gainLabel.setText("Gain moyen : " + _model.computeGain());
            }
        });
        menu.add(editItem);

        JMenuItem delItem = new JMenuItem("Supprimer",
                new ImageIcon(ImageLoader.getImageIcon(ImageLoader.DEL_IMG).getImage()));
        delItem.addActionListener(e -> {
            int row = _table.getSelectedRow();
            if (row >= 0) {
                _model.removeElement(row);
                _gainLabel.setText("Gain moyen : " + _model.computeGain());
            }
        });
        menu.add(delItem);



        JButton okBtn = new JButton("Ok");
        okBtn.addActionListener(e -> {
            try {
                TemplateService.updateTrimestreTemplate(_model._data);
                _confirmed = true;
                dispose();
            } catch (ComptaException ex) {
                ExceptionDisplayService.showException(ex);
            }
        });
        JButton cancelBtn = new JButton("Annuler");
        cancelBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(_gainLabel, BorderLayout.WEST);
        bottomPanel.add(btnPanel, BorderLayout.EAST);

        setLayout(new BorderLayout(5, 5));
        add(new JScrollPane(_table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        setSize(600, 400);
    }

    /** Modèle du tableau de template */
    private static class TemplateTableModel extends AbstractTableModel {
        private static final String[] COLS =
                {"Nom", "Montant", "Type", "Fréquence", "Occurence", "Source", "Cible"};
        final List<TrimestreTemplateElement> _data = new ArrayList<>();

        public void setData(List<TrimestreTemplateElement> data) {
            _data.clear();
            _data.addAll(data);
            fireTableDataChanged();
        }

        public void addElement(TrimestreTemplateElement elt) {
            _data.add(elt);
            fireTableDataChanged();
        }

        public void removeElement(int row) {
            _data.remove(row);
            fireTableDataChanged();
        }

        public TrimestreTemplateElement getRow(int row) {
            return (row >= 0 && row < _data.size()) ? _data.get(row) : null;
        }

        public String computeGain() {
            return String.valueOf(Math.round(TemplateService.getGainMoyen(_data) * 100) / 100.0);
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
            TrimestreTemplateElement e = _data.get(row);
            switch (col) {
                case 0:
                    return e.getNom();
                case 1:
                    return e.getMontant();
                case 2:
                    return e.getType();
                case 3:
                    return e.getFreq().toString();
                case 4:
                    return e.getOccurence();
                case 5:
                    return e.getCompteSource() != null ? e.getCompteSource().getNom() : "";
                case 6:
                    return e.getCompteCible() != null ? e.getCompteCible().getNom() : "";
                default:
                    return null;
            }
        }
    }

}
