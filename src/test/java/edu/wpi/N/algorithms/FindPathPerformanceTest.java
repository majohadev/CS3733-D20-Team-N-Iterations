package edu.wpi.N.algorithms;

import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FindPathPerformanceTest {
  Algorithm myAStar = new Algorithm();

  @BeforeAll
  public static void initialize()
      throws DBException, FileNotFoundException, SQLException, ClassNotFoundException {
    MapDB.initTestDB();
    File fNodes = new File("src/test/resources/edu/wpi/N/csv/TeamNAllNodes.csv");
    File fEdges = new File("src/test/resources/edu/wpi/N/csv/TeamNAllEdges.csv");
    String pathNodes = fNodes.getAbsolutePath();
    String pathEdges = fEdges.getAbsolutePath();
    CSVParser.parseCSVfromPath(pathNodes);
    CSVParser.parseCSVfromPath(pathEdges);
  }

  /** Calculates time in miliseconds to create a path on a single floor */
  @Test
  public void testPerformanceFindPath() throws DBException {

    DbNode start = MapDB.getNode("NLABS00104");
    DbNode end = MapDB.getNode("NDEPT00504");

    long startTime = System.nanoTime();

    Path actual = myAStar.findPath(start, end, false);

    long endTime = System.nanoTime();

    long timeElapsed = endTime - startTime;
    System.out.println("Elapsed time for FindPath in milliseconds:" + timeElapsed / 1000000);
  }

  @AfterAll
  public static void clearDB() throws DBException {
    MapDB.clearNodes();
  }
}
