package org.arthur.compta.lapin.presentation.trimestre.pane;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.operation.dialog.CreateOperationDialog;
import org.arthur.compta.lapin.presentation.operation.table.OperationTableView;
import org.arthur.compta.lapin.presentation.operation.table.TransfertTableView;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
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

	/** Libellé de la date */
	private ExerciceHeaderPane _title;
	/** Tableau des dépenses */
	private OperationTableView<AppOperation> _depenseTable;
	/** Tableau des ressources */
	private OperationTableView<AppOperation> _ressourceTable;
	/** Tableau des transferts */
	private TransfertTableView _transfertTable;
	/** Numéro du mois présenté */
	private Integer _numMois;
	/** panneau des depenses */
	private TitledPane depPane;
	/** panneau des ressources */
	private TitledPane resPane;
	/** panneau des transfert */
	private TitledPane transPane;

	/**
	 * Constructeur
	 * 
	 * @param id
	 *            l'id
	 */
	public ExerciceMensuelPane(String id, int numMois) {

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
		depPane = new TitledPane("Dépenses", _depenseTable);
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
		resPane = new TitledPane("Ressources", _ressourceTable);
		resPane.setId("resPane");
		add(resPane, 0, 2);
		// tableau des transferts
		_transfertTable = new TransfertTableView();
		_transfertTable.setMaxWidth(Double.MAX_VALUE);
		_transfertTable.setMaxHeight(Double.MAX_VALUE);
		_transfertTable.setId("transTable");
		createContextMenu(_transfertTable);
		transPane = new TitledPane("Transfert", _transfertTable);
		transPane.setId("transPane");
		add(transPane, 0, 3);

		// ajout d'une borduer
		Border bord = new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, new CornerRadii(5),
				new BorderWidths(1), new Insets(2, 2, 2, 2)));
		setBorder(bord);

		hookStateListener();

	}

	/**
	 * Ajout des listener afin d'enregistrer l'état du panneau ( taille colonne,
	 * panneau étendu ou pas)
	 */
	private void hookStateListener() {

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

		// restitution des largeur de colonnes
		for (TableColumn<?, ?> col : _depenseTable.getColumns()) {

			col.widthProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					ConfigurationManager.getInstance().setProp(
							"ExerciceMensuel" + getId() + ".tabledep.col." + col.getId(), String.valueOf(newValue));

				}
			});
			col.setPrefWidth(Double.parseDouble(ConfigurationManager.getInstance()
					.getProp("ExerciceMensuel" + getId() + ".tabledep.col." + col.getId(), "50")));
		}

		// restitution des largeur de colonnes
		for (TableColumn<?, ?> col : _ressourceTable.getColumns()) {

			col.widthProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					ConfigurationManager.getInstance().setProp(
							"ExerciceMensuel" + getId() + ".tableres.col." + col.getId(), String.valueOf(newValue));

				}
			});
			col.setPrefWidth(Double.parseDouble(ConfigurationManager.getInstance()
					.getProp("ExerciceMensuel" + getId() + ".tableres.col." + col.getId(), "50")));
		}

		// restitution des largeur de colonnes
		for (TableColumn<?, ?> col : _transfertTable.getColumns()) {

			col.widthProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					ConfigurationManager.getInstance().setProp(
							"ExerciceMensuel" + getId() + ".tabletrans.col." + col.getId(), String.valueOf(newValue));

				}
			});
			col.setPrefWidth(Double.parseDouble(ConfigurationManager.getInstance()
					.getProp("ExerciceMensuel" + getId() + ".tabletrans.col." + col.getId(), "50")));
		}

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

		// action d'ajout d'opération
		final MenuItem addOp = new MenuItem("Ajouter");
		addOp.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.ADD_IMG)));
		addOp.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				// remonte l'ihm de saisie
				CreateOperationDialog cod = new CreateOperationDialog(null, _numMois);
				cod.showAndWait();
				_title.setResutlat(TrimestreManager.getInstance().getResultat(_numMois));

			}
		});
		menu.getItems().add(addOp);

		// action d'édition d'opération
		final MenuItem editOp = new MenuItem("Editer");
		editOp.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.EDIT_IMG)));
		editOp.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				AppOperation appOp = table.getSelectionModel().getSelectedItems().get(0);

				// remonte l'ihm de saisie
				CreateOperationDialog cod = new CreateOperationDialog(appOp, _numMois);
				cod.showAndWait();
				_title.setResutlat(TrimestreManager.getInstance().getResultat(_numMois));

			}
		});
		// on désactive le menu si la selection est vide
		editOp.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
		menu.getItems().add(editOp);

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
					TrimestreManager.getInstance().removeOperation(appOp, _numMois);
					_title.setResutlat(TrimestreManager.getInstance().getResultat(_numMois));
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
	 */
	public void changeBind() {

		_title.setMois(TrimestreManager.getInstance().getDateDebut(_numMois));
		_title.setResutlat(TrimestreManager.getInstance().getResultat(_numMois));

		_depenseTable.setItems(TrimestreManager.getInstance().getDepenses(_numMois));
		_ressourceTable.setItems(TrimestreManager.getInstance().getRessources(_numMois));
		_transfertTable.setItems(TrimestreManager.getInstance().getTransfert(_numMois));

	}

}
