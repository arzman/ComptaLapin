package org.arthur.compta.lapin.presentation.compte.dialog;

import org.arthur.compta.lapin.model.Compte;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Callback;

/**
 * Fenêtre d'édition d'un 
 * @author ARDUFLOT
 *
 */
public class EditCompteDialog extends Dialog<Compte> {

	public EditCompteDialog() {

		setTitle("Création d'un compte");

		createBoutonBar();

	}

	private void createBoutonBar() {

		// Création du bouton OK
		ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().add(buttonTypeOk);

		// Création du bouton Cancel
		ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(buttonTypeCancel);

		// Retourne le Compte créé sur le OK
		setResultConverter(new Callback<ButtonType, Compte>() {

			@Override
			public Compte call(ButtonType param) {

				Compte zeReturn;

				if (param.getButtonData().equals(ButtonData.OK_DONE)) {
					zeReturn = new Compte("Le nom");
				} else {
					zeReturn = null;
				}

				return zeReturn;
			}
		});

	}

}
