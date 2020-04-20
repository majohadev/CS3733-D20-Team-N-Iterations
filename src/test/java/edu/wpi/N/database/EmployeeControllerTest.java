package edu.wpi.N.database;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.N.entities.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EmployeeControllerTest {
  static int laundReqID1, transReqID1;
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

    laundReqID1 = EmployeeController.addLaundReq("wash", "ZHALL00101");
    transReqID1 = EmployeeController.addTransReq("speak", "ZHALL00102", "Gnomish");
  }

  @Test
  public void testgetlistEmployees() throws DBException {
    LinkedList<Employee> list = EmployeeController.getEmployees();
    assertEquals(1, list.size());
    EmployeeController.addLaundry("Joshua Aloeface");
    assertEquals("Joshua Aloeface", list.get(1).getName());
  }

  @Test
  public void testgetOpenRequest() throws DBException {
    EmployeeController.addLaundReq("Make it extra clean", "NSERV00104");
    EmployeeController.addTransReq(
        "Need a translator for medicine description", "NDEPT00302", "Korean");
    LinkedList<Request> list = EmployeeController.getOpenRequests();
    assertEquals(2, list.size());
    assertEquals("Korean", ((TranslatorRequest) list.get(1)).getLanguage());
  }

  @Test
  public void testgetTranslang() throws DBException {
    LinkedList<String> list = new LinkedList<String>();
    list.add("Korean");
    list.add("English");
    EmployeeController.addTranslator("Chris Lee", list);
    EmployeeController.addTranslator("Wilson Wong", list);
    LinkedList<Translator> result = EmployeeController.getTransLang("Korean");
    assertEquals(2, result.size());
    assertEquals("Wilson Wong", result.get(1).getName());
  }

  @Test
  public void testaddTransReq() {}

  @Test
  public void testCompleteRequest() throws DBException {
    EmployeeController.completeRequest(transReqID1);
    Request req = EmployeeController.getRequest(transReqID1);
    assertNotNull(req.getTimeCompleted());
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
    assertEquals("wash", EmployeeController.getRequest(laundReqID1).getNotes());
    assertEquals("ZHALL00101", EmployeeController.getRequest(laundReqID1).getNodeID());
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
