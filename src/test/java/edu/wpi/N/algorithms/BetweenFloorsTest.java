package edu.wpi.N.algorithms;

import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.LinkedList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BetweenFloorsTest {

  @BeforeAll
  public static void initializeTest()
      throws SQLException, ClassNotFoundException, DBException, FileNotFoundException {
    MapDB.initTestDB();
    InputStream inputNodes =
        BetweenFloorsTest.class.getResourceAsStream("../csv/ThreeFloorsTestNode.csv");
    InputStream inputEdges =
        BetweenFloorsTest.class.getResourceAsStream("../csv/ThreeFloorsTestEdges.csv");
    CSVParser.parseCSV(inputNodes);
    CSVParser.parseCSV(inputEdges);
  }

  @Test
  public void edgesBetweenFloorsTester() throws DBException {
    LinkedList<DbNode[]> edgesBetweenFloors =
        AbsAlgo.getEdgesBetweenFloors(MapDB.getNode("ELEV021000"));
    DbNode[] firstEdge = edgesBetweenFloors.getFirst();
    DbNode[] secondEdge = edgesBetweenFloors.get(1);
    Assertions.assertEquals(firstEdge[0].getNodeID(), "ELEV021000");
    Assertions.assertEquals(firstEdge[1].getNodeID(), "ELEV022000");
    Assertions.assertEquals(secondEdge[0].getNodeID(), "ELEV022000");
    Assertions.assertEquals(secondEdge[1].getNodeID(), "ELEV023000");
    Assertions.assertTrue(edgesBetweenFloors.size() == 2);
  }

  @Test
  public void edgesBetweenFloorsTester2() throws DBException {
    LinkedList<DbNode[]> edgesBetweenFloors =
        AbsAlgo.getEdgesBetweenFloors(MapDB.getNode("ELEV033000"));
    DbNode[] firstEdge = edgesBetweenFloors.getFirst();
    System.out.println(firstEdge[0].getNodeID());
    System.out.println(firstEdge[1].getNodeID());
    Assertions.assertTrue(edgesBetweenFloors.size() == 1);
  }

  @Test
  public void edgesBetweenFloorsTester3() throws DBException {
    LinkedList<DbNode[]> edgesBetweenFloors =
        AbsAlgo.getEdgesBetweenFloors(MapDB.getNode("STAI011000"));
    DbNode[] firstEdge = edgesBetweenFloors.getFirst();
    System.out.println(firstEdge[0].getNodeID());
    System.out.println(firstEdge[1].getNodeID());
    Assertions.assertTrue(edgesBetweenFloors.size() == 1);
  }

  @Test
  public void edgesBetweenFloorsTester4() throws DBException {
    LinkedList<DbNode[]> edgesBetweenFloors =
        AbsAlgo.getEdgesBetweenFloors(MapDB.getNode("ELEV011000"));
    DbNode[] firstEdge = edgesBetweenFloors.getFirst();
    DbNode[] secondEdge = edgesBetweenFloors.get(1);
    System.out.println(firstEdge[0].getNodeID());
    System.out.println(firstEdge[1].getNodeID());
    System.out.println(secondEdge[0].getNodeID());
    System.out.println(secondEdge[1].getNodeID());
    Assertions.assertTrue(edgesBetweenFloors.size() == 2);
  }

  @AfterAll
  public static void clearDB() throws DBException {
    MapDB.clearNodes();
  }
}
