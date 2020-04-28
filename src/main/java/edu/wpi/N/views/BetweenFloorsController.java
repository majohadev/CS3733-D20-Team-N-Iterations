package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.AbsAlgo;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class BetweenFloorsController implements Controller, Initializable {
  App mainApp;
  DbNode node;
  int floor;
  HashMap<Integer, Circle> nodes; // key is floor number
  HashMap<Integer, Line> lines; // key is sum
  HashMap<Integer, Text> labels;
  LinkedList<DbNode[]> originalEdges;
  LinkedList<DbNode[]> finalEdges;
  HashMap<Integer, DbNode[]> potentialEdges; // key is sum
  HashMap<Line, Boolean> lineStatus;
  StateSingleton singleton;

  final Color DEFAULT_CIRCLE_COLOR = Color.PURPLE;
  final Color DEFAULT_CIRCLE_COLOR2 = Color.GRAY;
  final Color DEFAULT_TEXT_COLOR = Color.WHITE;
  final Color ACTIVE_LINE_COLOR = Color.BLACK;
  final Color INACTIVE_LINE_COLOR = Color.GRAY;
  final Color DEFAULT_SELECTED_COLOR = Color.RED;
  final int DEFAULT_RADIUS = 20;
  final int TEXT_OFFSETX = -6;
  final int TEXT_OFFSETY = 6;
  final double DEFAULT_LINE_WIDTH = 6;

  @FXML private AnchorPane parent;
  @FXML private JFXButton btn_save;
  @FXML private JFXButton btn_cancel;

  @FXML
  public void initialize(URL url, ResourceBundle rb) {
    System.out.println("Hello");
    this.lines = new HashMap<Integer, Line>();
    this.nodes = new HashMap<Integer, Circle>();
    this.labels = new HashMap<Integer, Text>();
    this.originalEdges = new LinkedList<DbNode[]>();
    // this.finalEdges = new LinkedList<DbNode[]>();
    this.potentialEdges = new HashMap<Integer, DbNode[]>(); // key is sum
    this.lineStatus = new HashMap<Line, Boolean>();

    Circle circle5 = createCircle(65, 0, "5", 5);
    Circle circle4 = createCircle(65, 75, "4", 4);
    Circle circle3 = createCircle(65, 150, "3", 3);
    Circle circle2 = createCircle(65, 225, "2", 2);
    Circle circle1 = createCircle(65, 300f, "1", 1);

    nodes.put(1, circle1);
    nodes.put(2, circle2);
    nodes.put(3, circle3);
    nodes.put(4, circle4);
    nodes.put(5, circle5);

    Line line12 = createLine(circle1, circle2, ACTIVE_LINE_COLOR);
    createContextMenu(line12);
    Line line23 = createLine(circle2, circle3, ACTIVE_LINE_COLOR);
    createContextMenu(line23);
    Line line34 = createLine(circle3, circle4, ACTIVE_LINE_COLOR);
    createContextMenu(line34);
    Line line45 = createLine(circle4, circle5, ACTIVE_LINE_COLOR);
    createContextMenu(line45);

    lines.put(3, line12);
    lines.put(5, line23);
    lines.put(7, line34);
    lines.put(9, line45);
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

  public void setFloor(int floor) {
    this.floor = floor;
    lines.get(3).setStroke(INACTIVE_LINE_COLOR);
    lines.get(3).setVisible(false);
    lineStatus.put(lines.get(3), false);
    lines.get(5).setStroke(INACTIVE_LINE_COLOR);
    lines.get(5).setVisible(false);
    lineStatus.put(lines.get(5), false);
    lines.get(7).setStroke(INACTIVE_LINE_COLOR);
    lines.get(7).setVisible(false);
    lineStatus.put(lines.get(7), false);
    lines.get(9).setStroke(INACTIVE_LINE_COLOR);
    lines.get(9).setVisible(false);
    lineStatus.put(lines.get(9), false);
    for (int i = 1; i <= 5; i++) {
      Circle circle = nodes.get(i);
      circle.setFill(DEFAULT_CIRCLE_COLOR2);
      circle.setVisible(false);
      labels.get(i).setVisible(false);
    }
    nodes.get(floor).setFill(DEFAULT_CIRCLE_COLOR);
    nodes.get(floor).setVisible(true);
    labels.get(floor).setVisible(true);
  }

  public void setNode(DbNode node) throws DBException {
    setFloor(node.getFloor());
    this.node = node;
    LinkedList<DbNode[]> edges = AbsAlgo.getEdgesBetweenFloors(node);

    // make nodes visible, grey
    for (int i = 1; i <= 5; i++) {
      Circle circle = nodes.get(i);
      circle.setFill(DEFAULT_CIRCLE_COLOR2);
      circle.setVisible(true);
      labels.get(i).setVisible(true);
    }

    LinkedList<DbNode> nodesAvaliable = getFloors(node);
    LinkedList<Integer> floorsAvaliable = new LinkedList<Integer>();

    for (DbNode n : nodesAvaliable) {
      floorsAvaliable.add(n.getFloor());
    }
    enableLines(floorsAvaliable);
    setAllPotentialEdges(nodesAvaliable);

    // make lines visible for existent edges
    if (!edges.isEmpty()) {
      for (DbNode[] edge : edges) {
        Line line = lines.get(edge[0].getFloor() + edge[1].getFloor());
        line.setStroke(ACTIVE_LINE_COLOR);
        lineStatus.put(line, true);
        originalEdges.add(edge);
      }
    }
  }

  public void enableLines(LinkedList<Integer> floorsAvaliable) {
    if (floorsAvaliable.contains(1) && floorsAvaliable.contains(2)) {
      lines.get(3).setVisible(true);
      lines.get(3).setDisable(false);
    }
    if (floorsAvaliable.contains(2) && floorsAvaliable.contains(3)) {
      lines.get(5).setVisible(true);
      lines.get(5).setDisable(false);
    }
    if (floorsAvaliable.contains(3) && floorsAvaliable.contains(4)) {
      lines.get(7).setVisible(true);
      lines.get(7).setDisable(false);
    }
    if (floorsAvaliable.contains(4) && floorsAvaliable.contains(5)) {
      lines.get(9).setVisible(true);
      lines.get(9).setDisable(false);
    }
  }

  public void setAllPotentialEdges(LinkedList<DbNode> nodes) {
    for (DbNode n : nodes) {
      for (DbNode d : nodes) {
        if (n.getFloor() == d.getFloor() + 1) {
          DbNode[] edge = new DbNode[] {d, n};
          potentialEdges.put(n.getFloor() + d.getFloor(), edge);
        }
      }
    }
  }

  public void createContextMenu(Line line) {
    line.setOnMouseClicked(
        (event -> {
          if (event.getButton() == MouseButton.PRIMARY) {
          } else if (event.getButton() == MouseButton.SECONDARY) {
            line.setStroke(DEFAULT_SELECTED_COLOR);
            ContextMenu menu = new ContextMenu();
            MenuItem activateEdge = new MenuItem("Activate");
            MenuItem deactivateEdge = new MenuItem("Deactivate");
            activateEdge.setOnAction(
                e -> {
                  try {
                    activateEdgeOnClick(line);
                    line.setStroke(ACTIVE_LINE_COLOR);
                  } catch (IOException | DBException ex) {
                    ex.printStackTrace();
                  }
                });
            deactivateEdge.setOnAction(
                e -> {
                  try {
                    deactivateEdgeOnClick(line);
                    line.setStroke(INACTIVE_LINE_COLOR);
                  } catch (IOException | DBException ex) {
                    ex.printStackTrace();
                  }
                });
            menu.getItems().addAll(activateEdge, deactivateEdge);
            menu.show(this.mainApp.getStage(), event.getSceneX(), event.getSceneY());
          }
        }));
  }

  public void onSaveButton() throws DBException {
    // setFloor(floor);
    LinkedList<DbNode[]> newEdges = new LinkedList<DbNode[]>();
    LinkedList<DbNode[]> removedEdges = new LinkedList<DbNode[]>();
    if (lineStatus.get(lines.get(3))) {
      if (!(potentialEdges.get(3) == null)) newEdges.add(potentialEdges.get(3));
    } else {
      if (!(potentialEdges.get(3) == null)) removedEdges.add(potentialEdges.get(3));
    }
    if (lineStatus.get(lines.get(5))) {
      if (!(potentialEdges.get(5) == null)) newEdges.add(potentialEdges.get(5));
    } else {
      if (!(potentialEdges.get(5) == null)) removedEdges.add(potentialEdges.get(5));
    }
    if (lineStatus.get(lines.get(7))) {
      if (!(potentialEdges.get(7) == null)) newEdges.add(potentialEdges.get(7));
    } else {
      if (!(potentialEdges.get(7) == null)) removedEdges.add(potentialEdges.get(7));
    }
    if (lineStatus.get(lines.get(9))) {
      if (!(potentialEdges.get(9) == null)) newEdges.add(potentialEdges.get(9));
    } else {
      if (!(potentialEdges.get(9) == null)) removedEdges.add(potentialEdges.get(9));
    }
    if (!newEdges.isEmpty()) {
      for (DbNode[] n : newEdges) {
        if (!MapDB.getAdjacent(n[0].getNodeID()).contains(n[1])) {
          try {
            MapDB.addEdge(n[0].getNodeID(), n[1].getNodeID());
          } catch (Exception e) {
            e.printStackTrace();
            ;
          }
        }
      }
    }
    if (!removedEdges.isEmpty()) {
      for (DbNode[] r : removedEdges) {
        if (MapDB.getAdjacent(r[0].getNodeID()).contains(r[1])) {
          try {
            MapDB.removeEdge(r[0].getNodeID(), r[1].getNodeID());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  public void onCancelButton() {
    setFloor(this.floor);
  }

  private Line createLine(Circle c1, Circle c2, Color c) {
    double x1 = c1.getCenterX();
    double x2 = c2.getCenterX();
    double y1 = c1.getCenterY();
    double y2 = c2.getCenterY();
    Line line = new Line(x1, y1, x2, y2);
    line.setStroke(c);
    line.setStrokeWidth(DEFAULT_LINE_WIDTH);
    parent.getChildren().add(line);
    line.toBack();
    line.setVisible(false);
    return line;
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
    return circle;
  }

  private LinkedList<DbNode> getFloors(DbNode node) throws DBException {
    LinkedList<DbNode> floorChangeNodes = new LinkedList<DbNode>();
    for (int i = 1; i <= 5; i++) {
      // will need to change when we add another building with different number of floors
      floorChangeNodes.addAll(MapDB.searchNode(i, node.getBuilding(), node.getNodeType(), ""));
    }
    LinkedList<DbNode> thisFloorChangeNodes = new LinkedList<DbNode>();
    for (DbNode n : floorChangeNodes) {
      if (node.getX() == n.getX() && node.getY() == n.getY()) {
        thisFloorChangeNodes.add(n);
        nodes.get(n.getFloor()).setFill(DEFAULT_CIRCLE_COLOR);
      }
    }
    return thisFloorChangeNodes;
  }

  private void deactivateEdgeOnClick(Line line) throws IOException, DBException {
    this.lineStatus.put(line, false);
  }

  private void activateEdgeOnClick(Line line) throws IOException, DBException {
    this.lineStatus.put(line, true);
  }
}
