package edu.wpi.N.views.services;

import com.jfoenix.controls.JFXButton;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class ServiceController implements Controller {

  public AnchorPane service_anchor;
  private App mainApp;
  private StateSingleton singleton;

  @FXML JFXButton btn_translator;
  @FXML JFXButton btn_laundry;

  public ServiceController() throws DBException {}

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @Override
  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public void initialize() throws DBException, IOException {
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("mainServicePage.fxml"));
    service_anchor.getChildren().setAll(currentPane);
  }

  @FXML
  public void switchToTranslatorPage() throws IOException {

    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("serviceTemplate.fxml"));
    service_anchor.getChildren().setAll(currentPane);
  }

  @FXML
  public void switchToLaundryPage() throws IOException {

    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("laundryPage.fxml"));
    service_anchor.getChildren().setAll(currentPane);
  }

  @FXML
  public void switchToEmotionalPage() throws IOException {
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("emotionalSupportReq.fxml"));
    service_anchor.getChildren().setAll(currentPane);
  }

  @FXML
  public void switchToSanitationPage() throws IOException {
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("sanitationRequestPage.fxml"));
    service_anchor.getChildren().setAll(currentPane);
  }

  @FXML
  public void switchToITServicePage() throws IOException {
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("itService.fxml"));
    service_anchor.getChildren().setAll(currentPane);
  }

  @FXML
  public void switchToFloralPage() throws IOException {
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("flowerDeliveryReq.fxml"));
    service_anchor.getChildren().setAll(currentPane);
  }

  @FXML
  public void switchToWheelchairPage() throws IOException {
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("wheelchairRequest.fxml"));
    service_anchor.getChildren().setAll(currentPane);
  }

  @FXML
  public void backHome() throws IOException {
    mainApp.switchScene("views/newHomePage.fxml", singleton);
  }

  @FXML
  public void switchToTransportPage() throws IOException {
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("internalTransport.fxml"));
    service_anchor.getChildren().setAll(currentPane);
  }

  @FXML
  public void switchToSecurityPage() throws IOException {
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("securityRequest.fxml"));
    service_anchor.getChildren().setAll(currentPane);
  }
}
