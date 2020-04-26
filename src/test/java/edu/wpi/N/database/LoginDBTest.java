package edu.wpi.N.database;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LoginDBTest {

  @BeforeAll
  public static void setup()
      throws DBException, SQLException, ClassNotFoundException, FileNotFoundException {
    MapDB.initTestDB();
  }

  @Test
  public void testLogin() throws DBException {
    LoginDB.createAdminLogin("Gaben", "MoolyFTW");
    LoginDB.verifyLogin("Gaben", "MoolyFTW");
    assertEquals("Gaben", LoginDB.currentLogin());
    assertEquals("ADMIN", LoginDB.currentAccess());
    LoginDB.logout();
    assertThrows(
        DBException.class,
        () -> {
          LoginDB.currentLogin();
        });
    assertThrows(
        DBException.class,
        () -> {
          LoginDB.verifyLogin("Gaben", "wrongPass");
        });
    assertThrows(
        DBException.class,
        () -> {
          LoginDB.verifyLogin("wrongUser", "MoolyFTW");
        });
    LoginDB.changePass("Gaben", "MoolyFTW", "wrongPass");
    LoginDB.verifyLogin("Gaben", "wrongPass");
    assertThrows(
        DBException.class,
        () -> {
          LoginDB.verifyLogin("Gaben", "MoolyFTW");
        });
    assertThrows(
        DBException.class,
        () -> {
          LoginDB.changePass("Gaben", "MoolyFTW", "oldpasswrong");
        });
    int wID = DoctorDB.addDoctor("Wong", "field", "Wong", "password", null);
    // LoginDB.createDoctorLogin("Wong", "password"); //I'm allowed to do i, you're not
    LoginDB.logout();
    LoginDB.removeLogin("Gaben");
    assertThrows(
        DBException.class,
        () -> {
          LoginDB.verifyLogin("Gaben", "wrongPass");
        });
    LoginDB.verifyLogin("Wong", "password");
    assertEquals("DOCTOR", LoginDB.currentAccess());
    assertThrows(
        DBException.class,
        () -> {
          LoginDB.removeLogin("Wong");
        });
    ServiceDB.removeEmployee(wID);
  }

  @AfterAll
  public static void clearDB() throws DBException {
    // LoginDB.removeLogin("Wong");
  }
}
