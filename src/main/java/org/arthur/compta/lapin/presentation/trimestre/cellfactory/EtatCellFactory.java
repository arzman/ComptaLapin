package org.arthur.compta.lapin.presentation.trimestre.cellfactory;

import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 * Classe permettant l'affichage d'un V vert dans une table d'opération lorsque
 * celle-ci est à l'état PRISE_EN_COMPTE
 *
 * @param <T>
 *            Sous-classe de l'opération
 */
public class EtatCellFactory<T extends AppOperation> implements Callback<TableColumn<T, EtatOperation>, TableCell<T, EtatOperation>> {

	@Override
	public TableCell<T, EtatOperation> call(TableColumn<T, EtatOperation> param) {

		TableCell<T, EtatOperation> cell = new TableCell<T, EtatOperation>() {

			@Override
			protected void updateItem(EtatOperation item, boolean empty) {

				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setGraphic(null);
				} else {
					// on affiche son nom
					setText("");

					if (item.equals(EtatOperation.PRISE_EN_COMPTE)) {

						ImageView im = new ImageView(ImageLoader.getImage(ImageLoader.VALID_IMG));
						setGraphic(im);

					} else {
						setGraphic(null);
					}

				}
			}

		};

		return cell;
	}

}
