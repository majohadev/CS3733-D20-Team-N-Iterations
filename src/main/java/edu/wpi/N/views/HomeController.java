package edu.wpi.N.views;

import edu.wpi.N.App;
import edu.wpi.N.entities.States.StateSingleton;
import java.io.IOException;

public class HomeController implements Controller {
  private App mainApp;

  private StateSingleton singleton;

  @Override
  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void onBtnMapClicked() throws IOException {
    this.mainApp.switchScene("views/mapDisplay.fxml", singleton);
  }

  public void onBtnEditClicked() throws IOException {
    this.mainApp.switchScene("views/mapEditor.fxml", singleton);
  }

  public void onBtnFileClicked() throws IOException {
    this.mainApp.switchScene("views/servicesPage.fxml", singleton);
  }

  public void onBtnHamClicked() throws IOException {
    this.mainApp.switchScene("/edu/wpi/N/views/hamburgerTest.fxml", singleton);
  }

  public void onBtnLoginClicked() throws IOException {
    this.mainApp.switchScene("/edu/wpi/N/views/newLogin.fxml", singleton);
  }

  public void onBtnAdminClick() throws IOException {
    this.mainApp.switchScene("/edu/wpi/N/views/adminPortal.fxml", singleton);
  }

  public void onBtnSanClick() throws IOException {
    this.mainApp.switchScene("/edu/wpi/N/views/sanitationRequestPage.fxml", singleton);
  }
}
