package edu.wpi.N.views.services;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTimePicker;
import edu.wpi.N.AppClass;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class InternalTransportController implements Controller {

  private StateSingleton singleton;

  private AppClass mainApp;

  // Add FXML Tags Here
  @FXML JFXComboBox<String> cmbo_dest;
  @FXML JFXComboBox<String> cmbo_pickup;
  @FXML JFXTextArea txtf_transNotes;
  @FXML JFXComboBox<String> cmbo_type;
  @FXML JFXTimePicker tp_transporttime;
  @FXML AnchorPane internalTransportPage;

  private ObservableList<String> fuzzySearchTextList =
      // List that fills TextViews
      FXCollections.observableArrayList();
  LinkedList<DbNode> fuzzySearchNodeList = new LinkedList<>();

  private ObservableList<String> fuzzySearchTextList1 =
      // List that fills TextViews
      FXCollections.observableArrayList();
  LinkedList<DbNode> fuzzySearchNodeList1 = new LinkedList<>();

  private String countVal = "";

  // Inject singleton
  public InternalTransportController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public InternalTransportController() throws DBException {}

  public void setMainApp(AppClass mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException {

    cmbo_dest.getEditor().setOnKeyTyped(this::locationTextChanged);
    cmbo_pickup.getEditor().setOnKeyTyped(this::locationTextChanged1);
    LinkedList<String> transportTypes = new LinkedList<String>();
    transportTypes.add("Wheelchair");
    transportTypes.add("Stretcher");

    ObservableList<String> transportTypeList = FXCollections.observableList(transportTypes);
    cmbo_type.setItems(transportTypeList);
  }

  @FXML
  public void autofillLocation(String currentText) {
    System.out.println(currentText);
    if (currentText.length() > 2) {
      try {
        fuzzySearchNodeList = FuzzySearchAlgorithm.suggestLocations(currentText);
      } catch (DBException e) {
        e.printStackTrace();
      }
      LinkedList<String> fuzzySearchStringList = new LinkedList<>();
      if (fuzzySearchNodeList != null) {

        for (DbNode node : fuzzySearchNodeList) {
          fuzzySearchStringList.add(node.getLongName());
        }
        fuzzySearchTextList = FXCollections.observableList(fuzzySearchStringList);
      }
      System.out.println(fuzzySearchTextList);
    }
    if (fuzzySearchTextList == null) fuzzySearchTextList.add("  ");
  }

  @FXML
  public void autofillLocation1(String currentText) {
    // System.out.println(currentText);
    if (currentText.length() > 2) {
      try {
        fuzzySearchNodeList1 = FuzzySearchAlgorithm.suggestLocations(currentText);
      } catch (DBException e) {
        e.printStackTrace();
      }
      LinkedList<String> fuzzySearchStringList = new LinkedList<>();
      if (fuzzySearchNodeList1 != null) {

        for (DbNode node : fuzzySearchNodeList1) {
          fuzzySearchStringList.add(node.getLongName());
        }
        fuzzySearchTextList1 = FXCollections.observableList(fuzzySearchStringList);
      }
    }
    if (fuzzySearchTextList1 == null) fuzzySearchTextList1.add("  ");
  }

  // Handler for location combo box
  @FXML
  public void locationTextChanged(KeyEvent event) {
    String curr = cmbo_dest.getEditor().getText();
    autofillLocation(curr);
    cmbo_dest.getItems().setAll(fuzzySearchTextList);
    cmbo_dest.show();
  }

  @FXML
  public void locationTextChanged1(KeyEvent event) {
    String curr = cmbo_pickup.getEditor().getText();
    autofillLocation1(curr);
    cmbo_pickup.getItems().setAll(fuzzySearchTextList1);
    cmbo_pickup.show();
  }

  // Create Transport Request
  @FXML
  public void createNewTransportationRequest() throws DBException, IOException {

    String typeSelection = cmbo_type.getSelectionModel().getSelectedItem();
    String destinationLocation = null;
    String pickupNodeID = null;
    int nodeIndex = 0;

    String notes = txtf_transNotes.getText();

    String userLocationName = cmbo_dest.getEditor().getText().toLowerCase().trim();
    LinkedList<DbNode> checkNodes = MapDB.searchVisNode(-1, null, null, userLocationName);

    String userPickupName = cmbo_pickup.getEditor().getText().toLowerCase().trim();
    LinkedList<DbNode> checkNodesPickup = MapDB.searchVisNode(-1, null, null, userPickupName);

    // Find the exact match and get the nodeID
    for (DbNode node : checkNodes) {
      if (node.getLongName().toLowerCase().equals(userLocationName)) {
        destinationLocation = node.getNodeID();
        break;
      }
    }
    // Check to see if such node was found
    if (destinationLocation == null) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a destination location for your service request!");
      errorAlert.show();
      return;
    }

    for (DbNode node : checkNodesPickup) {
      if (node.getLongName().toLowerCase().equals(userPickupName)) {
        pickupNodeID = node.getNodeID();
        break;
      }
    }

    if (pickupNodeID == null) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a pickup location for your service request!");
      errorAlert.show();
      return;
    }

    if (typeSelection == null) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a transport type for your request!");
      errorAlert.show();
      return;
    }

    String time = tp_transporttime.getValue().toString();

    int transportRequest =
        ServiceDB.addInternalTransportationReq(
            notes, pickupNodeID, typeSelection, time, destinationLocation);

    txtf_transNotes.clear();
    cmbo_dest.getItems().clear();
    cmbo_pickup.getItems().clear();
    cmbo_type.getItems().clear();

    Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confAlert.setContentText("Request Recieved");
    confAlert.show();
    internalTransportPage.setVisible(false);
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("mainServicePage.fxml"));
    internalTransportPage.getChildren().setAll(currentPane);
    internalTransportPage.setVisible(true);
  }
}
