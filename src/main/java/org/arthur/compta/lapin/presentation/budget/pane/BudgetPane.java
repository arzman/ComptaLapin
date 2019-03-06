package org.arthur.compta.lapin.presentation.budget.pane;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.BudgetManager;
import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.presentation.budget.cellfactory.AvancementTableCell;
import org.arthur.compta.lapin.presentation.budget.cellfactory.NomBudgetTableCell;
import org.arthur.compta.lapin.presentation.budget.dialog.EditBudgetDialog;
import org.arthur.compta.lapin.presentation.budget.dialog.HistoryBudgetDialog;
import org.arthur.compta.lapin.presentation.budget.dialog.UseBudgetDialog;
import org.arthur.compta.lapin.presentation.common.cellfactory.MontantCellFactory;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 * Panneau d'affichage des bugets
 *
 */
public class BudgetPane extends GridPane {

	/** Le tableau des budgets */
	private TableView<AppBudget> _table;

	public BudgetPane() {

		// paramétrage du layout
		ColumnConstraints colCons = new ColumnConstraints();
		colCons.setFillWidth(true);
		colCons.setHgrow(Priority.ALWAYS);
		getColumnConstraints().add(colCons);
		setVgap(2);
		setPadding(new Insets(2, 2, 2, 2));

		RowConstraints rcons = new RowConstraints();
		rcons.setFillHeight(true);
		rcons.setVgrow(Priority.ALWAYS);
		getRowConstraints().add(rcons);

		// Création du tableau des budgets
		createBudgetTable();

		// restitution des largeur de colonnes
		ConfigurationManager.getInstance().setPrefColumnWidth(_table, "BudgetPane.table.col");

	}

	/**
	 * Création du tableau des comptes
	 */
	private void createBudgetTable() {

		// Création de la table des comptes
		_table = new TableView<>();
		_table.setMaxWidth(Double.MAX_VALUE);
		_table.setMaxHeight(Double.MAX_VALUE);
		add(_table, 0, 0);

		// Colonne du nom
		TableColumn<AppBudget, String> colNom = new TableColumn<>("Nom");
		colNom.setResizable(true);
		colNom.setEditable(false);
		colNom.setId("nom");
		// bind sur la nom
		colNom.setCellValueFactory(cellData -> Bindings.createStringBinding(
				() -> cellData.getValue().getNom() + "#" + String.valueOf(cellData.getValue().isTermine()),
				cellData.getValue().nomProperty(), cellData.getValue().termineProperty()));
		colNom.setCellFactory(new NomBudgetTableCell());
		_table.getColumns().add(colNom);

		// Colonne de l'objectif
		TableColumn<AppBudget, Number> colObj = new TableColumn<>("Objectif");
		colObj.setResizable(true);
		colObj.setEditable(false);
		colObj.setCellValueFactory(cellData -> cellData.getValue().objectifProperty());
		colObj.setCellFactory(new MontantCellFactory<AppBudget>());
		colObj.setId("obj");
		_table.getColumns().add(colObj);

		// Colonne de l'avancement
		TableColumn<AppBudget, Number> colAv = new TableColumn<>("Avancement");
		colAv.setResizable(true);
		colAv.setEditable(false);
		colAv.setId("avance");
		// binding crado avec le nom de la propriété
		colAv.setCellValueFactory(cellData -> cellData.getValue().avancementProperty());
		colAv.setCellFactory(new AvancementTableCell());
		_table.getColumns().add(colAv);

		// Colonne du montant sur compte livret
		TableColumn<AppBudget, Number> collivret = new TableColumn<>("CL");
		collivret.setResizable(true);
		collivret.setEditable(false);
		collivret.setId("prev3");
		collivret.setCellValueFactory(cellData -> cellData.getValue().montantLivretProperty());
		collivret.setCellFactory(new MontantCellFactory<AppBudget>());
		_table.getColumns().add(collivret);

		// Colonne du montant sur compte courant
		TableColumn<AppBudget, Number> colcourant = new TableColumn<>("CC");
		colcourant.setResizable(true);
		colcourant.setEditable(false);
		colcourant.setId("prev2");
		colcourant.setCellValueFactory(cellData -> cellData.getValue().montantCourantProperty());
		colcourant.setCellFactory(new MontantCellFactory<AppBudget>());
		_table.getColumns().add(colcourant);

		// bind à la liste des comptes
		_table.setItems(BudgetManager.getInstance().getBudgetList());

		// ajout du menu contextuel
		_table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		createContextMenu();

	}

	/**
	 * Création du menu contexuel sur le tableau des budgets
	 * 
	 * @param table le tableau des comptes
	 */
	private void createContextMenu() {

		// le menu contextuel
		final ContextMenu menu = new ContextMenu();
		_table.setContextMenu(menu);

		// action d'utilisation d'un budget
		final MenuItem useBudget = new MenuItem("Utiliser");
		useBudget.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.USE_BUDGET_IMG)));
		useBudget.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				// récupération du budget applicatif
				AppBudget appB = _table.getSelectionModel().getSelectedItems().get(0);

				UseBudgetDialog dia = new UseBudgetDialog(appB);
				dia.showAndWait();
			}
		});
		menu.getItems().add(useBudget);

		// action de visualisation d'historique d'un budget
		final MenuItem showHistBudget = new MenuItem("Historique");
		showHistBudget.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.HISTORY_IMG)));
		showHistBudget.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				// récupération du budget applicatif
				AppBudget appB = _table.getSelectionModel().getSelectedItems().get(0);

				HistoryBudgetDialog dia = new HistoryBudgetDialog(appB);
				dia.showAndWait();
			}
		});
		menu.getItems().add(showHistBudget);

		// action d'édition des budgets
		final MenuItem editBudget = new MenuItem("Editer");
		editBudget.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.EDIT_IMG)));
		editBudget.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				// récupération du budget applicatif
				AppBudget appB = _table.getSelectionModel().getSelectedItems().get(0);
				EditBudgetDialog dia = new EditBudgetDialog(appB);
				dia.showAndWait();
			}
		});
		// on désactive le menu si la selection est vide
		editBudget.disableProperty().bind(Bindings.isEmpty(_table.getSelectionModel().getSelectedItems()));
		menu.getItems().add(editBudget);

		// action de désactivation des budgets
		final MenuItem removeBudget = new MenuItem("Désactiver");
		removeBudget.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.OFF_IMG)));
		removeBudget.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				// récupération du budget applicatif
				AppBudget appB = _table.getSelectionModel().getSelectedItems().get(0);
				// suppression
				try {
					BudgetManager.getInstance().desactivateBudget(appB);
				} catch (ComptaException e) {
					ExceptionDisplayService.showException(e);
				}
			}
		});
		// on désactive le menu si la selection est vide
		removeBudget.disableProperty().bind(Bindings.isEmpty(_table.getSelectionModel().getSelectedItems()));
		menu.getItems().add(removeBudget);

	}

}
