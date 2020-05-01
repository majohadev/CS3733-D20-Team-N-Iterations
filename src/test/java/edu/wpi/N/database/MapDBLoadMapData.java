package edu.wpi.N.database;

import edu.wpi.N.entities.DbNode;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import org.junit.jupiter.api.*;

public class MapDBLoadMapData {
  @BeforeEach
  public void initialize()
      throws SQLException, DBException, ClassNotFoundException, FileNotFoundException {
    MapDB.initTestDB();
    InputStream inputNodes =
        MapDBLoadMapData.class.getResourceAsStream("../csv/FourFloorsTestNode.csv");
    InputStream inputEdges =
        MapDBLoadMapData.class.getResourceAsStream("../csv/FourFloorsTestEdges.csv");
    CSVParser.parseCSV(inputNodes);
    CSVParser.parseCSV(inputEdges);
  }

  /** Tests that all Nodes and Edges get parsed correctly */
  @Test
  public void testGetHashMapAllKeys() throws DBException {
    HashMap<String, LinkedList<DbNode>> actual = MapDB.loadMapData();

    // Add all keys
    LinkedList<String> expectedKeys = new LinkedList<String>();
    expectedKeys.add("H011000000");
    expectedKeys.add("H021000000");
    expectedKeys.add("H031000000");
    expectedKeys.add("ELEV011000");
    expectedKeys.add("H041000000");
    expectedKeys.add("H051000000");
    expectedKeys.add("ELEV021000");
    expectedKeys.add("H061000000");
    expectedKeys.add("H071000000");
    expectedKeys.add("AAAAAAAAAA");
    expectedKeys.add("H081000000");
    expectedKeys.add("STAI011000");
    expectedKeys.add("H012000000");
    expectedKeys.add("H022000000");
    expectedKeys.add("H032000000");
    expectedKeys.add("ELEV012000");
    expectedKeys.add("H042000000");
    expectedKeys.add("H052000000");
    expectedKeys.add("ELEV022000");
    expectedKeys.add("H062000000");
    expectedKeys.add("H072000000");
    expectedKeys.add("BBBBBBBBBB");
    expectedKeys.add("H082000000");
    expectedKeys.add("H092000000");
    expectedKeys.add("H013000000");
    expectedKeys.add("H023000000");
    expectedKeys.add("H033000000");
    expectedKeys.add("ELEV013000");
    expectedKeys.add("H043000000");
    expectedKeys.add("H053000000");
    expectedKeys.add("ELEV023000");
    expectedKeys.add("H063000000");
    expectedKeys.add("H073000000");
    expectedKeys.add("CCCCCCCCCC");
    expectedKeys.add("H083000000");
    expectedKeys.add("STAI013000");
    expectedKeys.add("ELEV033000");
    expectedKeys.add("ELEV034000");
    expectedKeys.add("DDDDDDDDDD");

    // Iterate through the list and make the assertions
    for (String nodeID : actual.keySet()) {
      Assertions.assertTrue(expectedKeys.contains(nodeID));
    }
  }

  /** Tests that edges get Parsed into Hashmap correctly. Sample size: only first floor */
  @Test
  public void testGetHashMapEdges() throws DBException {
    HashMap<String, LinkedList<DbNode>> actual = MapDB.loadMapData();
    HashMap<String, LinkedList<DbNode>> expected = new HashMap<String, LinkedList<DbNode>>();

    String nodeOne = "H011000000";
    LinkedList<DbNode> adjacentOne = new LinkedList<DbNode>();
    // add adjacent
    adjacentOne.add(MapDB.getNode("H021000000"));

    String nodeTwo = "H021000000";
    LinkedList<DbNode> adjacentTwo = new LinkedList<DbNode>();
    // add adjacent
    adjacentTwo.add(MapDB.getNode("H011000000"));
    adjacentTwo.add(MapDB.getNode("H031000000"));
    adjacentTwo.add(MapDB.getNode("H041000000"));

    String nodeThree = "H031000000";
    LinkedList<DbNode> adjacentThree = new LinkedList<DbNode>();
    // add adjacent
    adjacentThree.add(MapDB.getNode("H021000000"));
    adjacentThree.add(MapDB.getNode("ELEV011000"));

    String nodeElevOne = "ELEV011000";
    LinkedList<DbNode> adjacentElevOne = new LinkedList<DbNode>();
    adjacentElevOne.add(MapDB.getNode("H031000000"));
    adjacentElevOne.add(MapDB.getNode("ELEV012000"));

    String nodeFour = "H041000000";
    LinkedList<DbNode> adjacentFour = new LinkedList<DbNode>();
    // add adjacent
    adjacentFour.add(MapDB.getNode("H021000000"));
    adjacentFour.add(MapDB.getNode("H051000000"));
    adjacentFour.add(MapDB.getNode("STAI011000"));

    String nodeFive = "H051000000";
    LinkedList<DbNode> adjacentFive = new LinkedList<DbNode>();
    // add adjacent
    adjacentFive.add(MapDB.getNode("H041000000"));
    adjacentFive.add(MapDB.getNode("ELEV021000"));

    String nodeElevTwo = "ELEV021000";
    LinkedList<DbNode> adjacentElevTwo = new LinkedList<DbNode>();
    adjacentElevTwo.add(MapDB.getNode("H051000000"));
    adjacentElevTwo.add(MapDB.getNode("ELEV022000"));
    adjacentElevTwo.add(MapDB.getNode("H061000000"));

    String nodeSix = "H061000000";
    LinkedList<DbNode> adjacentSix = new LinkedList<DbNode>();
    // add adjacent
    adjacentSix.add(MapDB.getNode("ELEV021000"));
    adjacentSix.add(MapDB.getNode("H071000000"));

    String nodeSeven = "H071000000";
    LinkedList<DbNode> adjacentSeven = new LinkedList<DbNode>();
    // add adjacent
    adjacentSeven.add(MapDB.getNode("H061000000"));
    adjacentSeven.add(MapDB.getNode("AAAAAAAAAA"));
    adjacentSeven.add(MapDB.getNode("H081000000"));

    String nodeAAA = "AAAAAAAAAA";
    LinkedList<DbNode> adjacentAAA = new LinkedList<DbNode>();
    // add adjacent
    adjacentAAA.add(MapDB.getNode("H071000000"));

    String nodeEight = "H081000000";
    LinkedList<DbNode> adjacentEight = new LinkedList<DbNode>();
    // add adjacent
    adjacentEight.add(MapDB.getNode("H071000000"));
    adjacentEight.add(MapDB.getNode("STAI011000"));

    String nodeStaiOne = "STAI011000";
    LinkedList<DbNode> adjacentStaiOne = new LinkedList<DbNode>();
    // add adjacent
    adjacentStaiOne.add(MapDB.getNode("H081000000"));
    adjacentStaiOne.add(MapDB.getNode("H041000000"));
    adjacentStaiOne.add(MapDB.getNode("STAI013000"));

    expected.put(nodeOne, adjacentOne);
    expected.put(nodeTwo, adjacentTwo);
    expected.put(nodeThree, adjacentThree);
    expected.put(nodeElevOne, adjacentElevOne);
    expected.put(nodeFour, adjacentFour);
    expected.put(nodeFive, adjacentFive);
    expected.put(nodeElevTwo, adjacentElevTwo);
    expected.put(nodeSix, adjacentSix);
    expected.put(nodeSeven, adjacentSeven);
    expected.put(nodeAAA, adjacentAAA);
    expected.put(nodeEight, adjacentEight);
    expected.put(nodeStaiOne, adjacentStaiOne);

    // check if key-value pair are the same
    Assertions.assertTrue(areHashMapsEqual(expected, actual));
  }

  public static boolean areHashMapsEqual(
      HashMap<String, LinkedList<DbNode>> expected, HashMap<String, LinkedList<DbNode>> actual) {
    boolean ans = true;
    for (String key : expected.keySet()) {
      // get edges of same key in actual hashmap and expected
      LinkedList<DbNode> correspondingEdgesActual = actual.get(key);
      LinkedList<DbNode> correspondingEdgesExpected = expected.get(key);

      // Compare the linked list, so they contain same elements but not necesserely in the same
      // order
      if (!doListHaveSameElements(correspondingEdgesActual, correspondingEdgesExpected)) {
        ans = false;
        return ans;
      }
    }
    return ans;
  }

  private static boolean doListHaveSameElements(
      LinkedList<DbNode> edgesActual, LinkedList<DbNode> edgesExpected) {
    boolean ans = true;

    // Iterate through nodes in expected edges
    for (DbNode node : edgesExpected) {
      // check if actual has the same edges
      if (!edgesActual.contains(node)) {
        ans = false;
        return ans;
      }
    }
    return ans;
  }

  /**
   * Tests that function loadMapData return empty Hashmap if no Nodes and edges are in the database
   *
   * @throws DBException
   */
  @Test
  public void testGetHashMapNoNodesAndEdges() throws DBException {
    MapDB.clearNodes();
    MapDB.clearEdges();

    HashMap<String, LinkedList<DbNode>> actual = MapDB.loadMapData();

    Assertions.assertTrue(actual.isEmpty());
  }

  /**
   * Tests performance of the function
   *
   * @throws DBException
   */
  @Test
  public void testGetHashMapPerformanceTest() throws DBException {
    // Measure performance
    long startTime = System.nanoTime();

    HashMap<String, LinkedList<DbNode>> actual = MapDB.loadMapData();

    long endTime = System.nanoTime();

    long timeElapsed = endTime - startTime;
    System.out.println(
        "Elapsed time for getAllNodes on 39 Nodes in milliseconds:" + timeElapsed / 1000000);
  }

  @AfterEach
  public void clearDB() throws DBException {
    MapDB.clearEdges();
    MapDB.clearNodes();
  }
}
