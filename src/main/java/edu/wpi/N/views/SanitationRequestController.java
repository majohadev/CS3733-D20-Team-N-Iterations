package edu.wpi.N.views;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;

public class SanitationRequestController implements Controller {

  private App mainApp;
  private StateSingleton singleton;

  @Override
  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  // Add FXML Tags Here
  @FXML JFXComboBox<String> cmbo_text;
  @FXML JFXComboBox<String> cmbo_selectSpillSize;
  @FXML JFXComboBox<String> cmbo_selectDangerLevel;
  @FXML JFXTextField txtf_spillType;
  @FXML JFXTextArea txtf_sanitationNotes;

  private ObservableList<String> fuzzySearchTextList =
      // List that fills TextViews
      FXCollections.observableArrayList();
  LinkedList<DbNode> fuzzySearchNodeList = new LinkedList<>();
  DbNode currentNode = null;

  private String countVal = "";

  public SanitationRequestController() throws DBException {}

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException {
    initializeSpillSizes();
    initizlizeDangerList();
  }

  public void initizlizeDangerList() {
    LinkedList<String> dangerLevels = new LinkedList<>();
    dangerLevels.add("Low");
    dangerLevels.add("Medium");
    dangerLevels.add("High");
    dangerLevels.add("Unknown");
    ObservableList<String> dangerList = FXCollections.observableList(dangerLevels);
    cmbo_selectDangerLevel.setItems(dangerList);
  }

  public void initializeSpillSizes() {
    cmbo_text.getEditor().setOnKeyTyped(this::locationTextChanged);
    LinkedList<String> spillSizes = new LinkedList<>();
    spillSizes.add("Small");
    spillSizes.add("Medium");
    spillSizes.add("Large");
    spillSizes.add("Unknown");
    ObservableList<String> sizeList = FXCollections.observableList(spillSizes);
    cmbo_selectSpillSize.setItems(sizeList);
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

  // Create Sanitation Request
  @FXML
  public void createNewSanitation() throws DBException {

    String sizeSelection = cmbo_selectSpillSize.getSelectionModel().getSelectedItem();
    String dangerSelection = cmbo_selectDangerLevel.getSelectionModel().getSelectedItem();
    String spillType = txtf_spillType.getText();
    String nodeID;
    int nodeIndex = 0;

    try {
      String curr = cmbo_text.getEditor().getText();
      for (String name : fuzzySearchTextList) {
        if (name.equals(curr)) {
          nodeIndex++;
          break;
        }
      }
      nodeID = fuzzySearchNodeList.get(nodeIndex).getNodeID();
      System.out.println(nodeID);
    } catch (IndexOutOfBoundsException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a location for your service request!");
      errorAlert.show();
      return;
    }

    String notes = txtf_sanitationNotes.getText();
    if (sizeSelection == null) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a size for your sanitation request!");
      errorAlert.show();
      return;
    }
    if (dangerSelection == null) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a danger level for your sanitation request!");
      errorAlert.show();
      return;
    }

    int sanitationReq =
        ServiceDB.addSanitationReq(notes, nodeID, spillType, sizeSelection, dangerSelection);
    // App.adminDataStorage.addToList(sanitationReq);

    txtf_sanitationNotes.clear();
    txtf_spillType.clear();
    cmbo_selectSpillSize.getItems().clear();
    cmbo_selectDangerLevel.getItems().clear();
    cmbo_text.getItems().clear();

    Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confAlert.setContentText("Request Recieved");
    confAlert.show();
  }
}
