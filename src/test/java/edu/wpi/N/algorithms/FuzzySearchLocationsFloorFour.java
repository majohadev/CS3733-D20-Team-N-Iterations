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

public class FuzzySearchLocationsFloorFour {

  @BeforeAll
  public static void initialize()
      throws DBException, FileNotFoundException, SQLException, ClassNotFoundException {
    MapDB.initTestDB();
    File fNodes = new File("src/test/resources/edu/wpi/N/csv/UPDATEDTeamNnodes.csv");
    String path = fNodes.getAbsolutePath();
    CSVParser.parseCSVfromPath(path);
  }

  /**
   * Tests that function outputs suggested locations in proper order (from most relevant to least)
   */
  @Test
  public void testSearchCorrectInputOutputsInProperOrder() throws DBException {
    String userInput = "admi";

    LinkedList<DbNode> expected = new LinkedList<DbNode>();
    expected.add(MapDB.getNode("NSERV01404"));
    expected.add(MapDB.getNode("NSERV00204"));

    LinkedList<DbNode> actual = FuzzySearchAlgorithm.suggestLocations(userInput);

    // Check if the answer is in proper order
    for (int i = 0; i < expected.size(); i++) {
      Assertions.assertTrue(expected.get(i).equals(actual.get(i)));
    }
  }

  /**
   * Tests that function outputs DbNodes in proper order (best match -> lowest match)
   *
   * @throws DBException
   */
  @Test
  public void testSearchIncorrectInputTwoWordsOutputInOrderOfPriority() throws DBException {

    String userInput = "cardolog";

    LinkedList<DbNode> expected = new LinkedList<DbNode>();
    expected.add(MapDB.getNode("NDEPT00504"));
    expected.add(MapDB.getNode("NDEPT00404"));

    LinkedList<DbNode> actual = FuzzySearchAlgorithm.suggestLocations(userInput);

    Assertions.assertTrue(actual.size() == 2);

    // Check if the answer is in proper order
    for (int i = 0; i < expected.size(); i++) {
      Assertions.assertTrue(expected.get(i).equals(actual.get(i)));
    }
  }

  @Test
  public void testSearchIncorrectInputTwoWordsOutputInOrder() throws DBException {

    String userInput = "DB Cardiology";

    LinkedList<DbNode> expected = new LinkedList<DbNode>();
    expected.add(MapDB.getNode("NDEPT00404"));
    expected.add(MapDB.getNode("NDEPT00504"));
    // expected.add(MapDB.getNode("NDEPT00104"));

    LinkedList<DbNode> actual = FuzzySearchAlgorithm.suggestLocations(userInput);

    Assertions.assertTrue(actual.size() == 2);

    // Check if the answer is in proper order
    for (int i = 0; i < expected.size(); i++) {
      Assertions.assertTrue(expected.get(i).equals(actual.get(i)));
    }
  }

  @AfterAll
  public static void clearDB() throws DBException {
    MapDB.clearNodes();
  }
}
