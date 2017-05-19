package org.arthur.compta.lapin.presentation.trimestre.pane;

import org.arthur.compta.lapin.application.model.AppExerciceMensuel;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

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

		_text = new Label("Aucun contenu");
		add(_text, 0, 0);

	}

	/**
	 * Change l'affichage des mois
	 * 
	 * @param oldEM
	 *            l'ancien mois dans un propriété observable
	 * @param newEM
	 *            le nouveau mois dans un propriété observable
	 */
	public void changeBind(AppExerciceMensuel oldEM, AppExerciceMensuel newEM) {

		// changement de l'affichage de la date
		if (newEM != null) {
			_text.setText(ApplicationFormatter.moiAnneedateFormat.format(newEM.getDateDebut().getTime()));

		}else{
			_text.setText("Aucun contenu");
		}

	}

}
