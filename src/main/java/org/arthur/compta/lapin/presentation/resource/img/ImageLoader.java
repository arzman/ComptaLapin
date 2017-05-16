package org.arthur.compta.lapin.presentation.resource.img;

import javafx.scene.image.Image;

public class ImageLoader {

	
	public static final String ADD_IMG = "add_icon.gif";
	
	public static final String DEL_IMG = "remove_icon.gif";
	
	
	public static Image getImage(String nom) {

		return new Image("org/arthur/compta/lapin/presentation/resource/img/" + nom);

	}

}
