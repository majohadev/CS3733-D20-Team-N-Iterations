package edu.wpi.N.views.info;

import edu.wpi.N.App;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import javafx.scene.input.MouseEvent;

public class CreditsPageController implements Controller {
  private App mainApp;
  private StateSingleton singleton;

  public CreditsPageController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void onBtnAboutClicked(MouseEvent event) throws IOException {
    mainApp.switchScene("/edu/wpi/N/views/info/aboutPage.fxml", singleton);
  }

  public void onBtnNavigationClicked() throws IOException {
    mainApp.switchScene("/edu/wpi/N/views/mapDisplay/newMapDisplay.fxml", singleton);
  }

  public void onLink1Clicked() throws IOException {
    InfoWebviewController.setURL("https://github.com/patrickfav/bcrypt/tree/v0.9.0");
    mainApp.switchScene("/edu/wpi/N/views/info/infoWebview.fxml", singleton);
  }

  public void onLink2Clicked() throws IOException {
    InfoWebviewController.setURL("https://dialogflow.com/");
    mainApp.switchScene("/edu/wpi/N/views/info/infoWebview.fxml", singleton);
  }

  public void onLink3Clicked() throws IOException {
    InfoWebviewController.setURL("http://jfoenix.com/");
    mainApp.switchScene("/edu/wpi/N/views/info/infoWebview.fxml", singleton);
  }

  public void onLink4Clicked() throws IOException {
    InfoWebviewController.setURL("https://gradle.org/");
    mainApp.switchScene("/edu/wpi/N/views/info/infoWebview.fxml", singleton);
  }

  public void onLink5Clicked() throws IOException {
    InfoWebviewController.setURL("https://db.apache.org/derby/");
    mainApp.switchScene("/edu/wpi/N/views/info/infoWebview.fxml", singleton);
  }

  public void onLink6Clicked() throws IOException {
    InfoWebviewController.setURL("https://guava.dev/");
    mainApp.switchScene("/edu/wpi/N/views/info/infoWebview.fxml", singleton);
  }

  public void onLink7Clicked() throws IOException {
    InfoWebviewController.setURL("http://opencsv.sourceforge.net/");
    mainApp.switchScene("/edu/wpi/N/views/info/infoWebview.fxml", singleton);
  }

  public void onLinkApacheLicenseClicked() throws IOException {
    InfoWebviewController.setURL("https://www.apache.org/licenses/LICENSE-2.0");
    mainApp.switchScene("/edu/wpi/N/views/info/infoWebview.fxml", singleton);
  }
}
