package edu.wpi.N.views;

import com.google.common.collect.HashBiMap;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.DbNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.io.IOException;
import java.util.LinkedList;

public class MapBaseController {

    // Screen constants
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

    // Map UI structure elements
    @FXML
    Pane pn_path, pn_routeNodes;
    @FXML
    StackPane pn_movableMap;
    @FXML
    AnchorPane pn_mapFrame;
    @FXML
    ImageView img_map;

    // List of selected nodes
    LinkedList<DbNode> selectedNodes; // stores all the selected nodes on the map

    // Stores references to all nodes
    private HashBiMap<Circle, DbNode> masterNodes; // stores the map nodes and their respective database nodes

    // Zoom/pan UI
    @FXML
    Button btn_zoomIn, btn_zoomOut;

    // Zoom/pan vars
    private double mapScaleAlpha; // Zoom ratio (0 = min, 1 = max)
    private double clickStartX, clickStartY; // Begin location of drag
    private boolean isStatic = false;  // Zoom/pan controls disabled if true

    // Zoom/pan constants
    private final double MIN_MAP_SCALE = 1;
    private final double MAX_MAP_SCALE = 3;
    private final double ZOOM_STEP_SCROLL = 0.01;
    private final double ZOOM_STEP_BUTTON = 0.1;


    public MapBaseController() throws DBException {}

    public void initialize() throws DBException {

        masterNodes = HashBiMap.create();
        selectedNodes = new LinkedList<DbNode>();
        populateMap();

        LinkedList<String> languages = ServiceDB.getLanguages();
        ObservableList<String> obvList = FXCollections.observableList(languages);

    }

    public void populateMap() {
        for (DbNode node : App.mapData.getFloorNodes(true)) {
            Circle mapNode = makeMapNode(node);
            pn_routeNodes.getChildren().add(mapNode);
            masterNodes.put(mapNode, node);
        }
    }

    private Circle makeMapNode(DbNode node) {
        Circle mapNode = new Circle();
        mapNode.setRadius(6);
        mapNode.setLayoutX((node.getX() * HORIZONTAL_SCALE + HORIZONTAL_OFFSET));
        mapNode.setLayoutY((node.getY() * VERTICAL_SCALE + VERTICAL_OFFSET));
        mapNode.setFill(Color.PURPLE);
        mapNode.setOpacity(0.7);
        mapNode.setOnMouseClicked(mouseEvent -> this.onMapNodeClicked(mapNode));
        mapNode.setCursor(Cursor.HAND); // Cursor points when over nodes
        return mapNode;
    }

    private void onMapNodeClicked(Circle mapNode) {
        if (selectedNodes.size() > 1) {
            masterNodes.inverse().get(selectedNodes.poll()).setFill(Color.PURPLE);
        }
        if (mapNode.getFill() == Color.PURPLE) {
            mapNode.setFill(Color.RED);
            selectedNodes.add(masterNodes.get(mapNode));
        } else {
            mapNode.setFill(Color.PURPLE);
            selectedNodes.remove(masterNodes.get(mapNode));
        }
    }

    // Draw lines between each pair of nodes in given path
    public void drawPath(LinkedList<DbNode> pathNodes) {
        DbNode firstNode;
        DbNode secondNode;

        for (int i = 0; i < pathNodes.size() - 1; i++) {
            firstNode = pathNodes.get(i);
            secondNode = pathNodes.get(i + 1);

            float startX = (firstNode.getX() * HORIZONTAL_SCALE) + HORIZONTAL_OFFSET;
            float startY = (firstNode.getY() * VERTICAL_SCALE) + VERTICAL_OFFSET;
            float endX = (secondNode.getX() * HORIZONTAL_SCALE) + HORIZONTAL_OFFSET;
            float endY = (secondNode.getY() * VERTICAL_SCALE) + VERTICAL_OFFSET;

            Line line = new Line(startX, startY, endX, endY);
            line.setStrokeWidth(5);

            pn_path.getChildren().add(line);
        }
    }

    // Deselect nodes and remove lines
    public void resetElements () {
        for (Circle mapNode : masterNodes.keySet()) {
            mapNode.setFill(Color.PURPLE);
            mapNode.setDisable(false);
        }
        pn_path.getChildren().removeIf(node -> node instanceof Line);
        selectedNodes.clear();
    }




    // == MAP ZOOM CONTROLS ==

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
