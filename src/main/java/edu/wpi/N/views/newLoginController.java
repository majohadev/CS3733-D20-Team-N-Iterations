package edu.wpi.N.views;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.LoginDB;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class newLoginController implements Controller {

  private App mainApp = null;

  @FXML JFXPasswordField pwf_password;
  @FXML JFXTextField txtf_username;

  @FXML
  public void checkUser() throws DBException {
    try {
      String pass = pwf_password.getText();
      String user = txtf_username.getText();
      LoginDB.verifyLogin(user, pass);

      if (LoginDB.currentAccess().equals("ADMIN")) {
        mainApp.switchScene("views/adminPortal.fxml");
      } else if (LoginDB.currentAccess().equals("DOCTOR")) {
        mainApp.switchScene("views/medicineRequest.fxml");
      }
      System.out.println("Valid Login");
    } catch (DBException | IOException e) {
      Alert invalidLogin = new Alert(Alert.AlertType.ERROR);
      invalidLogin.setContentText(e.getMessage());
      invalidLogin.show();
    }
  }

  @FXML
  public void clearFields() {
    pwf_password.clear();
    txtf_username.clear();
  }

  public void goBack() throws IOException {
    mainApp.switchScene("views/home.fxml");
  }

  @FXML
  public void logoutUser() throws DBException {
    try {
      LoginDB.logout();
    } catch (DBException e) {
      Alert logoutError = new Alert(Alert.AlertType.ERROR);
      logoutError.setContentText(e.getMessage());
      logoutError.show();
    }
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void childrenSwap() {}
}
