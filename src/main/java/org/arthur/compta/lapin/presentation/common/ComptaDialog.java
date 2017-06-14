package org.arthur.compta.lapin.presentation.common;

import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

/**
 * Fenetre qui enregistre sa taille
 *
 */
public class ComptaDialog<T> extends Dialog<T> {

	public ComptaDialog(String id) {

		setResizable(true);
		
		
		Stage stage = (Stage) getDialogPane().getScene().getWindow(); 
		stage.getIcons().add(ImageLoader.getImage(ImageLoader.LAPIN_IMG));
		stage.getIcons().add(ImageLoader.getImage(ImageLoader.LAPIN32_IMG));
		

		// valeur initial
		getDialogPane().setPrefSize(
				Double.parseDouble(ConfigurationManager.getInstance().getProp(id + ".size.width", "200")),
				Double.parseDouble(ConfigurationManager.getInstance().getProp(id + ".size.heigth", "200")));

		// changement de largeur
		widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				ConfigurationManager.getInstance().setProp(id + ".size.width", String.valueOf(getWidth()));

			}
		});

		// changement de hauteur
		heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				ConfigurationManager.getInstance().setProp(id + ".size.heigth", String.valueOf(getHeight()));

			}
		});

	}

}
