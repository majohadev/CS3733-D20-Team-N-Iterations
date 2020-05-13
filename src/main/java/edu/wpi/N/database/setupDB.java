package edu.wpi.N.database;

import edu.wpi.N.MainClass;
import java.io.*;
import java.sql.*;
import org.apache.ibatis.jdbc.ScriptRunner;

public class setupDB {
  public static void main(String[] args) throws SQLException, ClassNotFoundException, DBException {

    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    String URL;
    URL = "jdbc:derby:MapDB;create=true";
    Connection con = DriverManager.getConnection(URL);
    ScriptRunner sr = new ScriptRunner(con);
    sr.setLogWriter(null);
    Reader reader =
        new BufferedReader(
            new InputStreamReader(
                MainClass.class.getResourceAsStream("sql/drop.sql"))); // drop tables
    sr.runScript(reader);
    reader =
        new BufferedReader(
            new InputStreamReader(
                MainClass.class.getResourceAsStream("sql/setup.sql"))); // create tables
    sr.runScript(reader);
  }
}
