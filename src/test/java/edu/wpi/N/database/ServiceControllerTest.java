// package edu.wpi.N.database;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import edu.wpi.N.entities.Doctor;
// import java.sql.SQLException;
// import org.junit.jupiter.api.AfterAll;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;
//
// public class ServiceControllerTest {
//  @BeforeAll
//  public static void setup() throws DBException, SQLException, ClassNotFoundException {
//    DbController.initDB();
//    DbController.addNode(
//        "NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N');
//    DbController.addNode("NHALL00104", 1250, 850, 4, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N');
//    DbController.addNode(
//        "NDEPT00204", 1450, 950, 4, "Faulkner", "DEPT", "Neurology", "Dept 2", 'N');
//  }
//
//  @Test
//  public void testGetDoctor() throws DBException {
//    Doctor wong =
//        new Doctor(1, "Wilson Wong", DbController.getNode("NDEPT00104"), "Software Engineering");
//
//    assertTrue(ServiceController.addDoctor(wong));
//    assertEquals(wong, ServiceController.getDoctor(1));
//    assertTrue(ServiceController.deleteDoctor(1));
//  }
//
//  @Test
//  public void testAddDoctor() throws DBException {
//    Doctor wong =
//        new Doctor(2, "Wilson Wong", DbController.getNode("NDEPT00104"), "Software Engineering");
//
//    assertTrue(ServiceController.addDoctor(wong));
//    assertEquals(wong, ServiceController.getDoctor(2));
//    assertTrue(ServiceController.deleteDoctor(2));
//
//    assertTrue(
//        ServiceController.addDoctor(
//            3, "Wilson Wong", DbController.getNode("NDEPT00104"), "Software Engineering"));
//    assertEquals(wong, ServiceController.getDoctor(3));
//    assertTrue(ServiceController.deleteDoctor(3));
//  }
//
//  @Test
//  public void testRemoveDoctor() throws DBException {
//    ServiceController.addDoctor(4, "Wilson Wong", DbController.getNode("NDEPT00104"),
// "Neurology");
//    ServiceController.deleteDoctor(4);
//    assertThrows(DBException.class, () -> ServiceController.getDoctor(4));
//  }
//
//  @Test
//  public void testModifyDoctorFalse() throws DBException {
//    ServiceController.addDoctor(
//        5, "Wilson Wong", DbController.getNode("NDEPT00104"), "Software Engineering");
//    assertFalse(
//        ServiceController.modifyDoctor(
//            5, "Wilson Wang", DbController.getNode("NHALL00104"), "Algorithms"));
//  }
//
//  @Test
//  public void testModifyDoctorTrue() throws DBException {
//    ServiceController.addDoctor(
//        6, "Wilson Wong", DbController.getNode("NDEPT00104"), "Software Engineering");
//    assertTrue(
//        ServiceController.modifyDoctor(
//            6, "Wilson Wang", DbController.getNode("NDEPT00204"), "Algorithms"));
//  }
//
//  @AfterAll
//  public static void cleanup() throws DBException {
//    DbController.clearNodes();
//  }
// }
