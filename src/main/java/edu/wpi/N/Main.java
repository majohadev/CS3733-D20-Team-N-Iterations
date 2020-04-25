package edu.wpi.N;

import edu.wpi.N.database.*;
import java.io.FileNotFoundException;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args)
      throws SQLException, DBException, ClassNotFoundException, FileNotFoundException {
    MapDB.initDB();
    App.launch(App.class, args);
  }
}
