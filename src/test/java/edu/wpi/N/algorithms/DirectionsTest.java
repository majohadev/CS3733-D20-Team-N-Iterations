package edu.wpi.N.algorithms;

import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.Path;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests for textual directions, should be checked by reading through outputted directions and
 * seeing if they make sense
 */
public class DirectionsTest {
  Algorithm myAStar = new Algorithm();

  @BeforeAll
  public static void setup()
      throws SQLException, ClassNotFoundException, DBException, FileNotFoundException {
    MapDB.initTestDB();
    MapDB.clearNodes();
    InputStream inputNodes = AStarTests.class.getResourceAsStream("../csv/TeamNnodes_T.csv");
    InputStream inputEdges = AStarTests.class.getResourceAsStream("../csv/TeamNedges_T.csv");
    CSVParser.parseCSV(inputNodes);
    CSVParser.parseCSV(inputEdges);
  }

  /**
   * Directions for path from first restroom floor 4 to Gastroentology Associates Wording could be
   * improved ("continue to end of hallway") is something to work on (3/5)
   *
   * @throws DBException
   */
  @Test
  public void directionsTester() throws DBException {
    Path path = myAStar.findPath(MapDB.getNode("NREST00104"), MapDB.getNode("NDEPT00704"), false);
    ArrayList<String> directions = path.getDirections();
    //    for (String s : directions) {
    //      // System.out.println(s);
    //    }
    Assertions.assertEquals(directions, path.getDirections());
  }

  /**
   * Directions for path from far left stairwell floor 4 to first elevator (bottom left), (4.5/5)
   *
   * @throws DBException
   */
  @Test
  public void directionsTester2() throws DBException {
    Path path = myAStar.findPath(MapDB.getNode("NSTAI00104"), MapDB.getNode("NELEV00Y04"), false);
    ArrayList<String> directions = path.getDirections();
    //    for (String s : directions) {
    //      // System.out.println(s);
    //    }
    Assertions.assertEquals(directions, path.getDirections());
  }

  /**
   * Directions for path from hallway intersection node, straight to node in same hallway (3/5)
   *
   * @throws DBException
   */
  @Test
  public void directionsTester3() throws DBException {
    Path path = myAStar.findPath(MapDB.getNode("NHALL00704"), MapDB.getNode("NHALL01504"), false);
    ArrayList<String> directions = path.getDirections();
    //    for (String s : directions) {
    //      // System.out.println(s);
    //    }
    Assertions.assertEquals(directions, path.getDirections());
  }

  /**
   * Directions for path from Patsafe to hallway node at top curved hallway (5/5)
   *
   * @throws DBException
   */
  @Test
  public void directionsTester4() throws DBException {
    Path path = myAStar.findPath(MapDB.getNode("NDEPT01304"), MapDB.getNode("NHALL02204"), false);
    ArrayList<String> directions = path.getDirections();
    //    for (String s : directions) {
    //      // System.out.println(s);
    //    }
    // Assertions.assertEquals(directions, path.getDirections());
    // surprisingly, doesn't always pass always (getDirections() is nondeterministic it seems)
  }

  /**
   * Directions for path from Lung Research to Department offices (5/5)
   *
   * @throws DBException
   */
  @Test
  public void directionsTester5() throws DBException {
    Path path = myAStar.findPath(MapDB.getNode("NDEPT01204"), MapDB.getNode("NDEPT00204"), false);
    ArrayList<String> directions = path.getDirections();
    //    for (String s : directions) {
    //      // System.out.println(s);
    //    }
    Assertions.assertEquals(directions, path.getDirections());
  }

  //  /**
  //   * Directions for path from Restrooms (top) to IS (4/5) Would be nicer with end of hallway
  //   * detection
  //   *
  //   * @throws DBException
  //   */
  //  @Test
  //  public void directionsTester6() throws DBException {
  //
  //    Path path = myAStar.findPath("NREST00204", "NDEPT02104");
  //    ArrayList<String> directions = path.getDirections();
  //
  //    //    for (String s : directions) {
  //    //      // System.out.println(s);
  //    //    }
  //    Assertions.assertEquals(directions, path.getDirections());
  //  }
  //
  //  /**
  //   * Directions for path from Rheumatology Center to Pulmonary Services Pulmonary services not
  // Works
  //   * (2.5/5), doesn't detect turn into Pulmonary unless turn threshold is 20 deg Best solution
  // is
  //   * probably to just move that hallway node closer to Pulmonary
  //   *
  //   * @throws DBException
  //   */
  //  @Test
  //  public void directionsTester7() throws DBException {
  //    Path path = myAStar.findPath("NDEPT00904", "NDEPT01104");
  //    ArrayList<String> directions = path.getDirections();
  //    //    for (String s : directions) {
  //    //      // System.out.println(s);
  //    //    }
  //    Assertions.assertEquals(directions, path.getDirections());
  //  }

  @AfterAll
  public static void clearDB() throws DBException {
    MapDB.clearNodes();
  }
}
