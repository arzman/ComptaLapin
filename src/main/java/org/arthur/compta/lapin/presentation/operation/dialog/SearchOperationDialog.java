package org.arthur.compta.lapin.presentation.operation.dialog;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.OperationSearchResult;
import org.arthur.compta.lapin.application.service.OperationService;
import org.arthur.compta.lapin.presentation.common.ComptaDialog;
import org.arthur.compta.lapin.presentation.common.cellfactory.MoisCellFactory;
import org.arthur.compta.lapin.presentation.common.cellfactory.MontantCellFactory;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
 * Fenetre de recherche d'opération
 *
 */
public class SearchOperationDialog extends ComptaDialog<String> {

	/** champ de saisie du nom */
	private TextField _libTxt;
	/** champ de saisie du montant */
	private TextField _montantTxt;
	/** champ de saisie du montant */
	private TextField _toleranceTxt;
	/** tout les champs sont vide */
	private boolean _allEmpty;

	/** la liste des résultats de la recherche private */
	ObservableList<OperationSearchResult> _resultatList;
	/** Le bouton de recherche */
	private Button searchBut;

	/** La bordure rouge en cas d'erreur de saisi */
	private final Border BORDER_ERROR = new Border(
			new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1)));

	public SearchOperationDialog() {

		super(SearchOperationDialog.class.getSimpleName());
		setTitle("Recherche une opération");
		_allEmpty = true;

		_resultatList = FXCollections.observableArrayList();

		GridPane root = new GridPane();
		root.setVgap(15);
		ColumnConstraints colCons = new ColumnConstraints();
		colCons.setFillWidth(true);
		colCons.setHgrow(Priority.ALWAYS);
		root.getColumnConstraints().add(colCons);

		getDialogPane().setContent(root);

		// création des zones de saisie des critères de recherche
		root.add(createCriteriaFields(), 0, 0);
		// créations de la zone de résultat
		root.add(createSearchRes(), 0, 1);
		// création des buttons
		createButtonBar();

		// ajout des écouteur
		hookListener();
		searchBut.setDefaultButton(true);

	}

	/**
	 * Ajout de la vérification de la saisie
	 */
	private void hookListener() {

		_libTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});

		_montantTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});

		_toleranceTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			checkInput();
		});

	}

	/**
	 * Vérification de la saisie
	 */
	private void checkInput() {

		boolean montOk = true;
		if (!_montantTxt.getText().trim().isEmpty()) {
			try {
				Double.parseDouble(_montantTxt.getText().trim());
				_montantTxt.setBorder(null);
			} catch (Exception e) {
				montOk = false;
				_montantTxt.setBorder(BORDER_ERROR);
			}
		}

		boolean tolOk = true;
		if (!_toleranceTxt.getText().trim().isEmpty()) {
			try {
				Double.parseDouble(_toleranceTxt.getText().trim());
				_toleranceTxt.setBorder(null);
			} catch (Exception e) {
				tolOk = false;
				_toleranceTxt.setBorder(BORDER_ERROR);
			}
		}

		_allEmpty = _montantTxt.getText().trim().isEmpty() && _libTxt.getText().trim().isEmpty()
				&& _toleranceTxt.getText().trim().isEmpty();

		searchBut.setDisable(!montOk && !tolOk);

	}

	/**
	 * Création des champs de recherche
	 */
	private Node createCriteriaFields() {

		GridPane subRoot = new GridPane();

		// saisie du nom
		Label nomLdl = new Label("Libellé contient : ");
		subRoot.add(nomLdl, 0, 0);
		_libTxt = new TextField();
		subRoot.add(_libTxt, 1, 0);

		// saisie du montant
		Label montantLdl = new Label("Montant égale : ");
		subRoot.add(montantLdl, 0, 1);
		_montantTxt = new TextField();
		subRoot.add(_montantTxt, 1, 1);

		// saisie de la tolérance du montant
		Label tolLbl = new Label(" + / - : ");
		subRoot.add(tolLbl, 0, 2);
		_toleranceTxt = new TextField();
		subRoot.add(_toleranceTxt, 1, 2);

		return subRoot;

	}

	/**
	 * Crée la zone d'affichage des résultats d'un recherche d'opération s
	 */
	private Node createSearchRes() {

		GridPane subRoot = new GridPane();
		subRoot.setVgap(5);

		ColumnConstraints colCons = new ColumnConstraints();
		colCons.setFillWidth(true);
		colCons.setHgrow(Priority.ALWAYS);

		subRoot.getColumnConstraints().add(colCons);

		// le bouton de recherche
		searchBut = new Button("Rechercher");
		searchBut.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				boolean doSearch = true;

				if (_allEmpty) {

					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Confirmer la recherche");
					alert.setHeaderText(
							"Attention, les champs de recherche sont vides, l'intégralité des opérations sera remontée");
					alert.setContentText("Voulez-vous continuer ?");

					Optional<ButtonType> res = alert.showAndWait();

					if (res.isPresent()) {

						if (res.get() == ButtonType.CANCEL) {
							doSearch = false;
						}

					} else {
						doSearch = false;
					}

				}

				if (doSearch) {

					_resultatList.clear();
					try {
						List<OperationSearchResult> list = OperationService.doSearch(_libTxt.getText(),
								_montantTxt.getText(), _toleranceTxt.getText());
						_resultatList.addAll(list);
					} catch (ComptaException e) {
						ExceptionDisplayService.showException(e);
					}

				}

			}
		});
		subRoot.add(searchBut, 0, 0);

		// le tableau de résultat
		TableView<OperationSearchResult> _resTable = new TableView<>();
		_resTable.setItems(_resultatList);

		// libellé
		TableColumn<OperationSearchResult, String> colLib = new TableColumn<OperationSearchResult, String>();
		colLib.setText("Libellé");
		colLib.setResizable(true);
		colLib.setSortable(true);
		colLib.setCellValueFactory(cellData -> cellData.getValue().libelleProperty());

		// montant
		TableColumn<OperationSearchResult, Number> colMontant = new TableColumn<OperationSearchResult, Number>();
		colMontant.setText("Montant");
		colMontant.setResizable(true);
		colMontant.setSortable(true);
		colMontant.setCellValueFactory(cellData -> cellData.getValue().montantProperty());
		colMontant.setCellFactory(new MontantCellFactory<OperationSearchResult>());

		// date
		TableColumn<OperationSearchResult, Calendar> colDate = new TableColumn<OperationSearchResult, Calendar>();
		colDate.setText("Mois");
		colDate.setResizable(true);
		colDate.setSortable(true);
		colDate.setCellValueFactory(cellData -> cellData.getValue().getMoisProperty());
		colDate.setCellFactory(new MoisCellFactory<OperationSearchResult>());

		// ajout des colonnes
		_resTable.getColumns().add(colLib);
		_resTable.getColumns().add(colMontant);
		_resTable.getColumns().add(colDate);

		subRoot.add(_resTable, 0, 1);

		return subRoot;

	}

	/**
	 * Création des boutons
	 */
	private void createButtonBar() {
		// bouton ok
		ButtonType okButton = new ButtonType("Fermer", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(okButton);

	}

}
