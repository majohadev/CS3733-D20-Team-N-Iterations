package edu.wpi.N.views;

import com.google.common.collect.HashBiMap;
import edu.wpi.N.App;
import edu.wpi.N.Main;
import edu.wpi.N.algorithms.Pathfinder;
import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import java.io.InputStream;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class MapDisplayController implements Controller, MapController {
  private App mainApp;
  final float BAR_WIDTH = 300;
  final float IMAGE_WIDTH = 2475;
  final float IMAGE_HEIGHT = 1485;
  final float SCREEN_WIDTH = 1920;
  final float SCREEN_HEIGHT = 1080;
  final float MAP_WIDTH = SCREEN_WIDTH - BAR_WIDTH;
  final float MAP_HEIGHT = (MAP_WIDTH / IMAGE_WIDTH) * IMAGE_HEIGHT;
  final float HORIZONTAL_OFFSET = 10;
  final float VERTICAL_OFFSET = 8;
  final float HORIZONTAL_SCALE = (MAP_WIDTH) / IMAGE_WIDTH;
  final float VERTICAL_SCALE = (MAP_HEIGHT) / IMAGE_HEIGHT;

  @FXML Button btn_find;
  @FXML Button btn_reset;
  @FXML Pane pn_display;

  HashBiMap<Circle, DbNode> masterNodes; // stores the map nodes and their respective database nodes
  LinkedList<DbNode> allFloorNodes; // stores all the nodes on the floor
  LinkedList<DbNode> selectedNodes; // stores all the selected nodes on the map

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException, DBException {
    InputStream nodes = Main.class.getResourceAsStream("csv/TeamNFloor4Nodes.csv");
    InputStream edges = Main.class.getResourceAsStream("csv/TeamNFloor4Edges.csv");
    CSVParser.parseCSV(nodes);
    CSVParser.parseCSV(edges);
    selectedNodes = new LinkedList<DbNode>();
    allFloorNodes = DbController.floorNodes(4, "Faulkner");
    masterNodes = HashBiMap.create();
    populateMap();
  }

  public void populateMap() {
    for (DbNode node : allFloorNodes) {
      Circle mapNode = makeMapNode(node);
      pn_display.getChildren().add(mapNode);
      masterNodes.put(mapNode, node);
    }
  }

  public Circle makeMapNode(DbNode node) {
    Circle mapNode = new Circle();
    mapNode.setRadius(6);
    mapNode.setLayoutX((node.getX() * HORIZONTAL_SCALE + HORIZONTAL_OFFSET));
    mapNode.setLayoutY((node.getY() * VERTICAL_SCALE + VERTICAL_OFFSET));
    mapNode.setFill(Color.PURPLE);
    mapNode.setOpacity(0.7);
    mapNode.setOnMouseClicked(mouseEvent -> this.onMapNodeClicked(mapNode));
    return mapNode;
  }

  public void onMapNodeClicked(Circle mapNode) {
    if (mapNode.getFill() == Color.PURPLE) {
      mapNode.setFill(Color.RED);
      selectedNodes.add(masterNodes.get(mapNode));
    } else {
      mapNode.setFill(Color.PURPLE);
      selectedNodes.remove(masterNodes.get(mapNode));
    }
  }

  @FXML
  private void onBtnFindClicked(MouseEvent event) {
    if (selectedNodes.size() != 2) {
      return;
    }
    DbNode firstNode = selectedNodes.get(0);
    DbNode secondNode = selectedNodes.get(1);

    Path path = Pathfinder.findPath(firstNode.getNodeID(), secondNode.getNodeID());
    LinkedList<DbNode> pathNodes = path.getPath();
    drawPath(pathNodes);

    for (Circle mapNode : masterNodes.keySet()) {
      mapNode.setDisable(true);
    }
  }

  private void drawPath(LinkedList<DbNode> pathNodes) {
    DbNode firstNode;
    DbNode secondNode;

    for (int i = 0; i < pathNodes.size() - 1; i++) {
      firstNode = pathNodes.get(i);
      secondNode = pathNodes.get(i + 1);
      Line line =
          new Line(
              (firstNode.getX() * HORIZONTAL_SCALE) + HORIZONTAL_OFFSET,
              (firstNode.getY() * VERTICAL_SCALE) + VERTICAL_OFFSET,
              (secondNode.getX() * HORIZONTAL_SCALE) + HORIZONTAL_OFFSET,
              (secondNode.getY() * VERTICAL_SCALE) + VERTICAL_OFFSET);
      pn_display.getChildren().add(line);
    }
  }

  @FXML
  private void onResetClicked(MouseEvent event) throws Exception {
    for (Circle mapNode : masterNodes.keySet()) {
      mapNode.setFill(Color.PURPLE);
      mapNode.setDisable(false);
    }
    pn_display.getChildren().removeIf(node -> node instanceof Line);
    selectedNodes.clear();
  }
}
