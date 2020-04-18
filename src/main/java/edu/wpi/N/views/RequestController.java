package edu.wpi.N.views;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerNextArrowBasicTransition;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class RequestController implements Initializable {
  @FXML private JFXHamburger fx_ham1;
  @FXML private JFXDrawer fx_drawer;
  @FXML private TextField txt_laundryFloor;
  @FXML private AnchorPane anchorPane;
  @FXML private Label languageLabel;
  @FXML private TextField languageRequest;

  String storedLanguage;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    HamburgerNextArrowBasicTransition transition = new HamburgerNextArrowBasicTransition(fx_ham1);
    try {
      VBox requestBox = FXMLLoader.load(getClass().getResource("drawerInsides.fxml"));
      fx_drawer.setSidePane(requestBox);

      for (Node node : requestBox.getChildren()) {
        node.addEventHandler(
            MouseEvent.MOUSE_CLICKED,
            (e) -> {
              switch (node.getAccessibleText()) {
                case "laundryService":
                  txt_laundryFloor.setVisible(true);
                  languageLabel.setVisible(false);
                  languageRequest.setVisible(false);
                  System.out.println(txt_laundryFloor.getText());
                  break;

                case "TranslatorRequest":
                  txt_laundryFloor.setVisible(false);
                  languageLabel.setVisible(true);
                  languageRequest.setVisible(true);
                  storedLanguage = languageRequest.getText();
                  System.out.println("Stored Language: " + storedLanguage);
              }
            });
      }

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
    } catch (IOException ex) {

    }
  }
}
