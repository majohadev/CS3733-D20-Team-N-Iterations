package edu.wpi.N.algorithms;

import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.LinkedList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FuzzySearchTest {

  @BeforeAll
  public static void init()
      throws SQLException, ClassNotFoundException, FileNotFoundException, DBException {
    DbController.initDB();
    File fNodes = new File("src/test/resources/edu/wpi/N/csv/PrototypeNodes.csv");
    String path = fNodes.getAbsolutePath();
    CSVParser.parseCSVfromPath(path);
    fNodes = null;
    path = null;
  }

  @Test
  public void testSearchWithCorrecitonInputIsOneLetter() throws DBException {
    String userInput = "c";
    LinkedList<String> expected = null;

    Assertions.assertEquals(expected, FuzzySearchAlgorithm.suggestWithCorrection(userInput));
  }

  @Test
  public void testSearchWithCorrectionInputIsTwoLetters() throws DBException {
    String userInput = "Je";
    // expected output: Psych/Addiction Care, Psychiatric Inpatient Care,
    LinkedList<String> expected = new LinkedList<String>();
    expected.add("Jen Center for Primary Care");

    LinkedList<String> actual = FuzzySearchAlgorithm.suggestWithCorrection(userInput);

    Assertions.assertTrue(actual.contains(expected.get(0)));
  }

  @Test
  public void testSearchWithCorrectionInputIsSixLetters() throws DBException {
    String userInput = "Center";
    LinkedList<String> expected = new LinkedList<String>();
    expected.add("Jen Center for Primary Care");
    expected.add("Lee Bell Breast Center");
    expected.add("Weiner Center for Preoperative Evaluation");
    LinkedList<String> actual = FuzzySearchAlgorithm.suggestWithCorrection(userInput);
    Assertions.assertTrue(actual.size() == 3);
    Assertions.assertTrue(actual.contains(expected.get(0)));
    Assertions.assertTrue(actual.contains(expected.get(1)));
    Assertions.assertTrue(actual.contains(expected.get(2)));
  }

  @Test
  public void testSearchWithCorrectionInputSixLettersWrongSpelling() throws DBException {
    String userInput = "Centar";

    LinkedList<String> expected = new LinkedList<String>();
    expected.add("Weiner Center for Preoperative Evaluation");
    expected.add("Lee Bell Breast Center");
    expected.add("Jen Center for Primary Care");

    long startTime = System.nanoTime();

    LinkedList<String> actual = FuzzySearchAlgorithm.suggestWithCorrection(userInput);

    long endTime = System.nanoTime();

    long timeElapsed = endTime - startTime;
    System.out.println("Elapsed time for FuzzySearch in milliseconds:" + timeElapsed / 1000000);

    LinkedList<String> actual = FuzzySearchAlgorithm.suggestWithCorrection(userInput);

    Assertions.assertTrue(actual.size() == 3);
    Assertions.assertTrue(actual.contains(expected.get(0)));
    Assertions.assertTrue(actual.contains(expected.get(1)));
    Assertions.assertTrue(actual.contains(expected.get(2)));
  }

  @Test
  public void testSearchWithTwoWordIncorrectInput() throws DBException {
    String userInput = "Noose and ear";

    LinkedList<String> expected = new LinkedList<String>();
    expected.add("Ear Nose & Throat");

    LinkedList<String> actual = FuzzySearchAlgorithm.suggestWithCorrection(userInput);
    Assertions.assertTrue(actual.contains(expected.get(0)));
    Assertions.assertTrue(actual.size() == 1);
  }

  @Test
  public void testSearchWithTwoWordsCorrectInput() throws DBException {
    String userInput = "Ear Nose";

    LinkedList<String> expected = new LinkedList<String>();
    expected.add("Ear Nose & Throat");
    LinkedList<String> actual = FuzzySearchAlgorithm.suggestWithCorrection(userInput);
    Assertions.assertTrue(actual.contains(expected.get(0)));
    Assertions.assertTrue(actual.size() == 1);
  }

  @Test
  public void testSearchWithNonExistentIncorrectInput() throws DBException {
    String userInput = "Alaskan Airlines";

    LinkedList<String> actual = FuzzySearchAlgorithm.suggestWithCorrection(userInput);
    Assertions.assertTrue(actual.isEmpty());
  }

  @Test
  public void testSearchCommonInputCorrectSpellingShortWordForm() throws DBException {
    String userInput = "Info";

    LinkedList<String> expected = new LinkedList<String>();
    expected.add("Information Desk Level 2");

    LinkedList<String> actual = FuzzySearchAlgorithm.suggestWithCorrection(userInput);

    Assertions.assertTrue(actual.contains(expected.get(0)));
  }

  //  /**
  //   * Tests performance time given .csv of All Nodes Make sure to uncomment the test if need to
  // measure performance
  //   *
  //   * @throws DBException
  //   * @throws FileNotFoundException
  //   */
  //  @Test
  //  public void testSearchMeasureTimeOnAllNode() throws DBException, FileNotFoundException {
  //    DbController.clearNodes();
  //
  //    File fNodes = new File("src/test/resources/edu/wpi/N/csv/MapNAllnodes.csv");
  //    String path = fNodes.getAbsolutePath();
  //    CSVParser.parseCSVfromPath(path);
  //
  //    String userInput = "restrom";
  //
  //    long startTime = System.nanoTime();
  //
  //    LinkedList<String> actual = FuzzySearchAlgorithm.suggestWithCorrection(userInput);
  //
  //    long endTime = System.nanoTime();
  //
  //    System.out.println("Size of DB:" + DbController.allNodes().size());
  //
  //    long timeElapsed = endTime - startTime;
  //    System.out.println("Elapsed time for FuzzySearch in milliseconds:" + timeElapsed / 1000000);
  //  }

  @AfterAll
  public static void clearDB() throws DBException {
    DbController.clearNodes();
  }
}
