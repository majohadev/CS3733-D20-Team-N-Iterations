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
  final String DEFAULT_BUTTON_COLOR = "-fx-background-color: #4A69C6";
  final String INACTIVE_BUTTON_COLOR = "-fx-background-color: #E6EBF2";
  final String PLUS_BUTTON_COLOR = "-fx-background-color: #6C5C7F";
  final String DISCONNECTED = "DISCONNECTED";
  final String CONNECTED = "CONNECTED";
  final String EMPTY = "EMPTY";

  final Color INACTIVE_CIRCLE_COLOR = Color.GRAY;
  final Color DEFAULT_TEXT_COLOR = Color.WHITE;
  final Color DEFAULT_SELECTED_COLOR = Color.web("#ffc911");
  final int DEFAULT_RADIUS = 20;
  final int TEXT_OFFSETX = -6;
  final int TEXT_OFFSETY = 6;

  HashMap<Integer, JFXNodesList> nodes;
  HashMap<Integer, Pair<DbNode, String>> nodeStatus;
  LinkedList<Integer> floors;
  LinkedList<DbNode> originalEdges;
  int floor;
  boolean currNode;
  LinkedList<JFXButton> addShaftButtons;

  @FXML
  public void initialize(URL url, ResourceBundle rb) {
    this.nodes = new HashMap<Integer, JFXNodesList>();
    this.nodeStatus = new HashMap<Integer, Pair<DbNode, String>>();
    DbNode node =
        new DbNode("NHALL00104", 1250, 850, 1, "MainBuil", "ELEV", "Hall 1", "Hall 1", 'N');

    for (int i = 1; i <= 5; i++) {
      nodeStatus.put(i, new Pair<>(node, EMPTY));
    }
    this.floors = new LinkedList<Integer>();
    this.originalEdges = new LinkedList<DbNode>();
    this.addShaftButtons = new LinkedList<JFXButton>();
    JFXNodesList n5 = createButton(60, 100, 5);
    JFXNodesList n4 = createButton(60, 150, 4);
    JFXNodesList n3 = createButton(60, 200, 3);
    JFXNodesList n2 = createButton(60, 250, 2);
    JFXNodesList n1 = createButton(60, 300, 1);
  }

  public JFXButton getBtnCancel() {
    return btn_cancel;
  }

  public JFXButton getBtnSave() {
    return btn_save;
  }

  public LinkedList<JFXButton> getBtnAddShaft() {
    return this.addShaftButtons;
  }

  public void setFloor(int floor) {
    this.floor = floor;
    // this.nodeStatus = new HashMap<Integer, Pair<DbNode, String>>();
    this.floors = new LinkedList<Integer>();
    this.originalEdges = new LinkedList<DbNode>();
    currNode = false;
    text.setVisible(false);
    DbNode node =
        new DbNode("NHALL00104", 1250, 850, 1, "MainBuil", "ELEV", "Hall 1", "Hall 1", 'N');

    for (int i = 1; i <= 5; i++) {
      JFXButton circle = (JFXButton) nodes.get(i).getChildren().get(0);
      setEmpty(nodes.get(i), i);
      nodeStatus.put(i, new Pair<DbNode, String>(node, EMPTY));
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
      for (DbNode n : nodesAvailable) {
        setDisconnected(nodes.get(n.getFloor()), n.getFloor());
        this.floors.add(n.getFloor());
        nodes.get(n.getFloor()).setVisible(true);
        nodes.get(n.getFloor()).getChildren().get(0).setVisible(true);
        // nodes.get(n.getFloor()).setVisible(true);
        nodeStatus.put(n.getFloor(), new Pair<>(n, DISCONNECTED));
        LinkedList<DbNode> connectedNodes = AbsAlgo.searchAccessible(n);
        if (connectedNodes != null) this.originalEdges.addAll(connectedNodes);
      }
    } catch (DBException e) {
      nodesAvailable = null;
    }
    nodes.get(node.getFloor()).getChildren().get(0).setStyle(INACTIVE_BUTTON_COLOR);

    for (DbNode n : originalEdges) {
      nodeStatus.put(n.getFloor(), new Pair<>(n, CONNECTED));
      nodes.get(n.getFloor()).getChildren().get(0).setStyle(DEFAULT_BUTTON_COLOR);
      setConnected(nodes.get(n.getFloor()), n.getFloor());
      // nodes.get(n.getFloor()).setVisible(true);
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

  private JFXNodesList createButton(double x, double y, int floor) {
    JFXButton button = new JFXButton();
    button.setLayoutX(x);
    button.setLayoutY(y);
    button.toFront();
    button.setVisible(false);
    JFXButton btn2 = new JFXButton();
    btn2.setVisible(false);
    JFXNodesList nodeList = new JFXNodesList();
    nodeList.addAnimatedNode(button);
    nodeList.addAnimatedNode(btn2);
    nodeList.setSpacing(10);
    nodeList.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
    nodeList.setRotate(-90);
    nodeList.setLayoutX(x);
    nodeList.setLayoutY(y);
    nodeList = setEmpty(nodeList, floor);
    parent.getChildren().addAll(nodeList);
    this.nodes.put(floor, nodeList);
    return nodeList;
  }

  public void onSaveButton() throws DBException {
    if (currNode) {
      ArrayList<DbNode> activeNodes = new ArrayList<DbNode>();
      for (int i = 0; i < originalEdges.size() - 1; i++) {
        MapDB.removeEdge(originalEdges.get(i).getNodeID(), originalEdges.get(i + 1).getNodeID());
      }
      for (Integer i : floors) {
        if (nodeStatus.get(i).getValue().equals(CONNECTED)) {
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

  public JFXNodesList setEmpty(JFXNodesList nodeList, int floor) {
    nodeStatus.put(floor, new Pair<>(nodeStatus.get(floor).getKey(), EMPTY)); // Null pointer
    JFXButton button1 = (JFXButton) nodeList.getChildren().get(0);
    JFXButton button2 = (JFXButton) nodeList.getChildren().get(1);
    button2.setVisible(false);
    button1.setStyle(PLUS_BUTTON_COLOR);
    addShaftButtons.add(
        button1); // TODO: figure out when addShaftButtons needs to reset, make sure onClick add
    // shaft is handled somewhere
    Label label = new Label("+");
    label.setRotate(180);
    button1.setGraphic(new Group(label));
    /*    button1.setOnMouseClicked(
    (event -> {
      if (event.getButton() == MouseButton.PRIMARY) {

        // then switch
        setDisconnected(nodeList, floor);//TODO: open addShaftWindow
        return;
      }
    }));*/
    this.nodes.put(floor, nodeList);
    return nodeList;
  }

  public JFXNodesList setConnected(JFXNodesList nodeList, int floor) {
    nodeStatus.put(floor, new Pair<>(nodeStatus.get(floor).getKey(), CONNECTED));
    JFXButton button1 = (JFXButton) nodeList.getChildren().get(0);
    JFXButton button2 = (JFXButton) nodeList.getChildren().get(1);
    button1.setVisible(true);
    button2.setVisible(false);
    button1.setStyle(DEFAULT_BUTTON_COLOR);
    Label label = new Label(floor + "");
    label.setRotate(180);
    button1.setGraphic(new Group(label));
    button1.setOnMouseClicked(
        (event -> {
          if (event.getButton() == MouseButton.PRIMARY) {
            try {
              setDisconnected(nodeList, floor);
            } catch (DBException e) {
              e.printStackTrace();
            }
            return;
          }
        }));

    this.nodes.put(floor, nodeList);
    return nodeList;
  }

  public JFXNodesList setDisconnected(JFXNodesList nodeList, int floor) throws DBException {
    nodeStatus.put(floor, new Pair<>(nodeStatus.get(floor).getKey(), DISCONNECTED));
    JFXButton button1 = (JFXButton) nodeList.getChildren().get(0);
    JFXButton button2 = (JFXButton) nodeList.getChildren().get(1);
    button1.setVisible(true);
    button2.setVisible(true);
    button1.setStyle(INACTIVE_BUTTON_COLOR);
    Label label = new Label(floor + "");
    label.setRotate(180);
    button1.setGraphic(new Group(label));
    button2.setStyle(PLUS_BUTTON_COLOR);
    Label label2 = new Label("-");
    label2.setRotate(180);
    button2.setGraphic(new Group(label2));
    button1.setOnMouseClicked(
        (event -> {
          if (event.getButton() == MouseButton.PRIMARY) {

            // then switch
            setConnected(nodeList, floor);
            return;
          }
        }));

    button2.setOnMouseClicked(
        (event -> {
          if (event.getButton() == MouseButton.PRIMARY) {
            try {
              MapDB.removeFromShaft(
                  nodeStatus.get(floor).getKey().getNodeID()); // TODO: check if in shaft first
              // TODO:reset buttons after dbchange (setNode())
            } catch (DBException e) {
              e.printStackTrace();
            }
            setEmpty(nodeList, floor);
            return;
          }
        }));
    this.nodes.put(floor, nodeList);
    return nodeList;
  }
}
