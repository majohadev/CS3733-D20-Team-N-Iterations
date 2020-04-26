package edu.wpi.N.algorithms;

import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.LinkedList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BFSMultipleFloorsTest {
  Algorithm myBFS = new Algorithm();

  @BeforeAll
  public static void initialize()
      throws SQLException, DBException, ClassNotFoundException, FileNotFoundException {
    MapDB.initTestDB();
    InputStream inputNodes =
        BFSMultipleFloorsTest.class.getResourceAsStream("../csv/ThreeFloorsTestNode.csv");
    InputStream inputEdges =
        BFSMultipleFloorsTest.class.getResourceAsStream("../csv/ThreeFloorsTestEdges.csv");
    CSVParser.parseCSV(inputNodes);
    CSVParser.parseCSV(inputEdges);
  }

  /** Tests if the pathfinder chooses the more efficient elevator when changing floors */
  @Test
  public void findCloserElevatorBFSTest() throws DBException {
    myBFS.setPathFinder(new BFS());
    DbNode startNode = MapDB.getNode("H011000000");
    DbNode endNode = MapDB.getNode("BBBBBBBBBB");
    Path testPath = myBFS.findPath(startNode, endNode, false);

    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();
    actualPath.add(MapDB.getNode("H011000000"));
    actualPath.add(MapDB.getNode("H021000000"));
    actualPath.add(MapDB.getNode("H041000000"));
    actualPath.add(MapDB.getNode("H051000000"));
    actualPath.add(MapDB.getNode("ELEV021000"));
    actualPath.add(MapDB.getNode("ELEV022000"));
    actualPath.add(MapDB.getNode("H062000000"));
    actualPath.add(MapDB.getNode("H072000000"));
    actualPath.add(MapDB.getNode("BBBBBBBBBB"));

    Assertions.assertEquals(actualPath, testPath.getPath());
  }

  /**
   * Tests if the pathfinder will choose the stairs over elevator if it's more efficient to do so
   */
  @Test
  public void stairsOverElevatorBFSTest() throws DBException {
    myBFS.setPathFinder(new BFS());
    DbNode startNode = MapDB.getNode("STAI011000");
    DbNode endNode = MapDB.getNode("H083000000");
    Path testPath = myBFS.findPath(startNode, endNode, false);

    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();
    actualPath.add(MapDB.getNode("STAI011000"));
    actualPath.add(MapDB.getNode("STAI013000"));
    actualPath.add(MapDB.getNode("H083000000"));

    Assertions.assertEquals(actualPath, testPath.getPath());
  }

  /**
   * Second test to see if the pathfinder will choose the stairs over elevator if it's more
   * efficient to do so
   */
  @Test
  public void stairsOverElevatorBFSTest2() throws DBException {
    myBFS.setPathFinder(new BFS());
    DbNode startNode = MapDB.getNode("H083000000");
    DbNode endNode = MapDB.getNode("H041000000");
    Path testPath = myBFS.findPath(startNode, endNode, false);

    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();
    actualPath.add(MapDB.getNode("H083000000"));
    actualPath.add(MapDB.getNode("STAI013000"));
    actualPath.add(MapDB.getNode("STAI011000"));
    actualPath.add(MapDB.getNode("H041000000"));

    Assertions.assertEquals(actualPath, testPath.getPath());
  }

  /**
   * Tests if the pathfinder goes to the correct elevator (the only one that can access floor 4)
   * when going from floor 3 to floor 4
   */
  @Test
  public void thirdToFourthFloorBFSTest() throws DBException {
    myBFS.setPathFinder(new BFS());
    DbNode startNode = MapDB.getNode("ELEV013000");
    DbNode endNode = MapDB.getNode("DDDDDDDDDD");
    Path testPath = myBFS.findPath(startNode, endNode, false);

    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();
    actualPath.add(MapDB.getNode("ELEV013000"));
    actualPath.add(MapDB.getNode("H033000000"));
    actualPath.add(MapDB.getNode("H023000000"));
    actualPath.add(MapDB.getNode("H043000000"));
    actualPath.add(MapDB.getNode("STAI013000"));
    actualPath.add(MapDB.getNode("H083000000"));
    actualPath.add(MapDB.getNode("ELEV033000"));
    actualPath.add(MapDB.getNode("ELEV034000"));
    actualPath.add(MapDB.getNode("DDDDDDDDDD"));

    Assertions.assertEquals(actualPath, testPath.getPath());
  }

  /**
   * Tests if the pathfinder goes to the correct elevator (the only one that can access floor 4)
   * when going from floor 3 to floor 4 with handicap selected (ignores stairs)
   */
  @Test
  public void thirdToFourthFloorHandicapBFSTest() throws DBException {
    myBFS.setPathFinder(new BFS());
    DbNode startNode = MapDB.getNode("ELEV013000");
    DbNode endNode = MapDB.getNode("DDDDDDDDDD");
    Path testPath = myBFS.findPath(startNode, endNode, true);

    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();
    actualPath.add(MapDB.getNode("ELEV013000"));
    actualPath.add(MapDB.getNode("H033000000"));
    actualPath.add(MapDB.getNode("H023000000"));
    actualPath.add(MapDB.getNode("H043000000"));
    actualPath.add(MapDB.getNode("H053000000"));
    actualPath.add(MapDB.getNode("ELEV023000"));
    actualPath.add(MapDB.getNode("H063000000"));
    actualPath.add(MapDB.getNode("H073000000"));
    actualPath.add(MapDB.getNode("H083000000"));
    actualPath.add(MapDB.getNode("ELEV033000"));
    actualPath.add(MapDB.getNode("ELEV034000"));
    actualPath.add(MapDB.getNode("DDDDDDDDDD"));

    Assertions.assertEquals(actualPath, testPath.getPath());
  }

  /**
   * Tests if the pathfinder will choose an elevator over stairs since it's a handicap accessible
   * path
   */
  @Test
  public void elevatorOverStairHandicapBFSTest() throws DBException {
    myBFS.setPathFinder(new BFS());
    DbNode startNode = MapDB.getNode("STAI011000");
    DbNode endNode = MapDB.getNode("H083000000");
    Path testPath = myBFS.findPath(startNode, endNode, true);

    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();
    actualPath.add(MapDB.getNode("STAI011000"));
    actualPath.add(MapDB.getNode("H041000000"));
    actualPath.add(MapDB.getNode("H051000000"));
    actualPath.add(MapDB.getNode("ELEV021000"));
    actualPath.add(MapDB.getNode("ELEV022000"));
    actualPath.add(MapDB.getNode("ELEV023000"));
    actualPath.add(MapDB.getNode("H063000000"));
    actualPath.add(MapDB.getNode("H073000000"));
    actualPath.add(MapDB.getNode("H083000000"));

    Assertions.assertEquals(actualPath, testPath.getPath());
  }

  @AfterAll
  public static void clear() throws DBException {
    MapDB.clearNodes();
  }
}
