package org.arthur.compta.lapin.presentation.budget.dialog;

import java.util.Calendar;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.BudgetManager;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.application.model.AppUtilisation;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.common.cellfactory.DateCellFactory;
import org.arthur.compta.lapin.presentation.common.cellfactory.MontantCellFactory;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/**
 * Fênetre de visualisation de l'historique d'un budget
 *
 */
public class HistoryBudgetDialog extends ComptaDialog<ButtonData> {

	/** Le budget */
	private AppBudget _appBudget;
	/** La liste des utilisations du budget */
	private ObservableList<AppUtilisation> _useList;
	/** presentation de la liste */
	private TableView<AppUtilisation> _table;

	/**
	 * Constructeur
	 * 
	 * @param app
	 */
	public HistoryBudgetDialog(AppBudget app) {
		super(HistoryBudgetDialog.class.getSimpleName());

		_appBudget = app;
		_useList = FXCollections.observableArrayList();

		try {
			_useList.addAll(BudgetManager.getInstance().getUtilisation(_appBudget.getAppId()));
		} catch (ComptaException e) {
			ExceptionDisplayService.showException(e);
		}

		setTitle("Historique du budget");

		// création du contenu
		createContent();

		// création du menu contextuel
		createCtxMenu();

		// creation des bouton
		createButtonBar();

	}

	/**
	 * Création des zones d'affichage
	 */
	private void createContent() {

		// configuration du layout
		GridPane root = new GridPane();
		getDialogPane().setContent(root);
		root.setVgap(2.0);

		// affichage du nom
		Label nomLbl = new Label("Nom : ");
		root.add(nomLbl, 0, 0);
		Label nomTxt = new Label(_appBudget.getNom());
		root.add(nomTxt, 1, 0);

		// affichage de l'objectif
		Label objLbl = new Label("Objectif : ");
		root.add(objLbl, 0, 1);
		Label objTxt = new Label(ApplicationFormatter.montantFormat.format(_appBudget.getObjectif()));
		root.add(objTxt, 1, 1);

		// affichage du montant utilisé
		Label utilLbl = new Label("Utilisé : ");
		root.add(utilLbl, 0, 2);
		nomLbl.setTooltip(new Tooltip("Le montant déjà utilisé. Nombre réel"));
		Label utilsTxt = new Label(ApplicationFormatter.montantFormat.format(_appBudget.getMontantUtilise()));
		root.add(utilsTxt, 1, 2);

		_table = new TableView<>();
		root.add(_table, 0, 3, 2, 1);
		_table.setItems(_useList);

		// la colonne du nom
		TableColumn<AppUtilisation, String> colnom = new TableColumn<>("Libellé");
		colnom.setResizable(true);
		colnom.setSortable(true);
		_table.getColumns().add(colnom);
		colnom.setCellValueFactory(value -> value.getValue().nomProperty());

		// la colonne de la date
		TableColumn<AppUtilisation, Calendar> colDate = new TableColumn<>("Date");
		colDate.setResizable(true);
		colDate.setSortable(true);
		_table.getColumns().add(colDate);
		colDate.setCellValueFactory(value -> value.getValue().dateProperty());
		colDate.setCellFactory(new DateCellFactory<>());

		// la colonne du montant
		TableColumn<AppUtilisation, Number> colMontant = new TableColumn<>("Montant");
		colMontant.setResizable(true);
		colMontant.setSortable(true);
		_table.getColumns().add(colMontant);
		colMontant.setCellValueFactory(value -> value.getValue().montantProperyt());
		colMontant.setCellFactory(new MontantCellFactory<>());
	}

	/**
	 * Création des actions contextuelles sur le tableau
	 */
	private void createCtxMenu() {

		ContextMenu menu = new ContextMenu();
		_table.setContextMenu(menu);

		MenuItem editItem = new MenuItem("Editer");
		editItem.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.EDIT_IMG)));
		editItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				AppUtilisation util = _table.getSelectionModel().getSelectedItem();

				EditUtilisationDialog dia = new EditUtilisationDialog(util);
				dia.showAndWait();

			}
		});
		editItem.disableProperty().bind(Bindings.isEmpty(_table.getSelectionModel().getSelectedItems()));

		_table.getContextMenu().getItems().add(editItem);

	}

	/**
	 * Création des boutons
	 */
	private void createButtonBar() {
		// bouton fermer
		ButtonType close = new ButtonType("Fermer", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(close);

	}

}
