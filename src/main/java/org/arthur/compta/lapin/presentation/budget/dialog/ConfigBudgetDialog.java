package org.arthur.compta.lapin.presentation.budget.dialog;

import java.util.Comparator;
import java.util.Optional;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.BudgetManager;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.dataaccess.db.DBManager;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
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

	/** Les budgets actifs ordonnés */
	private ObservableList<AppBudget> _activesBudgets;
	/** Listview des budgets actifs */
	private ListView<AppBudget> list;
	/** la liste des label existant */
	private ObservableList<String> _existingLabels;

	public ConfigBudgetDialog() {
		super(ConfigBudgetDialog.class.getSimpleName());

		setTitle("Gestion des budgets");

		// récupération des valeurs
		initValues();
		// création IHM
		createContent();

		setResultConverter(new Callback<ButtonType, ButtonData>() {

			@Override
			public ButtonData call(ButtonType param) {

				if (param == _buttonTypeOk) {

					// on modifie l'ordre des budgets
					for (int i = 0; i < _activesBudgets.size(); i++) {

						_activesBudgets.get(i).setPriority(i);

					}

					// ordre modifié, on demande au manager de retrier sa liste
					BudgetManager.getInstance().calculateData();
					// on sauve les budgets
					try {
						BudgetManager.getInstance().updateBudgets(_activesBudgets);
					} catch (ComptaException e) {
						ExceptionDisplayService.showException(e);
					}

				}

				return param.getButtonData();
			}
		});

	}

	private void initValues() {

		// récupération des budgets actifs
		_activesBudgets = FXCollections.observableArrayList();
		_activesBudgets.addAll(BudgetManager.getInstance().getBudgetList());
		_activesBudgets.sort(new Comparator<AppBudget>() {

			@Override
			public int compare(AppBudget o1, AppBudget o2) {

				return Integer.compare(o1.getPriority(), o2.getPriority());
			}
		});

		// récupération des labels existant
		_existingLabels = FXCollections.observableArrayList();
		try {
			_existingLabels.addAll(BudgetManager.getInstance().getLabelRecurrentList());
		} catch (ComptaException e) {
			ExceptionDisplayService.showException(e);
		}
		_existingLabels.sort(null);

	}

	private void createContent() {

		GridPane root = new GridPane();
		root.setHgap(5);
		getDialogPane().setContent(root);

		RowConstraints rowCons = new RowConstraints();
		rowCons.setFillHeight(true);
		rowCons.setVgrow(Priority.ALWAYS);
		root.getRowConstraints().add(rowCons);

		// zone ré-organisation
		root.add(createReOrg(), 0, 0);
		// zonne des labels récurrent
		root.add(createLabelRecurrent(), 1, 0);

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
	private Node createLabelRecurrent() {

		GridPane subRoot = new GridPane();
		subRoot.setHgap(5);
		subRoot.setVgap(5);

		TitledPane borPa = new TitledPane("Budget Récurrent", subRoot);
		borPa.setCollapsible(false);
		borPa.setMaxHeight(Double.MAX_VALUE);

		ListView<String> _listV = new ListView<>();
		_listV.setItems(_existingLabels);

		subRoot.add(_listV, 0, 0);

		Button _addButton = new Button("Ajouter");
		_addButton.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.ADD_IMG)));
		subRoot.add(_addButton, 0, 1);

		// sur le bouton ajouter
		_addButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("Veuillez saisir le label");
				dialog.setContentText("Label récurrent :");
				dialog.setHeaderText(null);

				dialog.getEditor().textProperty().addListener(new ChangeListener<String>() {

					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {

						if (_existingLabels.contains(newValue)) {

							dialog.getEditor().setBorder(BORDER_ERROR);
							dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);

						} else {
							dialog.getEditor().setBorder(null);
							dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
						}

					}
				});

				Optional<String> result = dialog.showAndWait();

				if (result.isPresent()) {

					try {
						BudgetManager.getInstance().addLabelRecurrent(result.get());
						_existingLabels.add(result.get());
					} catch (ComptaException e) {
						ExceptionDisplayService.showException(e);
					}
				}

			}
		});

		return borPa;
	}

	/**
	 * Création des boutons
	 */
	protected void createButtonBar() {
		super.createButtonBar();
		ButtonType close = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(close);

	}

}
