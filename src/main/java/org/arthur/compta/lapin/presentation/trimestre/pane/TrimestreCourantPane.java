package org.arthur.compta.lapin.presentation.trimestre.pane;

import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppTrimestre;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 * Panneau d'affichage du trimestre courant Composé des affichages des trois
 * ExerciceMensuel
 *
 */
public class TrimestreCourantPane extends GridPane {

	/**
	 * Affichage du premier mois
	 */
	private ExerciceMensuelPane _premMoisPane;
	/**
	 * Affichage du deuxieme mois
	 */
	private ExerciceMensuelPane _deuxMoisPane;
	/**
	 * Affichage du troisieme mois
	 */
	private ExerciceMensuelPane _troisMoisPane;

	/**
	 * Constructeur par défaut
	 */
	public TrimestreCourantPane() {

		super();

		// paramétrage du layout
		ColumnConstraints colCons = new ColumnConstraints();
		colCons.setFillWidth(true);
		colCons.setHgrow(Priority.ALWAYS);
		// on l'ajoute 3 fois car 3 colonnes
		getColumnConstraints().add(colCons);
		getColumnConstraints().add(colCons);
		getColumnConstraints().add(colCons);

		RowConstraints rowCons = new RowConstraints();
		rowCons.setFillHeight(true);
		rowCons.setVgrow(Priority.ALWAYS);
		getRowConstraints().add(rowCons);

		setVgap(2);
		setPadding(new Insets(2, 2, 2, 2));

		// Création du contenu

		// panneau du premier mois
		_premMoisPane = new ExerciceMensuelPane();
		add(_premMoisPane, 0, 0);

		// panneau du deuxieme mois
		_deuxMoisPane = new ExerciceMensuelPane();
		add(_deuxMoisPane, 1, 0);

		// panneau du troisieme mois
		_troisMoisPane = new ExerciceMensuelPane();
		add(_troisMoisPane, 2, 0);

		// écoute sur le changement du trimestre courant
		TrimestreManager.getInstance().trimestreCourantProperty().addListener(new ChangeListener<AppTrimestre>() {

			@Override
			public void changed(ObservableValue<? extends AppTrimestre> observable, AppTrimestre oldValue,
					AppTrimestre newValue) {

				updateExerciceMensuelPane(oldValue,newValue);

			}

		});
	}

	/**
	 * Mets à jour le contenu des IHM des excercices mensuels
	 * @param oldValue
	 * @param newValue2 
	 */
	private void updateExerciceMensuelPane(AppTrimestre oldValue, AppTrimestre newValue) {
		
		
		// Mise à jour du premier mois
		_premMoisPane.changeBind(oldValue.premierMoisProperty(),newValue.premierMoisProperty());
	
		

	}

}
