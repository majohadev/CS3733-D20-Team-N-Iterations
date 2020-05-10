package edu.wpi.N.views.mapDisplay;

import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTreeView;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.Direction;
import edu.wpi.N.algorithms.Level;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.Path;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.util.ArrayList;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class MapQRController implements Controller {
  private StateSingleton singleton;
  private MapBaseController mapBaseController;
  private NewMapDisplayController mapDisplayController;
  @FXML JFXTabPane tbpn_directions;
  @FXML Tab tb_faulkner;
  @FXML Tab tb_main;
  @FXML Tab tb_drive;
  @FXML JFXTreeView tr_faulkner;
  @FXML JFXTreeView tr_main;
  @FXML JFXTreeView tr_drive;
  @FXML Pane btn_prev;
  @FXML Pane btn_next;

  ArrayList<Direction> faulknerPath = new ArrayList<>();
  ArrayList<Direction> mainPath = new ArrayList<>();
  TreeItem<Direction> rootFaulkner = new TreeItem<>();
  TreeItem<Direction> rootMain = new TreeItem<>();
  TreeItem<Direction> rootDrive = new TreeItem<>();
  TreeItem<Direction> currentDirection = new TreeItem<>();
  Path path = new Path(new LinkedList<>());

  @Override
  public void setMainApp(App mainApp) {}

  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public void setMapBaseController(MapBaseController mapBaseController) {
    this.mapBaseController = mapBaseController;
  }

  public void setMapDisplayController(NewMapDisplayController mapDisplayController) {
    this.mapDisplayController = mapDisplayController;
  }

  public void onBtnPrevClicked() throws DBException {
    if (tb_faulkner.isSelected()) {
      handleBtnPrevClicked(tr_faulkner, rootFaulkner, tb_faulkner);
    } else if (tb_drive.isSelected()) {
      handleBtnPrevClicked(tr_drive, rootDrive, tb_drive);
    } else if (tb_main.isSelected()) {
      handleBtnPrevClicked(tr_main, rootMain, tb_main);
    }
  }

  public void handleBtnPrevClicked(JFXTreeView tr, TreeItem<Direction> root, Tab tb)
      throws DBException {
    if (tr.getSelectionModel().getSelectedItem() == root.getChildren().get(0)) {
      if (tbpn_directions.getTabs().indexOf(tb) != 0) {
        tbpn_directions
            .getSelectionModel()
            .select(
                tbpn_directions
                        .getTabs()
                        .indexOf(tbpn_directions.getSelectionModel().getSelectedItem())
                    - 1);
        if (tbpn_directions.getSelectionModel().getSelectedItem() == tb_drive) {
          onDriveTabSelected();
        } else if (tbpn_directions.getSelectionModel().getSelectedItem() == tb_faulkner) {
          onFaulknerTabSelected();
        } else if (tbpn_directions.getSelectionModel().getSelectedItem() == tb_main) {
          onMainTabSelected();
        }
      }
    } else {
      tr.getSelectionModel().select(tr.getSelectionModel().getSelectedIndex() - 1);
    }
  }

  public void onBtnNextClicked() throws DBException {
    if (tb_faulkner.isSelected()) {
      handleBtnNextClicked(tr_faulkner, rootFaulkner, tb_faulkner);
    } else if (tb_drive.isSelected()) {
      handleBtnNextClicked(tr_drive, rootDrive, tb_drive);
    } else if (tb_main.isSelected()) {
      handleBtnNextClicked(tr_main, rootMain, tb_main);
    }
  }

  public void handleBtnNextClicked(JFXTreeView tr, TreeItem<Direction> root, Tab tb)
      throws DBException {
    tr.getSelectionModel().select(tr.getSelectionModel().getSelectedIndex() + 1);
    if (tr.getSelectionModel().getSelectedItem() == currentDirection
        && tr.getSelectionModel().getSelectedIndex() != 1) {
      if (tbpn_directions.getTabs().get(tbpn_directions.getTabs().size() - 1) != tb) {
        tbpn_directions
            .getSelectionModel()
            .select(
                tbpn_directions
                        .getTabs()
                        .indexOf(tbpn_directions.getSelectionModel().getSelectedItem())
                    + 1);
      } else {
        tbpn_directions.getSelectionModel().select(0);
      }
      if (tbpn_directions.getSelectionModel().getSelectedItem() == tb_drive) {
        onDriveTabSelected();
      } else if (tbpn_directions.getSelectionModel().getSelectedItem() == tb_faulkner) {
        onFaulknerTabSelected();
      } else if (tbpn_directions.getSelectionModel().getSelectedItem() == tb_main) {
        onMainTabSelected();
      }
    } else {
      currentDirection = (TreeItem<Direction>) tr.getSelectionModel().getSelectedItem();
      if (currentDirection.getValue().getLevel() == Level.FLOOR) {
        int i = root.getChildren().indexOf(currentDirection);
        root.getChildren().get(i).setExpanded(true);
        mapBaseController.setFloor(
            currentDirection.getValue().getNode().getBuilding(),
            currentDirection.getValue().getNode().getFloor(),
            path);
      }
    }
  }

  public void initialize() {}

  public void onFaulknerTreeClicked() throws DBException {
    collapseMain();
    tr_main.getSelectionModel().clearSelection();
    tr_drive.getSelectionModel().clearSelection();
    currentDirection = (TreeItem<Direction>) tr_faulkner.getSelectionModel().getSelectedItem();
    mapBaseController.setFloor("Faulkner", currentDirection.getValue().getNode().getFloor(), path);
    if (currentDirection.getValue().getLevel() == Level.FLOOR) {
      tr_faulkner.getTreeItem(tr_faulkner.getSelectionModel().getSelectedIndex()).setExpanded(true);
    }
  }

  public void onDriveTreeClicked() {
    collapseAllItems();
    tr_main.getSelectionModel().clearSelection();
    tr_faulkner.getSelectionModel().clearSelection();
    currentDirection = (TreeItem<Direction>) tr_drive.getSelectionModel().getSelectedItem();
  }

  public void onMainTreeClicked() throws DBException {
    collapseFaulkner();
    tr_drive.getSelectionModel().clearSelection();
    tr_faulkner.getSelectionModel().clearSelection();
    currentDirection = (TreeItem<Direction>) tr_main.getSelectionModel().getSelectedItem();
    mapBaseController.setFloor("Main", currentDirection.getValue().getNode().getFloor(), path);
    if (currentDirection.getValue().getLevel() == Level.FLOOR) {
      tr_main.getTreeItem(tr_main.getSelectionModel().getSelectedIndex()).setExpanded(true);
    }
  }

  public void onFaulknerTabSelected() throws DBException {
    tr_faulkner.getSelectionModel().select(0);
    currentDirection = (TreeItem<Direction>) tr_faulkner.getSelectionModel().getSelectedItem();
    if (currentDirection != null) {
      mapDisplayController.switchHospitalView();
      mapBaseController.setFloor(
          "Faulkner", currentDirection.getValue().getNode().getFloor(), path);
    }
    try {
      tr_faulkner.getTreeItem(0).setExpanded(true);
      onFaulknerTreeClicked();
    } catch (NullPointerException e) {
      return;
    }
  }

  public void onMainTabSelected() throws DBException {
    tr_main.getSelectionModel().select(0);
    currentDirection = (TreeItem<Direction>) tr_main.getSelectionModel().getSelectedItem();
    if (currentDirection != null) {
      mapDisplayController.switchHospitalView();
      mapBaseController.setFloor("Main", currentDirection.getValue().getNode().getFloor(), path);
    }
    try {
      tr_main.getTreeItem(0).setExpanded(true);
      onMainTreeClicked();
    } catch (NullPointerException e) {
      return;
    }
  }

  public void onDriveTabSelected() {
    tr_drive.getSelectionModel().select(0);
    mapDisplayController.switchGoogleView();
    try {
      onDriveTreeClicked();
    } catch (NullPointerException e) {
      return;
    }
  }

  public void setTabs(Path path) {
    String start = path.getPath().getFirst().getBuilding();
    String end = path.getPath().getLast().getBuilding();
    this.path = path;
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
      } else if (dir.getLevel() == Level.STEP || dir.getLevel() == Level.DRIVING) {
        instruction = new TreeItem<>(dir, img);
        floor.getChildren().add(instruction);
      } else if (dir.getLevel() == Level.BUILDING) {
        floor = new TreeItem<>(dir, img);
        floor.setExpanded(false);
        root.getChildren().add(floor);
      }
    }
    if (root.getChildren().size() > 0) {
      root.getChildren().get(0).setExpanded(true);
    }
    tr.setRoot(root);
    tr.setShowRoot(false);
    tr.getStylesheets()
        .add(getClass().getResource("/edu/wpi/N/css/newMapDisplay.css").toExternalForm());
    tr.getStyleClass().add("tree-view");
    if (tbpn_directions.getTabs().get(0) == tb_faulkner) {
      tr_faulkner.getSelectionModel().select(0);
      currentDirection = (TreeItem<Direction>) tr_faulkner.getSelectionModel().getSelectedItem();
    } else {
      tr_main.getSelectionModel().select(0);
      currentDirection = (TreeItem<Direction>) tr_main.getSelectionModel().getSelectedItem();
    }
    //    makeIntructionsClickacle(tr);
  }

  //  public void makeIntructionsClickacle(JFXTreeView<Direction> tr) {
  //    tr.setCellFactory(
  //        tv -> {
  //          TreeCell<Direction> cell =
  //              new TreeCell<Direction>() {
  //                @Override
  //                public void updateItem(Direction dir, boolean empty) {
  //                  super.updateItem(dir, empty);
  //                  if (empty) {
  //                    setText(null);
  //                  } else {
  //                    setText(dir.toString());
  //                  }
  //                }
  //              };
  //          cell.setOnMouseClicked(e -> System.out.println("Hello"));
  //          return cell;
  //        });
  //  }

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
        currentDirection = (TreeItem<Direction>) tr_drive.getSelectionModel().getSelectedItem();
      }
    }
  }

  public void setIntructionFocus(int floor, TreeItem<Direction> root, TreeView<Direction> tr) {
    collapseAllItems();
    tr_faulkner.getSelectionModel().clearSelection();
    tr_main.getSelectionModel().clearSelection();
    tr_drive.getSelectionModel().clearSelection();
    for (int i = 0; i < root.getChildren().size(); i++) {
      if (root.getChildren().get(i).getValue().getNode().getFloor() == floor) {
        root.getChildren().get(i).setExpanded(true);
        tr.getSelectionModel().select(i);
        currentDirection = tr.getSelectionModel().getSelectedItem();
      }
    }
  }

  public void collapseAllItems() {
    collapseFaulkner();
    collapseMain();
  }

  public void collapseFaulkner() {
    for (int i = 0; i < rootFaulkner.getChildren().size(); i++) {
      rootFaulkner.getChildren().get(i).setExpanded(false);
    }
  }

  public void collapseMain() {
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
