package org.arthur.compta.lapin.presentation.operation.table;

import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Rendu de la colonne état dans les tableaux d'opérations
 */
public class EtatCellRenderer extends DefaultTableCellRenderer {

private static final ImageIcon VALID_ICON = ImageLoader.getImageIcon(ImageLoader.VALID_IMG);

@Override
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
boolean hasFocus, int row, int column) {
JLabel label = (JLabel) super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
label.setHorizontalAlignment(SwingConstants.CENTER);
label.setIcon(null);
if (value instanceof EtatOperation && value.equals(EtatOperation.PRISE_EN_COMPTE)) {
label.setIcon(VALID_ICON);
}
return label;
}

}
