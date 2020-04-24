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
import javafx.scene.layout.VBox;

public class hamburgerTestController implements Controller, Initializable {

  private App mainApp;
  @FXML JFXHamburger hm_ham1;
  @FXML JFXDrawer dw_drawer;

  @Override
  public void initialize(URL location, ResourceBundle resourceBundle) {
    try {
      System.out.println("Here");

      VBox box = FXMLLoader.load(getClass().getResource("/edu/wpi/N/views/testDrawer.fxml"));
      System.out.println("Here 2");
      dw_drawer.setSidePane(box);
      System.out.println("Here 3");

      HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hm_ham1);
      System.out.println("Here 4");

      transition.setRate(1);
      System.out.println("Here 5");

      hm_ham1.addEventHandler(
          MouseEvent.MOUSE_CLICKED,
          (e) -> {
            // invert the transition, 1 becomes -1 and -1 becomes 1
            transition.setRate(transition.getRate() * -1);
            System.out.println("Here 6");

            // play the animation
            transition.play();
            System.out.println("Here 7");

            if (dw_drawer.isOpened()) {
              dw_drawer.close();
            } else {
              dw_drawer.open();
            }
          });
      System.out.println("Here 8");

    } catch (IOException e) {
      Alert newAlert = new Alert(Alert.AlertType.ERROR);
      newAlert.setContentText(e.getMessage());
      newAlert.show();
    }
  }

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }
}
