package edu.wpi.N.views;

import edu.wpi.N.App;
import edu.wpi.N.algorithms.AStarTests;
import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

public class TimeControllerTest extends Application {

  private static Connection con;

  @Override
  public void start(Stage primaryStage)
      throws IOException, SQLException, DBException, ClassNotFoundException {
    MapDB.initTestDB();
    con = MapDB.getCon();
    InputStream inputNodes = AStarTests.class.getResourceAsStream("../csv/PrototypeNodes.csv");
    CSVParser.parseCSV(inputNodes);

    Pane pane = (Pane) FXMLLoader.load(App.class.getResource("views/timeTab.fxml"));
    Scene scene = new Scene(pane);

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  @Test
  public void testClockDisplay() {}

  @AfterAll
  public static void cleadDB() throws DBException {
    MapDB.clearNodes();
  }
}
