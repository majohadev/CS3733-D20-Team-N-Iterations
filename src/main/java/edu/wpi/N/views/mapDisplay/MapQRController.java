package edu.wpi.N.views.mapDisplay;

import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTreeCell;
import com.jfoenix.controls.JFXTreeView;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.Direction;
import edu.wpi.N.algorithms.Directions;
import edu.wpi.N.algorithms.Level;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

  public synchronized void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public synchronized void setMapBaseController(MapBaseController mapBaseController) {
    this.mapBaseController = mapBaseController;
  }

  public void setMapDisplayController(NewMapDisplayController mapDisplayController) {
    this.mapDisplayController = mapDisplayController;
  }

  /**
   * Executes when the user wishes to see the previous instruction
   *
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
   *
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
      DbNode prevNode = currentDirection.getValue().getNode();
      currentDirection = (TreeItem<Direction>) tr.getSelectionModel().getSelectedItem();
      DbNode node = currentDirection.getValue().getNode();
      //      if (node.getFloor() != prevNode.getFloor() && )
      DbNode currentNode = currentDirection.getValue().getNode();
      if (currentNode.getFloor() != prevNode.getFloor() || changedBuilding(prevNode, currentNode)) {
        mapDisplayController.changeFloor(currentNode.getFloor(), currentNode.getBuilding());
      }
      if (currentDirection.getValue().getLevel() != Level.BUILDING
          && currentDirection.getValue().getLevel() != Level.FLOOR) {
        mapBaseController.autoFocusToNode(node);
      }
    }
  }

  public boolean changedBuilding(DbNode n, DbNode m) {
    boolean isFirstFaulkner = n.getBuilding().equals("Faulkner");
    boolean isSecondFaulkner = m.getBuilding().equals("Faulkner");
    return isFirstFaulkner ^ isSecondFaulkner;
  }
  /**
   * Executes when the user wishes to see the next instruction
   *
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
   *
   * @param tr the current tree
   * @param root the root of the current tree
   * @param tb the current tab
   * @throws DBException
   */
  public void handleBtnNextClicked(JFXTreeView tr, TreeItem<Direction> root, Tab tb)
      throws DBException {
    TreeItem<Direction> oldDirection = currentDirection; // the previous direction
    int oldIndex = tr.getSelectionModel().getSelectedIndex(); // the index of the previous direction
    tr.getSelectionModel().select(oldIndex + 1); // select the next instruction
    currentDirection =
        (TreeItem<Direction>) tr.getSelectionModel().getSelectedItem(); // the new direction
    if (currentDirection == oldDirection) { // if we have reached the bottom of the tree
      if (tbpn_directions.getTabs().get(tbpn_directions.getTabs().size() - 1)
          != tb) { // and we have not reached the bottom of the
        tbpn_directions
            .getSelectionModel()
            .select(
                tbpn_directions
                        .getTabs()
                        .indexOf(tbpn_directions.getSelectionModel().getSelectedItem())
                    + 1);
      } else {
        tbpn_directions.getSelectionModel().select(0);
        mapDisplayController.changeFloor(
            currentDirection.getValue().getNode().getFloor(),
            currentDirection.getValue().getNode().getBuilding());
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
      if (currentDirection.getValue().getLevel() != Level.BUILDING
          && currentDirection.getValue().getLevel() != Level.FLOOR) {
        DbNode node = currentDirection.getValue().getNode();
        mapBaseController.autoFocusToNode(node);
      } else {
        mapBaseController.autoFocusToNodesGroup();
      }
      if (currentDirection.getValue().getLevel() == Level.FLOOR) {
        int i = root.getChildren().indexOf(currentDirection);
        root.getChildren().get(i).setExpanded(true);
        mapBaseController.resetFocus();
        mapDisplayController.changeFloor(
            currentDirection.getValue().getNode().getFloor(),
            currentDirection.getValue().getNode().getBuilding());
      }
    }
  }

  public void onChangeTab() {
    tbpn_directions
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            new ChangeListener<Tab>() {
              @Override
              public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                if (t1 == tb_faulkner) {
                  try {
                    onFaulknerTabSelected();
                  } catch (DBException e) {
                    e.printStackTrace();
                  }
                } else if (t1 == tb_main) {
                  try {
                    onMainTabSelected();
                  } catch (DBException e) {
                    e.printStackTrace();
                  }
                } else if (t1 == tb_drive) {
                  onDriveTabSelected();
                }
              }
            });
  }

  public void initialize() {
    onChangeTab();
  }

  /**
   * Executes when the faulkner building is manually selected by the user
   *
   * @throws DBException
   */
  public void onFaulknerTabSelected() throws DBException {
    if (tbpn_directions.getSelectionModel().getSelectedItem() != tb_faulkner) {
      return;
    }
    tr_faulkner.getSelectionModel().select(0);
    currentDirection = (TreeItem<Direction>) tr_faulkner.getSelectionModel().getSelectedItem();
    if (currentDirection != null) {
      mapDisplayController.switchHospitalView();
      mapBaseController.resetFocus();
      mapDisplayController.changeFloor(
          currentDirection.getValue().getNode().getFloor(), "Faulkner");
    }
    try {
      tr_faulkner.getTreeItem(0).setExpanded(true);
      // onFaulknerTreeClicked();
    } catch (NullPointerException e) {
      return;
    }
  }

  /**
   * Executes when the main building tab is manually selected by the user
   *
   * @throws DBException
   */
  public void onMainTabSelected() throws DBException {
    if (tbpn_directions.getSelectionModel().getSelectedItem() != tb_main) {
      return;
    }

    tr_main.getSelectionModel().select(0);
    currentDirection = (TreeItem<Direction>) tr_main.getSelectionModel().getSelectedItem();
    if (currentDirection != null) {
      mapDisplayController.switchHospitalView();
      //      mapBaseController.setFloor("Main", currentDirection.getValue().getNode().getFloor(),
      // path);
      mapBaseController.resetFocus();
      mapDisplayController.changeFloor(currentDirection.getValue().getNode().getFloor(), "Main");
    }
    try {
      tr_main.getTreeItem(0).setExpanded(true);
      // onMainTreeClicked();
    } catch (NullPointerException e) {
      return;
    }
  }

  /** Executes when the drive tab is manually selected by the user */
  public void onDriveTabSelected() {
    if (tbpn_directions.getSelectionModel().getSelectedItem() != tb_drive) {
      return;
    }
    tr_drive.getSelectionModel().select(0);
    mapDisplayController.switchGoogleView();
    mapDisplayController.setFloorBuildingText(0, "Drive");
    try {
      // onDriveTreeClicked();
    } catch (NullPointerException e) {
      return;
    }
  }

  /**
   * Orders the tabs based on the path
   *
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
   *
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
   *
   * @param dirLst the textual directions for the faulkner building
   */
  public void setFaulknerText(ArrayList<Direction> dirLst) {
    faulknerPath = dirLst;
    makeInstructions(dirLst, tr_faulkner, rootFaulkner);
  }

  /**
   * populates the textual directions for the main building
   *
   * @param dirLst the textual directions for the main building
   */
  public void setMainText(ArrayList<Direction> dirLst) {
    mainPath = dirLst;
    makeInstructions(dirLst, tr_main, rootMain);
  }

  /**
   * populates the textual directions for the google maps
   *
   * @param dirList the textual directions for the google map
   */
  public void setDriveText(ArrayList<Direction> dirList) {
    makeInstructions(dirList, tr_drive, rootDrive);
  }

  /**
   * An event handler for clicking on a cell. Switches to the map of the cell This is where you
   * would want to put the zoom functionality
   */
  private static class CellClicked implements EventHandler<MouseEvent> {
    private MapQRController controller;
    private DirectionCell cell;

    public CellClicked(MapQRController controller, DirectionCell cell) {
      super();
      this.controller = controller;
      this.cell = cell;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
      controller.currentDirection = cell.getTreeItem();
      if (controller.currentDirection.getChildren().size() != 0)
        controller.currentDirection.setExpanded(
            !controller.currentDirection.expandedProperty().getValue());
      if (cell.getItem().getLevel() == Level.BUILDING) return;
      DbNode node = cell.getItem().getNode();
      try {
        controller.mapDisplayController.changeFloor(node.getFloor(), node.getBuilding());
        if (cell.getItem().getLevel() != Level.BUILDING && cell.getItem().getLevel() != Level.FLOOR)
          controller.mapBaseController.autoFocusToNode(node);
        else {
          controller.mapBaseController.autoFocusToNodesGroup();
        }
      } catch (DBException e) {
        e.printStackTrace();
      } catch (NullPointerException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Static class for a special kind of JFXTreeCell used to show directions. Don't need to provide
   * an image and automatically sets up on click
   */
  private static class DirectionCell extends JFXTreeCell<Direction> {
    private StateSingleton singleton;
    private StackPane hoverPane = new StackPane();

    public DirectionCell(StateSingleton singleton, MapQRController controller) {
      super();
      hoverPane.getStyleClass().add("hover-bar");
      hoverPane.setBackground(
          new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
      hoverPane.setPrefWidth(3);
      hoverPane.setMouseTransparent(true);
      this.singleton = singleton;
      this.setOnMouseClicked(new CellClicked(controller, this));
      this.setOnMouseEntered(mouseEvent -> hoverPane.setVisible(true));
      this.setOnMouseExited(mouseEvent -> hoverPane.setVisible(false));
    };

    @Override
    protected void updateItem(Direction item, boolean empty) {
      super.updateItem(item, empty);
      if (item == null || empty) {
        setText("");
        setGraphic(null);
      } else {
        setText(item.toString());
        if (item.getLevel() != Level.FLOOR) {
          ImageView img = new ImageView(singleton.mapImageLoader.getIcon(item.getIcon()));
          img.setFitWidth(25); // get the image from the Direction and set it up
          img.setFitHeight(25);
          setGraphic(img);
          Text reference = new Text(getText());
          reference.setFont(this.getFont());
          setPrefHeight(
              50
                  + (25
                      * Math.floor(
                          reference.getBoundsInLocal().getWidth()
                              / 300))); // calculate the height of the cell
        } else {
          setGraphic(null); // if null, just use computed size and set graphic to null
          setPrefHeight(USE_COMPUTED_SIZE);
        }
      }
    }

    /** Override layoutChildren to contain the hoverpane. */
    @Override
    protected void layoutChildren() {
      super.layoutChildren();
      if (!getChildren().contains(hoverPane)) {
        getChildren().add(0, hoverPane);
      }
      hoverPane.resizeRelocate(0, 0, hoverPane.prefWidth(USE_COMPUTED_SIZE), getHeight());
      hoverPane.setVisible(false);
    }
  }

  /**
   * initial population of textual directions
   *
   * @param dirLst the list of all textual directions
   * @param tr the current tree which will be populated with the textual directions
   * @param root the root of the current tree
   */
  private void makeInstructions(
      ArrayList<Direction> dirLst, JFXTreeView<Direction> tr, TreeItem<Direction> root) {
    MapQRController controller = this;
    tr.setCellFactory( // set the cellFactory to use our DirectionCells
        item -> new DirectionCell(singleton, controller));
    TreeItem<Direction> floor = new TreeItem<>();
    TreeItem<Direction> instruction;
    for (Direction dir : dirLst) {;
      //      ImageView img = new ImageView(singleton.mapImageLoader.getIcon(dir.getIcon()));
      //      img.setFitWidth(25);
      //      img.setFitHeight(25);
      if (dir.getLevel() == Level.FLOOR || dir.getLevel() == Level.BUILDING) {
        floor = new TreeItem<>(dir);
        floor.setExpanded(false);
        root.getChildren().add(floor);
      } else if (dir.getLevel() == Level.STEP || dir.getLevel() == Level.DRIVING) {
        instruction = new TreeItem<>(dir);
        floor.getChildren().add(instruction);
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
   *
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

  /** Collapses all entries in faulkner and main tab */
  public void collapseAllItems() {
    collapseFaulkner();
    collapseMain();
  }

  /** Collapses all entries in the faulkner tab */
  public void collapseFaulkner() {
    for (int i = 0; i < rootFaulkner.getChildren().size(); i++) {
      rootFaulkner.getChildren().get(i).setExpanded(false);
    }
  }

  /** Collapses all entries in the main tab */
  public void collapseMain() {
    for (int i = 0; i < rootMain.getChildren().size(); i++) {
      rootMain.getChildren().get(i).setExpanded(false);
    }
  }

  /** Function displays a pop-up window with user's directions */
  @FXML
  private void displayQRCode() throws IOException {
    try {
      Stage stage = new Stage();
      Parent root;
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("qrPopUp.fxml"));
      root = loader.load();
      Scene scene = new Scene(root);
      stage.setScene(scene);

      // Creates a QR code for the directions in the current tab
      QrPopUpController controller = (QrPopUpController) loader.getController();
      ArrayList<Direction> directions;
      if (tbpn_directions.getSelectionModel().getSelectedItem().equals(tb_faulkner)) {
        directions = faulknerPath;
      } else if (tbpn_directions.getSelectionModel().getSelectedItem().equals(tb_main)) {
        directions = mainPath;
      } else {
        directions = Directions.getGoogleDirections(getNecessaryGoogleRequestUrl("Driving"));
      }

      // Attempts to merge all of the directions together into one QR code, but
      // the text tends to be way too big for a single QR code
      //      String start = path.getPath().getFirst().getBuilding();
      //      String end = path.getPath().getLast().getBuilding();
      //      ArrayList<Direction> drivePath =
      //          Directions.getGoogleDirections(getNecessaryGoogleRequestUrl("Driving"));
      //      if (start.equals(end)) {
      //        if (start.equals("Faulkner")) {
      //          directions = faulknerPath;
      //        } else {
      //          directions = mainPath;
      //        }
      //      } else {
      //        if (start.equals("Faulkner")) {
      //          directions = faulknerPath;
      //          directions.addAll(drivePath);
      //          directions.addAll(mainPath);
      //        } else {
      //          directions = mainPath;
      //          directions.addAll(drivePath);
      //          directions.addAll(faulknerPath);
      //        }
      //      }

      controller.displayQrCode(directions);

      stage.initModality(Modality.APPLICATION_MODAL);
      stage.show();
    } catch (Exception ex) {
      ex.printStackTrace();
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText("QR code with directions could not be generated");
      errorAlert.showAndWait();
    }
  }

  /**
   * Gets the Google API request URL for making the API call based on Starting exit and Goal
   * Entrance
   *
   * @param mode
   * @return
   */
  private String getNecessaryGoogleRequestUrl(String mode) {
    String url = "";

    boolean isFirstFaulkner = this.path.get(0).getBuilding().equals("Faulkner");
    boolean isSecondFaulkner = this.path.get(path.size() - 1).getBuilding().equals("Faulkner");

    if (isFirstFaulkner ^ isSecondFaulkner) {
      if (isFirstFaulkner) {
        // Default Faulkner -> Francis
        url =
            "https://maps.googleapis.com/maps/api/directions/json?mode="
                + mode
                + "&origin=42.301213,-71.127795"
                + "&destination=Brigham+and+Women's+Hospital:+Spiegel+Joan+H+MD,+45+Francis+St+%23+D,+Boston,+MA+02115"
                + "&key=AIzaSyDx7BSweq5dRzXavs1vxuMWeR2ETMR6b3Q";

        // Identify which 'entrance' to generate text dirs for, use default if not found
        for (DbNode node : this.path.getPath()) {
          if (node.getNodeType().equals("EXIT")) {
            if (node.getNodeID().equals("GEXIT001L1")) {
              // FaulknerToShapiroFenwood
              url =
                  "https://maps.googleapis.com/maps/api/directions/json?mode="
                      + mode
                      + "&origin=42.301213,-71.127795"
                      + "&destination=42.335505,-71.108191"
                      + "&key=AIzaSyDx7BSweq5dRzXavs1vxuMWeR2ETMR6b3Q";

            } else if (node.getNodeID().equals("AEXIT0010G")) {
              // FaulknerToBTMFenwood
              url =
                  "https://maps.googleapis.com/maps/api/directions/json?mode="
                      + mode
                      + "&origin=42.301213,-71.127795"
                      + "&destination=42.335425,-71.108247"
                      + "&key=AIzaSyDx7BSweq5dRzXavs1vxuMWeR2ETMR6b3Q";

            } else if (node.getNodeID().equals("GEXIT00101")) {
              // FaulknerToShapiroFrancis
              url =
                  "https://maps.googleapis.com/maps/api/directions/json?mode="
                      + mode
                      + "&origin=42.301213,-71.127795"
                      + "&destination=42.335863,-71.107704"
                      + "&key=AIzaSyDx7BSweq5dRzXavs1vxuMWeR2ETMR6b3Q";

            } else if (node.getNodeID().equals("FEXIT00201")) {
              // FaulknerToTower75Francis
              url =
                  "https://maps.googleapis.com/maps/api/directions/json?mode="
                      + mode
                      + "&origin=42.301213,-71.127795"
                      + "&destination=75+Francis+St+Boston+MA+02115"
                      + "&key=AIzaSyDx7BSweq5dRzXavs1vxuMWeR2ETMR6b3Q";

            } else if (node.getNodeID().equals("XEXIT00202")) {
              // FaulknerToFLEX
              url =
                  "https://maps.googleapis.com/maps/api/directions/json?mode="
                      + mode
                      + "&origin=42.301213,-71.127795"
                      + "&destination=42.335078,-71.106326"
                      + "&key=AIzaSyDx7BSweq5dRzXavs1vxuMWeR2ETMR6b3Q";
            }
          }
        }
      } else {
        // Default Francis -> Faulkner
        url =
            "https://maps.googleapis.com/maps/api/directions/json?mode="
                + mode
                + "&origin=Brigham+and+Women's+Hospital:+Spiegel+Joan+H+MD,+45+Francis+St+%23+D,+Boston,+MA+02115"
                + "&destination=42.301213,-71.127795"
                + "&key=AIzaSyDx7BSweq5dRzXavs1vxuMWeR2ETMR6b3Q";

        // Identify which 'exit' to generate text dirs for, use default if not found
        for (DbNode node : this.path.getPath()) {
          if (node.getNodeType().equals("EXIT")) {
            if (node.getNodeID().equals("GEXIT001L1")) {
              // ShapiroFenwoodToFaulkner
              url =
                  "https://maps.googleapis.com/maps/api/directions/json?mode="
                      + mode
                      + "&origin=42.335505,-71.108191"
                      + "&destination=42.301213,-71.127795"
                      + "&key=AIzaSyDx7BSweq5dRzXavs1vxuMWeR2ETMR6b3Q";

            } else if (node.getNodeID().equals("AEXIT0010G")) {
              // BTMFenwoodToFaulkner
              url =
                  "https://maps.googleapis.com/maps/api/directions/json?mode="
                      + mode
                      + "&origin=42.335425,-71.108247"
                      + "&destination=42.301213,-71.127795"
                      + "&key=AIzaSyDx7BSweq5dRzXavs1vxuMWeR2ETMR6b3Q";

            } else if (node.getNodeID().equals("GEXIT00101")) {
              // ShapiroFrancisToFaulkner
              url =
                  "https://maps.googleapis.com/maps/api/directions/json?mode="
                      + mode
                      + "&origin=42.335863,-71.107704"
                      + "&destination=42.301213,-71.127795"
                      + "&key=AIzaSyDx7BSweq5dRzXavs1vxuMWeR2ETMR6b3Q";

            } else if (node.getNodeID().equals("FEXIT00201")) {
              // Tower75FrancisToFaulkner
              url =
                  "https://maps.googleapis.com/maps/api/directions/json?mode="
                      + mode
                      + "&origin=75+Francis+St+Boston+MA+02115"
                      + "&destination=42.301213,-71.127795"
                      + "&key=AIzaSyDx7BSweq5dRzXavs1vxuMWeR2ETMR6b3Q";

            } else if (node.getNodeID().equals("XEXIT00202")) {
              // FLEXToFaulkner
              url =
                  "https://maps.googleapis.com/maps/api/directions/json?mode="
                      + mode
                      + "&origin=42.335078,-71.106326"
                      + "&destination=42.301213,-71.127795"
                      + "&key=AIzaSyDx7BSweq5dRzXavs1vxuMWeR2ETMR6b3Q";
            }
          }
        }
      }
    }

    return url;
  }
}
