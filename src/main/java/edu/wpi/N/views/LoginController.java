package edu.wpi.N.views;

import edu.wpi.N.App;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginController implements Controller {
  private App mainApp;
  private static LoginController controller;
  public AdminController adminController;

  Boolean loggedin = false;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @FXML TextField txtf_username;
  @FXML PasswordField pwf_password;
  @FXML Button btn_loginSubmit;
  @FXML Button btn_cancel;

  @FXML
  public void login(MouseEvent event) throws IOException {
    String username = txtf_username.getText();
    String password = pwf_password.getText();

    if (username.equals("admin")
        && password.equals("1234")
        && event.getSource() == btn_loginSubmit) {
      loggedin = true;
      checkCredentials(event);
      Stage stage = new Stage();
      Parent root;
      root = FXMLLoader.load(getClass().getResource("adminRequestScreen.fxml"));
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.show();
    } else if (event.getSource() == btn_cancel) {
      ((Node) (event.getSource())).getScene().getWindow().hide();
      loggedin = false;
    }
  }

  public void checkCredentials(MouseEvent e) throws IOException {
    ((Node) (e.getSource())).getScene().getWindow().hide();
    System.out.println(loggedin);
  }

  public void getControllerMethod() {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("adminRequestScreen.fxml"));
    loader.getController(); // Controller State is saved
  }
}
