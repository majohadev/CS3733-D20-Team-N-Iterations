package edu.wpi.N.views;

import edu.wpi.N.App;

public class MapEditController implements Controller {
  App mainApp = null;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }
}
