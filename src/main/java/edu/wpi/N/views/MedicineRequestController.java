package edu.wpi.N.views;

import edu.wpi.N.App;
import java.io.IOException;

public class MedicineRequestController implements Controller {

  private App mainApp;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  // Temp Placeholder for Testing
  public void returnToRequests() throws IOException {
    this.mainApp.switchScene("home.fxml");
  }
}
