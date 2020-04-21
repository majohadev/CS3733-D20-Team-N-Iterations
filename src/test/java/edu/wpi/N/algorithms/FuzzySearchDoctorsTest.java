package edu.wpi.N.algorithms;

import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import edu.wpi.N.database.DoctorController;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Doctor;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.LinkedList;
import org.junit.jupiter.api.*;

public class FuzzySearchDoctorsTest {

  LinkedList<Doctor> testDoctors = new LinkedList<Doctor>();

  @BeforeAll
  static void initDb()
      throws SQLException, DBException, ClassNotFoundException, FileNotFoundException {
    DbController.initDB();

    File fNodes = new File("src/test/resources/edu/wpi/N/csv/PrototypeNodes.csv");
    String path = fNodes.getAbsolutePath();
    CSVParser.parseCSVfromPath(path);

    // Create first Doctor
    LinkedList<DbNode> ivanLocations = new LinkedList<DbNode>();
    ivanLocations.add(DbController.getNode("BHALL03802"));
    ivanLocations.add(DbController.getNode("BDEPT00202"));
    Doctor ivan = new Doctor("Ivan Eroshenko", "Prostate Specialist", ivanLocations);

    // Create Other Doctors
    Doctor annie = new Doctor("Annie Eroshenko", "Sleep Technologist", new LinkedList<DbNode>());
    Doctor mike = new Doctor("Michael Laks", "Sex Surrogate", ivanLocations);
    Doctor chris = new Doctor("Chris Lee", "Traveling Phlebotomist", new LinkedList<DbNode>());
    Doctor evan = new Doctor("Evan Llewellyn", "Egg-Broker", null);

    DoctorController.addDoctor(ivan.getName(), ivan.getField(), ivan.getLoc());
    DoctorController.addDoctor(annie.getName(), annie.getField(), annie.getLoc());
    DoctorController.addDoctor(mike.getName(), mike.getField(), mike.getLoc());
    DoctorController.addDoctor(chris.getName(), chris.getField(), chris.getLoc());
    DoctorController.addDoctor(evan.getName(), evan.getField(), evan.getLoc());
  }

  @BeforeEach
  void initialize()
      throws DBException, SQLException, ClassNotFoundException, FileNotFoundException {

    // Create first Doctor
    LinkedList<DbNode> ivanLocations = new LinkedList<DbNode>();
    ivanLocations.add(DbController.getNode("BHALL03802"));
    ivanLocations.add(DbController.getNode("BDEPT00202"));
    Doctor ivan = new Doctor("Ivan Eroshenko", "Prostate Specialist", ivanLocations);

    // Create Other Doctors
    Doctor annie = new Doctor("Annie Eroshenko", "Sleep Technologist", new LinkedList<DbNode>());
    Doctor mike = new Doctor("Michael Laks", "Sex Surrogate", ivanLocations);
    Doctor chris = new Doctor("Chris Lee", "Traveling Phlebotomist", new LinkedList<DbNode>());
    Doctor evan = new Doctor("Evan Llewellyn", "Egg-Broker", null);

    testDoctors.add(ivan);
    testDoctors.add(annie);
    testDoctors.add(mike);
    testDoctors.add(chris);
    testDoctors.add(evan);
  }

  /**
   * Tests that function returns empty list given empty string
   *
   * @throws DBException
   */
  @Test
  public void testSuggestDoctorsEmptyInput() throws DBException {
    String userInput = "";
    Assertions.assertTrue(FuzzySearchAlgorithm.suggestDoctors(userInput).isEmpty());
  }

  /**
   * Tests that function returns empty list given letter count less than 1
   *
   * @throws DBException
   */
  @Test
  public void testSuggestDoctorsInputIsOneLetter() throws DBException {
    String userInput = "i";
    Assertions.assertTrue(FuzzySearchAlgorithm.suggestDoctors(userInput).isEmpty());
  }

  /** Tests that function returns 2 doctors corresponding to correct input name */
  @Test
  public void testSuggestDoctorsCorrectInputOneWord() throws DBException {
    String userInput = "Eroshenko";
    LinkedList<Doctor> actual = FuzzySearchAlgorithm.suggestDoctors(userInput);

    Assertions.assertTrue(actual.size() == 2);
    // check if doctor Ivan is there
    Assertions.assertTrue(actual.contains(testDoctors.get(0)));
    // check if doctor Annie is there
    Assertions.assertTrue(actual.contains(testDoctors.get(1)));
  }

  /**
   * Tests that function returns 2 doctors corresponding to correct but not complete input
   *
   * @throws DBException
   */
  @Test
  public void testSuggestDoctorsCorrectInputNotFullWord() throws DBException {
    String userInput = "Eros";
    LinkedList<Doctor> actual = FuzzySearchAlgorithm.suggestDoctors(userInput);

    Assertions.assertTrue(actual.size() == 2);
    // check if doctor Ivan is there
    Assertions.assertTrue(actual.contains(testDoctors.get(0)));
    // check if doctor Annie is there
    Assertions.assertTrue(actual.contains(testDoctors.get(1)));
  }

  /**
   * Tests that function returns correct doctor corresponding to incorrect input single word
   *
   * @throws DBException
   */
  @Test
  public void testSuggestDoctorsIncorrectInputFullWord() throws DBException {
    String userInput = "Mikael";

    LinkedList<Doctor> actual = FuzzySearchAlgorithm.suggestDoctors(userInput);

    // check if doctor Mike is there
    Assertions.assertTrue(actual.contains(testDoctors.get(2)));
  }

  /** Tests that function returns correct doctor corresponding to incorrect not full user's word */
  @Test
  public void testSuggestDoctorsIncorrectInputNotFull() throws DBException {
    String userInput = "Micae";

    LinkedList<Doctor> actual = FuzzySearchAlgorithm.suggestDoctors(userInput);

    // check if doctor Mike is there
    Assertions.assertTrue(actual.contains(testDoctors.get(2)));
  }

  /** Test that function returns doctors corresponding to incorrect user's input of two words */
  @Test
  public void testSuggestDoctorsIncorrectInputTwoWords() throws DBException {
    String userInput = "Micael Las";

    LinkedList<Doctor> actual = FuzzySearchAlgorithm.suggestDoctors(userInput);

    // check if doctor Mike is there
    Assertions.assertTrue(actual.contains(testDoctors.get(2)));
  }

  /**
   * Tests that function returns empty list of Doctors if such doctor could not be found
   *
   * @throws DBException
   */
  @Test
  public void testSuggestDoctorsIncorrectInputNonExistentDoctor() throws DBException {
    String userInput = "A B C D E F G";
    LinkedList<Doctor> actual = FuzzySearchAlgorithm.suggestDoctors(userInput);
    Assertions.assertTrue(actual.isEmpty());
  }

  /**
   * Tests that function returns correct doctor given flipped order of missspelled words@throws
   * DBException
   */
  @Test
  public void testSuggestDoctorsIncorrectFlippedOrder() throws DBException {
    String userInput = "Liwelyn Ewan";

    LinkedList<Doctor> actual = FuzzySearchAlgorithm.suggestDoctors(userInput);

    // check if doctor Evan is there
    Assertions.assertTrue(actual.contains(testDoctors.get(4)));
  }

  @AfterAll
  public static void clear() throws DBException {
    DbController.clearNodes();
  }
}
