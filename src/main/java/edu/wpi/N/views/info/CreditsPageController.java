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

  public void onLink1Clicked() {}

  public void onLink2Clicked() {}

  public void onLink3Clicked() {}

  public void onLink4Clicked() {}

  public void onLink5Clicked() {}

  public void onLink6Clicked() {}

  public void onLink7Clicked() {}

  public void onLinkApacheLicenseClicked() throws IOException {
    InfoWebviewController.URL = "https://www.apache.org/licenses/LICENSE-2.0";
    mainApp.switchScene("/edu/wpi/N/views/info/infoWebview.fxml", singleton);
  }
}
