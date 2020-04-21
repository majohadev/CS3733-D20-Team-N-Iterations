package edu.wpi.N.database;

public class CSVParseEmployeesTest {

  //  @BeforeAll
  //  public static void initialize()
  //      throws FileNotFoundException, SQLException, DBException, ClassNotFoundException {
  //    DbController.initDB();
  //    File fNodes = new File("src/test/resources/edu/wpi/N/csv/Employees.csv");
  //    String path = fNodes.getAbsolutePath();
  //    CSVParser.parserCSVEmployeesFromPath(path);
  //  }
  //
  //  /** Tests that the function parses the inputted CSV successfully */
  //  @Test
  //  public void testParseEmployees() throws DBException {
  //    LinkedList<Employee> actualEmployees = EmployeeController.getEmployees();
  //    Assertions.assertTrue(actualEmployees.size() >= 10);
  //  }
  //
  //  /**
  //   * Tests that translotor was added correctly, including her list of languages
  //   *
  //   * @throws DBException
  //   */
  //  @Test
  //  public void testParseTranslatorEmployeeLanguagesAdded() throws DBException {
  //    LinkedList<Translator> actualTranslatorEmployees = EmployeeController.getTranslators();
  //
  //    LinkedList<String> languages = new LinkedList<String>();
  //    languages.add("Cambodian");
  //    languages.add("Spanish");
  //    Translator annie = new Translator(2, "Annie Fan", languages);
  //
  //    Assertions.assertTrue(actualTranslatorEmployees.size() == 6);
  //
  //    // The test itself. Find translator with expected name
  //    boolean foundExpectedName = false;
  //    for (Translator translator : actualTranslatorEmployees) {
  //      if (translator.getName().equals(annie.getName())) {
  //        // Check that list of languages matches expected
  //        foundExpectedName = true;
  //        Assertions.assertTrue(translator.getLanguages().contains(languages.get(0)));
  //        Assertions.assertTrue(translator.getLanguages().contains(languages.get(1)));
  //      }
  //    }
  //
  //    // If employee with expected name doesn't exist
  //    if (!foundExpectedName) {
  //      // fail the test
  //      Assertions.fail();
  //    }
  //  }
  //
  //    /**
  //     * Tests that Laundry employee was inputted correctly
  //     *
  //     * @throws DBException
  //     */
  //    @Test
  //    public void testParseCSVLandrySuccessful() throws DBException {
  //      LinkedList<Laundry> actualLandries = EmployeeController.getLaundrys();
  //
  //      // Check if list lf laundry workes has employee with expected name
  //      for (Laundry laundry : actualLandries) {
  //        if (laundry.getName().equals("Mike Laks")) {
  //          Assertions.assertTrue(true);
  //        }
  //      }
  //
  //      Assertions.assertTrue(actualLandries.size() == 4);
  //    }
  //
  //  @AfterAll
  //  public static void cleanup() throws DBException {
  //    DbController.clearNodes();
  //    for (int i : getAllEmployeeIds()) {
  //      EmployeeController.removeEmployee(i);
  //    }
  //  }
  //
  //  private static LinkedList<Integer> getAllEmployeeIds() throws DBException {
  //    LinkedList<Integer> ids = new LinkedList<Integer>();
  //
  //    for (Employee employee : EmployeeController.getEmployees()) {
  //      ids.add(employee.getID());
  //    }
  //
  //    return ids;
  //  }
}
