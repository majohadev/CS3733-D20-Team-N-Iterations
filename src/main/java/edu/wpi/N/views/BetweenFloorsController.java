package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.AbsAlgo;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class BetweenFloorsController implements Controller, Initializable {
  App mainApp = null;
  DbNode node;
  int floor;
  HashMap<Integer, Circle> nodes;
  HashMap<Integer, Line> lines;

  final Color DEFAULT_LINE_COLOR = Color.BLACK;
  final Color DEFAULT_CIRCLE_COLOR = Color.PURPLE;
  final Color DEFAULT_CIRCLE_COLOR2 = Color.GRAY;
  final int DEFAULT_RADIUS = 15;

  final double DEFAULT_LINE_WIDTH = 4;
  @FXML private AnchorPane parent;
  @FXML private JFXButton button;

  @FXML private JFXButton button2;

  @FXML
  public void initialize(URL url, ResourceBundle rb) {
    Circle circle1 = createCircle(1000, 500);
    // Text text1 =
    Circle circle2 = createCircle(1000, 450);
    Circle circle3 = createCircle(1000, 400);
    Circle circle4 = createCircle(1000, 350);
    Circle circle5 = createCircle(1000, 300);
    this.nodes = new HashMap<Integer, Circle>();
    nodes.put(1, circle1);
    nodes.put(2, circle2);
    nodes.put(3, circle3);
    nodes.put(4, circle4);
    nodes.put(5, circle5);

    Line line12 = createLine(circle1, circle2, DEFAULT_LINE_COLOR);
    Line line23 = createLine(circle2, circle3, DEFAULT_LINE_COLOR);
    Line line34 = createLine(circle3, circle4, DEFAULT_LINE_COLOR);
    Line line45 = createLine(circle4, circle5, DEFAULT_LINE_COLOR);
    this.lines = new HashMap<Integer, Line>();
    lines.put(3, line12);
    lines.put(5, line23);
    lines.put(7, line34);
    lines.put(9, line45);

    try {
      setFloor(MapDB.getNode("PELEV00X05").getFloor());
    } catch (DBException e) {
      e.printStackTrace();
    }
    /*try {
      setNode(MapDB.getNode("PELEV00X05"));
    } catch (DBException e) {
      e.printStackTrace();
    }*/
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void setFloor(int floor) {
    this.floor = floor;
    nodes.get(floor - 1).setVisible(true);
  }

  public void setNode(DbNode node) throws DBException {
    this.node = node;
    LinkedList<DbNode[]> edges = AbsAlgo.getEdgesBetweenFloors(node);
    // check which nodes exist, make nodes visible, non existent nodes grey
    for (int i = 1; i <= 5; i++) {
      Circle circle = nodes.get(i);
      circle.setFill(DEFAULT_CIRCLE_COLOR2);
      circle.setVisible(true);
    }
    for (Integer i : getFloors(node)) {
      Circle circle = nodes.get(i);
      nodes.get(i).setFill(DEFAULT_CIRCLE_COLOR);
    }
    // make lines visible for existent edges
    for (DbNode[] edge : edges) {
      Line line = lines.get(edge[0].getFloor() + edge[1].getFloor());
      line.setVisible(true);
    }
  }

  public void onButton1() throws DBException {
    setNode(MapDB.getNode("NSTAI00704"));
  }

  public void onButton2() throws DBException {
    setFloor(MapDB.getNode("NSTAI00704").getFloor());
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

  private Circle createCircle(double x, double y) {
    Circle circle = new Circle(DEFAULT_RADIUS, DEFAULT_CIRCLE_COLOR);
    circle.setCenterX(x);
    circle.setCenterY(y);
    parent.getChildren().add(circle);
    circle.toFront();
    circle.setVisible(false);
    return circle;
  }

  private LinkedList<Integer> getFloors(DbNode node) throws DBException {
    LinkedList<DbNode> floorChangeNodes = new LinkedList<DbNode>();
    for (int i = 1; i <= 5; i++) {
      // will need to change when we add another building with different number of floors
      floorChangeNodes.addAll(MapDB.searchNode(i, node.getBuilding(), node.getNodeType(), ""));
    }
    LinkedList<Integer> thisFloorChangeNodes = new LinkedList<Integer>();
    for (DbNode n : floorChangeNodes) {
      if (node.getX() == n.getX() && node.getY() == n.getY()) {
        thisFloorChangeNodes.add(n.getFloor());
      }
    }
    return thisFloorChangeNodes;
  }
}
