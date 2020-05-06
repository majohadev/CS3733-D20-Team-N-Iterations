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
  @FXML Pane chatbotView;

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
    JFXButton btn_google = new JFXButton("Google");
    styleBuildingButtons(btn_google);

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
    buildingButtonList.setRotate(-90);
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
    if (txt.equals("F1")) {
      changeFloor(1, "Faulkner");
      switchHospitalView();
      setDefaultKioskNode();
    } else if (txt.equals("F2")) {
      changeFloor(2, "Faulkner");
      switchHospitalView();
      setDefaultKioskNode();
    } else if (txt.equals("F3")) {
      changeFloor(3, "Faulkner");
      switchHospitalView();
      setDefaultKioskNode();
    } else if (txt.equals("F4")) {
      changeFloor(4, "Faulkner");
      switchHospitalView();
      setDefaultKioskNode();
    } else if (txt.equals("F5")) {
      changeFloor(5, "Faulkner");
      switchHospitalView();
      setDefaultKioskNode();
    } else if (txt.equals("L2")) {
      changeFloor(1, "Main");
      switchHospitalView();
      setDefaultKioskNode();
    } else if (txt.equals("L1")) {
      changeFloor(2, "Main");
      switchHospitalView();
      setDefaultKioskNode();
    } else if (txt.equals("G")) {
      changeFloor(3, "Main");
      switchHospitalView();
      setDefaultKioskNode();
    } else if (txt.equals("1")) {
      changeFloor(4, "Main");
      switchHospitalView();
      setDefaultKioskNode();
    } else if (txt.equals("2")) {
      changeFloor(5, "Main");
      switchHospitalView();
      setDefaultKioskNode();
    } else if (txt.equals("3")) {
      changeFloor(6, "Main");
      switchHospitalView();
      setDefaultKioskNode();
    } else if (txt.equals("Google")) {
      // TODO DEFAULT GOOGLE MAP VIEW
      switchGoogleView();
    }
  }

  public void changeFloor(int newFloor, String newBuilding) throws DBException {
    mapBaseController.clearPath();
    this.currentFloor = newFloor;
    this.currentBuilding = newBuilding;
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
              //      enableAllFloorButtons;
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

  public void initPathfind(DbNode first, DbNode second, boolean isSelected)
      throws DBException, IOException {
    if (first == null || second == null) {
      displayErrorMessage("Please select a location");
      return;
    }
    this.path = singleton.savedAlgo.findPath(first, second, isSelected);
    mapBaseController.setFloor(first.getBuilding(), first.getFloor(), path);
    disableNonPathFloors();
    displayGoogleMaps(first, second);
    //    setTextDecription();
  }

  public void displayGoogleMaps(DbNode first, DbNode second) throws IOException {
    boolean isFirstFaulkner = first.getBuilding().equals("Faulkner");
    boolean isSecondFaulkner = second.getBuilding().equals("Faulkner");
    if (isFirstFaulkner ^ isSecondFaulkner) {
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

  public void onIconClicked(MouseEvent e) throws IOException, DBException {
    mapBaseController.clearPath();
    enableAllFloorButtons();
    Pane src = (Pane) e.getSource();
    pn_iconBar.getChildren().forEach(n -> n.setStyle("-fx-background-color: #263051"));
    src.setStyle("-fx-background-color: #4A69C6;");
    FXMLLoader loader;
    if (src == pn_locationIcon) {
      loader = new FXMLLoader(getClass().getResource("mapLocationSearch.fxml"));
      Pane pane = loader.load();
      locationSearchController = loader.getController();
      initLocationSearchButton();
      initResetLocationSearch();
      setDefaultKioskNode();
      pn_change.getChildren().add(pane);
    } else if (src == pn_doctorIcon) {
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
      setDefaultKioskNode();
      pn_change.getChildren().add(pane);
    } else if (src == pn_serviceIcon) {
      // TODO load service page here
    } else if (src == pn_infoIcon) {
      // TODO load info page here
    } else if (src == pn_adminIcon) {
      this.mainApp.switchScene("/edu/wpi/N/views/admin/newLogin.fxml", singleton);
    }
  }

  public void displayErrorMessage(String str) {
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    errorAlert.setHeaderText("Something went wong...");
    errorAlert.setContentText(str);
    errorAlert.showAndWait();
  }

  public void setDefaultKioskNode() throws DBException {
    if (path.size() > 0) {
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

    boolean noFaulknerKiosk =
        !(currentBuilding.equals("Faulkner") && (currentFloor == 1 || currentFloor == 3));
    if (noFaulknerKiosk) {
      return;
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
    for (int i = 0; i < path.size() - 1; i++) {
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
  }

  public void switchHospitalView() {
    pn_mapContainer.getChildren().setAll(pn_hospitalView);
  }
}
