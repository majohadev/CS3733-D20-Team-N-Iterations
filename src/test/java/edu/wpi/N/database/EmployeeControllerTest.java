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
  static Translator felix;
  static Translator fats;
  static Laundry snaps;

  @BeforeAll
  public static void setup() throws DBException, SQLException, ClassNotFoundException {
    DbController.initDB();

    LinkedList<String> langs = new LinkedList<>();
    langs.add("Gnomish");
    langs.add("Lojban");
    EmployeeController.addTranslator("Felix Bignoodle", langs);
    felix = new Translator(1, "Felix Bignoodle", langs);

    langs.clear();
    langs.add("Gnomish");
    EmployeeController.addTranslator("Fats Rumbuckle", langs);
    fats = new Translator(2, "Fats Rumbuckle", langs);

    EmployeeController.addLaundry("Snaps McKraken");
    snaps = new Laundry(3, "Snaps McKraken");

    laundReqID = EmployeeController.addLaundReq("wash", "ZHALL00101");
  }

  @Test
  public void testGetEmployee() throws DBException {
    LinkedList<String> langs = new LinkedList<>();
    langs.add("Gnomish");
    langs.add("Lojban");
    assertEquals(new Translator(1, "Felix Bignoodle", langs), EmployeeController.getEmployee(1));
    assertEquals(new Laundry(3, "Snaps McKraken"), EmployeeController.getEmployee(3));
  }

  @Test
  public void testGetRequest() throws DBException {
    assertEquals("wash", EmployeeController.getRequest(laundReqID).getNotes());
    assertEquals("ZHALL00101", EmployeeController.getRequest(laundReqID).getNodeID());
  }

  @Test
  public void testGetTransLang() throws DBException {
    LinkedList<Translator> translators = EmployeeController.getTransLang("Gnomish");
    assertEquals(2, translators.size());
    assertTrue(translators.contains(felix));
    assertTrue(translators.contains(fats));

    translators = EmployeeController.getTransLang("Lojban");
    assertEquals(1, translators.size());
    assertTrue(translators.contains(felix));
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
