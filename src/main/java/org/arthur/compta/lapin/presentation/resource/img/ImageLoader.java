package org.arthur.compta.lapin.presentation.resource.img;

import javafx.scene.image.Image;

public class ImageLoader {

	public static Image getImage(String nom) {

		return new Image("org/arthur/compta/lapin/presentation/resource/img/" + nom);

	}

}
