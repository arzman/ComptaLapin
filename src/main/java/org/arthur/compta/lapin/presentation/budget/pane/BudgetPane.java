package org.arthur.compta.lapin.presentation.budget.pane;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.BudgetManager;
import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.presentation.common.cellfactory.MontantCellFactory;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

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

		// Création du tableau des budgets
		createBudgetTable();

		// restitution des largeur de colonnes
		for (TableColumn<?, ?> col : _table.getColumns()) {

			col.widthProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					ConfigurationManager.getInstance().setProp("BudgetPane.table.col." + col.getId(),
							String.valueOf(newValue));

				}
			});
			col.setPrefWidth(Double.parseDouble(
					ConfigurationManager.getInstance().getProp("BudgetPane.table.col." + col.getId(), "50")));
		}

	}

	/**
	 * Création du tableau des comptes
	 */
	private void createBudgetTable() {

		// Création de la table des comptes
		_table = new TableView<>();
		_table.setMaxWidth(Double.MAX_VALUE);
		add(_table, 0, 0);

		// Colonne du nom
		TableColumn<AppBudget, String> colNom = new TableColumn<>("Nom");
		colNom.setResizable(true);
		colNom.setEditable(false);
		colNom.setSortable(true);
		colNom.setId("nom");
		// bind sur la nom
		colNom.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
		_table.getColumns().add(colNom);

		// Colonne de l'objectif
		TableColumn<AppBudget, Number> colObj = new TableColumn<>("Objectif");
		colObj.setResizable(true);
		colObj.setEditable(false);
		colObj.setSortable(true);
		colObj.setCellValueFactory(cellData -> cellData.getValue().objectifProperty());
		colObj.setCellFactory(new MontantCellFactory<AppBudget>());
		colObj.setId("obj");
		_table.getColumns().add(colObj);

		// Colonne de l'avancement
		TableColumn<AppBudget, Double> colAv = new TableColumn<>("Avancement");
		colAv.setResizable(true);
		colAv.setEditable(false);
		colAv.setId("avance");
		// binding crado avec le nom de la propriété
		colAv.setCellValueFactory(new PropertyValueFactory<AppBudget, Double>("avancement"));
		colAv.setCellFactory(ProgressBarTableCell.<AppBudget>forTableColumn());
		_table.getColumns().add(colAv);

		// Colonne du solde prevu à la fin du 2eme mois
		TableColumn<AppBudget, Number> colprev2 = new TableColumn<>("CC");
		colprev2.setResizable(true);
		colprev2.setEditable(false);
		colprev2.setId("prev2");
		colprev2.setCellValueFactory(cellData -> cellData.getValue().montantCourantProperty());
		colprev2.setCellFactory(new MontantCellFactory<AppBudget>());
		_table.getColumns().add(colprev2);

		// Colonne du solde prévu à la fin du 3eme mois
		TableColumn<AppBudget, Number> colprev3 = new TableColumn<>("CL");
		colprev3.setResizable(true);
		colprev3.setEditable(false);
		colprev3.setId("prev3");
		colprev3.setCellValueFactory(cellData -> cellData.getValue().montantLivretProperty());
		colprev3.setCellFactory(new MontantCellFactory<AppBudget>());
		_table.getColumns().add(colprev3);

		// bind à la liste des comptes
		_table.setItems(BudgetManager.getInstance().getBudgetList());

		// ajout du menu contextuel
		_table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		createContextMenu();

	}

	/**
	 * Création du menu contexuel sur le tableau des budgets
	 * 
	 * @param table
	 *            le tableau des comptes
	 */
	private void createContextMenu() {

		// le menu contextuel
		final ContextMenu menu = new ContextMenu();
		_table.setContextMenu(menu);

		// action de suppression des budgets
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
