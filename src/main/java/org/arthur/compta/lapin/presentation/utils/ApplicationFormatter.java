package org.arthur.compta.lapin.presentation.utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Fourni les différent formatteur utilisé pour la présentation
 *
 */
public class ApplicationFormatter {

	/** Formateur de monnaie */
	public static NumberFormat montantFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);
	/** Formateur "pourcent" */
	public static NumberFormat pourcentFormat = NumberFormat.getPercentInstance(Locale.FRANCE);
	/** Formateur moi/année */
	public static SimpleDateFormat moiAnneedateFormat = new SimpleDateFormat("MMMM yyyy");
	/** Formattage de la date venant de la base */
	public static final SimpleDateFormat databaseDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	/** Formatteur mois */
	public static final SimpleDateFormat moisFormat = new SimpleDateFormat("MMMM", Locale.FRANCE);

}
