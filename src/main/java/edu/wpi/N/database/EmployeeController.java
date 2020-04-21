package edu.wpi.N.database;

import edu.wpi.N.entities.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class EmployeeController {
  private static Connection con = DbController.getCon();

  public static void initEmployee() throws DBException {
    try {
      String query =
          "CREATE TABLE service ("
              + "serviceType VARCHAR(255) NOT NULL PRIMARY KEY,"
              + "timeStart CHAR(5),"
              + "timeEnd CHAR(5),"
              + "description VARCHAR(255))";
      PreparedStatement state = con.prepareStatement(query);
      state.execute();
      query =
          "INSERT INTO service VALUES ('Translator', '00:00', '00:00', 'Make a request for our translation services!')";
      state = con.prepareStatement(query);
      state.execute();

      query =
          "INSERT INTO service VALUES ('Laundry', '00:00', '00:00', 'Make a request for laundry services!')";
      state = con.prepareStatement(query);
      state.execute();
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) {
        e.printStackTrace();
        throw new DBException("Unknown error: initEmployee", e);
      }
    }
    try {
      String query =
          "CREATE TABLE employees ("
              + "employeeID INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
              + "name VARCHAR(255) NOT NULL,"
              + "serviceType VARCHAR(255) NOT NULL,"
              + "FOREIGN KEY (serviceType) REFERENCES service (serviceType))";

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
              + "FOREIGN KEY (t_employeeID) REFERENCES employees(employeeID) ON DELETE CASCADE)";
      PreparedStatement state = con.prepareStatement(query);
      state.execute();
      query =
          "CREATE TABLE language ("
              + "t_employeeID INT NOT NULL, "
              + "language VARCHAR(255) NOT NULL, "
              + "CONSTRAINT LANG_PK PRIMARY KEY (t_employeeID, language),"
              + "FOREIGN KEY (t_employeeID) REFERENCES translator (t_employeeID) ON DELETE CASCADE)";
      state = con.prepareStatement(query);
      state.execute();
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) {
        e.printStackTrace();
        throw new DBException("Unknown error: initEmployee creating translator table", e);
      }
    }
    try {
      String query =
          "CREATE TABLE laundry("
              + "l_employeeID INT NOT NULL References employees(employeeID) ON DELETE CASCADE,"
              + "PRIMARY KEY(l_employeeID))";
      PreparedStatement state = con.prepareStatement(query);
      state.execute();
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) {
        e.printStackTrace();
        throw new DBException("Unknown error: intiEmployee creating laundry table", e);
      }
    }
    try {
      String query =
          "CREATE TABLE request("
              + "requestID INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
              + "timeRequested TIMESTAMP NOT NULL,"
              + "timeCompleted TIMESTAMP,"
              + "notes VARCHAR(255),"
              + "assigned_eID INT REFERENCES employees(employeeID) ON DELETE SET NULL,"
              + "serviceType VARCHAR(255) NOT NULL REFERENCES service(serviceType),"
              + "nodeID CHAR(10) REFERENCES nodes(nodeID) ON DELETE SET NULL,"
              + "status CHAR(4) NOT NULL CONSTRAINT STAT_CK CHECK (status IN ('OPEN', 'DENY', 'DONE')))";
      PreparedStatement state = con.prepareStatement(query);
      state.execute();
      query =
          "CREATE TABLE lrequest("
              + "requestID INT NOT NULL PRIMARY KEY REFERENCES request(requestID) ON DELETE CASCADE)";
      state = con.prepareStatement(query);
      state.execute();
      query =
          "CREATE TABLE trequest("
              + "requestID INT NOT NULL PRIMARY KEY REFERENCES request(requestID) ON DELETE CASCADE,"
              + "language VARCHAR(255) NOT NULL)";
      state = con.prepareStatement(query);
      state.execute();
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) {
        e.printStackTrace();
        throw new DBException("Unknown error: intiEmployee creating Request table", e);
      }
    }
  }

  // Noah
  /**
   * Returns the employee specified by the given ID
   *
   * @param id The employee's ID
   * @return an employee entity representing that employee
   */
  public static Employee getEmployee(int id) throws DBException {
    try {
      String query = "SELECT * FROM employees WHERE employeeID = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setInt(1, id);
      ResultSet rs = stmt.executeQuery();
      rs.next();
      if (rs.getString("serviceType").equals("Translator")) {
        String name = rs.getString("name");
        query = "SELECT language FROM language WHERE t_EmployeeID = ?";
        stmt = con.prepareStatement(query);
        stmt.setInt(1, id);
        rs = stmt.executeQuery();
        LinkedList<String> languages = new LinkedList<String>();
        while (rs.next()) {
          languages.add(rs.getString("language"));
        }
        return new Translator(id, name, languages);

      } else if (rs.getString("serviceType").equals("Laundry")) {
        return new Laundry(id, rs.getString("name"));
      } else
        throw new DBException(
            "Invalid employee in table employees! ID: " + id + "Name: " + rs.getString("name"));

    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getEmployee. ID: " + id, e);
    }
  }

  // Chris
  /**
   * Returns a list of all employees in the database
   *
   * @return a linked list of all employees in the database
   */
  public static LinkedList<Employee> getEmployees() throws DBException {
    LinkedList<Employee> allEmployee = new LinkedList<Employee>();
    allEmployee.addAll(getTranslators());
    allEmployee.addAll(getLaundrys());
    return allEmployee;
  }

  // Nick
  /**
   * Gets all services in the database
   *
   * @return a linked list of all services in the database
   */
  public static LinkedList<Service> getServices() throws DBException {
    LinkedList<Service> ret = new LinkedList<>();

    try {
      String query = "SELECT * FROM service";
      PreparedStatement st = con.prepareStatement(query);
      ResultSet rs = st.executeQuery();
      while (rs.next()) {
        ret.add(
            new Service(
                rs.getString("timeStart"),
                rs.getString("timeEnd"),
                rs.getString("serviceType"),
                rs.getString("description")));
      }
      return ret;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getServices", e);
    }
  }

  public static Request getRequest(int id) throws DBException {
    try {
      String query = "SELECT * FROM request WHERE requestID = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setInt(1, id);
      ResultSet rs = stmt.executeQuery();
      rs.next();
      if (rs.getString("serviceType").equals("Laundry")) {
        return new LaundryRequest(
            rs.getInt("requestID"),
            rs.getInt("assigned_eID"),
            rs.getString("notes"),
            rs.getString("nodeID"),
            getJavatime(rs.getTimestamp("timeRequested")),
            getJavatime(rs.getTimestamp("timeCompleted")),
            rs.getString("status"));
      } else if (rs.getString("serviceType").equals("Translator")) {
        int rid = rs.getInt("requestID");
        int empId = rs.getInt("assigned_eID");
        String notes = rs.getString("notes");
        String nodeID = rs.getString("nodeID");
        GregorianCalendar timeReq = getJavatime(rs.getTimestamp("timeRequested"));
        GregorianCalendar timeComp = getJavatime(rs.getTimestamp("timeCompleted"));
        String status = rs.getString("status");
        query = "SELECT language FROM trequest WHERE requestID = ?";
        stmt = con.prepareStatement(query);
        stmt.setInt(1, id);
        rs = stmt.executeQuery();
        rs.next();
        return new TranslatorRequest(
            rid, empId, notes, nodeID, timeReq, timeComp, status, rs.getString("language"));
      } else throw new DBException("Invalid request! ID = " + id);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getRequest", e);
    }
  }

  // Noah
  /**
   * Gets all the requests in the database
   *
   * @return a linked list of all service requests in the database
   */
  public static LinkedList<Request> getRequests() throws DBException {
    try {
      LinkedList<Request> requests = new LinkedList<Request>();
      String query = "SELECT * FROM request WHERE serviceType = 'Laundry'";
      PreparedStatement stmt = con.prepareStatement(query);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        requests.add(
            new LaundryRequest(
                rs.getInt("requestID"),
                rs.getInt("assigned_eID"),
                rs.getString("notes"),
                rs.getString("nodeID"),
                getJavatime(rs.getTimestamp("timeRequested")),
                getJavatime(rs.getTimestamp("timeCompleted")),
                rs.getString("status")));
      }
      query = "SELECT * from request, trequest WHERE request.requestID = trequest.requestID";
      stmt = con.prepareStatement(query);
      rs = stmt.executeQuery();
      while (rs.next()) {
        requests.add(
            new TranslatorRequest(
                rs.getInt("requestID"),
                rs.getInt("assigned_eID"),
                rs.getString("notes"),
                rs.getString("nodeID"),
                getJavatime(rs.getTimestamp("timeRequested")),
                getJavatime(rs.getTimestamp("timeCompleted")),
                rs.getString("status"),
                rs.getString("language")));
      }
      return requests;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getRequests", e);
    }
  }

  // Chris
  /**
   * Gets all the open requests (not completed requests) in the database
   *
   * @return a linked list of all open service requests in the database
   */
  public static LinkedList<Request> getOpenRequests() throws DBException {
    LinkedList<Request> openList = new LinkedList<>();
    try {
      String query =
          "SELECT * FROM request, trequest WHERE request.requestID = trequest.requestID AND status='OPEN'";
      PreparedStatement stmt = con.prepareStatement(query);

      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        openList.add(
            new TranslatorRequest(
                rs.getInt("requestID"),
                rs.getInt("assigned_eID"),
                rs.getString("notes"),
                rs.getString("nodeID"),
                getJavatime(rs.getTimestamp("timeRequested")),
                getJavatime(rs.getTimestamp("timeCompleted")),
                rs.getString("status"),
                rs.getString("language")));
      }
      query =
          "SELECT * FROM request, lrequest WHERE request.requestID = lrequest.requestID AND status = 'OPEN'";
      stmt = con.prepareStatement(query);
      rs = stmt.executeQuery();
      while (rs.next()) {
        openList.add(
            new LaundryRequest(
                rs.getInt("requestID"),
                rs.getInt("assigned_eID"),
                rs.getString("notes"),
                rs.getString("nodeID"),
                getJavatime(rs.getTimestamp("timeRequested")),
                getJavatime(rs.getTimestamp("timeCompleted")),
                rs.getString("status")));
      }
      return openList;
    } catch (SQLException ex) {
      ex.printStackTrace();
      throw new DBException("Error: getOpenRequest", ex);
    }
  }

  // Nick
  /**
   * Gets all the translators in the database
   *
   * @return a linked list of all translators in the database
   */
  public static LinkedList<Translator> getTranslators() throws DBException {
    try {
      String query = "SELECT * from employees, translator where employeeID = t_employeeID";
      PreparedStatement stmt = con.prepareStatement(query);
      ResultSet rs = stmt.executeQuery();
      LinkedList<Translator> translators = new LinkedList<Translator>();
      while (rs.next()) {
        translators.add((Translator) getEmployee(rs.getInt("t_employeeID")));
      }
      return translators;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getTranslators", e);
    }
  }

  // Noah
  /**
   * Gets all the laundrys in the database
   *
   * @return a linked list of all people who can do laundry in the database
   */
  public static LinkedList<Laundry> getLaundrys() throws DBException {
    try {
      String query = "SELECT * from employees, laundry where employeeID = l_employeeID";
      PreparedStatement stmt = con.prepareStatement(query);
      ResultSet rs = stmt.executeQuery();
      LinkedList<Laundry> laundrys = new LinkedList<Laundry>();
      while (rs.next()) {
        laundrys.add((Laundry) getEmployee(rs.getInt("l_employeeID")));
      }
      return laundrys;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getLaundrys", e);
    }
  }

  // Chris
  /**
   * Returns a list of all translators who speak a specified langauge
   *
   * @param lang the language that you want the translators to speak
   * @return a linked list of all the translators who speak a specified language
   */
  public static LinkedList<Translator> getTransLang(String lang) throws DBException {
    try {
      String query =
          "SELECT translator.t_employeeID FROM translator, (SELECT * FROM language where language = ?) AS language"
              + " WHERE translator.t_employeeID = language.t_employeeID";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, lang);
      ResultSet rs = stmt.executeQuery();
      LinkedList<Translator> translators = new LinkedList<Translator>();
      while (rs.next()) {
        translators.add((Translator) getEmployee(rs.getInt("t_employeeID")));
        //        LinkedList<String> langs = new LinkedList<String>();
        //        int id = rs.getInt("t_employeeID");
        //        String name = rs.getString("name");
        //        while (rs.getInt("t_employeeID") == id) {
        //          langs.add(rs.getString("language"));
        //          rs.next();
        //        }
        //        translators.add(new Translator(id, name, langs));
      }
      return translators;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getTransLang, lang :" + lang, e);
    }
    //    LinkedList<Translator> list = getTranslators();
    //    LinkedList<Translator> special = new LinkedList<Translator>();
    //    for (int i = 1; i < list.size(); i++) {
    //      if (list.get(i).getLanguages().equals(lang)) special.add(list.get(i));
    //    }
    //    return special;
  }

  // Nick
  /**
   * Adds a translator to the database
   *
   * @param name the translator's name
   * @param languages the languages that this translator is capable of speaking
   * @return id of created request
   */
  public static int addTranslator(String name, LinkedList<String> languages) throws DBException {
    try {
      String query = "INSERT INTO employees (name, serviceType) VALUES (?, 'Translator')";
      PreparedStatement st = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      st.setString(1, name);
      st.executeUpdate();
      ResultSet rs = st.getGeneratedKeys();
      rs.next();
      query = "INSERT INTO translator VALUES(?)";
      st = con.prepareStatement(query);
      int id = rs.getInt("1");
      st.setInt(1, id);
      st.executeUpdate();
      Iterator<String> langIt = languages.iterator();
      while (langIt.hasNext()) {
        query = "INSERT INTO language VALUES (?, ?)";
        st = con.prepareStatement(query);
        st.setInt(1, id);
        st.setString(2, langIt.next());
        st.executeUpdate();
      }
      return id;
    } catch (SQLException e) {
      // e.printStackTrace();
      throw new DBException("Unknown error: addTranslator", e);
    }
  }

  // Noah
  /**
   * Adds a laundry employee to the database
   *
   * @param name the laundry employee's name
   * @return id of created request
   */
  public static int addLaundry(String name) throws DBException {
    try {
      String query = "INSERT INTO employees (name, serviceType) VALUES (?, 'Laundry')";
      PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, name);
      stmt.executeUpdate();
      ResultSet rs = stmt.getGeneratedKeys();
      rs.next(); // NullPointerException
      query = "INSERT INTO Laundry VALUES (?)";
      stmt = con.prepareStatement(query);
      int id = rs.getInt("1");
      stmt.setInt(1, id);
      stmt.executeUpdate();
      return id;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: addLaundry , name = " + name, e);
    }
  }

  // Chris
  /**
   * Adds a request for a translator
   *
   * @param notes some notes for the translator request
   * @param nodeID The ID of the node in which these services are requested
   * @param language the language that the translator is requested for
   * @return the id of the created request
   */
  public static int addTransReq(String notes, String nodeID, String language) throws DBException {
    try {
      String query =
          "INSERT INTO request (timeRequested, notes, serviceType, nodeID, status) VALUES (?, ?, ?, ?, ?)";
      PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
      stmt.setString(2, notes);
      stmt.setString(3, "Translator");
      stmt.setString(4, nodeID);
      stmt.setString(5, "OPEN");
      stmt.execute();
      ResultSet rs = stmt.getGeneratedKeys();
      rs.next();
      query = "INSERT INTO trequest (requestID, language) VALUES (?, ?)";
      stmt = con.prepareStatement(query);
      int id = rs.getInt("1");
      stmt.setInt(1, id);
      stmt.setString(2, language);
      stmt.executeUpdate();
      return id;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Error: addTransReq", e);
    }
  }

  // Nick
  /**
   * Adds a request for laundry
   *
   * @param notes some notes for the laundry request
   * @param nodeID The ID of the node in which these services are requested
   * @return the id of the created request
   */
  public static int addLaundReq(String notes, String nodeID) throws DBException {
    try {
      String query =
          "INSERT INTO request (timeRequested, notes, serviceType, nodeID, status) VALUES (?, ?, ?, ?, ?)";
      PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
      stmt.setString(2, notes);
      stmt.setString(3, "Laundry");
      stmt.setString(4, nodeID);
      stmt.setString(5, "OPEN");
      stmt.execute();
      ResultSet rs = stmt.getGeneratedKeys();
      rs.next();
      query = "INSERT INTO lrequest (requestID) VALUES (?)";
      stmt = con.prepareStatement(query);
      int id = rs.getInt("1");
      stmt.setInt(1, id);
      stmt.executeUpdate();
      return id;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Error: addTransReq", e);
    }
  }

  // Noah
  /**
   * Assigns an employee to a request; the employee must be able to fulfil that request
   *
   * @param employeeID the ID of the employee to be assigned
   * @param requestID The ID of the request to which they will be assigned.
   * @return
   */
  public static void assignToRequest(int employeeID, int requestID) throws DBException {
    try {
      Employee emp = getEmployee(employeeID);
      Request req = getRequest(requestID);
      if (!emp.getServiceType().equals(req.getServiceType())) {
        throw new DBException(
            "Invalid kind of employee! That employee isn't authorized for that kind of job!");
      }
      String query = "UPDATE request SET assigned_eID = ? WHERE requestID = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setInt(1, employeeID);
      stmt.setInt(2, requestID);
      if (stmt.executeUpdate() <= 0) throw new DBException("That requestID is invalid!");
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: assignToRequest", e);
    }
  }

  // Chris
  /**
   * Marks a request as completed and done at the time that this function was called
   *
   * @param requestID the ID of the request to be marked as completed
   * @return true on success, false otherwise
   */
  public static void completeRequest(int requestID) throws DBException {
    try {
      String query = "UPDATE request SET status = 'DONE', timeCompleted = ? WHERE requestID = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
      stmt.setInt(2, requestID);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Error: completeRequest", e);
    }
  }

  // Nick
  /**
   * Removes an employee from the database
   *
   * @param employeeID the id of the employee to be excised
   */
  public static void removeEmployee(int employeeID) throws DBException {
    try {
      String query = "DELETE FROM employees WHERE employeeID = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setInt(1, employeeID);
      if (stmt.executeUpdate() <= 0) throw new DBException("That employeeID is invalid!");
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: removeEmployee", e);
    }
  }

  // regEx
  /**
   * Adds a language to the translator with the specified employee ID
   *
   * @param employeeID The ID of the employee who will speak this new language
   * @param language The language to be added
   * @throws DBException if the employee isn't a translator
   */
  public static void addLanguage(int employeeID, String language) throws DBException {
    try {
      String query = "INSERT INTO language VALUES(?, ?)";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setInt(1, employeeID);
      stmt.setString(2, language);
      if (stmt.executeUpdate() <= 0) throw new DBException("That translator doesn't exist");
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: addLanguage, eid = " + employeeID, e);
    }
  }

  // Chris
  /**
   * Removes a language to the translator with the specified employee ID
   *
   * @param employeeID The ID of the employee who no longer speaks the given language
   * @param language The language to be removed
   * @throws DBException if the employee isn't a translator
   */
  public static void removeLanguage(int employeeID, String language) throws DBException {
    try {
      String query = "DELETE FROM language WHERE t_employeeID = ? AND language = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setInt(1, employeeID);
      stmt.setString(2, language);
      if (stmt.executeUpdate() <= 0) throw new DBException("That translator doesn't exist");
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException(
          "Unknown Error: removeLanguaguage, ID: " + employeeID + " Lang: " + language, e);
    }
  }

  // Nick
  /**
   * Denies a given request
   *
   * @param requestID The request id of an open request to deny
   * @throws DBException on unsuccess
   */
  public static void denyRequest(int requestID) throws DBException {
    try {
      String query = "UPDATE request SET status = 'DENY', timeCompleted = ? WHERE requestID = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
      stmt.setInt(2, requestID);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Error: completeRequest", e);
    }
  }

  public static GregorianCalendar getJavatime(Timestamp time) {
    if (time == null) {
      return null;
    }
    Calendar cal = GregorianCalendar.getInstance();
    cal.setTime(time);
    return (GregorianCalendar) cal;
  }

  public static Timestamp getSqltime(GregorianCalendar cal) {
    Timestamp time = new Timestamp(cal.getTime().getTime());
    return time;
  }
}
