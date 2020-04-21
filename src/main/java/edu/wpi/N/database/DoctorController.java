package edu.wpi.N.database;

import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Doctor;
import java.sql.*;
import java.util.Iterator;
import java.util.LinkedList;

public class DoctorController {
  private static Connection con = DbController.getCon();

  static void initDoctor() throws DBException {
    try {
      String query =
          "CREATE TABLE doctors ("
              + "doctorID INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
              + "name VARCHAR(255) NOT NULL, "
              + "field VARCHAR(255) NOT NULL)";

      PreparedStatement state = con.prepareStatement(query);
      state.execute();
      query =
          "CREATE TABLE location ("
              + "doctor INT NOT NULL REFERENCES doctors(doctorID) ON DELETE CASCADE, "
              + "nodeID char(10) NOT NULL REFERENCES nodes(nodeID) ON DELETE CASCADE,"
              + "priority INT NOT NULL GENERATED ALWAYS AS IDENTITY,"
              + "PRIMARY KEY (doctor, nodeID))";
      state = con.prepareStatement(query);
      state.execute();
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) {
        e.printStackTrace();
        throw new DBException("Unknown error: initDoctor", e);
      }
    }
  }

  /**
   * Gets the doctor with the specified name (must be exact)
   *
   * @param doctorID The exact name of the doctor
   * @return The doctor asked for
   * @throws DBException On error or doctor not found
   */
  public static Doctor getDoctor(int doctorID) throws DBException {
    try {
      String query = "SELECT nodeID FROM location WHERE doctor = ? ORDER BY priority";
      PreparedStatement state = con.prepareStatement(query);
      state.setInt(1, doctorID);
      ResultSet rs = state.executeQuery();
      LinkedList<DbNode> offices = new LinkedList<DbNode>();
      while (rs.next()) {
        offices.add(DbController.getNode(rs.getString("nodeID")));
      }
      query = "SELECT doctorID, name, field FROM doctors WHERE doctorID = ?";
      state = con.prepareStatement(query);
      state.setInt(1, doctorID);
      rs = state.executeQuery();
      if (rs.next()) {
        return new Doctor(
            rs.getInt("doctorID"), rs.getString("name"), rs.getString("field"), offices);
      } else {
        throw new DBException("getDoctor: Doctor not found");
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getDoctor", e);
    }
  }

  /**
   * Adds a doctor to the database
   *
   * @param name The doctor's name
   * @param field The field in which they work
   * @param offices A linked list of all of their office in order of priority
   * @return the id of the doctor created
   * @throws DBException
   */
  public static int addDoctor(String name, String field, LinkedList<DbNode> offices)
      throws DBException {
    try {
      String query = "INSERT INTO doctors (name, field) VALUES (?, ?)";
      PreparedStatement state = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      state.setString(1, name);
      state.setString(2, field);
      if (state.executeUpdate() <= 0) throw new DBException("Error: Doctor not added!");
      ResultSet rs = state.getGeneratedKeys();
      rs.next();
      int id = rs.getInt("1");
      if (offices == null) return id;
      Iterator<DbNode> it = offices.iterator();
      while (it.hasNext()) {
        query = "INSERT INTO location (doctor, nodeID) VALUES (?, ?)";
        state = con.prepareStatement(query);
        state.setInt(1, id);
        state.setString(2, it.next().getNodeID());
        state.executeUpdate();
      }
      return id;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: addDoctor", e);
    }
  }

  /**
   * Deletes a doctor from the database
   *
   * @param doctorID The ID Of the doctor to be deleted
   * @return True if successful, false otherwise
   * @throws DBException on error
   */
  public static boolean deleteDoctor(int doctorID) throws DBException {
    try {
      String query = "DELETE FROM doctors WHERE doctorID = ?";
      PreparedStatement state = con.prepareStatement(query);
      state.setInt(1, doctorID);
      return state.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: deleteDoctor", e);
    }
  }

  /**
   * Adds an office to the specified doctor's list of offices
   *
   * @param doctorID The doctor you want to add an office for
   * @param office The node of the doctor's new office
   * @throws DBException On error
   */
  public static void addOffice(int doctorID, DbNode office) throws DBException {
    try {
      String query = "INSERT INTO location (doctor, nodeID) VALUES (?, ?)";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setInt(1, doctorID);
      stmt.setString(2, office.getNodeID());
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error, addOffice", e);
    }
  }

  /**
   * Removes an office from a doctor's list of offices
   *
   * @param doctorID The doctor from which to remove the office
   * @param office the office to remove from the doctor's list of offices
   * @return True if successful, false otherwise
   * @throws DBException On error
   */
  public static boolean removeOffice(int doctorID, DbNode office) throws DBException {
    try {
      String query = ("DELETE FROM location WHERE doctor = ? AND nodeID = ?");
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setInt(1, doctorID);
      stmt.setString(2, office.getNodeID());
      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: removeOffice", e);
    }
  }

  /**
   * Returns a linked list of all doctors in the database.
   *
   * @return A linked list containing all the doctors in the database
   * @throws DBException on error
   */
  public static LinkedList<Doctor> getDoctors() throws DBException {
    try {
      LinkedList<Doctor> docs = new LinkedList<Doctor>();
      String query = "SELECT doctorID FROM doctors";
      PreparedStatement stmt = con.prepareStatement(query);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        docs.add(getDoctor(rs.getInt("doctorID")));
      }
      return docs;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: searchDoctors", e);
    }
  }

  /**
   * Returns all doctors where the name contains the passed-in value as a substring.
   *
   * @param name A substring of the doctor that you're looking for
   * @return A linked list of all doctors with a name with the passed in value as a substring (case
   *     insensitive)
   * @throws DBException on error
   */
  public static LinkedList<Doctor> searchDoctors(String name) throws DBException {
    try {
      LinkedList<Doctor> docs = new LinkedList<Doctor>();
      String query = "SELECT doctorID FROM doctors WHERE UPPER(name) LIKE ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, "%" + name.toUpperCase() + "%");
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        docs.add(getDoctor(rs.getInt("doctorID")));
      }
      return docs;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: searchDoctors", e);
    }
  }
}
