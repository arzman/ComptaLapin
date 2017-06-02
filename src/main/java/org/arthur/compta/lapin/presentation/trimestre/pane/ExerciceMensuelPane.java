package org.arthur.compta.lapin.presentation.trimestre.pane;

import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.application.model.AppExerciceMensuel;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.presentation.trimestre.table.OperationTableView;
import org.arthur.compta.lapin.presentation.trimestre.table.TransfertTableView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

/**
 * Panneau de présentation d'un exercice mensuel : tableau des dépenses ,
 * tableau des ressources et tableau des transferts.
 *
 */
public class ExerciceMensuelPane extends GridPane {

	/**
	 * Libellé de la date
	 */
	private ExerciceHeaderPane _title;
	/**
	 * Tableau des dépenses
	 */
	private OperationTableView<AppOperation> _depenseTable;
	/**
	 * Tableau des ressources
	 */
	private OperationTableView<AppOperation> _ressourceTable;
	/**
	 * Tableau des transferts
	 */
	private TransfertTableView _transfertTable;

	/**
	 * Constructeur
	 * 
	 * @param id
	 *            l'id
	 */
	public ExerciceMensuelPane(String id) {

		setId(id);

		_title = new ExerciceHeaderPane();
		add(_title, 0, 0);

		createContent();

	}

	/**
	 * Création du contenu graphique
	 */
	private void createContent() {
		ColumnConstraints colCons = new ColumnConstraints();
		colCons.setHgrow(Priority.ALWAYS);
		colCons.setFillWidth(true);
		getColumnConstraints().add(colCons);

		setVgap(5);

		// tableau des dépenses
		_depenseTable = new OperationTableView<AppOperation>();
		_depenseTable.setMaxWidth(Double.MAX_VALUE);
		_depenseTable.setMaxHeight(Double.MAX_VALUE);
		_depenseTable.setId("depTable");
		TitledPane depPane = new TitledPane("Dépenses", _depenseTable);
		depPane.setMaxHeight(Double.MAX_VALUE);
		depPane.setMaxWidth(Double.MAX_VALUE);
		depPane.setId("depPane");
		add(depPane, 0, 1);
		// tableau des ressources
		_ressourceTable = new OperationTableView<AppOperation>();
		_ressourceTable.setMaxWidth(Double.MAX_VALUE);
		_ressourceTable.setMaxHeight(Double.MAX_VALUE);
		_ressourceTable.setId("resTable");
		TitledPane resPane = new TitledPane("Ressources", _ressourceTable);
		resPane.setId("resPane");
		add(resPane, 0, 2);
		// tableau des transferts
		_transfertTable = new TransfertTableView();
		_transfertTable.setMaxWidth(Double.MAX_VALUE);
		_transfertTable.setMaxHeight(Double.MAX_VALUE);
		_transfertTable.setId("transTable");
		TitledPane transPane = new TitledPane("Transfert", _transfertTable);
		transPane.setId("transPane");
		add(transPane, 0, 3);

		// ajout d'une borduer
		Border bord = new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, new CornerRadii(5),
				new BorderWidths(1), new Insets(2, 2, 2, 2)));
		setBorder(bord);

		// restauration de l'état
		depPane.setExpanded(Boolean.parseBoolean(ConfigurationManager.getInstance()
				.getProp("ExerciceMensuelPane." + getId() + "." + depPane.getId() + ".exp", Boolean.toString(true))));
		depPane.expandedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				ConfigurationManager.getInstance().setProp(
						"ExerciceMensuelPane." + getId() + "." + depPane.getId() + ".exp",
						Boolean.toString(depPane.isExpanded()));
			}
		});
		resPane.setExpanded(Boolean.parseBoolean(ConfigurationManager.getInstance()
				.getProp("ExerciceMensuelPane." + getId() + "." + resPane.getId() + ".exp", Boolean.toString(true))));
		resPane.expandedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				ConfigurationManager.getInstance().setProp(
						"ExerciceMensuelPane." + getId() + "." + resPane.getId() + ".exp",
						Boolean.toString(resPane.isExpanded()));
			}
		});
		transPane.setExpanded(Boolean.parseBoolean(ConfigurationManager.getInstance()
				.getProp("ExerciceMensuelPane." + getId() + "." + transPane.getId() + ".exp", Boolean.toString(true))));
		transPane.expandedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				ConfigurationManager.getInstance().setProp(
						"ExerciceMensuelPane." + getId() + "." + transPane.getId() + ".exp",
						Boolean.toString(transPane.isExpanded()));
			}
		});

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
			_title.setMois(newEM.getDateDebut().getTime());
			_title.setResutlat(newEM.getResultat());

			_depenseTable.setItems(newEM.getDepenses());
			_ressourceTable.setItems(newEM.getRessources());
			_transfertTable.setItems(newEM.getTransferts());

		} else {
			_title.setMois(null);
			_title.setResutlat(0);
			_depenseTable.setItems(null);
			_ressourceTable.setItems(null);
			_transfertTable.setItems(null);
		}

	}

}
