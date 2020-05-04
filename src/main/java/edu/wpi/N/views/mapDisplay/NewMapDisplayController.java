package edu.wpi.N.views.mapDisplay;

import edu.wpi.N.App;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class NewMapDisplayController implements Controller {
  private App mainApp;
  private StateSingleton singleton;

  @FXML Pane pn_change;
  @FXML Pane pn_iconBar;
  @FXML Pane pn_locationIcon;
  @FXML Pane pn_doctorIcon;
  @FXML Pane pn_qrIcon;
  @FXML Pane pn_serviceIcon;
  @FXML Pane pn_infoIcon;
  @FXML Pane pn_adminIcon;

  MapLocationSearchController locationSearchController;
  MapDoctorSearchController doctorSearchController;
  MapQRController mapQRController;

  public void initialize() {}

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }



  public void onIconClicked(MouseEvent e) throws IOException {
    Pane src = (Pane) e.getSource();
    pn_iconBar.getChildren().forEach(n -> n.setStyle("-fx-background-color: #263051"));
    src.setStyle("-fx-background-color: #4A69C6;");
    FXMLLoader loader;
    if (src == pn_locationIcon) {
      loader = new FXMLLoader(getClass().getResource("mapLocationSearch.fxml"));
      Pane pane = loader.load();
      locationSearchController = loader.getController();
      pn_change.getChildren().add(pane);
    } else if (src == pn_doctorIcon) {
      loader = new FXMLLoader(getClass().getResource("mapDoctorSearch.fxml"));
      Pane pane = loader.load();
      doctorSearchController = loader.getController();
      pn_change.getChildren().add(pane);
    } else if (src == pn_qrIcon) {
      loader = new FXMLLoader(getClass().getResource("mapQR.fxml"));
      Pane pane = loader.load();
      mapQRController = loader.getController();
      pn_change.getChildren().add(pane);
    } else if (src == pn_serviceIcon) {
      // TODO load service page here
    } else if (src == pn_infoIcon) {
      // TODO load info page here
    } else if (src == pn_adminIcon) {
      // TODO load admin page here
    }
  }
}
