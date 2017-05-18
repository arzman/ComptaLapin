package org.arthur.compta.lapin.presentation.resource.img;

import javafx.scene.image.Image;

public class ImageLoader {

	// un plus
	public static final String ADD_IMG = "add_icon.gif";
	// une croix rouge
	public static final String DEL_IMG = "remove_icon.gif";
	// un crayon
	public static final String EDIT_IMG = "edit_icon.jpg";
	
	
	public static Image getImage(String nom) {

		return new Image("org/arthur/compta/lapin/presentation/resource/img/" + nom);

	}

}
