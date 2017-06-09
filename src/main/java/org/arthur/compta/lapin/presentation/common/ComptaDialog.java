package org.arthur.compta.lapin.presentation.common;

import org.arthur.compta.lapin.application.manager.ConfigurationManager;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Dialog;

/**
 * Fenetre qui enregistre sa taille
 *
 */
public class ComptaDialog<T> extends Dialog<T> {

	public ComptaDialog(String id) {

		setResizable(true);

		// valeur initial
		getDialogPane().setPrefSize(
				Double.parseDouble(
						ConfigurationManager.getInstance().getProp(id + ".size.width", "200")),
				Double.parseDouble(ConfigurationManager.getInstance()
						.getProp(id + ".size.heigth", "200")));

		// changement de largeur
		widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				ConfigurationManager.getInstance().setProp(id + ".size.width",
						String.valueOf(getWidth()));

			}
		});

		// changement de hauteur
		heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				ConfigurationManager.getInstance().setProp(id + ".size.heigth",
						String.valueOf(getHeight()));

			}
		});

	}

}
