package edu.wpi.N.algorithms;

import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;

public class GetPathWithStopTest {
  Algorithm myAlgo = new Algorithm();

  @BeforeAll
  public static void initialize()
      throws SQLException, DBException, ClassNotFoundException, FileNotFoundException {
    MapDB.initTestDB();
    InputStream inputNodes =
        GetPathWithStopTest.class.getResourceAsStream("../csv/TeamNAllNodes.csv");
    InputStream inputEdges =
        GetPathWithStopTest.class.getResourceAsStream("../csv/TeamNAllEdges.csv");
    CSVParser.parseCSV(inputNodes);
    CSVParser.parseCSV(inputEdges);
  }

  @Test
  public void getPathWithStopRestroomSameFloorTest() throws DBException {
      DbNode start = MapDB.getNode("");
      DbNode end = MapDB.getNode("");
      DbNode stop = MapDB.getNode("");

      //myAlgo.getPathWithStop()
  }
}
