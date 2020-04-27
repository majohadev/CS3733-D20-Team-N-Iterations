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

public class AStarMultipleFloorsTest {
  Algorithm myAStar = new Algorithm();

  @BeforeAll
  public static void initialize()
      throws SQLException, DBException, ClassNotFoundException, FileNotFoundException {
    MapDB.initTestDB();
    InputStream inputNodes =
        AStarMultipleFloorsTest.class.getResourceAsStream("../csv/ThreeFloorsTestNode.csv");
    InputStream inputEdges =
        AStarMultipleFloorsTest.class.getResourceAsStream("../csv/ThreeFloorsTestEdges.csv");
    CSVParser.parseCSV(inputNodes);
    CSVParser.parseCSV(inputEdges);
  }

  /** Tests if the pathfinder chooses the more efficient elevator when changing floors */
  @Test
  public void findCloserElevatorTest() throws DBException {
    DbNode startNode = MapDB.getNode("H011000000");
    DbNode endNode = MapDB.getNode("BBBBBBBBBB");
    Path testPath = myAStar.findPath(startNode, endNode, false);

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
  public void stairsOverElevatorAStarTest() throws DBException {
    DbNode startNode = MapDB.getNode("STAI011000");
    DbNode endNode = MapDB.getNode("H083000000");
    Path testPath = myAStar.findPath(startNode, endNode, false);

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
  public void stairsOverElevatorAStarTest2() throws DBException {
    DbNode startNode = MapDB.getNode("H083000000");
    DbNode endNode = MapDB.getNode("H041000000");
    Path testPath = myAStar.findPath(startNode, endNode, false);

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
  public void thirdToFourthFloorAStarTest() throws DBException {
    DbNode startNode = MapDB.getNode("ELEV013000");
    DbNode endNode = MapDB.getNode("DDDDDDDDDD");
    Path testPath = myAStar.findPath(startNode, endNode, false);

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
  public void thirdToFourthFloorHandicapAStarTest() throws DBException {
    DbNode startNode = MapDB.getNode("ELEV013000");
    DbNode endNode = MapDB.getNode("DDDDDDDDDD");
    Path testPath = myAStar.findPath(startNode, endNode, true);

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
  public void elevatorOverStairHandicapAStarTest() throws DBException {
    DbNode startNode = MapDB.getNode("STAI011000");
    DbNode endNode = MapDB.getNode("H083000000");
    Path testPath = myAStar.findPath(startNode, endNode, true);

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

  //  /**
  //   * Tests if the pathfinder can get use one floor change to get to one floor, and then navigate
  // to
  //   * different one Doesn't currently work due to what GetGAdjacent returns
  //   */
  //  @Test
  //  public void fourthToFirstFloorAStarTest() throws DBException {
  //    DbNode startNode = MapDB.getNode("DDDDDDDDDD");
  //    DbNode endNode = MapDB.getNode("H011000000");
  //    Path testPath = myAStar.findPath(startNode, endNode, false);
  //
  //    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();
  //    actualPath.add(MapDB.getNode("DDDDDDDDDD"));
  //    actualPath.add(MapDB.getNode("ELEV034000"));
  //    actualPath.add(MapDB.getNode("ELEV033000"));
  //    actualPath.add(MapDB.getNode("H083000000"));
  //    actualPath.add(MapDB.getNode("STAI013000"));
  //    actualPath.add(MapDB.getNode("STAI011000"));
  //    actualPath.add(MapDB.getNode("H041000000"));
  //    actualPath.add(MapDB.getNode("H021000000"));
  //    actualPath.add(MapDB.getNode("H011000000"));
  //
  //    Assertions.assertEquals(actualPath, testPath.getPath());
  //  }

  @AfterAll
  public static void clear() throws DBException {
    MapDB.clearNodes();
  }
}
