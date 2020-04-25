package edu.wpi.N.database;

import java.sql.*;

public class LoginDB {
  private static Connection con = MapDB.getCon();
  private static String currentUser;
  private static String currentAccess;

  /**
   * Creates a new login for the specified username and password
   *
   * @param username The username for the new user
   * @param password The password for the new user
   * @throws DBException On error or when the user already exists
   */
  private static void createLogin(String username, String password, String access)
      throws DBException {
    String query = "INSERT INTO credential (username, password, access) VALUES (?, ?, ?)";
    try {
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, username);
      stmt.setString(2, password);
      stmt.setString(3, access);
      stmt.executeUpdate();
    } catch (SQLException e) {
      if (e.getSQLState()
          .equals("23505")) { // statement was aborted due to duplicate key in unique/primary key
        throw new DBException("That username already exists!");
      } else {
        e.printStackTrace();
        throw new DBException("Unknown error: createLogin", e);
      }
    }
  }

  /**
   * Creates a new login with admin access
   *
   * @param username the new username
   * @param password the new password
   * @throws DBException On error or when the user already exists
   */
  public static void createAdminLogin(String username, String password) throws DBException {
    createLogin(username, password, "ADMIN");
  }

  /**
   * Creates a new login with doctor access Shouldn't ever call directly, this is called when you
   * call addDoctor
   *
   * @param username the new username
   * @param password the new password
   * @throws DBException On error or when the user already exists
   */
  static void createDoctorLogin(String username, String password) throws DBException {
    createLogin(username, password, "DOCTOR");
  }

  /**
   * Removes the login of the specified user from the database
   *
   * @param username The username of the user you want to remove from the database
   * @throws DBException on error or when the specified user doesn't exist.
   */
  public static void removeLogin(String username) throws DBException {
    if (currentUser != null) logout();
    String query = "DELETE FROM credential WHERE username = ?";
    try {
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, username);
      if (stmt.executeUpdate() <= 0) throw new DBException("That user doesn't exist!");
    } catch (SQLException e) {
      if (e.getSQLState().equals("23503")) {
        throw new DBException(
            "You cannot directly delete logins for doctors! delete the doctor with removeEmployee instead (the login will be deleted automatically).");
      }
      e.printStackTrace();
      throw new DBException("Unknown error: deleteLogin", e);
    }
  }

  /**
   * Changes the password of the specified user
   *
   * @param username The username
   * @param oldpass The user's old password
   * @param newpass The user's new password
   * @throws DBException On error, when the old password isn't valid for the entered username or
   *     when the user doesn't exist
   */
  public static void changePass(String username, String oldpass, String newpass)
      throws DBException {
    verify(username, oldpass);
    String query = "UPDATE credential SET password = ? WHERE username = ? AND password = ?";
    try {
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, newpass);
      stmt.setString(2, username);
      stmt.setString(3, oldpass);
      if (stmt.executeUpdate() <= 0) throw new DBException("That user doesn't exist!");
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: deleteLogin", e);
    }
  }

  /**
   * Verifies the login credentials. Throws DBException when they are invalid; If this does not
   * throw DBException, the credentials are valid. This also sets the current user to these
   * credentials, which can be retrieved through currentLogin().
   *
   * @param username the user's username
   * @param password The user's password
   * @throws DBException When the credentials are invalid (or on error)
   */
  public static void verifyLogin(String username, String password) throws DBException {
    verify(username, password);
    currentUser = username;
    String query = "SELECT access FROM credential WHERE username = ?";
    try {
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, username);
      ResultSet rs = stmt.executeQuery();
      rs.next();
      currentAccess = rs.getString("access");
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: verify login", e);
    }
  }

  /**
   * Verifies the credentials without setting the currentUser; used for internal commands
   *
   * @param username the user's username
   * @param password the user's password
   * @throws DBException When credentials are invalid (or on error)
   */
  private static void verify(String username, String password) throws DBException {
    try {
      String query = "SELECT password FROM credential WHERE username = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, username);
      ResultSet rs = stmt.executeQuery();
      rs.next();
      if (!rs.getString("password").equals(password)) {
        throw new DBException("Invalid password!");
      }
    } catch (SQLException e) {
      if (e.getSQLState().equals("24000")) { // Invalid cursor state - no current row
        throw new DBException("Invalid username!");
      } else {
        e.printStackTrace();
        throw new DBException("Unknown error: verify", e);
      }
    }
  }

  public static void logout() throws DBException {
    if (currentUser == null) throw new DBException("No users are currently logged in!");
    currentUser = null;
    currentAccess = null;
  }

  public static String currentLogin() throws DBException {
    if (currentUser == null) throw new DBException("No users are currently logged in!");
    return currentUser;
  }

  public static String currentAccess() throws DBException {
    if (currentAccess == null) throw new DBException("No users are currently logged in!");
    return currentAccess;
  }
}
