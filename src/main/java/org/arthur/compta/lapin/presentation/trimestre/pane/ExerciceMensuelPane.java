package org.arthur.compta.lapin.presentation.trimestre.pane;

import org.arthur.compta.lapin.application.model.AppExerciceMensuel;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Panneau de présentation d'un exercice mensuel : tableau des dépenses ,
 * tableau des ressources et tableau des transferts.
 *
 */
public class ExerciceMensuelPane extends GridPane {

	/**
	 * Libellé de la date
	 */
	private Label _text;

	public ExerciceMensuelPane() {

		_text = new Label("Pas de mois");
		add(_text, 0, 0);

	}

	/**
	 * Change l'affichage des mois
	 * 
	 * @param oldEMProp
	 *            l'ancien mois dans un propriété observable
	 * @param newEMprop
	 *            le nouveau mois dans un propriété observable
	 */
	public void changeBind(ObjectProperty<AppExerciceMensuel> oldEMProp, ObjectProperty<AppExerciceMensuel> newEMprop) {

		//changement de l'affichage de la date
		_text.setText(ApplicationFormatter.moiAnneedateFormat.format(newEMprop.get().getDateDebut()));

	}

}
