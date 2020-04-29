package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.AbsAlgo;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;

public class BetweenFloorsController implements Controller, Initializable {
  App mainApp;
  private StateSingleton singleton;

  @FXML private AnchorPane parent;
  @FXML private JFXButton btn_save;
  @FXML private JFXButton btn_cancel;
  final Color DEFAULT_CIRCLE_COLOR = Color.PURPLE;
  final Color INACTIVE_CIRCLE_COLOR = Color.GRAY;
  final Color DEFAULT_TEXT_COLOR = Color.WHITE;
  final Color DEFAULT_SELECTED_COLOR = Color.RED;
  final int DEFAULT_RADIUS = 20;
  final int TEXT_OFFSETX = -6;
  final int TEXT_OFFSETY = 6;

  HashMap<Integer, Circle> nodes;
  HashMap<Integer, Text> labels;
  // HashMap<Integer, Boolean> status;
  HashMap<Integer, Pair<DbNode, Boolean>> nodeStatus;
  LinkedList<Integer> floors; // set on list of floors that have nodes
  LinkedList<DbNode[]> originalEdges;
  int floor;
  boolean currNode;

  @FXML
  public void initialize(URL url, ResourceBundle rb) {
    this.nodes = new HashMap<Integer, Circle>();
    this.labels = new HashMap<Integer, Text>();
    this.nodeStatus = new HashMap<Integer, Pair<DbNode, Boolean>>();
    this.floors = new LinkedList<Integer>();
    this.originalEdges = new LinkedList<DbNode[]>();

    Circle circle5 = createCircle(65, 0, "5", 5);
    Circle circle4 = createCircle(65, 75, "4", 4);
    Circle circle3 = createCircle(65, 150, "3", 3);
    Circle circle2 = createCircle(65, 225, "2", 2);
    Circle circle1 = createCircle(65, 300f, "1", 1);
  }

  public void setFloor(int floor) {
    this.floor = floor;
    currNode = false;
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
    setFloor(node.getFloor());
    currNode = true;
    this.originalEdges = AbsAlgo.getEdgesBetweenFloors(node);
    LinkedList<DbNode> nodesAvaliable = MapDB.getInShaft(node.getNodeID());
    nodes.get(node.getFloor()).setFill(INACTIVE_CIRCLE_COLOR);
    for (DbNode n : nodesAvaliable) {
      this.floors.add(n.getFloor());
      nodes.get(n.getFloor()).setVisible(true);
      labels.get(n.getFloor()).setVisible(true);
      nodeStatus.put(n.getFloor(), new Pair<>(n, false));
    }

    for (DbNode[] n : originalEdges) {
      nodeStatus.put(n[0].getFloor(), new Pair<>(n[0], true));
      nodeStatus.put(n[1].getFloor(), new Pair<>(n[1], true));
      nodes.get(n[0].getFloor()).setFill(DEFAULT_CIRCLE_COLOR);
      nodes.get(n[1].getFloor()).setFill(DEFAULT_CIRCLE_COLOR);
    }
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
          } else if (event.getButton() == MouseButton.SECONDARY) {
            circle.setFill(DEFAULT_SELECTED_COLOR);
            ContextMenu menu = new ContextMenu();
            MenuItem activateEdge = new MenuItem("Connect");
            MenuItem deactivateEdge = new MenuItem("Disconnect");
            activateEdge.setOnAction(
                e -> {
                  nodeStatus.put(num, new Pair<>(nodeStatus.get(num).getKey(), true));
                  circle.setFill(DEFAULT_CIRCLE_COLOR);
                });
            deactivateEdge.setOnAction(
                e -> {
                  nodeStatus.put(num, new Pair<>(nodeStatus.get(num).getKey(), false));
                  circle.setFill(INACTIVE_CIRCLE_COLOR);
                });
            menu.getItems().addAll(activateEdge, deactivateEdge);
            menu.show(this.mainApp.getStage(), event.getSceneX(), event.getSceneY());
          }
        }));
  }

  public void onSaveButton() throws DBException {
    if (currNode) {
      ArrayList<DbNode> activeNodes = new ArrayList<DbNode>();
      for (DbNode[] n : originalEdges) {
        MapDB.removeEdge(n[0].getNodeID(), n[1].getNodeID());
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
      setFloor(this.floor);
    }
  }

  public void onCancelButton() {
    setFloor(this.floor);
  }

  private LinkedList<DbNode> getFloors(DbNode node) throws DBException {
    try {
      return MapDB.getInShaft(node.getNodeID());
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
      return new LinkedList<DbNode>();
    }
    //    LinkedList<DbNode> floorChangeNodes = new LinkedList<DbNode>();
    //    for (int i = 1; i <= 5; i++) {
    //      // will need to change when we add another building with different number of floors
    //      floorChangeNodes.addAll(MapDB.searchNode(i, node.getBuilding(), node.getNodeType(),
    // ""));
    //    }
    //    LinkedList<DbNode> thisFloorChangeNodes = new LinkedList<DbNode>();
    //    for (DbNode n : floorChangeNodes) {
    //      if (node.getX() == n.getX() && node.getY() == n.getY()) {
    //        thisFloorChangeNodes.add(n);
    //      }
    //    }
    //    return thisFloorChangeNodes;
  }
}
