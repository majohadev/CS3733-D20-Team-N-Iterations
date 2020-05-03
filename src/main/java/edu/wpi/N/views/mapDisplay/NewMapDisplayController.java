package edu.wpi.N.views.mapDisplay;

import edu.wpi.N.App;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class NewMapDisplayController implements Controller {
  private App mainApp;
  private StateSingleton singleton;

  @FXML Pane pn_iconBar;
  @FXML Pane pn_change;

  public void initialize() {}

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void onLocationIconClicked(MouseEvent e) {
    pn_iconBar.getChildren().forEach(n -> n.setStyle("-fx-background-color: #263051"));
    ((Pane) e.getSource()).setStyle("-fx-background-color: #4A69C6;");
  }
}
