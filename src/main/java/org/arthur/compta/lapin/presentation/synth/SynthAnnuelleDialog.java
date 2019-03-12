package org.arthur.compta.lapin.presentation.synth;

import java.time.LocalDate;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.service.SyntheseService;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

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
import javafx.scene.layout.RowConstraints;

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
		root.setVgap(2.0);
		root.setHgap(2.0);
		getDialogPane().setContent(root);

		ColumnConstraints c1 = new ColumnConstraints();
		ColumnConstraints c2 = new ColumnConstraints();
		c2.setFillWidth(true);
		c2.setHgrow(Priority.ALWAYS);
		root.getColumnConstraints().addAll(c1, c2);

		RowConstraints r1 = new RowConstraints();
		RowConstraints r2 = new RowConstraints();
		r2.setFillHeight(true);
		r2.setVgrow(Priority.ALWAYS);
		root.getRowConstraints().addAll(r1, r2);

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
	 * @throws ComptaException Echec lors de la récupératoin des données
	 */
	private void drawChart(Integer year) throws ComptaException {

		_lineChart.setTitle("Synthèse " + year);

		_lineChart.getData().clear();

		Series<String, Number> depenseSerie = new Series<String, Number>();
		depenseSerie.setName("Dépenses");

		Series<String, Number> ressourceSerie = new Series<String, Number>();
		ressourceSerie.setName("Ressource");

		Series<String, Number> budgetUseSerie = new Series<String, Number>();
		budgetUseSerie.setName("Budget utilisé");

		for (int month = 1; month < 13; month++) {

			LocalDate date = LocalDate.of(year, month, 1);

			double dep = SyntheseService.getDepenseForMonth(date);
			double res = SyntheseService.getRessourceForMonth(date);
			double bud = SyntheseService.getBudgetUsageForMonth(date);

			depenseSerie.getData().add(new Data<String, Number>(ApplicationFormatter.moisFormat.format(date), dep));
			ressourceSerie.getData().add(new Data<String, Number>(ApplicationFormatter.moisFormat.format(date), res));
			budgetUseSerie.getData().add(new Data<String, Number>(ApplicationFormatter.moisFormat.format(date), bud));

		}

		_lineChart.getData().add(depenseSerie);
		_lineChart.getData().add(ressourceSerie);
		_lineChart.getData().add(budgetUseSerie);

	}

}
