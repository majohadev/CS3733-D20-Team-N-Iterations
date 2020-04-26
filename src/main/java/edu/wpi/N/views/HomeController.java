package edu.wpi.N.views;

import edu.wpi.N.App;
import java.io.IOException;

public class HomeController implements Controller {
  private App mainApp;

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void onBtnMapClicked() throws IOException {
    this.mainApp.switchScene("views/mapDisplay.fxml");
  }

  public void onBtnEditClicked() throws IOException {
    this.mainApp.switchScene("views/mapEditor.fxml");
  }

  public void onBtnFileClicked() throws IOException {
    this.mainApp.switchScene("views/fileManagementScreen.fxml");
  }

  public void onBtnFloorEdgesClicked() throws IOException {
    this.mainApp.switchScene("views/BetweenFloorsEditor.fxml");
  }
}
