package edu.wpi.N.views;

import com.google.common.collect.HashBiMap;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.UIEdge;
import edu.wpi.N.entities.UINode;
import java.util.HashMap;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javax.swing.*;

public class MapEditorController implements Controller {
  App mainApp;

  @FXML Pane pn_display;
  @FXML JFXHamburger hb_editor;

  final int DEFAULT_FLOOR = 4;
  final String DEFAULT_BUILDING = "Faulkner";
  final Color DEFAULT_CIRCLE_COLOR = Color.PURPLE;
  final Color DEFAULT_LINE_COLOR = Color.BLACK;
  final double DEFAULT_LINE_WIDTH = 4;
  final Color ADD_NODE_COLOR = Color.RED;
  final double DEFAULT_CIRCLE_OPACITY = 0.7;
  final double DEFAULT_CIRCLE_RADIUS = 7;

  final double SCREEN_WIDTH = 1920;
  final double SCREEN_HEIGHT = 1080;
  final double IMAGE_WIDTH = 2475;
  final double IMAGE_HEIGHT = 1485;
  final double MAP_WIDTH = 1800;
  final double MAP_HEIGHT = 1080;
  final double HORIZONTAL_SCALE = MAP_WIDTH / IMAGE_WIDTH;
  final double VERTICAL_SCALE = MAP_HEIGHT / IMAGE_HEIGHT;

  HashBiMap<Circle, UINode> nodesMap;
  HashBiMap<Line, UIEdge> edgesMap;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException {
    int currentFloor = DEFAULT_FLOOR;
    String currentBuilding = DEFAULT_BUILDING;
    nodesMap = HashBiMap.create();
    edgesMap = HashBiMap.create();
    loadFloor(currentFloor, currentBuilding);
    initEditor();
  }

  private void loadFloor(int floor, String building) throws DBException {
    LinkedList<DbNode> floorNodes = MapDB.floorNodes(floor, building);
    LinkedList<DbNode[]> floorEdges = MapDB.getFloorEdges(floor);
    HashMap<DbNode, UINode> conversion = createUINodes(floorNodes, DEFAULT_CIRCLE_COLOR);
    createUIEdges(conversion, floorEdges, DEFAULT_LINE_COLOR);
  }

  private HashMap<DbNode, UINode> createUINodes(LinkedList<DbNode> nodes, Color c) {
    HashMap<DbNode, UINode> conversion = new HashMap<>();
    for (DbNode DBnode : nodes) {
      Circle circle = createCircle(scaleX(DBnode.getX()), scaleY(DBnode.getY()), c);
      UINode UInode = new UINode(circle, DBnode);
      nodesMap.put(circle, UInode);
      conversion.put(DBnode, UInode);
    }
    return conversion;
  }

  private void createUIEdges(
      HashMap<DbNode, UINode> conversion, LinkedList<DbNode[]> edges, Color c) {
    for (DbNode[] edge : edges) {
      Line line =
          createLine(
              scaleX(edge[0].getX()),
              scaleY(edge[0].getY()),
              scaleX(edge[1].getX()),
              scaleY(edge[1].getY()),
              c);
      UIEdge UIedge = new UIEdge(line, edge);
      conversion.get
      edgesMap.put(line, UIedge);
    }
  }

  public void initEditor() {
    HamburgerSlideCloseTransition trans = new HamburgerSlideCloseTransition(hb_editor);
    trans.setRate(-1);
    hb_editor.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> this.onHBEditorClicked(trans));
  }

  public void onHBEditorClicked(HamburgerSlideCloseTransition trans) {
    trans.setRate(trans.getRate() * -1);
    trans.play();
  }

  // Nodes Add
  public void onPaneDisplayClicked(MouseEvent event) {
    if (event.getClickCount() == 2) {
      createCircle(event.getX(), event.getY(), ADD_NODE_COLOR);
    }
  }

  private Circle createCircle(double x, double y, Color c) {
    Circle circle = new Circle();
    circle.setRadius(DEFAULT_CIRCLE_RADIUS);
    circle.setCenterX(x);
    circle.setCenterY(y);
    circle.setFill(c);
    circle.setOpacity(DEFAULT_CIRCLE_OPACITY);
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
}
