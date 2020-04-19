package edu.wpi.N.views;

import com.google.common.collect.HashBiMap;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import edu.wpi.N.entities.DbNode;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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

  @FXML TextField txt_add_longName;
  @FXML TextField txt_add_shortName;
  @FXML TextField txt_add_type;
  @FXML Button btn_add_newNode;
  @FXML Button btn_add_cancel;
  @FXML Button btn_add_save;

  @FXML ListView lst_selected;
  @FXML Button btn_delete_clear;
  @FXML Button btn_delete;

  HashBiMap<Circle, DbNode> masterNodes; // stores the map nodes and their respective database nodes
  LinkedList<DbNode> allFloorNodes; // stores all the nodes on the floor
  LinkedList<DbNode> selectedNodes; // stores all the selected nodes on the map
  Circle tempNode;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException, DBException {
    selectedNodes = new LinkedList<DbNode>();
    allFloorNodes = DbController.floorNodes(4, "Faulkner");
    masterNodes = HashBiMap.create();
    tempNode = null;
    populateMap();
    //    accordionListener();
  }
  //
  //  public void accordionListener() {
  //    acc_modify
  //        .expandedPaneProperty()
  //        .addListener(
  //            (observable, oldValue, newValue) -> {
  //              if (newValue != null) {
  //                if (newValue.equals(pn_nodes)) {
  //                  accordionListenerNodes();
  //                } else if (newValue.equals(pn_edges)) {
  //                  System.out.println("Edges");
  //                }
  //              }
  //            });
  //  }
  //
  //  public void accordionListenerNodes() {
  //    acc_nodes
  //        .expandedPaneProperty()
  //        .addListener(
  //            (observable, oldValue, newValue) -> {
  //              if (newValue != null) {
  //                if (newValue.equals(pn_nodes_delete)) {
  //
  //                } else if (newValue.equals(pn_nodes_edit)) {
  //
  //                }
  //              }
  //            });
  //  }
  //
  //  @FXML ListView lst_delete;
  //  @FXML Button btn_delete_clear;
  //  @FXML Button btn_delete;

  public void onBtnNewNodeClicked() {
    txt_add_longName.setDisable(false);
    txt_add_shortName.setDisable(false);
    txt_add_type.setDisable(false);
    btn_add_cancel.setDisable(false);
    btn_add_save.setDisable(false);
    btn_add_newNode.setDisable(true);

    tempNode = new Circle();
    tempNode.setRadius(6);
    tempNode.setCenterX(IMAGE_WIDTH / 2);
    tempNode.setCenterY(SCREEN_HEIGHT / 2);
    tempNode.setFill(Color.BLACK);
    tempNode.setOpacity(0.7);
    tempNode.setOnMouseDragged(event -> this.onDragNode(event, tempNode));
    pn_display.getChildren().add(tempNode);
  }

  public void onBtnCancelClicked() {
    pn_display.getChildren().remove(tempNode);
    tempNode = null;
    txt_add_longName.clear();
    txt_add_shortName.clear();
    txt_add_type.clear();
    txt_add_longName.setDisable(true);
    txt_add_shortName.setDisable(true);
    txt_add_type.setDisable(true);
    btn_add_newNode.setDisable(false);
    btn_add_save.setDisable(true);
    btn_add_cancel.setDisable(true);
  }

  public void onBtnSaveClicked() throws DBException {
    String longName = txt_add_longName.getText();
    String shortName = txt_add_longName.getText();
    String type = txt_add_type.getText().toUpperCase();
    if (longName.equals("") || shortName.equals("") || type.equals("") || type.length() != 4) {
      return;
    }
    int x = (int) ((float) tempNode.getCenterX() / HORIZONTAL_SCALE);
    int y = (int) ((float) tempNode.getCenterY() / VERTICAL_SCALE);

    DbNode newNode = DbController.addNode(x, y, 4, "Faulkner", type, longName, shortName);
    Circle mapNode = makeMapNode(newNode);
    pn_display.getChildren().remove(tempNode);
    pn_display.getChildren().add(mapNode);
    masterNodes.put(mapNode, newNode);
    txt_add_longName.clear();
    txt_add_shortName.clear();
    txt_add_type.clear();
    txt_add_longName.setDisable(true);
    txt_add_shortName.setDisable(true);
    txt_add_type.setDisable(true);
    btn_add_newNode.setDisable(false);
    btn_add_save.setDisable(true);
    btn_add_cancel.setDisable(true);
  }

  public void onDragNode(MouseEvent event, Circle tempNode) {
    tempNode.setCenterX(event.getX());
    tempNode.setCenterY(event.getY());
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
      Label lbl = new Label();
      lbl.setText(masterNodes.get(mapNode).getLongName());
      lst_selected.getItems().add(lbl);

    } else {
      mapNode.setFill(Color.PURPLE);
      selectedNodes.remove(masterNodes.get(mapNode));
      lst_selected
          .getItems()
          .removeIf(n -> ((Label) n).getText().equals(masterNodes.get(mapNode).getLongName()));
    }
  }
}
