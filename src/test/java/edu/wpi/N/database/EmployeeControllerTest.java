package edu.wpi.N.database;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.N.entities.Translator;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EmployeeControllerTest {
  @BeforeAll
  public static void setup() throws DBException, SQLException, ClassNotFoundException {
    DbController.initDB();
  }

  @Test
  public void testaddEmployee() throws DBException {
    assertTrue(EmployeeController.addEmployee(1, "Wilson Wong"));
  }

  @Test
  public void testaddTranslator() throws DBException {
    assertTrue(EmployeeController.addTranslator(1, "Korean"));
  }

  @Test
  public void testgetEmployee() throws DBException {
    assertEquals(1, EmployeeController.getEmployee(1).getId());
  }

  @Test
  public void testmodifyEmployee() {}

  @Test
  public void testdeleteEmployee() {}

  @Test
  public void testmodifyLanguage() {}

  @Test
  public void testgetTranslator() throws DBException {
    assertTrue(EmployeeController.addEmployee(1, "Wilson Wong"));
    assertTrue(EmployeeController.addTranslator(1, "Korean"));
    assertEquals("Korean", ((Translator) EmployeeController.getTranslator(1)).getLanguage());
  }
}
