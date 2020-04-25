package edu.wpi.N.views;

import com.google.common.collect.HashBiMap;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.UIEdge;
import edu.wpi.N.entities.UINode;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javax.swing.*;

public class MapEditorController implements Controller {
  App mainApp;

  @FXML Pane pn_display;
  @FXML Pane pn_editor;

  final int DEFAULT_FLOOR = 4;
  final String DEFAULT_BUILDING = "Faulkner";
  final Color DEFAULT_CIRCLE_COLOR = Color.PURPLE;
  final Color DEFAULT_LINE_COLOR = Color.BLACK;
  final double DEFAULT_LINE_WIDTH = 4;
  final Color ADD_NODE_COLOR = Color.BLACK;
  final double DEFAULT_CIRCLE_OPACITY = 0.7;
  final double DEFAULT_CIRCLE_RADIUS = 7;

  final double SCREEN_WIDTH = 1920;
  final double SCREEN_HEIGHT = 1080;
  final double IMAGE_WIDTH = 2475;
  final double IMAGE_HEIGHT = 1485;
  final double MAP_WIDTH = 1661;
  final double MAP_HEIGHT = 997;
  final double HORIZONTAL_SCALE = MAP_WIDTH / IMAGE_WIDTH;
  final double VERTICAL_SCALE = MAP_HEIGHT / IMAGE_HEIGHT;
  Mode mode;

  private enum Mode {
    NO_STATE,
    ADD_NODE,
    DELETE_NODE,
    EDIT_NODE,
    ADD_EDGE,
    DELETE_EDGE,
    EDIT_EDGE
  }

  HashBiMap<Circle, UINode> nodesMap;
  HashBiMap<Line, UIEdge> edgesMap;
  MapEditorAddNodeController controllerAddNode;
  int currentFloor;
  String currentBuilding;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException {
    currentFloor = DEFAULT_FLOOR;
    currentBuilding = DEFAULT_BUILDING;
    nodesMap = HashBiMap.create();
    edgesMap = HashBiMap.create();
    mode = Mode.NO_STATE;
    loadFloor();
  }

  private void loadFloor() throws DBException {
    LinkedList<DbNode> floorNodes = MapDB.floorNodes(currentFloor, currentBuilding);
    LinkedList<DbNode[]> floorEdges = MapDB.getFloorEdges(currentFloor);
    HashMap<String, UINode> conversion = createUINodes(floorNodes, DEFAULT_CIRCLE_COLOR);
    createUIEdges(conversion, floorEdges, DEFAULT_LINE_COLOR);
  }

  private HashMap<String, UINode> createUINodes(LinkedList<DbNode> nodes, Color c) {
    HashMap<String, UINode> conversion = new HashMap<>();
    for (DbNode DBnode : nodes) {
      Circle circle = createCircle(scaleX(DBnode.getX()), scaleY(DBnode.getY()), c);
      UINode UInode = new UINode(circle, DBnode);
      nodesMap.put(circle, UInode);
      conversion.put(DBnode.getNodeID(), UInode);
    }
    return conversion;
  }

  private void createUIEdges(
      HashMap<String, UINode> conversion, LinkedList<DbNode[]> edges, Color c) {
    for (DbNode[] edge : edges) {
      Line line =
          createLine(
              scaleX(edge[0].getX()),
              scaleY(edge[0].getY()),
              scaleX(edge[1].getX()),
              scaleY(edge[1].getY()),
              c);
      UIEdge UIedge = new UIEdge(line, edge);
      conversion.get(edge[0].getNodeID()).addEdge(UIedge);
      conversion.get(edge[1].getNodeID()).addEdge(UIedge);
      edgesMap.put(line, UIedge);
    }
  }

  private Circle createCircle(double x, double y, Color c) {
    Circle circle = new Circle();
    circle.setRadius(DEFAULT_CIRCLE_RADIUS);
    circle.setCenterX(x);
    circle.setCenterY(y);
    circle.setFill(c);
    circle.setOpacity(DEFAULT_CIRCLE_OPACITY);
    circle.setOnMouseDragged(event -> this.handleCircleDragEvents(event, circle));
    pn_display.getChildren().add(circle);
    return circle;
  }

  private Line createLine(double x1, double y1, double x2, double y2, Color c) {
    Line line = new Line(x1, y1, x2, y2);
    line.setStroke(c);
    line.setStrokeWidth(DEFAULT_LINE_WIDTH);
    pn_display.getChildren().add(line);
    return line;
  }

  private double scaleX(double x) {
    return x * HORIZONTAL_SCALE;
  }

  private double scaleY(double y) {
    return y * VERTICAL_SCALE;
  }

  private void changeEditor() throws IOException {
    switch (mode) {
      case ADD_NODE:
        loadEditor("/edu/wpi/N/views/mapEditorAddNode.fxml");
      case DELETE_NODE:
        //        loadEditor("views/mapEditorDeleteNode.fxml");
      case EDIT_NODE:
        //        loadEditor("views/mapEditorEditNode.fxml");
      case ADD_EDGE:
        //        loadEditor("views/mapEditorAddEdge.fxml");
      case DELETE_EDGE:
        //        loadEditor("views/mapEditorDeleteEdge.fxml");
      case EDIT_EDGE:
        //        loadEditor("views/mapEditorEditEdge.fxml");
    }
  }

  private void loadEditor(String path) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
    Pane pane = loader.load();
    switch (mode) {
      case ADD_NODE:
        controllerAddNode = loader.getController();
    }
    pn_editor.getChildren().add(pane);
    pn_editor.setVisible(true);
  }

  private void handleCircleDragEvents(MouseEvent event, Circle circle) {
    if (mode == Mode.ADD_NODE) {
      onCircleAddNodeDragged(event, circle);
    }
    if (mode == Mode.EDIT_NODE) {}
  }

  // Pane Display Clicked
  public void onPaneDisplayClicked(MouseEvent event) throws IOException {
    // Add Node
    if (event.getClickCount() == 2 && mode != Mode.ADD_NODE) {
      onPaneDisplayClickedAddNode(event);
    }
  }

  private void onPaneDisplayClickedAddNode(MouseEvent event) throws IOException {
    mode = Mode.ADD_NODE;
    changeEditor();
    Circle circle = createCircle(event.getX(), event.getY(), ADD_NODE_COLOR);
    controllerAddNode.setPos(event.getX(), event.getY());
    onTxtPosAddNodeTextChanged(circle);
    onBtnConfirmAddNodeClicked(circle);
    onBtnCancelAddNodeClicked(circle);
  }

  public void onPaneDisplayKeyPressed(KeyEvent event) {
    // Add Node
    if (mode == Mode.ADD_NODE) {
      if (event.getCode() == KeyCode.ENTER) {
        System.out.println("Enter Pressed");
      }
    }
  }

  public void onCircleAddNodeDragged(MouseEvent event, Circle circle) {
    circle.setCenterX(event.getX());
    circle.setCenterY(event.getY());
    controllerAddNode.setPos(event.getX(), event.getY());
  }

  // Add Node FXML
  public void onTxtPosAddNodeTextChanged(Circle circle) {
    controllerAddNode
        .getTxtXPos()
        .textProperty()
        .addListener(((observable, oldValue, newValue) -> setCircleXPosAddNode(circle, newValue)));
    controllerAddNode
        .getTxtYPos()
        .textProperty()
        .addListener(((observable, oldValue, newValue) -> setCircleYPosAddNode(circle, newValue)));
  }

  private void setCircleXPosAddNode(Circle circle, String newValue) {
    try {
      circle.setCenterX(Double.parseDouble(newValue));
    } catch (NumberFormatException e) {
      controllerAddNode.setPos(circle.getCenterX(), circle.getCenterY());
      displayErrorMessage("Invalid Input");
    }
  }

  private void setCircleYPosAddNode(Circle circle, String newValue) {
    try {
      circle.setCenterY(Double.parseDouble(newValue));
    } catch (NumberFormatException e) {
      controllerAddNode.setPos(circle.getCenterX(), circle.getCenterY());
      displayErrorMessage("Invalid Input");
    }
  }

  private void onBtnConfirmAddNodeClicked(Circle circle) {
    controllerAddNode
        .getBtnConfirm()
        .setOnMouseClicked(
            event -> {
              try {
                mode = Mode.NO_STATE;
                String strX = controllerAddNode.getXPos();
                String strY = controllerAddNode.getYPos();
                int x = 0;
                int y = 0;
                try {
                  x = (int) scaleXDB(Double.parseDouble(strX));
                  y = (int) scaleYDB(Double.parseDouble(strY));
                } catch (NumberFormatException e) {
                  displayErrorMessage("Invalid input");
                  return;
                }
                String type = controllerAddNode.getType();
                String longName = controllerAddNode.getShortName();
                String shortName = controllerAddNode.getLongName();
                if (type == null || longName == null || shortName == null) {
                  displayErrorMessage("Invalid input");
                  return;
                }
                DbNode newNode =
                    MapDB.addNode(x, y, currentFloor, currentBuilding, type, longName, shortName);
                LinkedList list = new LinkedList();
                list.add(newNode);
                createUINodes(list, DEFAULT_CIRCLE_COLOR);
                pn_display.getChildren().remove(circle);
                pn_editor.setVisible(false);
              } catch (DBException e) {
                e.printStackTrace();
              }
            });
  }

  private void onBtnCancelAddNodeClicked(Circle circle) {
    controllerAddNode
        .getBtnCancel()
        .setOnMouseClicked(
            event -> {
              mode = Mode.NO_STATE;
              pn_editor.setVisible(false);
              pn_display.getChildren().remove(circle);
            });
  }

  private double scaleXDB(double x) {
    return x / HORIZONTAL_SCALE;
  }

  private double scaleYDB(double y) {
    return y / VERTICAL_SCALE;
  }

  public void displayErrorMessage(String str) {
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    errorAlert.setHeaderText("Invalid input");
    errorAlert.setContentText(str);
    errorAlert.showAndWait();
  }
}
