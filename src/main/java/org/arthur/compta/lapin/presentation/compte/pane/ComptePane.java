package org.arthur.compta.lapin.presentation.compte.pane;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.presentation.compte.dialog.EditCompteDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Panneau d'affichage des comptes
 */
public class ComptePane extends JPanel {

    /** Le tableau des comptes */
    private final JTable _table;
    private final CompteTableModel _model;

    public ComptePane() {
        super(new BorderLayout(2, 2));
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        _model = new CompteTableModel();
        _table = new JTable(_model);
        _table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // rendu coloré pour les soldes
        SoldeCellRenderer soldeRenderer = new SoldeCellRenderer();
        for (int i = 1; i < _model.getColumnCount(); i++) {
            _table.getColumnModel().getColumn(i).setCellRenderer(soldeRenderer);
        }

        add(new JScrollPane(_table), BorderLayout.CENTER);
        createContextMenu();

        // mise à jour automatique lors des changements
        CompteManager.getInstance().addChangeListener(() -> {
            _model.setData(CompteManager.getInstance().getCompteList());
        });
    }

    private void createContextMenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem removeCompte = new JMenuItem("Supprimer",
                new ImageIcon(ImageLoader.getImageIcon(ImageLoader.DEL_IMG).getImage()));
        removeCompte.addActionListener(e -> {
            int row = _table.getSelectedRow();
            if (row >= 0) {
                AppCompte appC = _model.getRow(row);
                try {
                    CompteManager.getInstance().removeCompte(appC);
                } catch (ComptaException ex) {
                    ExceptionDisplayService.showException(ex);
                }
            }
        });
        menu.add(removeCompte);

        JMenuItem editCompte = new JMenuItem("Editer",
                new ImageIcon(ImageLoader.getImageIcon(ImageLoader.EDIT_IMG).getImage()));
        editCompte.addActionListener(e -> {
            int row = _table.getSelectedRow();
            if (row >= 0) {
                AppCompte appC = _model.getRow(row);
                EditCompteDialog ecd = new EditCompteDialog(appC);
                ecd.setVisible(true);
            }
        });
        menu.add(editCompte);

        _table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = _table.rowAtPoint(e.getPoint());
                    if (row >= 0)
                        _table.setRowSelectionInterval(row, row);
                    boolean sel = _table.getSelectedRow() >= 0;
                    removeCompte.setEnabled(sel);
                    editCompte.setEnabled(sel);
                    menu.show(_table, e.getX(), e.getY());
                }
            }
        });
    }

    /** Modèle de tableau pour les comptes */
    public static class CompteTableModel extends AbstractTableModel {
        private static final String[] COLS = {"Nom", "Solde", "1er Mois", "2ème Mois", "3ème Mois"};
        private List<AppCompte> _data;

        public CompteTableModel() {
            _data = CompteManager.getInstance().getCompteList();
        }

        public void setData(List<AppCompte> data) {
            _data = data;
            fireTableDataChanged();
        }

        public AppCompte getRow(int row) {
            return (row >= 0 && row < _data.size()) ? _data.get(row) : null;
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
            AppCompte c = _data.get(row);
            switch (col) {
                case 0 :
                    return c.getNom();
                case 1 :
                    return c.getSolde();
                case 2 :
                    return c.getSoldePrev1();
                case 3 :
                    return c.getSoldePrev2();
                case 4 :
                    return c.getSoldePrev3();
                default :
                    return null;
            }
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return col == 0 ? String.class : Double.class;
        }
    }

    /** Rendu coloré : rouge si négatif, bleu si positif */
    private static class SoldeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.RIGHT);
            if (value instanceof Double) {
                double v = (Double) value;
                lbl.setText(ApplicationFormatter.montantFormat.format(v));
                if (!isSelected) {
                    lbl.setForeground(v < 0 ? Color.RED : Color.BLUE);
                }
            }
            return lbl;
        }
    }

}
