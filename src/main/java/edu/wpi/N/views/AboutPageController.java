package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import edu.wpi.N.App;
import edu.wpi.N.entities.States.StateSingleton;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class AboutPageController implements Controller {
  @FXML JFXButton btn_back;
  private App mainApp;
  private StateSingleton singleton;

  public AboutPageController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @FXML
  public void onBtnBackClicked(MouseEvent event) throws IOException {
    mainApp.switchScene("/edu/wpi/N/views/newHomePage.fxml", singleton);
  }
}
