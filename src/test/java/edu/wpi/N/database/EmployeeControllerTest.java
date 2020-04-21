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

    LinkedList<String> langs2 = new LinkedList<>();
    langs2.add("Gnomish");
    EmployeeController.addTranslator("Fats Rumbuckle", langs2);
    fats = new Translator(2, "Fats Rumbuckle", langs2);

    EmployeeController.addLaundry("Snaps McKraken");
    snaps = new Laundry(3, "Snaps McKraken");
    DbController.addNode("ZHALL00101", 10, 10, 1, "Faulkner", "HALL", "HALLZ1", "HALLZ1", 'Z');
    DbController.addNode("ZHALL00102", 10, 10, 2, "Faulkner", "HALL", "HALLZ2", "HALLZ2", 'Z');
    laundReqID1 = EmployeeController.addLaundReq("wash", "ZHALL00101");
    transReqID1 = EmployeeController.addTransReq("speak", "ZHALL00102", "Gnomish");
  }

  @Test
  public void testgetlistEmployees() throws DBException {
    LinkedList<Employee> list = EmployeeController.getEmployees();
    assertEquals(3, list.size());
    int id = EmployeeController.addLaundry("Joshua Aloeface");
    list = EmployeeController.getEmployees();
    assertEquals(4, list.size());
    assertTrue(list.contains(new Laundry(id, "Joshua Aloeface")));
    EmployeeController.removeEmployee(id);
  }

  @Test
  public void testaddLanguage() throws DBException {
    EmployeeController.addLanguage(fats.getID(), "Chinese");
    // assertEquals("Chinese", fats.getLanguages().get(1));
    fats = (Translator) EmployeeController.getEmployee(fats.getID());
    assertTrue(fats.getLanguages().contains("Chinese"));
    EmployeeController.removeLanguage(fats.getID(), "Chinese");
    fats = (Translator) EmployeeController.getEmployee(fats.getID());
  }

  @Test
  public void testremoveLanguage() throws DBException {
    EmployeeController.removeLanguage(felix.getID(), "Gnomish");
    // assertNull(felix.getLanguages().get(0));
    felix = (Translator) EmployeeController.getEmployee(felix.getID());
    assertFalse(felix.getLanguages().contains("Gnomish"));
    EmployeeController.addLanguage(felix.getID(), "Gnomish");
    felix = (Translator) EmployeeController.getEmployee(felix.getID());
  }

  @Test
  public void testgetOpenRequest() throws DBException {
    // DbController.addNode("NSERV00104", 11, 11, 4, "Faulkner", "SERV", "Longname", "ShortName",
    // 'N');
    DbController.addNode(
        "NDEPT00302", 22, 22, 2, "Faulkner", "DEPT", "Longname1", "Shortname1", 'N');
    // EmployeeController.addLaundReq("Make it extra clean", "NSERV00104");
    EmployeeController.addTransReq(
        "Need a translator for medicine description", "NDEPT00302", "Korean");
    LinkedList<Request> list = EmployeeController.getOpenRequests();
    assertEquals(3, list.size());
    assertFalse((list.contains(EmployeeController.getRequest(transReqID1))));
    assertTrue((list.contains(EmployeeController.getRequest(laundReqID1))));
  }

  @Test
  public void testaddTransReq() throws DBException {
    DbController.addNode(
        "NDEPT00104", 100, 100, 4, "Faulkner", "DEPT", "Longname", "shortname", 'N');
    int id =
        EmployeeController.addTransReq(
            "Need a Korean translator for prescription",
            DbController.getNode("NDEPT00104").getNodeID(),
            "Korean");
    assertEquals("NDEPT00104", EmployeeController.getRequest(id).getNodeID());
  }

  @Test
  public void testCompleteRequest() throws DBException {
    EmployeeController.completeRequest(transReqID1);
    Request req = EmployeeController.getRequest(transReqID1);
    assertNotNull(req.getTimeCompleted());
  }

  @Test
  public void testDenyRequest() throws DBException {
    EmployeeController.denyRequest(transReqID1);
    Request req = EmployeeController.getRequest(transReqID1);
    assertNotNull(req.getTimeCompleted());
    assertEquals("DENY", req.getStatus());
  }

  @Test
  public void testGetEmployee() throws DBException {
    LinkedList<String> langs = new LinkedList<>();
    Translator felix = (Translator) EmployeeController.getEmployee(1);
    Laundry snaps = (Laundry) EmployeeController.getEmployee(3);
    assertEquals(1, felix.getID());
    assertTrue(felix.getName().equals("Felix Bignoodle"));
    assertTrue(felix.getLanguages().contains("Lojban"));
    assertTrue(felix.getLanguages().contains("Gnomish"));
    assertEquals(new Laundry(3, "Snaps McKraken"), snaps);
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
    // assertTrue(translators.contains(felix));
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

  @Test
  public void testassigntoRequest() throws DBException {
    EmployeeController.assignToRequest(felix.getID(), transReqID1);
    assertEquals(1, EmployeeController.getRequest(transReqID1).getEmp_assigned());
    assertThrows(
        DBException.class,
        () -> {
          EmployeeController.assignToRequest(fats.getID(), laundReqID1);
        });
    assertEquals(0, EmployeeController.getRequest(laundReqID1).getEmp_assigned());
  }

  @Test
  public void testAllLangs() throws DBException {
    LinkedList<String> langs = EmployeeController.getLanguages();
    assertTrue(langs.contains("Gnomish"));
    assertTrue(langs.contains("Lojban"));
  }

  @AfterAll
  public static void cleanup() throws DBException {
    DbController.clearNodes();
    for (int i : getAllEmployeeIds()) {
      EmployeeController.removeEmployee(i);
    }
  }

  private static LinkedList<Integer> getAllEmployeeIds() throws DBException {
    LinkedList<Integer> ids = new LinkedList<Integer>();

    for (Employee employee : EmployeeController.getEmployees()) {
      ids.add(employee.getID());
    }

    return ids;
  }
}
