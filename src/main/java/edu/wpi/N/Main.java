package edu.wpi.N;

import edu.wpi.N.database.*;
import edu.wpi.N.entities.memento.GlobalMouseListenerExample;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

public class Main {

  public static void main(String[] args)
      throws SQLException, DBException, ClassNotFoundException, IOException {
    MapDB.initDB();

    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException ex) {
      System.err.println("There was a problem registering the native hook.");
      System.err.println(ex.getMessage());

      System.exit(1);
    }
    GlobalMouseListenerExample example = new GlobalMouseListenerExample();

    // Add the appropriate listeners.
    GlobalScreen.addNativeMouseListener(example);
    GlobalScreen.addNativeMouseMotionListener(example);
    // ArduinoController periperal = new ArduinoController();
    // periperal.initialize();
    // MapDB.setKiosk("NSERV00301", 180);

    /*final String DEFAULT///_NODES = "csv/UPDATEDTeamNnodes.csv";
    final String DEFAULT_PATHS = "csv/UPDATEDTeamNedges.csv";
    final InputStream INPUT_NODES_DEFAULT = Main.class.getResourceAsStream(DEFAULT_NODES);
    final InputStream INPUT_EDGES_DEFAULT = Main.class.getResourceAsStream(DEFAULT_PATHS);
    CSVParser.parseCSV(INPUT_NODES_DEFAULT);
    CSVParser.parseCSV(INPUT_EDGES_DEFAULT);*/

    App.launch(App.class, args);
  }
}
