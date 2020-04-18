package edu.wpi.N.views;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import edu.wpi.N.App;
import edu.wpi.N.entities.DbNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.util.LinkedList;

public class MapDisplayController implements Controller, MapController {
  private App mainApp;
  final int BAR_WIDTH = 300;
  final int IMAGE_WIDTH = 2475;
  final int IMAGE_HEIGHT = 1485;
  final int SCREEN_WIDTH = 1920;
  final int SCREEN_HEIGHT = 1080;
  final int MAP_WIDTH = SCREEN_WIDTH - BAR_WIDTH;
  final int MAP_HEIGHT = (MAP_WIDTH / IMAGE_WIDTH) * IMAGE_HEIGHT;
  final int HORIZONTAL_OFFSET = BAR_WIDTH;
  final int VERTICAL_OFFSET = (SCREEN_HEIGHT - IMAGE_HEIGHT) / 2;

  @FXML Button btn_find;
  @FXML Button btn_reset;
  @FXML Pane pn_display;

  BiMap<String, String> masterNodes = HashBiMap.create(); // stores the map nodes and their respective database nodes
  LinkedList<DbNode> allFloorNodes; // stores all the nodes on the floor
  LinkedList<DbNode> selectedNodes; // stores all the selected nodes on the map


  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() {}
}

//  final double SCALE = 0.55;
//
//  public void initialize() {
//    //    InputStream inputNodes = Main.class.getResourceAsStream("csv/MapEnodes.csv");
//    //    InputStream inputEdges = Main.class.getResourceAsStream("csv/MapEedges.csv");
//    //    CSVParser.parseCSV(inputNodes);
//    //    CSVParser.parseCSV(inputEdges);
//    selectedNodes = new LinkedList<DbNode>();
//    populateMap();
//  }
//
//  public void populateMap() {
//    nodesList = dbController.floorNodes(4, "Faulkner");
//    masterNodes = new HashMap<Circle, DbNode>();
//    Iterator iter = nodesList.iterator();
//    while (iter.hasNext()) {
//      DbNode newNode = (DbNode) iter.next();
//      Circle newMapNode = makeNode(newNode);
//      pane_nodes.getChildren().add(newMapNode);
//      masterNodes.put(newMapNode, newNode);
//    }
//  }
//
//  public Circle makeNode(DbNode newNode) {
//    Circle newMapNode = new Circle();
//    newMapNode.setRadius(5);
//    newMapNode.setLayoutX(newNode.getX() * SCALE);
//    newMapNode.setLayoutY(newNode.getY() * SCALE);
//    newMapNode.setFill(Color.PURPLE);
//    newMapNode.setOnMouseClicked(
//            mouseEvent -> {
//              if (newMapNode.getFill() == Color.PURPLE) {
//                newMapNode.setFill(Color.RED);
//                selectedNodes.add(masterNodes.get(newMapNode));
//              } else {
//                newMapNode.setFill(Color.PURPLE);
//                selectedNodes.remove(masterNodes.get(newMapNode));
//              }
//            });
//    return newMapNode;
//  }
//
//  @FXML
//  private void onFindPathClicked(MouseEvent event) throws Exception {
//    if (selectedNodes.size() != 2) {
//      System.out.println("Incorrect number of nodes");
//      return;
//    }
//
//    DbNode firstNode = selectedNodes.get(0);
//    DbNode secondNode = selectedNodes.get(1);
//    Path mapPath = Pathfinder.findPath(firstNode.getNodeID(), secondNode.getNodeID());
//    LinkedList<DbNode> pathNodes = mapPath.getPath();
//    drawPath(pathNodes);
//    for (Map.Entry<Circle, DbNode> entry : masterNodes.entrySet()) {
//      Circle mapNode = entry.getKey();
//      mapNode.setDisable(true);
//    }
//  }
//
//  private void drawPath(LinkedList<DbNode> pathNodes) {
//    int size = pathNodes.size();
//    DbNode firstNode;
//    DbNode secondNode;
//    for (int i = 0; i < size - 1; i++) {
//      firstNode = pathNodes.get(i);
//      secondNode = pathNodes.get(i + 1);
//      Line line =
//              new Line(
//                      firstNode.getX() * SCALE,
//                      firstNode.getY() * SCALE,
//                      secondNode.getX() * SCALE,
//                      secondNode.getY() * SCALE);
//
//      pane_nodes.getChildren().add(line);
//    }
//  }
//
//  @FXML
//  private void onResetClicked(MouseEvent event) throws Exception {
//    for (Map.Entry<Circle, DbNode> entry : masterNodes.entrySet()) {
//      Circle mapNode = entry.getKey();
//      mapNode.setFill(Color.PURPLE);
//      mapNode.setDisable(false);
//    }
//    pane_nodes.getChildren().removeIf(node -> node instanceof Line);
//    selectedNodes.clear();
//  }
//
//  @FXML
//  private void onNavClicked(MouseEvent event) throws IOException {
//
//    Stage stage = null;
//    Parent root = null;
//    if (event.getSource() == btn_previous) {
//      stage = (Stage) btn_previous.getScene().getWindow();
//      root = FXMLLoader.load(getClass().getResource("nodeTableEditor.fxml"));
//    } else {
//      stage = (Stage) btn_next.getScene().getWindow();
//      root = FXMLLoader.load(getClass().getResource("kioskHome.fxml"));
//    }
//
//    if (stage != null && root != null) {
//      Scene scene = new Scene(root);
//      stage.setScene(scene);
//      stage.show();
//    }
//  }
// }
