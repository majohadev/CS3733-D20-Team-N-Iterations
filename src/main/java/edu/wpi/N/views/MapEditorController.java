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
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javax.swing.*;

public class MapEditorController implements Controller {
  App mainApp;

  @FXML Pane pn_display;
  @FXML Pane pn_editor;
  @FXML Button btn_home;
  @FXML StackPane pn_stack;
  @FXML Pane pn_edges;

  final int DEFAULT_FLOOR = 1;
  final String DEFAULT_BUILDING = "Faulkner";
  final Color DEFAULT_CIRCLE_COLOR = Color.PURPLE;
  final Color DEFAULT_LINE_COLOR = Color.BLACK;
  final double DEFAULT_LINE_WIDTH = 4;
  final Color ADD_NODE_COLOR = Color.BLACK;
  final Color DELETE_NODE_COLOR = Color.RED;
  final Color EDIT_NODE_COLOR = Color.RED;
  final double DEFAULT_CIRCLE_OPACITY = 1;
  final double DEFAULT_CIRCLE_RADIUS = 7;
  final Color DELETE_EDGE_COLOR = Color.RED;

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
  MapEditorDeleteNodeController controllerDeleteNode;
  MapEditorEditNodeController controllerEditNode;
  MapEditorDeleteEdgeController controllerDeleteEdge;

  int currentFloor;
  String currentBuilding;
  // Add Node Variable
  Circle addNodeCircle;
  // Delete Node Variable
  LinkedList<Circle> deleteNodeCircles;
  // Edit Node Variable
  Circle editNodeCircle;
  // Add Edge Variable
  Line addEdgeLine;
  // Delete Edge Variable
  LinkedList<Line> deleteEdgeLines;

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
    addNodeCircle = null;
    deleteNodeCircles = new LinkedList<>();
    editNodeCircle = null;
    addEdgeLine = new Line();
    pn_edges.getChildren().add(addEdgeLine);
    deleteEdgeLines = new LinkedList<>();
  }

  private void loadFloor() throws DBException {
    LinkedList<DbNode> floorNodes = MapDB.floorNodes(currentFloor, currentBuilding);
    LinkedList<DbNode[]> floorEdges = MapDB.getFloorEdges(currentFloor, currentBuilding);
    HashMap<String, UINode> conversion = createUINodes(floorNodes, DEFAULT_CIRCLE_COLOR);
    createUIEdges(conversion, floorEdges, DEFAULT_LINE_COLOR);
    displayEdges();
    displayNodes();
  }

  private void displayEdges() {
    for (Line line : edgesMap.keySet()) {
      pn_edges.getChildren().add(line);
    }
  }

  private void displayNodes() {
    for (Circle circle : nodesMap.keySet()) {
      pn_display.getChildren().add(circle);
    }
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
      line.setOnMouseClicked(event -> this.handleLineClickedEvents(event, line));
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
    circle.setOnMouseClicked(event -> this.handleCircleClickedEvents(event, circle));
    circle.setOnMouseReleased(
        event -> {
          try {
            this.handleCircleDragReleased(event, circle);
          } catch (DBException e) {
            e.printStackTrace();
          }
        });
    //    pn_display.getChildren().add(circle);
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
    resetAll();
    if (mode == Mode.ADD_NODE) {
      loadEditor("/edu/wpi/N/views/mapEditorAddNode.fxml");
    } else if (mode == Mode.DELETE_NODE) {
      loadEditor("/edu/wpi/N/views/mapEditorDeleteNode.fxml");
    } else if (mode == Mode.EDIT_NODE) {
      loadEditor("/edu/wpi/N/views/mapEditorEditNode.fxml");
    } else if (mode == Mode.DELETE_EDGE) {
      loadEditor("/edu/wpi/N/views/mapEditorDeleteEdge.fxml");
    }
  }

  private void loadEditor(String path) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
    Pane pane = loader.load();
    if (mode == Mode.ADD_NODE) {
      controllerAddNode = loader.getController();
    } else if (mode == Mode.DELETE_NODE) {
      controllerDeleteNode = loader.getController();
    } else if (mode == Mode.EDIT_NODE) {
      controllerEditNode = loader.getController();
    } else if (mode == Mode.DELETE_EDGE) {
      controllerDeleteEdge = loader.getController();
    }
    pn_editor.getChildren().add(pane);
    pn_editor.setVisible(true);
  }

  private void handleCircleDragEvents(MouseEvent event, Circle circle) {
    if (mode == Mode.ADD_NODE && circle == addNodeCircle) {
      onCircleAddNodeDragged(event, circle);
    }
    if (mode == Mode.EDIT_NODE) {
      onBtnCancelEditNodeClicked();
      onBtnConfirmEditNodeClicked();
      onTxtPosEditNodeTextChanged(circle);
      onCircleEditNodeDragged(event, circle);
    }
    if (mode == Mode.ADD_EDGE || mode == Mode.NO_STATE) {
      mode = Mode.ADD_EDGE;
      handleCircleAddEdgeDragged(event, circle);
    }
  }
  // TODO
  // SDFKJSDLFKJSLKDFJSLKDFJSLKDJFLKSDJFLSKJDFLKSJDFLKSJDFLKSJDFLKSJDFLKSJDFLKSJFLKSJDFLKSJDFLKSJDF
  private void handleLineClickedEvents(MouseEvent event, Line line) {
    if (mode == Mode.DELETE_EDGE) {
      onLineDeleteEdgeClicked(event, line);
    }
  }

  private void onLineDeleteEdgeClicked(MouseEvent event, Line line) {
    if (line.getStroke() == DEFAULT_LINE_COLOR) {
      line.setStroke(DELETE_EDGE_COLOR);
      DbNode[] edge = edgesMap.get(line).getDBNodes();
      deleteEdgeLines.add(line);
      controllerDeleteEdge.addLstDeleteNode(edge[0].getShortName() + ", " + edge[1].getShortName());
    } else if (line.getStroke() == DELETE_EDGE_COLOR) {
      line.setStroke(DEFAULT_LINE_COLOR);
      DbNode[] edge = edgesMap.get(line).getDBNodes();
      controllerDeleteEdge.removeLstDeleteNode(
          edge[0].getShortName() + ", " + edge[1].getShortName());
      deleteEdgeLines.remove(line);
    }
  }

  private void cancelDeleteEdge() {
    controllerDeleteEdge
        .getBtnCancel()
        .setOnMouseClicked(
            e -> {
              resetDeleteEdge();
              mode = Mode.NO_STATE;
            });
  }

  private void confirmDeleteEdge() {
    controllerDeleteEdge
        .getBtnConfirm()
        .setOnMouseClicked(
            e -> {
              for (Line line : deleteEdgeLines) {
                DbNode[] nodes = edgesMap.get(line).getDBNodes();
                pn_edges.getChildren().remove(line);
                edgesMap.remove(line);
                for (UINode node : nodesMap.values()) {
                  if (node.getDBNode().getNodeID() == nodes[0].getNodeID()
                      || node.getDBNode().getNodeID() == nodes[1].getNodeID()) {
                    node.getEdges().remove(nodesMap.get(line));
                  }
                }
                try {
                  MapDB.removeEdge(nodes[0].getNodeID(), nodes[1].getNodeID());
                } catch (DBException ex) {
                  ex.printStackTrace();
                }
              }
              resetDeleteEdge();
              mode = Mode.NO_STATE;
            });
  }

  private void resetDeleteEdge() {
    pn_editor.setVisible(false);
    pn_stack.getChildren().remove(pn_display);
    pn_stack.getChildren().add(pn_display);
    if (!pn_display.getChildren().contains(pn_editor)) {
      pn_display.getChildren().add(pn_editor);
    }
    for (Line line : deleteEdgeLines) {
      line.setStroke(DEFAULT_LINE_COLOR);
    }
    deleteEdgeLines.clear();
  }
  // TODO ASLDKFJAS;LKDFJA;LSDKFJAS;LDKFJA;LKDFJA;LKSDJF;ALKSDJF
  private void handleCircleClickedEvents(MouseEvent event, Circle circle) {
    if (mode == Mode.DELETE_NODE) {
      onCircleDeleteNodeClicked(event, circle);
    }
    if (mode == Mode.EDIT_NODE) {
      onBtnCancelEditNodeClicked();
      onBtnConfirmEditNodeClicked();
      onTxtPosEditNodeTextChanged(circle);
      onCircleEditNodeClicked(event, circle);
    }
  }

  private void onCircleEditNodeClicked(MouseEvent event, Circle circle) {
    if (editNodeCircle != circle && editNodeCircle != null) {
      DbNode node = nodesMap.get(editNodeCircle).getDBNode();
      editNodeCircle.setCenterX(scaleX(node.getX()));
      editNodeCircle.setCenterY(scaleY(node.getY()));
      editNodeCircle.setFill(DEFAULT_CIRCLE_COLOR);
      cancelEditNode();
    }
    editNodeCircle = circle;
    circle.setFill(EDIT_NODE_COLOR);
    DbNode node = nodesMap.get(circle).getDBNode();
    controllerEditNode.setShortName(node.getShortName());
    controllerEditNode.setLongName(node.getLongName());
    controllerEditNode.setPos(event.getX(), event.getY());
  }

  private void onCircleEditNodeDragged(MouseEvent event, Circle circle) {
    if (editNodeCircle != circle && editNodeCircle != null) {
      DbNode node = nodesMap.get(editNodeCircle).getDBNode();
      editNodeCircle.setCenterX(scaleX(node.getX()));
      editNodeCircle.setCenterY(scaleY(node.getY()));
      editNodeCircle.setFill(DEFAULT_CIRCLE_COLOR);
      cancelEditNode();
    }
    editNodeCircle = circle;
    circle.setFill(EDIT_NODE_COLOR);
    controllerEditNode.setPos(event.getX(), event.getY());
    circle.setCenterX(event.getX());
    circle.setCenterY(event.getY());
    DbNode node = nodesMap.get(circle).getDBNode();
    controllerEditNode.setShortName(node.getShortName());
    controllerEditNode.setLongName(node.getLongName());
    UINode uiNode = nodesMap.get(circle);
    for (UIEdge edges : uiNode.getEdges()) {
      DbNode firstNode = edges.getDBNodes()[0];
      DbNode secondNode = edges.getDBNodes()[1];
      if (firstNode.getNodeID().equals(node.getNodeID())) {
        edges.getLine().setStartX(event.getX());
        edges.getLine().setStartY(event.getY());
      } else if (secondNode.getNodeID().equals(node.getNodeID())) {
        edges.getLine().setEndX(event.getX());
        edges.getLine().setEndY(event.getY());
      }
    }
  }

  private void cancelEditNode() {
    DbNode node = nodesMap.get(editNodeCircle).getDBNode();
    UINode uiNode = nodesMap.get(editNodeCircle);
    for (UIEdge edges : uiNode.getEdges()) {
      DbNode firstNode = edges.getDBNodes()[0];
      DbNode secondNode = edges.getDBNodes()[1];
      if (firstNode.getNodeID().equals(node.getNodeID())) {
        edges.getLine().setStartX(scaleX(uiNode.getDBNode().getX()));
        edges.getLine().setStartY(scaleY(uiNode.getDBNode().getY()));
      } else if (secondNode.getNodeID().equals(node.getNodeID())) {
        edges.getLine().setEndX(scaleX(uiNode.getDBNode().getX()));
        edges.getLine().setEndY(scaleY(uiNode.getDBNode().getY()));
      }
    }
  }

  private void onCircleDeleteNodeClicked(MouseEvent event, Circle circle) {
    if (circle.getFill() == DEFAULT_CIRCLE_COLOR) {
      circle.setFill(DELETE_NODE_COLOR);
      deleteNodeCircles.add(circle);
      controllerDeleteNode.addLstDeleteNode(nodesMap.get(circle).getDBNode().getShortName());
    } else if (circle.getFill() == DELETE_NODE_COLOR) {
      circle.setFill(DEFAULT_CIRCLE_COLOR);
      deleteNodeCircles.remove(circle);
      controllerDeleteNode.removeLstDeleteNode(nodesMap.get(circle).getDBNode().getShortName());
    }
  }

  private void handleDeleteNodeRightClick() throws IOException, DBException {
    mode = Mode.DELETE_NODE;
    changeEditor();
    onBtnCancelDeleteNodeClicked();
    onBtnConfirmDeleteNodeClicked();
  }

  private void handleEditNodeRightClick() throws IOException {
    mode = Mode.EDIT_NODE;
    changeEditor();
  }

  private void handleDeleteEdgeRightClick() throws IOException {
    mode = Mode.DELETE_EDGE;
    changeEditor();
    pn_stack.getChildren().remove(pn_edges);
    pn_stack.getChildren().add(pn_edges);
    pn_edges.getChildren().add(pn_editor);
    cancelDeleteEdge();
    confirmDeleteEdge();
  }

  // Pane Display Clicked
  public void onPaneDisplayClicked(MouseEvent event) throws IOException {
    // Add Node
    if (event.getClickCount() == 2 && mode != Mode.ADD_NODE) {
      onPaneDisplayClickedAddNode(event);
    }
    if (event.getButton() == MouseButton.SECONDARY) {
      ContextMenu menu = new ContextMenu();
      MenuItem deleteNode = new MenuItem("Delete Node");
      MenuItem editNode = new MenuItem("Edit Node");
      MenuItem deleteEdge = new MenuItem("Delete Edge");
      deleteNode.setOnAction(
          e -> {
            try {
              handleDeleteNodeRightClick();
            } catch (IOException | DBException ex) {
              ex.printStackTrace();
            }
          });
      editNode.setOnAction(
          e -> {
            try {
              handleEditNodeRightClick();
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          });
      deleteEdge.setOnAction(
          e -> {
            try {
              handleDeleteEdgeRightClick();
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          });
      menu.getItems().addAll(deleteNode, editNode, deleteEdge);
      menu.show(mainApp.getStage(), event.getSceneX(), event.getSceneY());
    }
  }

  private void onPaneDisplayClickedAddNode(MouseEvent event) throws IOException {
    mode = Mode.ADD_NODE;
    changeEditor();
    addNodeCircle = createCircle(event.getX(), event.getY(), ADD_NODE_COLOR);
    pn_display.getChildren().add(addNodeCircle);
    controllerAddNode.setPos(event.getX(), event.getY());
    onTxtPosAddNodeTextChanged(addNodeCircle);
    onBtnConfirmAddNodeClicked(addNodeCircle);
    onBtnCancelAddNodeClicked();
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

  public void onTxtPosEditNodeTextChanged(Circle circle) {
    controllerEditNode
        .getTxtXPos()
        .textProperty()
        .addListener(((observable, oldValue, newValue) -> setCircleXPosEditNode(circle, newValue)));
    controllerEditNode
        .getTxtYPos()
        .textProperty()
        .addListener(((observable, oldValue, newValue) -> setCircleYPosEditNode(circle, newValue)));
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

  private void setCircleXPosEditNode(Circle circle, String newValue) {
    try {
      if (editNodeCircle == circle || (mode != Mode.EDIT_NODE)) {
        circle.setCenterX(Double.parseDouble(newValue));
        DbNode node = nodesMap.get(circle).getDBNode();
        UINode uiNode = nodesMap.get(circle);
        for (UIEdge edges : uiNode.getEdges()) {
          DbNode firstNode = edges.getDBNodes()[0];
          DbNode secondNode = edges.getDBNodes()[1];
          if (firstNode.getNodeID().equals(Double.parseDouble(newValue))) {
            edges.getLine().setStartX(Double.parseDouble(newValue));
          } else if (secondNode.getNodeID().equals(node.getNodeID())) {
            edges.getLine().setEndX(Double.parseDouble(newValue));
          }
        }
      }
    } catch (NumberFormatException e) {
      controllerEditNode.setPos(circle.getCenterX(), circle.getCenterY());
      displayErrorMessage("Invalid Input");
    }
  }

  private void setCircleYPosEditNode(Circle circle, String newValue) {
    try {
      if (editNodeCircle == circle || (mode != Mode.EDIT_NODE)) {
        circle.setCenterY(Double.parseDouble(newValue));
        DbNode node = nodesMap.get(circle).getDBNode();
        UINode uiNode = nodesMap.get(circle);
        for (UIEdge edges : uiNode.getEdges()) {
          DbNode firstNode = edges.getDBNodes()[0];
          DbNode secondNode = edges.getDBNodes()[1];
          if (firstNode.getNodeID().equals(Double.parseDouble(newValue))) {
            edges.getLine().setStartY(Double.parseDouble(newValue));
          } else if (secondNode.getNodeID().equals(node.getNodeID())) {
            edges.getLine().setEndY(Double.parseDouble(newValue));
          }
        }
      }
    } catch (NumberFormatException e) {
      controllerEditNode.setPos(circle.getCenterX(), circle.getCenterY());
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
                //                LinkedList list = new LinkedList();
                //                list.add(newNode);
                //                createUINodes(list, DEFAULT_CIRCLE_COLOR);
                Circle circle1 = createCircle(scaleX(x), scaleY(y), DEFAULT_CIRCLE_COLOR);
                UINode uiNode = new UINode(circle1, newNode);
                nodesMap.put(circle1, uiNode);
                pn_display.getChildren().add(circle1);
                pn_display.getChildren().remove(circle);
                //                pn_display.getChildren().add();
                pn_editor.setVisible(false);
              } catch (DBException e) {
                e.printStackTrace();
              }
            });
  }

  private void onBtnConfirmDeleteNodeClicked() throws DBException {
    controllerDeleteNode
        .getBtnConfirm()
        .setOnMouseClicked(
            event -> {
              for (Circle circle : deleteNodeCircles) {
                UINode node = nodesMap.get(circle);
                try {
                  MapDB.deleteNode(node.getDBNode().getNodeID());
                } catch (DBException e) {
                  e.printStackTrace();
                }
                for (UIEdge edge : node.getEdges()) {
                  DbNode[] edgeNodes = edge.getDBNodes();
                  try {
                    MapDB.removeEdge(edgeNodes[0].getNodeID(), edgeNodes[1].getNodeID());
                  } catch (DBException e) {
                    e.printStackTrace();
                  }
                  edgesMap.remove(edge.getLine());
                  pn_edges.getChildren().remove(edge.getLine());
                }
                pn_display.getChildren().remove(circle);
                nodesMap.remove(circle);
              }
              resetDeleteNode();
              pn_editor.setVisible(false);
              mode = Mode.NO_STATE;
            });
  }

  private void onBtnConfirmEditNodeClicked() {
    controllerEditNode
        .getBtnConfirm()
        .setOnMouseClicked(
            event -> {
              mode = Mode.NO_STATE;
              String strX = controllerEditNode.getXPos();
              String strY = controllerEditNode.getYPos();
              int x = 0;
              int y = 0;
              try {
                x = (int) scaleXDB(Double.parseDouble(strX));
                y = (int) scaleYDB(Double.parseDouble(strY));
              } catch (NumberFormatException e) {
                displayErrorMessage("Invalid input");
                return;
              }
              String longName = controllerEditNode.getShortName();
              String shortName = controllerEditNode.getLongName();
              if (longName == null || shortName == null) {
                displayErrorMessage("Invalid input");
                return;
              }
              String id = nodesMap.get(editNodeCircle).getDBNode().getNodeID();
              try {
                MapDB.modifyNode(id, x, y, longName, shortName);
              } catch (DBException e) {
                e.printStackTrace();
              }
              pn_editor.setVisible(false);
              editNodeCircle.setFill(DEFAULT_CIRCLE_COLOR);
              editNodeCircle = null;
            });
  }

  private void onBtnCancelAddNodeClicked() {
    controllerAddNode
        .getBtnCancel()
        .setOnMouseClicked(
            event -> {
              mode = Mode.NO_STATE;
              pn_editor.setVisible(false);
              pn_display.getChildren().remove(addNodeCircle);
            });
  }

  private void onBtnCancelDeleteNodeClicked() {
    controllerDeleteNode
        .getBtnCancel()
        .setOnMouseClicked(
            event -> {
              mode = Mode.NO_STATE;
              for (Circle circle : deleteNodeCircles) {
                circle.setFill(DEFAULT_CIRCLE_COLOR);
              }
              deleteNodeCircles.clear();
              pn_editor.setVisible(false);
            });
  }

  private void onBtnCancelEditNodeClicked() {
    controllerEditNode
        .getBtnCancel()
        .setOnMouseClicked(
            event -> {
              mode = Mode.NO_STATE;
              editNodeCircle.setFill(DEFAULT_CIRCLE_COLOR);
              editNodeCircle.setCenterX(scaleX(nodesMap.get(editNodeCircle).getDBNode().getX()));
              editNodeCircle.setCenterY(scaleY(nodesMap.get(editNodeCircle).getDBNode().getY()));
              cancelEditNode();
              editNodeCircle = null;
              pn_editor.setVisible(false);
            });
  }

  private void resetAll() {
    pn_editor.setVisible(false);
    resetAddNode();
    resetDeleteNode();
    resetEditNode();
    resetDeleteEdge();
  }

  private void resetAddNode() {
    if (addNodeCircle != null) {
      pn_display.getChildren().remove(addNodeCircle);
    }
  }

  private void resetDeleteNode() {
    for (Circle circle : deleteNodeCircles) {
      circle.setFill(DEFAULT_CIRCLE_COLOR);
    }
    deleteNodeCircles.clear();
  }

  private void resetEditNode() {
    if (editNodeCircle != null) {
      editNodeCircle.setFill(DEFAULT_CIRCLE_COLOR);

      editNodeCircle.setCenterX(scaleX(nodesMap.get(editNodeCircle).getDBNode().getX()));

      editNodeCircle.setCenterY(scaleY(nodesMap.get(editNodeCircle).getDBNode().getY()));
      cancelEditNode();
    }
    editNodeCircle = null;
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

  public void onBtnHomeClicked() throws IOException {
    mainApp.switchScene("views/home.fxml");
  }

  private void handleCircleAddEdgeDragged(MouseEvent event, Circle circle) {
    setLinePosition(
        addEdgeLine, circle.getCenterX(), circle.getCenterY(), event.getX(), event.getY());
  }

  private void setLinePosition(Line line, double x1, double y1, double x2, double y2) {
    line.setStartX(x1);
    line.setStartY(y1);
    line.setEndX(x2);
    line.setEndY(y2);
    line.setStrokeWidth(DEFAULT_LINE_WIDTH);
  }

  private void handleCircleDragReleased(MouseEvent event, Circle circle) throws DBException {
    LinkedList<DbNode> nodes = new LinkedList();
    LinkedList<UINode> UInode = new LinkedList();
    LinkedList<Circle> circles = new LinkedList();
    if (mode == mode.ADD_EDGE) {
      for (Circle aCircle : nodesMap.keySet()) {
        if (aCircle.contains(addEdgeLine.getStartX(), addEdgeLine.getStartY())
            || aCircle.contains(addEdgeLine.getEndX(), addEdgeLine.getEndY())) {
          nodes.add(nodesMap.get(aCircle).getDBNode());
          UInode.add(nodesMap.get(aCircle));
          circles.add(aCircle);
        }
      }
      if (nodes.size() == 2) {
        MapDB.addEdge(nodes.get(0).getNodeID(), nodes.get(1).getNodeID());
        DbNode[] nodes1 = {nodes.get(0), nodes.get(1)};
        DbNode[] nodes2 = {nodes.get(1), nodes.get(0)};
        UIEdge edge;
        Line line =
            new Line(
                addEdgeLine.getStartX(),
                addEdgeLine.getStartY(),
                addEdgeLine.getEndX(),
                addEdgeLine.getEndY());
        line.setStrokeWidth(DEFAULT_LINE_WIDTH);
        line.setOnMouseClicked(e -> this.handleLineClickedEvents(event, line));

        if (circles.get(0).contains(addEdgeLine.getStartX(), addEdgeLine.getStartY())) {
          edge = new UIEdge(line, nodes1);
        } else {
          edge = new UIEdge(line, nodes2);
        }
        edgesMap.put(line, edge);
        addEdgeLine.setOnMouseClicked(e -> this.handleLineClickedEvents(event, edge.getLine()));
        pn_edges.getChildren().add(edge.getLine());
        pn_edges.getChildren().remove(addEdgeLine);
        UInode.get(0).addEdge(edge);
        UInode.get(1).addEdge(edge);
      }
      nodes.clear();
      pn_edges.getChildren().remove(addEdgeLine);
      addEdgeLine = new Line();
      pn_edges.getChildren().add(addEdgeLine);
      mode = Mode.NO_STATE;
    }
  }
}
