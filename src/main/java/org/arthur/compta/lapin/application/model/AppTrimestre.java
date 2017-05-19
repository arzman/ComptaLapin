package org.arthur.compta.lapin.application.model;

import java.util.Calendar;

import org.arthur.compta.lapin.model.Trimestre;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Encapsulation applicative d'un trimestre
 *
 */
public class AppTrimestre extends AppObject {

	/**
	 * Premier mois du trimestre
	 */
	private SimpleObjectProperty<AppExerciceMensuel> _premierMois;
	/**
	 * Deuxieme mois du trimestre
	 */
	private SimpleObjectProperty<AppExerciceMensuel> _deuxiemeMois;
	/**
	 * Troisieme mois du trimestre
	 */
	private SimpleObjectProperty<AppExerciceMensuel> _troisiemeMois;

	/**
	 * le trimestre a encapsuler
	 */
	private Trimestre _trimeste;

	/**
	 * Constructeur
	 * 
	 * @param trimestre
	 *            le trimestre a encapsuler
	 */
	public AppTrimestre(Trimestre trimestre) {

		_trimeste = trimestre;

		_premierMois = new SimpleObjectProperty<AppExerciceMensuel>();
		_deuxiemeMois = new SimpleObjectProperty<AppExerciceMensuel>();
		_troisiemeMois = new SimpleObjectProperty<AppExerciceMensuel>();

		bindListener();

	}

	/**
	 * Mise en place des listeners
	 */
	private void bindListener() {

		// modification du premier mois
		_premierMois.addListener(new ChangeListener<AppExerciceMensuel>() {

			@Override
			public void changed(ObservableValue<? extends AppExerciceMensuel> observable, AppExerciceMensuel oldValue,
					AppExerciceMensuel newValue) {
				// répercution sur le modele
				_trimeste.getExerciceMensuel()[0] = newValue.getExcerciceMensuel();

			}
		});
		// modification du deuxieme mois
		_deuxiemeMois.addListener(new ChangeListener<AppExerciceMensuel>() {

			@Override
			public void changed(ObservableValue<? extends AppExerciceMensuel> observable, AppExerciceMensuel oldValue,
					AppExerciceMensuel newValue) {
				// répercution sur le modele
				_trimeste.getExerciceMensuel()[1] = newValue.getExcerciceMensuel();

			}
		});
		// modification du troisieme mois
		_troisiemeMois.addListener(new ChangeListener<AppExerciceMensuel>() {

			@Override
			public void changed(ObservableValue<? extends AppExerciceMensuel> observable, AppExerciceMensuel oldValue,
					AppExerciceMensuel newValue) {
				// répercution sur le modele
				_trimeste.getExerciceMensuel()[2] = newValue.getExcerciceMensuel();

			}
		});

	}

	/**
	 * Premier exercice mensuel sous forme de propriété observable
	 * 
	 * @return
	 */
	public SimpleObjectProperty<AppExerciceMensuel> premierMoisProperty() {

		return _premierMois;
	}

	/**
	 * Deuxieme exercice mensuel sous forme de propriété observable
	 * 
	 * @return
	 */
	public SimpleObjectProperty<AppExerciceMensuel> deuxiemeMoisProperty() {

		return _deuxiemeMois;
	}

	/**
	 * Troisieme exercice mensuel sous forme de propriété observable
	 * 
	 * @return
	 */
	public SimpleObjectProperty<AppExerciceMensuel> troisiemeMoisProperty() {

		return _troisiemeMois;
	}

	/**
	 * Retourne la date de début
	 * 
	 * @return
	 */
	public Calendar getDateDebut() {

		return _trimeste.getDateDebut();
	}

}
