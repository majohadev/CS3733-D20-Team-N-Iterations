package edu.wpi.N.views.info;

import edu.wpi.N.App;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import javafx.scene.input.MouseEvent;

public class AboutPageController implements Controller {
  private App mainApp;
  private StateSingleton singleton;

  public AboutPageController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void onBtnBackClicked(MouseEvent event) throws IOException {
    mainApp.switchScene("/edu/wpi/N/views/mapDisplay/newMapDisplay.fxml", singleton);
  }

  public void onBtnCreditsClicked() throws IOException {
    mainApp.switchScene("/edu/wpi/N/views/info/creditsPage.fxml", singleton);
  }
}
