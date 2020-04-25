package edu.wpi.N.views;

import edu.wpi.N.App;
import java.io.IOException;

public class HomeController implements Controller {
  private App mainApp;

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void onBtnMapClicked() throws IOException {
    this.mainApp.switchScene("views/mapDisplayV2.fxml");
  }

  public void onBtnEditClicked() throws IOException {
    this.mainApp.switchScene("views/mapEdit.fxml");
  }

  public void onBtnFileClicked() throws IOException {
    this.mainApp.switchScene("views/fileManagementScreen.fxml");
  }
}
