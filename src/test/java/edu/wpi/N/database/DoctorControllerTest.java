package edu.wpi.N.database;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Doctor;
import java.sql.SQLException;
import java.util.LinkedList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DoctorControllerTest {
  @BeforeAll
  public static void setup() throws DBException, SQLException, ClassNotFoundException {
    DbController.initDB();
    DbController.addNode(
        "NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N');
    DbController.addNode("NHALL00104", 1250, 850, 4, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N');
    DbController.addNode(
        "NDEPT00204", 1450, 950, 4, "Faulkner", "DEPT", "Neurology", "Dept 2", 'N');
  }

  @Test
  public void testGetDoctor() throws DBException {
    LinkedList<DbNode> offices = new LinkedList<DbNode>();
    offices.add(DbController.getNode("NDEPT00104"));
    offices.add(DbController.getNode("NHALL00104"));
    DoctorController.addDoctor("Wong", "Softeng", offices);
    Doctor wong = DoctorController.getDoctor("Wong");
    assertTrue(wong.getField().equals("Softeng"));
    assertTrue(wong.getLoc().get(0).equals(DbController.getNode("NDEPT00104")));
    assertTrue(wong.getLoc().get(1).equals(DbController.getNode("NHALL00104")));
    assertFalse(wong.getLoc().contains(DbController.getNode("NDEPT00204")));
    DoctorController.addOffice("Wong", DbController.getNode("NDEPT00204"));
    wong = DoctorController.getDoctor("Wong");
    assertTrue(wong.getLoc().get(2).equals(DbController.getNode("NDEPT00204")));
    DoctorController.removeOffice("Wong", DbController.getNode("NHALL00104"));
    wong = DoctorController.getDoctor("Wong");
    assertTrue(wong.getLoc().get(1).equals(DbController.getNode("NDEPT00204")));
    DoctorController.deleteDoctor("Wong");
  }

  @Test
  public void testSearchDoctor() throws DBException {
    LinkedList<DbNode> offices = new LinkedList<DbNode>();
    offices.add(DbController.getNode("NDEPT00104"));
    offices.add(DbController.getNode("NHALL00104"));
    DoctorController.addDoctor("Wong", "Softeng", offices);
    offices.pop();
    offices.add(DbController.getNode("NDEPT00204"));
    DoctorController.addDoctor("Kong", "History", offices);
    LinkedList<Doctor> drs = DoctorController.searchDoctors("oNg");
    Doctor wong = DoctorController.getDoctor("Wong");
    Doctor kong = DoctorController.getDoctor("Kong");
    assertTrue(drs.contains(wong));
    assertTrue(drs.contains(kong));
    drs = DoctorController.searchDoctors("ko");
    assertFalse(drs.contains(wong));
    assertTrue(drs.contains(kong));
    drs = DoctorController.getDoctors();
    assertTrue(drs.contains(wong));
    assertTrue(drs.contains(kong));
    DoctorController.deleteDoctor("Wong");
    DoctorController.deleteDoctor("Kong");
  }

  @AfterAll
  public static void cleanup() throws DBException {
    DbController.clearNodes();
  }
}
