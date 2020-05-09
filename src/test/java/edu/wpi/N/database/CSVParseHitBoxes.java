package edu.wpi.N.database;

import edu.wpi.N.Main;
import edu.wpi.N.entities.DbNode;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CSVParseHitBoxes {

  @BeforeAll
  public static void initialize() throws SQLException, ClassNotFoundException {
    MapDB.initTestDB();

    InputStream inputNodes = Main.class.getResourceAsStream("csv/newNodes.csv");
    InputStream inputHitboxes = Main.class.getResourceAsStream("csv/testHitBoxes.csv");
    CSVParser.parseCSV(inputNodes);
    CSVParser.parseCSVHitBoxes(inputHitboxes);
  }

  @Test
  public void testParseHitBoxesSuccessfully() throws DBException {
    DbNode expected = MapDB.getNode("GDEPT00702");

    Assertions.assertEquals(expected, MapDB.checkHitbox(11, 11, "Shapiro", 5));

    String allHitBoxesExpected =
        "x1,y1,x2,y2,nodeID\n"
            + "1,1,2,2,FSERV00201\n"
            + "5,5,7,7,GDEPT00403\n"
            + "10,10,15,15,GDEPT00702\n";

    String allHitBoxesActual = MapDB.exportHitboxes();
    Assertions.assertEquals(allHitBoxesExpected, allHitBoxesActual);
  }

  @Test
  public void testParseHitBoxNull() throws DBException {
    Assertions.assertNull(MapDB.checkHitbox(16, 16, "Shapiro", 5));
  }

  @Test
  public void testExportHitBoxesToCSV() throws DBException, IOException {
    CSVParser.exportExistingHitBoxesToCSV();
  }

  @AfterAll
  public static void clear() throws DBException {
    MapDB.clearNodes();
  }
}
