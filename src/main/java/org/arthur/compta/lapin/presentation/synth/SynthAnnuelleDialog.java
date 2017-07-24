package org.arthur.compta.lapin.presentation.synth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.service.SyntheseService;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Fenetre traçant la synthèse sur l'année.
 *
 */
public class SynthAnnuelleDialog extends ComptaDialog<ButtonData> {

	/** La combo pour choisir l'année */
	private ComboBox<Integer> _yearCombo;
	/** La liste des années disponibles */
	private ObservableList<Integer> _yearList;
	/** Le graphique présentant les totaux des dépense/ressource */
	private LineChart<String, Number> _lineChart;

	/**
	 * Constructeur
	 */
	public SynthAnnuelleDialog() {
		super(SynthAnnuelleDialog.class.getSimpleName());

		setTitle("Synthèse Annuelle");

		GridPane root = new GridPane();
		getDialogPane().setContent(root);

		ColumnConstraints c1 = new ColumnConstraints();
		ColumnConstraints c2 = new ColumnConstraints();
		c2.setFillWidth(true);
		c2.setHgrow(Priority.ALWAYS);
		root.getColumnConstraints().addAll(c1, c2);

		Label comboLdl = new Label("Sélection de l'année");
		_yearCombo = new ComboBox<Integer>();
		_yearList = FXCollections.observableArrayList();
		_yearCombo.setItems(_yearList);

		_yearCombo.valueProperty().addListener(new ChangeListener<Integer>() {

			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {

				try {
					drawChart(newValue);
				} catch (ComptaException e) {
					ExceptionDisplayService.showException(e);
				}

			}

		});

		root.add(comboLdl, 0, 0);
		root.add(_yearCombo, 1, 0);

		final CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Mois");
		xAxis.setAnimated(false);

		final NumberAxis yAxis = new NumberAxis();

		// creating the chart
		_lineChart = new LineChart<String, Number>(xAxis, yAxis);
		root.add(_lineChart, 0, 2, 2, 1);

		getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

		try {
			fillYearCombo();
		} catch (ComptaException e) {
			ExceptionDisplayService.showException(e);
		}

	}

	/**
	 * Remplit la combobox avec les années disponible.
	 * 
	 * @throws ComptaException
	 */
	private void fillYearCombo() throws ComptaException {

		_yearList.clear();
		_yearList.addAll(SyntheseService.getAnnees());

	}

	/**
	 * Remplit le graphique avec les valeurs de l'année
	 * 
	 * @param year
	 * @throws ComptaException
	 *             Echec lors de la récupératoin des données
	 */
	private void drawChart(Integer year) throws ComptaException {

		_lineChart.setTitle("Synthèse " + year);

		Series<String, Number> depenseSerie = new Series<String, Number>();
		depenseSerie.setName("Dépenses");

		Series<String, Number> ressourceSerie = new Series<String, Number>();
		ressourceSerie.setName("Ressource");

		SimpleDateFormat format = new SimpleDateFormat("MMMM", Locale.FRANCE);

		for (int month = 0; month < 12; month++) {

			Calendar date = Calendar.getInstance();
			date.set(year, month, 1);

			double dep = SyntheseService.getDepenseForMonth(date);
			double res = SyntheseService.getRessourceForMonth(date);

			depenseSerie.getData().add(new Data<String, Number>(format.format(date.getTime()), dep));
			ressourceSerie.getData().add(new Data<String, Number>(format.format(date.getTime()), res));

		}

		_lineChart.getData().add(depenseSerie);
		_lineChart.getData().add(ressourceSerie);

	}

}
