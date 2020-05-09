package edu.wpi.N.views.mapDisplay;

import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTreeView;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.Direction;
import edu.wpi.N.algorithms.Level;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

public class MapQRController implements Controller {
  private StateSingleton singleton;

  @FXML JFXTabPane tbpn_directions;
  @FXML Tab tb_faulkner;
  @FXML Tab tb_main;
  @FXML Tab tb_drive;
  @FXML JFXTreeView tr_faulkner;
  @FXML JFXTreeView tr_main;
  @FXML JFXTreeView tr_drive;

  @Override
  public void setMainApp(App mainApp) {}

  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public void initialize() {}

  public void setTabs(String start, String end) {
    tbpn_directions.getTabs().clear();
    addTabs(start);
    tbpn_directions.getSelectionModel().select(0);
    if ((end.equals("Faulkner") && !start.equals("Faulkner"))
        || (!end.equals("Faulkner") && start.equals("Faulkner"))) {
      tbpn_directions.getTabs().add(tb_drive);
      addTabs(end);
    }
  }

  private void addTabs(String b) {
    if (b.equals("Faulkner")) {
      tbpn_directions.getTabs().add(tb_faulkner);
    } else {
      tbpn_directions.getTabs().add(tb_main);
    }
  }

  public void setFaulknerText(ArrayList<Direction> dirLst) {
    makeInstructions(dirLst, tr_faulkner);
  }

  public void setMainText(ArrayList<Direction> dirLst) {
    makeInstructions(dirLst, tr_main);
  }

  private void makeInstructions(ArrayList<Direction> dirLst, JFXTreeView tr) {
    TreeItem<Direction> root = new TreeItem<>();
    TreeItem<Direction> floor = new TreeItem<>();
    TreeItem<Direction> instruction;
    for (Direction dir : dirLst) {
      ImageView img = new ImageView(singleton.mapImageLoader.getIcon(dir.getIcon()));
      img.setFitWidth(25);
      img.setFitHeight(25);
      if (dir.getLevel() == Level.FLOOR) {
        floor = new TreeItem<>(dir);
        floor.setExpanded(false);
        root.getChildren().add(floor);
      } else if (dir.getLevel() == Level.STEP) {
        instruction = new TreeItem<>(dir, img);
        floor.getChildren().add(instruction);
      }
    }
    root.getChildren().removeIf(n -> n.getChildren().size() == 0);
    if (root.getChildren().size() > 0) {
      root.getChildren().get(0).setExpanded(true);
    }
    tr.setRoot(root);
    tr.setShowRoot(false);
    tr.getStylesheets()
        .add(getClass().getResource("/edu/wpi/N/css/newMapDisplay.css").toExternalForm());
    tr.getStyleClass().add("tree-view");
    tr_faulkner.getSelectionModel().select(1);
  }

  public void setDriveText(ArrayList<Direction> dir) {}

  public void setTabFocus(int floor, String building) {
    if (building.equals("Faulkner")) {
      if (tbpn_directions.getTabs().contains(tb_faulkner)) {
        tbpn_directions.getSelectionModel().select(tb_faulkner);
        tr_faulkner.getSelectionModel().select(1);
      }
    } else if (building.equals("Main")) {
      if (tbpn_directions.getTabs().contains(tb_main)) {
        tbpn_directions.getSelectionModel().select(tb_main);
        tr_main.getSelectionModel().select(1);
      }
    } else if (building.equals("Drive")) {
      if (tbpn_directions.getTabs().contains(tb_drive)) {
        tbpn_directions.getSelectionModel().select(tb_drive);
        tr_drive.getSelectionModel().select(1);
      }
    }
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
