package edu.wpi.N.views.mapDisplay;

import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTreeView;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.Direction;
import edu.wpi.N.views.Controller;
import edu.wpi.N.views.mapDisplay.enums.Buildings;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.LinkedList;

public class MapQRController implements Controller {

  @FXML JFXTabPane tbpn_directions;
  @FXML Tab tb_faulkner;
  @FXML Tab tb_main;
  @FXML Tab tb_drive;
  @FXML JFXTreeView tr_faulkner;
  @FXML JFXTreeView tr_main;
  @FXML JFXTreeView tr_drive;


  @Override
  public void setMainApp(App mainApp) {}

  public void initialize() {}

  /**
   * Populates tabs according to the results of the path finding
   * @param start the first building which path finding traverses through
   * @param end the second building which path finding traverses through
   */
  public void setTabs(String start, String end) {

    tbpn_directions.getTabs().clear();
    addTabs(start);
    tbpn_directions.getSelectionModel().select(0);
    if (end != start) {
      tbpn_directions.getTabs().add(tb_drive);
      addTabs(end);
    }
  }

  /**
   * Includes the tab depending on the input building
   * @param b the building which the path finding traverses through
   */
  private void addTabs(String b) {
    if (b == "Faulkner") {
      tbpn_directions.getTabs().add(tb_faulkner);
    }
    else if (b != "") {
      tbpn_directions.getTabs().add(tb_main);
    }
  }


  public void setFaulknerText(ArrayList<Direction> dir) {

  }

  public void setMainText(ArrayList<Direction> dir) {

  }

  public void setDriveText(ArrayList<Direction> dir) {

  }


//  public TextArea getTextFaulkner() {
//    return this.txt_faulkner_directions;
//  }
//
//  public TextArea getTextMain() {
//    return this.txt_main_directions;
//  }
//
//  public TextArea getTextDrive() {
//    return this.txt_drive_directions;
//  }
//
//  public ImageView getImageFaulkner() {
//    return this.img_faulkner;
//  }
//
//  public ImageView getImageMain() {
//    return this.img_main;
//  }
//
//  public ImageView getImageDrive() {
//    return this.img_drive;
//  }
}
