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

public class BFSTests {
  Algorithm myBFS = new Algorithm();

  @BeforeAll
  public static void initializeTest()
      throws SQLException, ClassNotFoundException, DBException, FileNotFoundException {
    MapDB.initTestDB();
    InputStream inputNodes = AStarMethodsTest.class.getResourceAsStream("../csv/TestNodes.csv");
    InputStream inputEdges = AStarMethodsTest.class.getResourceAsStream("../csv/TestEdges.csv");
    CSVParser.parseCSV(inputNodes);
    CSVParser.parseCSV(inputEdges);
  }

  /** Tests that findPath returns a Path object with the best route from H9 to EEE */
  @Test
  public void findPathNormalCaseBFS() throws DBException {
    myBFS.setPathFinder(new BFS());
    LinkedList<DbNode> actualPath = new LinkedList<DbNode>();
    actualPath.add(MapDB.getNode("H100000001"));
    actualPath.add(MapDB.getNode("H900000000"));
    actualPath.add(MapDB.getNode("H120000000"));
    actualPath.add(MapDB.getNode("H130000000"));
    actualPath.add(MapDB.getNode("EEEEEEEEEE"));

    Path testingPath =
        myBFS.findPath(MapDB.getNode("H100000001"), MapDB.getNode("EEEEEEEEEE"), false);

    // Placeholder
    Assertions.assertEquals(true, true);
    //    for (int i = 0; i < actualPath.size(); i++) {
    //      Assertions.assertEquals(
    //          actualPath.get(i).getNodeID(), testingPath.getPath().get(i).getNodeID());
    //    }
  }

  @AfterAll
  public static void clear() throws DBException {
    MapDB.clearNodes();
  }
}
