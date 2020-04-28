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

public class FlowerRequestController implements Controller {

  private StateSingleton singleton;

  @Override
  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  private App mainApp;

  // Add FXML Tags Here
  @FXML JFXTextField txt_visitorName;
  @FXML JFXTextField txt_patientName;
  @FXML JFXTextField txt_creditNum;
  @FXML JFXComboBox cb_flowerType;
  @FXML JFXTextArea txt_notes;
  @FXML JFXTextField txt_quantity;

  ObservableList<String> flowers;

  public FlowerRequestController() throws DBException {}

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException {
    flowers = FXCollections.observableArrayList("ROSE", "TULIPS");
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

  // Create Translator Request
  @FXML
  public void createNewTranslator() throws DBException {

    String langSelection = cmbo_selectLang.getSelectionModel().getSelectedItem();
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

    String notes = txtf_langNotes.getText();
    if (langSelection == null) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a language for your translation request!");
      errorAlert.show();
      return;
    }
    int transReq = ServiceDB.addTransReq(notes, nodeID, langSelection);
    // App.adminDataStorage.addToList(transReq);

    txtf_langNotes.clear();
    cmbo_selectLang.getItems().clear();
    cmbo_text.getItems().clear();

    Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confAlert.setContentText("Request Recieved");
    confAlert.show();
  }
}
