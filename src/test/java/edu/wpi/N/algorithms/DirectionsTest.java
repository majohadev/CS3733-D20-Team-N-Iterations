package edu.wpi.N.algorithms;

import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import edu.wpi.N.entities.Path;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DirectionsTest {
  @BeforeAll
  public static void setup() throws SQLException, ClassNotFoundException, DBException {
    DbController.initDB();
    DbController.clearNodes();
    InputStream inputNodes =
        PathfinderMethodsTest.class.getResourceAsStream("../csv/TeamNnodes_T.csv");
    InputStream inputEdges =
        PathfinderMethodsTest.class.getResourceAsStream("../csv/TeamNedges_T.csv");
    CSVParser.parseCSV(inputNodes);
    CSVParser.parseCSV(inputEdges);
  }

  @Test
  public void directionsTester() throws DBException {
    Path path = Pathfinder.findPath("NDEPT00104", "NDEPT00604");
    ArrayList<String> directions = path.getDirections();
    for (String s : directions) {
      System.out.println(s);
    }
  }
}
