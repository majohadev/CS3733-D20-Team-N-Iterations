package edu.wpi.N.views;

import com.github.sarxos.webcam.WebcamPanel;
import com.jfoenix.controls.JFXButton;
import edu.wpi.N.App;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.qrcontrol.QRReader;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javax.swing.*;

public class QRReadTestController extends QRReader implements Controller {

  private StateSingleton singleton;

  @Override
  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  @FXML JFXButton btn_scanButton;
  @FXML Label lbl_output;
  @FXML AnchorPane pane_swingNodeSpot;

  // SceneBuilder gets whiny if you try to add a SwingNode before runtime
  private SwingNode pane_webcamViewContainer;

  private App mainApp;

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @FXML
  public void initialize() {
    pane_webcamViewContainer = new SwingNode();
    pane_swingNodeSpot.getChildren().add(pane_webcamViewContainer);
    createAndSetSwingContent(pane_webcamViewContainer, pane_swingNodeSpot);
  }

  // When scan button is clicked
  @FXML
  public void onScanClicked() {

    btn_scanButton.setDisable(true);
    lbl_output.setText("Make sure QR code is clearly within view.");
    btn_scanButton.setText("Scanning...");

    // Open camera
    panel.resume();
    panel.setVisible(true);

    startScan(false);
  }

  @Override
  public void onScanSucceed(String readKey) {

    // Close camera
    panel.pause();
    panel.setVisible(false);

    lbl_output.setText("Scan successful! Key: " + readKey);
    btn_scanButton.setDisable(false);
    btn_scanButton.setText("Scan");
  }

  @Override
  public void onScanFail() {

    // Close camera
    panel.pause();
    panel.setVisible(false);

    lbl_output.setText("Scan timed out.");
    btn_scanButton.setDisable(false);
    btn_scanButton.setText("Scan");
  }

  // Put camera view into SwingNode
  private void createAndSetSwingContent(final SwingNode swingNode, final Pane pane) {
    SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            WebcamPanel wp = panel;
            swingNode.setContent(wp);
          }
        });
  }
}
