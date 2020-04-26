package edu.wpi.N.database;
// TODO: Add your imports

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.N.entities.*;
import edu.wpi.N.entities.employees.Employee;
import edu.wpi.N.entities.employees.WheelchairEmployee;
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
   * Tests that employee is added and you can get that employee from the database
   *
   * @throws DBException
   * @throws SQLException
   */
  @Test
  public void testGetWheelchairEmployee() throws DBException, SQLException {
    try {
      con.setAutoCommit(false);
      int jamesID = ServiceDB.addWheelchairEmployee("James Joe");
      con.commit();
      con.setAutoCommit(true);
      WheelchairEmployee james = (WheelchairEmployee) ServiceDB.getEmployee(jamesID);
      assertEquals(james, (WheelchairEmployee) ServiceDB.getEmployee(jamesID));
      assertEquals("Wheelchair", ServiceDB.getEmployee(jamesID).getServiceType());

      ServiceDB.removeEmployee(jamesID);
    } catch (SQLException | DBException e) {
      con.rollback();
      try {
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Oh no");
      }
    }
  }

  /**
   * Tests that when wheelchair employees are added, they appear in getWheelchairEmployees list and
   * the getEmployees list The wheelchair employees list does not contain non-wheelchair employees
   * When a wheelchair employee is removed, they are no longer on this list
   *
   * @throws DBException
   * @throws SQLException
   */
  @Test
  public void testGetAllWheelchairEmployees() throws DBException, SQLException {
    try {
      con.setAutoCommit(false);
      int bobID = ServiceDB.addWheelchairEmployee("Bob Joe");
      int joeID = ServiceDB.addWheelchairEmployee("Joe F");
      int jerryID = ServiceDB.addLaundry("Jerry L");
      con.commit();
      con.setAutoCommit(true);
      LinkedList<Employee> list = ServiceDB.getEmployees();
      assertEquals(3, list.size());
      assertTrue(list.contains(ServiceDB.getEmployee(bobID)));
      assertTrue(list.contains(ServiceDB.getEmployee(joeID)));
      assertTrue(list.contains(ServiceDB.getEmployee(jerryID)));
      LinkedList<WheelchairEmployee> wheelchairEmployees = ServiceDB.getWheelchairEmployees();
      assertTrue(wheelchairEmployees.contains(ServiceDB.getEmployee(bobID)));
      assertTrue(wheelchairEmployees.contains(ServiceDB.getEmployee(joeID)));
      assertFalse(wheelchairEmployees.contains(ServiceDB.getEmployee(jerryID)));
      ServiceDB.removeEmployee(bobID);
      assertFalse(wheelchairEmployees.contains(ServiceDB.getEmployee(bobID)));
      ServiceDB.removeEmployee(joeID);
      ServiceDB.removeEmployee(jerryID);
    } catch (SQLException | DBException e) {
      con.rollback();
      try {
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Oh no");
      }
    }
  }

  /**
   * Tests that a wheelchair request is added and appears in getRequests and getOpenRequests Once
   * the request is denied, it still appears in getRequests, but no longer on getOpenRequests
   *
   * @throws DBException
   * @throws SQLException
   */
  @Test
  public void testAddWheelchairRequest() throws DBException, SQLException {
    try {
      con.setAutoCommit(false);
      MapDB.addNode("ZHALL00101", 10, 10, 1, "Faulkner", "HALL", "HALLZ1", "HALLZ1", 'Z');
      MapDB.addNode("ZHALL00102", 10, 10, 2, "Faulkner", "HALL", "HALLZ2", "HALLZ2", 'Z');
      int wheelchairReqID1 =
          ServiceDB.addWheelchairRequest("my leg is broken", "ZHALL00101", "yes");
      int wheelchairReqID2 = ServiceDB.addWheelchairRequest("plz help", "ZHALL00102", "no");
      con.commit();
      con.setAutoCommit(true);
      LinkedList<Request> requests = new LinkedList<Request>();
      requests.add(ServiceDB.getRequest(wheelchairReqID1));
      requests.add(ServiceDB.getRequest(wheelchairReqID2));

      Assertions.assertEquals(ServiceDB.getOpenRequests(), requests);
      Assertions.assertEquals(ServiceDB.getRequests(), requests);
      ServiceDB.denyRequest(wheelchairReqID1, "done");
      Assertions.assertFalse(
          ServiceDB.getOpenRequests().contains(ServiceDB.getRequest(wheelchairReqID1)));
      Assertions.assertTrue(
          ServiceDB.getRequests().contains(ServiceDB.getRequest(wheelchairReqID1)));
      ServiceDB.denyRequest(wheelchairReqID2, "done");
      Assertions.assertFalse(
          ServiceDB.getOpenRequests().contains(ServiceDB.getRequest(wheelchairReqID1)));
      Assertions.assertFalse(
          ServiceDB.getOpenRequests().contains(ServiceDB.getRequest(wheelchairReqID2)));

      ServiceDB.removeEmployee(wheelchairReqID1);
      ServiceDB.removeEmployee(wheelchairReqID2);

      MapDB.clearNodes();
    } catch (SQLException | DBException e) {
      con.rollback();
      try {
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
