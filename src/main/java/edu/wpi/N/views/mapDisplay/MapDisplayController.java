// package edu.wpi.N.views.mapDisplay;
//
// import com.jfoenix.controls.*;
// import edu.wpi.N.App;
// import edu.wpi.N.database.CSVParser;
// import edu.wpi.N.database.DBException;
// import edu.wpi.N.database.MapDB;
// import edu.wpi.N.entities.DbNode;
// import edu.wpi.N.entities.Path;
// import edu.wpi.N.entities.States.StateSingleton;
// import edu.wpi.N.entities.employees.Doctor;
// import edu.wpi.N.views.Controller;
// import java.io.IOException;
// import java.net.URL;
// import java.util.ArrayList;
// import java.util.ResourceBundle;
// import javafx.fxml.FXML;
// import javafx.fxml.FXMLLoader;
// import javafx.fxml.Initializable;
// import javafx.scene.Parent;
// import javafx.scene.Scene;
// import javafx.scene.control.*;
// import javafx.scene.input.MouseEvent;
// import javafx.scene.layout.AnchorPane;
// import javafx.scene.layout.Pane;
// import javafx.scene.layout.StackPane;
// import javafx.scene.text.Text;
// import javafx.stage.Modality;
// import javafx.stage.Stage;
//
// public class MapDisplayController implements Controller, Initializable {
//
//  private StateSingleton singleton;
//
//  private App mainApp = null;
//
//  @FXML Pane pn_display;
//  @FXML Pane pn_changeFloor;
//  @FXML JFXTextField txt_firstLocation;
//  @FXML JFXTextField txt_secondLocation;
//  @FXML JFXListView<DbNode> lst_firstLocation;
//  @FXML JFXListView<DbNode> lst_secondLocation;
//  // @FXML JFXButton btn_searchdoc;
//  @FXML JFXTextField txt_doctorname;
//  @FXML ListView<Doctor> lst_doctornames;
//  // @FXML Button btn_searchdoc;
//  @FXML ListView<DbNode> lst_doctorlocations;
//  @FXML Button btn_findpathdoc;
//  @FXML JFXButton btn_findPath;
//  @FXML JFXButton btn_home;
//  @FXML private JFXButton btn_floors;
//  @FXML TitledPane pn_locationSearch;
//  @FXML Accordion acc_search;
//  @FXML Text txt_description;
//  @FXML JFXCheckBox handicapp1;
//  @FXML JFXCheckBox handicapp2;
//  @FXML StackPane mapContainer; // Reference to the StackPane containing embded map
//  @FXML AnchorPane googleMapView;
//  @FXML AnchorPane hospitalView;
//
//  @FXML JFXButton btn_faulkner;
//  @FXML JFXButton btn_main;
//  @FXML JFXButton btn_google;
//
//  @FXML MapBaseController mapBaseController; // Reference to the embedded map
//  @FXML GoogleMapController googleMapController;
//
//  private ArrayList<String> directions;
//  JFXNodesList floorButtonList = new JFXNodesList();
//
//  // Program variables
//  Path path;
//  int currentFloor;
//  String currentBuilding;
//
//  // Inject singleton
//  //  public MapDisplayController(StateSingleton singleton) {
//  //    this.singleton = singleton;
//  //  }
//
//  @Override
//  public void initialize(URL location, ResourceBundle resourceBundle) {
//    //    path = new Path(new LinkedList<>());
//    //    currentFloor = 1;
//    //    currentBuilding = "Faulkner";
//    //    createFloorButtons();
//    //    hospitalView = mapBaseController.getAnchorPane();
//    //    try {
//    //      mapBaseController.setFloor(this.currentBuilding, this.currentFloor, this.path);
//    //    } catch (DBException e) {
//    //      e.printStackTrace();
//    //    }
//    //    acc_search.setExpandedPane(pn_locationSearch);
//    //    try {
//    //      setDefaultKioskNode();
//    //    } catch (NullPointerException | DBException | IOException e) {
//    //      e.printStackTrace();
//    //    }
//  }
//
////  /** Switches the Map Base view to Loaded previously Google Map View */
////  @FXML
////  public void switchToGoogleView() {
////    mapContainer.getChildren().setAll(googleMapView);
////  }
//
////  /** Switches the Map Base view to Faulkner Map */
////  @FXML
////  public void switchToFaulkner() {
////    try {
////      mapContainer.getChildren().setAll(hospitalView);
////      mapBaseController.setBuilding("Faulkner", 1, this.path);
////    } catch (DBException e) {
////      e.printStackTrace();
////      displayErrorMessage("Error: Switching to Faulkner");
////    }
////  }
////
////  /**
////   * Switches the Map Base view to Main Hospital Map
////   *
////   * @throws DBException
////   */
////  @FXML
////  public void switchToMain() {
////    int numFloor = CSVParser.convertFloor("L2"); // Number for main Entrance on 45 Francis
// street
////    try {
////      mapContainer.getChildren().setAll(hospitalView);
////      // TODO: make it point to Main
////      mapBaseController.setBuilding("Main", numFloor, this.path);
////    } catch (DBException e) {
////      e.printStackTrace();
////      displayErrorMessage("Error: Switching to Main Hospital - 45 Francis");
////    }
////  }
//
//  //  /** creates the buttons which enables the user to view different floors */
//  //  public void createFloorButtons() {
//  //    LinkedList floorButtons = new LinkedList();
//  //    initFloorButtons(floorButtons);
//  //    styleFloorButtons(floorButtons);
//  //    displayFloorButtonList(floorButtons);
//  //  }
//
//  //  /**
//  //   * populates the list of buttons which enable the user to view different floors
//  //   *
//  //   * @param floorButtons the empty list of buttons which enable the user to view different
//  // floors
//  //   * @return the populated list of buttons which enable the user to view different floors
//  //   */
//  //  public void initFloorButtons(LinkedList<JFXButton> floorButtons) {
//  //    btn_floors = new JFXButton("Floors");
//  //    floorButtons.add(btn_floors);
//  //    for (int i = 1; i <= 5; i++) {
//  //      JFXButton btn = new JFXButton();
//  //      btn.setText(String.valueOf(i));
//  //      btn.setOnMouseClicked(
//  //          e -> {
//  //            try {
//  //              changeFloor(btn);
//  //              setDefaultKioskNode();
//  //            } catch (DBException | IOException ex) {
//  //              ex.printStackTrace();
//  //            }
//  //          });
//  //      floorButtons.add(btn);
//  //    }
//  //  }
//  //
//  //  public void changeFloor(JFXButton btn) throws DBException {
//  //    this.currentFloor = Integer.parseInt(btn.getText());
//  //    mapBaseController.setFloor(this.currentBuilding, this.currentFloor, this.path);
//  //  }
//
//  //  /**
//  //   * styles the buttons which enable the user to view different floors
//  //   *
//  //   * @param floorButtons the list of buttons which enable the user to view different floors
//  //   */
//  //  public void styleFloorButtons(LinkedList<JFXButton> floorButtons) {
//  //    for (JFXButton btn : floorButtons) {
//  //      btn.setButtonType(JFXButton.ButtonType.RAISED);
//  //      btn.getStylesheets()
//  //
//  // .addAll(getClass().getResource("/edu/wpi/N/css/MapDisplayFloors.css").toExternalForm());
//  //      btn.getStyleClass().addAll("animated-option-button");
//  //    }
//  //  }
//
//  //  /**
//  //   * displays the buttons which enable the user to view different floors
//  //   *
//  //   * @param floorButtons the populated and styled list of buttons which enable the user to
//  // view
//  //   *     different floors
//  //   */
//  //  public void displayFloorButtonList(LinkedList<JFXButton> floorButtons) {
//  //    for (JFXButton btn : floorButtons) {
//  //      floorButtonList.addAnimatedNode(btn);
//  //    }
//  //    floorButtonList.setSpacing(20);
//  //    pn_changeFloor.getChildren().add(floorButtonList);
//  //  }
//  //
//  //  /**
//  //   * detects when user enters a first location and applies fuzzy search
//  //   *
//  //   * @param e the key event which triggers the search
//  //   * @throws DBException
//  //   */
//  //  public void onSearchFirstLocation(KeyEvent e) throws DBException {
//  //    fuzzyLocationSearch(txt_firstLocation, lst_firstLocation);
//  //  }
//
//  //  /**
//  //   * detects when user enters a second path location and applies fuzzy search
//  //   *
//  //   * @param e the key event which triggers the search
//  //   * @throws DBException
//  //   */
//  //  public void onSearchSecondLocation(KeyEvent e) throws DBException {
//  //    fuzzyLocationSearch(txt_secondLocation, lst_secondLocation);
//  //  }
//
//  //  /**
//  //   * detects when user enters a doctor and applies fuzzy search
//  //   *
//  //   * @param e the key event which triggers the search
//  //   * @throws DBException
//  //   */
//  //  public void onSearchDoctor(KeyEvent e) throws DBException {
//  //    fuzzyDoctorSearch(txt_doctorname, lst_doctornames);
//  //  }
//
//  //  /**
//  //   * applies fuzzy search to the user input for locations
//  //   *
//  //   * @param txt the textfield with the user input
//  //   * @param lst the fuzzy search results
//  //   * @throws DBException
//  //   */
//  //  public void fuzzyLocationSearch(JFXTextField txt, ListView lst) throws DBException {
//  //    ObservableList<DbNode> fuzzyList;
//  //    String str = txt.getText();
//  //    fuzzyList = FXCollections.observableList(FuzzySearchAlgorithm.suggestLocations(str));
//  //    lst.setItems(fuzzyList);
//  //  }
//
//  //  /**
//  //   * applies fuzzy search to the user input for doctors
//  //   *
//  //   * @param txt the textfield with the user input
//  //   * @param lst the fuzzy search results
//  //   * @throws DBException
//  //   */
//  //  public void fuzzyDoctorSearch(JFXTextField txt, ListView lst) throws DBException {
//  //    ObservableList<Doctor> fuzzyList;
//  //    String str = txt.getText();
//  //    fuzzyList = FXCollections.observableList(FuzzySearchAlgorithm.suggestDoctors(str));
//  //    lst.setItems(fuzzyList);
//  //  }
//
//  //  /**
//  //   * finds the doctor's department and displays the result in a list view
//  //   *
//  //   * @param e the key event which triggers the search
//  //   * @throws Exception
//  //   */
//  //  @FXML
//  //  private void onFindDoctorClicked(MouseEvent e) throws Exception {
//  //    Doctor doc = lst_doctornames.getSelectionModel().getSelectedItem();
//  //    ObservableList<DbNode> docNodes = FXCollections.observableList(doc.getLoc());
//  //    lst_doctorlocations.setItems(docNodes);
//  //  }
//
//  //  /**
//  //   * triggers path finding for two chosen locations
//  //   *
//  //   * @param event the mouse event which triggers path finding
//  //   * @throws Exception
//  //   */
//  //  public void onBtnPathfindClicked(MouseEvent event) throws Exception {
//  //    initPathfind(lst_firstLocation, lst_secondLocation.getSelectionModel().getSelectedItem());
//  //  }
//
//  //  public void onDoctorPathFindClicked(MouseEvent event) throws Exception {
//  //    initPathfind(lst_firstLocation,
//  // lst_doctorlocations.getSelectionModel().getSelectedItem());
//  //  }
//
//  public void findPathToCafetaria(MouseEvent e) throws DBException {
//    initPathfind(lst_firstLocation, MapDB.getNode("MRETL00203"));
//  }
//
//  public void findPathToStarBucks(MouseEvent e) throws DBException {
//    initPathfind(lst_firstLocation, MapDB.getNode("NRETL00201"));
//  }
//
////  public void initPathfind(ListView<DbNode> firstLst, DbNode second) throws DBException {
////    DbNode first = firstLst.getSelectionModel().getSelectedItem();
////    if (first == null || second == null) {
////      displayErrorMessage("Please select a location");
////      return;
////    }
////
////    // Reset the view to start with Start Node building and floor
//////    resetViewToStart(first);
////
////    // Check if the start building is diff than end building
////    if (!first.getBuilding().equals(second.getBuilding())
////        && (first.getBuilding().equals("Faulkner") || second.getBuilding().equals("Faulkner")))
// {
////      try {
////        FXMLLoader loader = new FXMLLoader();
////        loader.setLocation(App.class.getResource("views/mapDisplay/googleMap.fxml"));
////
////        String pathToHTML = null;
////
////        if (first.getBuilding().equals("Faulkner")) {
////          // go from Falkner to Main
////          pathToHTML = "views/googleMapFaulknerToMain.html";
////        } else {
////          // going from Main to Falkner
////          pathToHTML = "views/googleMapMainToFaulkner.html";
////        }
////
////        // inject the path to html file to the GoogleMapController
////        String finalPathToHTML = pathToHTML;
////        loader.setControllerFactory(
////            type -> {
////              try {
////                return new GoogleMapController(finalPathToHTML);
////              } catch (Exception exc) {
////                throw new RuntimeException(exc);
////              }
////            });
////
////        googleMapView = loader.load();
////
////        // Enable buttons for switching between maps
////        btn_faulkner.setVisible(true);
////        btn_main.setVisible(true);
////        btn_google.setVisible(true);
////      } catch (IOException ex) {
////        ex.printStackTrace();
////        displayErrorMessage("Error when loading Google Map");
////        return;
////      }
////    }
//
////    this.path =
////        singleton.savedAlgo.findPath(
////            first, second, handicapp1.isSelected() || handicapp2.isSelected());
////    mapBaseController.setFloor(first.getBuilding(), first.getFloor(), path);
////    disableNonPathFloors();
////    setTextDecription();
////  }
//
//  public void findPathToBathroom(MouseEvent e) throws DBException {
//    initQuickAccess(lst_firstLocation, "REST");
//  }
//
//  public void initQuickAccess(ListView<DbNode> firstLst, String type) throws DBException {
//    DbNode first = lst_firstLocation.getSelectionModel().getSelectedItem();
//    this.path = singleton.savedAlgo.findQuickAccess(first, type);
//    mapBaseController.setFloor(first.getBuilding(), first.getFloor(), path);
//    disableNonPathFloors();
//    setTextDecription();
//  }
//
////  private void enableAllFloorButtons() {
////    for (int i = 1; i < floorButtonList.getChildren().size(); i++) {
////      JFXButton btn = (JFXButton) floorButtonList.getChildren().get(i);
////      btn.setDisable(false);
////    }
////  }
//
////  private void disableNonPathFloors() {
////    floorButtonList.getChildren().forEach(e -> e.setDisable(true));
////    JFXButton startButton = (JFXButton) floorButtonList.getChildren().get(0);
////    startButton.setDisable(false);
////    for (int i = 0; i < path.size() - 1; i++) {
////      DbNode node = path.get(i);
////      if (!(node.getNodeType().equals("ELEV") || node.getNodeType().equals("STAI"))) {
////        JFXButton btn = (JFXButton) floorButtonList.getChildren().get(node.getFloor());
////        btn.setDisable(false);
////      }
////    }
//  }
//  //    for (int i = 0; i < path.size(); i++) {
//  //      DbNode node = path.get(i);
//  //      JFXButton btn = (JFXButton) floorButtonList.getChildren().get(node.getFloor() + 1);
//  //      btn.setDisable(true);
//  //      if (!(node.getNodeType().equals("ELEV") || node.getNodeType().equals("STAI"))) {
//  //        btn.setDisable(false);
//  //      }
//  //    }
//  //  }
//
////  /** Function resets the view to be of Start Node's floor and building */
////  public void resetViewToStart(DbNode start) {
////    try {
////      btn_google.setVisible(false);
////      mapContainer.getChildren().setAll(hospitalView);
////
////      // Check which building this is
////      if (start.getBuilding().equals("Faulkner")) {
////        mapBaseController.setBuilding(start.getBuilding(), start.getFloor(), null);
////      } else {
////        // TODO: change it to work so the view gets selected as Start Node building and floor
////        // A.K. Generic
////        int numFloor = CSVParser.convertFloor("L2");
////        mapBaseController.setBuilding("Main", numFloor, this.path);
////      }
////    } catch (DBException ex) {
////      displayErrorMessage("Error when resetting view to Start Node");
////    }
////  }
//
//  //  public void onBtnResetPathClicked() throws DBException, IOException {
//  //    //    path.clear();
//  //    //    enableAllFloorButtons();
//  //    //    txt_firstLocation.clear();
//  //    //    txt_secondLocation.clear();
//  //    //    lst_firstLocation.getItems().clear();
//  //    lst_secondLocation.getItems().clear();
//  //    mapBaseController.clearPath();
//  //    setDefaultKioskNode();
//  //  }
//
//  //  public void setMainApp(App mainApp) {
//  //    this.mainApp = mainApp;
//  //  }
//
//  //  public void onBtnHomeClicked() throws IOException {
//  //    mainApp.switchScene("views/newHomePage.fxml", singleton);
//  //  }
//
//  //  public void displayErrorMessage(String str) {
//  //    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
//  //    errorAlert.setHeaderText("Invalid input");
//  //    errorAlert.setContentText(str);
//  //    errorAlert.showAndWait();
//  //  }
//
//  /**
//   * Function generates and sets textual description label to Textual Descriptions
//   *
//   * @param
//   */
//  private void setTextDecription() {
//    try {
//      // Convert the array of textual descriptions to text
//      String directionsAsText = "";
//      directions = path.getDirections();
//      for (String s : directions) {
//        directionsAsText += s;
//        directionsAsText += "\n";
//      }
//
//      // Check to make sure that directionAsText isn't empty
//      if (!directionsAsText.equals("")) {
//        txt_description.setText(directionsAsText);
//      }
//
//    } catch (Exception ex) {
//      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
//      errorAlert.setHeaderText("Oops... Something went Wong");
//      errorAlert.setContentText("Textual descriptions could not be generated");
//      errorAlert.showAndWait();
//    }
//  }
//
//  /** Function displays a pop-up window with user's directions */
//  @FXML
//  private void displayQRCode() throws IOException {
//    try {
//      Stage stage = new Stage();
//      Parent root;
//      FXMLLoader loader = new FXMLLoader();
//      loader.setLocation(getClass().getResource("qrPopUp.fxml"));
//      root = loader.load();
//      Scene scene = new Scene(root);
//      stage.setScene(scene);
//
//      QrPopUpController controller = (QrPopUpController) loader.getController();
//      controller.displayQrCode(directions);
//
//      stage.initModality(Modality.APPLICATION_MODAL);
//      stage.show();
//    } catch (Exception ex) {
//      ex.printStackTrace();
//      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
//      errorAlert.setHeaderText("Oops... Something went Wong");
//      errorAlert.setContentText("QR code with directions could not be generated");
//      errorAlert.showAndWait();
//    }
//  }
//
//  //  private void setDefaultKioskNode() throws DBException, IOException {
//  //
//  //    boolean noFaulknerKiosk =
//  //        !(currentBuilding.equals("Faulkner") && (currentFloor == 1 || currentFloor == 3));
//  //    if (noFaulknerKiosk) {
//  //      clearKioskFields();
//  //      return;
//  //    }
//  //    DbNode kiosk = null;
//  //    if (currentBuilding.equals("Faulkner") && currentFloor == 1) {
//  //      kiosk = MapDB.getNode("NSERV00301");
//  //    } else if (currentBuilding.equals("Faulkner") && currentFloor == 3) {
//  //      kiosk = MapDB.getNode("NSERV00103");
//  //    }
//  //
//  //    txt_firstLocation.clear();
//  //    lst_firstLocation.getItems().clear();
//  //    try {
//  //      txt_firstLocation.setText(kiosk.toString());
//  //    } catch (NullPointerException e) {
//  //      displayErrorMessage("There are no items in the database!");
//  //      return;
//  //    }
//  //    lst_firstLocation.getItems().add(kiosk);
//  //    lst_firstLocation.getSelectionModel().select(0);
//  //  }
//  //
//  //  private void clearKioskFields() {
//  //    txt_firstLocation.clear();
//  //    lst_firstLocation.getItems().clear();
//  //  }
// }
