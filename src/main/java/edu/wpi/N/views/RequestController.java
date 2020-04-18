package edu.wpi.N.views;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerNextArrowBasicTransition;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

public class RequestController implements Initializable {
  @FXML private JFXHamburger fx_ham1;
  @FXML private JFXDrawer fx_drawer;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    HamburgerNextArrowBasicTransition transition = new HamburgerNextArrowBasicTransition(fx_ham1);
    transition.setRate(-1);
    fx_ham1.addEventHandler(
        MouseEvent.MOUSE_PRESSED,
        (e) -> {
          transition.setRate(transition.getRate() * -1);
          transition.play();

          if (fx_drawer.isOpened()) {
            fx_drawer.close();
          } else {
            fx_drawer.open();
          }
        });
  }
}
