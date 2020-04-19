package edu.wpi.N.views;

import edu.wpi.N.App;

public class LoginController implements Controller {
  private App mainApp;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }
}
