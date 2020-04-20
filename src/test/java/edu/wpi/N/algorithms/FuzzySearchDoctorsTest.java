package edu.wpi.N.algorithms;

import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Doctor;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.LinkedList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FuzzySearchDoctorsTest {

  LinkedList<Doctor> testDoctors = new LinkedList<Doctor>();

  @BeforeEach
  void initialize()
      throws DBException, SQLException, ClassNotFoundException, FileNotFoundException {
    DbController.initDB();
    File fNodes = new File("src/test/resources/edu/wpi/N/csv/PrototypeNodes.csv");
    String path = fNodes.getAbsolutePath();
    CSVParser.parseCSVfromPath(path);

    // Create first Doctor
    LinkedList<DbNode> ivanLocations = new LinkedList<DbNode>();
    ivanLocations.add(DbController.getNode("BHALL03802"));
    ivanLocations.add(DbController.getNode("BDEPT00202"));
    Doctor ivan = new Doctor(001, "Ivan Eroshenko", ivanLocations, "Prostate Specialist");

    // Create Second Doctor
    Doctor annie =
        new Doctor(002, "Annie Eroshenko", new LinkedList<DbNode>(), "Sleep Technologist");
    Doctor mike = new Doctor(003, "Michael Laks", ivanLocations, "Sex Surrogate");
    Doctor chris = new Doctor(004, "Chris Lee", new LinkedList<DbNode>(), "Traveling Phlebotomist");
    Doctor evan = new Doctor(005, "Evan Llewellyn", null, "Egg-Broker");

    testDoctors.add(ivan);
    testDoctors.add(annie);
    testDoctors.add(mike);
    testDoctors.add(chris);
    testDoctors.add(evan);
  }

    /**
     * Tests that function returns null given empty string
     * @throws DBException
     */
  @Test
  public void testSuggestDoctorsEmptyInput() throws DBException {
    String userInput = "";
    Assertions.assertNull(FuzzySearchAlgorithm.suggestDoctors(userInput));
  }

    /**
     * Tests that function returns null given letter count less than 1
     * @throws DBException
     */
  @Test
    public void testSuggestDoctorsInputIsOneLetter() throws DBException{
      String userInput = "i";
      Assertions.assertTrue(FuzzySearchAlgorithm.suggestDoctors(userInput).isEmpty());
  }

    /**
     * Tests that function returns 2 doctors corresponding to correct input name
     */
    @Test
    public void testSuggestDoctorsCorrectInputOneWord() throws DBException{
        String userInput = "Eroshenko";
        LinkedList<Doctor> actual = FuzzySearchAlgorithm.suggestDoctors(userInput);

        Assertions.assertTrue(actual.size() == 2);
        // check if doctor Ivan is there
        Assertions.assertTrue(actual.contains(testDoctors.get(0)));
        // check if doctor Annie is there
        Assertions.assertTrue(actual.contains(testDoctors.get(1)));
    }

    @Test
    public void testSuggestDoctorsCorrectInputNotFullWord() throws DBException{
        String userInput = "Eros";
        LinkedList<Doctor> actual = FuzzySearchAlgorithm.suggestDoctors(userInput);

        Assertions.assertTrue(actual.size() == 2);
        // check if doctor Ivan is there
        Assertions.assertTrue(actual.contains(testDoctors.get(0)));
        // check if doctor Annie is there
        Assertions.assertTrue(actual.contains(testDoctors.get(1)));
    }

    @Test
    public void testSuggestDoctorsIncorrectInputFullWord() throws DBException{
        String userInput = "Mikael";

        LinkedList<Doctor> actual = FuzzySearchAlgorithm.suggestDoctors(userInput);

        // check if doctor Mike is there
        Assertions.assertTrue(actual.contains(testDoctors.get(2)));
    }

}
