package edu.wpi.N.views;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import edu.wpi.N.App;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class hamburgerTestController implements Controller, Initializable {

  private App mainApp;
  @FXML JFXHamburger hm_ham1;
  @FXML JFXDrawer dw_drawer;

  @Override
  public void initialize(URL location, ResourceBundle resourceBundle) {
    try {

      AnchorPane box = FXMLLoader.load(getClass().getResource("testDrawer.fxml"));
      dw_drawer.setSidePane(box);

      HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hm_ham1);

      transition.setRate(1);
      hm_ham1.addEventHandler(
          MouseEvent.MOUSE_CLICKED,
          (e) -> {
            // invert the transition, 1 becomes -1 and -1 becomes 1
            transition.setRate(transition.getRate() * -1);
            // play the animation
            transition.play();

            if (dw_drawer.isOpened()) {
              dw_drawer.close();
            } else {
              dw_drawer.open();
            }
          });

    } catch (IOException e) {
      Alert newAlert = new Alert(Alert.AlertType.ERROR);
      newAlert.setContentText(e.getMessage());
      newAlert.show();
    }
  }

  @Override
  public void setMainApp(App mainApp) {}
}
