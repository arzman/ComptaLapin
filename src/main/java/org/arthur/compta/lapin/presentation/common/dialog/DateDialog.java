package org.arthur.compta.lapin.presentation.common.dialog;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * Fenetre permettant de choisr une date
 */
public class DateDialog extends Dialog<Calendar> {

	public DateDialog(Calendar date) {

		setTitle("Choisissez une date");

		DatePicker datPick = new DatePicker();

		if (date != null) {
			datPick.setValue(
					LocalDate.of(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH)));
		} else {

			datPick.setValue(LocalDate.now());
		}

		GridPane root = new GridPane();
		getDialogPane().setContent(root);
		root.add(datPick, 0, 0);

		ButtonType _okButton = new ButtonType("Ok", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().add(_okButton);
		// bouton annuler
		ButtonType cancelButton = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(cancelButton);

		// crée ou édite l'élement de template après appuis sur Ok
		setResultConverter(new Callback<ButtonType, Calendar>() {

			@Override
			public Calendar call(ButtonType param) {

				Calendar deb = null;

				if (param == _okButton) {
					deb = Calendar.getInstance();
					deb.setTime(Date.valueOf(datPick.getValue()));

				}

				return deb;
			}
		});

	}

}
