package edu.wpi.N.views;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import edu.wpi.N.App;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HomeController implements Controller {
  private App mainApp;
  @FXML Button btn_map;
  @FXML JFXHamburger ham_1;
  @FXML JFXDrawer homeDrawer;

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void onBtnMapClicked() throws IOException {
    this.mainApp.switchScene("views/mapDisplay.fxml");

  }

  public void onBtnEditClicked() throws IOException {
    this.mainApp.switchScene("");


  }
}
