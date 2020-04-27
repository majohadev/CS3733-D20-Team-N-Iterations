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
import org.bridj.util.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GetPathWithStopDFSTest {
  Algorithm myDFS = new Algorithm();

  @BeforeAll
  public static void initialize()
      throws SQLException, DBException, ClassNotFoundException, FileNotFoundException {
    MapDB.initTestDB();
    InputStream inputNodes =
        GetPathWithStopDFSTest.class.getResourceAsStream("../csv/ThreeFloorsTestNode.csv");
    InputStream inputEdges =
        GetPathWithStopDFSTest.class.getResourceAsStream("../csv/ThreeFloorsTestEdges.csv");
    CSVParser.parseCSV(inputNodes);
    CSVParser.parseCSV(inputEdges);
  }

  /**
   * Tests if getPathWithStop() given a node will generate the correct path to the start node the
   * stop node then to the end node (stop node on same floor)
   */
  @Test
  public void getPathWithStopNodeSameFloorDFSTest() throws DBException {
    myDFS.setPathFinder(new DFS());
    DbNode start = MapDB.getNode("H011000000");
    DbNode end = MapDB.getNode("H081000000");
    DbNode stop = MapDB.getNode("H061000000");
    Pair<Path, Path> actualPathPair = myDFS.getPathWithStop(start, end, stop, false);

    LinkedList<DbNode> testPathToStop = new LinkedList<>();
    testPathToStop.add(MapDB.getNode("H011000000"));
    testPathToStop.add(MapDB.getNode("H021000000"));
    testPathToStop.add(MapDB.getNode("H041000000"));
    testPathToStop.add(MapDB.getNode("STAI011000"));
    testPathToStop.add(MapDB.getNode("H081000000"));
    testPathToStop.add(MapDB.getNode("H071000000"));
    testPathToStop.add(MapDB.getNode("H061000000"));

    LinkedList<DbNode> testPathToEnd = new LinkedList<>();
    testPathToEnd.add(MapDB.getNode("H061000000"));
    testPathToEnd.add(MapDB.getNode("H071000000"));
    testPathToEnd.add(MapDB.getNode("H081000000"));

    Assertions.assertEquals(testPathToStop, actualPathPair.getKey().getPath());
    Assertions.assertEquals(testPathToEnd, actualPathPair.getValue().getPath());
  }

  /**
   * Tests if getPathWithStop() given a node will generate a path from the start node to the stop
   * node of the given type then to the end node (stop node on a different floor)
   */
  @Test
  public void getPathWithStopNodeDifferentFloorDFSTest() throws DBException {
    myDFS.setPathFinder(new DFS());
    DbNode start = MapDB.getNode("H011000000");
    DbNode end = MapDB.getNode("H083000000");
    DbNode stop = MapDB.getNode("H062000000");
    Pair<Path, Path> actualPathPair = myDFS.getPathWithStop(start, end, stop, false);

    LinkedList<DbNode> testPathToStop = new LinkedList<>();
    testPathToStop.add(MapDB.getNode("H011000000"));
    testPathToStop.add(MapDB.getNode("H021000000"));
    testPathToStop.add(MapDB.getNode("H041000000"));
    testPathToStop.add(MapDB.getNode("STAI011000"));
    testPathToStop.add(MapDB.getNode("H081000000"));
    testPathToStop.add(MapDB.getNode("H071000000"));
    testPathToStop.add(MapDB.getNode("H061000000"));
    testPathToStop.add(MapDB.getNode("ELEV021000"));
    testPathToStop.add(MapDB.getNode("ELEV022000"));
    testPathToStop.add(MapDB.getNode("H062000000"));

    LinkedList<DbNode> testPathToEnd = new LinkedList<>();
    testPathToEnd.add(MapDB.getNode("H062000000"));
    testPathToEnd.add(MapDB.getNode("H072000000"));
    testPathToEnd.add(MapDB.getNode("H082000000"));
    testPathToEnd.add(MapDB.getNode("H092000000"));
    testPathToEnd.add(MapDB.getNode("H042000000"));
    testPathToEnd.add(MapDB.getNode("H022000000"));
    testPathToEnd.add(MapDB.getNode("H032000000"));
    testPathToEnd.add(MapDB.getNode("ELEV012000"));
    testPathToEnd.add(MapDB.getNode("ELEV013000"));
    testPathToEnd.add(MapDB.getNode("H033000000"));
    testPathToEnd.add(MapDB.getNode("H023000000"));
    testPathToEnd.add(MapDB.getNode("H043000000"));
    testPathToEnd.add(MapDB.getNode("STAI013000"));
    testPathToEnd.add(MapDB.getNode("H083000000"));

    Assertions.assertEquals(testPathToStop, actualPathPair.getKey().getPath());
    Assertions.assertEquals(testPathToEnd, actualPathPair.getValue().getPath());
  }

  /**
   * Tests if getPathWithStop() given a node type will generate a path from the start node to a stop
   * node of the given type then to the end node (stop node type is on same floor)
   */
  @Test
  public void getPathWithStopNodeTypeSameFloorDFSTest() throws DBException {
    myDFS.setPathFinder(new DFS());
    DbNode start = MapDB.getNode("H011000000");
    DbNode end = MapDB.getNode("H061000000");
    Pair<Path, Path> actualPathPair = myDFS.getPathWithStop(start, end, "STAI", false);

    LinkedList<DbNode> testPathToStop = new LinkedList<>();
    testPathToStop.add(MapDB.getNode("H011000000"));
    testPathToStop.add(MapDB.getNode("H021000000"));
    testPathToStop.add(MapDB.getNode("H041000000"));
    testPathToStop.add(MapDB.getNode("STAI011000"));

    LinkedList<DbNode> testPathToEnd = new LinkedList<>();
    testPathToEnd.add(MapDB.getNode("STAI011000"));
    testPathToEnd.add(MapDB.getNode("H081000000"));
    testPathToEnd.add(MapDB.getNode("H071000000"));
    testPathToEnd.add(MapDB.getNode("H061000000"));

    Assertions.assertEquals(testPathToStop, actualPathPair.getKey().getPath());
    Assertions.assertEquals(testPathToEnd, actualPathPair.getValue().getPath());
  }

  /**
   * Tests if getPathWithStop() given a node type will generate a path from the start node to a stop
   * node of the given type then to the end node (stop node type is on a different floor)
   */
  @Test
  public void getPathWithStopNodeTypeDifferentFloorDFSTest() throws DBException {
    myDFS.setPathFinder(new DFS());
    DbNode start = MapDB.getNode("H092000000");
    DbNode end = MapDB.getNode("H083000000");
    Pair<Path, Path> actualPathPair = myDFS.getPathWithStop(start, end, "STAI", false);

    LinkedList<DbNode> testPathToStop = new LinkedList<>();
    testPathToStop.add(MapDB.getNode("H092000000"));
    testPathToStop.add(MapDB.getNode("H082000000"));
    testPathToStop.add(MapDB.getNode("H072000000"));
    testPathToStop.add(MapDB.getNode("H062000000"));
    testPathToStop.add(MapDB.getNode("ELEV022000"));
    testPathToStop.add(MapDB.getNode("ELEV023000"));
    testPathToStop.add(MapDB.getNode("H063000000"));
    testPathToStop.add(MapDB.getNode("H073000000"));
    testPathToStop.add(MapDB.getNode("H083000000"));
    testPathToStop.add(MapDB.getNode("STAI013000"));

    LinkedList<DbNode> testPathToEnd = new LinkedList<>();
    testPathToEnd.add(MapDB.getNode("STAI013000"));
    testPathToEnd.add(MapDB.getNode("H083000000"));

    Assertions.assertEquals(testPathToStop, actualPathPair.getKey().getPath());
    Assertions.assertEquals(testPathToEnd, actualPathPair.getValue().getPath());
  }

  /**
   * Tests if getPathWithStop() given a node will generate a handicap accessible path from the start
   * node to a stop node of the given type then to the end node (stop node is on a different floor)
   */
  @Test
  public void getPathWithStopNodeDifferentFloorHandicapDFSTest() throws DBException {
    myDFS.setPathFinder(new DFS());
    DbNode start = MapDB.getNode("H021000000");
    DbNode end = MapDB.getNode("H053000000");
    DbNode stop = MapDB.getNode("H081000000");
    Pair<Path, Path> actualPathPair = myDFS.getPathWithStop(start, end, stop, true);

    LinkedList<DbNode> testPathToStop = new LinkedList<>();
    testPathToStop.add(MapDB.getNode("H021000000"));
    testPathToStop.add(MapDB.getNode("H041000000"));
    testPathToStop.add(MapDB.getNode("H051000000"));
    testPathToStop.add(MapDB.getNode("ELEV021000"));
    testPathToStop.add(MapDB.getNode("H061000000"));
    testPathToStop.add(MapDB.getNode("H071000000"));
    testPathToStop.add(MapDB.getNode("H081000000"));

    LinkedList<DbNode> testPathToEnd = new LinkedList<>();
    testPathToEnd.add(MapDB.getNode("H081000000"));
    testPathToEnd.add(MapDB.getNode("H071000000"));
    testPathToEnd.add(MapDB.getNode("H061000000"));
    testPathToEnd.add(MapDB.getNode("ELEV021000"));
    testPathToEnd.add(MapDB.getNode("ELEV022000"));
    testPathToEnd.add(MapDB.getNode("ELEV023000"));
    testPathToEnd.add(MapDB.getNode("H053000000"));

    Assertions.assertEquals(testPathToStop, actualPathPair.getKey().getPath());
    Assertions.assertEquals(testPathToEnd, actualPathPair.getValue().getPath());
  }

  @AfterAll
  public static void clear() throws DBException {
    MapDB.clearNodes();
  }
}
