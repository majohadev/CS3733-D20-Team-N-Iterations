package edu.wpi.N.views;

import edu.wpi.N.App;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HomeController {
  private App mainApp;

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @FXML Button btn_Swap;

  @FXML
  public void swapToData() throws IOException {
    Stage stage = new Stage();
    Parent root;
    root = FXMLLoader.load(getClass().getResource("adminRequestScreen.fxml"));
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  public void logoutFunction() {}
}
