package edu.wpi.N.views;

import edu.wpi.N.App;
import edu.wpi.N.database.DBException;

public class ServiceController implements Controller {

  private App mainApp;

  public ServiceController() throws DBException {}

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException {}
}
