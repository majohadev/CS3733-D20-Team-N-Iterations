package edu.wpi.N.views.mapDisplay;

import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.directions.*;
import edu.wpi.N.App;
import edu.wpi.N.Main;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

public class GoogleMapController implements Controller, Initializable {

  private StateSingleton singleton;
  private App mainApp;

  @FXML protected WebView webView;

  public GoogleMapController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  // Empty constructor
  public GoogleMapController(){

  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    webView.getEngine().load(Main.class.getResource("views/googleMapDisplay.html").toString());
    System.out.println("test");
  }

  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }
}
