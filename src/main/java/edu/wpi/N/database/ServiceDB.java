package edu.wpi.N.database;

import edu.wpi.N.entities.*;
import edu.wpi.N.entities.employees.Employee;
import edu.wpi.N.entities.employees.Laundry;
import edu.wpi.N.entities.employees.Translator;
import edu.wpi.N.entities.request.LaundryRequest;
import edu.wpi.N.entities.request.Request;
import edu.wpi.N.entities.request.TranslatorRequest;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Pattern;

public class ServiceDB {
  private static Connection con = MapDB.getCon();

  // Noah
  /**
   * Returns the employee specified by the given ID
   *
   * @param id The employee's ID
   * @return an employee entity representing that employee
   */
  public static Employee getEmployee(int id) throws DBException {
    try {
      if (id <= 0) return null; // handle case of unassigned employee without printing anything
      String query = "SELECT * FROM employees WHERE employeeID = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setInt(1, id);
      ResultSet rs = stmt.executeQuery();
      rs.next();
      String sType = rs.getString("serviceType");
      String name = rs.getString("name");
      if (sType.equals("Translator")) {
        query = "SELECT language FROM language WHERE t_EmployeeID = ?";
        stmt = con.prepareStatement(query);
        stmt.setInt(1, id);
        rs = stmt.executeQuery();
        LinkedList<String> languages = new LinkedList<String>();
        while (rs.next()) {
          languages.add(rs.getString("language"));
        }
        return new Translator(id, name, languages);

      } else if (sType.equals("Laundry")) {
        return new Laundry(id, name);
      } else if (sType.equals("Medicine")) {
        return DoctorDB.getDoctor(id);
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
    allEmployee.addAll(DoctorDB.getDoctors());
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
      int rid = rs.getInt("requestID");
      int empId = rs.getInt("assigned_eID");
      String reqNotes = rs.getString("reqNotes");
      String compNotes = rs.getString("compNotes");
      String nodeID = rs.getString("nodeID");
      GregorianCalendar timeReq = getJavatime(rs.getTimestamp("timeRequested"));
      GregorianCalendar timeComp = getJavatime(rs.getTimestamp("timeCompleted"));
      String status = rs.getString("status");
      if (rs.getString("serviceType").equals("Laundry")) {
        return new LaundryRequest(
            rid, empId, reqNotes, compNotes, nodeID, timeReq, timeComp, status);
      } else if (rs.getString("serviceType").equals("Translator")) {
        query = "SELECT language FROM trequest WHERE requestID = ?";
        stmt = con.prepareStatement(query);
        stmt.setInt(1, id);
        rs = stmt.executeQuery();
        rs.next();
        return new TranslatorRequest(
            rid,
            empId,
            reqNotes,
            compNotes,
            nodeID,
            timeReq,
            timeComp,
            status,
            rs.getString("language"));
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
                rs.getString("reqNotes"),
                rs.getString("compNotes"),
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
                rs.getString("compNotes"),
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
                rs.getString("reqNotes"),
                rs.getString("compNotes"),
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
                rs.getString("reqNotes"),
                rs.getString("compNotes"),
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
      String query =
          "SELECT t_employeeID from employees, translator where employeeID = t_employeeID";
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
      String query = "SELECT l_employeeID from employees, laundry where employeeID = l_employeeID";
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
   * @param reqNotes some notes for the translator request
   * @param nodeID The ID of the node in which these services are requested
   * @param language the language that the translator is requested for
   * @return the id of the created request
   */
  public static int addTransReq(String reqNotes, String nodeID, String language)
      throws DBException {
    try {
      String query =
          "INSERT INTO request (timeRequested, reqNotes, serviceType, nodeID, status) VALUES (?, ?, ?, ?, ?)";
      PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
      stmt.setString(2, reqNotes);
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
   * @param reqNotes some notes for the laundry request
   * @param nodeID The ID of the node in which these services are requested
   * @return the id of the created request
   */
  public static int addLaundReq(String reqNotes, String nodeID) throws DBException {
    try {
      String query =
          "INSERT INTO request (timeRequested, reqNotes, serviceType, nodeID, status) VALUES (?, ?, ?, ?, ?)";
      PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
      stmt.setString(2, reqNotes);
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
      throw new DBException("Error: addLaundReq", e);
    }
  }

  // Noah
  /**
   * Assigns an employee to a request; the employee must be able to fulfil that request
   *
   * @param employeeID the ID of the employee to be assigned
   * @param requestID The ID of the request to which they will be assigned.
   */
  public static void assignToRequest(int employeeID, int requestID) throws DBException {
    try {
      Employee emp = getEmployee(employeeID);
      Request req = getRequest(requestID);
      if (!emp.getServiceType().equals(req.getServiceType())) {
        throw new DBException(
            "Invalid kind of employee! That employee isn't authorized for that kind of job!");
      }
      if (req instanceof TranslatorRequest) {
        String language = ((TranslatorRequest) req).getAtr1();
        if (!((Translator) emp).getLanguages().contains(language)) {
          throw new DBException(
              "Invalid selection: That translator can't speak the requested langauge");
        }
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
   * @param compNotes notes regarding the completion of the request
   */
  public static void completeRequest(int requestID, String compNotes) throws DBException {
    try {
      String query = "SELECT status FROM request WHERE requestID = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setInt(1, requestID);
      ResultSet rs = stmt.executeQuery();
      rs.next();
      if (!rs.getString("status").equals("OPEN")) {
        throw new DBException("That request isn't open!");
      }
      query =
          "UPDATE request SET status = 'DONE', timeCompleted = ?, compNotes = ? WHERE requestID = ?";
      stmt = con.prepareStatement(query);
      stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
      stmt.setInt(3, requestID);
      stmt.setString(2, compNotes);
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
      stmt.executeUpdate();
    } catch (SQLException e) {
      if (e.getSQLState()
          .equals("23503")) { // foreign key violation (so the employeeID isn't in translator)
        throw new DBException("Error: Translator by ID " + employeeID + " does not exist!");
      }
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
   * @param compNotes Notes on the denial of this request
   * @throws DBException on unsuccess
   */
  public static void denyRequest(int requestID, String compNotes) throws DBException {
    try {
      String query = "SELECT status FROM request WHERE requestID = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setInt(1, requestID);
      ResultSet rs = stmt.executeQuery();
      rs.next();
      if (!rs.getString("status").equals("OPEN")) {
        throw new DBException("That request isn't open!");
      }

      query =
          "UPDATE request SET status = 'DENY', timeCompleted = ?, compNotes = ? WHERE requestID = ?";
      stmt = con.prepareStatement(query);
      stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
      stmt.setInt(3, requestID);
      stmt.setString(2, compNotes);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Error: completeRequest", e);
    }
  }

  /**
   * Returns all the languages that are currently available
   *
   * @return a linked list of strings representing all the languages available
   */
  public static LinkedList<String> getLanguages() throws DBException {
    try {
      String query = "SELECT DISTINCT language FROM language";
      PreparedStatement stmt = con.prepareStatement(query);
      ResultSet rs = stmt.executeQuery();
      LinkedList<String> langs = new LinkedList<String>();
      while (rs.next()) {
        langs.add(rs.getString("language"));
      }
      return langs;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getlanguages", e);
    }
  }

  /**
   * Changes the time that a service is available. Important: Times must be in a five-character time
   * format in 24-hour time Examples of valid times: 08:45, 14:20, 00:15 (12:15 AM), invalid times:
   * 8:45, 2:20PM, 12:15 would be 15 minutes past noon
   *
   * @param serviceType The service type which you want to change
   * @param startTime The new start time for the service
   * @param endTime The new end time for the service
   * @throws DBException On error or when input is invalid.
   */
  public static void setServiceTime(String serviceType, String startTime, String endTime)
      throws DBException {
    String p = "([01]\\d:[0-6]\\d)|(2[0-4]:[0-6]\\d)";
    Pattern pattern = Pattern.compile(p);
    if (startTime.length() == 5
        && endTime.length() == 5
        && pattern.matcher(startTime).matches()
        && pattern.matcher(startTime).matches()) {
      String query = "UPDATE service SET timeStart = ?, timeEnd = ? WHERE serviceType = ?";
      try {
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, startTime);
        stmt.setString(2, endTime);
        stmt.setString(3, serviceType);
        if (stmt.executeUpdate() <= 0) throw new DBException("That service type is invalid!");
      } catch (SQLException e) {
        e.printStackTrace();
        throw new DBException("Unknown error: setServiceTime ", e);
      }
    } else
      throw new DBException(
          "The times you entered, " + startTime + ", " + endTime + ", are invalid!");
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
