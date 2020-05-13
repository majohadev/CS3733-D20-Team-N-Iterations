package edu.wpi.N;

import edu.wpi.N.database.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

public class MainClass {

  public static void main(String[] args)
      throws SQLException, DBException, ClassNotFoundException, IOException {
    MapDB.initDB();

    // Get the logger for "org.jnativehook" and set the level to warning.
    Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    logger.setLevel(Level.WARNING);

    // Don't forget to disable the parent handlers.
    logger.setUseParentHandlers(false);

    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException ex) {
      System.err.println("There was a problem registering the native hook.");
      System.err.println(ex.getMessage());

      System.exit(1);
    }

    // ArduinoController periperal = new ArduinoController();
    // periperal.initialize();
    // MapDB.setKiosk("NSERV00301", 180);

    /*final String DEFAULT///_NODES = "csv/UPDATEDTeamNnodes.csv";
    final String DEFAULT_PATHS = "csv/UPDATEDTeamNedges.csv";
    final InputStream INPUT_NODES_DEFAULT = Main.class.getResourceAsStream(DEFAULT_NODES);
    final InputStream INPUT_EDGES_DEFAULT = Main.class.getResourceAsStream(DEFAULT_PATHS);
    CSVParser.parseCSV(INPUT_NODES_DEFAULT);
    CSVParser.parseCSV(INPUT_EDGES_DEFAULT);*/

    AppClass.launch(AppClass.class, args);
  }
}
