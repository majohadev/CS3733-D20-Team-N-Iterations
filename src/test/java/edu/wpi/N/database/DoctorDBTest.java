package edu.wpi.N.database;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.employees.Doctor;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.LinkedList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DoctorDBTest {
  @BeforeAll
  public static void setup()
      throws DBException, SQLException, ClassNotFoundException, FileNotFoundException {
    MapDB.initTestDB();
    MapDB.addNode("NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N');
    MapDB.addNode("NHALL00104", 1250, 850, 4, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N');
    MapDB.addNode("NDEPT00204", 1450, 950, 4, "Faulkner", "DEPT", "Neurology", "Dept 2", 'N');
  }

  @Test
  public void testGetDoctor() throws DBException {
    LinkedList<DbNode> offices = new LinkedList<DbNode>();
    offices.add(MapDB.getNode("NDEPT00104"));
    offices.add(MapDB.getNode("NHALL00104"));
    int wongID = DoctorDB.addDoctor("Wong", "Softeng", "DocWong", "password", offices);
    Doctor wong = DoctorDB.getDoctor(wongID);
    assertTrue(wong.getField().equals("Softeng"));
    assertTrue(wong.getLoc().get(0).equals(MapDB.getNode("NDEPT00104")));
    assertTrue(wong.getLoc().get(1).equals(MapDB.getNode("NHALL00104")));
    assertFalse(wong.getLoc().contains(MapDB.getNode("NDEPT00204")));
    DoctorDB.addOffice(wongID, MapDB.getNode("NDEPT00204"));
    wong = DoctorDB.getDoctor(wongID);
    assertTrue(wong.getLoc().get(2).equals(MapDB.getNode("NDEPT00204")));
    DoctorDB.removeOffice(wongID, MapDB.getNode("NHALL00104"));
    wong = DoctorDB.getDoctor(wongID);
    assertTrue(wong.getLoc().get(1).equals(MapDB.getNode("NDEPT00204")));
    LoginDB.verifyLogin("DocWong", "password");
    assertEquals("DOCTOR", LoginDB.currentAccess());
    ServiceDB.removeEmployee(wongID);
    assertThrows(
        DBException.class,
        () -> {
          LoginDB.verifyLogin("DocWong", "password");
        });
  }

  @Test
  public void testSearchDoctor() throws DBException {
    LinkedList<DbNode> offices = new LinkedList<DbNode>();
    offices.add(MapDB.getNode("NDEPT00104"));
    offices.add(MapDB.getNode("NHALL00104"));
    int wongID = DoctorDB.addDoctor("Wong", "Softeng", "DocWong", "password", offices);
    offices.pop();
    offices.add(MapDB.getNode("NDEPT00204"));
    int kongID = DoctorDB.addDoctor("Kong", "History", "Kong", "Kpass", offices);
    LinkedList<Doctor> drs = DoctorDB.searchDoctors("oNg");
    Doctor wong = DoctorDB.getDoctor(wongID);
    Doctor kong = DoctorDB.getDoctor(kongID);
    assertTrue(drs.contains(wong));
    assertTrue(drs.contains(kong));
    drs = DoctorDB.searchDoctors("ko");
    assertFalse(drs.contains(wong));
    assertTrue(drs.contains(kong));
    drs = DoctorDB.getDoctors();
    assertTrue(drs.contains(wong));
    assertTrue(drs.contains(kong));
    ServiceDB.removeEmployee(wongID);
    ServiceDB.removeEmployee(kongID);
  }

  @AfterAll
  public static void cleanup() throws DBException {
    MapDB.clearNodes();
  }
}
