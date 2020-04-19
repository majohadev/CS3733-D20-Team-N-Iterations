package edu.wpi.N.views;

import com.google.common.collect.HashBiMap;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import edu.wpi.N.entities.DbNode;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MapEditController implements Controller {
  App mainApp = null;

  final float BAR_WIDTH = 400;
  final float IMAGE_WIDTH = 2475;
  final float IMAGE_HEIGHT = 1485;
  final float SCREEN_WIDTH = 1920;
  final float SCREEN_HEIGHT = 1080;
  final float MAP_WIDTH = SCREEN_WIDTH - BAR_WIDTH;
  final float MAP_HEIGHT = (MAP_WIDTH / IMAGE_WIDTH) * IMAGE_HEIGHT;
  final float HORIZONTAL_SCALE = (MAP_WIDTH) / IMAGE_WIDTH;
  final float VERTICAL_SCALE = (MAP_HEIGHT) / IMAGE_HEIGHT;

  @FXML Pane pn_display;
  @FXML Accordion acc_modify;
  @FXML TitledPane pn_nodes;
  @FXML TitledPane pn_edges;

  @FXML Accordion acc_nodes;
  @FXML TitledPane pn_nodes_add;
  @FXML TitledPane pn_nodes_delete;
  @FXML TitledPane pn_nodes_edit;

  HashBiMap<Circle, DbNode> masterNodes; // stores the map nodes and their respective database nodes
  LinkedList<DbNode> allFloorNodes; // stores all the nodes on the floor
  LinkedList<DbNode> selectedNodes; // stores all the selected nodes on the map

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException, DBException {
    selectedNodes = new LinkedList<DbNode>();
    allFloorNodes = DbController.floorNodes(4, "Faulkner");
    masterNodes = HashBiMap.create();
    populateMap();
    accordionListener();
  }

  public void accordionListener() {
    acc_modify
        .expandedPaneProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (!newValue.equals(null)) {
                if (newValue.equals(pn_nodes)) {
                  accordionListenerNodes();
                } else if (newValue.equals(pn_edges)) {
                  System.out.println("Edges");
                }
              }
            });
  }

  public void accordionListenerNodes() {
    acc_nodes.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.equals(null)) {
        if (newValue.equals(pn_nodes_add)) {

        }
        else if (newValue.equals(pn_nodes_delete)) {

        }
        else if (newValue.equals(pn_nodes_edit)) {

        }
      }
    });
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
    mapNode.setLayoutX((node.getX() * HORIZONTAL_SCALE));
    mapNode.setLayoutY((node.getY() * VERTICAL_SCALE));
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
}
