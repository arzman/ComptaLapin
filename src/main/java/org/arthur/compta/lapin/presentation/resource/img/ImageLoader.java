package org.arthur.compta.lapin.presentation.resource.img;

import javafx.scene.image.Image;

/**
 * S'occupe du chargement des images dans l'application
 *
 */
public class ImageLoader {

	// un plus vert
	public static final String ADD_IMG = "add_icon.gif";
	// une croix rouge
	public static final String DEL_IMG = "remove_icon.gif";
	// un crayon
	public static final String EDIT_IMG = "edit_icon.jpg";
	// selection du trimestre
	public static final String SELECT_TRIM_IMG = "select_icon.gif";
	// configuration du template
	public static final String CONFIG_TMP_IMG = "config_icon.png";
	// V vert
	public static final String VALID_IMG = "valid_icon.png";
	// pref
	public static final String PREF_IMG = "pref.png";
	// calendrier
	public static final String CALENDRIER_IMG = "calendrier.png";
	// loupe
	public static final String LOUPE_IMG = "search.png";
	// bouton off
	public static final String OFF_IMG = "power_off.png";
	// fleche haut
	public static final String UP_IMG = "up_icon.png";
	// fleche bas
	public static final String DOWN_IMG = "down_icon.png";
	// buget
	public static final String BUDGET_IMG = "budget_icon.png";
	// compte
	public static final String COMPTE_IMG = "compte_icon.png";
	// compte
	public static final String TRIMESTRE_IMG = "trimestre_icon.png";
	// operation
	public static final String OPERATION_IMG = "operation.gif";
	// system
	public static final String SYSTEM_IMG = "system.png";
	// validation
	public static final String VERIF_IMG = "accept.png";
	// lapin
	public static final String LAPIN_IMG = "lapin.png";
	// lapin en 32
	public static final String LAPIN32_IMG = "lapin_32x32.png";

	/**
	 * Retourne une image
	 * 
	 * @param nom
	 *            le nom de l'image a retourner
	 * @return l'image
	 */
	public static Image getImage(String nom) {

		return new Image("org/arthur/compta/lapin/presentation/resource/img/" + nom);

	}

}
