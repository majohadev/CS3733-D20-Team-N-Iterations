package edu.wpi.N.views.mapDisplay;

import edu.wpi.N.App;
import edu.wpi.N.views.Controller;

public class MapLocationSearchController implements Controller {
  App mainApp;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }
}
