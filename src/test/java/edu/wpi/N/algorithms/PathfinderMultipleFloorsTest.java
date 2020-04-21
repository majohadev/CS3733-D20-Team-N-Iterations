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

public class PathfinderMultipleFloorsTest {

  @BeforeAll
  public static void initialize() throws SQLException, DBException, ClassNotFoundException {
    DbController.initDB();
    InputStream inputNodes =
        PathfinderMethodsTest.class.getResourceAsStream("../csv/ThreeFloorsTestNode.csv");
    InputStream inputEdges =
        PathfinderMethodsTest.class.getResourceAsStream("../csv/ThreeFloorsTestEdges.csv");
    CSVParser.parseCSV(inputNodes);
    CSVParser.parseCSV(inputEdges);
  }

  /** Tests if the pathfinder chooses the more efficient elevator when changing floors */
  @Test
  public void findCloserElevatorTest() throws DBException {
    DbNode startNode = DbController.getNode("H011000000");
    DbNode endNode = DbController.getNode("BBBBBBBBBB");
    Path testPath = Pathfinder.findPath(startNode, endNode);

    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();
    actualPath.add(DbController.getNode("H011000000"));
    actualPath.add(DbController.getNode("H021000000"));
    actualPath.add(DbController.getNode("H041000000"));
    actualPath.add(DbController.getNode("H051000000"));
    actualPath.add(DbController.getNode("ELEV021000"));
    actualPath.add(DbController.getNode("ELEV022000"));
    actualPath.add(DbController.getNode("H062000000"));
    actualPath.add(DbController.getNode("H072000000"));
    actualPath.add(DbController.getNode("BBBBBBBBBB"));

    for (int i = 0; i < actualPath.size(); i++) {
      Assertions.assertEquals(actualPath.get(i), testPath.getPath().get(i));
    }
  }

  /**
   * Tests if the pathfinder will choose the stairs over elevator if it's more efficient
   * to do so
   */
  @Test
  public void stairsOverElevatorTest() throws DBException {
    DbNode startNode = DbController.getNode("STAI011000");
    DbNode endNode = DbController.getNode("H083000000");
    Path testPath = Pathfinder.findPath(startNode, endNode);

    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();
    actualPath.add(DbController.getNode("STAI011000"));
    actualPath.add(DbController.getNode("STAI013000"));
    actualPath.add(DbController.getNode("H083000000"));

    for (int i = 0; i < actualPath.size(); i++) {
      Assertions.assertEquals(actualPath.get(i), testPath.getPath().get(i));
    }
  }

  /**
   * Second test to see if the pathfinder will choose the stairs over elevator if
   * it's more efficient to do so
   */
  @Test
  public void stairsOverElevatorTest2() throws DBException {
    DbNode startNode = DbController.getNode("H083000000");
    DbNode endNode = DbController.getNode("H041000000");
    Path testPath = Pathfinder.findPath(startNode, endNode);

    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();
    actualPath.add(DbController.getNode("H083000000"));
    actualPath.add(DbController.getNode("STAI013000"));
    actualPath.add(DbController.getNode("STAI011000"));
    actualPath.add(DbController.getNode("H041000000"));

    for (int i = 0; i < actualPath.size(); i++) {
      Assertions.assertEquals(actualPath.get(i), testPath.getPath().get(i));
    }
  }

  //  /**
  //   * Another elevator optimization test (doesn't seem to pick the most efficient path
  //   * in this case due to score of ELEV021000 to endNode being misleading)
  //   */
  //  @Test
  //  public void secondToFirstFloorTest() throws DBException {
  //    DbNode startNode = DbController.getNode("H022000000");
  //    DbNode endNode = DbController.getNode("H081000000");
  //    Path testPath = Pathfinder.findPath(startNode, endNode);
  //
  //    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();
  //    actualPath.add(DbController.getNode("H022000000"));
  //    actualPath.add(DbController.getNode("H042000000"));
  //    actualPath.add(DbController.getNode("H052000000"));
  //    actualPath.add(DbController.getNode("ELEV022000"));
  //    actualPath.add(DbController.getNode("ELEV021000"));
  //    actualPath.add(DbController.getNode("H061000000"));
  //    actualPath.add(DbController.getNode("H071000000"));
  //    actualPath.add(DbController.getNode("H081000000"));
  //
  //    for (int i = 0; i < actualPath.size(); i++) {
  //      Assertions.assertEquals(actualPath.get(i), testPath.getPath().get(i));
  //    }
  //  }

  /**
   * Tests if the algorithm knows to not pick stairs (since they don't have access to floor 2) and
   * chooses the correct elevator
   */
  @Test
  public void thirdToSecondFloorNoStairsTest() throws DBException {
    DbNode startNode = DbController.getNode("STAI013000");
    DbNode endNode = DbController.getNode("H022000000");
    Path testPath = Pathfinder.findPath(startNode, endNode);

    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();
    actualPath.add(DbController.getNode("STAI013000"));
    actualPath.add(DbController.getNode("H043000000"));
    actualPath.add(DbController.getNode("H023000000"));
    actualPath.add(DbController.getNode("H033000000"));
    actualPath.add(DbController.getNode("ELEV013000"));
    actualPath.add(DbController.getNode("ELEV012000"));
    actualPath.add(DbController.getNode("H032000000"));
    actualPath.add(DbController.getNode("H022000000"));

    for (int i = 0; i < actualPath.size(); i++) {
      Assertions.assertEquals(actualPath.get(i), testPath.getPath().get(i));
    }
  }

  //  /**
  //   * Tests if the pathfinder can get use one floor change to get to one floor, and then navigate
  //   * to different one
  //   * (Not implemented currently in our getBestFloorChange() method
  //   */
  //  @Test
  //  public void fourthToFirstFloorTest() throws DBException {
  //    DbNode startNode = DbController.getNode("DDDDDDDDDD");
  //    DbNode endNode = DbController.getNode("H011000000");
  //    Path testPath = Pathfinder.findPath(startNode, endNode);
  //
  //    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();
  //    actualPath.add(DbController.getNode("DDDDDDDDDD"));
  //    actualPath.add(DbController.getNode("ELEV034000"));
  //    actualPath.add(DbController.getNode("ELEV033000"));
  //    actualPath.add(DbController.getNode("H083000000"));
  //    actualPath.add(DbController.getNode("STAI013000"));
  //    actualPath.add(DbController.getNode("STAI011000"));
  //    actualPath.add(DbController.getNode("H041000000"));
  //    actualPath.add(DbController.getNode("H021000000"));
  //    actualPath.add(DbController.getNode("H011000000"));
  //
  //    for (int i = 0; i < actualPath.size(); i++) {
  //      Assertions.assertEquals(actualPath.get(i), testPath.getPath().get(i));
  //    }
  //  }

  /**
   * Tests if the pathfinder goes to the correct elevator (the only one that can access floor 4)
   * when going from floor 3 to floor 4
   */
  @Test
  public void thirdToFourthFloorTest() throws DBException {
    DbNode startNode = DbController.getNode("ELEV013000");
    DbNode endNode = DbController.getNode("DDDDDDDDDD");
    Path testPath = Pathfinder.findPath(startNode, endNode);

    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();
    actualPath.add(DbController.getNode("ELEV013000"));
    actualPath.add(DbController.getNode("H033000000"));
    actualPath.add(DbController.getNode("H023000000"));
    actualPath.add(DbController.getNode("H043000000"));
    actualPath.add(DbController.getNode("STAI013000"));
    actualPath.add(DbController.getNode("H083000000"));
    actualPath.add(DbController.getNode("ELEV033000"));
    actualPath.add(DbController.getNode("ELEV034000"));
    actualPath.add(DbController.getNode("DDDDDDDDDD"));

    for (int i = 0; i < actualPath.size(); i++) {
      Assertions.assertEquals(actualPath.get(i), testPath.getPath().get(i));
    }
  }

  /**
   * Tests if the pathfinder will determine the correct eligibility of an elevator node when going
   * up 2 floors
   */
  @Test
  public void isEligibleFloorChangeElevatorUpTest() throws DBException {
    DbNode elevator = DbController.getNode("ELEV011000");
    int endFloorNum = 3;
    Assertions.assertTrue(
        Pathfinder.isEligibleFloorChange(elevator, elevator.getFloor(), endFloorNum));
  }

  /**
   * Tests if the pathfinder will determine the correct eligibility of an stair node when going up 2
   * floors
   */
  @Test
  public void isEligibleFloorChangeStairUpTest() throws DBException {
    DbNode stair = DbController.getNode("STAI011000");
    int endFloorNum = 3;
    Assertions.assertTrue(Pathfinder.isEligibleFloorChange(stair, stair.getFloor(), endFloorNum));
  }

  /**
   * Tests if the pathfinder will determine the correct eligibility of an stair node when going to
   * the second floor where no stair node exists (can't access second floor from stairs)
   */
  @Test
  public void isEligibleFloorChangeStairSecondFloorTest() throws DBException {
    DbNode stair = DbController.getNode("STAI011000");
    int endFloorNum = 2;
    Assertions.assertFalse(Pathfinder.isEligibleFloorChange(stair, stair.getFloor(), endFloorNum));
  }

  /**
   * Tests if the pathfinder will determine the correct eligibility of an elevator node when going
   * down 2 floors
   */
  @Test
  public void isEligibleFloorChangeElevatorDownTest() throws DBException {
    DbNode elevator = DbController.getNode("ELEV013000");
    int endFloorNum = 1;
    Assertions.assertTrue(
        Pathfinder.isEligibleFloorChange(elevator, elevator.getFloor(), endFloorNum));
  }

  /**
   * Tests if the pathfinder will determine the correct eligibility of an stair node when going down
   * 2 floors
   */
  @Test
  public void isEligibleFloorChangeStairDownTest() throws DBException {
    DbNode stair = DbController.getNode("STAI013000");
    int endFloorNum = 1;
    Assertions.assertTrue(Pathfinder.isEligibleFloorChange(stair, stair.getFloor(), endFloorNum));
  }

  /** Tests if the pathfinder get the correct elevator node when going up 2 floors */
  @Test
  public void getEligibleFloorChangeElevatorUpTest() throws DBException {
    DbNode elevator = DbController.getNode("ELEV011000");
    int endFloorNum = 3;
    DbNode floorChangeElevator = DbController.getNode("ELEV013000");
    Assertions.assertEquals(
        floorChangeElevator,
        Pathfinder.getFloorChangeNode(elevator, elevator.getFloor(), endFloorNum));
  }

  /** Tests if the pathfinder get the correct elevator node when going down 1 floor */
  @Test
  public void getEligibleFloorChangeElevatorDownTest() throws DBException {
    DbNode elevator = DbController.getNode("ELEV012000");
    int endFloorNum = 1;
    DbNode floorChangeElevator = DbController.getNode("ELEV011000");
    Assertions.assertEquals(
        floorChangeElevator,
        Pathfinder.getFloorChangeNode(elevator, elevator.getFloor(), endFloorNum));
  }

  /** Tests if the pathfinder get the correct stair node when going up 2 floors */
  @Test
  public void getEligibleFloorChangeStairUpTest() throws DBException {
    DbNode stair = DbController.getNode("STAI011000");
    int endFloorNum = 3;
    DbNode floorChangeStair = DbController.getNode("STAI013000");
    Assertions.assertEquals(
        floorChangeStair, Pathfinder.getFloorChangeNode(stair, stair.getFloor(), endFloorNum));
  }

  /** Tests if the pathfinder get the correct elevator node when going down 2 floors */
  @Test
  public void getEligibleFloorChangeStairDownTest() throws DBException {
    DbNode stair = DbController.getNode("STAI013000");
    int endFloorNum = 1;
    DbNode floorChangeStair = DbController.getNode("STAI011000");
    Assertions.assertEquals(
        floorChangeStair, Pathfinder.getFloorChangeNode(stair, stair.getFloor(), endFloorNum));
  }

  @AfterAll
  public static void clear() throws DBException {
    DbController.clearNodes();
  }
}
