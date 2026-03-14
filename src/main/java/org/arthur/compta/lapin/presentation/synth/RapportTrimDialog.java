package org.arthur.compta.lapin.presentation.synth;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.service.SyntheseService;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;

/**
 * Dialogue de rapport trimestriel
 */
public class RapportTrimDialog extends ComptaDialog {

    private final DefaultListModel<String> _listModel;
    private final JList<String> _listV;
    private final HashMap<String, LocalDate> _resumeTrimestre;

    public RapportTrimDialog() {
        super(RapportTrimDialog.class.getSimpleName(), "Rapport Trimestriel");

        _resumeTrimestre = new HashMap<>();
        _listModel = new DefaultListModel<>();
        _listV = new JList<>(_listModel);
        _listV.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _listV.setCellRenderer(new TrimestreListCellRenderer());

        try {
            HashMap<String, LocalDate> map = TrimestreManager.getInstance().getAllTrimestreShortList();
            _resumeTrimestre.putAll(map);
            for (String id : map.keySet())
                _listModel.addElement(id);
        } catch (ComptaException e) {
            ExceptionDisplayService.showException(e);
        }

        JButton exportBtn = new JButton("Exporter");
        exportBtn.addActionListener(e -> {
            String id = _listV.getSelectedValue();
            if (id != null) {
                JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new FileNameExtensionFilter("PDF files (*.pdf)", "pdf"));
                if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    if (!file.getName().endsWith(".pdf")) {
                        file = new File(file.getAbsolutePath() + ".pdf");
                    }
                    try {
                        SyntheseService.writeRapportForTrim(Integer.parseInt(id), file);
                    } catch (ComptaException ex) {
                        ExceptionDisplayService.showException(ex);
                    }
                }
            }
        });

        JButton closeBtn = new JButton("Fermer");
        closeBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(exportBtn);
        btnPanel.add(closeBtn);

        setLayout(new BorderLayout());
        add(new JScrollPane(_listV), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        setSize(400, 300);
    }

    private class TrimestreListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String id = (String) value;
            if (id != null && !id.isEmpty() && _resumeTrimestre.containsKey(id)) {
                LocalDate deb = _resumeTrimestre.get(id);
                lbl.setText("De " + ApplicationFormatter.moiAnneedateFormat.format(deb) + " à "
                        + ApplicationFormatter.moiAnneedateFormat.format(deb.plusMonths(2)));
            }
            return lbl;
        }
    }

}
