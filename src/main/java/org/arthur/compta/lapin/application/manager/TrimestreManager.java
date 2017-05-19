package org.arthur.compta.lapin.application.manager;

import java.util.Calendar;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppExerciceMensuel;
import org.arthur.compta.lapin.application.model.AppTrimestre;
import org.arthur.compta.lapin.dataaccess.db.DBManager;
import org.arthur.compta.lapin.model.ExerciceMensuel;
import org.arthur.compta.lapin.model.Trimestre;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Gestionnaire des trimestres.
 *
 */
public class TrimestreManager {

	/**
	 * L'unique instance du singleton
	 */
	private static TrimestreManager _instance;

	private SimpleObjectProperty<AppTrimestre> _trimestreCourant;

	/**
	 * Le constructeur
	 */
	private TrimestreManager() {

		_trimestreCourant = new SimpleObjectProperty<AppTrimestre>();

		String[] info;
		try {
			info = DBManager.getInstance().getTrimestreCourantId();
			if (info != null && info.length == 1 && info[0] != null && !info[0].isEmpty()) {
				loadTrimestreCourant(info[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Retourne l'instance du singleton
	 * 
	 * @return
	 */
	public static TrimestreManager getInstance() {

		if (_instance == null) {

			_instance = new TrimestreManager();
		}
		return _instance;
	}

	/**
	 * Charge un trimestre courant
	 * 
	 * @param appId
	 *            l'id du trimestre a charger
	 * @throws ComptaException
	 *             Echec du chargement
	 */
	private void loadTrimestreCourant(String appId) throws ComptaException {

		// recup des infos en base
		String[] info;

		try {
			// chargmement du trimestre
			info = DBManager.getInstance().getTrimestreInfo(appId);

			if (info != null) {

				// création du trimestre applicatif
				AppTrimestre appTrimestre = new AppTrimestre(new Trimestre());
				appTrimestre.setAppID(info[0]);

				// chargement du 1er mois
				appTrimestre.premierMoisProperty().set(loadExerciceMensuel(info[1]));
				// chargement du 2eme mois
				appTrimestre.deuxiemeMoisProperty().set(loadExerciceMensuel(info[2]));
				// chargement du 3eme mois
				appTrimestre.troisiemeMoisProperty().set(loadExerciceMensuel(info[3]));

				_trimestreCourant.set(appTrimestre);
			}
		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer le trimestre courant",e);
		}

	}

	/**
	 * Crée un exercice mensuel applicatif depuis la base de donnée
	 * 
	 * @param id
	 *            l'id de l'exercice mensuel
	 * @return l'exercice mensuel
	 * @throws ComptaException
	 */
	private AppExerciceMensuel loadExerciceMensuel(String id) throws ComptaException {

		AppExerciceMensuel appEm = null;

		try {

			if (id != null && !id.isEmpty()) {

				// récupération en base des donnée de l'exercice
				String[] infos = DBManager.getInstance().getExMensuelInfos(id);
				ExerciceMensuel em = new ExerciceMensuel();

				// date de début
				Calendar deb = Calendar.getInstance();
				deb.setTime(ApplicationFormatter.databaseDateFormat.parse(infos[1]));
				em.setDateDebut(deb);

				appEm = new AppExerciceMensuel(em);
				appEm.setAppID(infos[0]);

			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de charger l'exercice mensuel", e);
		}

		return appEm;
	}

	/**
	 * Retourne le trimestre courant sous forme de propriété
	 * 
	 * @return
	 */
	public ObjectProperty<AppTrimestre> trimestreCourantProperty() {
		return _trimestreCourant;

	}

	/**
	 * Crée un trimestre applicatif
	 * 
	 * @param dateDeb
	 * @return
	 * @throws ComptaException
	 */
	public AppTrimestre createTrimestre(Calendar dateDeb) throws ComptaException {

		AppTrimestre appTrim = null;

		try {

			Trimestre trim = new Trimestre();
			appTrim = new AppTrimestre(trim);

			final int numMoi = dateDeb.get(Calendar.MONTH);

			// création des exercice mensuel du trimestre
			for (int i = 0; i < 3; i++) {

				final ExerciceMensuel em = new ExerciceMensuel();
				// date de debut
				final Calendar debut = Calendar.getInstance();
				debut.set(Calendar.DAY_OF_MONTH, 1);
				debut.set(Calendar.MONTH, (i + numMoi) % 12);
				debut.set(Calendar.YEAR, dateDeb.get(Calendar.YEAR) + ((i + numMoi) / 12));
				em.setDateDebut(debut);
				// date de fin
				final Calendar fin = Calendar.getInstance();
				fin.set(Calendar.DAY_OF_MONTH, debut.getActualMaximum(Calendar.DAY_OF_MONTH));
				fin.set(Calendar.MONTH, (i + numMoi) % 12);
				fin.set(Calendar.YEAR, dateDeb.get(Calendar.YEAR) + ((i + numMoi) / 12));
				em.setDateFin(fin);
				
				// insertion de l'exercice mensuel en base
				String idEm = DBManager.getInstance().addExerciceMensuel(debut, fin);
				// création de l'exercice applicatif
				AppExerciceMensuel appEm = new AppExerciceMensuel(em);
				appEm.setAppID(idEm);

				appTrim.setAppExerciceMensuel(i, appEm);

			}

			// insertion du trimestre en base
			String idTrim = DBManager.getInstance().addTrimestre(appTrim.premierMoisProperty().get().getAppId(),
					appTrim.deuxiemeMoisProperty().get().getAppId(), appTrim.troisiemeMoisProperty().get().getAppId());

			appTrim.setAppID(idTrim);

		} catch (Exception e) {
			throw new ComptaException("Impossible de créer le trimestre", e);
		}

		return appTrim;
	}

}
