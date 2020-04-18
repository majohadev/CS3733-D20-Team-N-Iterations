package edu.wpi.N.database;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import edu.wpi.N.entities.Doctor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class ServiceControllerTest {
  @BeforeAll
  public static void setup() throws DBException, SQLException, ClassNotFoundException {
    DbController.initDB();
  }

  @Test
  public void testGetDoctor() throws DBException {
      DbController.addNode("NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N');
      Doctor wong = new Doctor(1, "Wilson Wong", DbController.getNode("NDEPT00104"), "Software Engineering");

      assertTrue(ServiceController.addDoctor(wong));
      assertEquals(wong, ServiceController.getDoctor(1));
      assertTrue(ServiceController.deleteDoctor(1));

      DbController.deleteNode("NDEPT00104");
  }

  @Test
  public void testAddDoctor() throws DBException {
      DbController.addNode("NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N');
      Doctor wong = new Doctor(1, "Wilson Wong", DbController.getNode("NDEPT00104"), "Software Engineering");

      assertTrue(ServiceController.addDoctor(wong));
      assertEquals(wong, ServiceController.getDoctor(1));
      assertTrue(ServiceController.deleteDoctor(1));

      assertTrue(ServiceController.addDoctor(1, "Wilson Wong", DbController.getNode("NDEPT00104"), "Software Engineering"));
      assertEquals(wong, ServiceController.getDoctor(1));
      assertTrue(ServiceController.deleteDoctor(1));


      DbController.deleteNode("NDEPT00104");
  }

  @Test
  public void testRemoveDoctor() throws DBException {
      DbController.addNode("NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N');

      ServiceController.addDoctor(1, "Wilson Wong", DbController.getNode("NDEPT00104"), "Neurology");
      ServiceController.deleteDoctor(1);
      assertNull(ServiceController.getDoctor(1));

      DbController.deleteNode("NDEPT00104");
  }

  @Test
  public void testModifyDoctorFalse() throws DBException {
      DbController.addNode("NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N');
      DbController.addNode("NHALL00104", 1250, 850, 4, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N');

      ServiceController.addDoctor(1, "Wilson Wong", DbController.getNode("NDEPT00104"), "Software Engineering");
      assertFalse(ServiceController.modifyDoctor(1, "Wilson Wang", DbController.getNode("NHALL00104"), "Algorithms"));

      DbController.deleteNode("NDEPT00104");
      DbController.deleteNode("NHALL00104");
  }

  @Test
  public void testModifyDoctorTrue() throws DBException {
      DbController.addNode("NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N');
      DbController.addNode("NDEPT00204", 1450, 950, 4, "Faulkner", "DEPT", "Neurology", "Dept 2", 'N');

      ServiceController.addDoctor(1, "Wilson Wong", DbController.getNode("NDEPT00104"), "Software Engineering");
      assertFalse(ServiceController.modifyDoctor(1, "Wilson Wang", DbController.getNode("NDEPT00204"), "Algorithms"));

      DbController.deleteNode("NDEPT00104");
      DbController.deleteNode("NDEPT00204");
  }
}
