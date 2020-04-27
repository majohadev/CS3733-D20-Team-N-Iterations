package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class ServiceController implements Controller {

  public AnchorPane servicePageTemplate;
  public AnchorPane service_anchor;
  private App mainApp;

  @FXML JFXButton btn_translator;
  @FXML JFXButton btn_laundry;

  public ServiceController() throws DBException {}

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException, IOException {}

  @FXML
  public void switchToTranslatorPage() throws IOException {

    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("serviceTemplate.fxml"));
    service_anchor.getChildren().setAll(currentPane);
  }

  @FXML
  public void switchToLaundryPage() throws IOException {

    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("fileManagementScreen.fxml"));
    service_anchor.getChildren().setAll(currentPane);
  }
}
