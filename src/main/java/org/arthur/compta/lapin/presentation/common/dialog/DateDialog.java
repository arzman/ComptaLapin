package org.arthur.compta.lapin.presentation.common.dialog;

import java.time.LocalDate;

import org.arthur.compta.lapin.presentation.common.ComptaDialog;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * Fenetre permettant de choisr une date
 */
public class DateDialog extends ComptaDialog<LocalDate> {

	public DateDialog(LocalDate date) {

		super(DateDialog.class.getSimpleName());

		setTitle("Choisissez une date");

		DatePicker datPick = new DatePicker();

		if (date != null) {
			datPick.setValue(date);
		} else {

			datPick.setValue(LocalDate.now());
		}

		GridPane root = new GridPane();
		getDialogPane().setContent(root);
		root.add(datPick, 0, 0);

		// bouton annuler
		ButtonType cancelButton = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(cancelButton);

		// crée ou édite l'élement de template après appuis sur Ok
		setResultConverter(new Callback<ButtonType, LocalDate>() {

			@Override
			public LocalDate call(ButtonType param) {

				LocalDate deb = null;

				if (param.equals(_buttonTypeOk)) {
					deb = datPick.getValue();

				}

				return deb;
			}
		});

	}

}
