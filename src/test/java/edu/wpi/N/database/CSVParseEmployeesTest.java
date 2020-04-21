package edu.wpi.N.database;

import edu.wpi.N.entities.Employee;
import edu.wpi.N.entities.Laundry;
import edu.wpi.N.entities.Translator;
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
    DbController.initDB();
    File fNodes = new File("src/test/resources/edu/wpi/N/csv/Employees.csv");
    String path = fNodes.getAbsolutePath();
    CSVParser.parserCSVEmployeesFromPath(path);
  }

  /** Tests that the function parses the inputted CSV successfully */
  @Test
  public void testParseEmployees() throws DBException {
    LinkedList<Employee> actualEmployees = EmployeeController.getEmployees();
    Assertions.assertTrue(actualEmployees.size() >= 10);
  }

  /**
   * Tests that translotor was added correctly, including her list of languages
   *
   * @throws DBException
   */
  @Test
  public void testParseTranslatorEmployeeLanguagesAdded() throws DBException {
    LinkedList<Translator> actualTranslatorEmployees = EmployeeController.getTranslators();

    LinkedList<String> languages = new LinkedList<String>();
    languages.add("Cambodian");
    languages.add("Spanish");
    Translator annie = new Translator(2, "Annie Fan", languages);

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
    LinkedList<Laundry> actualLandries = EmployeeController.getLaundrys();

    Laundry landrEmployeeName = new Laundry(6, "Mike Laks");

    Assertions.assertTrue(actualLandries.contains(landrEmployeeName));
  }

  @AfterAll
  public static void clearDb() throws DBException {
    DbController.clearNodes();
  }
}
