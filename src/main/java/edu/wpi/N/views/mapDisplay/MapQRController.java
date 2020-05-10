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

  ArrayList<Direction> faulknerPath = new ArrayList<>();
  ArrayList<Direction> mainPath = new ArrayList<>();

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
    faulknerPath = dirLst;
    makeInstructions(dirLst, tr_faulkner);
  }

  public void setMainText(ArrayList<Direction> dirLst) {
    mainPath = dirLst;
    makeInstructions(dirLst, tr_main);
  }

  public void setDriveText(ArrayList<Direction> dirList) {
    System.out.println(dirList.size());
    makeInstructions(dirList, tr_drive);
  }

  private void makeInstructions(ArrayList<Direction> dirLst, JFXTreeView tr) {
    TreeItem<Direction> root = new TreeItem<>();
    TreeItem<Direction> floor = new TreeItem<>();
    TreeItem<Direction> instruction;
    for (Direction dir : dirLst) {;
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
      } else if (dir.getLevel() == Level.BUILDING) {
        floor = new TreeItem<>(dir, img);
        floor.setExpanded(false);
        root.getChildren().add(floor);
      }
    }
    if (tr != tr_drive) {
      root.getChildren().removeIf(n -> n.getChildren().size() == 0);
    }
    if (root.getChildren().size() > 0) {
      root.getChildren().get(0).setExpanded(true);
    }
    tr.setRoot(root);
    tr.setShowRoot(false);
    tr.getStylesheets()
        .add(getClass().getResource("/edu/wpi/N/css/newMapDisplay.css").toExternalForm());
    tr.getStyleClass().add("tree-view");
    tr.getSelectionModel().select(1);
  }

  public void setTabFocus(int floor, String building) {
    if (building.equals("Faulkner")) {
      if (tbpn_directions.getTabs().contains(tb_faulkner)) {
        tbpn_directions.getSelectionModel().select(tb_faulkner);
        setIntructionFocus(floor, faulknerPath, tr_faulkner);
      }
    } else if (building.equals("Main")) {
      if (tbpn_directions.getTabs().contains(tb_main)) {
        tbpn_directions.getSelectionModel().select(tb_main);
        setIntructionFocus(floor, mainPath, tr_main);
      }
    } else if (building.equals("Drive")) {
      if (tbpn_directions.getTabs().contains(tb_drive)) {
        tbpn_directions.getSelectionModel().select(tb_drive);
        tr_drive.getSelectionModel().select(0);
      }
    }
  }

  public void setIntructionFocus(int floor, ArrayList<Direction> path, JFXTreeView tr) {
    int floorStep = 0;
    for (int i = 0; i < path.size(); i++) {
      if (i > 2 && (path.get(i - 1).getNode().getFloor() != path.get(i).getNode().getFloor())) {
        floorStep++;
      }
      if (path.get(i).getNode().getFloor() == floor) {
        tr.getTreeItem(floorStep).setExpanded(true);
        tr.getSelectionModel().select(i - 1);
        System.out.println(i - 1);
        return;
      }
    }
  }

  public void setMainInstructionFocus(int floor) {
    System.out.println(floor);
    for (int i = 0; i < mainPath.size(); i++) {
      if (mainPath.get(i).getNode().getFloor() == floor) {
        tr_main.getSelectionModel().clearAndSelect(i + 1);
        tr_main.getTreeItem(i).setExpanded(true);
        return;
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
