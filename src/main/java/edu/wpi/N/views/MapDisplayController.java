package edu.wpi.N.views;

import com.google.common.collect.HashBiMap;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.algorithms.Pathfinder;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Doctor;
import edu.wpi.N.entities.Path;
import java.io.IOException;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MapDisplayController implements Controller {
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
  int currentFloor = 4;

  Boolean loggedin = false;

  @FXML Button btn_find;
  @FXML Button btn_reset;
  @FXML Pane pn_path, pn_routeNodes;
  @FXML StackPane pn_movableMap;
  @FXML AnchorPane pn_mapFrame;
  @FXML ImageView img_map;

  HashBiMap<Circle, DbNode> masterNodes; // stores the map nodes and their respective database nodes

  // Sidebar search by location initializations
  @FXML TextField txtf_searchlocation;
  @FXML ListView lst_locationsorted;
  @FXML Button btn_findlocationpath;

  // Sidebar search by doctor initializations
  @FXML TextField txtf_doctorname;
  @FXML ListView lst_doctornames;
  @FXML Button btn_searchdoc;
  @FXML ListView lst_doctorlocations;
  @FXML Button btn_findpathdoc;

  // Map Controls
  @FXML Button btn_zoomIn, btn_zoomOut;
  private double mapScaleAlpha;
  private double clickStartX, clickStartY;
  private final double MIN_MAP_SCALE = 1;
  private final double MAX_MAP_SCALE = 3;
  private final double ZOOM_STEP_SCROLL = 0.01;
  private final double ZOOM_STEP_BUTTON = 0.1;

  @FXML TextField txtf_translatorLocation;
  @FXML TextField txtf_laundryLocation;
  @FXML ListView lst_laundryLocation;
  @FXML ListView lst_translatorSearchBox;

  LinkedList<DbNode> allFloorNodes; // stores all the nodes on the floor
  LinkedList<DbNode> selectedNodes; // stores all the selected nodes on the map
  LinkedList<String> longNamesList = new LinkedList<>(); // Stores Floor Node names

  private ObservableList<String> fuzzySearchTextList =
      FXCollections.observableArrayList(); // List that fills TextViews
  private LinkedList<DbNode> fuzzySearchNodeList =
      new LinkedList<>(); // List to store output of fuzzy search functions
  private ObservableList<String> fuzzySearchDoctorList =
      FXCollections.observableArrayList(); // List that fills TextViews

  private LinkedList<DbNode> fuzzySearchNodeListLaundry = new LinkedList<>();
  private LinkedList<DbNode> fuzzySearchNodeListTranslator = new LinkedList<>();
  private LinkedList<DbNode> getFuzzySearchNodeList;
  private LinkedList<Doctor> searchedDoc = new LinkedList<>();
  private LinkedList<DbNode> doctorNodes = new LinkedList<>();

  private DbNode defaultNode = new DbNode();

  public MapDisplayController() throws DBException {}

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException {
    selectedNodes = new LinkedList<DbNode>();
    allFloorNodes = DbController.floorNodes(4, "Faulkner");
    masterNodes = HashBiMap.create();
    defaultNode = DbController.getNode("NHALL00804");
    if (defaultNode == null) defaultNode = allFloorNodes.getFirst();
    populateMap();
  }

  public void populateMap() {
    for (DbNode node : allFloorNodes) {
      Circle mapNode = makeMapNode(node);
      pn_routeNodes.getChildren().add(mapNode);
      longNamesList.add(node.getLongName());
      masterNodes.put(mapNode, node);
    }
  }

  public Circle makeMapNode(DbNode node) {
    Circle mapNode = new Circle();
    mapNode.setRadius(6);
    mapNode.setLayoutX((node.getX() * HORIZONTAL_SCALE + HORIZONTAL_OFFSET));
    mapNode.setLayoutY((node.getY() * VERTICAL_SCALE + VERTICAL_OFFSET));
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

  @FXML
  private void onBtnFindClicked(MouseEvent event) {
    if (selectedNodes.size() != 2) {
      return;
    }
    DbNode firstNode = selectedNodes.get(0);
    DbNode secondNode = selectedNodes.get(1);

    Path path = Pathfinder.findPath(firstNode.getNodeID(), secondNode.getNodeID());
    LinkedList<DbNode> pathNodes = path.getPath();
    drawPath(pathNodes);

    for (Circle mapNode : masterNodes.keySet()) {
      mapNode.setDisable(true);
    }
  }

  private void drawPath(LinkedList<DbNode> pathNodes) {
    DbNode firstNode;
    DbNode secondNode;

    for (int i = 0; i < pathNodes.size() - 1; i++) {
      firstNode = pathNodes.get(i);
      secondNode = pathNodes.get(i + 1);
      Line line =
          new Line(
              (firstNode.getX() * HORIZONTAL_SCALE) + HORIZONTAL_OFFSET,
              (firstNode.getY() * VERTICAL_SCALE) + VERTICAL_OFFSET,
              (secondNode.getX() * HORIZONTAL_SCALE) + HORIZONTAL_OFFSET,
              (secondNode.getY() * VERTICAL_SCALE) + VERTICAL_OFFSET);
      line.setStrokeWidth(5);
      pn_path.getChildren().add(line);
    }
  }

  @FXML
  private void onResetClicked(MouseEvent event) throws Exception {
    for (Circle mapNode : masterNodes.keySet()) {
      mapNode.setFill(Color.PURPLE);
      mapNode.setDisable(false);
    }
    pn_path.getChildren().removeIf(node -> node instanceof Line);
    selectedNodes.clear();
  }

  public void onReturnClicked() throws IOException {
    mainApp.switchScene("views/home.fxml");
  }
  // Zoom controls

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

  // Scale map pane, clamping between MIN_MAP_SCALE and MAX_MAP_SCALE
  private void zoom(double percent) {

    mapScaleAlpha = Math.max(0, Math.min(1, mapScaleAlpha + percent));

    // Maps 0-1 value (alpha) to min-max value
    double lerpedScale = MIN_MAP_SCALE + mapScaleAlpha * (MAX_MAP_SCALE - MIN_MAP_SCALE);
    pn_movableMap.setScaleX(lerpedScale);
    pn_movableMap.setScaleY(lerpedScale);
    clampPanning(0, 0);
  }

  // Panning controls

  @FXML
  private void mapClickHandler(MouseEvent event) throws IOException {
    if (event.getSource() == pn_movableMap) {
      clickStartX = event.getSceneX();
      clickStartY = event.getSceneY();
    }
  }

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
  // mike
  // Upon changing text in the search by location UI component this method
  // is triggered
  @FXML
  private void searchByLocationTextFill(KeyEvent inputMethodEvent) throws DBException {
    String currentText = txtf_searchlocation.getText();
    fuzzySearchNodeList = FuzzySearchAlgorithm.suggestLocations(currentText);
    LinkedList<String> fuzzySearchStringList = new LinkedList<>();
    if (fuzzySearchNodeList != null) {

      for (DbNode node : fuzzySearchNodeList) {
        fuzzySearchStringList.add(node.getLongName());
      }

      fuzzySearchTextList = FXCollections.observableList(fuzzySearchStringList);
    } else fuzzySearchTextList = FXCollections.observableList(longNamesList);
    lst_locationsorted.setItems(fuzzySearchTextList);
  }

  // Upon clicking find path to location button call this method
  @FXML
  private void onLocationPathFindClicked(MouseEvent event) throws Exception {
    pn_path.getChildren().removeIf(node -> node instanceof Line);
    int currentSelection = lst_locationsorted.getSelectionModel().getSelectedIndex();
    DbNode destinationNode = fuzzySearchNodeList.get(currentSelection);
    selectedNodes.add(destinationNode);
    if (selectedNodes.size() < 2) selectedNodes.add(defaultNode);
    onBtnFindClicked(event);
    selectedNodes.clear();
  }

  @FXML
  private void onNearestBathroomClicked(MouseEvent event) throws Exception {
    onResetClicked(event);
    Path pathToBathroom = Pathfinder.findQuickAccess(defaultNode, "REST");
    LinkedList<DbNode> pathNodes = pathToBathroom.getPath();
    drawPath(pathNodes);
  }

  @FXML
  private void searchByDoctorTextFill(KeyEvent keyEvent) throws DBException {
    String currentText = txtf_doctorname.getText();
    if (currentText.length() > 1) {
      searchedDoc = FuzzySearchAlgorithm.suggestDoctors(currentText);
      LinkedList<String> fuzzySearchStringList = new LinkedList<>();
      for (Doctor doctors : searchedDoc) {
        fuzzySearchStringList.add(doctors.getName());
      }
      fuzzySearchDoctorList = FXCollections.observableList(fuzzySearchStringList);
      lst_doctornames.setItems(fuzzySearchDoctorList);
    }
  }

  @FXML
  private void onFindDoctorClicked(MouseEvent event) throws Exception {
    int currentSelection = lst_doctornames.getSelectionModel().getSelectedIndex();
    System.out.println(currentSelection);
    Doctor selectedDoc = searchedDoc.get(currentSelection);
    System.out.println(selectedDoc);
    doctorNodes = selectedDoc.getLoc();
    LinkedList<String> docNames = new LinkedList<>();
    for (DbNode nodes : doctorNodes) {
      docNames.add(nodes.getLongName());
    }
    ObservableList<String> doctorsLocations = FXCollections.observableList(docNames);
    lst_doctorlocations.setItems(doctorsLocations);
  }

  // Upon clicking find path to location button call this method
  @FXML
  private void onDoctorPathFindClicked(MouseEvent event) throws Exception {
    pn_path.getChildren().removeIf(node -> node instanceof Line);
    int currentSelection = lst_doctorlocations.getSelectionModel().getSelectedIndex();
    DbNode destinationNode = doctorNodes.get(currentSelection);
    selectedNodes.add(destinationNode);
    if (selectedNodes.size() < 2) selectedNodes.add(defaultNode);
    onBtnFindClicked(event);
    selectedNodes.clear();
  }

  public void fuzzySearchLaundryRequest(KeyEvent keyInput) throws DBException {
    String currentText = txtf_laundryLocation.getText();
    fuzzySearchNodeListLaundry = FuzzySearchAlgorithm.suggestLocations(currentText);
    LinkedList<String> fuzzySearchStringList = new LinkedList<>();
    if (fuzzySearchNodeListLaundry != null) {

      for (DbNode node : fuzzySearchNodeListLaundry) {
        fuzzySearchStringList.add(node.getLongName());
      }

      fuzzySearchTextList = FXCollections.observableList(fuzzySearchStringList);
    } else fuzzySearchTextList = FXCollections.observableList(longNamesList);
    lst_laundryLocation.setItems(fuzzySearchTextList);
  }

  @FXML
  public void fuzzySearchTranslatorRequest(KeyEvent keyInput) throws DBException {
    String currentText = txtf_translatorLocation.getText();
    fuzzySearchNodeListTranslator = FuzzySearchAlgorithm.suggestLocations(currentText);
    LinkedList<String> fuzzySearchStringList = new LinkedList<>();
    if (fuzzySearchNodeListTranslator != null) {

      for (DbNode node : fuzzySearchNodeListTranslator) {
        fuzzySearchStringList.add(node.getLongName());
      }

      fuzzySearchTextList = FXCollections.observableList(fuzzySearchStringList);
    } else fuzzySearchTextList = FXCollections.observableList(longNamesList);
    lst_translatorSearchBox.setItems(fuzzySearchTextList);
  }

  @FXML
  public void popupWindow() throws IOException {
    if (loggedin == false) {
      loggedin = true;
      Stage stage = new Stage();
      Parent root;
      root = FXMLLoader.load(getClass().getResource("loginWindow.fxml"));
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.showAndWait();
    } else if (loggedin == true) {
      Stage stage = new Stage();
      Parent root;
      root = FXMLLoader.load(getClass().getResource("adminRequestScreen.fxml"));
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.showAndWait();
    }
  }

  /*
  - Add to database function ->
      - Take in a service request object
      - Put it into the table
      -
   */

  @FXML
  public void loginWindow(MouseEvent e) throws IOException {}
}
