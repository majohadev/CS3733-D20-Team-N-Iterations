package edu.wpi.N.algorithms;

import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import java.io.InputStream;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

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

  @AfterAll
  public static void clear() throws DBException {
    DbController.clearNodes();
  }
}
