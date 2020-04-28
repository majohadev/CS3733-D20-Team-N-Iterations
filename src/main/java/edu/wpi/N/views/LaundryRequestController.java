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

public class LaundryRequestController implements Controller {

  private StateSingleton singleton;

  @Override
  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  private App mainApp;

  // Add FXML Tags Here
  @FXML JFXComboBox<String> cmbo_text;
  @FXML JFXTextArea txtf_supportNotes;

  private ObservableList<String> fuzzySearchTextList =
      // List that fills TextViews
      FXCollections.observableArrayList();
  LinkedList<DbNode> fuzzySearchNodeList = new LinkedList<>();
  DbNode currentNode = null;

  private String countVal = "";

  public LaundryRequestController() throws DBException {}

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException {

    cmbo_text.getEditor().setOnKeyTyped(this::locationTextChanged);
  }

  @FXML
  public void autofillLocation(String currentText) {
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

  // Create Emotional Request
  @FXML
  public void createNewLaundryRequest() throws DBException {

    String nodeID = null;

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

    String notes = txtf_supportNotes.getText();

    int laundReq = ServiceDB.addLaundReq(notes, nodeID);

    txtf_supportNotes.clear();
    cmbo_text.getItems().clear();

    Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confAlert.setContentText("Request Recieved");
    confAlert.show();
    return;
  }
}
