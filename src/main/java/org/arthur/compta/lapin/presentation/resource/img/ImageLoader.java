package org.arthur.compta.lapin.presentation.resource.img;

import javax.swing.*;
import java.net.URL;

/**
 * S'occupe du chargement des images dans l'application
 */
public class ImageLoader {

public static final String ADD_IMG = "add_icon.gif";
public static final String DEL_IMG = "remove_icon.gif";
public static final String EDIT_IMG = "edit_icon.jpg";
public static final String SELECT_TRIM_IMG = "select_icon.gif";
public static final String CONFIG_TMP_IMG = "config_icon.png";
public static final String VALID_IMG = "valid_icon.png";
public static final String PREF_IMG = "pref.png";
public static final String CALENDRIER_IMG = "calendrier.png";
public static final String LOUPE_IMG = "search.png";
public static final String OFF_IMG = "power_off.png";
public static final String UP_IMG = "up_icon.png";
public static final String DOWN_IMG = "down_icon.png";
public static final String BUDGET_IMG = "budget_icon.png";
public static final String COMPTE_IMG = "compte_icon.png";
public static final String TRIMESTRE_IMG = "trimestre_icon.png";
public static final String OPERATION_IMG = "operation.gif";
public static final String SYSTEM_IMG = "system.png";
public static final String VERIF_IMG = "accept.png";
public static final String LAPIN_IMG = "lapin.png";
public static final String LAPIN32_IMG = "lapin_32x32.png";
public static final String USE_BUDGET_IMG = "use_budget_icon.gif";
public static final String HISTORY_IMG = "historique.png";
public static final String CHART_IMG = "synth_icon.png";
public static final String BOOK_IMG = "book.png";
public static final String TRANSFERT_IMG = "transfert_icon.png";
public static final String CYCLE_IMG = "cycle.png";

/**
 * Retourne une ImageIcon
 *
 * @param nom le nom de l'image
 * @return l'ImageIcon, ou null
 */
public static ImageIcon getImageIcon(String nom) {
URL url = ImageLoader.class.getClassLoader().getResource(
"org/arthur/compta/lapin/presentation/resource/img/" + nom);
if (url != null) {
return new ImageIcon(url);
}
return new ImageIcon();
}

}
