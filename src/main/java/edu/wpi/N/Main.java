package edu.wpi.N;

import edu.wpi.N.database.*;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) throws SQLException, DBException, ClassNotFoundException {
    MapDB.initDB();
    App.launch(App.class, args);
  }
}
