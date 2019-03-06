package org.arthur.compta.lapin.presentation.trimestre.pane;

import java.util.Optional;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppExerciceMensuelLightId;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.operation.dialog.CreateOperationDialog;
import org.arthur.compta.lapin.presentation.operation.table.OperationTableView;
import org.arthur.compta.lapin.presentation.operation.table.TransfertTableView;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;
import org.arthur.compta.lapin.presentation.trimestre.dialog.SelectExerciceMensuelDialog;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
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
import javafx.scene.layout.RowConstraints;
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
	private double _prevRes;
	/** Groupement des tableaux */
	private Accordion accordion;

	/**
	 * Constructeur
	 * 
	 * @param id l'id
	 */
	public ExerciceMensuelPane(String id, int numMois) {

		setId(id);
		_numMois = numMois;
		_prevRes = TrimestreManager.getInstance().getResultatPrev(_numMois);

		_title = new ExerciceHeaderPane(_prevRes);
		add(_title, 0, 0);
		getRowConstraints().add(new RowConstraints());
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
		_depenseTable.setMaxHeight(Double.MAX_VALUE);
		createContextMenu(_depenseTable);
		depPane = new TitledPane("Dépenses", _depenseTable);
		depPane.setMaxHeight(Double.MAX_VALUE);
		depPane.setMaxWidth(Double.MAX_VALUE);
		depPane.setId("depPane");

		RowConstraints depconst = new RowConstraints();
		depconst.setVgrow(Priority.SOMETIMES);
		getRowConstraints().add(depconst);

		// tableau des ressources
		_ressourceTable = new OperationTableView<AppOperation>();
		_ressourceTable.setMaxWidth(Double.MAX_VALUE);
		_ressourceTable.setMaxHeight(Double.MAX_VALUE);
		_ressourceTable.setId("resTable");
		createContextMenu(_ressourceTable);
		resPane = new TitledPane("Ressources", _ressourceTable);
		resPane.setId("resPane");
		resPane.setMaxHeight(Double.MAX_VALUE);

		// tableau des transferts
		_transfertTable = new TransfertTableView();
		_transfertTable.setMaxWidth(Double.MAX_VALUE);
		_transfertTable.setMaxHeight(Double.MAX_VALUE);
		_transfertTable.setId("transTable");
		createContextMenu(_transfertTable);
		transPane = new TitledPane("Transfert", _transfertTable);
		transPane.setId("transPane");

		accordion = new Accordion(depPane, resPane, transPane);
		add(accordion, 0, 1);

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
		int idExp = Integer
				.parseInt(ConfigurationManager.getInstance().getProp("ExerciceMensuelPane." + getId() + ".exp", "0"));

		switch (idExp) {

		case 0:
			accordion.setExpandedPane(depPane);
			break;
		case 1:
			accordion.setExpandedPane(resPane);
			break;
		case 2:
			accordion.setExpandedPane(transPane);
			break;
		default:
			accordion.setExpandedPane(depPane);

		}

		// écoute du changement d'état
		accordion.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {

			@Override
			public void changed(ObservableValue<? extends TitledPane> observable, TitledPane oldValue,
					TitledPane newValue) {

				if (newValue == depPane) {
					ConfigurationManager.getInstance().setProp("ExerciceMensuelPane." + getId() + ".exp", "0");
				}
				if (newValue == resPane) {
					ConfigurationManager.getInstance().setProp("ExerciceMensuelPane." + getId() + ".exp", "1");
				}
				if (newValue == transPane) {
					ConfigurationManager.getInstance().setProp("ExerciceMensuelPane." + getId() + ".exp", "2");
				}

			}

		});

		// restitution des largeur de colonnes
		ConfigurationManager.getInstance().setPrefColumnWidth(_depenseTable,
				"ExerciceMensuel" + getId() + ".tabledep.col");
		ConfigurationManager.getInstance().setPrefColumnWidth(_ressourceTable,
				"ExerciceMensuel" + getId() + ".tableres.col.");
		ConfigurationManager.getInstance().setPrefColumnWidth(_transfertTable,
				"ExerciceMensuel" + getId() + ".tabletrans.col");

	}

	/**
	 * Création du menu contexuel sur le tableau des compte
	 * 
	 * @param table le tableau des comptes
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

				Optional<String> st = cod.showAndWait();
				if (st.isPresent()) {

					if (st.get().equals("DEPENSE")) {
						_depenseTable.sort();
					} else if (st.get().equals("RESSOURCE")) {
						_ressourceTable.sort();
					} else if (st.get().equals("TRASNFERT")) {
						_transfertTable.sort();
					}

				}

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
				Optional<String> st = cod.showAndWait();
				if (st.isPresent()) {

					if (st.get().equals("DEPENSE")) {
						_depenseTable.sort();
					} else if (st.get().equals("RESSOURCE")) {
						_ressourceTable.sort();
					} else if (st.get().equals("TRASNFERT")) {
						_transfertTable.sort();
					}
				}
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

		// action de suppression de l'operation
		final MenuItem trasnOp = new MenuItem("Transférer");
		trasnOp.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.TRANSFERT_IMG)));
		trasnOp.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				// récupération de l'operation
				AppOperation appOp = table.getSelectionModel().getSelectedItems().get(0);
				SelectExerciceMensuelDialog semd = new SelectExerciceMensuelDialog();

				Optional<AppExerciceMensuelLightId> appEMId = semd.showAndWait();

				if (appEMId != null && appEMId.isPresent()) {
					try {
						TrimestreManager.getInstance().moveOperationFromTrimCourant(appOp, _numMois, appEMId.get());
					} catch (ComptaException e) {
						ExceptionDisplayService.showException(e);
					}
				}

			}
		});
		// on désactive le menu si la selection est vide ou si l'opération est deja
		// prise en compte

		BooleanBinding booBind = Bindings.createBooleanBinding(
				() -> (table.getSelectionModel().getSelectedItem() != null && table.getSelectionModel()
						.getSelectedItem().getEtat().equals(EtatOperation.PRISE_EN_COMPTE.toString()))
						|| (table.getSelectionModel().isEmpty()),
				table.getSelectionModel().getSelectedItems());

		trasnOp.disableProperty().bind(booBind);
		menu.getItems().add(trasnOp);

	}

	/**
	 * Change l'affichage des mois
	 * 
	 */
	public void changeBind() {

		_title.setMois(TrimestreManager.getInstance().getDateDebut(_numMois));
		_title.setResultatPrev(TrimestreManager.getInstance().getResultatPrev(_numMois));
		_title.setResutlat(TrimestreManager.getInstance().getResultat(_numMois));

		_depenseTable.setItems(TrimestreManager.getInstance().getDepenses(_numMois));
		_ressourceTable.setItems(TrimestreManager.getInstance().getRessources(_numMois));
		_transfertTable.setItems(TrimestreManager.getInstance().getTransfert(_numMois));

	}

}
