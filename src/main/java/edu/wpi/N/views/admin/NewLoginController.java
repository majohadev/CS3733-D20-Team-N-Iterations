package edu.wpi.N.views.admin;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.LoginDB;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

public class NewLoginController implements Controller, Initializable {

  private StateSingleton singleton;

  private App mainApp = null;

  @FXML JFXPasswordField pwf_password;
  @FXML JFXTextField txtf_username;

  String medReq = "views/newMedRequest.fxml";

  // Inject singleton
  public NewLoginController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  @FXML
  public void checkUser() throws DBException {
    try {

      String pass = pwf_password.getText();
      String user = txtf_username.getText();
      LoginDB.verifyLogin(user, pass);

      if (LoginDB.currentAccess().equals("ADMIN")) {
        mainApp.switchScene("views/admin/adminPortal.fxml", singleton);
      } else if (LoginDB.currentAccess().equals("DOCTOR")) {
        mainApp.switchScene(medReq, singleton);
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
    mainApp.switchScene("views/mapDisplay/newMapDisplay.fxml", singleton);
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

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    txtf_username.setStyle("-fx-text-inner-color: white");
    pwf_password.setStyle("-fx-text-inner-color: white");
  }
}
