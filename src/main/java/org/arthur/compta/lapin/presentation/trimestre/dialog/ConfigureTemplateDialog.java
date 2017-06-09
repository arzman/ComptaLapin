package org.arthur.compta.lapin.presentation.trimestre.dialog;

import java.util.Optional;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElement;
import org.arthur.compta.lapin.application.service.TemplateService;
import org.arthur.compta.lapin.presentation.common.cellfactory.MontantCellFactory;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;
import org.arthur.compta.lapin.presentation.trimestre.cellfactory.CompteCellFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

/**
 * Fenêtre de configuration du modèle de trimestre. Il s'agit un tableau présent
 * les différent élément du modèle et leur attribut. Lorsque la fenêtre est
 * fermée en appuyant sur "Ok", un ordre est donné au TrimestreManager pour
 * écraser le modèle sauvegardé.
 *
 */
public class ConfigureTemplateDialog extends Dialog<String> {

	/** La liste des éléments de template */
	private ObservableList<TrimestreTemplateElement> _elementList;
	/** Le tableau des éléments */
	private TableView<TrimestreTemplateElement> _table;
	/** bouton d'ajout d'element */
	private Button _addBut;

	/**
	 * Constructeur
	 */
	public ConfigureTemplateDialog() {

		_elementList = FXCollections.observableArrayList();
		_elementList.addAll(TemplateService.getTrimestreTemplate().getElements());

		setTitle("Configuration du modèle");
		setResizable(true);
		// création du contenu de la fenetre
		createContent();
		// création des boutons
		createButtonBar();

		setOnCloseRequest(new EventHandler<DialogEvent>() {

			@Override
			public void handle(DialogEvent event) {
				// sauvegarde de la taille de la fenetres
				ConfigurationManager.getInstance().setProp("ConfigureTemplateDialog.dialog.width",
						String.valueOf(getWidth()));
				ConfigurationManager.getInstance().setProp("ConfigureTemplateDialog.dialog.height",
						String.valueOf(getHeight()));
				// sauvegarde de la taille des colonnes
				for (TableColumn<?, ?> col : _table.getColumns()) {

					ConfigurationManager.getInstance().setProp("ConfigureTemplateDialog.col." + col.getId(),
							String.valueOf(col.getWidth()));
				}

			}
		});

	}

	/**
	 * Créée le contenu de la fenêtre
	 */
	private void createContent() {
		// racine des noeuds graphiques
		GridPane root = new GridPane();
		ColumnConstraints colConst = new ColumnConstraints();
		colConst.setFillWidth(true);
		colConst.setHgrow(Priority.ALWAYS);
		getDialogPane().setContent(root);
		root.getColumnConstraints().add(colConst);

		// création des actions sur la liste
		createControlButton();
		root.add(_addBut, 0, 0);

		// Tableau des élements de modèle
		createTable();
		root.add(_table, 0, 1);

		// restaure les tailles sauvegardées
		// sauvegarde de la taille de la fenetres
		setWidth(Double.parseDouble(
				ConfigurationManager.getInstance().getProp("ConfigureTemplateDialog.dialog.width", "500")));
		setHeight(Double.parseDouble(
				ConfigurationManager.getInstance().getProp("ConfigureTemplateDialog.dialog.height", "500")));
		// sauvegarde de la taille des colonnes
		for (TableColumn<?, ?> col : _table.getColumns()) {

			col.setPrefWidth(Double.parseDouble(
					ConfigurationManager.getInstance().getProp("ConfigureTemplateDialog.col." + col.getId(), "50")));
		}

	}

	/**
	 * Création des boutons ajouter et supprimer des éléments de template
	 */
	private void createControlButton() {

		_addBut = new Button("Ajouter");
		_addBut.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.ADD_IMG)));
		_addBut.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {

				// ouverture de la fenêtre de saisie
				EditTemplateEltDialog diag = new EditTemplateEltDialog(null);
				Optional<TrimestreTemplateElement> res = diag.showAndWait();

				if (res.isPresent()) {

					_elementList.add(res.get());

				}
			};
		});
	}

	/**
	 * Création du tableau
	 * 
	 * @param root
	 *            Le noeud ou doit se placer le tableau
	 */
	private void createTable() {

		_table = new TableView<>();
		_table.setItems(_elementList);

		// colonne Nom
		TableColumn<TrimestreTemplateElement, String> colNom = new TableColumn<>("Nom");
		colNom.setResizable(true);
		colNom.setEditable(false);
		colNom.setId("nom");
		colNom.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
		_table.getColumns().add(colNom);

		// colonne Montant
		TableColumn<TrimestreTemplateElement, Number> colMontant = new TableColumn<>("Montant");
		colMontant.setResizable(true);
		colMontant.setEditable(false);
		colMontant.setId("montant");
		colMontant.setCellValueFactory(cellData -> cellData.getValue().montantProperty());
		colMontant.setCellFactory(new MontantCellFactory<TrimestreTemplateElement>());
		_table.getColumns().add(colMontant);

		// colonne Type
		TableColumn<TrimestreTemplateElement, String> colType = new TableColumn<>("Type");
		colType.setResizable(true);
		colType.setEditable(false);
		colType.setId("type");
		colType.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
		_table.getColumns().add(colType);

		// colonne Fréquence
		TableColumn<TrimestreTemplateElement, String> colFreq = new TableColumn<>("Fréquence");
		colFreq.setResizable(true);
		colFreq.setEditable(false);
		colFreq.setId("freq");
		colFreq.setCellValueFactory(cellData -> cellData.getValue().frequenceProperty());
		_table.getColumns().add(colFreq);

		// colonne occurence
		TableColumn<TrimestreTemplateElement, Number> colOcc = new TableColumn<>("Occurence");
		colOcc.setResizable(true);
		colOcc.setEditable(false);
		colOcc.setId("occ");
		colOcc.setCellValueFactory(cellData -> cellData.getValue().occurenceProperty());
		_table.getColumns().add(colOcc);

		// colonne Compte Source
		TableColumn<TrimestreTemplateElement, AppCompte> colsource = new TableColumn<>("Source");
		colsource.setResizable(true);
		colsource.setEditable(false);
		colsource.setId("source");
		colsource.setCellValueFactory(cellData -> cellData.getValue().compteSourceProperty());
		colsource.setCellFactory(new CompteCellFactory<TrimestreTemplateElement>());
		_table.getColumns().add(colsource);

		// colonne Compte Cible
		TableColumn<TrimestreTemplateElement, AppCompte> colcible = new TableColumn<>("Cible");
		colcible.setResizable(true);
		colcible.setEditable(false);
		colcible.setId("cible");
		colcible.setCellValueFactory(cellData -> cellData.getValue().compteCibleProperty());
		colcible.setCellFactory(new CompteCellFactory<TrimestreTemplateElement>());
		_table.getColumns().add(colcible);
	}

	/**
	 * Création des boutons
	 */
	private void createButtonBar() {
		// bouton ok
		ButtonType okButton = new ButtonType("Ok", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().add(okButton);
		// bouton annuler
		ButtonType cancelButton = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(cancelButton);

		setResultConverter(new Callback<ButtonType, String>() {

			@Override
			public String call(ButtonType param) {

				if (param.equals(okButton)) {

					// on écrase le template en DB par le nouveau
					try {
						TemplateService.updateTrimestreTemplate(_elementList);
					} catch (ComptaException e) {
						ExceptionDisplayService.showException(e);
					}

				}

				return "lol";
			}
		});
	}

}
