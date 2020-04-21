package edu.wpi.N.views;

import edu.wpi.N.App;
import edu.wpi.N.qrcontrol.QRReader;
import java.awt.*;
import java.io.IOException;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.*;

public class LoginController extends QRReader implements Controller {
  private App mainApp;
  private static LoginController controller;
  public AdminController adminController;

  Boolean loggedin = false;

  private final String USER = "admin"; // Username
  private final String PASS = "1234"; // Password
  private final String DELIM = "\\$"; // QR code string delimiter - just pulls out a "$"

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @FXML TextField txtf_username;
  @FXML PasswordField pwf_password;
  @FXML Button btn_loginSubmit;
  @FXML Button btn_cancel;
  @FXML Pane pn_swingSpot;

  @FXML
  public void initialize() {
    SwingNode pn_webcamViewContainer = new SwingNode();
    pn_swingSpot.getChildren().add(pn_webcamViewContainer);
    createAndSetSwingContent(pn_webcamViewContainer, pn_swingSpot);

    // Open camera
    panel.resume();
    panel.setVisible(true);
    startScan(true);
  }

  @FXML
  public void login(MouseEvent event) {

    if (event.getSource() == btn_loginSubmit) {
      tryLogin(txtf_username.getText(), pwf_password.getText());
    } else if (event.getSource() == btn_cancel) {

      cancelScan();
      panel.pause();
      panel.setVisible(false);

      ((Node) (event.getSource())).getScene().getWindow().hide();
      loggedin = false;
    }
  }

  private boolean tryLogin(String user, String pass) {

    if (user.equals(USER) && pass.equals(PASS)) {
      loggedin = true;
      pn_swingSpot.getScene().getWindow().hide();

      cancelScan();
      panel.pause();
      panel.setVisible(false);

      try {
        Stage stage = new Stage();
        Parent root;
        root = FXMLLoader.load(getClass().getResource("adminRequestScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
      } catch (IOException e) {
        System.out.println("Root failed to load in LoginController!");
      }
    } else {
      loggedin = false;
      txtf_username.setText("");
      pwf_password.setText("");
    }
    return loggedin;
  }

  @Override
  public void onScanSucceed(String readKey) {
    String[] stringArr = readKey.split(DELIM);
    if (!(stringArr.length == 2 && tryLogin(stringArr[0], stringArr[1]))) {
      onScanFail();
    }
  }

  @Override
  public void onScanFail() {
    startScan(true);
  }

  // Put camera view into SwingNode
  private void createAndSetSwingContent(final SwingNode swingNode, final Pane pane) {
    SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            Dimension d = new Dimension();
            d.setSize(pane.getPrefWidth(), pane.getPrefHeight());
            panel.setMaximumSize(d);
            swingNode.setContent(panel);
          }
        });
  }

  public void getControllerMethod() {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("adminRequestScreen.fxml"));
    loader.getController(); // Controller State is saved
  }
}
