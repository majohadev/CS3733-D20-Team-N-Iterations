package edu.wpi.N.views;

import edu.wpi.N.App;
import edu.wpi.N.algorithms.AStarTests;
import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.request.EmotionalRequest;
import edu.wpi.N.entities.request.Request;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.ComboBoxMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

public class EmotionalRequestControllerTest extends ApplicationTest {

  private static Connection con;

  @Override
  public void start(Stage primaryStage)
      throws IOException, SQLException, DBException, ClassNotFoundException {
    MapDB.initTestDB();
    con = MapDB.getCon();
    InputStream inputNodes = AStarTests.class.getResourceAsStream("../csv/PrototypeNodes.csv");
    CSVParser.parseCSV(inputNodes);

    AnchorPane pane =
        (AnchorPane) FXMLLoader.load(App.class.getResource("views/emotionalSupportReq.fxml"));
    Scene scene = new Scene(pane);

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  // TODO: Add @Test flag before testing

  public void testAddNewRequest() throws DBException {

    try {
      con.setAutoCommit(false);
      // Insertion statements, like addTranslator

      clickOn("#cmbo_text").write("Duncan Reid Confarence");
      clickOn("Duncan Reid Conference Room");
      // type(KeyCode.ENTER);
      FxAssert.verifyThat(
          "#cmbo_text", ComboBoxMatchers.hasSelectedItem("Duncan Reid Conference Room"));

      clickOn("#cmbo_selectSupport").type(KeyCode.DOWN).type(KeyCode.DOWN);
      type(KeyCode.ENTER);
      FxAssert.verifyThat("#cmbo_selectSupport", ComboBoxMatchers.hasSelectedItem("Family"));

      clickOn("#txtf_supportNotes").write("I wanna live!");
      FxAssert.verifyThat("#txtf_supportNotes", TextInputControlMatchers.hasText("I wanna live!"));

      clickOn("#btn_submit");

      con.commit();
      con.setAutoCommit(true);
      // checking statements

      LinkedList<Request> allRequests = ServiceDB.getRequests();
      boolean passedTest = false;

      for (Request r : allRequests) {
        try {
          if (r.getNodeID().equals("BCONF00102")
              && r.getReqNotes().equals("I wanna live!")
              && r.getServiceType().equals("Emotional Support")
              && ((EmotionalRequest) r).getSupportType().equals("Family")) {
            Assertions.assertTrue(true);
            passedTest = true;
          }
        } catch (Exception e) {
          continue;
        }
      }

      if (!passedTest) {
        Assertions.fail();
      }

      // deleting statements
    } catch (SQLException e) { // also wanna catch DBException e
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Oh no");
      }
    }
  }

  @AfterAll
  public static void clearDB() throws DBException {
    MapDB.clearNodes();
  }
}
