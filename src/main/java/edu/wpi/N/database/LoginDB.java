package edu.wpi.N.database;

import java.sql.*;

public class LoginDB {
  private static Connection con = MapDB.getCon();
  private static String currentUser;

  /**
   * Creates a new login for the specified username and password
   *
   * @param username The username for the new user
   * @param password The password for the new user
   * @throws DBException On error
   */
  public static void createLogin(String username, String password) throws DBException {
    String query = "INSERT INTO credential (username, password) VALUES (?, ?)";
    try{
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, username);
      stmt.setString(2, password);
      stmt.executeUpdate();
    } catch(SQLException e){
      if(e.getSQLState().equals("23505")){
        throw new DBException("That username already exists!");
      }
      else{
        e.printStackTrace();
        throw new DBException("Unknown error: createLogin", e);
      }
    }
  }

  /**
   * Removes the login of the specified user from the database
   * @param username The username of the user you want to remove from the database
   * @throws DBException on error
   */
  public static void removeLogin(String username) throws DBException {
    String query = "DELETE FROM credential WHERE username = ?";
    try{
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, username);
      if(stmt.executeUpdate() <= 0) throw new DBException("That user doesn't exist!");
    } catch(SQLException e){
        e.printStackTrace();
        throw new DBException("Unknown error: deleteLogin", e);
    }
  }

  public static void changePass(String username, String oldpass, String newpass) throws DBException {
    verify(username, oldpass);
    String query = "UPDATE credential SET password = ? WHERE username = ? AND password = ?";
    try{
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, newpass);
      stmt.setString(2, username);
      stmt.setString(3, oldpass);
      if(stmt.executeUpdate() <= 0) throw new DBException("That user doesn't exist!");
    } catch(SQLException e){
      e.printStackTrace();
      throw new DBException("Unknown error: deleteLogin", e);
    }
  }

  public static void verifyLogin(String username, String password) throws DBException {}

  private static void verify(String username, String password) throws DBException{

  }

  public static void logout() throws DBException {}

  public static String currentLogin() throws DBException {
    return currentUser;
  }
}
