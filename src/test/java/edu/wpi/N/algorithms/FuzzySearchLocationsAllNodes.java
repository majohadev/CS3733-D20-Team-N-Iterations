package edu.wpi.N.algorithms;

import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.LinkedList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FuzzySearchLocationsAllNodes {

  @BeforeAll
  public static void initialize()
      throws DBException, FileNotFoundException, SQLException, ClassNotFoundException {
    MapDB.initTestDB();
    File fNodes = new File("src/test/resources/edu/wpi/N/csv/TeamNAllNodes.csv");
    String path = fNodes.getAbsolutePath();
    CSVParser.parseCSVfromPath(path);
  }

  @Test
  public void testFuzzySearchIncorrectInputTwoWords() throws DBException {
    String userInput = "department affices";

    LinkedList<DbNode> expected = new LinkedList<DbNode>();
    expected.add(MapDB.getNode("NDEPT00204"));
    expected.add(MapDB.getNode("NDEPT01504"));

    // Measure performance
    long startTime = System.nanoTime();

    LinkedList<DbNode> actual = FuzzySearchAlgorithm.suggestLocations(userInput);

    long endTime = System.nanoTime();

    long timeElapsed = endTime - startTime;
    System.out.println(
        "Elapsed time for FuzzySearch on all Nodes in milliseconds:" + timeElapsed / 1000000);

    Assertions.assertTrue(actual.getFirst().equals(expected.get(0)));
    Assertions.assertTrue(actual.contains(expected.get(1)));
  }

  @AfterAll
  public static void clearDB() throws DBException {
    MapDB.clearNodes();
  }
}
