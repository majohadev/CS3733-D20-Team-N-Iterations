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
    this.mainApp.switchScene("views/servicesPage.fxml");
  }

  public void onBtnHamClicked() throws IOException {
    this.mainApp.switchScene("/edu/wpi/N/views/hamburgerTest.fxml");
  }

  public void onBtnLoginClicked() throws IOException {
    this.mainApp.switchScene("/edu/wpi/N/views/newLogin.fxml");
  }

  public void onBtnAdminClick() throws IOException {
    this.mainApp.switchScene("/edu/wpi/N/views/adminPortal.fxml");
  }
}
