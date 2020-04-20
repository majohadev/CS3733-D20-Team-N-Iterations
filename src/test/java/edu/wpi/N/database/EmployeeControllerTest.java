package edu.wpi.N.database;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.N.entities.Service;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EmployeeControllerTest {
  @BeforeAll
  public static void setup() throws DBException, SQLException, ClassNotFoundException {
    DbController.initDB();
  }

  @Test
  public void testGetServices() throws DBException {
    LinkedList<Service> res = EmployeeController.getServices();
    assertEquals(2, res.size());

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    assertEquals(formatter.parse("00:00").toString(), res.get(0).getStartTime().toString());
    assertEquals(formatter.parse("00:00").toString(), res.get(0).getEndTime().toString());
    assertEquals("Translator", res.get(0).getServiceType());
    assertEquals("Make a request for our translation services!", res.get(0).getDescription());

    assertTrue(
        res.contains(
            new Service(
                "00:00", "00:00", "Translator", "Make a request for our translation services!")));
    assertTrue(
        res.contains(
            new Service("00:00", "00:00", "Laundry", "Make a request for laundry services!")));
  }
}
