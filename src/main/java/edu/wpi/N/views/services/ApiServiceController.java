package edu.wpi.N.views.services;

import com.jfoenix.controls.JFXButton;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import edu.wpi.cs3733.d20.teamB.api.IncidentReportApplication;
import edu.wpi.cs3733.d20.teamC.InterpreterRequest;
import edu.wpi.cs3733.d20.teamE.onCallBeds;
import edu.wpi.cs3733.d20.teamL.GiftServiceRequest;
import edu.wpi.cs3733.d20.teamP.APIController;
import edu.wpi.cs3733.d20.teamP.ServiceException;
import flowerapi.FlowerAPI;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class ApiServiceController implements Controller {

  private StateSingleton singleton;
  public Pane service_anchor;
  private App mainApp;
  private InterpreterRequest interpreterRequest = new InterpreterRequest();
  private onCallBeds oncall = new onCallBeds();
  private GiftServiceRequest giftRequest = new GiftServiceRequest();
  private IncidentReportApplication IncidentReport = new IncidentReportApplication();

  @FXML JFXButton btn_interpreter;
  @FXML JFXButton btn_onCall;
  @FXML JFXButton btn_food;
  @FXML JFXButton btn_flower;
  @FXML JFXButton btn_schedule;
  @FXML JFXButton btn_security;
  @FXML JFXButton btn_wheelchair;
  @FXML JFXButton btn_maintenance;
  @FXML JFXButton btn_gift;

  @FXML Label txt_interpreter;
  @FXML Label txt_onCall;
  @FXML Label txt_food;
  @FXML Label txt_flower;
  @FXML Label txt_schedule;
  @FXML Label txt_security;
  @FXML Label txt_wheelchair;
  @FXML Label txt_maintenance;
  @FXML Label txt_gift;

  public ApiServiceController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException, IOException {
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("mainServicePage.fxml"));
    service_anchor.getChildren().setAll(currentPane);
    txt_flower.setVisible(false);
    txt_onCall.setVisible(false);
    txt_interpreter.setVisible(false);
    txt_maintenance.setVisible(false);
    txt_schedule.setVisible(false);
    txt_security.setVisible(false);
    txt_wheelchair.setVisible(false);
    txt_gift.setVisible(false);
    txt_food.setVisible(false);
  }

  @FXML
  public void switchToInterpreterPage() throws IOException {

    String css = this.getClass().getResource("default.css").toExternalForm();
    interpreterRequest.run(576, 90, 1280, 950, null, null, null);
  }

  @FXML
  public void switchToOnCallPage() throws IOException {

    String css = this.getClass().getResource("default.css").toExternalForm();
    oncall.run(576, 90, 1280, 950, css, null, null);
  }

  @FXML
  public void switchToEmotionalPage() throws IOException {
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("emotionalSupportReq.fxml"));
    service_anchor.getChildren().setAll(currentPane);
  }

  @FXML
  public void switchToMaintenancePage() {
    //      throws IOException, edu.wpi.cs3733.d20.teamF.ModelClasses.ServiceException {
    //    MaintenanceRequestLaunch requestLaunch = new MaintenanceRequestLaunch();
    //    String css = this.getClass().getResource("default.css").toExternalForm();
    //    requestLaunch.run(576, 90, 1280, 950, css, null, null);
  }

  @FXML
  public void switchToFoodPage() throws IOException, ServiceException {
    // String css = this.getClass().getResource("default.css").toExternalForm();
    APIController.run(576, 90, 1280, 950, null, null, null);
  }

  @FXML
  public void switchToFloralPage() throws IOException, flowerapi.ServiceException {
    String css = this.getClass().getResource("default.css").toExternalForm();
    FlowerAPI.run(576, 90, 1280, 950, css, null, null);
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
    String css = this.getClass().getResource("default.css").toExternalForm();
    IncidentReport.run(576, 90, 1280, 950, css, null, null);
  }

  @FXML
  public void switchToGiftPage() throws Exception {
    String css = this.getClass().getResource("default.css").toExternalForm();
    giftRequest.run(576, 90, 1280, 950, css, null, null);
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
    if (e.getSource() == btn_interpreter) txt_interpreter.setVisible(true);
    if (e.getSource() == btn_maintenance) txt_maintenance.setVisible(true);
    if (e.getSource() == btn_schedule) txt_schedule.setVisible(true);
    if (e.getSource() == btn_security) txt_security.setVisible(true);
    if (e.getSource() == btn_food) txt_food.setVisible(true);
    if (e.getSource() == btn_onCall) txt_onCall.setVisible(true);
    if (e.getSource() == btn_wheelchair) txt_wheelchair.setVisible(true);
    if (e.getSource() == btn_gift) txt_gift.setVisible(true);
  }

  @FXML
  public void hideLabel(MouseEvent e) {

    if (e.getSource() == btn_flower) txt_flower.setVisible(false);
    if (e.getSource() == btn_interpreter) txt_interpreter.setVisible(false);
    if (e.getSource() == btn_maintenance) txt_maintenance.setVisible(false);
    if (e.getSource() == btn_schedule) txt_schedule.setVisible(false);
    if (e.getSource() == btn_security) txt_security.setVisible(false);
    if (e.getSource() == btn_food) txt_food.setVisible(false);
    if (e.getSource() == btn_onCall) txt_onCall.setVisible(false);
    if (e.getSource() == btn_wheelchair) txt_wheelchair.setVisible(false);
    if (e.getSource() == btn_gift) txt_gift.setVisible(false);
  }
}
