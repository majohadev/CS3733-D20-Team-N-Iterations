package edu.wpi.N;

import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import java.io.InputStream;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) throws SQLException, DBException, ClassNotFoundException {
    DbController.initDB();
    InputStream nodes = Main.class.getResourceAsStream("csv/TeamNFloor4Nodes.csv");
    InputStream edges = Main.class.getResourceAsStream("csv/TeamNFloor4Edges.csv");
    CSVParser.parseCSV(nodes);
    CSVParser.parseCSV(edges);
    App.launch(App.class, args);
  }
}
