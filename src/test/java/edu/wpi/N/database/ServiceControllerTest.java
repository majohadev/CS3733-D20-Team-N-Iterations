package edu.wpi.N.database;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ServiceControllerTest {
  @BeforeAll
  public static void setup() throws DBException, SQLException, ClassNotFoundException {
    DbController.initDB();
    Timestamp time1 = ServiceController.getSqltime(new GregorianCalendar(3, 0, 5, 4, 4, 4));
    Timestamp time2 = ServiceController.getSqltime(new GregorianCalendar(3, 0, 5, 5, 15, 0));
    ServiceController.addService(1, "Wilson Wong", 3, "Chinese", 'M', time1, time2, "Translator");
  }

  @Test
  public static void testaddService() {}

  @Test
  public static void testgetService() {}

  @Test
  public static void testmodifyService() {}

  @Test
  public static void testdeleteService() {}

  @Test
  public static void testmodifyLanguage() {}

  @Test
  public static void testsearchbyLang() {}
}
