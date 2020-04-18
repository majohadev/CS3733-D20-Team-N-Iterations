package edu.wpi.N.database;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;

import edu.wpi.N.entities.Doctor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ServiceControllerTest {
  @BeforeAll
  public static void setup() throws DBException, SQLException, ClassNotFoundException {
    DbController.initDB();
  }

  @Test
  public void testGetDoctor() throws DBException {
    DbController.addNode("NHALL00104", 1250, 850, 4, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N');
    Doctor jim = new Doctor("jim", DbController.getNode("NHALL00104"), "good");

    assertTrue(ServiceController.addDoctor(jim));

    assertEquals(jim, ServiceController.getDoctor(1));

    assertTrue(ServiceController.removeDoctor(1));

    DbController.deleteNode("NHALL00104");
  }

  @Test
  public void testAddDoctor() {}

  @Test
  public void testRemoveDoctor() {}

  @Test
  public void testModifyDoctor() {}
}
