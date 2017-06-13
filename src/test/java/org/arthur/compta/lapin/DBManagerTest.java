package org.arthur.compta.lapin;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.dataaccess.db.DBManager;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * test du DBManager
 */
public class DBManagerTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public DBManagerTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSetup(new TestSuite(DBManagerTest.class)) {

			protected void setUp() throws Exception {
				// vidage de la base
				Path rootdir = Paths.get(System.getProperty("user.dir"), "context");

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

			protected void tearDown() throws Exception {
				System.out.println(" Global tearDown ");
			}
		};
	}

	/**
	 * Test de l'ajout de compte
	 */
	public void testAddGetCompte() {

		try {
			// ajout du compte courant
			String idCourant = DBManager.getInstance().addCompte("Courant", 100, false, false);
			assertNotNull(idCourant);
			assertFalse(idCourant.isEmpty());
			// ajout d'un compt livret
			String idLivret = DBManager.getInstance().addCompte("Livret", 1000, true, false);
			assertNotNull(idLivret);
			assertFalse(idLivret.isEmpty());

			// récupération du compte
			HashMap<String, String[]> map = DBManager.getInstance().getAllCompte();
			assertNotNull(map);
			String[] infoCompte = map.get(idCourant);
			assertNotNull(infoCompte);
			assertTrue(infoCompte.length == 4);
			assertEquals("Courant", infoCompte[0]);
			assertEquals("100.0", infoCompte[1]);
			assertEquals("false", infoCompte[2]);
			assertEquals("false", infoCompte[3]);

			String[] infoCompte2 = map.get(idLivret);
			assertNotNull(infoCompte2);
			assertTrue(infoCompte2.length == 4);
			assertEquals("Livret", infoCompte2[0]);
			assertEquals("1000.0", infoCompte2[1]);
			assertEquals("true", infoCompte2[2]);
			assertEquals("false", infoCompte2[3]);

		} catch (ComptaException e) {
			fail("Echec dans le cas nominal");
		}
	}

}
