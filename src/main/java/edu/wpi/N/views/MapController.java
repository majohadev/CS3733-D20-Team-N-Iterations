package edu.wpi.N.views;

import edu.wpi.N.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MapController implements Controller {
  private App mainApp;
  @FXML Button btn_map;

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }
}
