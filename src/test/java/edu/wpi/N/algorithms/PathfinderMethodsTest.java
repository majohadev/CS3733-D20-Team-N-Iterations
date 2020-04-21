package edu.wpi.N.algorithms;

import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.LinkedList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PathfinderMethodsTest {

  @BeforeAll
  public static void initializeTest() throws SQLException, ClassNotFoundException, DBException {
    DbController.initDB();
    InputStream inputNodes =
        PathfinderMethodsTest.class.getResourceAsStream("../csv/TestNodes.csv");
    InputStream inputEdges =
        PathfinderMethodsTest.class.getResourceAsStream("../csv/TestEdges.csv");
    CSVParser.parseCSV(inputNodes);
    CSVParser.parseCSV(inputEdges);
  }

  /** Tests that findPath returns a Path object with the best route from H9 to EEE */
  @Test
  public void findPathNormalCase() throws DBException {
    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();
    actualPath.add(DbController.getNode("H100000001"));
    actualPath.add(DbController.getNode("H900000000"));
    actualPath.add(DbController.getNode("H120000000"));
    actualPath.add(DbController.getNode("H130000000"));
    actualPath.add(DbController.getNode("EEEEEEEEEE"));

    Path testingPath = Pathfinder.findPath("H100000001", "EEEEEEEEEE");

    for (int i = 0; i < actualPath.size(); i++) {
      Assertions.assertEquals(
          actualPath.get(i).getNodeID(), testingPath.getPath().get(i).getNodeID());
    }
  }

  /**
   * Tests that findPath method return a Path object with route consisting of 2 Nodes, since start
   * and end nodes are neighbors
   */
  @Test
  public void findPathStartIsNeighborWithEndNode() throws DBException {

    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();

    actualPath.add(DbController.getNode("H120000000"));
    actualPath.add(DbController.getNode("H130000000"));

    Path testingPath = Pathfinder.findPath("H120000000", "H130000000");

    for (int i = 0; i < actualPath.size(); i++) {
      Assertions.assertEquals(
          testingPath.getPath().get(i).getNodeID(), actualPath.get(i).getNodeID());
    }
  }

  /**
   * Tests that findPath throws NullPointerException if the destination given is not connected to
   * any node
   */
  @Test
  public void findPathDestinationNotFound() {
    Assertions.assertNull(Pathfinder.findPath("H120000000", "NonExistentNode"));
  }

  /**
   * Tests that findPath method throws NullPointerException if start Node doesn't have a connection
   * to any node (including end node)
   */
  @Test
  public void findPathStartNodeHasNoEdges() {
    Assertions.assertNull(Pathfinder.findPath("NonExistentNode", "H120000000"));
  }

  /**
   * Tests that findPath method returns a Path object with only one node in its route since Start
   * Node = End Node
   */
  @Test
  public void findPathEndIsStartNode() throws DBException {
    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();

    actualPath.add(DbController.getNode("H120000000"));
    Path testingPath = Pathfinder.findPath("H120000000", "H120000000");

    for (int i = 0; i < actualPath.size(); i++) {
      Assertions.assertEquals(
          testingPath.getPath().get(i).getNodeID(), actualPath.get(i).getNodeID());
    }
  }

  /** Tests that findQuickAccess chooses finds the path to the closest node of the given nodeType */
  @Test
  public void findQuickAccessTester1() throws DBException {
    Path path = Pathfinder.findQuickAccess(DbController.getNode("H200000000"), "REST");
    Assertions.assertEquals(path.getPath().getLast(), DbController.getNode("AAAAAAAAAA"));
  }

  /** Tests that findQuickAccess chooses finds the path to the closest node of the given nodeType */
  @Test
  public void findQuickAccessTester2() throws DBException {
    Path path = Pathfinder.findQuickAccess(DbController.getNode("H700000000"), "LABS");
    Assertions.assertEquals(path.getPath().getLast(), DbController.getNode("BBBBBBBBBB"));
  }

  /**
   * Tests that findQuickAccess returns null if the given nodeType does not exist on the given floor
   */
  @Test
  public void findQuickAccessNullTester() throws DBException {
    Assertions.assertNull(Pathfinder.findQuickAccess(DbController.getNode("H700000000"), "ELEV"));
  }

  /**
   * Tests that findQuickAccess returns null if there is no path to a node of the given nodeType,
   * even though the node is present on the floor
   */
  @Test
  public void findQuickAccessNoPathTester() throws DBException {
    DbController.addNode("NHALL00104", 1250, 850, 1, "MainBuil", "ELEV", "Hall 1", "Hall 1", 'N');
    Assertions.assertNull(Pathfinder.findQuickAccess(DbController.getNode("H700000000"), "ELEV"));
    DbController.deleteNode("NHALL00104");
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
    DbController.clearNodes();
  }
}
