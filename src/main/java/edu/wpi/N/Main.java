package edu.wpi.N;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) throws SQLException, DBException, ClassNotFoundException {
    DbController.initDB();
    App.launch(App.class, args);
  }
}
