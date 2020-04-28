package edu.wpi.N.views;

import com.google.common.collect.HashBiMap;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.Algorithm;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.entities.employees.Doctor;
import edu.wpi.N.qrcontrol.QRGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

public class MapDisplayController extends QRGenerator implements Controller {

  private StateSingleton singleton;

  @Override
  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

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

  @FXML Button btn_find;
  @FXML Button btn_reset;
  @FXML Pane pn_path, pn_routeNodes;
  @FXML StackPane pn_movableMap;
  @FXML AnchorPane pn_mapFrame;
  @FXML ImageView img_map;
  @FXML ComboBox<String> cb_languages;
  @FXML Button btn_Login;

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
  @FXML TextArea txtf_laundryNotes;
  @FXML TextArea txtf_translatorNotes;
  @FXML TextField txtf_language;
  @FXML ListView lst_laundryLocation;
  @FXML ListView lst_translatorSearchBox;

  // QR directions
  @FXML ImageView img_qrDirections;
  @FXML Pane pn_directionsBox;

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
    // MapDB.clearNodes();
    // InputStream nodes = Main.class.getResourceAsStream("csv/UPDATEDTeamNnodes.csv");
    // InputStream edges = Main.class.getResourceAsStream("csv/UPDATEDTeamNedges.csv");
    // CSVParser.parseCSV(nodes);
    // CSVParser.parseCSV(edges);
    clampPanning(0, 0);
    selectedNodes = new LinkedList<DbNode>();
    allFloorNodes = MapDB.visNodes(4, "Faulkner");
    masterNodes = HashBiMap.create();
    defaultNode = MapDB.getNode("NHALL00804");
    try {
      if (defaultNode == null) defaultNode = allFloorNodes.getFirst();
    } catch (NoSuchElementException e) {
      Alert emptyMap = new Alert(Alert.AlertType.WARNING);
      emptyMap.setContentText("The map is empty!");
      emptyMap.show();
    }
    populateMap();

    LinkedList<String> languages = ServiceDB.getLanguages();
    ObservableList<String> obvList = FXCollections.observableList(languages);
    cb_languages.setItems(obvList);
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
    mapNode.setCursor(Cursor.HAND); // Cursor points when over nodes
    return mapNode;
  }

  public void onMapNodeClicked(Circle mapNode) {
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

  @FXML
  private void onBtnFindClicked(MouseEvent event) throws DBException {
    if (selectedNodes.size() != 2) {
      return;
    }
    DbNode firstNode = selectedNodes.get(0);
    DbNode secondNode = selectedNodes.get(1);
    if (MapDB.getAdjacent(firstNode.getNodeID()).size() == 0
        || MapDB.getAdjacent(secondNode.getNodeID()).size() == 0) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Invalid input");
      errorAlert.setContentText("No existing paths to this node");
      errorAlert.showAndWait();
      return;
    }

    if (MapDB.getAdjacent(firstNode.getNodeID()).size() == 0
        || MapDB.getAdjacent(secondNode.getNodeID()).size() == 0) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Invalid input");
      errorAlert.setContentText("No existing paths to this node");
      errorAlert.showAndWait();
      return;
    }

    Path path = singleton.savedAlgo.findPath(firstNode, secondNode, false);

    if (path != null) {
      LinkedList<DbNode> pathNodes = path.getPath();
      drawPath(pathNodes);
      GenerateQRDirections(path);
    } else {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Invalid input");
      errorAlert.setContentText("No existing paths to this node");
      errorAlert.showAndWait();
      return;
    }

    //    ArrayList<String> directions = path.getDirections();
    //    for (String s : directions) {
    //      System.out.println(s);
    //    }
    //    System.out.println(" ");

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
    pn_directionsBox.setVisible(false);
    for (Circle mapNode : masterNodes.keySet()) {
      mapNode.setFill(Color.PURPLE);
      mapNode.setDisable(false);
    }
    pn_path.getChildren().removeIf(node -> node instanceof Line);
    selectedNodes.clear();
  }

  public void onReturnClicked() throws IOException {
    mainApp.switchScene("views/home.fxml", singleton);
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
      pn_movableMap.setCursor(Cursor.CLOSED_HAND);
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

  @FXML
  private void mapReleaseHandler(MouseEvent event) throws IOException {
    pn_movableMap.setCursor(Cursor.OPEN_HAND);
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
    if (selectedNodes.size() < 1) selectedNodes.add(defaultNode);
    selectedNodes.add(destinationNode);
    onBtnFindClicked(event);
    selectedNodes.clear();
  }

  @FXML
  private void onNearestBathroomClicked(MouseEvent event) throws Exception {
    DbNode startNode = defaultNode;
    if (selectedNodes.size() > 0) startNode = selectedNodes.getFirst();
    onResetClicked(event);

    // TODO: Use SINGLETON to retrieve Algorithm object and call findQuickAccess
    // TODO: Fix later

    Algorithm algorithmSetting = new Algorithm();

    Path pathToBathroom = algorithmSetting.findQuickAccess(startNode, "REST");
    if (pathToBathroom != null) {
      LinkedList<DbNode> pathNodes = pathToBathroom.getPath();
      drawPath(pathNodes);
      GenerateQRDirections(pathToBathroom);
    }
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
    if (selectedNodes.size() < 1) selectedNodes.add(defaultNode);
    selectedNodes.add(destinationNode);
    // if (selectedNodes.size() < 2) selectedNodes.add(defaultNode);
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
  public void popupWindow(MouseEvent e) throws IOException {
    Stage stage = new Stage();
    Parent root;
    root = FXMLLoader.load(getClass().getResource("adminRequestScreen.fxml"));
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.show();
  }

  @FXML
  public void createNewLaundry() throws DBException {
    int currentSelection = lst_laundryLocation.getSelectionModel().getSelectedIndex();
    String nodeID;
    try {
      nodeID = fuzzySearchNodeListLaundry.get(currentSelection).getNodeID();
    } catch (IndexOutOfBoundsException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a location for your service request!");
      errorAlert.show();
      return;
    }
    String notes = txtf_laundryNotes.getText();
    int laundryRequest = ServiceDB.addLaundReq(notes, nodeID);

    txtf_laundryLocation.clear();
    txtf_laundryNotes.clear();
    lst_laundryLocation.getItems().clear();

    Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confAlert.setContentText("Request Recieved");
    confAlert.show();
  }

  private void GenerateQRDirections(Path path) {
    try {
      ArrayList<String> directions = path.getDirections();
      img_qrDirections.setImage(generateImage(directions, false));
      pn_directionsBox.setVisible(true);
    } catch (DBException e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void createNewTranslator() throws DBException {
    int currentSelection = lst_translatorSearchBox.getSelectionModel().getSelectedIndex();

    String nodeID;
    try {
      nodeID = fuzzySearchNodeListTranslator.get(currentSelection).getNodeID();
    } catch (IndexOutOfBoundsException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a location for your service request!");
      errorAlert.show();
      return;
    }
    String notes = txtf_translatorNotes.getText();
    String language = cb_languages.getSelectionModel().getSelectedItem();
    if (language == null) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a language for your translation request!");
      errorAlert.show();
      return;
    }
    int transReq = ServiceDB.addTransReq(notes, nodeID, language);

    txtf_translatorLocation.clear();
    txtf_translatorNotes.clear();
    cb_languages.cancelEdit();
    lst_translatorSearchBox.getItems().clear();

    Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confAlert.setContentText("Request Received");
    confAlert.show();
  }
}
