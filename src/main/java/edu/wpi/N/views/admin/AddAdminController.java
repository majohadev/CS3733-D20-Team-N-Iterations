package edu.wpi.N.views.admin;

import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.LoginDB;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class AddAdminController {

  @FXML JFXTextField txtf_adduser;
  @FXML JFXTextField txtf_addpass;
  @FXML JFXTextField txtf_username;
  @FXML JFXTextField txtf_oldpassword;
  @FXML JFXTextField txtf_newpassword;
  @FXML JFXTextField txtf_rmusername;

  NewAdminController newAdminController;

  public void setAdminController(NewAdminController adminController) {
    this.newAdminController = adminController;
  }

  public void addAdmin() {
    try {

      if (txtf_adduser.getText().equals("")) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText("No username provided.");
        errorAlert.show();

        return;
      }

      if (txtf_addpass.getText().equals("")) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText("No password provided.");
        errorAlert.show();

        return;
      }

      LoginDB.createAdminLogin(txtf_adduser.getText(), txtf_addpass.getText());

      Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
      confAlert.setContentText("New Login Created.");
      confAlert.show();

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }

    txtf_addpass.clear();
    txtf_adduser.clear();
  }

  public void changePassword() {
    try {
      if (txtf_username.getText().equals("")) {

        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText("Provide a username");
        errorAlert.show();

        return;
      }

      if (txtf_newpassword.getText().equals("")) {

        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText("Provide a new password");
        errorAlert.show();

        return;
      }

      if (txtf_oldpassword.getText().equals("")) {

        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText("Provide original password.");
        errorAlert.show();

        return;
      }

      if (txtf_oldpassword.getText().equals(txtf_newpassword.getText())) {

        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText("Old and New password is the same.");
        errorAlert.show();

        return;
      }

      LoginDB.changePass(
          txtf_username.getText(), txtf_oldpassword.getText(), txtf_newpassword.getText());

      Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
      confAlert.setContentText("Password has been updated.");
      confAlert.show();

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }

    txtf_oldpassword.clear();
    txtf_newpassword.clear();
    txtf_username.clear();
  }

  public void removeLogin() {
    try {

      if (txtf_rmusername.getText().equals("")) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText("No username provided");
        errorAlert.show();

        return;
      }

      LoginDB.removeLogin(txtf_rmusername.getText());

      Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
      confAlert.setContentText("Login Removed");
      confAlert.show();

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }

    txtf_rmusername.clear();
  }
}
