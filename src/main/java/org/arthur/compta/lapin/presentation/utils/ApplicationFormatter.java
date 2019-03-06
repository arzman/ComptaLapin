package org.arthur.compta.lapin.presentation.utils;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
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
	public static DateTimeFormatter moiAnneedateFormat = DateTimeFormatter.ofPattern("MMMM yyyy");
	/** Formattage de la date venant de la base */
	public static final DateTimeFormatter databaseDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	/** Formatteur mois */
	public static final DateTimeFormatter moisFormat = DateTimeFormatter.ofPattern("MMMM", Locale.FRANCE);

}
