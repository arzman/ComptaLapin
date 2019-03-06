package org.arthur.compta.lapin;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.AppTrimestre;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElement;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElementFrequence;
import org.arthur.compta.lapin.application.service.ComptaService;
import org.arthur.compta.lapin.application.service.TemplateService;
import org.arthur.compta.lapin.dataaccess.db.DBManager;
import org.arthur.compta.lapin.model.operation.OperationType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * test du DBManager
 */
public class ComptaSimpleTest {
	/**
	 * Create the test case
	 *
	 */
	public ComptaSimpleTest() {

	}

	@BeforeClass
	public static void setUp() throws Exception {
		// vidage de la base
		Path rootdir = Paths.get(System.getProperty("user.dir"), "context", "db");

		if (Files.exists(rootdir)) {

			Files.walkFileTree(rootdir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}

	@AfterClass
	public static void tearDown() throws Exception {
		DBManager.getInstance().closeDataBase();
		System.out.println(" Global tearDown ");
	}

	/**
	 * Test de l'ajout de compte
	 */
	@Test
	public void testAddGetCompte() {

		try {
			// ajout du compte courant
			AppCompte idCourant = CompteManager.getInstance().addCompte("Courant", 100, false, false);
			Assert.assertNotNull(idCourant);

			// ajout d'un compt livret
			AppCompte idLivret = CompteManager.getInstance().addCompte("Livret", 1000, true, false);
			Assert.assertNotNull(idLivret);

		} catch (ComptaException e) {
			e.printStackTrace();
			Assert.fail("Echec dans le cas nominal");

		}
	}

	@Test
	public void testSetTrimestreCourant() {

		try {

			// creation de 3 trimestres
			TrimestreManager.getInstance().createTrimestre(LocalDate.of(2019, 1, 13));
			AppTrimestre trim = TrimestreManager.getInstance().createTrimestre(LocalDate.of(2019, 4, 20));
			TrimestreManager.getInstance().createTrimestre(LocalDate.of(2019, 7, 7));

			// chargement du trimestre courant
			TrimestreManager.getInstance().loadTrimestreCourant(trim.getAppId());
			Assert.assertEquals(trim.getAppId(),
					TrimestreManager.getInstance().trimestreCourantProperty().get().getAppId());

			// verfif
			HashMap<String, LocalDate> shortList = TrimestreManager.getInstance().getAllTrimestreShortList();
			Assert.assertFalse(shortList.isEmpty());
			assertTrue(shortList.keySet().size() == 3);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Echec dans le cas nominal");
		}

	}

	@Test
	public void testComptaService() {

		try {

			ComptaService.getDateDerVerif();
			LocalDate date = LocalDate.of(1986, 9, 30);
			ComptaService.setDateDerVerif(date);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Echec dans le cas nominal");
		}

	}

	@Test
	public void testTemplate() {

		try {
			// ajout du compte courant pour template
			AppCompte idCourant = CompteManager.getInstance().addCompte("Courant Template", 1000, false, false);
			Assert.assertNotNull(idCourant);

			ArrayList<TrimestreTemplateElement> list = new ArrayList<>();

			// création du template
			TrimestreTemplateElement courseJeudi = new TrimestreTemplateElement();
			courseJeudi.setCompteSource(idCourant);
			courseJeudi.setNom("Course");
			courseJeudi.setMontant(120);
			courseJeudi.setFreq(TrimestreTemplateElementFrequence.HEBDOMADAIRE);
			courseJeudi.setOccurence(DayOfWeek.THURSDAY.getValue());
			courseJeudi.setType(OperationType.DEPENSE.toString());
			list.add(courseJeudi);

			// salaire
			TrimestreTemplateElement salaire = new TrimestreTemplateElement();
			salaire.setCompteSource(idCourant);
			salaire.setNom("Salaire");
			salaire.setMontant(2000);
			salaire.setFreq(TrimestreTemplateElementFrequence.MENSUEL);
			salaire.setType(OperationType.RESSOURCE.toString());
			list.add(salaire);

			// charge
			TrimestreTemplateElement charge = new TrimestreTemplateElement();
			charge.setCompteSource(idCourant);
			charge.setNom("Charge");
			charge.setMontant(500);
			charge.setFreq(TrimestreTemplateElementFrequence.TRIMESTRIEL);
			charge.setOccurence(2);
			charge.setType(OperationType.DEPENSE.toString());
			list.add(charge);

			TemplateService.updateTrimestreTemplate(list);

			AppTrimestre trim = TrimestreManager.getInstance().createTrimestre(LocalDate.of(2019, 10, 23));

			// 1er mois
			Assert.assertTrue(trim.premierMoisProperty().get().getDepenses().size() == 5);
			Assert.assertTrue(trim.premierMoisProperty().get().getRessources().size() == 1);
			Assert.assertTrue(trim.premierMoisProperty().get().getTransferts().size() == 0);

			// 2eme mois
			Assert.assertTrue(trim.deuxiemeMoisProperty().get().getDepenses().size() == 4);
			Assert.assertTrue(trim.deuxiemeMoisProperty().get().getRessources().size() == 1);
			Assert.assertTrue(trim.deuxiemeMoisProperty().get().getTransferts().size() == 0);

			// 3eme mois
			Assert.assertTrue(trim.troisiemeMoisProperty().get().getDepenses().size() == 5);
			Assert.assertTrue(trim.troisiemeMoisProperty().get().getRessources().size() == 1);
			Assert.assertTrue(trim.troisiemeMoisProperty().get().getTransferts().size() == 0);

		} catch (ComptaException e) {
			e.printStackTrace();
			Assert.fail("Echec dans le cas nominal");
		}

	}

}
