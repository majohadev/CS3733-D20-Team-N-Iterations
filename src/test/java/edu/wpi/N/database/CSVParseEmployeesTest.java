package edu.wpi.N.database;

import edu.wpi.N.entities.employees.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.LinkedList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CSVParseEmployeesTest {

  @BeforeAll
  public static void initialize()
      throws FileNotFoundException, SQLException, DBException, ClassNotFoundException {
    MapDB.initTestDB();
    File fNodes = new File("src/main/resources/edu/wpi/N/csv/newNodes.csv");
    String path = fNodes.getAbsolutePath();
    CSVParser.parseCSVfromPath(path);

    File employees = new File("src/test/resources/edu/wpi/N/csv/Employees.csv");
    String pathToEmployees = employees.getAbsolutePath();
    CSVParser.parseCSVEmployeesFromPath(pathToEmployees);
  }

  /** Tests that the function parses the inputted CSV successfully */
  @Test
  public void testParseEmployees() throws DBException {
    LinkedList<Employee> actualEmployees = ServiceDB.getEmployees();
    Assertions.assertTrue(actualEmployees.size() >= 26);
  }

  /**
   * Tests that translotor was added correctly, including her list of languages
   *
   * @throws DBException
   */
  @Test
  public void testParseTranslatorEmployeeLanguagesAdded() throws DBException {
    LinkedList<Translator> actualTranslatorEmployees = ServiceDB.getTranslators();

    LinkedList<String> languages = new LinkedList<String>();
    languages.add("Cambodian");
    languages.add("Spanish");
    Translator annie = new Translator(2, "Annie Fan", languages);

    Assertions.assertTrue(actualTranslatorEmployees.size() == 6);

    // The test itself. Find translator with expected name
    boolean foundExpectedName = false;
    for (Translator translator : actualTranslatorEmployees) {
      if (translator.getName().equals(annie.getName())) {
        // Check that list of languages matches expected
        foundExpectedName = true;
        Assertions.assertTrue(translator.getLanguages().contains(languages.get(0)));
        Assertions.assertTrue(translator.getLanguages().contains(languages.get(1)));
      }
    }

    // If employee with expected name doesn't exist
    if (!foundExpectedName) {
      // fail the test
      Assertions.fail();
    }
  }

  /**
   * Tests that Laundry employee was inputted correctly
   *
   * @throws DBException
   */
  @Test
  public void testParseCSVLandrySuccessful() throws DBException {
    LinkedList<Laundry> actualLandries = ServiceDB.getLaundrys();

    // Check if list lf laundry workes has employee with expected name
    for (Laundry laundry : actualLandries) {
      if (laundry.getName().equals("Mike Laks")) {
        Assertions.assertTrue(true);
      }
    }

    Assertions.assertTrue(actualLandries.size() == 4);
  }

  //  /** Tests that random locations (3 nodes) will get generated */
  //  @Test
  //  public void testGenerateRandomDoctorLocations() throws DBException {
  //    LinkedList<DbNode> actualLocations = CSVParser.generateRandomLocations();
  //
  //    Assertions.assertTrue(actualLocations.size() == 3);
  //  }

  /**
   * Tests that doctors have been added successfully
   *
   * @throws DBException
   */
  @Test
  public void testAddingDoctors() throws DBException {
    LinkedList<Doctor> actualDoctors = DoctorDB.getDoctors();

    Assertions.assertTrue(actualDoctors.size() == 2);
  }

  /**
   * Tests that emotional supporters were added correctly
   *
   * @throws DBException
   */
  @Test
  public void testIfEmotionalSupporterAdded() throws DBException {
    LinkedList<EmotionalSupporter> actual = ServiceDB.getEmotionalSupporters();

    Assertions.assertTrue(actual.size() == 2);
  }

  /**
   * Tests that flower deliverists were added correctly
   *
   * @throws DBException
   */
  @Test
  public void testIfFlowerEmployeeAdded() throws DBException {
    LinkedList<FlowerDeliverer> actual = ServiceDB.getFlowerDeliverers();

    Assertions.assertTrue(actual.size() == 2);
  }

  /**
   * Tests that Internal Transportation Employees were added correctly
   *
   * @throws DBException
   */
  @Test
  public void testIfInternalTransportAdded() throws DBException {
    LinkedList<InternalTransportationEmployee> actual =
        ServiceDB.getInternalTransportationEmployees();

    Assertions.assertTrue(actual.size() == 2);
  }

  /**
   * Tests that IT Employees were added correctly
   *
   * @throws DBException
   */
  @Test
  public void testIfITAdded() throws DBException {
    LinkedList<IT> actual = ServiceDB.getITs();

    Assertions.assertTrue(actual.size() == 2);
  }

  /**
   * Tests that IT Employees were added correctly
   *
   * @throws DBException
   */
  @Test
  public void testIfSanitationAdded() throws DBException {
    LinkedList<Sanitation> actual = ServiceDB.getSanitationEmp();

    Assertions.assertTrue(actual.size() == 2);
  }

  /**
   * Tests that IT Security were added correctly
   *
   * @throws DBException
   */
  @Test
  public void testIfSecurityAdded() throws DBException {
    LinkedList<SecurityOfficer> actual = ServiceDB.getSecurityOfficers();

    Assertions.assertTrue(actual.size() == 2);
  }

  /**
   * Tests that Wheel Chair Employees were added correctly
   *
   * @throws DBException
   */
  @Test
  public void testIfWheelChairAdded() throws DBException {
    LinkedList<WheelchairEmployee> actual = ServiceDB.getWheelchairEmployees();

    Assertions.assertTrue(actual.size() == 2);
  }

  @AfterAll
  public static void cleanup() throws DBException {
    MapDB.clearNodes();
    for (int i : getAllEmployeeIds()) {
      ServiceDB.removeEmployee(i);
    }
  }

  private static LinkedList<Integer> getAllEmployeeIds() throws DBException {
    LinkedList<Integer> ids = new LinkedList<Integer>();

    for (Employee employee : ServiceDB.getEmployees()) {
      ids.add(employee.getID());
    }

    return ids;
  }
}
