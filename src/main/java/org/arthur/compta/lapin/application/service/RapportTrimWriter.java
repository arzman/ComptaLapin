package org.arthur.compta.lapin.application.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppExerciceMensuel;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.application.model.AppTrimestre;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Ecrit un PDF listant les dépenses, les ressources et les transferts qui ont eu
 * lieu lors d'un trimestre
 */
public class RapportTrimWriter {

/** Font pour le titre */
private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.COURIER, 20, Font.UNDERLINE, Color.BLUE);
/** Font pour les sous-parties de mois */
private final Font FONT_DRT = FontFactory.getFont(FontFactory.COURIER, 12, Font.BOLD, Color.DARK_GRAY);
/** Font pour le titre de la partie de mois */
private final Font FONT_MOIS_TITLE = FontFactory.getFont(FontFactory.COURIER, 20, Font.UNDERLINE, Color.GREEN);
/** Le trimestre */
private final AppTrimestre _appTrim;

/**
 * Constructeur
 *
 * @param idTrim l'id du trimestre
 */
public RapportTrimWriter(int idTrim) throws ComptaException {
_appTrim = TrimestreManager.getInstance().loadTrimestre(idTrim);
}

/**
 * Ecrit le rapport PDF
 *
 * @param file le fichier cible
 * @throws ComptaException
 */
public void writeRapport(File file) throws ComptaException {
try {
Document document = new Document();
PdfWriter.getInstance(document, new FileOutputStream(file));
document.open();

// titre
Paragraph titre = new Paragraph("Rapport du trimestre", FONT_TITLE);
titre.setAlignment(Element.ALIGN_CENTER);
document.add(titre);
document.add(new Paragraph(" "));

// 3 mois
for (int i = 0; i < 3; i++) {
AppExerciceMensuel em = _appTrim.getAppExerciceMensuel(i);
if (em != null) {
addMois(document, em);
}
}

document.close();
} catch (Exception e) {
throw new ComptaException("Erreur lors de la création du rapport PDF", e);
}
}

private void addMois(Document document, AppExerciceMensuel em) throws Exception {
String moisTitle = ApplicationFormatter.moiAnneedateFormat.format(em.getDateDebut());
Paragraph titreMois = new Paragraph(moisTitle, FONT_MOIS_TITLE);
titreMois.setAlignment(Element.ALIGN_CENTER);
document.add(titreMois);
document.add(new Paragraph(" "));

addOperationSection(document, "Dépenses", em.getDepenses());
addOperationSection(document, "Ressources", em.getRessources());
addTransfertSection(document, em.getTransferts());

document.add(new Paragraph("Résultat : " + ApplicationFormatter.montantFormat.format(em.getResultat())));
document.add(new Paragraph(" "));
}

private void addOperationSection(Document document, String titre, List<AppOperation> operations) throws Exception {
document.add(new Paragraph(titre, FONT_DRT));

if (operations.isEmpty()) {
document.add(new Paragraph("  (aucune)"));
return;
}

PdfPTable table = new PdfPTable(3);
table.setWidthPercentage(100);
table.addCell("Libellé");
table.addCell("Montant");
table.addCell("État");

for (AppOperation op : operations) {
table.addCell(op.getLibelle());
table.addCell(ApplicationFormatter.montantFormat.format(op.getMontant()));
table.addCell(op.getEtat().toString());
}
document.add(table);
document.add(new Paragraph(" "));
}

private void addTransfertSection(Document document, List<AppTransfert> transferts) throws Exception {
document.add(new Paragraph("Transferts", FONT_DRT));

if (transferts.isEmpty()) {
document.add(new Paragraph("  (aucun)"));
return;
}

PdfPTable table = new PdfPTable(5);
table.setWidthPercentage(100);
table.addCell("Libellé");
table.addCell("Montant");
table.addCell("Source");
table.addCell("Cible");
table.addCell("État");

for (AppTransfert tr : transferts) {
table.addCell(tr.getLibelle());
table.addCell(ApplicationFormatter.montantFormat.format(tr.getMontant()));
table.addCell(tr.getCompteSource() != null ? tr.getCompteSource().getNom() : "");
table.addCell(tr.getCompteCible() != null ? tr.getCompteCible().getNom() : "");
table.addCell(tr.getEtat().toString());
}
document.add(table);
document.add(new Paragraph(" "));
}

}
