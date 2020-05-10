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

  /**
   * Executes when the user wishes to see the previous instruction
   * @throws DBException
   */
  public void onBtnPrevClicked() throws DBException {
    if (tb_faulkner.isSelected()) {
      handleBtnPrevClicked(tr_faulkner, rootFaulkner, tb_faulkner);
    } else if (tb_drive.isSelected()) {
      handleBtnPrevClicked(tr_drive, rootDrive, tb_drive);
    } else if (tb_main.isSelected()) {
      handleBtnPrevClicked(tr_main, rootMain, tb_main);
    }
  }

  /**
   * Executes when the user wishes to see the previous instruction
   * @param tr the current tree
   * @param root the root of the current tree
   * @param tb the current tab
   * @throws DBException
   */
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

  /**
   * Executes when the user wishes to see the next instruction
   * @throws DBException
   */
  public void onBtnNextClicked() throws DBException {
    if (tb_faulkner.isSelected()) {
      handleBtnNextClicked(tr_faulkner, rootFaulkner, tb_faulkner);
    } else if (tb_drive.isSelected()) {
      handleBtnNextClicked(tr_drive, rootDrive, tb_drive);
    } else if (tb_main.isSelected()) {
      handleBtnNextClicked(tr_main, rootMain, tb_main);
    }
  }

  /**
   * Executes when the user wishes to see the next instruction
   * @param tr the current tree
   * @param root the root of the current tree
   * @param tb the current tab
   * @throws DBException
   */
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

  /**
   * Executes when the user clicks on an item in the faulkner building tree
   * @throws DBException
   */
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

  /**
   * Executes when the user clicks on an item in the driving directions tree
   */
  public void onDriveTreeClicked() {
    collapseAllItems();
    tr_main.getSelectionModel().clearSelection();
    tr_faulkner.getSelectionModel().clearSelection();
    currentDirection = (TreeItem<Direction>) tr_drive.getSelectionModel().getSelectedItem();
  }

  /**
   * Executes when the user clicks on an item in the main building tree
   * @throws DBException
   */
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

  /**
   * Executes when the faulkner building is manually selected by the user
   * @throws DBException
   */
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

  /**
   * Executes when the main building tab is manually selected by the user
   * @throws DBException
   */
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

  /**
   * Executes when the drive tab is manually selected by the user
   */
  public void onDriveTabSelected() {
    tr_drive.getSelectionModel().select(0);
    mapDisplayController.switchGoogleView();
    try {
      onDriveTreeClicked();
    } catch (NullPointerException e) {
      return;
    }
  }

  /**
   * Orders the tabs based on the path
   * @param path the path finding path
   */
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

  /**
   * adds a corresponding tab depending on the building name
   * @param b the building name
   */
  private void addTabs(String b) {
    if (b.equals("Faulkner")) {
      tbpn_directions.getTabs().add(tb_faulkner);
    } else {
      tbpn_directions.getTabs().add(tb_main);
    }
  }

  /**
   * populates the textual directions for the faulkner building
   * @param dirLst the textual directions for the faulkner building
   */
  public void setFaulknerText(ArrayList<Direction> dirLst) {
    faulknerPath = dirLst;
    makeInstructions(dirLst, tr_faulkner, rootFaulkner);
  }

  /**
   * populates the textual directions for the main building
   * @param dirLst the textual directions for the main building
   */
  public void setMainText(ArrayList<Direction> dirLst) {
    mainPath = dirLst;
    makeInstructions(dirLst, tr_main, rootMain);
  }

  /**
   * populates the textual directions for the google maps
   * @param dirList the textual directions for the google map
   */
  public void setDriveText(ArrayList<Direction> dirList) {
    makeInstructions(dirList, tr_drive, rootDrive);
  }

  /**
   * initial population of textual directions
   * @param dirLst the list of all textual directions
   * @param tr the current tree which will be populated with the textual directions
   * @param root the root of the current tree
   */
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
  }

  /**
   * changes the tab depending on the building
   * @param floor the floor to be focused on
   * @param building the building to be focused on
   */
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

  /**
   *
   * @param floor the floor of the current instructions
   * @param root the root of the current treeview
   * @param tr the current treeview
   */
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

  /**
   * Collapses all entries in faulkner and main tab
   */
  public void collapseAllItems() {
    collapseFaulkner();
    collapseMain();
  }

  /**
   * Collapses all entries in the faulkner tab
   */
  public void collapseFaulkner() {
    for (int i = 0; i < rootFaulkner.getChildren().size(); i++) {
      rootFaulkner.getChildren().get(i).setExpanded(false);
    }
  }

  /**
   * Collapses all entries in the main tab
   */
  public void collapseMain() {
    for (int i = 0; i < rootMain.getChildren().size(); i++) {
      rootMain.getChildren().get(i).setExpanded(false);
    }
  }
}
