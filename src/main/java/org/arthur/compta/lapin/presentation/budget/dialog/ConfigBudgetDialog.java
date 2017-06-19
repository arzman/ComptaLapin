package org.arthur.compta.lapin.presentation.budget.dialog;

import java.util.Iterator;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.BudgetManager;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.dataaccess.db.DBManager;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Callback;

/**
 * Fenêtre permettant de gérer les budgets : les supprimer et modifier leur
 * ordre
 *
 */
public class ConfigBudgetDialog extends ComptaDialog<ButtonData> {

	/** Le bouton OK */
	private ButtonType _buttonTypeOk;
	/** Les budgets actifs ordonnés */
	private ObservableList<AppBudget> _activesBudgets;
	/** Listview des budgets actids */
	private ListView<AppBudget> list;
	/** Tous les budgets */
	private ObservableList<AppBudget> _allBudgets;

	public ConfigBudgetDialog() {
		super(ConfigBudgetDialog.class.getSimpleName());

		setTitle("Gestion des budgets");

		GridPane root = new GridPane();
		root.setHgap(5);
		getDialogPane().setContent(root);

		RowConstraints rowCons = new RowConstraints();
		rowCons.setFillHeight(true);
		rowCons.setVgrow(Priority.ALWAYS);
		root.getRowConstraints().add(rowCons);

		// récupération des budgets actifs
		_activesBudgets = FXCollections.observableArrayList();
		_activesBudgets.addAll(BudgetManager.getInstance().getBudgetList());

		// récupérations de tout les budgets
		_allBudgets = FXCollections.observableArrayList();
		try {
			_allBudgets.addAll(BudgetManager.getInstance().getAllBudgets());
		} catch (ComptaException e) {
			ExceptionDisplayService.showException(e);
		}

		// zone ré-organisation
		root.add(createReOrg(), 0, 0);
		// de suppression
		root.add(createSuppr(), 1, 0);

		// bouton Ok et Fermer
		createButtonBar();

		setResultConverter(new Callback<ButtonType, ButtonData>() {

			@Override
			public ButtonData call(ButtonType param) {

				if (param == _buttonTypeOk) {

					// on modifie l'ordre des budgets
					for (int i = 0; i < _activesBudgets.size(); i++) {

						_activesBudgets.get(i).setPriority(i);

					}

					// ordre modifié, on demande au manager de retrier sa liste
					BudgetManager.getInstance().pleaseSort();
					// on sauve les budgets
					try {
						DBManager.getInstance().updateBudgets(_activesBudgets);
					} catch (ComptaException e) {
						ExceptionDisplayService.showException(e);
					}

				}

				return param.getButtonData();
			}
		});

	}

	/**
	 * Crée la zone permettant la ré-organisation des budgets
	 * 
	 * @return
	 */
	private Node createReOrg() {

		GridPane subRoot = new GridPane();
		subRoot.setHgap(5);
		subRoot.setVgap(5);

		// la liste des budgets
		list = new ListView<AppBudget>();

		subRoot.add(list, 0, 0, 3, 1);

		// bouton pour monter le budgets
		Button upButt = new Button();
		upButt.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.UP_IMG)));
		subRoot.add(upButt, 0, 1);
		upButt.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				int index = list.getSelectionModel().getSelectedIndex();
				if (index > 0) {
					AppBudget appB = list.getSelectionModel().getSelectedItem();

					list.getItems().remove(appB);
					list.getItems().add(index - 1, appB);
					list.getSelectionModel().select(index - 1);
				}

			}
		});

		// bouton pour descendre les budgets
		Button downButt = new Button();
		downButt.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.DOWN_IMG)));
		subRoot.add(downButt, 1, 1);
		downButt.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				int index = list.getSelectionModel().getSelectedIndex();
				if (index < _activesBudgets.size() - 1) {

					AppBudget appB = list.getSelectionModel().getSelectedItem();

					list.getItems().remove(appB);
					list.getItems().add(index + 1, appB);
					list.getSelectionModel().select(index + 1);
				}

			}
		});

		list.setItems(_activesBudgets);

		TitledPane borPa = new TitledPane("Ré-organisation", subRoot);
		borPa.setCollapsible(false);
		borPa.setMaxHeight(Double.MAX_VALUE);

		return borPa;

	}

	/**
	 * Crée la liste permettant de visualiser et supprimer les budgets
	 * 
	 * @return
	 */
	private Node createSuppr() {

		GridPane subRoot = new GridPane();
		subRoot.setHgap(5);
		subRoot.setVgap(5);

		// la liste de tous les budgets
		ListView<AppBudget> listAllBud = new ListView<AppBudget>();
		listAllBud.setItems(_allBudgets);

		subRoot.add(listAllBud, 0, 0);

		TitledPane borPa = new TitledPane("Historique des budgets", subRoot);
		borPa.setCollapsible(false);
		borPa.setMaxHeight(Double.MAX_VALUE);

		ContextMenu menu = new ContextMenu();
		listAllBud.setContextMenu(menu);

		MenuItem delItem = new MenuItem("Supprimer");
		delItem.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.DEL_IMG)));
		delItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				AppBudget appB = listAllBud.getSelectionModel().getSelectedItem();

				try {
					BudgetManager.getInstance().removeBudget(appB);
					listAllBud.getItems().remove(appB);

					// suppression de la liste ( le remove ne marche pas)
					Iterator<AppBudget> iter = _activesBudgets.iterator();
					boolean goOn = true;
					while (iter.hasNext() && goOn) {

						AppBudget bud = iter.next();
						if (bud.getAppId().equals(appB.getAppId())) {
							iter.remove();
							goOn = false;
						}

					}

				} catch (ComptaException e) {
					ExceptionDisplayService.showException(e);
				}

			}
		});

		menu.getItems().add(delItem);

		return borPa;
	}

	/**
	 * Création des boutons
	 */
	private void createButtonBar() {
		// bouton ok
		_buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().add(_buttonTypeOk);
		ButtonType close = new ButtonType("Fermer", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(close);

	}

}
