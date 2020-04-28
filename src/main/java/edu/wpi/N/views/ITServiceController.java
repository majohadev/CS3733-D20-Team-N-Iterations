package edu.wpi.N.views;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;

public class ITServiceController implements Controller {

  private App mainApp;

  // Add FXML Tags Here
  @FXML JFXComboBox<String> cmbo_text;
  @FXML JFXTextArea txtf_device;
  @FXML JFXTextArea txtf_problem;
  @FXML JFXTextArea txtf_ITnotes;

  private ObservableList<String> fuzzySearchTextList =
      // List that fills TextViews
      FXCollections.observableArrayList();
  LinkedList<DbNode> fuzzySearchNodeList = new LinkedList<>();
  DbNode currentNode = null;

  private String countVal = "";

  public ITServiceController() throws DBException {}

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @Override
  public void setSingleton(StateSingleton singleton) {}

  public void initialize() throws DBException {
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

  // Create IT Request
  @FXML
  public void createNewITRequest() throws DBException {

    String nodeID = "";
    int nodeIndex = 0;

    String userLocationName = cmbo_text.getEditor().getText().toLowerCase().trim();
    LinkedList<DbNode> checkNodes = MapDB.searchVisNode(-1, null, null, userLocationName);

    // Find the exact match and get the nodeID when selected
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

    String device = txtf_device.getText();
    if (device == null) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please enter the device you need help with!");
      errorAlert.show();
      return;
    }

    String problem = txtf_problem.getText();
    if (problem == null) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please enter the problem with your device!");
      errorAlert.show();
      return;
    }

    String notes = txtf_ITnotes.getText();

    int ITReq = ServiceDB.addITReq(notes, nodeID, device, problem);
    // App.adminDataStorage.addToList(ITReq);

    txtf_device.clear();
    txtf_problem.clear();
    txtf_ITnotes.clear();
    cmbo_text.getItems().clear();

    Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confAlert.setContentText("Request Received");
    confAlert.show();
  }
}
