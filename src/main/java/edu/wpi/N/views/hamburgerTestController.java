package edu.wpi.N.views;

import com.jfoenix.controls.*;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.Algorithm;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import lombok.SneakyThrows;

public class hamburgerTestController implements Controller, Initializable {

  private App mainApp = null;
  final float IMAGE_WIDTH = 2475;
  final float IMAGE_HEIGHT = 1485;
  final float MAP_WIDTH = 1678;
  final float MAP_HEIGHT = 1010;
  final float HORIZONTAL_SCALE = (MAP_WIDTH) / IMAGE_WIDTH;
  final float VERTICAL_SCALE = (MAP_HEIGHT) / IMAGE_HEIGHT;
  @FXML ImageView img_map;
  @FXML Pane pn_display;
  @FXML Pane pn_changeFloor;
  @FXML JFXTextField txt_firstLocation;
  @FXML JFXTextField txt_secondLocation;
  @FXML JFXListView lst_firstLocation;
  @FXML JFXListView lst_secondLocation;
  @FXML JFXButton btn_findPath;
  @FXML JFXButton btn_home;
  @FXML private JFXButton btn_floors, btn_floor1, btn_floor2, btn_floor3, btn_floor4, btn_floor5;
  @FXML TitledPane pn_locationSearch;
  @FXML Accordion acc_search;
  private final int DEFAULT_FLOOR = 1;
  private final String DEFAULT_BUILDING = "FAULKNER";
  private int currentFloor;
  private String currentBuilding;
  HashMap<String, DbNode> stringNodeConversion = new HashMap<>();
  LinkedList<String> allLongNames = new LinkedList<>();
  LinkedList<DbNode> allFloorNodes = new LinkedList<>();
  JFXNodesList nodesList;
  LinkedList<DbNode> pathNodes;
  String[] imgPaths =
      new String[] {
        "/edu/wpi/N/images/Floor1TeamN.png",
        "/edu/wpi/N/images/Floor2TeamN.png",
        "/edu/wpi/N/images/Floor3TeamN.png",
        "/edu/wpi/N/images/Floor4TeamN.png",
        "/edu/wpi/N/images/Floor5TeamN.png"
      };

  private enum Mode {
    NO_STATE,
    PATH_STATE;
  }

  Mode mode;

  @SneakyThrows
  @Override
  public void initialize(URL location, ResourceBundle resourceBundle) {
    initializeChangeFloorButtons();
    this.currentFloor = DEFAULT_FLOOR;
    this.currentBuilding = DEFAULT_BUILDING;
    this.mode = Mode.NO_STATE;
    this.allFloorNodes = MapDB.allNodes();
    initializeConversions();
    defaultKioskNode();
    acc_search.setExpandedPane(pn_locationSearch);
  }

  private void initializeConversions() {
    for (DbNode node : allFloorNodes) {
      stringNodeConversion.put(node.getLongName(), node);
      allLongNames.add(node.getLongName());
    }
  }

  public void onSearchFirstLocation(KeyEvent inputMethodEvent) throws DBException {
    LinkedList<DbNode> fuzzySearchNodeList;
    ObservableList<String> fuzzySearchTextList;
    LinkedList<String> fuzzySearchStringList = new LinkedList<>();

    String currentText = txt_firstLocation.getText();
    fuzzySearchNodeList = FuzzySearchAlgorithm.suggestLocations(currentText);
    if (fuzzySearchNodeList != null) {
      for (DbNode node : fuzzySearchNodeList) {
        fuzzySearchStringList.add(node.getLongName());
      }
      fuzzySearchTextList = FXCollections.observableList(fuzzySearchStringList);
    } else fuzzySearchTextList = FXCollections.observableList(this.allLongNames);
    lst_firstLocation.setItems(fuzzySearchTextList);
  }

  public void onSearchSecondLocation(KeyEvent inputMethodEvent) throws DBException {
    LinkedList<DbNode> fuzzySearchNodeList;
    ObservableList<String> fuzzySearchTextList;
    LinkedList<String> fuzzySearchStringList = new LinkedList<>();

    String currentText = txt_secondLocation.getText();
    fuzzySearchNodeList = FuzzySearchAlgorithm.suggestLocations(currentText);
    if (fuzzySearchNodeList != null) {
      for (DbNode node : fuzzySearchNodeList) {
        fuzzySearchStringList.add(node.getLongName());
      }
      fuzzySearchTextList = FXCollections.observableList(fuzzySearchStringList);
    } else fuzzySearchTextList = FXCollections.observableList(this.allLongNames);
    lst_secondLocation.setItems(fuzzySearchTextList);
  }

  public void onBtnPathfindClicked(MouseEvent event) throws Exception {
    this.mode = Mode.PATH_STATE;
    pn_display.getChildren().removeIf(node -> node instanceof Line);
    enableAllFloorButtons();
    String firstSelection = (String) lst_firstLocation.getSelectionModel().getSelectedItem();
    String secondSelection = (String) lst_secondLocation.getSelectionModel().getSelectedItem();
    jumpToFloor(imgPaths[stringNodeConversion.get(firstSelection).getFloor() - 1]);
    currentFloor = stringNodeConversion.get(firstSelection).getFloor();
    try {
      findPath(stringNodeConversion.get(firstSelection), stringNodeConversion.get(secondSelection));
    } catch (NullPointerException e) {
      displayErrorMessage("Path does not exist");
      return;
    }
  }

  private void enableAllFloorButtons() {
    for (int i = 1; i < nodesList.getChildren().size(); i++) {
      JFXButton btn = (JFXButton) nodesList.getChildren().get(i);
      btn.setDisable(false);
    }
  }

  private void findPath(DbNode node1, DbNode node2) throws DBException {
    if (node1.getFloor() <= node2.getFloor()) {
      Path path;
      Algorithm myAStar = new Algorithm();
      try {
        path = myAStar.findPath(node1, node2, false);
      } catch (NullPointerException e) {
      } catch (NullPointerException e) {
        displayErrorMessage("The path does not exist");
        return;
      }
      pathNodes = path.getPath();
    } else {
      Algorithm myAStar = new Algorithm();
      Path path = myAStar.findPath(node2, node1, false);
      pathNodes = path.getPath();
    }
    disableNonPathFloors(pathNodes);
    drawPath(pathNodes);
  }

  private void disableNonPathFloors(LinkedList<DbNode> pathNodes) {
    LinkedList<Integer> activeFloors = new LinkedList();
    for (DbNode node : pathNodes) {
      if (!(node.getNodeType().equals("ELEV") || node.getNodeType().equals("STAI"))) {
        if (!activeFloors.contains(node.getFloor())) {
          activeFloors.add(node.getFloor());
        }
      }
    }
    for (int i = 1; i < nodesList.getChildren().size(); i++) {
      JFXButton btn = (JFXButton) nodesList.getChildren().get(i);
      if (!activeFloors.contains(Integer.parseInt(btn.getText()))) {
        btn.setDisable(true);
      }
    }
  }

  private void drawPath(LinkedList<DbNode> pathNodes) {
    DbNode firstNode;
    DbNode secondNode;
    for (int i = 0; i < pathNodes.size() - 1; i++) {
      firstNode = pathNodes.get(i);
      secondNode = pathNodes.get(i + 1);
      if (firstNode.getFloor() == currentFloor && secondNode.getFloor() == currentFloor) {
        Line line =
            new Line(
                scaleX(firstNode.getX()),
                scaleY(firstNode.getY()),
                scaleX(secondNode.getX()),
                scaleY(secondNode.getY()));
        line.setStrokeWidth(5);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        pn_display.getChildren().add(line);
      }
    }
  }

  public void onBtnResetPathClicked() throws DBException {
    mode = Mode.NO_STATE;
    defaultKioskNode();
    enableAllFloorButtons();
    pn_display.getChildren().removeIf(node -> node instanceof Line);
    txt_firstLocation.clear();
    txt_secondLocation.clear();
    lst_firstLocation.getItems().clear();
    lst_secondLocation.getItems().clear();
  }

  private double scaleX(double x) {
    return x * HORIZONTAL_SCALE;
  }

  private double scaleY(double y) {
    return y * VERTICAL_SCALE;
  }

  public void initializeChangeFloorButtons() {
    btn_floors = new JFXButton("Floors");
    btn_floor1 = new JFXButton("1");
    btn_floor2 = new JFXButton("2");
    btn_floor3 = new JFXButton("3");
    btn_floor4 = new JFXButton("4");
    btn_floor5 = new JFXButton("5");
    btn_floors.setButtonType(JFXButton.ButtonType.RAISED);
    btn_floor1.setButtonType(JFXButton.ButtonType.RAISED);
    btn_floor2.setButtonType(JFXButton.ButtonType.RAISED);
    btn_floor3.setButtonType(JFXButton.ButtonType.RAISED);
    btn_floor4.setButtonType(JFXButton.ButtonType.RAISED);
    btn_floor5.setButtonType(JFXButton.ButtonType.RAISED);
    btn_floors
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floors.getStyleClass().addAll("animated-option-button");
    btn_floor1
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floor1.getStyleClass().addAll("animated-option-button");
    btn_floor1.setOnMouseClicked(
        e -> {
          currentFloor = 1;
          try {
            setFloorImg("/edu/wpi/N/images/Floor1TeamN.png");
          } catch (DBException ex) {
            ex.printStackTrace();
          }
        });
    btn_floor2
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floor2.getStyleClass().addAll("animated-option-button");
    btn_floor2.setOnMouseClicked(
        e -> {
          currentFloor = 2;
          try {
            setFloorImg("/edu/wpi/N/images/Floor2TeamN.png");
          } catch (DBException ex) {
            ex.printStackTrace();
          }
        });
    btn_floor3
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floor3.getStyleClass().addAll("animated-option-button");
    btn_floor3.setOnMouseClicked(
        e -> {
          currentFloor = 3;
          try {
            setFloorImg("/edu/wpi/N/images/Floor3TeamN.png");
          } catch (DBException ex) {
            ex.printStackTrace();
          }
        });
    btn_floor4
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floor4.getStyleClass().addAll("animated-option-button");
    btn_floor4.setOnMouseClicked(
        e -> {
          currentFloor = 4;
          try {
            setFloorImg("/edu/wpi/N/images/Floor4TeamN.png");
          } catch (DBException ex) {
            ex.printStackTrace();
          }
        });
    btn_floor5
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floor5.getStyleClass().addAll("animated-option-button");
    btn_floor5.setOnMouseClicked(
        e -> {
          currentFloor = 5;
          try {
            setFloorImg("/edu/wpi/N/images/Floor5TeamN.png");
          } catch (DBException ex) {
            ex.printStackTrace();
          }
        });
    nodesList = new JFXNodesList();
    nodesList.addAnimatedNode(btn_floors);
    nodesList.addAnimatedNode(btn_floor5);
    nodesList.addAnimatedNode(btn_floor4);
    nodesList.addAnimatedNode(btn_floor3);
    nodesList.addAnimatedNode(btn_floor2);
    nodesList.addAnimatedNode(btn_floor1);
    nodesList.setSpacing(20);
    pn_changeFloor.getChildren().add(nodesList);
  }

  private void setFloorImg(String path) throws DBException {
    pn_display.getChildren().removeIf(node -> node instanceof Line);
    if (mode == Mode.PATH_STATE) {
      drawPath(pathNodes);
    }
    if (mode == Mode.NO_STATE) {
      defaultKioskNode();
    }
    Image img = new Image(getClass().getResourceAsStream(path));
    img_map.setImage(img);
  }

  private void jumpToFloor(String path) throws DBException {
    Image img = new Image(getClass().getResourceAsStream(path));
    img_map.setImage(img);
  }

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void onBtnHomeClicked() throws IOException {
    mainApp.switchScene("views/home.fxml");
  }

  public void displayErrorMessage(String str) {
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    errorAlert.setHeaderText("Invalid input");
    errorAlert.setContentText(str);
    errorAlert.showAndWait();
  }

  private void defaultKioskNode() throws DBException {
    LinkedList<String> kiosks = new LinkedList<>();
    if (currentFloor == 1) {
      txt_firstLocation.setText(MapDB.getNode("NSERV00301").getLongName());
      kiosks.add(MapDB.getNode("NSERV00301").getLongName());
      ObservableList<String> textList = FXCollections.observableList(kiosks);
      lst_firstLocation.setItems(textList);
      lst_firstLocation.getSelectionModel().select(0);

    } else if (currentFloor == 3) {
      txt_firstLocation.setText(MapDB.getNode("NSERV00103").getLongName());
      kiosks.add(MapDB.getNode("NSERV00103").getLongName());
      ObservableList<String> textList = FXCollections.observableList(kiosks);
      lst_firstLocation.setItems(textList);
      lst_firstLocation.getSelectionModel().select(0);
    } else {
      txt_firstLocation.clear();
      lst_firstLocation.getItems().clear();
    }
  }
  // Upon clicking find path to location button call this method
/*  @FXML
  private void onDoctorPathFindClicked(MouseEvent event) throws Exception {
    pn_path.getChildren().removeIf(node -> node instanceof Line);
    int currentSelection = lst_doctorlocations.getSelectionModel().getSelectedIndex();
    DbNode destinationNode = doctorNodes.get(currentSelection);
    if (selectedNodes.size() < 1) selectedNodes.add(defaultNode);
    selectedNodes.add(destinationNode);
    // if (selectedNodes.size() < 2) selectedNodes.add(defaultNode);
    onBtnFindClicked(event);
    selectedNodes.clear();
  }*/
}
