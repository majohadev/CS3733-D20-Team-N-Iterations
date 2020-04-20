package edu.wpi.N.database;

import edu.wpi.N.entities.Employee;
import java.sql.*;
import java.util.*;

public class ServiceController {
  private static Connection con = DbController.getCon();

  public static void initService() throws DBException {
    try {
      String query =
          "CREATE TABLE employees ("
              + "employeeID INT NOT NULL PRIMARY KEY, "
              + "name VARCHAR(255) NOT NULL, "
              + "yearsofExperience INT NOT NULL, "
              + "lang VARCHAR(255) NOT NULL, "
              + "gender CHAR(1) NOT NULL, "
              + "start TIMESTAMP NOT NULL, "
              + "endTime TIMESTAMP NOT NULL, "
              + "type VARCHAR(255) NOT NULL)";

      PreparedStatement state = con.prepareStatement(query);
      state.execute();
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) {
        e.printStackTrace();
        throw new DBException("Unknown error: initService", e);
      }
    }
  }

  public static Employee getService(int id) throws DBException {
    try {
      String query = "SELECT * FROM employees WHERE employeeID = ?";
      PreparedStatement state = con.prepareStatement(query);
      state.setInt(1, id);
      ResultSet rs = state.executeQuery();

      if (rs.next()) {
        return new Employee(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("yearsofExperience"),
            rs.getString("lang"),
            rs.getString("gender").charAt(0),
            rs.getTimestamp("start"),
            rs.getTimestamp("end"),
            rs.getString("type"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("getService not found", e);
    }
    return null;
  }

  public static boolean addService(
      int id,
      String name,
      int years,
      String language,
      char gender,
      Timestamp start,
      Timestamp end,
      String type)
      throws DBException {
    try {
      String query = "INSERT INTO employees VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
      PreparedStatement state = con.prepareStatement(query);
      state.setInt(1, id);
      state.setString(2, name);
      state.setInt(3, years);
      state.setString(4, language);
      state.setString(5, String.valueOf(gender));
      state.setTimestamp(6, start);
      state.setTimestamp(7, end);
      state.setString(8, type);
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: addService method", e);
    }
  }

  public static boolean deleteService(int id) throws DBException {
    try {
      String query = "DELETE FROM employees WHERE (employeeID = ?)";
      PreparedStatement state = con.prepareStatement(query);
      state.setInt(1, id);
      return state.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Error: deleteService is unavailable", e);
    }
  }

  public static boolean modifyService(
      int id,
      String name,
      int years,
      String lang,
      char gender,
      Timestamp start,
      Timestamp end,
      String type)
      throws DBException {
    try {
      String query =
          "UPDATE employees SET employeeID = ?, name = ?, yearsofExperience =?, lang = ?, gender = ?, start = ?, end = ?, type = ?";
      PreparedStatement state = con.prepareStatement(query);
      state.setInt(1, id);
      state.setString(2, name);
      state.setInt(3, years);
      state.setString(4, lang);
      state.setString(5, String.valueOf(gender));
      state.setTimestamp(6, start);
      state.setTimestamp(7, end);
      state.setString(8, type);
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Error: modifyService not working", e);
    }
  }

  public static boolean modifyLanguage(int id, String lang) throws DBException {
    try {
      return modifyService(
          id,
          getService(id).getName(),
          getService(id).getYearsofExperience(),
          lang,
          getService(id).getGender(),
          getService(id).getStart(),
          getService(id).getEnd(),
          getService(id).getType());
    } catch (DBException e) {
      e.printStackTrace();
      throw new DBException("modify language not working properly", e);
    }
  }

  public static List<Employee> searchbyLang(String lang) throws DBException {
    String query = "SELECT * FROM employees WHERE";
    LinkedList<String> queries = new LinkedList<String>();
    if (lang != null) {
      queries.add("lang = ?");
    }
    if (queries.size() == 0) throw new DBException("Error: size is wrong in searchbyLang method");
    Iterator<String> it = queries.iterator();
    while (true) {
      query = query + it.next();
      if (it.hasNext()) query = query + "AND";
      else break;
    }
    try {
      PreparedStatement state = con.prepareStatement(query);
      int ct = 0;
      if (lang != null) {
        state.setString(++ct, lang);
      }
      return getallTranslator(state);

    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Error: Unknown error in searchbyLang", e);
    }
  }

  public static LinkedList<Employee> getallTranslator(PreparedStatement stmt) throws SQLException {
    LinkedList<Employee> transList = new LinkedList<Employee>();
    ResultSet rs = stmt.executeQuery();
    while (rs.next()) {
      transList.add(
          new Employee(
              rs.getInt("employeeID"),
              rs.getString("name"),
              rs.getInt("yearsofExperience"),
              rs.getString("lang"),
              rs.getString("gender").charAt(0),
              rs.getTimestamp("start"),
              rs.getTimestamp("end"),
              rs.getString("type")));
    }
    return transList;
  }

  public static Calendar getJavatime(Timestamp time) {
    Calendar cal = GregorianCalendar.getInstance();
    cal.setTime(time);
    return cal;
  }

  public static Timestamp getSqltime(GregorianCalendar cal) {
    Timestamp time = new Timestamp(cal.getTime().getTime());
    return time;
  }
}
