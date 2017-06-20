package org.arthur.compta.lapin.presentation.common;

import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Fenetre qui enregistre sa taille
 *
 */
public class ComptaDialog<T> extends Dialog<T> {

	/** La bordure rouge en cas d'erreur de saisi */
	protected final Border BORDER_ERROR = new Border(
			new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1)));

	public ComptaDialog(String id) {

		// on peut redimensionner la fenetre
		setResizable(true);

		// ajout des icones de la fenetre = meme que l'appli
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
				ConfigurationManager.getInstance().setProp(id + ".size.width",
						String.valueOf(getDialogPane().getBoundsInLocal().getWidth()));
			}
		});

		// changement de hauteur
		heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				ConfigurationManager.getInstance().setProp(id + ".size.heigth",
						String.valueOf(getDialogPane().getBoundsInLocal().getHeight()));

			}
		});

	}

}
