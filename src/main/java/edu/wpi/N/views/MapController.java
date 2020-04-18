package edu.wpi.N.views;

import edu.wpi.N.App;
import java.awt.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class MapController implements Controller {
  private App mainApp;
  @FXML Button btn_map;
  @FXML AnchorPane anchorPane;

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() {
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    int width = gd.getDisplayMode().getWidth();
    int height = gd.getDisplayMode().getHeight();
  }
}
