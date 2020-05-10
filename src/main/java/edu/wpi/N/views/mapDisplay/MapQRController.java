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
import javafx.scene.control.TreeView;
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
  TreeItem<Direction> rootFaulkner = new TreeItem<>();
  TreeItem<Direction> rootMain = new TreeItem<>();
  TreeItem<Direction> rootDrive = new TreeItem<>();

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
    makeInstructions(dirLst, tr_faulkner, rootFaulkner);
  }

  public void setMainText(ArrayList<Direction> dirLst) {
    mainPath = dirLst;
    makeInstructions(dirLst, tr_main, rootMain);
  }

  public void setDriveText(ArrayList<Direction> dirList) {
    System.out.println(dirList.size());
    makeInstructions(dirList, tr_drive, rootDrive);
  }

  private void makeInstructions(
      ArrayList<Direction> dirLst, JFXTreeView tr, TreeItem<Direction> root) {
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
    //    if (tr != tr_drive) {
    //      root.getChildren().removeIf(n -> n.getChildren().size() == 0);
    //    }
    if (root.getChildren().size() > 0) {
      root.getChildren().get(0).setExpanded(true);
    }
    tr.setRoot(root);
    tr.setShowRoot(false);
    tr.getStylesheets()
        .add(getClass().getResource("/edu/wpi/N/css/newMapDisplay.css").toExternalForm());
    tr.getStyleClass().add("tree-view");
    tr.getSelectionModel().select(0);
  }

  public void setTabFocus(int floor, String building) {
    if (building.equals("Faulkner")) {
      if (tbpn_directions.getTabs().contains(tb_faulkner)) {
        tbpn_directions.getSelectionModel().select(tb_faulkner);
        setIntructionFocus(floor, rootFaulkner, tr_faulkner);
      }
    } else if (building.equals("Main")) {
      if (tbpn_directions.getTabs().contains(tb_main)) {
        tbpn_directions.getSelectionModel().select(tb_main);
        setIntructionFocus(floor, rootMain, tr_main);
      }
    } else if (building.equals("Drive")) {
      if (tbpn_directions.getTabs().contains(tb_drive)) {
        tbpn_directions.getSelectionModel().select(tb_drive);
        tr_drive.getSelectionModel().select(0);
      }
    }
  }

  public void setIntructionFocus(int floor, TreeItem<Direction> root, TreeView<Direction> tr) {
    collapseAllItems();
    for (int i = 0; i < root.getChildren().size(); i++) {
      if (root.getChildren().get(i).getValue().getNode().getFloor() == floor) {
        root.getChildren().get(i).setExpanded(true);
        tr.getSelectionModel().clearAndSelect(i);
      }
    }
  }

  public void collapseAllItems() {
    for (int i = 0; i < rootFaulkner.getChildren().size(); i++) {
      rootFaulkner.getChildren().get(i).setExpanded(false);
    }
    for (int i = 0; i < rootMain.getChildren().size(); i++) {
      rootMain.getChildren().get(i).setExpanded(false);
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
