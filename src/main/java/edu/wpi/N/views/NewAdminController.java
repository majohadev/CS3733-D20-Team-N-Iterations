package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.LoginDB;
import java.time.LocalTime;
import java.util.LinkedList;

import edu.wpi.N.entities.Translator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class NewAdminController implements Controller {

  private App mainApp = null;

  @FXML Label lbl_Time;
  @FXML Label lbl_Date;
  @FXML JFXPasswordField pwf_newpass;
  @FXML JFXPasswordField pwf_confpass;
  @FXML JFXTextField txtf_newuser;
  @FXML StackPane sp_getpanes;
  @FXML Pane pn_pane1;
  @FXML Pane pn_pane2;
  @FXML Pane pn_pane3;
  @FXML JFXButton btn_EmployeeEdit;
  @FXML JFXButton btn_AccountEdit;
  @FXML JFXButton btn_ViewRequests;
  @FXML JFXButton btn_EditMap;
  @FXML ChoiceBox cb_credentials;

  ObservableList<String> credentials = FXCollections.observableArrayList();

  public void setTime() throws InterruptedException {
    try {
      Thread.sleep(60 * 1000); // One Minute

      LocalTime hour = null;
      LocalTime minute = null;
      lbl_Time.setText(hour.getHour() + ":" + minute.getMinute());

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void addNewAdmin() throws DBException {
    try {
      String newUser = txtf_newuser.getText();
      String newPass = pwf_newpass.getText();
      System.out.println(
          "Original Pass: " + pwf_newpass.getText() + " Conf Pass: " + pwf_confpass.getText());
      // if (newPass.equals(pwf_confpass.getText())) {
      LoginDB.createAdminLogin(newUser, newPass);
      System.out.println("Admin Created");

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  public void changePanes(MouseEvent e) {
    if (e.getSource() == btn_AccountEdit) {
      System.out.println("In Account Edit");
      pn_pane1.setVisible(true);
      pn_pane2.setVisible(false);
      pn_pane3.setVisible(false);
    } else if (e.getSource() == btn_EmployeeEdit) {
      System.out.println("In Employee Edit");
      pn_pane2.setVisible(true);
      pn_pane1.setVisible(false);
      pn_pane3.setVisible(false);
    } else if (e.getSource() == btn_ViewRequests) {
      System.out.println("In View Requests");
      pn_pane3.setVisible(true);
      pn_pane1.setVisible(false);
      pn_pane2.setVisible(false);
    }
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }
}
