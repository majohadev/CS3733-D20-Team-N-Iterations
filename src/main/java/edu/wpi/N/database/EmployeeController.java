package edu.wpi.N.database;

import edu.wpi.N.entities.Employee;
import edu.wpi.N.entities.Translator;
import java.sql.*;
import java.util.*;

public class EmployeeController {
  private static Connection con = DbController.getCon();

  public static void initEmployee() throws DBException {
    try {
      String query =
          "CREATE TABLE employees ("
              + "employeeID INT NOT NULL PRIMARY KEY, "
              + "name VARCHAR(255) NOT NULL)";

      PreparedStatement state = con.prepareStatement(query);
      state.execute();
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) {
        e.printStackTrace();
        throw new DBException("Unknown error: initEmployee", e);
      }
    }
    try {
      String query =
          "CREATE TABLE translator ("
              + "t_employeeID INT NOT NULL PRIMARY KEY,"
              + "language VARCHAR(255) NOT NULL,"
              + "FOREIGN KEY (t_employeeID) REFERENCES employees(employeeID) ON DELETE CASCADE)";
      PreparedStatement state = con.prepareStatement(query);
      state.execute();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: initEmployee creating translator Table", e);
    }
    try {
      String query =
          "CREATE TABLE laundry("
              + "l_employeeID INT NOT NULL References employees(employeeID),"
              + "PRIMARY KEY(l_employeeID))";
      PreparedStatement state = con.prepareStatement(query);
      state.execute();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: intiEmployee creating laundry table", e);
    }
  }

  public static Employee getEmployee(int id) throws DBException {
    try {
      String query = "SELECT * FROM employees WHERE employeeID = ?";
      PreparedStatement state = con.prepareStatement(query);
      ResultSet rs = state.executeQuery();

      if (rs.next()) {
        return new Employee(rs.getInt("id"), rs.getString("name"));
      } else throw new DBException("Employee not in Database");
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("getEmployee not found", e);
    }
  }

  public static Employee getTranslator(int id) throws DBException {
    try {
      String query =
          "SELECT * FROM translator WHERE t_employeeID = ? JOIN employees ON t_employeeID = employeeID";
      PreparedStatement state = con.prepareStatement(query);
      state.setInt(1, id);
      ResultSet rs = state.executeQuery();

      if (rs.next()) {
        return new Translator(rs.getInt("id"), rs.getString("name"), rs.getString("language"));
      } else throw new DBException("Translator not in Database");
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getTranslator", e);
    }
  }

  public static boolean addEmployee(int id, String name) throws DBException {
    try {
      String query = "INSERT INTO employees VALUES (?, ?)";
      PreparedStatement state = con.prepareStatement(query);
      state.setInt(1, id);
      state.setString(2, name);
      return state.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: addEmployee method", e);
    }
  }

  public static boolean addTranslator(int id, String lang) throws DBException {
    try {
      String query = "INSERT INTO translator VALUES (?, ?)";
      PreparedStatement state = con.prepareStatement(query);
      state.setInt(1, id);
      state.setString(2, lang);
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: addTranslator method", e);
    }
  }

  public static boolean deleteEmployee(int id) throws DBException {
    try {
      String query = "DELETE FROM employees WHERE (employeeID = ?)";
      PreparedStatement state = con.prepareStatement(query);
      state.setInt(1, id);
      return state.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Error: deleteEmployee is unavailable", e);
    }
  }

  public static boolean modifyEmployee(int id, String name) throws DBException {
    try {
      String query = "UPDATE employees SET employeeID = ?, name = ?";
      PreparedStatement state = con.prepareStatement(query);
      state.setInt(1, id);
      state.setString(2, name);
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Error: modifyEmployee not working", e);
    }
  }

  public static boolean modifyLanguage(int id, String name, String lang) throws DBException {
    try {
      String query = "UPDATE translator SET t_employeeID = ?, name = ?, language = ?";
      PreparedStatement state = con.prepareStatement(query);
      state.setInt(1, id);
      state.setString(2, name);
      state.setString(3, lang);
      return true;
    } catch (SQLException e) {
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
      transList.add(new Employee(rs.getInt("employeeID"), rs.getString("name")));
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
