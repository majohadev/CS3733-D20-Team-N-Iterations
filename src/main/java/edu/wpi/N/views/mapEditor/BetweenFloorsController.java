package edu.wpi.N.views.mapEditor;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXNodesList;
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
import javafx.geometry.NodeOrientation;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Pair;

public class BetweenFloorsController implements Controller, Initializable {
  App mainApp;
  private StateSingleton singleton;

  @FXML private AnchorPane parent;
  @FXML private JFXButton btn_save;
  @FXML private JFXButton btn_cancel;
  @FXML private Text text;
  final Color DEFAULT_CIRCLE_COLOR = Color.web("#002186");
  final String DEFAULT_BUTTON_COLOR = "-fx-background-color: #F7B80F";
  final String INACTIVE_BUTTON_COLOR = "-fx-background-color: #bdbdbd";
  final Color INACTIVE_CIRCLE_COLOR = Color.GRAY;
  final Color DEFAULT_TEXT_COLOR = Color.WHITE;
  final Color DEFAULT_SELECTED_COLOR = Color.web("#ffc911"); // Color.RED;
  final int DEFAULT_RADIUS = 20;
  final int TEXT_OFFSETX = -6;
  final int TEXT_OFFSETY = 6;

  HashMap<Integer, JFXNodesList> nodes;
  HashMap<Integer, Pair<DbNode, Boolean>> nodeStatus;
  LinkedList<Integer> floors;
  LinkedList<DbNode> originalEdges;
  int floor;
  boolean currNode;

  public BetweenFloorsController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  @FXML
  public void initialize(URL url, ResourceBundle rb) {
    this.nodes = new HashMap<Integer, JFXNodesList>();
    this.nodeStatus = new HashMap<Integer, Pair<DbNode, Boolean>>();
    this.floors = new LinkedList<Integer>();
    this.originalEdges = new LinkedList<DbNode>();
    JFXNodesList n5 = createButton(60, 100, "5", 5);
    JFXNodesList n4 = createButton(60, 150, "4", 4);
    JFXNodesList n3 = createButton(60, 200, "3", 3);
    JFXNodesList n2 = createButton(60, 250, "2", 2);
    JFXNodesList n1 = createButton(60, 300, "1", 1);
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
      JFXButton circle = (JFXButton) nodes.get(i).getChildren().get(0);
      circle.setStyle(INACTIVE_BUTTON_COLOR);
      circle.setVisible(false);
    }
    nodes.get(floor).getChildren().get(0).setStyle(DEFAULT_BUTTON_COLOR);
    nodes.get(floor).getChildren().get(0).setVisible(true);
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
        LinkedList<DbNode> connectedNodes = AbsAlgo.searchAccessible(node);
        if (connectedNodes != null) this.originalEdges.addAll(connectedNodes);
      }
    } catch (DBException e) {
      nodesAvailable = null;
    }
    nodes.get(node.getFloor()).getChildren().get(0).setStyle(INACTIVE_BUTTON_COLOR);
    for (DbNode n : nodesAvailable) {
      this.floors.add(n.getFloor());
      nodes.get(n.getFloor()).setVisible(true);
      nodes.get(n.getFloor()).getChildren().get(0).setVisible(true);
      // nodes.get(n.getFloor()).setVisible(true);
      nodeStatus.put(n.getFloor(), new Pair<>(n, false));
    }

    for (DbNode n : originalEdges) {
      nodeStatus.put(n.getFloor(), new Pair<>(n, true));
      nodes.get(n.getFloor()).getChildren().get(0).setStyle(DEFAULT_BUTTON_COLOR);
      // nodes.get(n.getFloor()).setVisible(true);
    }
    text.setVisible(true);
    text.setText(node.getLongName());
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  private JFXNodesList createButton(double x, double y, String floor, int num) {
    JFXButton button = new JFXButton();
    button.setLayoutX(x);
    button.setLayoutY(y);
    button.toFront();
    button.setVisible(false);
    Label label = new Label(floor);
    label.setRotate(180);
    button.setGraphic(new Group(label));
    createContextMenu(button, num);
    JFXButton btn2 = new JFXButton("+");
    JFXNodesList nodeList = new JFXNodesList();
    nodeList.addAnimatedNode(button);
    nodeList.addAnimatedNode(btn2);
    nodeList.setSpacing(10);
    nodeList.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
    this.nodes.put(num, nodeList);
    nodeList.setRotate(-90);
    nodeList.setLayoutX(x);
    nodeList.setLayoutY(y);

    parent.getChildren().addAll(nodeList);
    return nodeList;
  }

  public void createContextMenu(JFXButton button, int num) {
    button.setOnMouseClicked(
        (event -> {
          if (event.getButton() == MouseButton.PRIMARY) {
            nodeStatus.put(
                num, new Pair<>(nodeStatus.get(num).getKey(), !nodeStatus.get(num).getValue()));
            if (nodeStatus.get(num).getValue()) button.setStyle(DEFAULT_BUTTON_COLOR);
            else button.setStyle(INACTIVE_BUTTON_COLOR);
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
      JFXButton circle = (JFXButton) nodes.get(i).getChildren().get(0);
      circle.setStyle(INACTIVE_BUTTON_COLOR);
      circle.setVisible(false);
    }
    btn_save.setVisible(false);
    btn_cancel.setVisible(false);
    // setFloor(this.floor);
  }

  public void onCancelButton() {
    text.setVisible(false);
    for (int i = 1; i <= 5; i++) {
      JFXButton circle = (JFXButton) nodes.get(i).getChildren().get(0);
      circle.setStyle(INACTIVE_BUTTON_COLOR);
      circle.setVisible(false);
    }
    btn_save.setVisible(false);
    btn_cancel.setVisible(false);
    // setFloor(this.floor);
  }
}
