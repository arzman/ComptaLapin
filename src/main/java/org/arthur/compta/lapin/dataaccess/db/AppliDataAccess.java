package org.arthur.compta.lapin.dataaccess.db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

public class AppliDataAccess extends ComptaDataAccess {

	public AppliDataAccess() {
		super();
	}

	/**
	 * Retourne la date de dernière vérification sauvée en base
	 * 
	 * @return
	 * @throws ComptaException Echec de la récupération
	 */
	public String getDateDerVerif() throws ComptaException {

		String res = "";

		// création de la requete
		String query = "SELECT date_verif FROM CONFIGURATION limit 1";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {
			// execution
			ResultSet queryRes = executeQuery(stmt);
			// parse du resultat
			while (queryRes.next()) {

				res = ApplicationFormatter.databaseDateFormat.format(queryRes.getDate("date_verif").toLocalDate());

			}
		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer la date de dernière vérif", e);
		}

		return res;

	}

	/**
	 * Récupère en base l'id du trimestre courant
	 * 
	 * @return les champ du compte courant
	 * @throws ComptaException Echec de la récupération
	 */
	public String getTrimestreCourantId() throws ComptaException {

		String res = "";

		// création de la requete
		String query = "SELECT ID_TRIMESTRE FROM CONFIGURATION limit 1";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {
			// execution
			ResultSet queryRes = executeQuery(stmt);
			// parse du resultat
			while (queryRes.next()) {

				res = String.valueOf(queryRes.getInt("ID_TRIMESTRE"));

			}
		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer l'id du trimestre courant", e);
		}

		return res;
	}

	/**
	 * Sauve en base la date de dernière modif
	 * 
	 * @param date
	 * @throws ComptaException Echec de l'écriture en base
	 */
	public void setDateDerVerif(LocalDate date) throws ComptaException {

		String query = "UPDATE CONFIGURATION SET date_verif=?;";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

			stmt.setDate(1, Date.valueOf(date));
			executeUpdate(stmt);
		} catch (Exception e) {
			throw new ComptaException("Impossible de mettre la date a jour", e);
		}

	}

	/**
	 * Sauvegarde l'id du trimestre courant en base
	 * 
	 * @param appId le nouvel id
	 * @throws ComptaException Echec de l'insertion
	 */
	public void setTrimestreCourant(String appId) throws ComptaException {

		// préparation de la requête
		String query = "UPDATE CONFIGURATION SET ID_TRIMESTRE=?;";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query,
				Statement.RETURN_GENERATED_KEYS)) {

			stmt.setInt(1, Integer.parseInt(appId));
			executeUpdate(stmt);

			if (stmt.getUpdateCount() == 0) {
				// pas d'update, on insert
				String query2 = "INSERT INTO CONFIGURATION (date_verif,ID_TRIMESTRE) VALUES (?,?);";
				try (PreparedStatement stmt2 = DBManager.getInstance().getConnexion().prepareStatement(query2)) {
					stmt2.setDate(1, Date.valueOf(LocalDate.now()));
					stmt2.setInt(2, Integer.parseInt(appId));
					executeUpdate(stmt2);
				} catch (Exception e) {
					throw new ComptaException("Impossible d'insérer la nouvelle configuration en base", e);
				}
			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de mettre à jour la nouvelle configuration en base", e);
		}

	}

}
