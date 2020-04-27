package edu.wpi.N.views;

import com.jfoenix.controls.*;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.Algorithm;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import lombok.SneakyThrows;

public class hamburgerTestController implements Controller, Initializable {

  private App mainApp = null;
  @FXML ImageView img_map;
  @FXML Pane pn_display;
  @FXML Pane pn_changeFloor;
  @FXML JFXTextField txt_firstLocation;
  @FXML JFXTextField txt_secondLocation;
  @FXML JFXListView lst_firstLocation;
  @FXML JFXListView lst_secondLocation;
  @FXML JFXButton btn_findPath;

  private JFXButton btn_floors, btn_floor1, btn_floor2, btn_floor3, btn_floor4, btn_floor5;
  private final int DEFAULT_FLOOR = 1;
  private final String DEFAULT_BUILDING = "FAULKNER";
  private int currentFloor;
  private String currentBuilding;
  HashMap<String, DbNode> stringNodeConversion = new HashMap<>();
  LinkedList<String> allLongNames = new LinkedList<>();
  LinkedList<DbNode> allFloorNodes = new LinkedList<>();

  @SneakyThrows
  @Override
  public void initialize(URL location, ResourceBundle resourceBundle) {
    initializeChangeFloorButtons();
    this.currentFloor = DEFAULT_FLOOR;
    this.currentBuilding = DEFAULT_BUILDING;
    this.allFloorNodes = MapDB.allNodes();
    initializeConversions();
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
    pn_display.getChildren().removeIf(node -> node instanceof Line);
    String firstSelection = (String) lst_firstLocation.getSelectionModel().getSelectedItem();
    String secondSelection = (String) lst_secondLocation.getSelectionModel().getSelectedItem();
    findPath(stringNodeConversion.get(firstSelection), stringNodeConversion.get(secondSelection));
  }

  private void findPath(DbNode node1, DbNode node2) throws DBException {
    Algorithm myAStar = new Algorithm();
    Path path = myAStar.findPath(node1, node2, false);
    LinkedList<DbNode> pathNodes = path.getPath();
    drawPath(pathNodes);
  }

//  private void drawPath(LinkedList<DbNode> pathNodes) {
//    DbNode firstNode = pathNodes.get(0);
//    DbNode secondNode = pathNodes.get(1);
//    for (int i = 0; (i < pathNodes.size() - 1) && secondNode.getFloor() == currentFloor; i++) {
//      firstNode = pathNodes.get(i);
//      secondNode = pathNodes.get(i + 1);
//      //        Line line = new Line(
//      //                              (firstNode.getX() * HORIZONTAL_SCALE) + HORIZONTAL_OFFSET,
//      //                              (firstNode.getY() * VERTICAL_SCALE) + VERTICAL_OFFSET,
//      //                              (secondNode.getX() * HORIZONTAL_SCALE) + HORIZONTAL_OFFSET,
//      //                              (secondNode.getY() * VERTICAL_SCALE) + VERTICAL_OFFSET);
//    }
//  }
//
//  //      Line line =
//  //              new Line(
//  //                      (firstNode.getX() * HORIZONTAL_SCALE) + HORIZONTAL_OFFSET,
//  //                      (firstNode.getY() * VERTICAL_SCALE) + VERTICAL_OFFSET,
//  //                      (secondNode.getX() * HORIZONTAL_SCALE) + HORIZONTAL_OFFSET,
//  //                      (secondNode.getY() * VERTICAL_SCALE) + VERTICAL_OFFSET);
//  //      line.setStrokeWidth(5);
//  //      pn_path.getChildren().add(line);
//  //    }
//  //  }

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
    btn_floors.getStyleClass().addAll("animated-option-button", "animated-option-sub-button");
    btn_floor1
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floor1.getStyleClass().addAll("animated-option-button", "animated-option-sub-button");
    btn_floor1.setOnMouseClicked(
        e -> {
          setFloorImg("/edu/wpi/N/images/Floor1TeamN.png");
          currentFloor = 1;
        });
    btn_floor2
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floor2.getStyleClass().addAll("animated-option-button", "animated-option-sub-button");
    btn_floor2.setOnMouseClicked(
        e -> {
          setFloorImg("/edu/wpi/N/images/Floor2TeamN.png");
          currentFloor = 2;
        });
    btn_floor3
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floor3.getStyleClass().addAll("animated-option-button", "animated-option-sub-button");
    btn_floor3.setOnMouseClicked(
        e -> {
          setFloorImg("/edu/wpi/N/images/Floor3TeamN.png");
          currentFloor = 3;
        });
    btn_floor4
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floor4.getStyleClass().addAll("animated-option-button", "animated-option-sub-button");
    btn_floor4.setOnMouseClicked(
        e -> {
          setFloorImg("/edu/wpi/N/images/Floor4TeamN.png");
          currentFloor = 4;
        });
    btn_floor5
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floor5.getStyleClass().addAll("animated-option-button", "animated-option-sub-button");
    btn_floor5.setOnMouseClicked(
        e -> {
          setFloorImg("/edu/wpi/N/images/Floor5TeamN.png");
          currentFloor = 5;
        });
    JFXNodesList nodesList = new JFXNodesList();
    nodesList.addAnimatedNode(btn_floors);
    nodesList.addAnimatedNode(btn_floor1);
    nodesList.addAnimatedNode(btn_floor2);
    nodesList.addAnimatedNode(btn_floor3);
    nodesList.addAnimatedNode(btn_floor4);
    nodesList.addAnimatedNode(btn_floor5);
    nodesList.setSpacing(20);
    pn_changeFloor.getChildren().add(nodesList);
  }

  private void setFloorImg(String path) {
    Image img = new Image(getClass().getResourceAsStream(path));
    img_map.setImage(img);
  }

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }
}
