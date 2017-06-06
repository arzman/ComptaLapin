package org.arthur.compta.lapin.presentation.trimestre.pane;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppExerciceMensuel;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;
import org.arthur.compta.lapin.presentation.trimestre.table.OperationTableView;
import org.arthur.compta.lapin.presentation.trimestre.table.TransfertTableView;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
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

	/** Libellé de la date	 */
	private ExerciceHeaderPane _title;
	/** Tableau des dépenses */
	private OperationTableView<AppOperation> _depenseTable;
	/** Tableau des ressources	 */
	private OperationTableView<AppOperation> _ressourceTable;
	/** Tableau des transferts	 */
	private TransfertTableView _transfertTable;
	/** Numéro du mois présenté */
	private Integer _numMois;

	/**
	 * Constructeur
	 * 
	 * @param id
	 *            l'id
	 */
	public ExerciceMensuelPane(String id,int numMois) {

		setId(id);
		_numMois = numMois;
		
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
		createContextMenu(_depenseTable);
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
		createContextMenu(_ressourceTable);
		TitledPane resPane = new TitledPane("Ressources", _ressourceTable);
		resPane.setId("resPane");
		add(resPane, 0, 2);
		// tableau des transferts
		_transfertTable = new TransfertTableView();
		_transfertTable.setMaxWidth(Double.MAX_VALUE);
		_transfertTable.setMaxHeight(Double.MAX_VALUE);
		_transfertTable.setId("transTable");
		createContextMenu(_transfertTable);
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
	 * Création du menu contexuel sur le tableau des compte
	 * 
	 * @param table
	 *            le tableau des comptes
	 */
	private void createContextMenu(TableView<? extends AppOperation> table) {

		// le menu contextuel
		final ContextMenu menu = new ContextMenu();
		table.setContextMenu(menu);

		// action de suppression de l'operation
		final MenuItem removeOp = new MenuItem("Supprimer");
		removeOp.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.DEL_IMG)));
		removeOp.setOnAction(new EventHandler<ActionEvent>() {
			

			@Override
			public void handle(ActionEvent event) {

				// récupération de l'operation
				AppOperation appOp = table.getSelectionModel().getSelectedItems().get(0);
				// suppression
				try {
					TrimestreManager.getInstance().removeOperation(appOp,_numMois);
				} catch (ComptaException e) {
					ExceptionDisplayService.showException(e);
				}
			}
		});
		// on désactive le menu si la selection est vide
		removeOp.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
		menu.getItems().add(removeOp);

		

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
