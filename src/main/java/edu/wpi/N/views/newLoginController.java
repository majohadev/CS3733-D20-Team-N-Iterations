package edu.wpi.N.views;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.LoginDB;
import edu.wpi.N.entities.Translator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.util.LinkedList;

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
      System.out.println("Valid Login");
    } catch (DBException e) {
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

  public void fillChoiceBox(){
    cb_Employee
            .valueProperty()
            .addListener(
                    (ov, old, emp) -> {
                      if (emp instanceof Translator) {
                        languageData.setAll(((Translator) emp).getLanguages());
                      } else {
                        languageData.setAll(new LinkedList<String>());
                      }
                    });
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void childrenSwap() {}
}
