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
	
	
	/**
	 * Retourne une image
	 * @param nom le nom de l'image a retourner
	 * @return l'image
	 */
	public static Image getImage(String nom) {

		return new Image("org/arthur/compta/lapin/presentation/resource/img/" + nom);

	}

}
