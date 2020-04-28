package edu.wpi.N.algorithms;

import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.Path;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AbsAlgoTests {

  Algorithm algorithm = new Algorithm();

  @BeforeAll
  public static void initializeTest()
      throws SQLException, ClassNotFoundException, DBException, FileNotFoundException {
    MapDB.initTestDB();
    InputStream inputNodes = AbsAlgoTests.class.getResourceAsStream("../csv/TestNodes.csv");
    InputStream inputEdges = AbsAlgoTests.class.getResourceAsStream("../csv/TestEdges.csv");
    CSVParser.parseCSV(inputNodes);
    CSVParser.parseCSV(inputEdges);
  }

  /** Tests that findQuickAccess chooses finds the path to the closest node of the given nodeType */
  @Test
  public void findQuickAccessTester1() throws DBException {
    Path path = algorithm.findQuickAccess(MapDB.getNode("H200000000"), "REST");
    Assertions.assertEquals(path.getPath().getLast(), MapDB.getNode("AAAAAAAAAA"));
  }

  /** Tests that findQuickAccess chooses finds the path to the closest node of the given nodeType */
  @Test
  public void findQuickAccessTester2() throws DBException {
    Path path = algorithm.findQuickAccess(MapDB.getNode("H700000000"), "LABS");
    Assertions.assertEquals(path.getPath().getLast(), MapDB.getNode("BBBBBBBBBB"));
  }

  /**
   * Tests that findQuickAccess returns null if the given nodeType does not exist on the given floor
   */
  @Test
  public void findQuickAccessNullTester() throws DBException {
    Assertions.assertNull(algorithm.findQuickAccess(MapDB.getNode("H700000000"), "ELEV"));
  }

  /**
   * Tests that findQuickAccess returns null if there is no path to a node of the given nodeType,
   * even though the node is present on the floor
   */
  @Test
  public void findQuickAccessNoPathTester() throws DBException {
    MapDB.addNode("NHALL00104", 1250, 850, 1, "MainBuil", "ELEV", "Hall 1", "Hall 1", 'N');
    Assertions.assertNull(algorithm.findQuickAccess(MapDB.getNode("H700000000"), "ELEV"));
    MapDB.deleteNode("NHALL00104");
  }

  // to test generatePath: uncomment the necessary test methods make the method itself public
  // just for the time of testing, then switch back to private after test

  //  /**
  //   * Tests that generatePath function generates a correct route from Start to End nodes given a
  // Map
  //   * cameFrom ("NodeID", "NodeID-came-from")
  //   */
  //  @Test
  //  public void generateCorrectPathGivenUsualCaseMap() {
  //
  //    // initialize map (nodeID, came-from-nodeID)
  //    Map<String, String> cameFrom = new HashMap<String, String>();
  //    cameFrom.put("EEE", "H4");
  //    cameFrom.put("H4", "H3");
  //    cameFrom.put("H3", null);
  //
  //    // create an actual path which must be generated
  //    LinkedList<DbNode> actualPathList = new LinkedList<DbNode>();
  //    actualPathList.add(new DbNode("H3", 0, 0, 0, "", "", "", "", 'c'));
  //    actualPathList.add(new DbNode("H4", 0, 0, 0, "", "", "", "", 'c'));
  //    actualPathList.add(new DbNode("EEE", 0, 0, 0, "", "", "", "", 'c'));
  //
  //    Node start = new Node(0, 0, "H3");
  //    Node end = new Node(0, 0, "EEE");
  //
  //    for (int i = 0; i < actualPathList.size(); i++) {
  //      Assertions.assertEquals(
  //          Pathfinder.generatePath(start, end, cameFrom).getPath().get(i).getNodeID(),
  //          actualPathList.get(i).getNodeID());
  //    }
  //  }

  //  /** Tests that generatePath method throws NullPointerException, given empty map */
  //  @Test
  //  public void generatePathGivenEmptyMap() {
  //    // initialize map (nodeID, came-from-nodeID)
  //    Map<String, String> cameFrom = new HashMap<String, String>();
  //
  //    Node start = new Node(0, 0, "H3");
  //    Node end = new Node(0, 0, "EEE");
  //
  //    Assertions.assertThrows(
  //        NullPointerException.class, () -> Pathfinder.generatePath(start, end, cameFrom));
  //  }

  @AfterAll
  public static void clearDB() throws DBException {
    MapDB.clearNodes();
  }
}
