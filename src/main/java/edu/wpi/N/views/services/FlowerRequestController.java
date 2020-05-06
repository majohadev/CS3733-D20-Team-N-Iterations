package edu.wpi.N.views.services;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Flower;
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

public class FlowerRequestController implements Controller {

  private StateSingleton singleton;

  private App mainApp;

  // Add FXML Tags Here
  @FXML JFXTextField txt_visitorName;
  @FXML JFXTextField txt_patientName;
  @FXML JFXTextField txt_creditNum;
  @FXML JFXComboBox<Flower> cb_flowerType;
  @FXML JFXTextArea txt_notes;
  @FXML JFXTextField txt_quantity;
  @FXML JFXComboBox<String> cmbo_text;
  @FXML AnchorPane flowerDeliveryRequest;

  private ObservableList<String> fuzzySearchTextList =
      // List that fills TextViews
      FXCollections.observableArrayList();
  LinkedList<DbNode> fuzzySearchNodeList = new LinkedList<>();
  DbNode currentNode = null;

  ObservableList<Flower> flowers;

  // Inject singleton
  public FlowerRequestController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public FlowerRequestController() throws DBException {}

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException {
    LinkedList<Flower> listFlower = ServiceDB.getFlowers();
    flowers = FXCollections.observableList(listFlower);
    cb_flowerType.setItems(flowers);

    cmbo_text.getEditor().setOnKeyTyped(this::locationTextChanged);
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

  // Handler for location combo box
  @FXML
  public void locationTextChanged(KeyEvent event) {
    String curr = cmbo_text.getEditor().getText();
    autofillLocation(curr);
    cmbo_text.getItems().setAll(fuzzySearchTextList);
    cmbo_text.show();
  }

  // Create Flower Request
  @FXML
  public void createNewFlowerReq() throws DBException, IOException {

    if (cb_flowerType.getSelectionModel().getSelectedIndex() > 0) {

      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Select a Flower");
      errorAlert.show();
      return;
    }

    if (txt_visitorName.getText().equals("")
        || txt_creditNum.getText().equals("")
        || txt_patientName.getText().equals("")
        || txt_quantity.getText().equals("")) {

      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("One or more arguments is NULL!");
      errorAlert.show();
      return;
    }

    String visitorName = txt_visitorName.getText();
    String patientName = txt_patientName.getText();
    String creditNum = txt_creditNum.getText();
    String quantity = txt_quantity.getText();
    String flowerSelection = cb_flowerType.getSelectionModel().getSelectedItem().getFlowerName();
    String nodeID = null;
    int nodeIndex = 0;

    String userLocationName = cmbo_text.getEditor().getText().toLowerCase().trim();
    LinkedList<DbNode> checkNodes = MapDB.searchVisNode(-1, null, null, userLocationName);

    // Find the exact match and get the nodeID
    for (DbNode node : checkNodes) {
      if (node.getLongName().toLowerCase().equals(userLocationName)) {
        nodeID = node.getNodeID();
        break;
      }
    }
    // Check to see if such node was found
    if (nodeID == null) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(
          "Please select a location for your service request from suggestions menu!");
      errorAlert.show();
      return;
    }

    String notes = txt_notes.getText();
    if (flowerSelection == null) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a language for your translation request!");
      errorAlert.show();
      return;
    }

    if (txt_creditNum.getText().length() != 16) {

      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Invalid Credit Card Number!");
      errorAlert.show();

      return;
    }

    LinkedList<String> flowers = new LinkedList<>();
    for (int i = 0; i < Integer.parseInt(quantity); i++) {
      flowers.add(flowerSelection);
    }
    int flowerReq =
        ServiceDB.addFlowerReq(notes, nodeID, patientName, visitorName, creditNum, flowers);
    // App.adminDataStorage.addToList(transReq);

    txt_notes.clear();
    cb_flowerType.getItems().clear();
    cmbo_text.getItems().clear();
    txt_creditNum.clear();
    txt_patientName.clear();
    txt_quantity.clear();
    txt_visitorName.clear();

    Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confAlert.setContentText("Request Recieved");
    confAlert.show();

    flowerDeliveryRequest.setVisible(false);
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("mainServicePage.fxml"));
    flowerDeliveryRequest.getChildren().setAll(currentPane);
    flowerDeliveryRequest.setVisible(true);
  }

  public void displayErrorMessage(String str) {
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    errorAlert.setHeaderText(str);
    errorAlert.setContentText(str);
    errorAlert.showAndWait();
  }
}
