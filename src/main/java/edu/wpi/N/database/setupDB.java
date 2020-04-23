package edu.wpi.N.database;

import edu.wpi.N.Main;
import java.io.*;
import java.sql.*;
import org.apache.ibatis.jdbc.ScriptRunner;

public class setupDB {
  public static void main(String[] args)
      throws SQLException, ClassNotFoundException, DBException, FileNotFoundException {

    String query;
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    String URL;
    URL = "jdbc:derby:MapDB;create=true";
    Connection con = DriverManager.getConnection(URL);
    ScriptRunner sr = new ScriptRunner(con);
    Reader reader =
        new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("sql/setup.sql")));
    sr.runScript(reader);
  }
}
