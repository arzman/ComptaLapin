package org.arthur.compta.lapin.dataaccess.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.arthur.compta.lapin.application.exception.ComptaException;

/**
 * Service de mise à jour de la base. Ce service centralise les modifications du
 * schéma depuis la v1 du schéma de la base ie : on enchaine les modif du schéma
 * de la base
 *
 */
public class DBUpdateService {

	public static void checkUpdate(Connection _connexionDB) throws ComptaException {

		// ajout des champs label_recurrent et date_recurrent dans BUDGET
		String query = "SELECT COLUMN_NAME as c FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME='BUDGET';";
		try (PreparedStatement stmt = _connexionDB.prepareStatement(query)) {

			ArrayList<String> tmp = new ArrayList<>();
			ResultSet toto = stmt.executeQuery();
			while (toto.next()) {

				tmp.add(toto.getString(1));

			}

			if (!tmp.contains("LABEL_RECURRENT")) {

				query = "ALTER TABLE BUDGET ADD COLUMN label_recurrent VARCHAR (25) DEFAULT '' NOT NULL  ;";
				try (PreparedStatement stmt2 = _connexionDB.prepareStatement(query)) {

					stmt2.executeUpdate();

				} catch (Exception e) {
					throw new ComptaException("Impossible d'ajouter la colonne label_recurrent", e);
				}

			}

			if (!tmp.contains("DATE_RECURRENT")) {

				query = "ALTER TABLE BUDGET ADD COLUMN  date_recurrent DATE DEFAULT '1986-06-27' NOT NULL  ;";
				try (PreparedStatement stmt2 = _connexionDB.prepareStatement(query)) {

					stmt2.executeUpdate();

				} catch (Exception e) {
					throw new ComptaException("Impossible d'ajouter la colonne DATE_RECURRENT", e);
				}

			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer la table BUDGET", e);
		}

		// ajout de la table LABEL_BUDGET_RECURRENT
		query = "SELECT count(*) as c FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME='LABEL_BUDGET_RECURRENT';";
		try (PreparedStatement stmt = _connexionDB.prepareStatement(query)) {

			ResultSet toto = stmt.executeQuery();

			if (toto.next() && toto.getInt("c") != 1) {

				query = "CREATE TABLE LABEL_BUDGET_RECURRENT( label VARCHAR (25) NOT NULL);";
				try (PreparedStatement stmt2 = _connexionDB.prepareStatement(query)) {

					stmt2.executeUpdate();

				} catch (Exception e) {
					throw new ComptaException("Impossible d'ajouter la table LABEL_BUDGET_RECURRENT", e);
				}
			}
		} catch (SQLException e1) {
			throw new ComptaException("Impossible de récupérer la table LABEL_BUDGET_RECURRENT", e1);
		}

	}

}
