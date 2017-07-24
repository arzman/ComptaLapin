package org.arthur.compta.lapin.application.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.dataaccess.db.DBManager;

import com.lowagie.text.Anchor;
import com.lowagie.text.Chapter;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class SyntheseService {

	/**
	 * Retourne la liste des années des exercice mensuels sous forme d'entier
	 * 
	 * @return
	 * @throws ComptaException
	 *             Echec
	 */
	public static List<Integer> getAnnees() throws ComptaException {

		ArrayList<Integer> res = new ArrayList<>();

		for (String st : DBManager.getInstance().getAllAnnees()) {

			res.add(Integer.parseInt(st));

		}

		return res;
	}

	/**
	 * Retourne la somme des ressources pour le mois donné
	 * 
	 * @param date
	 * @return
	 * @throws ComptaException
	 */
	public static double getRessourceForMonth(Calendar date) throws ComptaException {

		double res = 0;
		// on récupère les ressources...et on somme
		for (double dou : DBManager.getInstance().getOperationForMonth("RESSOURCE", date)) {
			res = res + dou;
		}

		return res;
	}

	/**
	 * Retourne la somme des dépenses pour le mois donné
	 * 
	 * @param date
	 * @return
	 * @throws ComptaException
	 */
	public static double getDepenseForMonth(Calendar date) throws ComptaException {

		double res = 0;
		// on récupère les dépenses...et on somme
		for (double dou : DBManager.getInstance().getOperationForMonth("DEPENSE", date)) {
			res = res + dou;
		}

		return res;
	}

	public static void writeRapportForTrim(String idTrim, File file) {

		try (FileOutputStream fos = new FileOutputStream(file);) {
			Document document = new Document();
			PdfWriter.getInstance(document, fos);

			document.open();

			Anchor anchor = new Anchor("Premier Chapitre");
			anchor.setName("FirstChapter");

			// Second parameter is the number of the chapter
			Chapter catPart = new Chapter(new Paragraph(anchor), 1);

			Paragraph subPara = new Paragraph("Sous-cat 1");
			Section subCatPart = catPart.addSection(subPara);
			subCatPart.add(new Paragraph("Hello"));

			subPara = new Paragraph("Sous-cat 2");
			subCatPart = catPart.addSection(subPara);
			subCatPart.add(new Paragraph("Paragraph 1"));
			subCatPart.add(new Paragraph("Paragraph 2"));
			subCatPart.add(new Paragraph("Paragraph 3"));

			// add a list
			com.lowagie.text.List list = new com.lowagie.text.List(true, false, 10);
			list.add(new com.lowagie.text.ListItem("Premier point"));
			list.add(new com.lowagie.text.ListItem("Second point"));
			list.add(new com.lowagie.text.ListItem("Troisième point"));
			subCatPart.add(list);

			Paragraph paragraph = new Paragraph();
			for (int i = 0; i < 5; i++) {
				paragraph.add(new Paragraph(" "));
			}
			subCatPart.add(paragraph);

			// add a table
			PdfPTable table = new PdfPTable(3);

	        PdfPCell c1 = new PdfPCell(new Phrase("Header 1"));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(c1);

	        c1 = new PdfPCell(new Phrase("Header 2"));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(c1);

	        c1 = new PdfPCell(new Phrase("Header 3"));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(c1);
	        table.setHeaderRows(1);

	        table.addCell("1.0");
	        table.addCell("1.1");
	        table.addCell("1.2");
	        table.addCell("2.1");
	        table.addCell("2.2");
	        table.addCell("2.3");

	        subCatPart.add(table);

			// now add all this to the document
			document.add(catPart);

			// Next section
			anchor = new Anchor("Second Chapitre");
			anchor.setName("Second Chapter");

			// Second parameter is the number of the chapter
			catPart = new Chapter(new Paragraph(anchor), 1);

			subPara = new Paragraph("Subcategory");
			subCatPart = catPart.addSection(subPara);
			subCatPart.add(new Paragraph("This is a very important message"));

			// now add all this to the document
			document.add(catPart);

			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
