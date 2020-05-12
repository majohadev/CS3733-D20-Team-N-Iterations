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
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Pair;

public class BetweenFloorsController implements Controller, Initializable {
  App mainApp;
  private StateSingleton singleton;

  @FXML private AnchorPane parent;
  @FXML private JFXButton btn_save;
  @FXML private JFXButton btn_cancel;
  @FXML private JFXButton btn_manage;
  @FXML private Text text;
  @FXML private VBox vbox_shaft;
  @FXML private AnchorPane background;

  final Color DEFAULT_CIRCLE_COLOR = Color.web("#002186");
  final String DEFAULT_BUTTON_COLOR = "-fx-background-color: #4a69c6";
  final String INACTIVE_BUTTON_COLOR = "-fx-background-color: #515678";
  final String DISCONNECTED = "DISCONNECTED";
  final String CONNECTED = "CONNECTED";
  final String EMPTY = "EMPTY";

  final Color INACTIVE_CIRCLE_COLOR = Color.GRAY;
  final Color DEFAULT_TEXT_COLOR = Color.WHITE;
  final Color DEFAULT_SELECTED_COLOR = Color.web("#ffc911");
  final int DEFAULT_RADIUS = 20;
  final int TEXT_OFFSETX = -6;
  final int TEXT_OFFSETY = 6;

  HashMap<Integer, JFXButton> nodes;
  HashMap<Integer, Pair<DbNode, String>> nodeStatus;
  LinkedList<Integer> floors;
  LinkedList<DbNode> originalEdges;
  int floor;
  boolean currNode;
  LinkedList<Integer> addShaftButtons;
  DbNode currentNode;
  String currentBuilding;
  MapEditorController mapEditorController;

  int numFloors;

  @FXML
  public void initialize(URL url, ResourceBundle rb) {
    numFloors = 6;
    this.nodes = new HashMap<Integer, JFXButton>();
    this.nodeStatus = new HashMap<Integer, Pair<DbNode, String>>();
    btn_manage.setStyle(DEFAULT_BUTTON_COLOR);
    btn_cancel.setStyle(DEFAULT_BUTTON_COLOR);
    btn_save.setStyle(DEFAULT_BUTTON_COLOR);
    DbNode node =
        new DbNode("NHALL00104", 1250, 850, 1, "MainBuil", "ELEV", "Hall 1", "Hall 1", 'N');

    for (int i = numFloors; i >= 1; i--) {
      nodeStatus.put(i, new Pair<>(node, EMPTY));
      createButton(1, 1, i);
    }
    this.floors = new LinkedList<Integer>();
    this.originalEdges = new LinkedList<DbNode>();
    this.addShaftButtons = new LinkedList<Integer>();
  }

  public JFXButton getBtnCancel() {
    return btn_cancel;
  }

  public JFXButton getBtnSave() {
    return btn_save;
  }

  public JFXButton getBtnAddShaft() {

    return btn_manage;
  }

  public void setFloor(int floor, String building) {
    if (building.equals("Faulkner")) {
      numFloors = 5;
      for (int i = 1; i <= 5; i++) {
        // nodes.get(i).setText(i + "");
        nodes.get(i).setGraphic(new Group(new Label(i + "")));
      }
    } else {
      numFloors = 6;
      nodes.get(1).setGraphic(new Group(new Label("L2")));
      nodes.get(2).setGraphic(new Group(new Label("L1")));
      nodes.get(3).setGraphic(new Group(new Label("G")));
      nodes.get(4).setGraphic(new Group(new Label("1")));
      nodes.get(5).setGraphic(new Group(new Label("2")));
      nodes.get(6).setGraphic(new Group(new Label("3")));
    }
    currentBuilding = building;
    btn_save.setVisible(false);
    btn_cancel.setVisible(false);
    this.floor = floor;
    this.floors = new LinkedList<Integer>();
    this.originalEdges = new LinkedList<DbNode>();
    currNode = false;
    text.setVisible(false);
    btn_manage.setVisible(true);
    DbNode node =
        new DbNode("NHALL00104", 1250, 850, 1, "MainBuil", "ELEV", "Hall 1", "Hall 1", 'N');
    for (int i = 1; i <= numFloors; i++) {
      setEmpty(nodes.get(i), i);
      nodeStatus.put(i, new Pair<DbNode, String>(node, EMPTY));
      this.floors.add(i);
    }
  }

  public LinkedList<DbNode> getNodesInShaft() throws DBException {
    LinkedList<DbNode> newList = new LinkedList<>();
    if (!originalEdges.isEmpty()) {
      for (DbNode n : originalEdges) {
        if (!newList.contains(n)) newList.add(n);
      }
    }
    return newList;
  }

  public DbNode getCurrentNode() {
    return this.currentNode;
  }

  public void setNode(DbNode node) throws DBException {
    setFloor(node.getFloor(), node.getBuilding());
    btn_cancel.setVisible(true);
    btn_save.setVisible(true);
    this.floor = node.getFloor();
    currNode = true;
    currentNode = node;
    LinkedList<DbNode> nodesAvailable;
    try {
      nodesAvailable = MapDB.getInShaft(node.getNodeID());
      Iterator<DbNode> nodeIt = nodesAvailable.iterator();
      this.originalEdges = new LinkedList<DbNode>();
      for (DbNode n : nodesAvailable) {
        setDisconnected(nodes.get(n.getFloor()), n.getFloor());
        // this.floors.add(n.getFloor());
        nodes.get(n.getFloor()).setVisible(true);
        nodes.get(n.getFloor()).setVisible(true);
        // nodes.get(n.getFloor()).setVisible(true);
        nodeStatus.put(n.getFloor(), new Pair<>(n, DISCONNECTED));
        LinkedList<DbNode> connectedNodes = AbsAlgo.searchAccessible(n);
        if (connectedNodes != null) this.originalEdges.addAll(connectedNodes);
      }
    } catch (DBException e) {
      nodesAvailable = null;
    }
    nodes.get(node.getFloor()).setStyle(INACTIVE_BUTTON_COLOR);

    for (DbNode n : originalEdges) {
      nodeStatus.put(n.getFloor(), new Pair<>(n, CONNECTED));
      nodes.get(n.getFloor()).setStyle(DEFAULT_BUTTON_COLOR);
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

  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  private JFXButton createButton(double x, double y, int floor) {
    JFXButton button = new JFXButton();
    // button.setText(floor + "");
    button.setGraphic(new Group(new Label(floor + "")));
    button.toFront();
    button.setVisible(false);
    setEmpty(button, floor);
    vbox_shaft.getChildren().add(button);
    this.nodes.put(floor, button);
    return button;
  }

  public void styleFloorButtons(JFXButton btn) {
    btn.getStylesheets()
        .add(getClass().getResource("/edu/wpi/N/css/MapDisplayFloors.css").toExternalForm());
    btn.getStyleClass().add("choice-button");
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
    for (int i = 1; i <= numFloors; i++) {
      JFXButton circle = (JFXButton) nodes.get(i);
      JFXButton circle2 = (JFXButton) nodes.get(i);
      circle.setVisible(false);
      circle2.setVisible(false);
    }
    btn_save.setVisible(false);
    btn_cancel.setVisible(false);
    setFloor(this.floor, this.currentBuilding);
  }

  public void onCancelButton() {
    text.setVisible(false);
    for (int i = 1; i <= numFloors; i++) {
      JFXButton circle = nodes.get(i);
      circle.setVisible(false);
    }
    btn_save.setVisible(false);
    btn_cancel.setVisible(false);
  }

  public JFXButton setEmpty(JFXButton nodeList, int floor) {
    nodeStatus.put(floor, new Pair<>(nodeStatus.get(floor).getKey(), EMPTY));
    JFXButton button1 = nodeList;
    button1.setVisible(false);
    this.nodes.put(floor, nodeList);
    return nodeList;
  }

  public JFXButton setConnected(JFXButton nodeList, int floor) {
    nodeStatus.put(floor, new Pair<>(nodeStatus.get(floor).getKey(), CONNECTED));
    JFXButton button1 = nodeList;
    button1.setVisible(true);
    button1.setStyle(DEFAULT_BUTTON_COLOR);
    // Label label = new Label(floor + "");
    // button1.setGraphic(new Group(label));
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

  public JFXButton setDisconnected(JFXButton nodeList, int floor) throws DBException {
    nodeStatus.put(floor, new Pair<>(nodeStatus.get(floor).getKey(), DISCONNECTED));
    for (int i = 0; i < addShaftButtons.size(); i++) {
      if (addShaftButtons.get(i) == floor) {
        addShaftButtons.remove(i);
      }
    }
    JFXButton button1 = nodeList;
    button1.setVisible(true);
    button1.setStyle(INACTIVE_BUTTON_COLOR);
    // label = new Label(floor + "");
    // button1.setGraphic(new Group(label));
    button1.setOnMouseClicked(
        (event -> {
          if (event.getButton() == MouseButton.PRIMARY) {
            setConnected(nodeList, floor);
            return;
          }
        }));

    this.nodes.put(floor, nodeList);
    return nodeList;
  }
}
