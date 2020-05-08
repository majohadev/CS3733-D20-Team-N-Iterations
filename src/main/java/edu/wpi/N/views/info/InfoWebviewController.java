package edu.wpi.N.views.info;

import edu.wpi.N.App;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

public class InfoWebviewController implements Controller, Initializable {
  private App mainApp;
  private StateSingleton singleton;
  public static String URL;

  @FXML protected WebView webview;

  public InfoWebviewController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    webview.getEngine().load(URL);
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void onBtnBackClicked() throws IOException {
    mainApp.switchScene("/edu/wpi/N/views/info/creditsPage.fxml", singleton);
  }
}
