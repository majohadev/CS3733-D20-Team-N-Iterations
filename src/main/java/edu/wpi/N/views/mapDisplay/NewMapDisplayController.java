package edu.wpi.N.views.mapDisplay;

import com.jfoenix.controls.JFXNodesList;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
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
  private App mainApp;
  private StateSingleton singleton;

  @FXML Pane pn_change;
  @FXML Pane pn_iconBar;
  @FXML Pane pn_locationIcon;
  @FXML Pane pn_doctorIcon;
  @FXML Pane pn_qrIcon;
  @FXML Pane pn_serviceIcon;
  @FXML Pane pn_infoIcon;
  @FXML Pane pn_adminIcon;
  @FXML MapBaseController mapBaseController;

  MapLocationSearchController locationSearchController;
  MapDoctorSearchController doctorSearchController;
  MapQRController mapQRController;

  Path path;
  int currentFloor;
  String currentBuilding;
  ArrayList<String> directions;
  JFXNodesList floorButtonList;

  public void initialize() {
    this.path = new Path(new LinkedList<>());
    this.currentFloor = 1;
    this.currentBuilding = "FAULKNER";
    this.directions = new ArrayList<>();
    this.floorButtonList = new JFXNodesList();

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
              } catch (DBException ex) {
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
              } catch (DBException ex) {
                ex.printStackTrace();
              }
            });
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public NewMapDisplayController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public void initPathfind(DbNode first, DbNode second, boolean isSelected) throws DBException {
    if (first == null || second == null) {
      displayErrorMessage("Please select a location");
      return;
    }
    this.path = singleton.savedAlgo.findPath(first, second, isSelected);
    mapBaseController.setFloor(first.getBuilding(), first.getFloor(), path);
    //    disableNonPathFloors();
    //    setTextDecription();
  }

  /**
   * applies fuzzy search to the user input for locations
   *
   * @param txt the textfield with the user input
   * @param lst the fuzzy search results
   * @throws DBException
   */
  public static void fuzzyLocationSearch(TextField txt, ListView lst) throws DBException {
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
  public static void fuzzyDoctorSearch(TextField txt, ListView lst) throws DBException {
    ObservableList<Doctor> fuzzyList;
    String str = txt.getText();
    fuzzyList = FXCollections.observableList(FuzzySearchAlgorithm.suggestDoctors(str));
    lst.setItems(fuzzyList);
  }

  /**
   * manages the panes displayed on the sidebar
   *
   * @param e the event which triggers switching between panes
   * @throws IOException
   */
  public void onIconClicked(MouseEvent e) throws IOException {
    Pane src = (Pane) e.getSource();
    pn_iconBar.getChildren().forEach(n -> n.setStyle("-fx-background-color: #263051"));
    src.setStyle("-fx-background-color: #4A69C6;");
    FXMLLoader loader;
    if (src == pn_locationIcon) {
      loader = new FXMLLoader(getClass().getResource("mapLocationSearch.fxml"));
      Pane pane = loader.load();
      locationSearchController = loader.getController();
      initLocationSearchButton();
      pn_change.getChildren().add(pane);
    } else if (src == pn_doctorIcon) {
      loader = new FXMLLoader(getClass().getResource("mapDoctorSearch.fxml"));
      Pane pane = loader.load();
      doctorSearchController = loader.getController();
      initDoctorSearchButton();
      pn_change.getChildren().add(pane);
    } else if (src == pn_qrIcon) {
      loader = new FXMLLoader(getClass().getResource("mapQR.fxml"));
      Pane pane = loader.load();
      mapQRController = loader.getController();
      pn_change.getChildren().add(pane);
    } else if (src == pn_serviceIcon) {
      // TODO load service page here
    } else if (src == pn_infoIcon) {
      // TODO load info page here
    } else if (src == pn_adminIcon) {
      // TODO load admin page here
    }
  }

  public void displayErrorMessage(String str) {
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    errorAlert.setHeaderText("Invalid input");
    errorAlert.setContentText(str);
    errorAlert.showAndWait();
  }
}
