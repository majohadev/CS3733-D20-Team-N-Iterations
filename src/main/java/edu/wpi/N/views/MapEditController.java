package edu.wpi.N.views;

import com.google.common.collect.HashBiMap;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import edu.wpi.N.entities.DbNode;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class MapEditController implements Controller {
  App mainApp = null;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

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

  @FXML Accordion acc_edges;
  @FXML TitledPane pn_edges_add;
  @FXML TitledPane pn_edges_delete;
  @FXML TitledPane pn_edges_edit;

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

  @FXML TextField txt_EdgesAddFirstLocation;
  @FXML TextField txt_EdgesAddSecondLocation;
  @FXML CheckBox chk_EdgesAddShowFirst;
  @FXML CheckBox chk_EdgesAddShowSecond;
  @FXML Pane pn_firstEdges;
  @FXML Pane pn_secondEdges;
  @FXML Button btn_return;
  @FXML Button btn_EdgesAdd;

  // EDGES REMOVE
  @FXML TextField txt_EdgesDeleteNode;
  @FXML TextField txt_EdgesDeleteEdge;
  @FXML Button btn_EdgesDelete;
  Line line_EdgesDeleteSelected;
  DbNode db_EdgesDeleteFirstSelected;
  DbNode db_EdgesDeleteSecondSelected;

  // EDGES EDIT
  @FXML TextField txt_EdgesEditStartNode;
  @FXML TextField txt_EdgesEditEdge;
  @FXML TextField txt_EdgesEditEndNode;
  @FXML Button btn_EdgesEditConfirm;
  Line line_EdgesEditSelected;
  DbNode db_EdgesEditFirstSelected;
  DbNode db_EdgesEditSecondSelected;
  DbNode db_EdgesEditSecondSelectedOld;

  HashBiMap<Circle, DbNode> masterNodes; // stores the map nodes and their respective database nodes
  LinkedList<DbNode> allFloorNodes; // stores all the nodes on the floor
  LinkedList<DbNode> selectedNodes; // stores all the selected nodes on the map
  DbNode[] edgeNodes;
  Circle tempNode;
  DbNode editingNode;
  EditMode editMode;

  public enum EditMode {
    NOSTATE,
    NODES_ADD,
    NODES_DELETE,
    NODES_EDIT,
    EDGES_ADD,
    EDGES_DELETE,
    EDGES_EDIT
  }

  public void initialize() throws DBException, DBException {
    selectedNodes = new LinkedList<DbNode>();
    allFloorNodes = DbController.floorNodes(4, "Faulkner");
    masterNodes = HashBiMap.create();
    tempNode = null;
    editMode = editMode.NOSTATE;
    editingNode = null;
    edgeNodes = new DbNode[2];
    line_EdgesDeleteSelected = null;
    db_EdgesDeleteFirstSelected = null;
    db_EdgesDeleteSecondSelected = null;
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
                  accordionListenerEdges();
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
                  resetPanes();
                } else if (newValue.equals(pn_nodes_delete)) {
                  editMode = EditMode.NODES_DELETE;
                  resetPanes();
                } else if (newValue.equals(pn_nodes_edit)) {
                  editMode = EditMode.NODES_EDIT;
                  resetPanes();
                }
              }
            });
  }

  public void accordionListenerEdges() {
    acc_edges
        .expandedPaneProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null) {
                if (newValue.equals(pn_edges_add)) {
                  resetPanes();
                  editMode = EditMode.EDGES_ADD;
                  checkBoxListener(chk_EdgesAddShowFirst);
                  checkBoxListener(chk_EdgesAddShowSecond);
                } else if (newValue.equals(pn_edges_delete)) {
                  editMode = EditMode.EDGES_DELETE;
                  resetPanes();
                } else if (newValue.equals(pn_edges_edit)) {
                  editMode = EditMode.EDGES_EDIT;
                  resetPanes();
                }
              }
            });
  }

  public void resetPanes() {
    // RESET DELETE
    for (Circle mapNode : masterNodes.keySet()) {
      mapNode.setFill(Color.PURPLE);
      mapNode.setDisable(false);
    }
    selectedNodes.clear();
    lst_selected.getItems().clear();
    // RESET ADD
    if (tempNode != null) {
      pn_display.getChildren().remove(tempNode);
      tempNode = null;
    }
    txt_add_longName.setDisable(true);
    txt_add_longName.clear();
    txt_add_shortName.setDisable(true);
    txt_add_shortName.clear();
    txt_add_type.setDisable(true);
    txt_add_type.clear();
    btn_add_newNode.setDisable(false);
    btn_add_cancel.setDisable(true);
    btn_add_save.setDisable(true);
    // RESET EDIT
    if (editingNode != null) {
      Circle lastMapNode = masterNodes.inverse().get(editingNode);
      lastMapNode.setFill(Color.PURPLE);
      lastMapNode.setCenterX(editingNode.getX() * HORIZONTAL_SCALE);
      lastMapNode.setCenterY(editingNode.getY() * VERTICAL_SCALE);
      editingNode = null;
    }
    txt_NodesEditLongName.setDisable(true);
    txt_NodesEditLongName.clear();
    txt_NodesEditShortName.setDisable(true);
    txt_NodesEditShortName.clear();
    // RESET EDGES ADD
    Arrays.fill(edgeNodes, null);
    txt_EdgesAddFirstLocation.clear();
    txt_EdgesAddSecondLocation.clear();
    chk_EdgesAddShowFirst.setSelected(false);
    chk_EdgesAddShowSecond.setSelected(false);
    chk_EdgesAddShowFirst.setDisable(true);
    chk_EdgesAddShowSecond.setDisable(true);
    pn_firstEdges.getChildren().clear();
    pn_secondEdges.getChildren().clear();

    // RESET EDGES DELETE
    resetEdgesDelete();

    // RESET EDGES EDIT
    resetEdgesEdit();
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
    String shortName = txt_add_shortName.getText();
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
    DbNode newNode = DbController.getNode(editingNode.getNodeID());
    // CHECK
    masterNodes.replace(masterNodes.inverse().get(editingNode), newNode);
    masterNodes.inverse().get(newNode).setFill(Color.PURPLE);
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
    mapNode.setOnMouseClicked(
        mouseEvent -> {
          try {
            this.onMapNodeClicked(mapNode);
          } catch (DBException e) {
            e.printStackTrace();
          }
        });
    return mapNode;
  }

  public void onMapNodeClicked(Circle mapNode) throws DBException {
    if (editMode == EditMode.NODES_ADD) {
      return;
    } else if (editMode == EditMode.NODES_DELETE) {
      nodesDelete(mapNode);
    } else if (editMode == EditMode.NODES_EDIT) {
      nodesEdit(mapNode);
    } else if (editMode == EditMode.EDGES_ADD) {
      edgesAdd(mapNode);
    } else if (editMode == EditMode.EDGES_DELETE) {
      edgesDeleteNodeClick(mapNode);
    } else if (editMode == EditMode.EDGES_EDIT) {
      edgesEditNodeClick(mapNode);
    }
  }

  public void nodesEdit(Circle mapNode) {
    btn_NodesEditSave.setDisable(false);
    txt_NodesEditShortName.setDisable(false);
    txt_NodesEditLongName.setDisable(false);
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

  // EDGES ADD METHODS
  public void onTxtEdgesAddChooseFirstClicked() {
    txt_EdgesAddFirstLocation.requestFocus();
  }

  public void onTxtEdgesAddChooseSecondClicked() {
    txt_EdgesAddSecondLocation.requestFocus();
  }

  public void edgesAdd(Circle mapNode) {
    System.out.println("Hello");
    if (txt_EdgesAddFirstLocation.isFocused()) {
      if (masterNodes.get(mapNode) == edgeNodes[1]) {
        return;
      }
      pn_display.getChildren().removeIf(node -> node instanceof Line);
      chk_EdgesAddShowFirst.setSelected(false);
      txt_EdgesAddFirstLocation.setText(masterNodes.get(mapNode).getShortName());
      chk_EdgesAddShowFirst.setDisable(false);
      if (edgeNodes[0] != null) {
        masterNodes.inverse().get(edgeNodes[0]).setFill(Color.PURPLE);
      }
      edgeNodes[0] = masterNodes.get(mapNode);
      mapNode.setFill(Color.GREEN);
      if (edgeNodes[1] != null) {
        DbNode firstNode = edgeNodes[0];
        DbNode secondNode = edgeNodes[1];
        Line line =
            new Line(
                firstNode.getX() * HORIZONTAL_SCALE,
                firstNode.getY() * VERTICAL_SCALE,
                secondNode.getX() * HORIZONTAL_SCALE,
                secondNode.getY() * VERTICAL_SCALE);
        line.setFill(Color.RED);
        pn_display.getChildren().add(line);
      }
    } else if (txt_EdgesAddSecondLocation.isFocused()) {
      if (masterNodes.get(mapNode) == edgeNodes[0]) {
        return;
      }
      pn_display.getChildren().removeIf(node -> node instanceof Line);
      chk_EdgesAddShowSecond.setSelected(false);
      txt_EdgesAddSecondLocation.setText(masterNodes.get(mapNode).getShortName());
      chk_EdgesAddShowSecond.setDisable(false);
      if (edgeNodes[1] != null) {
        masterNodes.inverse().get(edgeNodes[1]).setFill(Color.PURPLE);
      }
      edgeNodes[1] = masterNodes.get(mapNode);
      mapNode.setFill(Color.GREEN);
      if (edgeNodes[0] != null) {
        DbNode firstNode = edgeNodes[0];
        DbNode secondNode = edgeNodes[1];
        Line line =
            new Line(
                firstNode.getX() * HORIZONTAL_SCALE,
                firstNode.getY() * VERTICAL_SCALE,
                secondNode.getX() * HORIZONTAL_SCALE,
                secondNode.getY() * VERTICAL_SCALE);
        line.setStroke(Color.RED);
        pn_display.getChildren().add(line);
      }
    }
  }

  public void checkBoxListener(CheckBox chk) {
    chk.selectedProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue) {
                if (chk == chk_EdgesAddShowFirst) {
                  displayPaths(0);
                } else if (chk == chk_EdgesAddShowSecond) {
                  displayPaths(1);
                }
              }
              if (!newValue) {
                if (chk == chk_EdgesAddShowFirst) {
                  pn_firstEdges.getChildren().clear();
                } else if (chk == chk_EdgesAddShowSecond) {
                  pn_secondEdges.getChildren().clear();
                }
              }
            });
  };

  public void displayPaths(int index) {
    LinkedList<DbNode> adjacentNodes = null;
    try {
      adjacentNodes = DbController.getAdjacent(edgeNodes[index].getNodeID());
    } catch (DBException e) {
      e.printStackTrace();
    }
    Circle firstMapNode = masterNodes.inverse().get(edgeNodes[index]);
    for (DbNode node : adjacentNodes) {
      Line line =
          new Line(
              firstMapNode.getCenterX(),
              firstMapNode.getCenterY(),
              node.getX() * HORIZONTAL_SCALE,
              node.getY() * VERTICAL_SCALE);
      if (index == 0) {
        pn_firstEdges.getChildren().add(line);
      } else {
        pn_secondEdges.getChildren().add(line);
      }
    }
  }

  public void onBtnAddEdgeClicked() throws DBException {
    if (!txt_EdgesAddFirstLocation.getText().equals("")
        && (!txt_EdgesAddSecondLocation.getText().equals(""))) {
      pn_display.getChildren().removeIf(node -> node instanceof Line);
      DbController.addEdge(edgeNodes[0].getNodeID(), edgeNodes[1].getNodeID());
      resetPanes();
    }
  }

  /**
   * Executes when a node on the map is clicked in EDGE DELETE mode Displays all edges of the node
   * and makes then clickable
   *
   * @param mapNode the clicked circle on the map
   */
  public void edgesDeleteNodeClick(Circle mapNode) throws DBException {
    if (txt_EdgesDeleteNode.isFocused()) {
      if (db_EdgesDeleteFirstSelected != null) {
        masterNodes.inverse().get(db_EdgesDeleteFirstSelected).setFill(Color.PURPLE);
        db_EdgesDeleteSecondSelected = null;
        line_EdgesDeleteSelected = null;
        pn_display.getChildren().removeIf(node -> node instanceof Line);
        txt_EdgesDeleteEdge.setText("");
      }
      db_EdgesDeleteFirstSelected = masterNodes.get(mapNode);
      displayAdjacentEdges(db_EdgesDeleteFirstSelected, mapNode);
      mapNode.setFill(Color.GREEN);
      txt_EdgesDeleteNode.setText(db_EdgesDeleteFirstSelected.getShortName());
    }
  }

  /**
   * Displays all edges of a node and makes them clickable if in correct edit mode
   *
   * @param centerNode
   * @param centerMapNode
   * @throws DBException
   */
  public void displayAdjacentEdges(DbNode centerNode, Circle centerMapNode) throws DBException {
    LinkedList<DbNode> adjacentNodes = DbController.getAdjacent(centerNode.getNodeID());
    for (DbNode adjacentNode : adjacentNodes) {
      double x1 = centerMapNode.getCenterX();
      double y1 = centerMapNode.getCenterY();
      float x2 = adjacentNode.getX() * HORIZONTAL_SCALE;
      float y2 = adjacentNode.getY() * VERTICAL_SCALE;
      Line line = new Line(x1, y1, x2, y2);
      line.setStrokeWidth(3);
      if (editMode == EditMode.EDGES_DELETE) {
        line.setOnMouseClicked(
            mouseEvent -> {
              if (line_EdgesDeleteSelected != null) {
                line_EdgesDeleteSelected.setStroke(Color.BLACK);
              }
              line_EdgesDeleteSelected = line;
              line.setStroke(Color.RED);
              txt_EdgesDeleteEdge.setText(adjacentNode.getShortName());
              db_EdgesDeleteSecondSelected = adjacentNode;
            });
      } else if (editMode == EditMode.EDGES_EDIT) {
        line.setOnMouseClicked(
            mouseEvent -> {
              if (txt_EdgesEditEdge.isFocused()) {
                if (line_EdgesEditSelected != null) {
                  line_EdgesEditSelected.setStroke(Color.BLACK);
                }
                db_EdgesEditSecondSelectedOld = adjacentNode;
                line_EdgesEditSelected = line;
                line.setStroke(Color.RED);
                line.setOpacity(0.5);
                txt_EdgesEditEdge.setText(adjacentNode.getShortName());
              }
            });
      }

      pn_display.getChildren().add(line);
    }
  }

  public void resetEdgesDelete() {
    if (db_EdgesDeleteFirstSelected != null) {
      masterNodes.inverse().get(db_EdgesDeleteFirstSelected).setFill(Color.PURPLE);
    }
    db_EdgesDeleteSecondSelected = null;
    line_EdgesDeleteSelected = null;
    pn_display.getChildren().removeIf(node -> node instanceof Line);
    txt_EdgesDeleteEdge.setText("");
    txt_EdgesDeleteNode.setText("");
  }

  public void onBtnEdgesDeleteClicked() throws DBException {
    DbController.removeEdge(
        db_EdgesDeleteFirstSelected.getNodeID(), db_EdgesDeleteSecondSelected.getNodeID());
    DbNode node = db_EdgesDeleteFirstSelected;
    resetEdgesDelete();
    txt_EdgesDeleteNode.requestFocus();
    edgesDeleteNodeClick(masterNodes.inverse().get(node));
  }

  // EDIT EDGES METHODS
  public void edgesEditNodeClick(Circle mapNode) throws DBException {
    if (txt_EdgesEditStartNode.isFocused()) {
      if (db_EdgesEditFirstSelected != null) {
        resetEdgesEdit();
      }
      db_EdgesEditFirstSelected = masterNodes.get(mapNode);
      displayAdjacentEdges(db_EdgesEditFirstSelected, mapNode);
      mapNode.setFill(Color.GREEN);
      txt_EdgesEditStartNode.setText(db_EdgesEditFirstSelected.getShortName());
    } else if (txt_EdgesEditEndNode.isFocused()) {
      db_EdgesEditSecondSelected = masterNodes.get(mapNode);
      mapNode.setFill(Color.SADDLEBROWN);
      txt_EdgesEditEndNode.setText(db_EdgesEditSecondSelected.getShortName());
      double x1 = masterNodes.inverse().get(db_EdgesEditFirstSelected).getCenterX();
      double y1 = masterNodes.inverse().get(db_EdgesEditFirstSelected).getCenterY();
      double x2 = mapNode.getCenterX();
      double y2 = mapNode.getCenterY();
      pn_firstEdges.getChildren().clear();
      Line line = new Line(x1, y1, x2, y2);
      pn_display.getChildren().remove(line_EdgesEditSelected);
      line.setStroke(Color.MAGENTA);
      line.setStrokeWidth(2);
      pn_firstEdges.getChildren().add(line);
    }
  }

  public void onBtnEdgesEditDeleteClicked() throws DBException {
    DbController.removeEdge(
        db_EdgesEditFirstSelected.getNodeID(), db_EdgesEditSecondSelectedOld.getNodeID());
    DbController.addEdge(
        db_EdgesEditFirstSelected.getNodeID(), db_EdgesEditSecondSelected.getNodeID());
    DbNode node = db_EdgesEditFirstSelected;
    resetEdgesEdit();
    txt_EdgesEditStartNode.requestFocus();
    edgesEditNodeClick(masterNodes.inverse().get(node));
  }

  public void resetEdgesEdit() {
    if (db_EdgesEditFirstSelected != null) {
      masterNodes.inverse().get(db_EdgesEditFirstSelected).setFill(Color.PURPLE);
    }
    if (db_EdgesEditSecondSelected != null) {
      masterNodes.inverse().get(db_EdgesEditSecondSelected).setFill(Color.PURPLE);
    }
    db_EdgesEditSecondSelected = null;
    db_EdgesEditFirstSelected = null;
    db_EdgesEditSecondSelectedOld = null;
    line_EdgesEditSelected = null;
    pn_display.getChildren().removeIf(node -> node instanceof Line);
    txt_EdgesEditEdge.setText("");
    txt_EdgesEditStartNode.setText("");
    txt_EdgesEditEndNode.setText("");
    pn_firstEdges.getChildren().clear();
  }

  public void onReturnClicked() throws IOException {
    mainApp.switchScene("views/home.fxml");
  }
}
