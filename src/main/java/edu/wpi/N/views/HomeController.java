package edu.wpi.N.views;

import edu.wpi.N.App;
import javafx.fxml.FXML;

import java.awt.*;

public class HomeController {
  private App mainApp;

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @FXML
  Button btn_Swap;
}
