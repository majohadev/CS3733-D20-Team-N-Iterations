package edu.wpi.N.views;

import static org.testfx.api.FxAssert.verifyThat;

import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import java.io.IOException;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.TextInputControlMatchers;

public class EmotionalRequestControllerTest extends ApplicationTest {
  @Override
  public void start(Stage primaryStage)
      throws IOException, SQLException, DBException, ClassNotFoundException {
    MapDB.initTestDB();

    AnchorPane pane =
        (AnchorPane) FXMLLoader.load(App.class.getResource("views/emotionalSupportReq.fxml"));
    Scene scene = new Scene(pane);

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  @Test
  public void testAddNewRequest() {
    clickOn("#cmbo_text").write("1000");
    verifyThat("#loanAmountTxt", TextInputControlMatchers.hasText("1000"));
  }
}
