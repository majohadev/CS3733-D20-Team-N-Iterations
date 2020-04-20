package edu.wpi.N.database;

import edu.wpi.N.entities.*;
import java.sql.*;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;

public class EmployeeController {
  private static Connection con = DbController.getCon();

  public static void initEmployee() throws DBException {
    try{
      String query = "CREATE TABLE service (" +
              "serviceType VARCHAR(255) NOT NULL PRIMARY KEY," +
              "timeStart CHAR(5)," +
              "timeEnd CHAR(5)," +
              "description VARCHAR(255))";
      PreparedStatement state = con.prepareStatement(query);
      state.execute();
      query = "INSERT INTO service VALUES ('Translator', '00:00', '00:00', 'Make a request for our translation services!')";

      query = "INSERT INTO service VALUES ('Translator', '00:00', '00:00', 'Make a request for our translation services!')";
    } catch (SQLException e){
      if (!e.getSQLState().equals("X0Y32")) {
        e.printStackTrace();
        throw new DBException("Unknown error: initEmployee", e);
      }
    }
    try {
      String query =
          "CREATE TABLE employees ("
              + "employeeID INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
              + "name VARCHAR(255) NOT NULL," +
                  "serviceType VARCHAR(255) NOT NULL," +
                  "FOREIGN KEY (serviceType) REFERENCES service (serviceType))";

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
      query = "CREATE TABLE language (" +
              "t_employeeID INT NOT NULL, " +
              "language VARCHAR(255) NOT NULL, +" +
              "CONSTRAINT LANG_PK PRIMARY KEY (t_employeeID, language)," +
              "FOREIGN KEY (t_employeeID) REFERENCES translator (t_employeeID) ON DELETE CASCADE)";
      state = con.prepareStatement(query);
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
    try {
      String query =
              "CREATE TABLE request("
                      + "requestID INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                      "timeRequested TIMESTAMP NOT NULL," +
                      "timeCompleted TIMESTAMP," +
                      "notes VARCHAR(255)," +
                      "assigned_eID INT REFERENCES employees(employeeID)," +
                      "serviceType VARCHAR(255) NOT NULL REFERENCES service(serviceType)," +
                      "nodeID CHAR(10) NOT NULL REFERENCES nodes(nodeID)," +
                      "status CHAR(4) NOT NULL CONSTRAINT STAT_CK CHECK (status IN ('OPEN', 'DENY', 'DONE')))";
      PreparedStatement state = con.prepareStatement(query);
      state.execute();
      query = "CREATE TABLE lrequest(" +
              "requestID INT NOT NULL PRIMARY KEY REFERENCES request(requestID))";
      state = con.prepareStatement(query);
      state.execute();
      query = "CREATE TABLE trequest(" +
              "requestID INT NOT NULL PRIMARY KEY REFERENCES request(requestID)," +
              "language VARCHAR(255) NOT NULL)";
      state = con.prepareStatement(query);
      state.execute();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: intiEmployee creating Request table", e);
    }

  }

  //Noah
  /**
   * Returns the employee specified by the given ID
   * @param id The employee's ID
   * @return an employee entity representing that employee
   */
  public static Employee getEmployee(int id){
    return null;
  }

  //Chris
  /**
   * Returns a list of all employees in the database
   * @return a linked list of all employees in the database
   */
  public static LinkedList<Employee> getEmployees() throws DBException {
      LinkedList<Employee> allEmployee = new LinkedList<Employee>();
      allEmployee.addAll(getTranslators());
      allEmployee.addAll(getLaundrys());
      return allEmployee;

  }

  //Nick
  /**
   * Gets all services in the database
   * @return a linked list of all services in the database
   */
  public static LinkedList<Service> getServices(){
    return null;
  }

  //Noah
  /**
   * Gets all the requests in the database
   * @return a linked list of all service requests in the database
   */
  public static LinkedList<Request> getRequests(){
    return null;
  }

  //Chris
  /**
   * Gets all the open requests (not completed requests) in the database
   * @return a linked list of all open service requests in the database
   */
  public static LinkedList<Request> getOpenRequests() throws DBException {
    try {
        String query = "SELECT * FROM request, trequest WHERE requestID = requestID AND status='OPEN'";
        PreparedStatement stmt = con.prepareStatement(query);
        LinkedList<Request> openList = new LinkedList<Request>();

        ResultSet rs = stmt.executeQuery();
        while(rs.next()) {
          openList.add(
                  new TranslatorRequest(
                          rs.getInt("requestID"),
                          rs.getInt("emp_assigned"),
                          rs.getString("notes"),
                          rs.getString("nodeID"),
                          rs.getString("serviceType"),
                          getJavatime(rs.getTimestamp("timeRequested")),
                          getJavatime(rs.getTimestamp("timeCompleted")),
                          rs.getString("status"),
                          rs.getString("language")));


        }
        query = "SELECT * FROM request, lrequest WHERE requestID = requestID AND status = 'OPEN'";
        stmt = con.prepareStatement(query);
        rs = stmt.executeQuery();
        while(rs.next()){
          openList.add(
                  new LaundryRequest(
                          rs.getInt("requestID"),
                          rs.getInt("emp_assigned"),
                          rs.getString("notes"),
                          rs.getString("nodeID"),
                          rs.getString("serviceType"),
                          getJavatime(rs.getTimestamp("timeRequested")),
                          getJavatime(rs.getTimestamp("timeCompleted")),
                          rs.getString("status")
                  )
          );
        }
        return openList;
      } catch (SQLException ex) {
      ex.printStackTrace();
      throw new DBException("Error: getOpenRequest", ex);
    }
  }

  //Nick
  /**
   * Gets all the translators in the database
   * @return a linked list of all translators in the database
   */
  public static LinkedList<Translator> getTranslators(){
    return null;
  }

  //Noah
  /**
   * Gets all the laundrys in the database
   * @return a linked list of all people who can do laundry in the database
   */
  public static LinkedList<Laundry> getLaundrys(){
    return null;
  }

  //Chris
  /**
   * Returns a list of all translators who speak a specified langauge
   * @param lang the language that you want the translators to speak
   * @return a linked list of all the translators who speak a specified language
   */
  public static LinkedList<Translator> getTransLang(String lang){
    LinkedList<Translator> list = getTranslators();
    LinkedList<Translator> special = new LinkedList<Translator>();
    for(int i = 1; i<list.size(); i++){
      if(list.get(i).getLanguages().equals(lang))
        special.add(list.get(i));
    }
    return special;
  }

  //Nick
  /**
   * Adds a translator to the database
   * @param name the translator's name
   * @param languages the languages that this translator is capable of speaking
   * @return true if successful, false otherwise
   */
  public static void addTranslator(String name, LinkedList<String> languages) {

  }


  //Noah
  /**
   * Adds a laundry employee to the database
   * @param name the laundry employee's name
   * @return true if successful, false otherwise
   */
  public static void addLaundry(String name) {

  }


  //Chris
  /**
   * Adds a request for a translator
   * @param notes some notes for the translator request
   * @param nodeID The ID of the node in which these services are requested
   * @param language the language that the translator is requested for
   * @return true on success, false otherwise.
   */
  public static void addTransReq(String notes, String nodeID, String language) throws DBException {
    try{
      String query = "INSERT INTO trequest VALUES (?, ?)";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, nodeID);
      stmt.setString(2, language);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Error: addTransReq");
    }

  }

  //Nick
  /**
   * Adds a request for laundry
   * @param notes some notes for the laundry request
   * @param nodeID The ID of the node in which these services are requested
   * @return true on success, false otherwise.
   */
  public static void addLaundReq(String notes, String nodeID){
  }

  //Noah
  /**
   * Assigns an employee to a request; the employee must be able to fulfil that request
   * @param employeeID the ID of the employee to be assigned
   * @param requestID The ID of the request to which they will be assigned.
   * @return
   */
  public static void assignToRequest(int employeeID, int requestID){
  }

  //Chris
  /**
   * Marks a request as completed and done at the time that this function was called
   * @param requestID the ID of the request to be marked as completed
   * @return true on sucess, false otherwise
   */
  public static void completeRequest(int requestID) throws DBException{
    try{
      String query = "UPDATE request SET status = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setInt(1, requestID);
      stmt.execute();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Error: completeRequest", e);
    }
  }

  //Nick
  /**
   * Removes an employee from the database
   * @param employeeID the id of the employee to be excised
   */
  public static void removeEmployee(int employeeID) throws DBException{

  }

  //regEx
  /**
   * Adds a language to the translator with the specified employee ID
   * @param employeeID The ID of the employee who will speak this new language
   * @param language The language to be added
   * @throws DBException if the employee isn't a translator
   */
  public static void addLanguage(int employeeID, String language) throws DBException{

  }

  //Chris
  /**
   * Removes a language to the translator with the specified employee ID
   * @param employeeID The ID of the employee who no longer speaks the given language
   * @param language The language to be removed
   * @throws DBException if the employee isn't a translator
   */
  public static void removeLanguage(int employeeID, String language) throws DBException{
    try{
      String query = "DELETE FROM language WHERE employeeID = ? AND language = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setInt(1, employeeID);
      stmt.setString(2, language);
      stmt.executeUpdate();
    }catch(SQLException e){
        throw new DBException("Unknown Error: removeLanguage not working");
    }
  }

  //Nick
  /**
   * Denies a given request
   * @param requestID The request id of an open request to deny
   * @throws DBException on unsuccess
   */
  public static void denyRequest(int requestID) throws DBException{
  }




  public static GregorianCalendar getJavatime(Timestamp time) {
    Calendar cal = GregorianCalendar.getInstance();
    cal.setTime(time);
    return (GregorianCalendar) cal;
  }

  public static Timestamp getSqltime(GregorianCalendar cal) {
    Timestamp time = new Timestamp(cal.getTime().getTime());
    return time;
  }
}
