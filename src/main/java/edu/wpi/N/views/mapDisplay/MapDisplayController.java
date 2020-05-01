package edu.wpi.N.views.mapDisplay;

import com.jfoenix.controls.*;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.entities.employees.Doctor;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MapDisplayController implements Controller, Initializable {

  private StateSingleton singleton;

  private App mainApp = null;
  final float IMAGE_WIDTH = 2475;
  final float IMAGE_HEIGHT = 1485;
  final float MAP_WIDTH = 1678;
  final float MAP_HEIGHT = 1010;
  final float HORIZONTAL_SCALE = (MAP_WIDTH) / IMAGE_WIDTH;
  final float VERTICAL_SCALE = (MAP_HEIGHT) / IMAGE_HEIGHT;
  @FXML ImageView img_map;
  // @FXML Pane pn_display;
  @FXML Pane pn_changeFloor;
  @FXML JFXTextField txt_firstLocation;
  @FXML JFXTextField txt_secondLocation;
  @FXML JFXListView<DbNode> lst_firstLocation;
  @FXML JFXListView<DbNode> lst_secondLocation;
  // @FXML JFXButton btn_searchdoc;
  @FXML JFXTextField txt_doctorname;
  @FXML ListView<Doctor> lst_doctornames;
  // @FXML Button btn_searchdoc;
  @FXML ListView<DbNode> lst_doctorlocations;
  @FXML Button btn_findpathdoc;
  @FXML JFXButton btn_findPath;
  @FXML JFXButton btn_home;
  @FXML private JFXButton btn_floors, btn_floor1, btn_floor2, btn_floor3, btn_floor4, btn_floor5;
  @FXML TitledPane pn_locationSearch;
  @FXML Accordion acc_search;
  @FXML Text txt_description;
  @FXML JFXCheckBox handicapp1;
  @FXML JFXCheckBox handicapp2;

  @FXML MapBaseController mapBaseController; // Reference to the embedded map

  /*
  private final int DEFAULT_FLOOR = 1;
  private final String DEFAULT_BUILDING = "FAULKNER";
  private int currentFloor;
  private String currentBuilding;
   */

  // sphagetting code I guesss
  private ArrayList<String> directions;

  HashMap<String, DbNode> stringNodeConversion = new HashMap<>();
  private LinkedList<Doctor> searchedDoc = new LinkedList<>();
  private LinkedList<DbNode> doctorNodes = new LinkedList<>();
  // LinkedList<DbNode> pathNodes;
  //  String[] imgPaths =
  //      new String[] {
  //        "/edu/wpi/N/images/Floor1TeamN.png",
  //        "/edu/wpi/N/images/Floor2TeamN.png",
  //        "/edu/wpi/N/images/Floor3TeamN.png",
  //        "/edu/wpi/N/images/Floor4TeamN.png",
  //        "/edu/wpi/N/images/Floor5TeamN.png"
  //      };

  // the list of floor buttons which allow users to switch between floors
  JFXNodesList floorButtonList = new JFXNodesList();

  // Inject singleton
  public MapDisplayController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  @Override
  public void initialize(URL location, ResourceBundle resourceBundle) {
    try {
      createFloorButtons();
      // this.currentFloor = DEFAULT_FLOOR;
      // this.currentBuilding = DEFAULT_BUILDING;
      // this.mode = Mode.NO_STATE;
      // this.allFloorNodes = MapDB.allNodes();

      // initializeConversions();
      defaultKioskNode();
      acc_search.setExpandedPane(pn_locationSearch);

    } catch (Exception ex) {
      ex.printStackTrace();
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText(
          "Map view couldn't be opened. Make sure to upload Nodes and Edges as CSVs");
      errorAlert.showAndWait();
    }
  }

  /** creates the buttons which enables the user to view different floors */
  public void createFloorButtons() {
    LinkedList floorButtons = new LinkedList();
    initFloorButtons(floorButtons);
    styleFloorButtons(floorButtons);
    displayFloorButtonList(floorButtons);
  }

  /**
   * populates the list of buttons which enable the user to view different floors
   *
   * @param floorButtons the empty list of buttons which enable the user to view different floors
   * @return the populated list of buttons which enable the user to view different floors
   */
  public LinkedList<JFXButton> initFloorButtons(LinkedList<JFXButton> floorButtons) {
    btn_floors = new JFXButton("Floors");
    floorButtons.add(btn_floors);
    for (int i = 1; i <= 5; i++) {
      int floor = i;
      JFXButton btn = new JFXButton();
      btn.setText(String.valueOf(i));
      btn.setOnMouseClicked(e -> drawPathOnFloor(floor));
      floorButtons.add(btn);
    }
    return floorButtons;
  }

  /**
   * styles the buttons which enable the user to view different floors
   *
   * @param floorButtons the list of buttons which enable the user to view different floors
   */
  public void styleFloorButtons(LinkedList<JFXButton> floorButtons) {
    for (JFXButton btn : floorButtons) {
      btn.setButtonType(JFXButton.ButtonType.RAISED);
      btn.getStylesheets()
          .addAll(getClass().getResource("/edu/wpi/N/css/MapDisplayFloors.css").toExternalForm());
      btn.getStyleClass().addAll("animated-option-button");
    }
  }

  /**
   * displays the buttons which enable the user to view different floors
   *
   * @param floorButtons the populated and styled list of buttons which enable the user to view
   *     different floors
   */
  public void displayFloorButtonList(LinkedList<JFXButton> floorButtons) {
    for (JFXButton btn : floorButtons) {
      floorButtonList.addAnimatedNode(btn);
    }
    floorButtonList.setSpacing(20);
    pn_changeFloor.getChildren().add(floorButtonList);
  }

  /**
   * detects when user enters a first location and applies fuzzy search
   *
   * @param e the key event which triggers the search
   * @throws DBException
   */
  public void onSearchFirstLocation(KeyEvent e) throws DBException {
    fuzzyLocationSearch(txt_firstLocation, lst_firstLocation);
  }

  /**
   * detects when user enters a second path location and applies fuzzy search
   *
   * @param e the key event which triggers the search
   * @throws DBException
   */
  public void onSearchSecondLocation(KeyEvent e) throws DBException {
    fuzzyLocationSearch(txt_secondLocation, lst_secondLocation);
  }

  /**
   * detects when user enters a doctor and applies fuzzy search
   *
   * @param e the key event which triggers the search
   * @throws DBException
   */
  public void onSearchDoctor(KeyEvent e) throws DBException {
    fuzzyDoctorSearch(txt_doctorname, lst_doctornames);
  }

  /**
   * applies fuzzy search to the user input for locations
   *
   * @param txt the textfield with the user input
   * @param lst the fuzzy search results
   * @throws DBException
   */
  public void fuzzyLocationSearch(JFXTextField txt, ListView lst) throws DBException {
    ObservableList<DbNode> fuzzyList;
    String str = txt.getText();
    fuzzyList = FXCollections.observableList(FuzzySearchAlgorithm.suggestLocations(str));
    lst.setItems(fuzzyList);
  }

  /**
   * applies fuzzy search to the user input for doctors
   *
   * @param txt the textfield with the user input
   * @param lst the fuzzy search results
   * @throws DBException
   */
  public void fuzzyDoctorSearch(JFXTextField txt, ListView lst) throws DBException {
    ObservableList<Doctor> fuzzyList;
    String str = txt.getText();
    fuzzyList = FXCollections.observableList(FuzzySearchAlgorithm.suggestDoctors(str));
    lst.setItems(fuzzyList);
  }

  /**
   * finds the doctor's department and displays the result in a list view
   *
   * @param e the key event which triggers the search
   * @throws Exception
   */
  @FXML
  private void onFindDoctorClicked(MouseEvent e) throws Exception {
    Doctor doc = lst_doctornames.getSelectionModel().getSelectedItem();
    ObservableList<DbNode> docNodes = FXCollections.observableList(doc.getLoc());
    lst_doctorlocations.setItems(docNodes);
  }

  /**
   * triggers path finding
   * @param event the mouse event which triggers path finding
   * @throws Exception
   */
  public void onBtnPathfindClicked(MouseEvent event) throws Exception {
    // TODO clear the floor
    // TODO only enable buttons on the cer
//    enableAllFloorButtons();
    DbNode firstSelection = lst_firstLocation.getSelectionModel().getSelectedItem();
    DbNode secondSelection = lst_secondLocation.getSelectionModel().getSelectedItem();
    // jumpToFloor(imgPaths[stringNodeConversion.get(firstSelection).getFloor() - 1]);
    mapBaseController.setMode(MapBaseController.Mode.NO_STATE);
    mapBaseController.changeFloor(stringNodeConversion.get(firstSelection).getFloor());
    // currentFloor = stringNodeConversion.get(firstSelection).getFloor();
    try {
      findPath(stringNodeConversion.get(firstSelection), stringNodeConversion.get(secondSelection));
    } catch (NullPointerException e) {
      displayErrorMessage("Path does not exist");
      return;
    }
  }

  @FXML
  private void onDoctorPathFindClicked(MouseEvent event) throws Exception {
    // this.mode = Mode.PATH_STATE;
    mapBaseController.setMode(MapBaseController.Mode.PATH_STATE);
    // pn_display.getChildren().removeIf(node -> node instanceof Line);
    mapBaseController.clearPath();
    enableAllFloorButtons();
    String firstSelection = (String) lst_firstLocation.getSelectionModel().getSelectedItem();
    String secondSelection = (String) lst_doctorlocations.getSelectionModel().getSelectedItem();
    // jumpToFloor(imgPaths[stringNodeConversion.get(firstSelection).getFloor() - 1]);
    mapBaseController.setMode(MapBaseController.Mode.NO_STATE);
    mapBaseController.changeFloor(stringNodeConversion.get(firstSelection).getFloor());
    // currentFloor = stringNodeConversion.get(firstSelection).getFloor();
    try {
      findPath(stringNodeConversion.get(firstSelection), stringNodeConversion.get(secondSelection));
    } catch (NullPointerException e) {
      displayErrorMessage("Path does not exist");
      return;
    }
  }

  private void enableAllFloorButtons() {
    for (int i = 1; i < floorButtonList.getChildren().size(); i++) {
      JFXButton btn = (JFXButton) floorButtonList.getChildren().get(i);
      btn.setDisable(false);
    }
  }

  private void findPath(DbNode node1, DbNode node2) throws DBException {
    boolean handicap = false;
    if (handicapp1.isSelected() || handicapp2.isSelected()) {
      handicap = true;
    }
    if (node1.getFloor() <= node2.getFloor()) {
      Path path;
      try {
        path = singleton.savedAlgo.findPath(node1, node2, handicap);
        ArrayList<String> directions = path.getDirections();
        for (String s : directions) {
          System.out.println(s);
        }
        System.out.println("Start angle " + path.getStartAngle(MapDB.getKioskAngle()));
      } catch (NullPointerException e) {
        displayErrorMessage("The path does not exist");
        return;
      }
      mapBaseController.setPathNodes(path.getPath());
    } else {
      Path path = singleton.savedAlgo.findPath(node2, node1, handicap);
      mapBaseController.setPathNodes(path.getPath());
      ArrayList<String> directions = path.getDirections();
      for (String s : directions) {
        System.out.println(s);
      }
      System.out.println("Start angle " + path.getStartAngle(MapDB.getKioskAngle()));
    }
    disableNonPathFloors(mapBaseController.getPathNodes());
    mapBaseController.drawPath(mapBaseController.getPathNodes());
    // set textual decriptions
    setTextDecription(new Path(mapBaseController.getPathNodes()));
  }

  private void disableNonPathFloors(LinkedList<DbNode> pathNodes) {
    LinkedList<Integer> activeFloors = new LinkedList();
    for (DbNode node : pathNodes) {
      if (!(node.getNodeType().equals("ELEV") || node.getNodeType().equals("STAI"))) {
        if (!activeFloors.contains(node.getFloor())) {
          activeFloors.add(node.getFloor());
        }
      }
    }
    for (int i = 1; i < floorButtonList.getChildren().size(); i++) {
      JFXButton btn = (JFXButton) floorButtonList.getChildren().get(i);
      if (!activeFloors.contains(Integer.parseInt(btn.getText()))) {
        btn.setDisable(true);
      }
    }
  }

  /*
  private void drawPath(LinkedList<DbNode> pathNodes) {
    DbNode firstNode;
    DbNode secondNode;
    for (int i = 0; i < pathNodes.size() - 1; i++) {
      firstNode = pathNodes.get(i);
      secondNode = pathNodes.get(i + 1);
      if (firstNode.getFloor() == currentFloor && secondNode.getFloor() == currentFloor) {
        Line line =
            new Line(
                scaleX(firstNode.getX()),
                scaleY(firstNode.getY()),
                scaleX(secondNode.getX()),
                scaleY(secondNode.getY()));
        styleLine(line);
        pn_display.getChildren().add(line);
      }
    }
  }
   */

  public void onBtnResetPathClicked() throws DBException {
    // mode = Mode.NO_STATE;
    mapBaseController.setMode(MapBaseController.Mode.NO_STATE);
    defaultKioskNode();
    enableAllFloorButtons();
    // pn_display.getChildren().removeIf(node -> node instanceof Line);
    txt_firstLocation.clear();
    txt_secondLocation.clear();
    lst_firstLocation.getItems().clear();
    lst_secondLocation.getItems().clear();
  }

  /*
  private double scaleX(double x) {
    return x * HORIZONTAL_SCALE;
  }

  private double scaleY(double y) {
    return y * VERTICAL_SCALE;
  }
   */

  /*
  private void setFloorImg(String path) throws DBException {
    // pn_display.getChildren().removeIf(node -> node instanceof Line);
    mapBaseController.clearPath();
    if (mode == Mode.PATH_STATE) {
      mapBaseController.drawPath(pathNodes);
    }
    if (mode == Mode.NO_STATE) {
      defaultKioskNode();
    }
    Image img = new Image(getClass().getResourceAsStream(path));
    img_map.setImage(img);
  }
   */

  private void drawPathOnFloor(int floor) {
    mapBaseController.setMode(MapBaseController.Mode.PATH_STATE);
    mapBaseController.changeFloor(floor);
  }

  /**
   * Finds and draws path to the nearest bathroom
   *
   * @param e
   */
  @FXML
  private void findPathToBathroom(MouseEvent e) throws DBException {
    try {
      // this.mode = Mode.PATH_STATE;
      mapBaseController.setMode(MapBaseController.Mode.PATH_STATE);
      // pn_display.getChildren().removeIf(node -> node instanceof Line);
      enableAllFloorButtons();
      String startSelection = (String) lst_firstLocation.getSelectionModel().getSelectedItem();
      DbNode startNode = stringNodeConversion.get(startSelection);
      Path pathToBathroom = singleton.savedAlgo.findQuickAccess(startNode, "REST");
      mapBaseController.drawPath(pathToBathroom.getPath());
      // set textual decriptions
      setTextDecription(pathToBathroom);
    } catch (Exception ex) {
      ex.printStackTrace();
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText("Path to bathroom wasn't found");
      errorAlert.showAndWait();
    }
  }

  /**
   * Finds and draws path to the cafeteria
   *
   * @param e
   */
  @FXML
  private void findPathToCafetaria(MouseEvent e) {
    // this.mode = Mode.PATH_STATE;
    mapBaseController.setMode(MapBaseController.Mode.PATH_STATE);
    // pn_display.getChildren().removeIf(node -> node instanceof Line);
    enableAllFloorButtons();
    String firstSelection = (String) lst_firstLocation.getSelectionModel().getSelectedItem();
    // jumpToFloor(imgPaths[stringNodeConversion.get(firstSelection).getFloor() - 1]);
    mapBaseController.setMode(MapBaseController.Mode.NO_STATE);
    mapBaseController.changeFloor(stringNodeConversion.get(firstSelection).getFloor());
    // currentFloor = stringNodeConversion.get(firstSelection).getFloor();
    try {
      findPath(stringNodeConversion.get(firstSelection), MapDB.getNode("MRETL00203"));
    } catch (NullPointerException | DBException ex) {
      displayErrorMessage("Path does not exist");
      return;
    }
  }

  /**
   * Finds and draws path to the Starbucks
   *
   * @param e
   */
  @FXML
  private void findPathToStarBucks(MouseEvent e) {
    // this.mode = Mode.PATH_STATE;
    mapBaseController.setMode(MapBaseController.Mode.PATH_STATE);
    // pn_display.getChildren().removeIf(node -> node instanceof Line);
    enableAllFloorButtons();
    String firstSelection = (String) lst_firstLocation.getSelectionModel().getSelectedItem();
    // jumpToFloor(imgPaths[stringNodeConversion.get(firstSelection).getFloor() - 1]);
    mapBaseController.setMode(MapBaseController.Mode.NO_STATE);
    mapBaseController.changeFloor(stringNodeConversion.get(firstSelection).getFloor());
    // currentFloor = stringNodeConversion.get(firstSelection).getFloor();
    try {
      findPath(stringNodeConversion.get(firstSelection), MapDB.getNode("NRETL00201"));
    } catch (NullPointerException | DBException ex) {
      displayErrorMessage("Path does not exist");
      return;
    }
  }

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void onBtnHomeClicked() throws IOException {
    mainApp.switchScene("views/newHomePage.fxml", singleton);
  }

  public void displayErrorMessage(String str) {
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    errorAlert.setHeaderText("Invalid input");
    errorAlert.setContentText(str);
    errorAlert.showAndWait();
  }

  /**
   * Function generates and sets textual description label to Textual Descriptions
   *
   * @param path
   */
  private void setTextDecription(Path path) {
    try {
      // Convert the array of textual descriptions to text
      String directionsAsText = "";
      directions = path.getDirections();
      for (String s : directions) {
        directionsAsText += s;
        directionsAsText += "\n";
      }

      // Check to make sure that directionAsText isn't empty
      if (!directionsAsText.equals("")) {
        txt_description.setText(directionsAsText);
      }

    } catch (Exception ex) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText("Textual descriptions could not be generated");
      errorAlert.showAndWait();
    }
  }

  /** Function displays a pop-up window with user's directions */
  @FXML
  private void displayQRCode() throws IOException {
    try {
      Stage stage = new Stage();
      Parent root;
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("qrPopUp.fxml"));
      root = loader.load();
      Scene scene = new Scene(root);
      stage.setScene(scene);

      QrPopUpController controller = (QrPopUpController) loader.getController();
      controller.displayQrCode(directions);

      stage.initModality(Modality.APPLICATION_MODAL);
      stage.show();
    } catch (Exception ex) {
      ex.printStackTrace();
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText("QR code with directions could not be generated");
      errorAlert.showAndWait();
    }
  }

  // Copied to mapBaseController
  /*
  private void defaultKioskNode() throws DBException {
    LinkedList<String> kiosks = new LinkedList<>();
    if (currentFloor == 1) {
      txt_firstLocation.setText(MapDB.getNode("NSERV00301").getLongName());
      kiosks.add(MapDB.getNode("NSERV00301").getLongName());
      ObservableList<String> textList = FXCollections.observableList(kiosks);
      lst_firstLocation.setItems(textList);
      lst_firstLocation.getSelectionModel().select(0);

    } else if (currentFloor == 3) {
      txt_firstLocation.setText(MapDB.getNode("NSERV00103").getLongName());
      kiosks.add(MapDB.getNode("NSERV00103").getLongName());
      ObservableList<String> textList = FXCollections.observableList(kiosks);
      lst_firstLocation.setItems(textList);
      lst_firstLocation.getSelectionModel().select(0);
    } else {
      txt_firstLocation.clear();
      lst_firstLocation.getItems().clear();
    }
  }
   */

  private void defaultKioskNode() throws DBException {
    LinkedList<String> kiosks =
        mapBaseController.defaultKioskNode(); // Returns names of loaded kiosks
    if (kiosks.isEmpty()) {
      txt_firstLocation.clear();
      lst_firstLocation.getItems().clear();
    } else {
      txt_firstLocation.setText(kiosks.getFirst());
      ObservableList<String> textList = FXCollections.observableList(kiosks);
      lst_firstLocation.setItems(textList);
      lst_firstLocation.getSelectionModel().select(0);
    }
  }

  /*
   private void styleLine(Line line) {
     line.setStrokeWidth(5);
     line.setStrokeLineCap(StrokeLineCap.ROUND);
     line.setStrokeLineJoin(StrokeLineJoin.ROUND);
   }
  */

  // Upon clicking find path to location button call this method
  /*    @FXML
  private void onDoctorPathFindClicked(MouseEvent event) throws Exception {
    pn_path.getChildren().removeIf(node -> node instanceof Line);
    int currentSelection = lst_doctorlocations.getSelectionModel().getSelectedIndex();
    DbNode destinationNode = doctorNodes.get(currentSelection);
    if (selectedNodes.size() < 1) selectedNodes.add(defaultNode);
    selectedNodes.add(destinationNode);
    // if (selectedNodes.size() < 2) selectedNodes.add(defaultNode);
    onBtnFindClicked(event);
    selectedNodes.clear();
  }*/
}
