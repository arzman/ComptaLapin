package org.arthur.compta.lapin.presentation.budget.dialog;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.BudgetManager;
import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.presentation.budget.cellfactory.MontantPresBudgetCellFactory;
import org.arthur.compta.lapin.presentation.budget.cellfactory.NomBudgetTableCell;
import org.arthur.compta.lapin.presentation.budget.model.PresBudget;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.common.cellfactory.DateTreeCellFactory;
import org.arthur.compta.lapin.presentation.common.cellfactory.MontantCellFactory;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;
import org.arthur.compta.lapin.presentation.utils.PresBudgetSorter;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class VisuBudgetDialog extends ComptaDialog<ButtonData> {

	private ObservableList<AppBudget> _recurrentBudget;

	private ObservableList<AppBudget> _otherBudget;

	private TableView<AppBudget> _tableNoRecuBud;

	private TreeTableView<PresBudget> _treeRecBud;

	private final PresBudgetSorter _presbudgetSorter;

	public VisuBudgetDialog() {
		super(VisuBudgetDialog.class.getSimpleName());

		setTitle("Visualiser les budgets");

		_presbudgetSorter = new PresBudgetSorter();

		// initialisation des valeurs
		initValues();
		// creation du contenu
		createContent();

	}

	private void createContent() {

		GridPane root = new GridPane();
		root.setHgap(5);
		getDialogPane().setContent(root);

		RowConstraints rowCons = new RowConstraints();
		rowCons.setFillHeight(true);
		rowCons.setVgrow(Priority.ALWAYS);
		root.getRowConstraints().add(rowCons);

		ColumnConstraints colCons = new ColumnConstraints();
		colCons.setFillWidth(true);
		colCons.setHgrow(Priority.ALWAYS);
		root.getColumnConstraints().addAll(colCons, colCons);

		// budget non-récurent
		createTableOtherBud();
		root.add(_tableNoRecuBud, 0, 0);

		createTableBudRec();
		root.add(_treeRecBud, 1, 0);

	}

	private void createTableBudRec() {

		_treeRecBud = new TreeTableView<>(getTreeRoot());
		TreeTableColumn<PresBudget, String> colNom = new TreeTableColumn<PresBudget, String>("Nom");
		colNom.setId("nom");
		colNom.setResizable(true);
		colNom.setCellValueFactory(cellData -> cellData.getValue().getValue().nomProperty());
		_treeRecBud.getColumns().add(colNom);

		TreeTableColumn<PresBudget, Number> montNom = new TreeTableColumn<PresBudget, Number>("Montant");
		montNom.setId("montant");
		montNom.setResizable(true);
		montNom.setCellValueFactory(cellData -> cellData.getValue().getValue().montantProperty());
		montNom.setCellFactory(new MontantPresBudgetCellFactory());
		_treeRecBud.getColumns().add(montNom);

		TreeTableColumn<PresBudget, LocalDate> colDate = new TreeTableColumn<PresBudget, LocalDate>("Montant");
		colDate.setId("date");
		colDate.setResizable(true);
		colDate.setCellValueFactory(cellData -> cellData.getValue().getValue().dateRecurrentProp());
		colDate.setCellFactory(new DateTreeCellFactory<PresBudget>());
		_treeRecBud.getColumns().add(colDate);

		ConfigurationManager.getInstance().setPrefColumnWidth(_treeRecBud, "VisiBudgetDialog.treeRecBud");

		ContextMenu menu = new ContextMenu();
		_treeRecBud.setContextMenu(menu);

		MenuItem delItem = new MenuItem("Supprimer");
		delItem.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.DEL_IMG)));
		delItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				TreeItem<PresBudget> item = _treeRecBud.getSelectionModel().getSelectedItem();

				try {
					if (item.getValue().getAppBudget() != null) {
						BudgetManager.getInstance().removeBudget(item.getValue().getAppBudget());
						item.getParent().getChildren().remove(item);

						// suppression de la liste ( le remove ne marche pas)
						Iterator<AppBudget> iter = _recurrentBudget.iterator();
						boolean goOn = true;
						while (iter.hasNext() && goOn) {

							AppBudget bud = iter.next();
							if (bud.getAppId().equals(item.getValue().getAppBudget().getAppId())) {
								iter.remove();
								goOn = false;
							}

						}
					}

				} catch (ComptaException e) {
					ExceptionDisplayService.showException(e);

				}

			}
		});
		menu.getItems().add(delItem);
		delItem.disableProperty().bind(_treeRecBud.getSelectionModel().selectedItemProperty().isNull());

	}

	/**
	 * Création du tableau des budgets non-récurrent
	 */
	private void createTableOtherBud() {

		// Création de la table des comptes
		_tableNoRecuBud = new TableView<>();
		_tableNoRecuBud.setMaxWidth(Double.MAX_VALUE);
		_tableNoRecuBud.setMaxHeight(Double.MAX_VALUE);

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
		_tableNoRecuBud.getColumns().add(colNom);

		// Colonne de l'objectif
		TableColumn<AppBudget, Number> colObj = new TableColumn<>("Objectif");
		colObj.setResizable(true);
		colObj.setEditable(false);
		colObj.setCellValueFactory(cellData -> cellData.getValue().objectifProperty());
		colObj.setCellFactory(new MontantCellFactory<AppBudget>());
		colObj.setId("obj");

		_tableNoRecuBud.getColumns().add(colObj);
		_tableNoRecuBud.setItems(_otherBudget);

		ConfigurationManager.getInstance().setPrefColumnWidth(_tableNoRecuBud, "VisiBudgetDialog.tableNoRecuBud");

		ContextMenu menu = new ContextMenu();
		_tableNoRecuBud.setContextMenu(menu);

		MenuItem delItem = new MenuItem("Supprimer");
		delItem.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.DEL_IMG)));
		delItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				AppBudget item = _tableNoRecuBud.getSelectionModel().getSelectedItem();

				try {
					if (item != null) {
						BudgetManager.getInstance().removeBudget(item);
						_tableNoRecuBud.getItems().remove(item);

						// suppression de la liste ( le remove ne marche pas)
						Iterator<AppBudget> iter = _otherBudget.iterator();
						boolean goOn = true;
						while (iter.hasNext() && goOn) {

							AppBudget bud = iter.next();
							if (bud.getAppId().equals(item.getAppId())) {
								iter.remove();
								goOn = false;
							}

						}
					}

				} catch (ComptaException e) {
					ExceptionDisplayService.showException(e);

				}

			}
		});
		menu.getItems().add(delItem);
		delItem.disableProperty().bind(_tableNoRecuBud.getSelectionModel().selectedItemProperty().isNull());
		
		

	}

	private TreeItem<PresBudget> getTreeRoot() {

		TreeItem<PresBudget> root = new TreeItem<>();
		PresBudget pbr = new PresBudget(null, "Tous");
		root.setExpanded(true);
		root.setValue(pbr);

		// récupération des labels récurents
		HashMap<String, TreeItem<PresBudget>> tmp = new HashMap<String, TreeItem<PresBudget>>();

		try {
			for (String label : BudgetManager.getInstance().getLabelRecurrentList()) {

				TreeItem<PresBudget> lblItem = new TreeItem<PresBudget>(new PresBudget(null, label));

				root.getChildren().add(lblItem);
				tmp.put(label, lblItem);

			}
		} catch (ComptaException e) {
			ExceptionDisplayService.showException(e);
		}

		root.getChildren().sort(_presbudgetSorter);

		// placement des budgets
		for (AppBudget appBud : _recurrentBudget) {
			tmp.get(appBud.getLabelRecurrent()).getChildren().add(new TreeItem<PresBudget>(new PresBudget(appBud, "")));
		}

		for (TreeItem<PresBudget> treei : tmp.values()) {
			treei.getChildren().sort(_presbudgetSorter);
		}

		return root;
	}

	/**
	 * Initialisation des valeurs
	 */
	private void initValues() {

		// récupération des budgets
		_recurrentBudget = FXCollections.observableArrayList();
		_otherBudget = FXCollections.observableArrayList();

		try {

			for (AppBudget appBud : BudgetManager.getInstance().getAllBudgets()) {

				if (appBud.getLabelRecurrent().isEmpty()) {
					_otherBudget.add(appBud);
				} else {
					_recurrentBudget.add(appBud);
				}

			}
		} catch (ComptaException e) {
			ExceptionDisplayService.showException(e);
		}

	}

//	
//
//
//	ContextMenu menu = new ContextMenu();
//	listAllBud.setContextMenu(menu);
//
//	MenuItem delItem = new MenuItem("Supprimer");
//	delItem.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.DEL_IMG)));
//	delItem.setOnAction(new EventHandler<ActionEvent>() {
//
//		@Override
//		public void handle(ActionEvent event) {
//
//			AppBudget appB = listAllBud.getSelectionModel().getSelectedItem();
//
//			try {
//				BudgetManager.getInstance().removeBudget(appB);
//				listAllBud.getItems().remove(appB);
//
//				// suppression de la liste ( le remove ne marche pas)
//				Iterator<AppBudget> iter = _activesBudgets.iterator();
//				boolean goOn = true;
//				while (iter.hasNext() && goOn) {
//
//					AppBudget bud = iter.next();
//					if (bud.getAppId().equals(appB.getAppId())) {
//						iter.remove();
//						goOn = false;
//					}
//
//				}
//
//			} catch (ComptaException e) {
//				ExceptionDisplayService.showException(e);
//			}
//
//		}
//	});
//
//menu.getItems().add(delItem);

}
