package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import edu.wpi.N.App;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;

public class HomeController {

  @FXML JFXButton btn_openQR;

  private App mainApp;

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @FXML
  private void openQrReader(MouseEvent e) {
    try {
      Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/N/views/QRTest.fxml"));
      mainApp.getPrimaryStage().getScene().setRoot(root);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
