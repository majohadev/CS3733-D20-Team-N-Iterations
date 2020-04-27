package edu.wpi.N.database;

import edu.wpi.N.entities.*;
import edu.wpi.N.entities.employees.*;
import edu.wpi.N.entities.request.*;
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
  // TODO: Add your employee to getEmployee
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
      } else if (sType.equals("Emotional Support")) {
        return new EmotionalSupporter(id, name);
      } else if (sType.equals("Medicine")) {
        return DoctorDB.getDoctor(id);
      } else if (sType.equals("Sanitation")) {
        return new Sanitation(id, name);
      } else if (sType.equals("Wheelchair")) {
        return new WheelchairEmployee(id, name);
      } else if (sType.equals("IT")) {
        return new IT(id, name);
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
  // TODO add your employee type to this function, must first create a function to get all employees
  // of your type
  public static LinkedList<Employee> getEmployees() throws DBException {
    LinkedList<Employee> allEmployee = new LinkedList<Employee>();
    allEmployee.addAll(getTranslators());
    allEmployee.addAll(getLaundrys());
    allEmployee.addAll(getEmotionalSupporters());
    allEmployee.addAll(DoctorDB.getDoctors());
    allEmployee.addAll(getWheelchairEmployees());
    allEmployee.addAll(getITs());
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

  // TODO: Add your service request here
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
      String sType = rs.getString("serviceType");
      if (sType.equals("Laundry")) {
        return new LaundryRequest(
            rid, empId, reqNotes, compNotes, nodeID, timeReq, timeComp, status);
      } else if (sType.equals("Translator")) {
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
      } else if (sType.equals("Wheelchair")) {
        query = "SELECT needsAssistance FROM wrequest WHERE requestID = ?";
        stmt = con.prepareStatement(query);
        stmt.setInt(1, id);
        rs = stmt.executeQuery();
        rs.next();
        return new WheelchairRequest(
            rid,
            empId,
            reqNotes,
            compNotes,
            nodeID,
            timeReq,
            timeComp,
            status,
            rs.getString("needsAssistance"));
      } else if (sType.equals("Emotional Support")) {
        query = "SELECT supportType FROM erequest WHERE requestID = ?";

        stmt = con.prepareStatement(query);
        stmt.setInt(1, id);
        rs = stmt.executeQuery();
        rs.next();
        return new EmotionalRequest(
            rid,
            empId,
            reqNotes,
            compNotes,
            nodeID,
            timeReq,
            timeComp,
            status,
            rs.getString("supportType"));
      } else if (rs.getString("serviceType").equals("Medicine")) {
        query =
            "SELECT medicineName, dosage, units, patient FROM medicineRequests WHERE requestID = ?";
        stmt = con.prepareStatement(query);
        stmt.setInt(1, id);
        rs = stmt.executeQuery();
        rs.next();
        return new MedicineRequest(
            rid,
            empId,
            reqNotes,
            compNotes,
            nodeID,
            timeReq,
            timeComp,
            status,
            rs.getString("medicineName"),
            rs.getDouble("dosage"),
            rs.getString("units"),
            rs.getString("patient"));
      } else if (sType.equals("Sanitation")) {
        query = "SELECT size, sanitationType, danger FROM SANITATIONREQUESTS WHERE requestID = ?";
        stmt = con.prepareStatement(query);
        stmt.setInt(1, id);
        rs = stmt.executeQuery();
        rs.next();
        return new SanitationRequest(
            rid,
            empId,
            reqNotes,
            compNotes,
            nodeID,
            timeReq,
            timeComp,
            status,
            rs.getString("sanitationType"),
            rs.getString("size"),
            rs.getString("danger"));
      } else if (sType.equals("IT")) {
        query = "SELECT device, problem FROM ITrequest WHERE requestID = ?";
        stmt = con.prepareStatement(query);
        stmt.setInt(1, id);
        rs = stmt.executeQuery();
        rs.next();
        return new ITRequest(
            rid,
            empId,
            reqNotes,
            compNotes,
            nodeID,
            timeReq,
            timeComp,
            status,
            rs.getString("device"),
            rs.getString("problem"));
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
  // TODO: add your request type to getRequests
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
                rs.getString("reqNotes"),
                rs.getString("compNotes"),
                rs.getString("nodeID"),
                getJavatime(rs.getTimestamp("timeRequested")),
                getJavatime(rs.getTimestamp("timeCompleted")),
                rs.getString("status"),
                rs.getString("language")));
      }
      query = "SELECT * from request, erequest WHERE request.requestID = erequest.requestID";
      stmt = con.prepareStatement(query);
      rs = stmt.executeQuery();
      while (rs.next()) {
        requests.add(
            new EmotionalRequest(
                rs.getInt("requestID"),
                rs.getInt("assigned_eID"),
                rs.getString("reqNotes"),
                rs.getString("compNotes"),
                rs.getString("nodeID"),
                getJavatime(rs.getTimestamp("timeRequested")),
                getJavatime(rs.getTimestamp("timeCompleted")),
                rs.getString("status"),
                rs.getString("supportType")));
      }
      query =
          "SELECT * from request, medicineRequests WHERE request.requestID = medicineRequests.requestID";
      stmt = con.prepareStatement(query);
      rs = stmt.executeQuery();
      while (rs.next()) {
        requests.add(
            new MedicineRequest(
                rs.getInt("requestID"),
                rs.getInt("assigned_eID"),
                rs.getString("reqNotes"),
                rs.getString("compNotes"),
                rs.getString("nodeID"),
                getJavatime(rs.getTimestamp("timeRequested")),
                getJavatime(rs.getTimestamp("timeCompleted")),
                rs.getString("status"),
                rs.getString("medicineName"),
                rs.getDouble("dosage"),
                rs.getString("units"),
                rs.getString("patient")));
      }
      query =
          "SELECT * FROM request, sanitationRequests WHERE request.requestID = sanitationRequests.requestID";
      stmt = con.prepareStatement(query);
      rs = stmt.executeQuery();
      while (rs.next()) {
        requests.add(
            new SanitationRequest(
                rs.getInt("requestID"),
                rs.getInt("assigned_eID"),
                rs.getString("reqNotes"),
                rs.getString("compNotes"),
                rs.getString("nodeID"),
                getJavatime(rs.getTimestamp("timeRequested")),
                getJavatime(rs.getTimestamp("timeCompleted")),
                rs.getString("status"),
                rs.getString("sanitationType"),
                rs.getString("size"),
                rs.getString("danger")));
      }
      query = "SELECT * from request, wrequest WHERE request.requestID = wrequest.requestID";
      stmt = con.prepareStatement(query);
      rs = stmt.executeQuery();
      while (rs.next()) {
        requests.add(
            new WheelchairRequest(
                rs.getInt("requestID"),
                rs.getInt("assigned_eID"),
                rs.getString("reqNotes"),
                rs.getString("compNotes"),
                rs.getString("nodeID"),
                getJavatime(rs.getTimestamp("timeRequested")),
                getJavatime(rs.getTimestamp("timeCompleted")),
                rs.getString("status"),
                rs.getString("needsAssistance")));
      }
      query = "SELECT * from request, ITrequest WHERE request.requestID = ITrequest.requestID";
      stmt = con.prepareStatement(query);
      rs = stmt.executeQuery();
      while (rs.next()) {
        requests.add(
            new ITRequest(
                rs.getInt("requestID"),
                rs.getInt("assigned_eID"),
                rs.getString("reqNotes"),
                rs.getString("compNotes"),
                rs.getString("nodeID"),
                getJavatime(rs.getTimestamp("timeRequested")),
                getJavatime(rs.getTimestamp("timeCompleted")),
                rs.getString("status"),
                rs.getString("device"),
                rs.getString("problem")));
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
  // TODO: Add your service request here
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
      query =
          "SELECT * FROM request, wrequest WHERE request.requestID = wrequest.requestID AND status = 'OPEN'";
      stmt = con.prepareStatement(query);
      rs = stmt.executeQuery();
      while (rs.next()) {
        openList.add(
            new WheelchairRequest(
                rs.getInt("requestID"),
                rs.getInt("assigned_eID"),
                rs.getString("reqNotes"),
                rs.getString("compNotes"),
                rs.getString("nodeID"),
                getJavatime(rs.getTimestamp("timeRequested")),
                getJavatime(rs.getTimestamp("timeCompleted")),
                rs.getString("status"),
                rs.getString("needsAssistance")));
      }
      query =
          "SELECT * FROM request, erequest WHERE request.requestID = erequest.requestID AND status = 'OPEN'";
      stmt = con.prepareStatement(query);
      rs = stmt.executeQuery();
      while (rs.next()) {
        openList.add(
            new EmotionalRequest(
                rs.getInt("requestID"),
                rs.getInt("assigned_eID"),
                rs.getString("reqNotes"),
                rs.getString("compNotes"),
                rs.getString("nodeID"),
                getJavatime(rs.getTimestamp("timeRequested")),
                getJavatime(rs.getTimestamp("timeCompleted")),
                rs.getString("status"),
                rs.getString("supportType")));
      }
      query =
          "SELECT * from request, ITrequest WHERE request.requestID = ITrequest.requestID AND status = 'OPEN'";
      stmt = con.prepareStatement(query);
      rs = stmt.executeQuery();
      while (rs.next()) {
        openList.add(
            new ITRequest(
                rs.getInt("requestID"),
                rs.getInt("assigned_eID"),
                rs.getString("reqNotes"),
                rs.getString("compNotes"),
                rs.getString("nodeID"),
                getJavatime(rs.getTimestamp("timeRequested")),
                getJavatime(rs.getTimestamp("timeCompleted")),
                rs.getString("status"),
                rs.getString("device"),
                rs.getString("problem")));
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
  // TODO: GetEmployeeTypes (something which gets all the employees of your particular type)

  /**
   * Gets all the emotional supporters in the database
   *
   * @return a linked list of all people who can do emotional support in the database
   */
  public static LinkedList<EmotionalSupporter> getEmotionalSupporters() throws DBException {
    try {
      String query =
          "SELECT l_employeeID from employees, emotionalSupporter where employeeID = l_employeeID";
      PreparedStatement stmt = con.prepareStatement(query);
      ResultSet rs = stmt.executeQuery();
      LinkedList<EmotionalSupporter> emotionalSupporters = new LinkedList<EmotionalSupporter>();
      while (rs.next()) {
        emotionalSupporters.add((EmotionalSupporter) getEmployee(rs.getInt("l_employeeID")));
      }
      return emotionalSupporters;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getEmotionalSupporters", e);
    }
  }

  /**
   * Gets all the wheelchair employees in the database
   *
   * @return LinkedList<WheelchairEmployee>, all wheelchair employees
   * @throws DBException
   */
  public static LinkedList<WheelchairEmployee> getWheelchairEmployees() throws DBException {
    try {
      String query =
          "SELECT w_employeeID from employees, wheelchairEmployee where employeeID = w_employeeID";
      PreparedStatement stmt = con.prepareStatement(query);
      ResultSet rs = stmt.executeQuery();
      LinkedList<WheelchairEmployee> wheelchairEmployees = new LinkedList<WheelchairEmployee>();
      while (rs.next()) {
        wheelchairEmployees.add((WheelchairEmployee) getEmployee(rs.getInt("w_employeeID")));
      }
      return wheelchairEmployees;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getWheelchairEmployees", e);
    }
  }
  /**
   * Gets all the IT employees in the database
   *
   * @return a linked list of all people who can do IT in the database
   */
  public static LinkedList<IT> getITs() throws DBException {
    try {
      String query = "SELECT IT_employeeID from employees, IT where employeeID = IT_employeeID";
      PreparedStatement stmt = con.prepareStatement(query);
      ResultSet rs = stmt.executeQuery();
      LinkedList<IT> ITs = new LinkedList<>();
      while (rs.next()) {
        ITs.add((IT) getEmployee(rs.getInt("IT_employeeID")));
      }
      return ITs;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getITs", e);
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
      }
      return translators;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getTransLang, lang :" + lang, e);
    }
  }

  // Nick
  /**
   * Adds a translator to the database
   *
   * @param name the translator's name
   * @param languages the languages that this translator is capable of speaking
   * @return id of created translator
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
      rs.next();
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
   * adds a sanitation employee with the specified name
   *
   * @param name
   * @return the generated id
   */
  public static int addSanitationEmp(String name) throws DBException {
    try {
      String query = "INSERT INTO employees (name, serviceType) VALUES (?, 'Sanitation')";
      PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, name);
      stmt.executeUpdate();
      ResultSet rs = stmt.getGeneratedKeys();
      rs.next();
      query = "INSERT INTO sanitation VALUES (?)";
      stmt = con.prepareStatement(query);
      int id = rs.getInt(1);
      stmt.setInt(1, id);
      stmt.executeUpdate();
      return id;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown Error: addSanitationEmp");
    }
  }

  // TODO: Add a function to add your employee type to the database

  /**
   * Adds a Emotional Supporter employee to the database
   *
   * @param name the Emotional supporter employee's name
   * @return id of created request
   */
  public static int addEmotionalSupporter(String name) throws DBException {
    try {
      String query = "INSERT INTO employees (name, serviceType) VALUES (?, 'Emotional Support')";
      PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, name);
      stmt.executeUpdate();
      ResultSet rs = stmt.getGeneratedKeys();
      rs.next(); // NullPointerException
      query = "INSERT INTO emotionalSupporter VALUES (?)";
      stmt = con.prepareStatement(query);
      int id = rs.getInt("1");
      stmt.setInt(1, id);
      stmt.executeUpdate();
      return id;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: addEmotionalSupporter , name = " + name, e);
    }
  }

  /**
   * adds a wheelchair employee to the database
   *
   * @param name, the wheelchair employee's name
   * @return int, employeeID of the newly added employee
   * @throws DBException
   */
  public static int addWheelchairEmployee(String name) throws DBException {
    try {
      String query = "INSERT INTO employees (name, serviceType) VALUES (?, 'Wheelchair')";
      PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, name);
      stmt.executeUpdate();
      ResultSet rs = stmt.getGeneratedKeys();
      rs.next(); // NullPointerException
      query = "INSERT INTO WheelchairEmployee VALUES (?)";
      stmt = con.prepareStatement(query);
      int id = rs.getInt("1");
      stmt.setInt(1, id);
      stmt.executeUpdate();
      return id;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: addWheelchairEmployee , name = " + name, e);
    }
  }
  /**
   * Adds a IT employee to the database
   *
   * @param name the IT employee's name
   * @return id of created request
   */
  public static int addIT(String name) throws DBException {
    try {
      String query = "INSERT INTO employees (name, serviceType) VALUES (?, 'IT')";
      PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, name);
      stmt.executeUpdate();
      ResultSet rs = stmt.getGeneratedKeys();
      rs.next(); // NullPointerException
      query = "INSERT INTO IT VALUES (?)";
      stmt = con.prepareStatement(query);
      int id = rs.getInt("1");
      stmt.setInt(1, id);
      stmt.executeUpdate();
      return id;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: addIT , name = " + name, e);
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

  /**
   * Adds a request for a medicine
   *
   * @param reqNotes
   * @param nodeID
   * @param type
   * @param dosage
   * @param units
   * @param patient
   * @return the id of the created request
   * @throws DBException
   */
  public static int addMedReq(
      String reqNotes, String nodeID, String type, double dosage, String units, String patient)
      throws DBException {
    try {
      String query =
          "INSERT INTO request (timeRequested, reqNotes, serviceType, nodeID, status) VALUES (?, ?, ?, ?, ?)";
      PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      stmt.setTimestamp(1, new Timestamp((new Date().getTime())));
      stmt.setString(2, reqNotes);
      stmt.setString(3, "Medicine");
      stmt.setString(4, nodeID);
      stmt.setString(5, "OPEN");
      stmt.executeUpdate();
      ResultSet rs = stmt.getGeneratedKeys();
      rs.next();
      query =
          "INSERT INTO medicineRequests (requestID, medicineName, dosage, units, patient) VALUES (?, ?, ?, ?, ?)";
      stmt = con.prepareStatement(query);
      int id = rs.getInt("1");
      stmt.setInt(1, id);
      stmt.setString(2, type);
      stmt.setDouble(3, dosage);
      stmt.setString(4, units);
      stmt.setString(5, patient);
      stmt.executeUpdate();
      return id;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: addMedReq", e);
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

  // TODO: Create your addRequest call here

  /**
   * Adds a request for emotional support
   *
   * @param reqNotes some notes for the emotional support request
   * @param nodeID The ID of the node in which these services are requested
   * @param supportType the type of support the emotional supporter is requested for
   * @return the id of the created request
   */
  public static int addEmotSuppReq(String reqNotes, String nodeID, String supportType)
      throws DBException {
    try {
      String query =
          "INSERT INTO request (timeRequested, reqNotes, serviceType, nodeID, status) VALUES (?, ?, ?, ?, ?)";
      PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
      stmt.setString(2, reqNotes);
      stmt.setString(3, "Emotional Support");
      stmt.setString(4, nodeID);
      stmt.setString(5, "OPEN");
      stmt.execute();
      ResultSet rs = stmt.getGeneratedKeys();
      rs.next();
      query = "INSERT INTO erequest (requestID, supportType) VALUES (?, ?)";
      stmt = con.prepareStatement(query);
      int id = rs.getInt("1");
      stmt.setInt(1, id);
      stmt.setString(2, supportType);
      stmt.executeUpdate();
      return id;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Error: addEmotSuppReq", e);
    }
  }

  /**
   * creates a new wheelchair request
   *
   * @param reqNotes, notes for the wheelchair request
   * @param nodeID, String, location of wheelchair request
   * @param needsAssistance, String, whether the wheelchair requester requires assistance ("yes" or
   *     "no")
   * @return int, the requestID of the new request
   * @throws DBException
   */
  public static int addWheelchairRequest(String reqNotes, String nodeID, String needsAssistance)
      throws DBException {
    try {
      String query =
          "INSERT INTO request (timeRequested, reqNotes, serviceType, nodeID, status) VALUES (?, ?, ?, ?, ?)";
      PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
      stmt.setString(2, reqNotes);
      stmt.setString(3, "Wheelchair");
      stmt.setString(4, nodeID);
      stmt.setString(5, "OPEN");
      stmt.execute();
      ResultSet rs = stmt.getGeneratedKeys();
      rs.next();
      query = "INSERT INTO wrequest (requestID, needsAssistance) VALUES (?, ?)";
      stmt = con.prepareStatement(query);
      int id = rs.getInt("1");
      stmt.setInt(1, id);
      stmt.setString(2, needsAssistance);
      stmt.executeUpdate();
      return id;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Error: addWheelchairRequest", e);
    }
  }
  /**
   * Adds a request for IT
   *
   * @param reqNotes some notes for the IT request
   * @param nodeID The ID of the node in which these services are requested
   * @return the id of the created request
   */
  public static int addITReq(String reqNotes, String nodeID, String device, String problem)
      throws DBException {
    try {
      String query =
          "INSERT INTO request (timeRequested, reqNotes, serviceType, nodeID, status) VALUES (?, ?, ?, ?, ?)";
      PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
      stmt.setString(2, reqNotes);
      stmt.setString(3, "IT");
      stmt.setString(4, nodeID);
      stmt.setString(5, "OPEN");
      stmt.execute();
      ResultSet rs = stmt.getGeneratedKeys();
      rs.next();
      query = "INSERT INTO ITrequest (requestID, device, problem) VALUES (?, ?, ?)";
      stmt = con.prepareStatement(query);
      int id = rs.getInt("1");
      stmt.setInt(1, id);
      stmt.setString(2, device);
      stmt.setString(3, problem);
      stmt.executeUpdate();
      return id;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Error: addITReq", e);
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
    Request req = getRequest(requestID);
    try {
      if (req instanceof MedicineRequest) {
        Doctor doc = (Doctor) req.getEmp_assigned();
        try {
          if (!(LoginDB.currentLogin().equals(doc.getUsername()))) {
            throw new DBException(
                "Error: You muse login as the Doctor "
                    + doc.getName()
                    + " with username "
                    + doc.getUsername());
          }
        } catch (DBException e) {
          throw new DBException("Error: No login");
        }
      }
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

  // TODO: make functions for changing the attributes of your employees

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

  // Chris

  /**
   * gets a list of patients taking the specified medicine
   *
   * @param type
   * @return list of patients
   */
  public static LinkedList<String> getPatientByMedType(String type) throws DBException {
    try {
      String query = "SELECT patient FROM medicineRequests WHERE UPPER(medicineName) = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, type.toUpperCase());
      ResultSet rs = stmt.executeQuery();
      LinkedList<String> plist = new LinkedList<>();
      while (rs.next()) {
        plist.add(rs.getString("patient"));
      }
      return plist;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Error: getpatientbyMedType");
    }
  }

  // Nick

  /**
   * Gets a list of requests associated with the specified patient
   *
   * @param patient The specified patient
   * @return a LinkedList of MedicineRequest
   */
  public static LinkedList<MedicineRequest> getMedRequestByPatient(String patient)
      throws DBException {
    try {
      LinkedList<MedicineRequest> res = new LinkedList<>();

      String query =
          "SELECT * FROM medicineRequests "
              + "JOIN request ON medicineRequests.requestID = request.requestID "
              + "WHERE patient = ?";
      PreparedStatement st = con.prepareStatement(query);
      st.setString(1, patient);
      ResultSet rs = st.executeQuery();

      while (rs.next()) {
        res.add(
            new MedicineRequest(
                rs.getInt("requestID"),
                rs.getInt("assigned_eID"),
                rs.getString("reqNotes"),
                rs.getString("compNotes"),
                rs.getString("nodeID"),
                getJavatime(rs.getTimestamp("timeRequested")),
                getJavatime(rs.getTimestamp("timeCompleted")),
                rs.getString("status"),
                rs.getString("medicineName"),
                rs.getDouble("dosage"),
                rs.getString("units"),
                rs.getString("patient")));
      }

      return res;
    } catch (SQLException e) {
      throw new DBException("Unknown error: getMedRequest", e);
    }
  }

  // Chris
  /**
   * gets a list of sanitationRequest where the amount matches the given val
   *
   * @param amount
   * @return a list of sanitationRequest where amount matches the given amount
   */
  public static LinkedList<SanitationRequest> getsanitationbyAmount(String amount)
      throws DBException {
    LinkedList<SanitationRequest> list = new LinkedList<SanitationRequest>();
    try {
      String query =
          "SELECT * FROM sanitationRequests, request WHERE sanitationRequests.requestID = request.requestID AND Upper(size) = ?";
      PreparedStatement st = con.prepareStatement(query);
      st.setString(1, amount.toUpperCase());
      ResultSet rs = st.executeQuery();
      while (rs.next()) {
        list.add(
            new SanitationRequest(
                rs.getInt("requestID"),
                rs.getInt("assigned_eID"),
                rs.getString("reqNotes"),
                rs.getString("compNotes"),
                rs.getString("nodeID"),
                getJavatime(rs.getTimestamp("timeRequested")),
                getJavatime(rs.getTimestamp("timeCompleted")),
                rs.getString("status"),
                rs.getString("sanitationType"),
                rs.getString("size"),
                rs.getString("danger")));
      }
      return list;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown Error: getSanitationAmount");
    }
  }

  //  // Nick
  //  /**
  //   * Adds a patient to the database
  //   *
  //   * @param name The name of the patient
  //   * @param location The nodeID of the location of the patient
  //   * @return id of created patient
  //   */
  //  public static int addPatient(String name, String location) throws DBException {
  //    try {
  //      String query = "INSERT INTO patients (patientName, location) VALUES (?, ?)";
  //      PreparedStatement st = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
  //      st.setString(1, name);
  //      st.setString(2, location);
  //      st.executeUpdate();
  //      ResultSet rs = st.getGeneratedKeys();
  //      rs.next();
  //      int id = rs.getInt("1");
  //      return id;
  //    } catch (SQLException e) {
  //      throw new DBException("Unknown error: addPatient", e);
  //    }
  //  }
  //
  //  // Chris
  //  /**
  //   * gets list of all patients
  //   * @return lst of all patients
  //   */
  //  public static LinkedList<Patient> getlistPatient() throws DBException {
  //    LinkedList<Patient> allPatients = new LinkedList<Patient>();
  //
  //    try{
  //      String query = "SELECT * FROM patients";
  //      PreparedStatement stmt = con.prepareStatement(query);
  //      ResultSet rs = stmt.executeQuery();
  //      while(rs.next()){
  //        allPatients.add(
  //                new Patient(
  //                        rs.getInt("id"),
  //                        rs.getString("name"),
  //                        rs.getString("location")
  //                )
  //        );
  //      }
  //      return allPatients;
  //    } catch (SQLException e) {
  //      e.printStackTrace();
  //      throw new DBException("Error: getlistPatients");
  //    }
  //  }

  // Nick
  /**
   * Gets the patient specified by the given ID
   *
   * @param patientID the ID of the patient
   * @return The Patient object containing information about the patient
   */
  //  public static Patient getPatient(int patientID) throws DBException {
  //    try {
  //      String query = "SELECT * FROM patients WHERE patientID = ?";
  //      PreparedStatement st = con.prepareStatement(query);
  //      st.setInt(1, patientID);
  //      ResultSet rs = st.executeQuery();
  //      if (rs.next()) {
  //        return new Patient(
  //            rs.getInt("patientID"), rs.getString("patientName"), rs.getString("location"));
  //      } else {
  //        throw new DBException("getPatient: Could not find patient with id " + patientID);
  //      }
  //    } catch (SQLException e) {
  //      throw new DBException("Unknown error: getPatient", e);
  //    }
  //  }

  //// Chris
  //  /**
  //   * gets a list of patient with the specified name
  //   * @param id
  //   * @return list of patients
  //   */
  //  public static LinkedList<String> searchbyPatient(int id) throws DBException {
  //    try{
  //      LinkedList<String> patients = new LinkedList<>();
  //      String query = "SELECT * FROM patients WHERE id = ?";
  //      PreparedStatement stmt = con.prepareStatement(query);
  //      stmt.setInt(1, id);
  //      ResultSet rs = stmt.executeQuery();
  //      while(rs.next()){
  //        patients.add(
  //                new Patient(
  //                        rs.getInt("id"),
  //                        rs.getString("name"),
  //                        rs.getString("location")
  //                )
  //        );
  //      }
  //      return patients;
  //    } catch (SQLException e) {
  //      e.printStackTrace();
  //      throw new DBException("Error: searchbyPatient causing error");
  //    }
  //  }

  // Nick

  /**
   * Searches for a medicine name
   *
   * @param searchQuery The search query
   * @return LinkedList of medicine name (String) that matches query
   */
  public static LinkedList<String> searchByMedType(String searchQuery) throws DBException {
    try {
      LinkedList<String> res = new LinkedList<>();
      String query = "SELECT medicineName FROM medicineRequests WHERE UPPER(medicineName) LIKE ?";
      PreparedStatement st = con.prepareStatement(query);
      st.setString(1, "%" + searchQuery.toUpperCase() + "%");
      ResultSet rs = st.executeQuery();
      while (rs.next()) {
        res.add(rs.getString("medicineName"));
      }
      return res;
    } catch (SQLException e) {
      throw new DBException("Unknown error : searchByMedType", e);
    }
  }

  // Chris
  /**
   * searches through the database where the spillType contains the given type
   *
   * @param type
   * @return a list of sanitationRequest where spillType contains the given type
   */
  public static LinkedList<SanitationRequest> searchbyspillType(String type) throws DBException {
    try {
      LinkedList<SanitationRequest> list = new LinkedList<SanitationRequest>();
      String query = "SELECT * FROM sanitationRequests WHERE UPPER(sanitationType) LIKE ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, "%" + type.toUpperCase() + "%");
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        list.add((SanitationRequest) getRequest(rs.getInt("requestID")));
      }
      return list;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: searchbyspillType");
    }
  }

  // Nick
  /**
   * adds a sanitation request with the specified fields
   *
   * @param reqNotes
   * @param nodeID
   * @param spillType
   * @param size
   * @param danger
   * @return the generated requestID
   */
  public static int addSanitationReq(
      String reqNotes, String nodeID, String spillType, String size, String danger)
      throws DBException {
    size = size.toLowerCase();
    danger = danger.toLowerCase();

    String[] sizeArray = new String[] {"small", "medium", "large", "unknown"};
    String[] dangerArray = new String[] {"low", "medium", "high", "unknown"};

    if (!Arrays.asList(sizeArray).contains(size)) {
      throw new DBException("addSanitationReq: \"" + size + "\" is not a valid size");
    }

    if (!Arrays.asList(dangerArray).contains(danger)) {
      throw new DBException("addSanitationReq: \"" + danger + "\" is not a valid danger level");
    }

    try {
      con.setAutoCommit(false);

      String query =
          "INSERT INTO request (timeRequested, reqNotes, serviceType, nodeID, status) VALUES (?, ?, ?, ?, ?)";
      PreparedStatement st = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      st.setTimestamp(1, new Timestamp(new Date().getTime()));
      st.setString(2, reqNotes);
      st.setString(3, "Sanitation");
      st.setString(4, nodeID);
      st.setString(5, "OPEN");
      st.executeUpdate();

      ResultSet rs = st.getGeneratedKeys();
      rs.next();
      int id = rs.getInt("1");

      query =
          "INSERT INTO sanitationRequests (requestid, size, sanitationtype, danger) VALUES (?, ?, ?, ?)";
      st = con.prepareStatement(query);
      st.setInt(1, id);
      st.setString(2, size);
      st.setString(3, spillType);
      st.setString(4, danger);
      st.executeUpdate();

      con.commit();
      con.setAutoCommit(true);
      return id;
    } catch (SQLException e) {
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Unknown Error: addSanitationReq", ex);
      }
      throw new DBException("Unknown Error: addSanitationReq", e);
    }
  }
  // Nick
  /**
   * gets a list of sanitationRequest where the danger matches the given value (case insensitive)
   *
   * @param danger
   * @return a list of sanitationRequest where danger matches the given danger level
   */
  public static LinkedList<SanitationRequest> getSanitationByDanger(String danger)
      throws DBException {
    try {
      LinkedList<SanitationRequest> result = new LinkedList<>();

      danger = danger.toLowerCase();

      String query =
          "SELECT * FROM sanitationRequests "
              + "JOIN request ON sanitationRequests.requestID = request.requestID "
              + "WHERE LOWER(danger) = ?";

      PreparedStatement st = con.prepareStatement(query);
      st.setString(1, danger);
      ResultSet rs = st.executeQuery();

      while (rs.next()) {
        result.add(
            new SanitationRequest(
                rs.getInt("requestID"),
                rs.getInt("assigned_eID"),
                rs.getString("reqNotes"),
                rs.getString("compNotes"),
                rs.getString("nodeID"),
                getJavatime(rs.getTimestamp("timeRequested")),
                getJavatime(rs.getTimestamp("timeCompleted")),
                rs.getString("status"),
                rs.getString("sanitationType"),
                rs.getString("size"),
                rs.getString("danger")));
      }
      return result;
    } catch (SQLException e) {
      throw new DBException("Unknown error: getSanitationByDanger", e);
    }
  }

  // Nick
  /**
   * gets a list of all sanitation Employee in the database
   *
   * @return a LinkedList of Sanitation
   */
  public static LinkedList<Sanitation> getSanitationEmp() throws DBException {
    try {
      LinkedList<Sanitation> result = new LinkedList<>();
      String query = "SELECT * FROM employees WHERE SERVICETYPE = 'Sanitation'";

      PreparedStatement st = con.prepareStatement(query);
      ResultSet rs = st.executeQuery();

      while (rs.next()) {
        result.add(new Sanitation(rs.getInt("employeeID"), rs.getString("name")));
      }
      return result;
    } catch (SQLException e) {
      throw new DBException("Unknown error: getSanitationEmp", e);
    }
  }
}
