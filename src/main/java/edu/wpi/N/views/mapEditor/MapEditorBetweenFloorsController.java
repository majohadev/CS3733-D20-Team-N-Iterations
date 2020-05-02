package edu.wpi.N.views.mapEditor;

import com.jfoenix.controls.JFXButton;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.AbsAlgo;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.net.URL;
import java.util.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;

public class MapEditorBetweenFloorsController implements Controller, Initializable {
  App mainApp;
  private StateSingleton singleton;

  @FXML private AnchorPane parent;
  @FXML private JFXButton btn_save;
  @FXML private JFXButton btn_cancel;
  @FXML private Text text;
  final Color DEFAULT_CIRCLE_COLOR = Color.web("#002186"); // Color.PURPLE;
  final Color INACTIVE_CIRCLE_COLOR = Color.GRAY;
  final Color DEFAULT_TEXT_COLOR = Color.WHITE;
  final Color DEFAULT_SELECTED_COLOR = Color.web("#ffc911"); // Color.RED;
  final int DEFAULT_RADIUS = 20;
  final int TEXT_OFFSETX = -6;
  final int TEXT_OFFSETY = 6;

  HashMap<Integer, Circle> nodes;
  HashMap<Integer, Text> labels;
  // HashMap<Integer, Boolean> status;
  HashMap<Integer, Pair<DbNode, Boolean>> nodeStatus;
  LinkedList<Integer> floors; // set on list of floors that have nodes
  LinkedList<DbNode> originalEdges;
  int floor;
  boolean currNode;

  @FXML
  public void initialize(URL url, ResourceBundle rb) {
    this.nodes = new HashMap<Integer, Circle>();
    this.labels = new HashMap<Integer, Text>();
    this.nodeStatus = new HashMap<Integer, Pair<DbNode, Boolean>>();
    this.floors = new LinkedList<Integer>();
    this.originalEdges = new LinkedList<DbNode>();

    Circle circle5 = createCircle(65, 100, "5", 5);
    Circle circle4 = createCircle(65, 150, "4", 4);
    Circle circle3 = createCircle(65, 200, "3", 3);
    Circle circle2 = createCircle(65, 250, "2", 2);
    Circle circle1 = createCircle(65, 300, "1", 1);
  }

  public JFXButton getBtnCancel() {
    return btn_cancel;
  }

  public JFXButton getBtnSave() {
    return btn_save;
  }

  public void setFloor(int floor) {
    this.floor = floor;
    this.nodeStatus = new HashMap<Integer, Pair<DbNode, Boolean>>();
    this.floors = new LinkedList<Integer>();
    this.originalEdges = new LinkedList<DbNode>();
    currNode = false;
    text.setVisible(false);
    for (int i = 1; i <= 5; i++) {
      Circle circle = nodes.get(i);
      circle.setFill(INACTIVE_CIRCLE_COLOR);
      circle.setVisible(false);
      labels.get(i).setVisible(false);
    }
    nodes.get(floor).setFill(DEFAULT_CIRCLE_COLOR);
    nodes.get(floor).setVisible(true);
    labels.get(floor).setVisible(true);
  }

  public void setNode(DbNode node) throws DBException {
    btn_cancel.setVisible(true);
    btn_save.setVisible(true);
    setFloor(node.getFloor());
    this.floor = node.getFloor();
    currNode = true;
    LinkedList<DbNode> nodesAvailable;
    try {
      nodesAvailable = MapDB.getInShaft(node.getNodeID());
      Iterator<DbNode> nodeIt = nodesAvailable.iterator();
      this.originalEdges = new LinkedList<DbNode>();
      while (nodeIt.hasNext()) {
        DbNode next = nodeIt.next();
        LinkedList<DbNode> connectedNodes = AbsAlgo.searchAccessible(next);
        if (connectedNodes != null) this.originalEdges.addAll(connectedNodes);
      }
    } catch (DBException e) {
      nodesAvailable = null;
    }
    nodes.get(node.getFloor()).setFill(INACTIVE_CIRCLE_COLOR);
    for (DbNode n : nodesAvailable) {
      this.floors.add(n.getFloor());
      nodes.get(n.getFloor()).setVisible(true);
      labels.get(n.getFloor()).setVisible(true);
      nodeStatus.put(n.getFloor(), new Pair<>(n, false));
    }

    for (DbNode n : originalEdges) {
      nodeStatus.put(n.getFloor(), new Pair<>(n, true));
      nodes.get(n.getFloor()).setFill(DEFAULT_CIRCLE_COLOR);
    }
    text.setVisible(true);
    text.setText(node.getLongName());
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @Override
  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public Text setText(double x, double y, String text) {
    Text text1 = new Text(x + TEXT_OFFSETX, y + TEXT_OFFSETY, text);
    text1.setFill(DEFAULT_TEXT_COLOR);
    text1.setMouseTransparent(true);
    text1.setFont(Font.font("Calibri", 20));
    text1.toFront();
    text1.setTextAlignment(TextAlignment.CENTER);
    text1.setVisible(false);
    parent.getChildren().add(text1);
    return text1;
  }

  private Circle createCircle(double x, double y, String text, int num) {
    Circle circle = new Circle(DEFAULT_RADIUS, DEFAULT_CIRCLE_COLOR);
    circle.setCenterX(x);
    circle.setCenterY(y);
    parent.getChildren().add(circle);
    circle.toFront();
    circle.setVisible(false);
    Text text1 = setText(x, y, text);
    this.nodes.put(num, circle);
    this.labels.put(num, text1);
    createContextMenu(circle, num);
    return circle;
  }

  public void createContextMenu(Circle circle, int num) {
    circle.setOnMouseClicked(
        (event -> {
          if (event.getButton() == MouseButton.PRIMARY) {
            nodeStatus.put(
                num, new Pair<>(nodeStatus.get(num).getKey(), !nodeStatus.get(num).getValue()));
            if (nodeStatus.get(num).getValue()) circle.setFill(DEFAULT_CIRCLE_COLOR);
            else circle.setFill(INACTIVE_CIRCLE_COLOR);
          }
        }));
  }

  public void onSaveButton() throws DBException {
    if (currNode) {
      ArrayList<DbNode> activeNodes = new ArrayList<DbNode>();
      for (int i = 0; i < originalEdges.size() - 1; i++) {
        MapDB.removeEdge(originalEdges.get(i).getNodeID(), originalEdges.get(i + 1).getNodeID());
      }
      for (Integer i : floors) {
        if (nodeStatus.get(i).getValue()) {
          activeNodes.add(nodeStatus.get(i).getKey());
        }
      }
      if (activeNodes.size() >= 1) {
        for (int i = 0; i < activeNodes.size() - 1; i++) {
          MapDB.addEdge(activeNodes.get(i).getNodeID(), activeNodes.get(i + 1).getNodeID());
        }
      }
    }
    text.setVisible(false);
    for (int i = 1; i <= 5; i++) {
      Circle circle = nodes.get(i);
      circle.setFill(INACTIVE_CIRCLE_COLOR);
      circle.setVisible(false);
      labels.get(i).setVisible(false);
    }
    btn_save.setVisible(false);
    btn_cancel.setVisible(false);
    // setFloor(this.floor);
  }

  public void onCancelButton() {
    text.setVisible(false);
    for (int i = 1; i <= 5; i++) {
      Circle circle = nodes.get(i);
      circle.setFill(INACTIVE_CIRCLE_COLOR);
      circle.setVisible(false);
      labels.get(i).setVisible(false);
    }
    btn_save.setVisible(false);
    btn_cancel.setVisible(false);
    // setFloor(this.floor);
  }

  private LinkedList<DbNode> getFloors(DbNode node) throws DBException {
    LinkedList<DbNode> floorChangeNodes = new LinkedList<DbNode>();
    for (int i = 1; i <= 5; i++) {
      floorChangeNodes.addAll(MapDB.searchNode(i, node.getBuilding(), node.getNodeType(), ""));
    }
    LinkedList<DbNode> thisFloorChangeNodes = new LinkedList<DbNode>();
    for (DbNode n : floorChangeNodes) {
      if (Math.abs(node.getX() - n.getX()) <= 10 && Math.abs(node.getY() - n.getY()) <= 10) {
        thisFloorChangeNodes.add(n);
      }
    }
    return thisFloorChangeNodes;
  }
}
