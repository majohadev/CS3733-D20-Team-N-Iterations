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
    DbController.addNode("NHALL00104", 1250, 850, 4, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N');
    Doctor jim = new Doctor(1, "jim", DbController.getNode("NHALL00104"), "good");

    assertTrue(ServiceController.addDoctor(jim));

    assertEquals(jim, ServiceController.getDoctor(1));
    assertTrue(ServiceController.deleteDoctor(1));

    DbController.deleteNode("NHALL00104");
  }

  @Test
  public void testAddDoctor() {}

  @Test
  public void testRemoveDoctor() throws DBException {
      ServiceController.addDoctor(1, "Wilson Wong", DbController.getNode("NDEPT00104"), "Neurology");
      ServiceController.deleteDoctor(1);
      assertNull(ServiceController.getDoctor(1));
  }

  @Test
  public void testModifyDoctorFalse() throws DBException {
      ServiceController.addDoctor(1, "Wilson Wong", DbController.getNode("NDEPT00104"), "Software Engineering");
      assertFalse(ServiceController.modifyDoc(1, "Wilson Wang", DbController.getNode("NHALL00104"), "Algorithms"));
  }

  @Test
  public void testModifyDoctorTrue() throws DBException {
      ServiceController.addDoctor(1, "Wilson Wong", DbController.getNode("NDEPT00104"), "Software Engineering");
      assertFalse(ServiceController.modifyDoc(1, "Wilson Wang", DbController.getNode("NDEPT00204"), "Algorithms"));
  }
}
