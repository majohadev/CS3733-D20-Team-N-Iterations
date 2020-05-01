package edu.wpi.N.views.mapDisplay;

import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

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

  private final String FIRST_KIOSK = "NSERV00301";
  private final String THIRD_KIOSK = "NSERV00103";
  private final Color START_NODE_COLOR = Color.GREEN;
  private final Color END_NODE_COLOR = Color.RED;
  // FXML Item IDs
  @FXML StackPane pn_movableMap;
  @FXML Pane pn_path;
  @FXML ImageView img_map;
  @FXML Button btn_zoomIn, btn_zoomOut;

  /**
   * the constructor of MapBaseController
   *
   * @param singleton the algorithm singleton which determines the style of path finding
   */
  public MapBaseController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  /**
   * allows reference back the main application class of this program
   *
   * @param mainApp the main application class of this program
   */
  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  /**
   * initializes the MapBase Controller
   *
   * @throws DBException
   */
  public void initialize() throws DBException {}

  /**
   * sets the current building of the map display
   *
   * @param building the name of the building to be displayed
   */
  public void setBuilding(String building, int floor, Path currentPath) throws DBException {
    setFloor(building, floor, currentPath);
  }

  /**
   * loads the new floor image loads the floor image and the draws the path if necessary
   *
   * @param building the current building of the map display
   * @param floor the new floor of the map display
   * @param currentPath the current path finding nodes
   * @throws DBException
   */
  public void setFloor(String building, int floor, Path currentPath) throws DBException {
    clearPath();
    img_map.setImage(singleton.mapImageLoader.getMap(building, floor));
    if (!(currentPath == null || currentPath.isEmpty())) {
      drawPath(currentPath, floor);
    }
  }

  /**
   * @param currentPath
   * @param floor the current floor
   */
  public void drawPath(Path currentPath, int floor) {
    DbNode firstNode, secondNode;
    for (int i = 0; i < currentPath.size() - 1; i++) {
      firstNode = currentPath.get(i);
      secondNode = currentPath.get(i + 1);
      if (firstNode.getFloor() == floor && secondNode.getFloor() == floor) {
        if (i == 0) {
          drawCircle(firstNode, START_NODE_COLOR);
        } else if (i == currentPath.size() - 2) {
          drawCircle(secondNode, END_NODE_COLOR);
        }
      }
      Line line =
          new Line(
              scaleX(firstNode.getX()),
              scaleY(firstNode.getY()),
              scaleX(secondNode.getX()),
              scaleY(secondNode.getY()));
      pn_path.getChildren().add(line);
    }
  }

  /**
   * draws either the start of end circle on the map
   *
   * @param node the DbNode to be displayed on the map
   * @param c the color of the circle
   */
  public void drawCircle(DbNode node, Color c) {
    Circle circle = new Circle();
    circle.setCenterX(scaleX(node.getX()));
    circle.setCenterY(scaleY(node.getY()));
    circle.setFill(c);
  }

  public double scaleX(double x) {
    return x * HORIZONTAL_SCALE;
  }

  public double scaleY(double y) {
    return y * VERTICAL_SCALE;
  }
  /** Clears all the edges on the map display */
  public void clearPath() {
    // TODO: Insert any other memory cleanup methods here
    pn_path.getChildren().clear();
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
