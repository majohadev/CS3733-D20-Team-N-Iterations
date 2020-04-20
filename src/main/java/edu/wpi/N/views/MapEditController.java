package edu.wpi.N.views;

import com.google.common.collect.HashBiMap;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import edu.wpi.N.entities.DbNode;
import java.io.IOException;
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

  @FXML TextField txt_NodesEditLongName;
  @FXML TextField txt_NodesEditShortName;
  @FXML Button btn_NodesEditSave;

  HashBiMap<Circle, DbNode> masterNodes; // stores the map nodes and their respective database nodes
  LinkedList<DbNode> allFloorNodes; // stores all the nodes on the floor
  LinkedList<DbNode> selectedNodes; // stores all the selected nodes on the map
  Circle tempNode;
  DbNode editingNode;
  EditMode editMode;

  public enum EditMode {
    NOSTATE,
    NODES_ADD,
    NODES_DELETE,
    NODES_EDIT,
    EDGES_ADD,
    EDGES_DELETE
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException, DBException {
    selectedNodes = new LinkedList<DbNode>();
    allFloorNodes = DbController.floorNodes(4, "Faulkner");
    masterNodes = HashBiMap.create();
    tempNode = null;
    editMode = editMode.NOSTATE;
    editingNode = null;
    populateMap();
    accordionListener();
  }

  public void accordionListener() {
    acc_modify
        .expandedPaneProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null) {
                if (newValue.equals(pn_nodes)) {
                  accordionListenerNodes();
                } else if (newValue.equals(pn_edges)) {
                  onBtnClearClicked();
                  System.out.println("Edges");
                }
              }
            });
  }

  public void accordionListenerNodes() {
    acc_nodes
        .expandedPaneProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null) {
                if (newValue.equals(pn_nodes_add)) {
                  editMode = EditMode.NODES_ADD;
                  onBtnClearClicked();
                } else if (newValue.equals(pn_nodes_delete)) {
                  editMode = EditMode.NODES_DELETE;
                  onBtnClearClicked();
                } else if (newValue.equals(pn_nodes_edit)) {
                  editMode = EditMode.NODES_EDIT;
                  onBtnClearClicked();
                }
              }
            });
  }

  public void onBtnClearClicked() {
    for (Circle mapNode : masterNodes.keySet()) {
      mapNode.setFill(Color.PURPLE);
      mapNode.setDisable(false);
    }
    selectedNodes.clear();
    lst_selected.getItems().clear();
  }

  public void onBtnDeleteClicked() throws DBException {
    for (DbNode node : selectedNodes) {
      Circle mapNode = masterNodes.inverse().remove(node);
      pn_display.getChildren().remove(mapNode);
      DbController.deleteNode(node.getNodeID());
    }
    onBtnClearClicked();
  }

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

  public void onBtnNodesEditSaveClicked() throws DBException {
    int x = (int) ((float) masterNodes.inverse().get(editingNode).getCenterX() / HORIZONTAL_SCALE);
    int y = (int) ((float) masterNodes.inverse().get(editingNode).getCenterY() / VERTICAL_SCALE);
    String longName = txt_NodesEditLongName.getText();
    String shortName = txt_NodesEditShortName.getText();
    DbController.modifyNode(editingNode.getNodeID(), x, y, longName, shortName);
    masterNodes.inverse().get(editingNode).setFill(Color.PURPLE);
    txt_NodesEditShortName.clear();
    txt_NodesEditLongName.clear();
    btn_NodesEditSave.setDisable(true);
    editingNode = null;
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
    mapNode.setCenterX((node.getX() * HORIZONTAL_SCALE));
    mapNode.setCenterY((node.getY() * VERTICAL_SCALE));
    mapNode.setFill(Color.PURPLE);
    mapNode.setOpacity(0.7);
    mapNode.setOnMouseClicked(mouseEvent -> this.onMapNodeClicked(mapNode));
    return mapNode;
  }

  public void onMapNodeClicked(Circle mapNode) {
    if (editMode == EditMode.NODES_ADD) {
      return;
    } else if (editMode == EditMode.NODES_DELETE) {
      nodesDelete(mapNode);
    } else if (editMode == EditMode.NODES_EDIT) {
      nodesEdit(mapNode);
    }
  }

  public void nodesEdit(Circle mapNode) {
    btn_NodesEditSave.setDisable(false);
    DbNode newNode = masterNodes.get(mapNode);
    if (editingNode != null && !(editingNode == newNode)) {
      Circle lastMapNode = masterNodes.inverse().get(editingNode);
      lastMapNode.setFill(Color.PURPLE);
      lastMapNode.setCenterX(editingNode.getX() * HORIZONTAL_SCALE);
      lastMapNode.setCenterY(editingNode.getY() * VERTICAL_SCALE);
    }

    if (mapNode.getFill() != Color.GREEN) {
      editingNode = masterNodes.get(mapNode);
      mapNode.setFill(Color.GREEN);
      txt_NodesEditLongName.setText(editingNode.getLongName());
      txt_NodesEditShortName.setText(editingNode.getShortName());
      mapNode.setOnMouseDragged(event -> this.onDragNode(event, mapNode));
    }
  }

  public void nodesDelete(Circle mapNode) {
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

  public void onReturnClicked() throws IOException {
    mainApp.switchScene("views/home.fxml");
  }
}
