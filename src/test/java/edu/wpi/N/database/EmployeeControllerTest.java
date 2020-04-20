package edu.wpi.N.database;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.N.entities.Laundry;
import edu.wpi.N.entities.Service;
import edu.wpi.N.entities.Translator;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EmployeeControllerTest {
  static int laundReqID;

  @BeforeAll
  public static void setup() throws DBException, SQLException, ClassNotFoundException {
    DbController.initDB();

    LinkedList<String> langs = new LinkedList<>();
    langs.add("Gnomish");
    langs.add("Lojban");
    EmployeeController.addTranslator("Felix Bignoodle", langs);

    EmployeeController.addLaundry("Snaps McKraken");

    laundReqID = EmployeeController.addLaundReq("wash", "ZHALL00101");
  }

  @Test
  public void testGetEmployee() throws DBException {
    LinkedList<String> langs = new LinkedList<>();
    langs.add("Gnomish");
    langs.add("Lojban");
    assertEquals(new Translator(1, "Felix Bignoodle", langs), EmployeeController.getEmployee(1));
    assertEquals(new Laundry(2, "Snaps McKraken"), EmployeeController.getEmployee(2));
  }

  @Test
  public void testGetRequest() throws DBException {
    assertEquals("wash", EmployeeController.getRequest(laundReqID).getNotes());
    assertEquals("ZHALL00101", EmployeeController.getRequest(laundReqID).getNodeID());
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

  @AfterAll
  public static void cleanup() throws DBException {
    EmployeeController.removeEmployee(1);
    EmployeeController.removeEmployee(2);
  }
}
