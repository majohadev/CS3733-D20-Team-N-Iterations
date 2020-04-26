package edu.wpi.N;

import edu.wpi.N.database.*;
import java.io.FileNotFoundException;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args)
      throws SQLException, DBException, ClassNotFoundException, FileNotFoundException {
    MapDB.initDB();
    /*final String DEFAULT_NODES = "csv/UPDATEDTeamNnodes.csv";
    final String DEFAULT_PATHS = "csv/UPDATEDTeamNedges.csv";
    final InputStream INPUT_NODES_DEFAULT = Main.class.getResourceAsStream(DEFAULT_NODES);
    final InputStream INPUT_EDGES_DEFAULT = Main.class.getResourceAsStream(DEFAULT_PATHS);
    CSVParser.parseCSV(INPUT_NODES_DEFAULT);
    CSVParser.parseCSV(INPUT_EDGES_DEFAULT);*/
    App.launch(App.class, args);
  }
}
