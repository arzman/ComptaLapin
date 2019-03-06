package org.arthur.compta.lapin.presentation.trimestre.pane;

import java.time.LocalDate;

import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Panneau permettant d'afficher le mois et le résultat ( gain ou perte) pour un
 * exercice mensuel
 *
 */
public class ExerciceHeaderPane extends GridPane {

	/** Label du mois */
	private Label _moisLbl;
	/** Label du resultat */
	private Label _resultatLbl;
	private double _prevRes;
	/** Pas de date défini */
	private static final String NO_CONTENT_DATE_STR = "######";

	/**
	 * Le constructeur
	 */
	public ExerciceHeaderPane(double prev) {

		_prevRes = prev;

		_moisLbl = new Label();
		_moisLbl.setFont(Font.font("Verdana", 18));
		add(_moisLbl, 0, 0);
		// le résultat
		_resultatLbl = new Label();
		add(_resultatLbl, 1, 0);
		// centrage des libellés
		ColumnConstraints colCons = new ColumnConstraints();
		colCons.setFillWidth(true);
		colCons.setHgrow(Priority.ALWAYS);
		colCons.setHalignment(HPos.CENTER);

		getColumnConstraints().addAll(colCons, colCons);
	}

	/**
	 * Affiche la date sous forme <mois année>
	 * 
	 * @param time la date
	 */
	public void setMois(LocalDate time) {

		if (time != null) {
			_moisLbl.setText(ApplicationFormatter.moiAnneedateFormat.format(time));
		} else {
			_moisLbl.setText(NO_CONTENT_DATE_STR);
		}

	}

	/**
	 * Affiche le résultat
	 * 
	 * @param res
	 */
	public void setResutlat(double res) {

		if (res < 0) {
			_resultatLbl.setTextFill(Color.RED);
		} else {

			if (res > _prevRes) {
				_resultatLbl.setTextFill(Color.GREEN);
			} else {
				_resultatLbl.setTextFill(Color.ORANGE);
			}

		}

		_resultatLbl.setText(ApplicationFormatter.montantFormat.format(res) + " / "
				+ ApplicationFormatter.montantFormat.format(_prevRes));

	}

	/**
	 * Positionne le résultat prévisionnel
	 * 
	 * @param resultatPrev
	 */
	public void setResultatPrev(double resultatPrev) {
		_prevRes = resultatPrev;

	}

}
