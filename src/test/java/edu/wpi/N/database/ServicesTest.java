package edu.wpi.N.database;
// TODO: Add your imports
import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.N.entities.*;
import edu.wpi.N.entities.employees.IT;
import edu.wpi.N.entities.request.Request;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/*TODO: implement tests for your serviceType
Follow the golden rule of database tests: WHEN THE CONTROL FLOW EXITS YOUR FUNCTION, THE DATABASE SHOULD BE 110,000%
IDENTICAL TO WHAT IT WAS WHEN CONTROL FLOW ENTERED YOUR FUNCTION
For service requests, if you mutate them, that's probably ok.
DO NOT RELY ON IDS BEING PARTICULAR VALUES
ServiceDB.getEmployee(1) <--- NO
TURN OFF AUTOCOMMIT BEFORE ENTERING YOUR TESTS, CATCH DBEXCEPTION AND ROLLBACK
*/
public class ServicesTest {
  private static Connection con;

  @BeforeAll
  public static void setup()
      throws ClassNotFoundException, SQLException, DBException, FileNotFoundException {
    MapDB.initTestDB();
    con = MapDB.getCon();
  }

  /**
   * Tests that getITs returns a list of only IT employees
   *
   * @throws DBException
   */
  @Test
  public void getListOfITEmployeesTest() throws DBException {
    try {
      con.setAutoCommit(false);
      int idJerry = ServiceDB.addIT("Jerry");
      int idLucy = ServiceDB.addIT("Lucy");
      int idFrank = ServiceDB.addLaundry("Frank");
      LinkedList<String> langs = new LinkedList<>();
      int idDrew = ServiceDB.addTranslator("Drew", langs);

      con.commit();
      con.setAutoCommit(true);

      LinkedList<IT> employees = ServiceDB.getITs();
      Assertions.assertTrue(employees.contains(ServiceDB.getEmployee(idJerry)));
      Assertions.assertTrue(employees.contains(ServiceDB.getEmployee(idLucy)));
      Assertions.assertFalse(employees.contains(ServiceDB.getEmployee(idFrank)));
      Assertions.assertFalse(employees.contains(ServiceDB.getEmployee(idDrew)));

      ServiceDB.removeEmployee(idJerry);
      ServiceDB.removeEmployee(idLucy);
      ServiceDB.removeEmployee(idFrank);
      ServiceDB.removeEmployee(idDrew);

    } catch (DBException | SQLException e) {
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Oh no");
      }
    }
  }

  /**
   * Tests that function returns a IT Request if given ID matches IT Request
   *
   * @throws DBException
   */
  @Test
  public void getITRequestsTest() throws DBException {
    try {
      con.setAutoCommit(false);
      DbNode node = MapDB.addNode(5, 5, 1, "TestBuilding", "STAI", "My test", "Short");
      int idITReq =
          ServiceDB.addITReq(
              "Had device for 1 year", node.getNodeID(), "IPhone X", "Literally nothing");
      int idL = ServiceDB.addLaundReq("Clean my clothes", node.getNodeID());
      int idT = ServiceDB.addTransReq("Помогите!", node.getNodeID(), "Russian");

      con.commit();
      con.setAutoCommit(true);

      Request request = ServiceDB.getRequest(idITReq);
      Assertions.assertTrue(request.equals(ServiceDB.getRequest(idITReq)));

      ServiceDB.denyRequest(idITReq, "Nope");
      ServiceDB.denyRequest(idL, "Nope");
      ServiceDB.denyRequest(idT, "Nope");

    } catch (SQLException | DBException e) {
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Oh no");
      }
    }
  }

  /**
   * Tests that function getRequests returns all available requests including IT Requests
   *
   * @throws DBException
   */
  @Test
  public void getAllRequestsIncludingITRequestsTest() throws DBException {
    try {
      con.setAutoCommit(false);
      DbNode node = MapDB.addNode(5, 5, 1, "TestBuilding", "STAI", "My test", "Short");
      int idIT =
          ServiceDB.addITReq("Had device for 6 years", node.getNodeID(), "IPhone SE", "Trash");
      int idL = ServiceDB.addLaundReq("Clean my clothes, please", node.getNodeID());
      int idT = ServiceDB.addTransReq("Помогите!", node.getNodeID(), "Russian");
      int idIT2 =
          ServiceDB.addITReq("Had device for 2 years", node.getNodeID(), "LG G5", "Camera broke");
      int idLO = ServiceDB.addLaundReq("Filthy clothes", node.getNodeID());
      int idTO = ServiceDB.addTransReq("Помогите! Пожалуйста", node.getNodeID(), "Russian");

      ServiceDB.denyRequest(idIT, "Nope");
      ServiceDB.denyRequest(idL, "Nope");
      ServiceDB.denyRequest(idT, "Nope");

      con.commit();
      con.setAutoCommit(true);

      LinkedList<Request> allRequests = ServiceDB.getRequests();
      Assertions.assertTrue(allRequests.size() >= 6);
      Assertions.assertTrue(allRequests.contains(ServiceDB.getRequest(idIT)));
      Assertions.assertTrue(allRequests.contains(ServiceDB.getRequest(idL)));
      Assertions.assertTrue(allRequests.contains(ServiceDB.getRequest(idT)));
      Assertions.assertTrue(allRequests.contains(ServiceDB.getRequest(idIT2)));
      Assertions.assertTrue(allRequests.contains(ServiceDB.getRequest(idLO)));
      Assertions.assertTrue(allRequests.contains(ServiceDB.getRequest(idTO)));

      ServiceDB.denyRequest(idIT2, "Nope");
      ServiceDB.denyRequest(idLO, "Nope");
      ServiceDB.denyRequest(idTO, "Nope");

    } catch (SQLException | DBException e) {
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Oh no");
      }
    }
  }

  /**
   * Tests that get all open requests returns all Open requests including open IT requests
   *
   * @throws DBException
   */
  @Test
  public void getAllOpenRequestsIncludingITRequestsTest() throws DBException {
    try {
      con.setAutoCommit(false);
      DbNode node = MapDB.addNode(5, 5, 1, "TestBuilding", "STAI", "My test", "Short");
      int idIT =
          ServiceDB.addITReq(
              "Just got the phone", node.getNodeID(), "Samsung Galaxy S10", "Exploded");
      int idL = ServiceDB.addLaundReq("Clean my stuff", node.getNodeID());
      int idT = ServiceDB.addTransReq("Помогите!", node.getNodeID(), "Russian");

      con.commit();
      con.setAutoCommit(true);

      LinkedList<Request> openReqs = ServiceDB.getOpenRequests();
      Assertions.assertTrue(openReqs.size() >= 3);
      Assertions.assertTrue(openReqs.contains(ServiceDB.getRequest(idIT)));

      ServiceDB.denyRequest(idIT, "You're loss.");
      ServiceDB.denyRequest(idL, "Nope");
      ServiceDB.denyRequest(idT, "Nope");

    } catch (SQLException | DBException e) {
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Oh no");
      }
    }
  }

  /**
   * Tests adding IT employee to the database
   *
   * @throws DBException
   */
  @Test
  public void addITEmployeeTest() throws DBException {
    try {
      con.setAutoCommit(false);
      int id = ServiceDB.addIT("Evan");

      con.commit();
      con.setAutoCommit(true);

      Assertions.assertTrue("Evan".equals(ServiceDB.getEmployee(id).getName()));
      Assertions.assertTrue("IT".equals(ServiceDB.getEmployee(id).getServiceType()));

      ServiceDB.removeEmployee(id);

    } catch (SQLException | DBException e) {
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Oh no");
      }
    }
  }

  /**
   * Tests that request for IT gets added correctly to the database
   *
   * @throws DBException
   */
  @Test
  public void addITRequestTest() throws DBException {
    try {
      con.setAutoCommit(false);
      DbNode node = MapDB.addNode(5, 5, 1, "TestBuilding", "STAI", "My test", "Short");
      int id =
          ServiceDB.addITReq(
              "Had device for 1000000 year", node.getNodeID(), "Nokia", "Literally nothing");

      con.commit();
      con.setAutoCommit(true);

      String type = ServiceDB.getRequest(id).getServiceType();
      Assertions.assertTrue("IT".equals(type));
      Assertions.assertTrue(id != 0);

      ServiceDB.denyRequest(id, "Don't request ever again.");

    } catch (SQLException | DBException e) {
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Oh no");
      }
    }
  }

  @AfterEach
  public void clearDB() throws DBException {
    MapDB.clearNodes();
  }
}
