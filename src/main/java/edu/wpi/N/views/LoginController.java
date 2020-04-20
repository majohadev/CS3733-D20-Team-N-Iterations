package edu.wpi.N.views;

import edu.wpi.N.App;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController implements Controller {
  private App mainApp;

  Boolean loggedin = false;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @FXML TextField txtf_username;
  @FXML PasswordField pwf_password;

  @FXML
  public void login() throws IOException {
    String user = txtf_username.getText();
    String pass = pwf_password.getText();
    System.out.println("User: " + user + " Pass: " + pass);
    checkCredentials(user, pass);
  }

  public void checkCredentials(String username, String password) throws IOException {
    if (username.equals("admin") && password.equals("1234")) {
      loggedin = true;
      Stage stage = new Stage();
      Parent root;
      root = FXMLLoader.load(getClass().getResource("adminRequestScreen.fxml"));
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.show();
    } else {
      loggedin = false;
    }
    System.out.println(loggedin);
  }

  public void popupAdminScreen() throws IOException {
    Stage stage = new Stage();
    Parent root;
    root = FXMLLoader.load(getClass().getResource("adminRequestScreen.fxml"));
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
}
