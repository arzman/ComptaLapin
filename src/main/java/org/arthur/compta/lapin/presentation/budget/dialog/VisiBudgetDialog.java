package org.arthur.compta.lapin.presentation.budget.dialog;

import java.util.HashMap;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.BudgetManager;
import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.presentation.budget.cellfactory.NomBudgetTableCell;
import org.arthur.compta.lapin.presentation.budget.model.PresBudget;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.common.cellfactory.MontantCellFactory;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class VisiBudgetDialog extends ComptaDialog<ButtonData> {

	private ObservableList<AppBudget> _recurrentBudget;

	private ObservableList<AppBudget> _otherBudget;

	private TableView<AppBudget> _tableNoRecuBud;

	private TreeTableView<PresBudget> _treeRecBud;

	public VisiBudgetDialog() {
		super(VisiBudgetDialog.class.getSimpleName());

		setTitle("Visualiser les budgets");

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

		ConfigurationManager.getInstance().setPrefColumnWidth(_treeRecBud, "VisiBudgetDialog.treeRecBud");

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

	}

	private TreeItem<PresBudget> getTreeRoot() {

		TreeItem<PresBudget> root = new TreeItem<>();
		PresBudget pbr = new PresBudget(null, "Tous");
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

		// placement des budgets
		for (AppBudget appBud : _recurrentBudget) {
			tmp.get(appBud.getLabelRecurrent()).getChildren().add(new TreeItem<PresBudget>(new PresBudget(appBud, "")));
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
