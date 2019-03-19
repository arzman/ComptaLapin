package org.arthur.compta.lapin.application.model;

import java.time.LocalDate;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.model.Trimestre;

import javafx.beans.property.SimpleObjectProperty;

/**
 * Encapsulation applicative d'un trimestre
 *
 */
public class AppTrimestre extends AppObject<Trimestre> {

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
	 * Constructeur
	 * 
	 * @param trimestre
	 *            le trimestre a encapsuler
	 * @throws ComptaException
	 */
	public AppTrimestre(Trimestre trimestre) throws ComptaException {

		setAppID(trimestre.getId());

		_premierMois = new SimpleObjectProperty<AppExerciceMensuel>(TrimestreManager.getInstance().loadExerciceMensuel(trimestre.getExerciceMensuelIds()[0]));
		_deuxiemeMois = new SimpleObjectProperty<AppExerciceMensuel>(TrimestreManager.getInstance().loadExerciceMensuel(trimestre.getExerciceMensuelIds()[1]));
		_troisiemeMois = new SimpleObjectProperty<AppExerciceMensuel>(TrimestreManager.getInstance().loadExerciceMensuel(trimestre.getExerciceMensuelIds()[2]));

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
	public LocalDate getDateDebut() {

		return premierMoisProperty().get().getDateDebut();
	}

	/**
	 * Positionne un exercice mensuel applicatif par sa position. 0 = premier
	 * mois , 1 = deuxieme mois , 2 = troisieme mois
	 * 
	 * @param i
	 *            position
	 * @param appEm
	 *            l'exercice applicatif
	 */
	public void setAppExerciceMensuel(int i, AppExerciceMensuel appEm) {

		switch (i) {

		case 0:
			_premierMois.set(appEm);
			break;
		case 1:
			_deuxiemeMois.set(appEm);
			break;
		case 2:
			_troisiemeMois.set(appEm);
			break;
		default:
			break;
		}

	}

	/**
	 * Retourne l'exercice mensuel correspondant au numéro fourni
	 * 
	 * @param numMois
	 * @return
	 */
	public SimpleObjectProperty<AppExerciceMensuel> getAppExerciceMensuel(int numMois) {

		switch (numMois) {

		case 0:
			return _premierMois;
		case 1:
			return _deuxiemeMois;
		case 2:
			return _troisiemeMois;
		default:
			return null;
		}
	}

	/**
	 * Retourne la date de fin du trimestre
	 * 
	 * @return
	 */
	public LocalDate getDateFin() {
		return _troisiemeMois.get().getDateFin();
	}

	@Override
	public Trimestre getDBObject() {
		return new Trimestre(getAppId(), _premierMois.get().getAppId(), _deuxiemeMois.get().getAppId(), _troisiemeMois.get().getAppId());
	}

}
