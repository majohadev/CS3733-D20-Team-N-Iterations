package edu.wpi.N.views;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import edu.wpi.N.App;
import edu.wpi.N.Main;
import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import edu.wpi.N.entities.DbNode;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

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
  @FXML TextField txt_Location;
  @FXML TextArea txt_Notes;
  @FXML TextField txt_Location1;
  @FXML TextArea txt_Notes1;
  @FXML Button btn_SubmitLaundry;
  @FXML Button btn_SubmitTranslator;

  BiMap<Circle, DbNode> masterNodes; // stores the map nodes and their respective database nodes
  LinkedList<DbNode> allFloorNodes; // stores all the nodes on the floor
  LinkedList<DbNode> selectedNodes; // stores all the selected nodes on the map

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException, DBException {
    InputStream nodes = Main.class.getResourceAsStream("csv/MapEnodes.csv");
    //    InputStream edges = Main.class.getResourceAsStream("csv/MapEdges.csv");
    CSVParser.parseCSV(nodes);
    //    CSVParser.parseCSV(edges);
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
    mapNode.setRadius(5);
    System.out.println(HORIZONTAL_SCALE);
    System.out.println(VERTICAL_SCALE);
    mapNode.setLayoutX((node.getX() * HORIZONTAL_SCALE + HORIZONTAL_OFFSET));
    mapNode.setLayoutY((node.getY() * VERTICAL_SCALE + VERTICAL_OFFSET));
    mapNode.setFill(Color.PURPLE);
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
  public void dataHandler(MouseEvent event) throws IOException {
    String locationDataTranslator;
    String notesTranslator;
    String locationDataLaundry;
    String notesLaundry;

    if (event.getSource() == btn_SubmitTranslator) {
      locationDataTranslator = txt_Location1.getText();
      notesTranslator = txt_Notes1.getText();
      System.out.println("Location: " + locationDataTranslator + " Notes: " + notesTranslator);
    } else if (event.getSource() == btn_SubmitLaundry) {
      locationDataLaundry = txt_Location.getText();
      notesLaundry = txt_Notes.getText();
      System.out.println("Location: " + locationDataLaundry + " Notes: " + notesLaundry);
    }
  }
}

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
