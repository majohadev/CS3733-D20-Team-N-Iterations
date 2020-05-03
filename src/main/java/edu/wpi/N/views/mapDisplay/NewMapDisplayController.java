package edu.wpi.N.views.mapDisplay;

import edu.wpi.N.App;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;

public class NewMapDisplayController implements Controller {
  private App mainApp;
  private StateSingleton singleton;

  public void initialize() {}

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }


}
