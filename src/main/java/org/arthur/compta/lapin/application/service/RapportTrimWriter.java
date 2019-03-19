package org.arthur.compta.lapin.application.service;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppExerciceMensuel;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.application.model.AppTrimestre;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

/**
 * 
 * Ecrit un PDF listant les dépenses, les ressources et les transfert qui ont eu
 * lieu lors d'un trimestre
 *
 */
public class RapportTrimWriter {

	/** Font pour le titre */
	private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.COURIER, 20, Font.UNDERLINE, Color.BLUE);
	/** Font pour les sous-partie de mois */
	private final Font FONT_DRT = FontFactory.getFont(FontFactory.COURIER, 12, Font.BOLD, Color.DARK_GRAY);
	/** Font pour le titre de la partie de mois */
	private final Font FONT_MOIS_TITLE = FontFactory.getFont(FontFactory.COURIER, 20, Font.UNDERLINE, Color.GREEN);
	/** Le trimestre */
	private AppTrimestre _appTrim;

	/**
	 * Constructeur
	 * 
	 * @param idTrim
	 *            le trimestre
	 * @param file
	 *            le fichier ou l'on sauve le rapport
	 * @throws ComptaException
	 */
	public RapportTrimWriter(int idTrim) throws ComptaException {

		_appTrim = TrimestreManager.getInstance().loadTrimestre(idTrim);

	}

	/**
	 * Ecrit le rapport
	 * 
	 * @param file
	 * @throws ComptaException
	 */
	public void writeRapport(File file) throws ComptaException {

		try (FileOutputStream fos = new FileOutputStream(file);) {

			Document document = new Document();
			PdfWriter.getInstance(document, fos);

			// ajout des méta-donnée du doc
			document.addTitle("Rapport Trimestriel");

			document.open();

			// Ajout du titre
			document.add(createRapportTitle());

			// ajout du détails par mois
			for (int i = 0; i < 3; i++) {

				// on change de mois pour la prochaine boucle
				document.add(createChapMois(i));
			}

			document.add(createChapGraphique(4));

			document.close();
			// fileTmp.delete();
		} catch (Exception e) {
			throw new ComptaException("Impossible de créer le rapport", e);
		}

	}

	/**
	 * Création du chapitre du graphique récapitulatif
	 * 
	 * @param i
	 *            le numéro du chapitre
	 * @return
	 * @throws ComptaException
	 */
	private Element createChapGraphique(int i) throws ComptaException {

		Chapter test = new Chapter("Graphique", i);
		// création du graphique
		final CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Mois");
		xAxis.setAnimated(false);
		final NumberAxis yAxis = new NumberAxis();
		yAxis.setAnimated(false);
		// creating the chart
		LineChart<String, Number> _lineChart = new LineChart<String, Number>(xAxis, yAxis);
		Pane chartContainer = new Pane();
		chartContainer.getChildren().add(_lineChart);

		// bidouillage pcq sinon l'image a une taille trop petite
		@SuppressWarnings("unused")
		Scene snapshotScene = new Scene(chartContainer);

		_lineChart.setAnimated(false);
		Series<String, Number> depenseSerie = new Series<String, Number>();
		depenseSerie.setName("Dépenses");
		Series<String, Number> ressourceSerie = new Series<String, Number>();
		ressourceSerie.setName("Ressource");
		Series<String, Number> budgetUseSerie = new Series<String, Number>();
		budgetUseSerie.setName("Budget utilisé");
		for (int month = 0; month < 3; month++) {

			double dep = SyntheseService.getDepenseForMonth(_appTrim.getAppExerciceMensuel(month).get().getDateDebut());
			double res = SyntheseService.getRessourceForMonth(_appTrim.getAppExerciceMensuel(month).get().getDateDebut());
			double bud = SyntheseService.getBudgetUsageForMonth(_appTrim.getAppExerciceMensuel(month).get().getDateDebut());

			depenseSerie.getData()
					.add(new Data<String, Number>(ApplicationFormatter.moisFormat.format(_appTrim.getAppExerciceMensuel(month).get().getDateDebut()), dep));
			ressourceSerie.getData()
					.add(new Data<String, Number>(ApplicationFormatter.moisFormat.format(_appTrim.getAppExerciceMensuel(month).get().getDateDebut()), res));
			budgetUseSerie.getData()
					.add(new Data<String, Number>(ApplicationFormatter.moisFormat.format(_appTrim.getAppExerciceMensuel(month).get().getDateDebut()), bud));

		}
		_lineChart.getData().add(depenseSerie);
		_lineChart.getData().add(ressourceSerie);
		_lineChart.getData().add(budgetUseSerie);

		final SnapshotParameters spa = new SnapshotParameters();
		spa.setTransform(javafx.scene.transform.Transform.scale(2, 2));

		WritableImage img = _lineChart.snapshot(spa, null);
		File fileTmp = null;
		try {

			fileTmp = File.createTempFile("img_graphique", ".ems");
			ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", fileTmp);
			Image im = Image.getInstance(fileTmp.getAbsolutePath());
			im.setInterpolation(true);
			im.scalePercent(50);
			test.add(im);
		} catch (Exception e) {
			// rien
		} finally {
			fileTmp.delete();
		}

		return test;
	}

	/**
	 * Crée un rapport pour le mois
	 * 
	 * @param i
	 *            index du mois dans le trimestre
	 * @return
	 */
	private Element createChapMois(int i) {

		// l'exercice mensuel concerné
		AppExerciceMensuel em = _appTrim.getAppExerciceMensuel(i).getValue();

		// paragraphe du mois
		Paragraph moisTitle = new Paragraph(ApplicationFormatter.moiAnneedateFormat.format(em.getDateDebut()), FONT_MOIS_TITLE);
		Chapter chapMois = new Chapter(moisTitle, i + 1);

		// ajout des dépenses
		Paragraph parDep = new Paragraph();
		Chunk titleDep = new Chunk("Dépenses", FONT_DRT);
		parDep.add(titleDep);

		// Tableau des dépenses
		parDep.add(createTableOperation(em.getDepenses()));
		chapMois.add(parDep);

		// ajout des ressources
		Paragraph parRes = new Paragraph("Ressources", FONT_DRT);

		// Tableau des ressources
		parRes.add(createTableOperation(em.getRessources()));
		chapMois.add(parRes);

		// ajout des transferts
		Paragraph parTrans = new Paragraph("Transfert", FONT_DRT);

		// Tableau des transferts
		PdfPTable tabTrans = new PdfPTable(4);
		tabTrans.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);
		tabTrans.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		tabTrans.addCell("Libellé");
		tabTrans.addCell("Montant");
		tabTrans.addCell("Source");
		tabTrans.addCell("Cible");

		for (AppTransfert trans : _appTrim.getAppExerciceMensuel(i).getValue().getTransferts()) {

			tabTrans.addCell(trans.getLibelle());
			tabTrans.addCell(ApplicationFormatter.montantFormat.format(trans.getMontant()));
			tabTrans.addCell(trans.getCompteSource().getNom());
			tabTrans.addCell(trans.getCompteCible().getNom());

		}
		parTrans.add(tabTrans);
		chapMois.add(parTrans);

		return chapMois;
	}

	/**
	 * Crée une table libelle/montant pour une liste d'opérations
	 * 
	 * @param appOpList
	 * @return
	 */
	private PdfPTable createTableOperation(List<AppOperation> appOpList) {

		PdfPTable tabDep = new PdfPTable(2);
		tabDep.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);
		tabDep.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		tabDep.addCell("Libellé");
		tabDep.addCell("Montant");

		for (AppOperation dep : appOpList) {

			tabDep.addCell(dep.getLibelle());
			tabDep.addCell(ApplicationFormatter.montantFormat.format(dep.getMontant()));

		}

		return tabDep;
	}

	/**
	 * Crée le titre du doc
	 * 
	 * @return
	 */
	private Paragraph createRapportTitle() {

		Chunk titlechunk = new Chunk("Rapport Trimestriel de " + ApplicationFormatter.moiAnneedateFormat.format(_appTrim.getDateDebut()) + " à "
				+ ApplicationFormatter.moiAnneedateFormat.format(_appTrim.getDateFin()), FONT_TITLE);

		Paragraph parTitle = new Paragraph(titlechunk);
		parTitle.setAlignment(Element.ALIGN_CENTER);

		return parTitle;
	}

}
