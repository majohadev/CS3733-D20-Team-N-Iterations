package edu.wpi.N.views.services;

import com.jfoenix.controls.JFXButton;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class ServiceController implements Controller {

  private StateSingleton singleton;
  public Pane service_anchor;
  private App mainApp;

  @FXML JFXButton btn_translator;
  @FXML JFXButton btn_laundry;
  @FXML JFXButton btn_it;
  @FXML JFXButton btn_flower;
  @FXML JFXButton btn_schedule;
  @FXML JFXButton btn_security;
  @FXML JFXButton btn_wheelchair;
  @FXML JFXButton btn_sanitation;
  @FXML JFXButton btn_transport;

  @FXML Label txt_translator;
  @FXML Label txt_laundry;
  @FXML Label txt_it;
  @FXML Label txt_flower;
  @FXML Label txt_schedule;
  @FXML Label txt_security;
  @FXML Label txt_wheelchair;
  @FXML Label txt_sanitation;
  @FXML Label txt_transport;

  public ServiceController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException, IOException {
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("mainServicePage.fxml"));
    service_anchor.getChildren().setAll(currentPane);
    txt_flower.setVisible(false);
    txt_it.setVisible(false);
    txt_laundry.setVisible(false);
    txt_sanitation.setVisible(false);
    txt_schedule.setVisible(false);
    txt_security.setVisible(false);
    txt_wheelchair.setVisible(false);
    txt_transport.setVisible(false);
    txt_translator.setVisible(false);
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
    mainApp.switchScene("views/mapDisplay/newMapDisplay.fxml", singleton);
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

  @FXML
  public void switchToScheduler() throws Exception {
    //    AppointmentRequest apt = new AppointmentRequest();
    //    // String css = this.getClass().getResource("sanitationRequestUI1.css").toExternalForm();
    //    String css = this.getClass().getResource("default.css").toExternalForm();
    //    AppointmentRequest.run(576, 90, 1280, 950, css, null, null);
  }

  public void onIconClicked(MouseEvent event) throws IOException {
    backHome();
  }

  @FXML
  public void showLabel(MouseEvent e) {
    if (e.getSource() == btn_flower) txt_flower.setVisible(true);
    if (e.getSource() == btn_translator) txt_translator.setVisible(true);
    if (e.getSource() == btn_sanitation) txt_sanitation.setVisible(true);
    if (e.getSource() == btn_schedule) txt_schedule.setVisible(true);
    if (e.getSource() == btn_security) txt_security.setVisible(true);
    if (e.getSource() == btn_it) txt_it.setVisible(true);
    if (e.getSource() == btn_laundry) txt_laundry.setVisible(true);
    if (e.getSource() == btn_wheelchair) txt_wheelchair.setVisible(true);
    if (e.getSource() == btn_transport) txt_transport.setVisible(true);
  }

  @FXML
  public void hideLabel(MouseEvent e) {

    if (e.getSource() == btn_flower) txt_flower.setVisible(false);
    if (e.getSource() == btn_translator) txt_translator.setVisible(false);
    if (e.getSource() == btn_sanitation) txt_sanitation.setVisible(false);
    if (e.getSource() == btn_schedule) txt_schedule.setVisible(false);
    if (e.getSource() == btn_security) txt_security.setVisible(false);
    if (e.getSource() == btn_it) txt_it.setVisible(false);
    if (e.getSource() == btn_laundry) txt_laundry.setVisible(false);
    if (e.getSource() == btn_wheelchair) txt_wheelchair.setVisible(false);
    if (e.getSource() == btn_transport) txt_transport.setVisible(false);
  }
}
