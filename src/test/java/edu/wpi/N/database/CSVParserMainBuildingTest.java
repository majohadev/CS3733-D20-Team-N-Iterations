package edu.wpi.N.database;

import edu.wpi.N.algorithms.AStarTests;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import org.junit.jupiter.api.*;

public class CSVParserMainBuildingTest {
  @BeforeAll
  public static void initializeTest()
      throws SQLException, ClassNotFoundException, DBException, FileNotFoundException {
    MapDB.initTestDB();
    InputStream inputNodes = AStarTests.class.getResourceAsStream("../csv/nodes.csv");
    InputStream inputEdges = AStarTests.class.getResourceAsStream("../csv/edges.csv");
    CSVParser.parseCSV(inputNodes);
    CSVParser.parseCSV(inputEdges);
  }

  /**
   * Tests that all given Nodes are parsed correctly
   *
   * @throws DBException
   */
  //  @Test
  //  public void testCSVParserMainBuilding() throws DBException {
  //    LinkedList<DbNode> allNodes = MapDB.allNodes();
  //    Assertions.assertTrue(allNodes.size() == 963);
  //  }

  @AfterAll
  public static void clearDB() throws DBException {
    MapDB.clearNodes();
  }
}
