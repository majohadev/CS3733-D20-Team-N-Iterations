package edu.wpi.N.views;

import edu.wpi.N.App;
import edu.wpi.N.entities.States.StateSingleton;

public interface Controller {
  App mainApp = null;
  StateSingleton singleton = null;

  void setMainApp(App mainApp);

  void setSingleton(StateSingleton singleton);
}
