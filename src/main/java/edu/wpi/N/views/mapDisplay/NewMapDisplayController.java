package edu.wpi.N.views.mapDisplay;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXNodesList;
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
import javafx.scene.layout.Pane;

public class NewMapDisplayController implements Controller {
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
  @FXML Label lbl_building_floor;

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

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public NewMapDisplayController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public void initialize() throws DBException, IOException {
    this.path = new Path(new LinkedList<>());
    this.currentFloor = 1;
    this.currentBuilding = "Faulkner";
    this.directions = new ArrayList<>();

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
  }

  public void initFunctionPane() throws IOException, DBException {
    FXMLLoader loader;
    loader = new FXMLLoader(getClass().getResource("mapLocationSearch.fxml"));
    Pane pane = loader.load();
    locationSearchController = loader.getController();
    initLocationSearchButton();
    initResetLocationSearch();
    initRestroomSearchButton();
    pn_change.getChildren().add(pane);
  }

  public void styleBuildingButtons(JFXButton btn) {
    btn.getStylesheets()
        .add(getClass().getResource("/edu/wpi/N/css/MapDisplayFloors.css").toExternalForm());
    btn.getStyleClass().add("header-button");
  }

  public void styleFloorButtons(JFXButton btn) {
    btn.getStylesheets()
        .add(getClass().getResource("/edu/wpi/N/css/MapDisplayFloors.css").toExternalForm());
    btn.getStyleClass().add("choice-button");
  }

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
            btn_faulkner1,
            btn_faulkner2,
            btn_faulkner3,
            btn_faulkner4,
            btn_faulkner5);

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
        .addAll(btn_main, btn_main1, btn_main2, btn_main3, btn_main4, btn_main5, btn_main6);
    buildingButtonList.addAnimatedNode(btn_buildings);
    buildingButtonList.addAnimatedNode(faulknerButtonList);
    buildingButtonList.addAnimatedNode(mainButtonList);
    buildingButtonList.addAnimatedNode(btn_google);

    buildingButtonList.setSpacing(100);
    buildingButtonList.setRotate(90);
    faulknerButtonList.setSpacing(15);
    mainButtonList.setSpacing(15);

    pn_floors.getChildren().add(buildingButtonList);
  }

  public void onFloorButtonClicked(JFXButton btn) throws DBException {
    String txt = btn.getText();
    btn.setOnMouseClicked(
        e -> {
          try {
            handleFloorButtonClicked(txt);
          } catch (DBException ex) {
            ex.printStackTrace();
          }
        });
  }

  public void handleFloorButtonClicked(String txt) throws DBException {
    collapseAllFloorButtons();
    if (txt.equals("F1")) {
      changeFloor(1, "Faulkner");
      switchHospitalView();
      this.currentFloor = 1;
      this.currentBuilding = "Faulkner";
      setDirectionsTab();
      setDefaultKioskNode();
    } else if (txt.equals("F2")) {
      changeFloor(2, "Faulkner");
      switchHospitalView();
      this.currentFloor = 2;
      this.currentBuilding = "Faulkner";
      setDirectionsTab();
      setDefaultKioskNode();
    } else if (txt.equals("F3")) {
      changeFloor(3, "Faulkner");
      switchHospitalView();
      this.currentFloor = 3;
      this.currentBuilding = "Faulkner";
      setDirectionsTab();
      setDefaultKioskNode();
    } else if (txt.equals("F4")) {
      changeFloor(4, "Faulkner");
      switchHospitalView();
      this.currentFloor = 4;
      this.currentBuilding = "Faulkner";
      setDirectionsTab();
      setDefaultKioskNode();
    } else if (txt.equals("F5")) {
      changeFloor(5, "Faulkner");
      switchHospitalView();
      this.currentFloor = 5;
      this.currentBuilding = "Faulkner";
      setDirectionsTab();
      setDefaultKioskNode();
    } else if (txt.equals("L2")) {
      changeFloor(1, "Main");
      switchHospitalView();
      this.currentFloor = 1;
      this.currentBuilding = "Main";
      setDirectionsTab();
      setDefaultKioskNode();
    } else if (txt.equals("L1")) {
      switchHospitalView();
      changeFloor(2, "Main");
      this.currentFloor = 2;
      this.currentBuilding = "Main";
      setDirectionsTab();
      setDefaultKioskNode();
    } else if (txt.equals("G")) {
      switchHospitalView();
      changeFloor(3, "Main");
      this.currentFloor = 3;
      this.currentBuilding = "Main";
      setDirectionsTab();
      setDefaultKioskNode();
    } else if (txt.equals("1")) {
      switchHospitalView();
      changeFloor(4, "Main");
      this.currentFloor = 4;
      this.currentBuilding = "Main";
      setDirectionsTab();
      setDefaultKioskNode();
    } else if (txt.equals("2")) {
      switchHospitalView();
      changeFloor(5, "Main");
      this.currentFloor = 5;
      this.currentBuilding = "Main";
      setDirectionsTab();
      setDefaultKioskNode();
    } else if (txt.equals("3")) {
      switchHospitalView();
      changeFloor(6, "Main");
      this.currentFloor = 6;
      this.currentBuilding = "Main";
      setDirectionsTab();
      setDefaultKioskNode();
    } else if (txt.equals("Street View")) {
      switchGoogleView();
      this.currentFloor = -1;
      this.currentBuilding = "Street View";
      setDirectionsTab();
      setFloorBuildingText(-1, "");
      // TODO DEFAULT GOOGLE MAP VIEW
    }
  }

  public void changeFloor(int newFloor, String newBuilding) throws DBException {
    mapBaseController.clearPath();
    this.currentFloor = newFloor;
    this.currentBuilding = newBuilding;
    setFloorBuildingText(this.currentFloor, this.currentBuilding);
    setDefaultKioskNode();
    mapBaseController.setFloor(this.currentBuilding, this.currentFloor, this.path);
  }

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
              } catch (DBException | IOException ex) {
                ex.printStackTrace();
              }
            });
  }

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
              } catch (DBException | IOException ex) {
                ex.printStackTrace();
              }
            });
  }

  public void initResetLocationSearch() throws DBException {
    locationSearchController
        .getResetButton()
        .setOnMouseClicked(
            e -> {
              this.path.clear();
              setGoogleButtonDisable(true);
              locationSearchController.getTextFirstLocation().clear();
              locationSearchController.getTextSecondLocation().clear();
              locationSearchController.getFuzzyList().getItems().clear();
              locationSearchController.getTgHandicap().setSelected(false);
              mapBaseController.clearPath();
              enableAllFloorButtons();
              try {
                setDefaultKioskNode();
              } catch (DBException ex) {
                ex.printStackTrace();
              }
            });
  }

  public void initResetDoctorSearch() throws DBException {
    doctorSearchController
        .getResetButton()
        .setOnMouseClicked(
            e -> {
              this.path.clear();
              enableAllFloorButtons();
              setGoogleButtonDisable(true);
              doctorSearchController.getTextLocation().clear();
              doctorSearchController.getTxtDoctor().clear();
              doctorSearchController.getFuzzyList().getItems().clear();
              doctorSearchController.getTgHandicap().setSelected(false);
              mapBaseController.clearPath();
              enableAllFloorButtons();
              try {
                setDefaultKioskNode();
              } catch (DBException ex) {
                ex.printStackTrace();
              }
            });
  }

  public void initRestroomSearchButton() throws DBException {
    locationSearchController
        .getBtnRestRoom()
        .setOnMouseClicked(
            e -> {
              DbNode first = locationSearchController.getDBNodes()[0];
              try {
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
              //        setTextDescriptions();
            });
  }

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
    disableNonPathFloors();
    displayGoogleMaps(first, second);
    //    setTextDecription();
  }

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

  public static void fuzzyLocationSearch(TextField txt, ListView lst) throws DBException {
    ObservableList<DbNode> fuzzyList;
    String str = txt.getText();
    fuzzyList = FXCollections.observableList(FuzzySearchAlgorithm.suggestLocations(str));
    lst.setItems(fuzzyList);
  }

  public static void fuzzyDoctorSearch(TextField txt, ListView lst) throws DBException {
    ObservableList<Doctor> fuzzyList;
    String str = txt.getText();
    fuzzyList = FXCollections.observableList(FuzzySearchAlgorithm.suggestDoctors(str));
    lst.setItems(fuzzyList);
  }

  public void resetMap() {
    collapseAllFloorButtons();
    mapBaseController.clearPath();
    setGoogleButtonDisable(true);
    enableAllFloorButtons();
  }

  public void onIconClicked(MouseEvent e) throws IOException, DBException {
    Pane src = (Pane) e.getSource();
    pn_iconBar.getChildren().forEach(n -> n.setStyle("-fx-background-color: #263051"));
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
      setDirectionsTab();
      setTextDescription();
      setDefaultKioskNode();
      pn_change.getChildren().add(pane);
    } else if (src == pn_serviceIcon) {
      resetMap();
      // TODO load service page here
    } else if (src == pn_infoIcon) {
      resetMap();
      // TODO load info page here
      this.mainApp.switchScene("/edu/wpi/N/views/aboutPage.fxml", singleton);
    } else if (src == pn_adminIcon) {
      resetMap();
      this.mainApp.switchScene("/edu/wpi/N/views/admin/newLogin.fxml", singleton);
    }
  }

  public void setDirectionsTab() {
    if (mapQRController == null) {
      return;
    }
    if (pn_mapContainer.getChildren().get(0) == pn_googleMapView) {
      mapQRController.setDirectionTab("Street View");
      return;
    }
    if (this.currentBuilding.equals("Faulkner")) {
      mapQRController.setDirectionTab("Faulkner");
      return;
    }
    mapQRController.setDirectionTab("Main");
  }

  public void displayErrorMessage(String str) {
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    errorAlert.setHeaderText("Something went wong...");
    errorAlert.setContentText(str);
    errorAlert.showAndWait();
  }

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

  public void disableNonPathFloors() {
    faulknerButtonList.getChildren().forEach(e -> e.setDisable(true));
    faulknerButtonList.getChildren().get(0).setDisable(false);
    mainButtonList.getChildren().forEach(e -> e.setDisable(true));
    mainButtonList.getChildren().get(0).setDisable(false);
    for (int i = 0; i < path.size(); i++) {
      DbNode node = path.get(i);
      if (!(node.getNodeType().equals("ELEV") || node.getNodeType().equals("STAI"))) {
        if (node.getBuilding().equals("Faulkner")) {
          faulknerButtonList.getChildren().get(node.getFloor()).setDisable(false);
        } else {
          mainButtonList.getChildren().get(node.getFloor()).setDisable(false);
        }
      }
    }
  }

  public void enableAllFloorButtons() {
    faulknerButtonList.getChildren().forEach(e -> e.setDisable(false));
    mainButtonList.getChildren().forEach(e -> e.setDisable(false));
  }

  public void switchGoogleView() {
    pn_mapContainer.getChildren().setAll(pn_googleMapView);
    System.out.println("Hello");
  }

  public void switchHospitalView() {
    pn_mapContainer.getChildren().setAll(pn_hospitalView);
  }

  public void setGoogleButtonDisable(boolean b) {
    this.btn_google.setDisable(b);
  }

  public void setFloorBuildingText(int floor, String building) {
    if (floor == -1) {
      lbl_building_floor.setText("Driving Directions");
    }
    if (!building.equals("Faulkner")) {
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

  public void collapseAllFloorButtons() {
    buildingButtonList.animateList(false);
    mainButtonList.animateList(false);
    faulknerButtonList.animateList(false);
  }

  public void setTextDescription() throws DBException {
    if (this.path.size() == 0 || path == null) {
      return;
    }

    String faulknerText = "";
    String mainText = "";
    String driveText = "";

    Path pathFaulkner = new Path(new LinkedList<>());
    Path pathMain = new Path(new LinkedList<>());

    for (DbNode node : this.path.getPath()) {
      if (node.getBuilding().equals("Faulkner")) {
        pathFaulkner.getPath().add(node);
      } else {
        pathMain.getPath().add(node);
      }
    }

    ArrayList<String> faulknerDirections;
    ArrayList<String> mainDirections;
    if (pathFaulkner.size() > 0) {
      faulknerDirections = pathFaulkner.getDirections();
      for (String s : faulknerDirections) {
        faulknerText += s;
        faulknerText += "\n";
      }
    }

    if (pathMain.size() > 0) {
      mainDirections = pathMain.getDirections();
      for (String s : mainDirections) {
        mainText += s;
        mainText += "\n";
      }
    }

    if (!pathFaulkner.equals("")) {
      mapQRController.getTextFaulkner().setText(faulknerText);
    }
    if (!pathMain.equals("")) {
      mapQRController.getTextMain().setText(mainText);
    }
  }
}
