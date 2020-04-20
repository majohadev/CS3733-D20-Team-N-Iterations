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

  @Test
  public void isEligibleFloorChangeElevatorUpTest() throws DBException {
    DbNode elevator = DbController.getNode("ELEV011000");
    int endFloorNum = 3;
    Assertions.assertTrue(
        Pathfinder.isEligibleFloorChange(elevator, elevator.getFloor(), endFloorNum));
  }

  @Test
  public void isEligibleFloorChangeStairTest() throws DBException {
    DbNode stair = DbController.getNode("STAI011000");
    int endFloorNum = 3;
    Assertions.assertTrue(Pathfinder.isEligibleFloorChange(stair, stair.getFloor(), endFloorNum));
  }

  @Test
  public void isEligibleFloorChangeStairSecondFloorTest() throws DBException {
    DbNode stair = DbController.getNode("STAI011000");
    int endFloorNum = 2;
    Assertions.assertFalse(Pathfinder.isEligibleFloorChange(stair, stair.getFloor(), endFloorNum));
  }

  @Test
  public void isEligibleFloorChangeElevatorDownTest() throws DBException {
    DbNode elevator = DbController.getNode("ELEV013000");
    int endFloorNum = 1;
    Assertions.assertTrue(
        Pathfinder.isEligibleFloorChange(elevator, elevator.getFloor(), endFloorNum));
  }

  @Test
  public void isEligibleFloorChangeStairDownTest() throws DBException {
    DbNode stair = DbController.getNode("STAI013000");
    int endFloorNum = 1;
    Assertions.assertTrue(Pathfinder.isEligibleFloorChange(stair, stair.getFloor(), endFloorNum));
  }

  @AfterAll
  public static void clear() throws DBException {
    DbController.clearNodes();
  }
}
