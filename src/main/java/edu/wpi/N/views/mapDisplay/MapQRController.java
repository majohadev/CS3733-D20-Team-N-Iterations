package edu.wpi.N.views.mapDisplay;

import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTreeView;
import edu.wpi.N.App;
import edu.wpi.N.views.Controller;
import edu.wpi.N.views.mapDisplay.enums.Buildings;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;

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
   * @param first the first building which path finding traverses through
   * @param second the second building which path finding traverses through
   */
  public void setTabs(String first, String second) {
    tbpn_directions.getTabs().clear(); // removes all tabs from the pane
    addTabs(first); // add the first building in the path finding
    tbpn_directions.getSelectionModel().select(0); // default tab to the first building
    if (second != "") { // determines whether the path finding traverses through two buildings
      tbpn_directions.getTabs().add(tb_drive); // add the driving directions to the pane
      addTabs(second); // add the tab for the second building
    }
  }

  /**
   * Includes the tab depending on the input building
   * @param b the building which the path finding traverses through
   */
  private void addTabs(String b) {
    if (b == "Faulkner") { // determine whether the building is faulkner
      tbpn_directions.getTabs().add(tb_faulkner); // then add the faulkner tab to the pane
    }
    else if (b != "") { // determine whether the building is main
      tbpn_directions.getTabs().add(tb_main); // then add the main tab to the pane
    }
  }

  public void setFaulknerText() {

  }

  public void setMainText() {

  }

  public void setDriveText() {

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
