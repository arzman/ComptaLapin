package org.arthur.compta.lapin.presentation.trimestre.pane;

import org.arthur.compta.lapin.application.model.AppExerciceMensuel;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.model.operation.Operation;
import org.arthur.compta.lapin.presentation.trimestre.table.OperationTableView;
import org.arthur.compta.lapin.presentation.trimestre.table.TransfertTableView;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 * Panneau de présentation d'un exercice mensuel : tableau des dépenses ,
 * tableau des ressources et tableau des transferts.
 *
 */
public class ExerciceMensuelPane extends GridPane {

	/**
	 * Libellé de la date
	 */
	private Label _title;
	/**
	 * Tableau des dépenses
	 */
	private OperationTableView<AppOperation<Operation>> _depenseTable;
	/**
	 * Tableau des ressources
	 */
	private OperationTableView<AppOperation<Operation>> _ressourceTable;
	/**
	 * Tableau des transferts
	 */
	private TransfertTableView _transfertTable;
	
	/**
	 * 
	 */
	private static final String NO_CONTENT_DATE_STR = "######";

	/**
	 * Constructeur
	 */
	public ExerciceMensuelPane() {

		_title = new Label(NO_CONTENT_DATE_STR);
		add(_title, 0, 0);

		ColumnConstraints colCons = new ColumnConstraints();
		colCons.setHgrow(Priority.ALWAYS);
		colCons.setFillWidth(true);
		getColumnConstraints().add(colCons);
		
		RowConstraints rowCons = new RowConstraints();
		rowCons.setVgrow(Priority.ALWAYS);
		rowCons.setFillHeight(true);

		// tableau des dépenses
		_depenseTable = new OperationTableView<AppOperation<Operation>>();
		_depenseTable.setMaxWidth(Double.MAX_VALUE);
		_depenseTable.setMaxHeight(Double.MAX_VALUE);
		TitledPane depPane = new TitledPane("Dépenses", _depenseTable);
		depPane.setMaxHeight(Double.MAX_VALUE);
		depPane.setMaxWidth(Double.MAX_VALUE);
		add(depPane, 0, 1);
		// tableau des ressources
		_ressourceTable = new OperationTableView<AppOperation<Operation>>();
		_ressourceTable.setMaxWidth(Double.MAX_VALUE);
		_ressourceTable.setMaxHeight(Double.MAX_VALUE);
		TitledPane resPane = new TitledPane("Ressources", _ressourceTable);
		add(resPane, 0, 2);
		// tableau des transferts
		_transfertTable = new TransfertTableView();
		_transfertTable.setMaxWidth(Double.MAX_VALUE);
		_transfertTable.setMaxHeight(Double.MAX_VALUE);
		TitledPane transPane = new TitledPane("Transfert", _transfertTable);
		add(transPane, 0, 3);

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
			_title.setText(ApplicationFormatter.moiAnneedateFormat.format(newEM.getDateDebut().getTime()));

		} else {
			_title.setText(NO_CONTENT_DATE_STR);
		}

	}

}
