package edu.wpi.N.views.mapDisplay;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXNodesList;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.Direction;
import edu.wpi.N.algorithms.Directions;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.entities.employees.Doctor;
import edu.wpi.N.qrcontrol.QRGenerator;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class NewMapDisplayController extends QRGenerator implements Controller {
  private App mainApp = null;
  private StateSingleton singleton;

  @FXML Pane pn_change;
  @FXML Pane pn_iconBar;
  @FXML Pane pn_locationIcon;
  @FXML Pane pn_doctorIcon;
  @FXML Pane pn_qrIcon;
  @FXML Pane pn_serviceIcon;
  @FXML Pane pn_infoIcon;
  @FXML Pane pn_adminIcon;
  @FXML Pane pn_floors;
  @FXML Pane pn_mapContainer;
  @FXML Pane pn_googleMapView;
  @FXML Pane pn_hospitalView;
  @FXML Pane chatbotView;
  @FXML Label lbl_building_floor;
  @FXML AnchorPane pn_background;

  @FXML MapBaseController mapBaseController;

  MapLocationSearchController locationSearchController;
  MapDoctorSearchController doctorSearchController;
  MapQRController mapQRController;

  Path path;
  int currentFloor;
  String currentBuilding;
  ArrayList<String> directions;
  JFXNodesList buildingButtonList;
  JFXNodesList faulknerButtonList;
  JFXNodesList mainButtonList;
  JFXButton btn_google;
  ArrayList<JFXButton> pathButtonList;

  /**
   * provides reference to the main application class
   *
   * @param mainApp the main class of the application
   */
  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  /**
   * constructor for the map display class
   *
   * @param singleton the singleton which is initiated at the beginning of the program
   */
  public NewMapDisplayController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  /**
   * initializes all variables required for the map display
   *
   * @throws DBException
   * @throws IOException
   */
  public void initialize() throws DBException, IOException {
    this.path = new Path(new LinkedList<>());
    this.currentFloor = 1;
    this.currentBuilding = "Faulkner";
    setBackground("Faulkner");
    this.directions = new ArrayList<>();
    this.pathButtonList = new ArrayList<>();
    this.buildingButtonList = new JFXNodesList();
    this.faulknerButtonList = new JFXNodesList();
    this.mainButtonList = new JFXNodesList();
    this.pn_hospitalView = mapBaseController.getAnchorPane();
    mapBaseController.setFloor(this.currentBuilding, this.currentFloor, this.path);
    setFloorBuildingText(this.currentFloor, this.currentBuilding);
    pn_mapContainer.getChildren().setAll(pn_hospitalView);
    pn_iconBar.getChildren().get(0).setStyle("-fx-background-color: #4A69C6;");
    initFloorButtons();
    initFunctionPane();
    setDefaultKioskNode();
    disableTextDirections();
  }

  /**
   * initializes the map display with the location search functionality
   *
   * @throws IOException
   * @throws DBException
   */
  public void initFunctionPane() throws IOException, DBException {
    FXMLLoader loader;
    loader = new FXMLLoader(getClass().getResource("mapLocationSearch.fxml"));
    Pane pane = loader.load();
    locationSearchController = loader.getController();
    initLocationSearchButton();
    initResetLocationSearch();
    initRestroomSearchButton();
    initInfoSearchButton();
    initExitSearchButton();
    pn_change.getChildren().add(pane);
  }

  /**
   * styles the switch, faulkner, main, and driving directions buttons
   *
   * @param btn the building button which is to be styled
   */
  public void styleBuildingButtons(JFXButton btn) {
    btn.getStylesheets()
        .add(getClass().getResource("/edu/wpi/N/css/MapDisplayFloors.css").toExternalForm());
    btn.getStyleClass().add("header-button");
  }

  /**
   * sets background color to match map
   *
   * @param building
   */
  public void setBackground(String building) {
    if (building.equals("Faulkner")) {
      pn_background.setStyle("-fx-background-color: #E6EBF2");
    } else {
      pn_background.setStyle("-fx-background-color: #D3D3D3");
    }
  }

  /**
   * styles the sub buttons of the building buttons
   *
   * @param btn the floor button which is to be styled
   */
  public void styleFloorButtons(JFXButton btn) {
    btn.getStylesheets()
        .add(getClass().getResource("/edu/wpi/N/css/MapDisplayFloors.css").toExternalForm());
    btn.getStyleClass().add("choice-button");
  }

  /**
   * initializes all buttons required to switch between floors
   *
   * @throws DBException
   */
  public void initFloorButtons() throws DBException {
    // Building Buttons
    JFXButton btn_buildings = new JFXButton("Switch Map");
    styleBuildingButtons(btn_buildings);
    JFXButton btn_faulkner = new JFXButton("Faulkner");
    styleBuildingButtons(btn_faulkner);
    JFXButton btn_main = new JFXButton("Main");
    styleBuildingButtons(btn_main);
    btn_google = new JFXButton("Street View");
    styleBuildingButtons(btn_google);
    setGoogleButtonDisable(true);

    // Faulkner Buttons
    JFXButton btn_faulkner1 = new JFXButton("F1");
    styleFloorButtons(btn_faulkner1);

    JFXButton btn_faulkner2 = new JFXButton("F2");
    styleFloorButtons(btn_faulkner2);
    JFXButton btn_faulkner3 = new JFXButton("F3");
    styleFloorButtons(btn_faulkner3);
    JFXButton btn_faulkner4 = new JFXButton("F4");
    styleFloorButtons(btn_faulkner4);
    JFXButton btn_faulkner5 = new JFXButton("F5");
    styleFloorButtons(btn_faulkner5);
    faulknerButtonList
        .getChildren()
        .addAll(
            btn_faulkner,
            btn_faulkner5,
            btn_faulkner4,
            btn_faulkner3,
            btn_faulkner2,
            btn_faulkner1);

    // Main Buttons
    JFXButton btn_main1 = new JFXButton("L2");
    styleFloorButtons(btn_main1);
    JFXButton btn_main2 = new JFXButton("L1");
    styleFloorButtons(btn_main2);
    JFXButton btn_main3 = new JFXButton("G");
    styleFloorButtons(btn_main3);
    JFXButton btn_main4 = new JFXButton("1");
    styleFloorButtons(btn_main4);
    JFXButton btn_main5 = new JFXButton("2");
    styleFloorButtons(btn_main5);
    JFXButton btn_main6 = new JFXButton("3");
    styleFloorButtons(btn_main6);

    // Set onClick properties
    onFloorButtonClicked(btn_faulkner1);
    onFloorButtonClicked(btn_faulkner2);
    onFloorButtonClicked(btn_faulkner3);
    onFloorButtonClicked(btn_faulkner4);
    onFloorButtonClicked(btn_faulkner5);
    onFloorButtonClicked(btn_main1);
    onFloorButtonClicked(btn_main2);
    onFloorButtonClicked(btn_main3);
    onFloorButtonClicked(btn_main4);
    onFloorButtonClicked(btn_main5);
    onFloorButtonClicked(btn_main6);
    onFloorButtonClicked(btn_google);

    mainButtonList
        .getChildren()
        .addAll(btn_main, btn_main6, btn_main5, btn_main4, btn_main3, btn_main2, btn_main1);
    buildingButtonList.addAnimatedNode(btn_buildings);
    buildingButtonList.addAnimatedNode(faulknerButtonList);
    buildingButtonList.addAnimatedNode(mainButtonList);
    buildingButtonList.addAnimatedNode(btn_google);

    buildingButtonList.setSpacing(120);
    buildingButtonList.setRotate(-90);
    faulknerButtonList.setSpacing(15);
    mainButtonList.setSpacing(15);

    btn_faulkner.setOnMouseClicked(
        e -> {
          mainButtonList.animateList(false);
        });
    btn_main.setOnMouseClicked(
        e -> {
          faulknerButtonList.animateList(false);
        });
    pn_floors.getChildren().add(buildingButtonList);
  }

  /**
   * initializes a listener for a floor button click event
   *
   * @param btn the floor button which is clicked
   * @throws DBException
   */
  public void onFloorButtonClicked(JFXButton btn) throws DBException {
    String txt = btn.getText();
    btn.setOnMouseClicked(
        e -> {
          try {
            handleFloorButtonClicked(txt);
            if (this.path != null && this.path.size() > 0 && btn != btn_google) {
              for (int i = 0; i < pathButtonList.size(); i++) {
                pathButtonList.get(i).setStyle("-fx-background-color: #4A69C6");
                if (pathButtonList.get(i) == btn && i < pathButtonList.size() - 1) {
                  pathButtonList.get(i + 1).setStyle("-fx-background-color: #6C5C7F");
                  i++;
                }
              }
            }
          } catch (DBException ex) {
            ex.printStackTrace();
          }
        });
  }

  public void handleFloorButtonClicked(String txt) throws DBException {
    if (txt.equals("F1")) {
      handleFloorButtonClickedHelper(1, "Faulkner");
    } else if (txt.equals("F2")) {
      handleFloorButtonClickedHelper(2, "Faulkner");
    } else if (txt.equals("F3")) {
      handleFloorButtonClickedHelper(3, "Faulkner");
    } else if (txt.equals("F4")) {
      handleFloorButtonClickedHelper(4, "Faulkner");
    } else if (txt.equals("F5")) {
      handleFloorButtonClickedHelper(5, "Faulkner");
    } else if (txt.equals("L2")) {
      handleFloorButtonClickedHelper(1, "Main");
    } else if (txt.equals("L1")) {
      handleFloorButtonClickedHelper(2, "Main");
    } else if (txt.equals("G")) {
      handleFloorButtonClickedHelper(3, "Main");
    } else if (txt.equals("1")) {
      handleFloorButtonClickedHelper(4, "Main");
    } else if (txt.equals("2")) {
      handleFloorButtonClickedHelper(5, "Main");
    } else if (txt.equals("3")) {
      handleFloorButtonClickedHelper(6, "Main");
    } else if (txt.equals("Street View")) {
      handleFloorButtonClickedHelper(0, "Drive");
    }
  }

  public void handleFloorButtonClickedHelper(int floor, String building) throws DBException {
    if (building.equals("Faulkner") || building.equals("Main")) {
      changeFloor(floor, building);
      switchHospitalView();
    } else {
      this.currentFloor = 0;
      this.currentBuilding = "Drive";
      switchGoogleView();
    }
    if (mapQRController != null && pn_iconBar.getChildren().contains(pn_qrIcon)) {
      mapQRController.setTabFocus(floor, building);
    }
    if (path == null || path.size() == 0) {
      setDefaultKioskNode();
    }
  }

  public void changeFloor(int newFloor, String newBuilding) throws DBException {
    mapBaseController.clearPath();
    this.currentFloor = newFloor;
    this.currentBuilding = newBuilding;
    setBackground(newBuilding);
    setFloorBuildingText(this.currentFloor, this.currentBuilding);
    mapBaseController.setFloor(this.currentBuilding, this.currentFloor, this.path);
  }

  /** initiates a listener for the search button on location search */
  public void initLocationSearchButton() {
    locationSearchController
        .getSearchButton()
        .setOnMouseClicked(
            e -> {
              try {
                initPathfind(
                    (locationSearchController.getDBNodes())[0],
                    (locationSearchController.getDBNodes())[1],
                    locationSearchController.getHandicap());
                enableTextDirections();
              } catch (DBException | IOException ex) {
                ex.printStackTrace();
              }
            });
  }

  /** initiates a listener for the search button on a doctor search */
  public void initDoctorSearchButton() {
    doctorSearchController
        .getSearchButton()
        .setOnMouseClicked(
            e -> {
              try {
                initPathfind(
                    (doctorSearchController.getDBNodes())[0],
                    (doctorSearchController.getDBNodes())[1],
                    doctorSearchController.getHandicap());
                enableTextDirections();
              } catch (DBException | IOException ex) {
                ex.printStackTrace();
              }
            });
  }

  /**
   * initiates a listener for the reset button on a location search
   *
   * @throws DBException
   */
  public void initResetLocationSearch() throws DBException {
    locationSearchController
        .getResetButton()
        .setOnMouseClicked(
            e -> {
              if (path != null) {
                this.path.clear();
              }
              setGoogleButtonDisable(true);
              locationSearchController.getTextFirstLocation().clear();
              locationSearchController.getTextSecondLocation().clear();
              locationSearchController.getFuzzyList().getItems().clear();
              locationSearchController.getTgHandicap().setSelected(false);
              locationSearchController.clearDbNodes();
              mapBaseController.clearPath();
              disableTextDirections();
              enableAllFloorButtons();
              mainButtonList.animateList(false);
              faulknerButtonList.animateList(false);
              try {
                setDefaultKioskNode();
              } catch (DBException ex) {
                ex.printStackTrace();
              }
            });
  }

  /**
   * initiates a listener for the reset button on a doctor search
   *
   * @throws DBException
   */
  public void initResetDoctorSearch() throws DBException {
    doctorSearchController
        .getResetButton()
        .setOnMouseClicked(
            e -> {
              if (this.path != null) {
                this.path.clear();
              }
              enableAllFloorButtons();
              setGoogleButtonDisable(true);
              doctorSearchController.getTextLocation().clear();
              doctorSearchController.getTxtDoctor().clear();
              doctorSearchController.getFuzzyList().getItems().clear();
              doctorSearchController.getTgHandicap().setSelected(false);
              doctorSearchController.clearDbNodes();
              mapBaseController.clearPath();
              mainButtonList.animateList(false);
              faulknerButtonList.animateList(false);
              disableTextDirections();
              enableAllFloorButtons();
              try {
                setDefaultKioskNode();
              } catch (DBException ex) {
                ex.printStackTrace();
              }
            });
  }

  /**
   * initiates a listener for the restroom quick search on a location search
   *
   * @throws DBException
   */
  public void initRestroomSearchButton() throws DBException {
    locationSearchController
        .getBtnRestRoom()
        .setOnMouseClicked(
            e -> {
              DbNode first = locationSearchController.getDBNodes()[0];
              try {
                enableTextDirections();
                this.path = singleton.savedAlgo.findQuickAccess(first, "REST");
                mapBaseController.setFloor(first.getBuilding(), first.getFloor(), path);
                if (path.size() == 0) {
                  displayErrorMessage("Please select the first node");
                }
              } catch (DBException | NullPointerException ex) {
                displayErrorMessage("Please select the first node");
                return;
              }
              disableNonPathFloors();
              if (pathButtonList.size() > 1) {
                pathButtonList.get(1).setStyle("-fx-background-color: #6C5C7F;");
              }
            });
  }

  /**
   * initiates a listener for the info desk quick search on a location search
   *
   * @throws DBException
   */
  public void initInfoSearchButton() throws DBException {
    locationSearchController
        .getBtnInfoDesk()
        .setOnMouseClicked(
            e -> {
              DbNode first = locationSearchController.getDBNodes()[0];
              if (first.getBuilding().equals("Faulkner")) {
                displayErrorMessage("No information desks in this building");
                return;
              }
              try {
                resetTextualDirections();
                this.path = singleton.savedAlgo.findQuickAccess(first, "INFO");
                mapBaseController.setFloor(first.getBuilding(), first.getFloor(), path);
                if (path.size() == 0) {
                  displayErrorMessage("Please select the first node");
                }
              } catch (DBException | NullPointerException ex) {
                displayErrorMessage("Please select the first node");
                return;
              }
              disableNonPathFloors();
              if (pathButtonList.size() > 1) {
                pathButtonList.get(1).setStyle("-fx-background-color: #6C5C7F;");
              }
            });
  }

  /**
   * initiates a listener for the exit quick search on a location search
   *
   * @throws DBException
   */
  public void initExitSearchButton() throws DBException {
    locationSearchController
        .getBtnQuickExit()
        .setOnMouseClicked(
            e -> {
              DbNode first = locationSearchController.getDBNodes()[0];
              try {
                resetTextualDirections();
                this.path = singleton.savedAlgo.findQuickAccess(first, "EXIT");
                mapBaseController.setFloor(first.getBuilding(), first.getFloor(), path);
                if (path.size() == 0) {
                  displayErrorMessage("Please select the first node");
                }
              } catch (DBException | NullPointerException ex) {
                displayErrorMessage("Please select the first node");
                return;
              }
              disableNonPathFloors();
              if (pathButtonList.size() > 1) {
                pathButtonList.get(1).setStyle("-fx-background-color: #6C5C7F;");
              }
            });
  }

  /**
   * switches the floor to the first node of the path and initiates path finding
   *
   * @param first the first node in the path
   * @param second last node in the path
   * @param isSelected determines whether the handicap option is selected
   * @throws DBException
   * @throws IOException
   */
  public void initPathfind(DbNode first, DbNode second, boolean isSelected)
      throws DBException, IOException {
    if (first == null || second == null) {
      displayErrorMessage("Please select a location");
      return;
    }
    this.path = singleton.savedAlgo.findPath(first, second, isSelected);
    if (path == null) {
      displayErrorMessage("No path can be found");
    }
    switchHospitalView();
    mapBaseController.setFloor(first.getBuilding(), first.getFloor(), path);
    this.currentBuilding = first.getBuilding();
    this.currentFloor = first.getFloor();
    if (first.getBuilding().equals("Faulkner")) {
      faulknerButtonList.animateList(true);
      mainButtonList.animateList(false);
    } else {
      faulknerButtonList.animateList(false);
      mainButtonList.animateList(true);
    }
    disableNonPathFloors();
    if (pathButtonList.size() > 1) {
      pathButtonList.get(1).setStyle("-fx-background-color: #6C5C7F;");
    }
    displayGoogleMaps(first, second);
  }

  /**
   * determine whether the google map button should be activated and display a path between
   * buildings
   *
   * @param first the first node in the path
   * @param second the second node in the path
   * @throws IOException
   */
  public void displayGoogleMaps(DbNode first, DbNode second) throws IOException {
    boolean isFirstFaulkner = first.getBuilding().equals("Faulkner");
    boolean isSecondFaulkner = second.getBuilding().equals("Faulkner");
    setGoogleButtonDisable(true);
    if (isFirstFaulkner ^ isSecondFaulkner) {
      setGoogleButtonDisable(false);
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(App.class.getResource("views/mapDisplay/googleMap.fxml"));

      String pathToHTML = null;

      if (isFirstFaulkner) {
        pathToHTML = "views/googleMapFaulknerToMain.html";
      } else {
        pathToHTML = "views/googleMapMainToFaulkner.html";
      }

      String finalPathToHTML = pathToHTML;

      loader.setControllerFactory(
          type -> {
            try {
              return new GoogleMapController(finalPathToHTML);
            } catch (Exception exc) {
              throw new RuntimeException(exc);
            }
          });
      pn_googleMapView = loader.load();
    }
  }

  /**
   * initiates fuzzy search based on a location
   *
   * @param txt the textfield which initiates the fuzzy search
   * @param lst the list which will store the results of the fuzzy search
   * @throws DBException
   */
  public static void fuzzyLocationSearch(TextField txt, ListView lst) throws DBException {
    ObservableList<DbNode> fuzzyList;
    String str = txt.getText();
    fuzzyList = FXCollections.observableList(FuzzySearchAlgorithm.suggestLocations(str));
    lst.setItems(fuzzyList);
  }

  /**
   * initiates the fuzzy search based on a doctor
   *
   * @param txt the textfield which initiates the fuzzy search
   * @param lst the list which will store the result of the fuzzy search
   * @throws DBException
   */
  public static void fuzzyDoctorSearch(TextField txt, ListView lst) throws DBException {
    ObservableList<Doctor> fuzzyList;
    String str = txt.getText();
    fuzzyList = FXCollections.observableList(FuzzySearchAlgorithm.suggestDoctors(str));
    lst.setItems(fuzzyList);
  }

  /** clears necessary variables when the map is reset */
  public void resetMap() {
    this.path = new Path(new LinkedList<>());
    collapseAllFloorButtons();
    mapBaseController.clearPath();
    setGoogleButtonDisable(true);
    enableAllFloorButtons();
    disableTextDirections();
  }

  /**
   * determines which icon was clicked and performs the specified function
   *
   * @param e the event which triggers the click of an icon
   * @throws IOException
   * @throws DBException
   */
  public void onIconClicked(MouseEvent e) throws IOException, DBException {
    Pane src = (Pane) e.getSource();
    pn_iconBar.getChildren().forEach(n -> n.setStyle("-fx-background-color: #263051;"));
    src.setStyle("-fx-background-color: #4A69C6;");
    FXMLLoader loader;
    if (src == pn_locationIcon) {
      resetMap();
      loader = new FXMLLoader(getClass().getResource("mapLocationSearch.fxml"));
      Pane pane = loader.load();
      locationSearchController = loader.getController();
      initLocationSearchButton();
      initResetLocationSearch();
      initRestroomSearchButton();
      initExitSearchButton();
      initInfoSearchButton();
      setDefaultKioskNode();
      pn_change.getChildren().add(pane);
    } else if (src == pn_doctorIcon) {
      resetMap();
      loader = new FXMLLoader(getClass().getResource("mapDoctorSearch.fxml"));
      Pane pane = loader.load();
      doctorSearchController = loader.getController();
      initDoctorSearchButton();
      initResetDoctorSearch();
      setDefaultKioskNode();
      pn_change.getChildren().add(pane);
    } else if (src == pn_qrIcon) {
      loader = new FXMLLoader(getClass().getResource("mapQR.fxml"));
      Pane pane = loader.load();
      mapQRController = loader.getController();
      mapQRController.setSingleton(this.singleton);
      setTextDescription();
      setDefaultKioskNode();
      pn_change.getChildren().add(pane);
    } else if (src == pn_serviceIcon) {
      this.mainApp.switchScene("/edu/wpi/N/views/services/newServicesPage.fxml", singleton);
      resetMap();
    } else if (src == pn_infoIcon) {
      resetMap();
      // TODO load info page here
      this.mainApp.switchScene("/edu/wpi/N/views/info/aboutPage.fxml", singleton);
    } else if (src == pn_adminIcon) {
      resetMap();
      this.mainApp.switchScene("/edu/wpi/N/views/admin/newLogin.fxml", singleton);
    }
  }

  /**
   * displays an error message if something does wrong
   *
   * @param str the message to be displayed if something goes wrong
   */
  public void displayErrorMessage(String str) {
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    errorAlert.setHeaderText("Something went wong...");
    errorAlert.setContentText(str);
    errorAlert.showAndWait();
  }

  /**
   * determines whether there is a kiosk on the floor and sets it as the default first entry
   *
   * @throws DBException
   */
  public void setDefaultKioskNode() throws DBException {
    boolean noFaulknerKiosk =
        !(currentBuilding.equals("Faulkner") && (currentFloor == 1 || currentFloor == 3));
    if (noFaulknerKiosk) {
      return;
    }

    if (path == null && path.size() > 0) {
      return;
    }
    if (locationSearchController != null) {
      locationSearchController.getTextFirstLocation().clear();
      locationSearchController.getTextSecondLocation().clear();
      locationSearchController.getFuzzyList().getItems().clear();
    }
    if (doctorSearchController != null) {
      doctorSearchController.getTextLocation().clear();
      doctorSearchController.getTxtDoctor().clear();
      doctorSearchController.getFuzzyList().getItems().clear();
    }

    DbNode kiosk = null;
    if (currentBuilding.equals("Faulkner") && currentFloor == 1) {
      kiosk = MapDB.getNode("NSERV00301");
    } else if (currentBuilding.equals("Faulkner") && currentFloor == 3) {
      kiosk = MapDB.getNode("NSERV00103");
    }

    try {
      if (locationSearchController != null) {
        locationSearchController
            .getTextFirstLocation()
            .setText(kiosk.toString() + ", " + kiosk.getBuilding());
        locationSearchController.getFuzzyList().getItems().add(kiosk);
        locationSearchController.getFuzzyList().getSelectionModel().select(0);
        locationSearchController.setKioskLocation(kiosk);
      }
      if (doctorSearchController != null) {
        doctorSearchController
            .getTextLocation()
            .setText(kiosk.toString() + ", " + kiosk.getBuilding());
        doctorSearchController.getFuzzyList().getItems().add(kiosk);
        doctorSearchController.getFuzzyList().getSelectionModel().select(0);
        doctorSearchController.setKioskLocation(kiosk);
      }
    } catch (NullPointerException e) {
      displayErrorMessage("The kiosk node does not exist in the database!");
    }
  }

  /** disables all floor buttons which do not partake in the path */
  public void disableNonPathFloors() {
    //    faulknerButtonList.getChildren().forEach(e -> e.setDisable(true));
    //    faulknerButtonList.getChildren().get(0).setDisable(false);
    //    mainButtonList.getChildren().forEach(e -> e.setDisable(true));
    //    mainButtonList.getChildren().get(0).setDisable(false);
    if (path == null) {
      return;
    }
    faulknerButtonList.getChildren().forEach(n -> n.setStyle("-fx-background-color: #263051;"));
    mainButtonList.getChildren().forEach(n -> n.setStyle("-fx-background-color: #263051;"));
    pathButtonList.clear();
    for (int i = 0; i < path.size(); i++) {
      DbNode node = path.get(i);
      if (!(node.getNodeType().equals("ELEV") || node.getNodeType().equals("STAI"))) {
        if (node.getBuilding().equals("Faulkner")) {
          JFXButton btn = (JFXButton) faulknerButtonList.getChildren().get(node.getFloor());
          //          btn.setDisable(false);
          btn.setStyle("-fx-background-color: #4A69C6");
          if (!pathButtonList.contains(btn)) {
            pathButtonList.add(btn);
          }
        } else {
          JFXButton btn = (JFXButton) mainButtonList.getChildren().get(node.getFloor());
          //          btn.setDisable(false);
          btn.setStyle("-fx-background-color: #4A69C6");
          if (!pathButtonList.contains(btn)) {
            pathButtonList.add(btn);
          }
        }
      }
    }
  }

  /** enables all floor buttons */
  public void enableAllFloorButtons() {
    faulknerButtonList.getChildren().forEach(e -> e.setDisable(false));
    mainButtonList.getChildren().forEach(e -> e.setDisable(false));
    pathButtonList.forEach(n -> n.setStyle("-fx-background-color: #263051;"));
    pathButtonList = new ArrayList<>();
  }

  /** switches the current map view to the google map */
  public void switchGoogleView() {
    pn_mapContainer.getChildren().setAll(pn_googleMapView);
    System.out.println("Hello");
  }

  /** switches the current map view to the hospital view */
  public void switchHospitalView() {
    pn_mapContainer.getChildren().setAll(pn_hospitalView);
  }

  /**
   * disables or enables the driving direction button
   *
   * @param b whether the google button should be disabled or not
   */
  public void setGoogleButtonDisable(boolean b) {
    this.btn_google.setDisable(b);
  }

  /**
   * displays the label text
   *
   * @param floor the current floor
   * @param building the current building
   */
  public void setFloorBuildingText(int floor, String building) {
    if (floor == 0) {
      lbl_building_floor.setText("Driving Directions");
    } else if (!building.equals("Faulkner")) {
      if (floor == 1) {
        lbl_building_floor.setText(building + ", " + "L2");
      } else if (floor == 2) {
        lbl_building_floor.setText(building + ", " + "L1");
      }
      if (floor == 3) {
        lbl_building_floor.setText(building + ", " + "G");
      }
      if (floor == 4) {
        lbl_building_floor.setText(building + ", " + "1");
      }
      if (floor == 5) {
        lbl_building_floor.setText(building + ", " + "2");
      }
      if (floor == 6) {
        lbl_building_floor.setText(building + ", " + "3");
      }
    } else {
      lbl_building_floor.setText(building + ", " + floor);
    }
  }

  /** collapses all the floor buttons back to the main button */
  public void collapseAllFloorButtons() {
    buildingButtonList.animateList(false);
    mainButtonList.animateList(false);
    faulknerButtonList.animateList(false);
  }

  /**
   * sets the textual description when pathfinding
   *
   * @throws DBException
   */
  public void setTextDescription() throws DBException {

    if (this.path.size() == 0 || path == null) {
      return;
    }
    Path pathFaulkner = new Path(new LinkedList<>());
    Path pathMain = new Path(new LinkedList<>());

    for (DbNode node : this.path.getPath()) {
      if (node.getBuilding().equals("Faulkner")) {
        pathFaulkner.getPath().add(node);
      } else {
        pathMain.getPath().add(node);
      }
    }

    boolean FaulknerToMain =
        path.get(0).getBuilding().equals("Faulkner")
            && !path.get(path.size() - 1).getBuilding().equals("Faulkner");
    boolean MainToFaulkner =
        !path.get(0).getBuilding().equals("Faulkner")
            && path.get(path.size() - 1).getBuilding().equals("Faulkner");

    ArrayList<Direction> faulknerDirections =
        pathFaulkner.size() > 0
            ? pathFaulkner.getDirections(singleton.savedAlgo.getMapData(), FaulknerToMain)
            : new ArrayList<>();
    ArrayList<Direction> mainDirections =
        pathMain.size() > 0
            ? pathMain.getDirections(singleton.savedAlgo.getMapData(), MainToFaulkner)
            : new ArrayList<>();

    ArrayList<Direction> googleDirections = new ArrayList<>();

    mapQRController.setTabs(
        path.getPath().getFirst().getBuilding(), path.getPath().getLast().getBuilding());
    mapQRController.setFaulknerText(faulknerDirections);
    mapQRController.setMainText(mainDirections);

    boolean isFirstFaulkner = this.path.get(0).getBuilding().equals("Faulkner");
    boolean isSecondFaulkner = this.path.get(path.size() - 1).getBuilding().equals("Faulkner");
    if (isFirstFaulkner ^ isSecondFaulkner) {
      if (isFirstFaulkner) {
        googleDirections = Directions.getGoogleDirections("Driving", false);
      } else {
        googleDirections = Directions.getGoogleDirections("Driving", true);
      }
    }
    mapQRController.setDriveText(googleDirections);
  }

  public void disableTextDirections() {
    pn_iconBar.getChildren().remove(pn_qrIcon);
  }

  public void enableTextDirections() {
    if (!(path.size() == 0 || path == null)) {
      pn_iconBar.getChildren().add(pn_qrIcon);
    }
  }
}
