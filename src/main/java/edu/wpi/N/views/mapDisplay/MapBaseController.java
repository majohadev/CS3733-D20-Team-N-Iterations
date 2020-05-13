package edu.wpi.N.views.mapDisplay;

import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class MapBaseController implements Controller {

  // ApplicationStates
  private App mainApp;
  private StateSingleton singleton;

  // Screen Constants
  double IMAGE_WIDTH;
  double IMAGE_HEIGHT;
  double MAP_WIDTH;
  double MAP_HEIGHT;
  double HORIZONTAL_SCALE;
  double VERTICAL_SCALE;

  // Zoom constants
  private double MIN_MAP_SCALE = 1;
  private double MAX_MAP_SCALE = 3;
  private final double ZOOM_STEP_SCROLL = 0.005;
  private final double ZOOM_STEP_BUTTON = 0.1;
  private double DEFAULT_TRANSLATEX = 0;
  private double DEFAULT_TRANSLATEY = 0;
  private DoubleProperty mapScaleAlpha = new SimpleDoubleProperty(0);
  private double clickStartX, clickStartY;

  private Timeline autoFocus = new Timeline();
  private LinkedList<KeyValue> endFocusVals = new LinkedList<>();

  private final Color START_NODE_COLOR = Color.GREEN;
  private final Color END_NODE_COLOR = Color.RED;
  private final Color MIDDLE_NODE_COLOR = Color.PURPLE;

  // Path GUI constants
  private final Color DEFAULT_PATH_COLOR = Color.DODGERBLUE; // Default color of path
  private final double DEFAULT_PATH_WIDTH = 4; // Default stroke width of path
  private final double LIGHT_OPACITY = 0.05; // Faded line
  private final double HEAVY_OPACITY = 0.8; // Bold line (default)
  private final ArrayList<Double> DASH_PATTERN =
      new ArrayList<>() {
        {
          add(20d);
          add(10d);
        }
      }; // Line-gap pattern
  private final double MAX_DASH_OFFSET =
      DASH_PATTERN.stream().reduce(0d, (a, b) -> (a + b)); // Pattern length
  private final int PATH_ANIM_LENGTH =
      2000; // How many milliseconds it takes for dashes to move up a spot
  private Timeline pathAnimTimeline = new Timeline(); // Timeline object to set line animation
  private KeyFrame keyStart, keyEnd; // Keyframes in path animation
  private ArrayList<KeyValue> keyStartVals, keyEndVals;
  private Label startLabel, endLabel;
  private final int NODE_LABEL_PADDING = 35;
  private double SCALE_VALUE;

  LinkedList<DbNode> pathCircles;

  // FXML Item IDs
  @FXML StackPane pn_movableMap;
  @FXML Pane pn_path;
  @FXML ImageView img_map;
  @FXML Button btn_zoomIn, btn_zoomOut;
  @FXML AnchorPane controllerAnchorPane;
  @FXML Pane pn_routeNodes;

  MapQRController mapQRController;
  NewMapDisplayController newMapDisplayController;

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

  public void setMapQRController(MapQRController mapQRController) {
    this.mapQRController = mapQRController;
  }

  public void setNewMapDisplayController(NewMapDisplayController newMapDisplayController) {
    this.newMapDisplayController = newMapDisplayController;
  }
  /**
   * initializes the MapBase Controller
   *
   * @throws DBException
   */
  public void initialize() throws DBException {
    pathCircles = new LinkedList<>();
    initAutoFocus();
    initNodeLabels();
    initPathAnim();
    setFaulknerDefaults();
    pn_routeNodes.layoutXProperty().addListener((nval) -> System.out.println("layout X " + nval));
    pn_routeNodes.layoutYProperty().addListener((nval) -> System.out.println("layout Y " + nval));
    pn_movableMap.layoutXProperty().addListener((nval) -> System.out.println("layout X m" + nval));
    pn_movableMap.layoutYProperty().addListener((nval) -> System.out.println("layout Y m" + nval));
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
    if (!building.equals("Faulkner")) {
      building = "Main";
      setMainDefaults();

    } else {
      setFaulknerDefaults();
    }
    if (newMapDisplayController != null) {
      newMapDisplayController.setFloorBuildingText(floor, building);
    }
    img_map.setImage(singleton.mapImageLoader.getMap(building, floor));
    resetFocus();
    if (!(currentPath == null || currentPath.isEmpty())) {
      drawPath(currentPath, floor, building);
    }
  }

  public void setFaulknerDefaults() {
    IMAGE_WIDTH = 2475;
    IMAGE_HEIGHT = 1485;
    MAP_WIDTH = 1520;
    MAP_HEIGHT = 912;
    HORIZONTAL_SCALE = MAP_WIDTH / IMAGE_WIDTH;
    VERTICAL_SCALE = MAP_HEIGHT / IMAGE_HEIGHT;
    MIN_MAP_SCALE = 1;
    MAX_MAP_SCALE = 3.2;
    DEFAULT_TRANSLATEX = 0;
    DEFAULT_TRANSLATEY = 100;
  }

  public void setMainDefaults() {
    IMAGE_WIDTH = 5000;
    IMAGE_HEIGHT = 3400;
    MAP_WIDTH = 1520;
    MAP_HEIGHT = 1034;
    HORIZONTAL_SCALE = MAP_WIDTH / IMAGE_WIDTH;
    VERTICAL_SCALE = MAP_HEIGHT / IMAGE_HEIGHT;
    MIN_MAP_SCALE = 1.2;
    MAX_MAP_SCALE = 5.5;
    DEFAULT_TRANSLATEX = 0;
    DEFAULT_TRANSLATEY = 0;
  }

  /**
   * Returns AnchorPane of this controller
   *
   * @return
   */
  public AnchorPane getAnchorPane() {
    return this.controllerAnchorPane;
  }

  private void initPathAnim() {
    // Set timeline to repeat
    pathAnimTimeline.setCycleCount(Timeline.INDEFINITE);

    // Init keyval lists
    keyStartVals = new ArrayList<>();
    keyEndVals = new ArrayList<>();

    // Setup initial (empty) frames
    setAnimFrames();
  }

  private void initNodeLabels() {
    startLabel = new Label();
    startLabel.setTextAlignment(TextAlignment.CENTER);
    startLabel.setAlignment(Pos.CENTER);
    startLabel.setMouseTransparent(true);
    startLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
    startLabel.setBorder(
        new Border(
            new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
    endLabel = new Label();
    endLabel.setTextAlignment(TextAlignment.CENTER);
    endLabel.setAlignment(Pos.CENTER);
    endLabel.setMouseTransparent(true);
    endLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
    endLabel.setBorder(
        new Border(
            new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
  }

  /**
   * Draws lines between each location specified by currentPath
   *
   * @param currentPath Path object containing the DbNodes picked by pathfinder algorithm
   * @param floor The current floor
   */
  public void drawPath(Path currentPath, int floor, String building) {
    clearPath();
    pathCircles.clear();
    DbNode firstNode, secondNode;
    if (currentPath.size() > 1) {
      pathCircles.add(currentPath.get(0));
      pathCircles.add(currentPath.get(currentPath.size() - 1));
    } else {
      pathCircles.add(currentPath.get(0));
    }
    //    startLabel.setText("Start: ");
    //    endLabel.setText("Destination: ");
    //    if (currentPath.get(0).getFloor() == floor) {
    //      // drawCircle(currentPath.get(0), START_NODE_COLOR, startLabel);
    //    } else if (currentPath.get(currentPath.size() - 1).getFloor() == floor) {
    //      drawCircle(currentPath.get(currentPath.size() - 1), END_NODE_COLOR, endLabel);
    //    }
    boolean first = true;

    for (int i = 0; i < currentPath.size() - 1; i++) {
      firstNode = currentPath.get(i);
      secondNode = currentPath.get(i + 1);
      boolean isFirstFaulkner = firstNode.getBuilding().equals("Faulkner");
      boolean isSecondFaulkner = secondNode.getBuilding().equals("Faulkner");
      boolean drawFaulkner = building.equals("Faulkner") && isFirstFaulkner && isSecondFaulkner;
      boolean drawMain = !building.equals("Faulkner") && !isFirstFaulkner && !isSecondFaulkner;
      if (firstNode.getFloor() == floor
          && secondNode.getFloor() == floor
          && (drawFaulkner || drawMain)) {

        Line line =
            new Line(
                scaleX(secondNode.getX()),
                scaleY(secondNode.getY()),
                scaleX(firstNode.getX()),
                scaleY(firstNode.getY()));
        styleLine(line);
        pn_path.getChildren().add(line);

        if (first) {
          first = false;
          startLabel.setVisible(true);
          endLabel.setVisible(true);
          startLabel.setText("Start at ");
          autoFocusToNode(firstNode); // TODO: Place this somewhere better
          // autoFocusToNodesGroup(pathCircles, 0);
          drawCircle(firstNode, START_NODE_COLOR, startLabel);
        } else if (i == currentPath.size() - 2) {
          startLabel.setVisible(true);
          endLabel.setVisible(true);
          endLabel.setText("Destination: ");
          drawCircle(secondNode, END_NODE_COLOR, endLabel);
        } else if (currentPath.get(i - 1).getFloor() != floor) {
          // If firstNode is first on current floor
          startLabel.setVisible(true);
          endLabel.setVisible(true);
          startLabel.setText("Exit from ");

          // autoFocusToNode(firstNode); // TODO: Place this somewhere better
          // autoFocusToNodesGroup(pathCircles, 0.1);
          Circle circle = drawCircle(firstNode, MIDDLE_NODE_COLOR, startLabel);
          circle.setCursor(Cursor.HAND);
          DbNode finalFirstNode = firstNode;
          circle.setOnMouseClicked(
              e -> {
                LinkedList<DbNode> path = currentPath.getPath();
                DbNode prev = path.get(path.indexOf(finalFirstNode) - 1);
                while (prev.getNodeType().equals("STAI") || prev.getNodeType().equals("ELEV")) {
                  prev = path.get(path.indexOf(prev) - 1);
                }
                try {
                  newMapDisplayController.setCurrentFloor(prev.getFloor());
                  newMapDisplayController.setCurrentBuilding(prev.getBuilding());
                  setFloor(prev.getBuilding(), prev.getFloor(), currentPath);
                  if (mapQRController != null) {
                    if (!prev.getBuilding().equals("Faulkner")) {
                      mapQRController.setTabFocus(prev.getFloor(), "Main");
                    } else {
                      mapQRController.setTabFocus(prev.getFloor(), prev.getBuilding());
                    }
                  }
                } catch (DBException ex) {
                  ex.printStackTrace();
                }
              });
        } else if (currentPath.get(i + 2).getFloor() != floor) {
          // If secondNode is last on current floor
          startLabel.setVisible(true);
          endLabel.setVisible(true);
          endLabel.setText("Enter ");
          Circle circle = drawCircle(secondNode, MIDDLE_NODE_COLOR, endLabel);
          circle.setCursor(Cursor.HAND);
          DbNode finalSecondNode = secondNode;
          circle.setOnMouseClicked(
              e -> {
                LinkedList<DbNode> path = currentPath.getPath();
                DbNode next = path.get(path.indexOf(finalSecondNode) + 1);
                while (next.getNodeType().equals("STAI") || next.getNodeType().equals("ELEV")) {
                  next = path.get(path.indexOf(next) + 1);
                }
                try {
                  newMapDisplayController.setCurrentFloor(next.getFloor());
                  newMapDisplayController.setCurrentBuilding(next.getBuilding());
                  setFloor(next.getBuilding(), next.getFloor(), currentPath);
                  if (mapQRController != null) {
                    if (!next.getBuilding().equals("Faulkner")) {
                      mapQRController.setTabFocus(next.getFloor(), "Main");
                    } else {
                      mapQRController.setTabFocus(next.getFloor(), next.getBuilding());
                    }
                  }
                } catch (DBException ex) {
                  ex.printStackTrace();
                }
              });
        }
      }
    }

    pn_path.getChildren().addAll(startLabel, endLabel); // To make sure they render over the path
    setAnimFrames();
  }

  /**
   * Applies animation, color and other style elements to given path line
   *
   * @param line The JavaFX Line object to modify
   */
  public void styleLine(Line line) {

    // Apply basic line stroke effects
    line.setStroke(DEFAULT_PATH_COLOR);
    line.setOpacity(HEAVY_OPACITY);
    line.setStrokeWidth(DEFAULT_PATH_WIDTH);
    line.setStrokeLineCap(StrokeLineCap.ROUND);
    line.getStrokeDashArray().setAll(DASH_PATTERN);

    // Add keyvalues for this line to keyvalue lists
    // Frame 0: no offset
    keyStartVals.add(new KeyValue(line.strokeDashOffsetProperty(), 0, Interpolator.LINEAR));
    // Frame 1: Offset by length of line-gap pattern (so it's continuous)
    keyEndVals.add(
        new KeyValue(line.strokeDashOffsetProperty(), MAX_DASH_OFFSET, Interpolator.LINEAR));
  }

  /**
   * Generate the new keyframes, apply them to the timeline, and play the timeline if there's
   * anything to animate
   */
  private void setAnimFrames() {

    // Set up keyframes
    keyStart = new KeyFrame(Duration.ZERO, "keyStart", null, keyStartVals);
    keyEnd = new KeyFrame(Duration.millis(PATH_ANIM_LENGTH), "keyEnd", null, keyEndVals);

    // Apply new frames
    pathAnimTimeline.getKeyFrames().setAll(keyStart, keyEnd);

    if (!(keyStartVals.isEmpty() || keyEndVals.isEmpty())) {
      // Play anim
      pathAnimTimeline.play();
    } else {
      pathAnimTimeline.stop();
    }
  }

  /**
   * draws either the start or end circle on the map, as well as a text label above it
   *
   * @param node the DbNode to be displayed on the map
   * @param c the color of the circle
   */
  public Circle drawCircle(DbNode node, Color c, Label label) {
    Circle circle = new Circle();
    circle.setRadius(5);
    circle.setCenterX(scaleX(node.getX()));
    circle.setCenterY(scaleY(node.getY()));
    circle.setFill(c);
    pn_path.getChildren().add(circle);
    if (label != null) {
      pn_path.getChildren().add(label);
      label.setText(label.getText() + node.getLongName());
      label.applyCss(); // To make sure prefWidth doesn't return 0, for whatever reason
      label.relocate(
          scaleX(node.getX()) - label.prefWidth(-1) / 2, scaleY(node.getY()) - NODE_LABEL_PADDING);
      pn_path.getChildren().remove(label); // Gets added back after all lines are drawn
    }
    return circle;
  }

  public double scaleX(double x) {
    return x * HORIZONTAL_SCALE;
  }

  public double scaleY(double y) {
    return y * VERTICAL_SCALE;
  }

  public void clearPath() {
    pathAnimTimeline.stop();
    pathAnimTimeline.getKeyFrames().clear();
    keyStart = null;
    keyEnd = null;
    keyStartVals.clear();
    keyEndVals.clear();
    startLabel.setVisible(false);
    endLabel.setVisible(false);
    pn_path.getChildren().clear();
  }

  //   == MAP ZOOM CONTROLS ==

  // When user scrolls mouse over map
  @FXML
  private void mapScrollHandler(ScrollEvent event) throws IOException {
    if (event.getSource() == pn_movableMap) {
      double deltaY = event.getDeltaY();

      // Scaling parameter (alpha) is clamped between 0 (min. scale) and 1 (max. scale)
      double newScale = mapScaleAlpha.get() + deltaY * ZOOM_STEP_SCROLL;
      mapScaleAlpha.set(Math.max(0, Math.min(1, newScale)));
    }
  }

  /**
   * zoom - Scale map pane up or down, clamping value between MIN_MAP_SCALE and MAX_MAP_SCALE
   *
   * @param alphaVal - Double representing the amount of zooming applied (0 = min, 1 = max)
   */
  private void zoom(double alphaVal) {

    // TODO: use zoom to scale line & node size

    // Exponentially interpolate alpha to actual scale value
    // Results in finer zoom up close, coarser zoom in mid range
    SCALE_VALUE = MIN_MAP_SCALE * Math.pow(MAX_MAP_SCALE / MIN_MAP_SCALE, alphaVal);

    // Linearly interpolate (lerp) alpha to actual scale value
    // double lerpedScale = MIN_MAP_SCALE + alphaVal * (MAX_MAP_SCALE - MIN_MAP_SCALE);

    // Apply new scale and correct panning
    pn_movableMap.setScaleX(SCALE_VALUE);
    pn_movableMap.setScaleY(SCALE_VALUE);
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

  // == MAP AUTO-FOCUS ==

  /** Sets up autofocus components (run in init) */
  private void initAutoFocus() {
    autoFocus.setCycleCount(1);
    autoFocus.setOnFinished(event -> onAutoFocusEnd());
    mapScaleAlpha.addListener((observable, oldValue, newValue) -> zoom(newValue.doubleValue()));
  }

  /**
   * Automatically pans and zooms to a given node
   *
   * @param node DbNode to get location data from
   */
  public void autoFocusToNode(DbNode node) {
    autoFocusToPoint(scaleX(node.getX()), scaleY(node.getY()));
  }

  // TODO: This implementation should be cleaner - currently uses raw circle objects
  /** Automatically pans and zooms to the center of a group of nodes */
  public void autoFocusToNodesGroup() {

    double xSum = 0;
    double ySum = 0;
    int nodesCount = pathCircles.size();

    for (DbNode n : pathCircles) {
      xSum += scaleX(n.getX());
      ySum += scaleX(n.getY());
    }
    autoFocusToPoint(xSum / nodesCount, ySum / nodesCount, 0); // Get midpoint of group
  }

  /**
   * Automatically pans and zooms to a given node
   *
   * @param x x location to go to
   * @param y y location to go to
   */
  public void autoFocusToPoint(double x, double y) {

    endFocusVals.add(
        new KeyValue(
            pn_movableMap.translateXProperty(),
            MAX_MAP_SCALE * (MAP_WIDTH / 2 - x),
            Interpolator.LINEAR));
    endFocusVals.add(
        new KeyValue(
            pn_movableMap.translateYProperty(),
            MAX_MAP_SCALE * (MAP_HEIGHT / 2 - y),
            Interpolator.LINEAR));
    endFocusVals.add(new KeyValue(mapScaleAlpha, 1, Interpolator.LINEAR));

    KeyFrame endFrame = new KeyFrame(Duration.millis(500), "endFocus", null, endFocusVals);

    autoFocus.getKeyFrames().setAll(endFrame);
    autoFocus.playFromStart();
  }

  /**
   * Automatically pans and zooms to a given node
   *
   * @param x x location to go to
   * @param y y location to go to
   */
  public void autoFocusToPoint(double x, double y, double alpha) {
    mapScaleAlpha.set(alpha);
    pn_movableMap.setTranslateX(SCALE_VALUE * (MAP_WIDTH / 2 - x));
    pn_movableMap.setTranslateY(SCALE_VALUE * (MAP_HEIGHT / 2 - y));
    /*endFocusVals.add(
        new KeyValue(
            pn_movableMap.translateXProperty(),
            MAX_MAP_SCALE * (MAP_WIDTH / 2 - x),
            Interpolator.LINEAR));
    endFocusVals.add(
        new KeyValue(
            pn_movableMap.translateYProperty(),
            MAX_MAP_SCALE * (MAP_HEIGHT / 2 - y),
            Interpolator.LINEAR));
    endFocusVals.add(new KeyValue(mapScaleAlpha, 1, Interpolator.LINEAR));

    KeyFrame endFrame = new KeyFrame(Duration.millis(500), "endFocus", null, endFocusVals);
    // mapScaleAlpha.set(alpha);
    autoFocus.getKeyFrames().setAll(endFrame);
    autoFocus.playFromStart();
    // mapScaleAlpha.set(alpha);*/
  }

  /** Automatically pans and zoom out to reset view */
  public void resetFocus() {

    //    endFocusVals.add(new KeyValue(pn_movableMap.translateXProperty(), 0,
    // Interpolator.LINEAR));
    //    endFocusVals.add(new KeyValue(pn_movableMap.translateYProperty(), 0,
    // Interpolator.LINEAR));
    //    endFocusVals.add(new KeyValue(mapScaleAlpha, 0, Interpolator.LINEAR));
    //
    //    KeyFrame endFrame = new KeyFrame(Duration.millis(500), "endFocus", null, endFocusVals);
    //
    //    autoFocus.getKeyFrames().setAll(endFrame);
    //    autoFocus.playFromStart();
    mapScaleAlpha.set(0);
    pn_movableMap.setTranslateX(DEFAULT_TRANSLATEX);
    pn_movableMap.setTranslateY(DEFAULT_TRANSLATEY);
  }

  /** Called when zooming is completed */
  private void onAutoFocusEnd() {
    autoFocus.stop();
    autoFocus.getKeyFrames().clear();
    endFocusVals.clear();
    System.out.println(
        "X: " + pn_movableMap.getTranslateX() + "\nY: " + pn_movableMap.getTranslateY());
  }
}
