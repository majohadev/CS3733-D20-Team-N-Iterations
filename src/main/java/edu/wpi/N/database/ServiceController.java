package edu.wpi.N.database;


import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Doctor;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServiceController {
  public static void initService() throws DBException {
    try {
      String query =
          "Create Table doctors ("
              + "doctorID INT NOT NULL PRIMARY KEY,"
              + "name VARCHAR(255) NOT NULL,"
              + "location CHAR(10) NOT NULL,"
              + "field VARCHAR(255) NOT NULL)";
      PreparedStatement state = DbController.getCon().prepareStatement(query);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("initService causing error", e);
    }
  }

  public static Doctor getDoctor(int id) {
    return new Doctor("a", new DbNode(), "a");
  }

  public static boolean addDoctor(Doctor d){
    return false;
  }
}
