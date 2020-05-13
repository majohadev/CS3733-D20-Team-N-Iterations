package edu.wpi.N.views;

import edu.wpi.N.AppClass;
import edu.wpi.N.entities.States.StateSingleton;

public interface Controller {
  AppClass mainApp = null;
  StateSingleton singleton = null;

  void setMainApp(AppClass mainApp);
}
