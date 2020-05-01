package edu.wpi.N.views.mapDisplay;

import com.google.common.collect.HashBiMap;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.entities.UIDispEdge;
import edu.wpi.N.entities.UIDispNode;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class MapBaseController implements Controller {

  // ApplicationStates
  private App mainApp;
  private StateSingleton singleton;

  // Screen Constants
  private final float BAR_WIDTH = 300;
  private final float IMAGE_WIDTH = 2475;
  private final float IMAGE_HEIGHT = 1485;
  private final float SCREEN_WIDTH = 1920;
  private final float SCREEN_HEIGHT = 1080;
  private final float MAP_WIDTH = SCREEN_WIDTH - BAR_WIDTH;
  private final float MAP_HEIGHT = (MAP_WIDTH / IMAGE_WIDTH) * IMAGE_HEIGHT;
  private final float HORIZONTAL_OFFSET = 10;
  private final float VERTICAL_OFFSET = 8;
  private final float HORIZONTAL_SCALE = (MAP_WIDTH) / IMAGE_WIDTH;
  private final float VERTICAL_SCALE = (MAP_HEIGHT) / IMAGE_HEIGHT;

  // Zoom constants
  private final double MIN_MAP_SCALE = 1;
  private final double MAX_MAP_SCALE = 3;
  private final double ZOOM_STEP_SCROLL = 0.01;
  private final double ZOOM_STEP_BUTTON = 0.1;
  private double mapScaleAlpha;
  private double clickStartX, clickStartY;

  //Program Constants
  private final String FIRST_KIOSK = "NSERV00301";
  private final String THIRD_KIOSK = "NSERV00103";
  private final String DEFAULT_BUILDING = "Faulkner";
  private final int DEFAULT_FLOOR = 1;

  // Program Variables
  private int currentFloor;
  private String currentBuilding;
  private LinkedList<DbNode> currentPath;

  // FXML Item IDs
  @FXML AnchorPane pn_movableMap;
  @FXML Pane pn_path;
  @FXML ImageView img_map;
  @FXML Button btn_zoomIn, btn_zoomOut;

  /**
   * the constructor of MapBaseController
   * @param singleton the algorithm singleton which determines the style of path finding
   */
  public MapBaseController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  /**
   * allows reference back the main application class of this program
   * @param mainApp the main application class of this program
   */
  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  /**
   * initializes the MapBase Controller
   * @throws DBException
   */
  public void initialize() throws DBException {
    this.currentFloor = DEFAULT_FLOOR;
    this.currentBuilding = DEFAULT_BUILDING;
  }

  /**
   * sets the current building of the map display
   * @param building the name of the building to be displayed
   */
  public void setBuilding(String building) {
    this.currentBuilding = building;
    setFloor(DEFAULT_FLOOR);
  }

  /**
   * sets the current floor of the map display
   * @param floor the floor number to be displayed
   */
  public void setFloor(int floor) {
    this.setFloor(floor);
  }


  public void changeFloor(int floorToDraw) {
    // Change floor in current building
    currentFloor = floorToDraw;

    // TODO: check later. I'm not sure if we need to refreshAllNodes when changing floors, right?
    singleton.mapData.refreshMapData();
    img_map.setImage(singleton.mapData.getMap(currentBuilding, floorToDraw));

    clearPath();
    clearEdges(); // Erase lines
    populateMap(); // Set up/recycle UIDispNodes

    // If there's a path to draw, draw it - otherwise just show the default kiosk
    if (mode == Mode.NO_STATE) {
      defaultKioskNode();
    } else if (mode == Mode.PATH_STATE) {
      if (pathNodes != null && !pathNodes.isEmpty()) {
        drawPath(pathNodes);
      } else {
        System.out.println("Tried to draw an empty path!");
      }
    }
  }

  public void setPathNodes(LinkedList<DbNode> nodesList) {
    this.pathNodes = nodesList;
  }

  public LinkedList<DbNode> getPathNodes() {
    return this.pathNodes;
  }

  public LinkedList<String> defaultKioskNode() {

    clearPath();

    LinkedList<String> kiosks = new LinkedList<>();

    if (currentFloor == 1) {
      try {
        DbNode floor1Kiosk = MapDB.getNode("NSERV00301"); // Should be specified elsewhere
        defaultNode = getUiFromDb(floor1Kiosk);
        kiosks.add(floor1Kiosk.getLongName());
      } catch (DBException e) {
        System.out.println("Couldn't find node NSERV00301 as 1st floor kiosk");
        e.printStackTrace();
      }

    } else if (currentFloor == 3) {
      try {
        DbNode floor3Kiosk = MapDB.getNode("NSERV00103"); // Should be specified elsewhere
        defaultNode = getUiFromDb(floor3Kiosk);
        kiosks.add(floor3Kiosk.getLongName());
      } catch (DBException e) {
        System.out.println("Couldn't find node NSERV00103 as 3rd floor kiosk");
        e.printStackTrace();
      }
    }
    return kiosks;
  }

  private UIDispNode makeUINode() {
    UIDispNode newNode = new UIDispNode(true);
    newNode.placeOnPane(pn_routeNodes);
    newNode.setBaseMap(this);
    return newNode;
  }

  private void addEdge(UIDispNode nodeA, UIDispNode nodeB) {
    UIDispEdge newEdge;
    if (freedEdges.isEmpty()) {
      newEdge = nodeA.addEdgeTo(nodeB, null);
    } else {
      newEdge = nodeA.addEdgeTo(nodeB, freedEdges.pop());
    }
    if (newEdge != null) {
      edgesInUse.add(newEdge);
      newEdge.placeOnPane(pn_path);
      newEdge.setBaseMap(this);
    }
  }

  public void clearEdges() {
    for (UIDispEdge edge : edgesInUse) {
      edge.breakSelf(); // Safely disassemble edge infrastructure
      freedEdges.add(edge);
    }
    edgesInUse.clear();
  }

  public DbNode getDbFromUi(UIDispNode uiNode) {
    return masterNodes.get(uiNode);
  }

  public UIDispNode getUiFromDb(DbNode dbNode) {
    return masterNodes.inverse().get(dbNode);
  }

  // Replace (or create) link between a UIDispNode and a DbNode
  private void setLink(UIDispNode uiNode, DbNode dbNode, boolean showKey) {
    if (uiNode != null && dbNode != null) {

      // if (dbNode.getNodeType().equals("HALL")) uiNode.setVisible(false);
      // else uiNode.setVisible(showKey);

      uiNode.setPos(
          dbNode.getX() * HORIZONTAL_SCALE + HORIZONTAL_OFFSET,
          dbNode.getY() * VERTICAL_SCALE + VERTICAL_OFFSET);
      if (masterNodes.replace(uiNode, dbNode) == null) { // If uiNode is a new map key
        masterNodes.put(uiNode, dbNode); // Add the new key
      }
      try { // Add any attached edges
        UIDispNode otherAsUI;
        for (DbNode other : MapDB.getAdjacent(dbNode.getNodeID())) {
          otherAsUI = getUiFromDb(other);
          if (otherAsUI != null) {
            addEdge(uiNode, otherAsUI);
          } else {
            // System.out.println("Couldn't find UI element for node " + other.getNodeID());
          }
        }
      } catch (DBException e) {
        System.out.println("No adjacent nodes to " + dbNode.getShortName());
      }
    }
  }

  // Called by the UIDispNode that was clicked
  public void onUINodeClicked(MouseEvent e, UIDispNode clickedNode) {

    if (clickedNode.getSelected()) {
      selectedNodes.add(clickedNode);
      // System.out.println("Selected node " + masterNodes.get(clickedNode).getLongName() + ".");
    } else {
      selectedNodes.remove(clickedNode);
      // System.out.println("Deselected node " + masterNodes.get(clickedNode).getLongName() + ".");
    }
  }

  // Called by the UIDispEdge that was clicked
  public void onUIEdgeClicked(MouseEvent e, UIDispEdge clickedEdge) {

    if (clickedEdge.getSelected()) {
      selectedEdges.add(clickedEdge);
      // System.out.println("Selected node " + masterNodes.get(clickedNode).getLongName() + ".");
    } else {
      selectedEdges.remove(clickedEdge);
      // System.out.println("Deselected node " + masterNodes.get(clickedNode).getLongName() + ".");
    }
  }

  public void populateMap() {
    try {

      LinkedList<UIDispNode> keys = new LinkedList<>();
      keys.addAll(masterNodes.keySet());

      allFloorNodes = MapDB.floorNodes(currentFloor, currentBuilding); // Reference copy

      LinkedList<DbNode> thisFloor = new LinkedList<>();
      thisFloor.addAll(allFloorNodes); // Make a copy of allFloorNodes to use pop or pull on

      Iterator<UIDispNode> keysIterator = keys.iterator();
      Iterator<DbNode> dbIterator = thisFloor.iterator();
      while (dbIterator.hasNext()) {
        DbNode value = dbIterator.next();
        if (keysIterator.hasNext()) {
          UIDispNode key = keysIterator.next();
          setLink(key, value, true);
        } else {
          // If there are DbNodes without keys, add keys
          setLink(makeUINode(), value, true);
        }
      }

      while (keysIterator.hasNext()) {
        UIDispNode key = keysIterator.next();
        masterNodes.remove(key);
        key.setVisible(false);
        key.breakAllEdges();
      }
    } catch (DBException e) {
      System.out.print("Populating floor " + currentFloor + " in  " + currentBuilding + " failed.");
      e.printStackTrace();
    }
  }

  // Draw lines between each pair of nodes in given path
  public void drawPath(LinkedList<DbNode> nodes) {
    UIDispNode uiFirst, uiSecond;
    DbNode dbFirst, dbSecond;
    UIDispEdge edge;

    for (int i = 0; i < nodes.size() - 1; i++) {
      dbFirst = nodes.get(i);
      dbSecond = nodes.get(i + 1);

      if (dbFirst.getFloor() == currentFloor && dbSecond.getFloor() == currentFloor) {

        uiFirst = getUiFromDb(dbFirst);
        uiSecond = getUiFromDb(dbSecond);

        if (i == 0) {
          uiFirst.setStart();
        } else if (i == nodes.size() - 2) {
          uiSecond.setEnd();
        }

        edge = uiFirst.edgeTo(uiSecond);
        if (edge != null) {
          edge.pointEdgeToward(uiSecond); // Set correct direction of line!
          edge.setHighlighted(true);
        } else {
          System.out.println(
              "Edge between "
                  + dbFirst.getNodeID()
                  + " and "
                  + dbSecond.getNodeID()
                  + " doesn't exist.");
        }
      }
    }
  }

  // Resets highlighted edges to their default state
  public void clearPath() {
    for (UIDispEdge edge : edgesInUse) {
      edge.setHighlighted(false);
    }
    for (UIDispNode node : masterNodes.keySet()) {
      node.setNormalNode();
    }
  }



  public void forceSelect(UIDispNode uiNode, boolean selected) { // Don't really want
    uiNode.setSelected(selected);
  }

  //   == MAP ZOOM CONTROLS ==

  // Get zoom button input
  @FXML
  private void zoomToolHandler(MouseEvent event) throws IOException {

    if (event.getSource() == btn_zoomIn) {
      zoom(ZOOM_STEP_BUTTON);
    } else if (event.getSource() == btn_zoomOut) {
      zoom(-ZOOM_STEP_BUTTON);
    }
  }

  // When user scrolls mouse over map
  @FXML
  private void mapScrollHandler(ScrollEvent event) throws IOException {
    if (event.getSource() == pn_movableMap) {
      double deltaY = event.getDeltaY();
      zoom(deltaY * ZOOM_STEP_SCROLL);
    }
  }

  /**
   * zoom - Scale map pane up or down, clamping value between MIN_MAP_SCALE and MAX_MAP_SCALE
   *
   * @param percentDelta - Signed double representing how much to zoom in/out
   */
  private void zoom(double percentDelta) {

    // Scaling parameter (alpha) is clamped between 0 (min. scale) and 1 (max. scale)
    mapScaleAlpha = Math.max(0, Math.min(1, mapScaleAlpha + percentDelta));

    // Linearly interpolate (lerp) alpha to actual scale value
    double lerpedScale = MIN_MAP_SCALE + mapScaleAlpha * (MAX_MAP_SCALE - MIN_MAP_SCALE);

    // Apply new scale and correct panning
    pn_movableMap.setScaleX(lerpedScale);
    pn_movableMap.setScaleY(lerpedScale);
    clampPanning(0, 0);
  }

  // == MAP PANNING CONTROLS ==

  // User begins drag
  @FXML
  private void mapPressHandler(MouseEvent event) throws IOException {
    if (event.getSource() == pn_movableMap) {
      pn_movableMap.setCursor(Cursor.CLOSED_HAND);
      clickStartX = event.getSceneX();
      clickStartY = event.getSceneY();
    }
  }

  // User is currently dragging
  @FXML
  private void mapDragHandler(MouseEvent event) throws IOException {
    if (event.getSource() == pn_movableMap) {

      double dragDeltaX = event.getSceneX() - clickStartX;
      double dragDeltaY = event.getSceneY() - clickStartY;

      clampPanning(dragDeltaX, dragDeltaY);

      clickStartX = event.getSceneX();
      clickStartY = event.getSceneY();
    }
  }

  // User ends drag
  @FXML
  private void mapReleaseHandler(MouseEvent event) throws IOException {
    pn_movableMap.setCursor(Cursor.OPEN_HAND);
  }

  /**
   * clampPanning - Attempts to move map by deltaX and deltaY, clamping movement to stay in-bounds
   *
   * @param deltaX - How many screen pixels to move the map horizontally
   * @param deltaY - How many screen pixels to move the map vertically
   */
  private void clampPanning(double deltaX, double deltaY) {
    double xLimit = (pn_movableMap.getScaleX() - MIN_MAP_SCALE) * MAP_WIDTH / 2;
    double yLimit = (pn_movableMap.getScaleY() - MIN_MAP_SCALE) * MAP_HEIGHT / 2;

    double newTranslateX =
        Math.min(Math.max(pn_movableMap.getTranslateX() + deltaX, -xLimit), xLimit);
    double newTranslateY =
        Math.min(Math.max(pn_movableMap.getTranslateY() + deltaY, -yLimit), yLimit);

    pn_movableMap.setTranslateX(newTranslateX);
    pn_movableMap.setTranslateY(newTranslateY);
  }
}
