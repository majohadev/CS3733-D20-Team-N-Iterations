package edu.wpi.N.database;

import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Doctor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoctorController {
  private static Connection con = DbController.getCon();

    public static void initDoctor() throws DBException {
        try {
            String query =
                    "CREATE TABLE doctors ("
                            + "doctorID INT NOT NULL PRIMARY KEY, "
                            + "name VARCHAR(255) NOT NULL, "
                            + "location CHAR(10) NOT NULL, "
                            + "field VARCHAR(255) NOT NULL, "
                            + "FOREIGN KEY (location) REFERENCES nodes(nodeID) ON DELETE CASCADE)";

            PreparedStatement state = con.prepareStatement(query);
            state.execute();
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) {
                e.printStackTrace();
                throw new DBException("Unknown error: initDoctor", e);
            }
        }
    }
  // Nick
  public static Doctor getDoctor(int id) throws DBException {
    try {
      String query = "SELECT * FROM doctors WHERE doctorID = ?";
      PreparedStatement state = con.prepareStatement(query);
      state.setInt(1, id);
      ResultSet rs = state.executeQuery();

      if (rs.next()) {
        return new Doctor(
            id,
            rs.getString("name"),
            DbController.getNode(rs.getString("location")),
            rs.getString("field"));
      } else {
        throw new DBException("getDoctor: Doctor not found", null);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getDoctor", e);
    }
  }
  // Nick
  public static boolean addDoctor(Doctor d) throws DBException {
    return addDoctor(d.getId(), d.getName(), d.getLoc(), d.getField());
  }
  // Nick
  public static boolean addDoctor(int id, String name, DbNode location, String field)
      throws DBException {
    try {
      String query = "INSERT INTO doctors VALUES (?, ?, ?, ?)";
      PreparedStatement state = con.prepareStatement(query);
      state.setInt(1, id);
      state.setString(2, name);
      state.setString(3, location.getNodeID());
      state.setString(4, field);
      return state.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: addDoctor", e);
    }
  }

  // Chris
  public static boolean deleteDoctor(int docID) throws DBException {
    try {
      String query = "DELETE FROM doctors WHERE doctorID = ?";
      PreparedStatement state = con.prepareStatement(query);
      state.setInt(1, docID);
      return state.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: deleteDoctor", e);
    }
  }
  // Chris
  public static boolean modifyDoctor(int docID, String n, DbNode loc, String f) throws DBException {
    String key = loc.getNodeID();
    try {
      if (key.contains("DEPT")) {
        String query = "UPDATE doctors SET name = ?, location = ?, field = ? WHERE doctorID = ?";
        PreparedStatement state = con.prepareStatement(query);
        state.setString(1, n);
        state.setString(2, key);
        state.setString(3, f);
        state.setInt(4, docID);
        return state.executeUpdate() > 0;
      }
      System.out.println("nodeID for the location is incorrect");
      return false;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("modify function causing error", e);
    }
  }
}
