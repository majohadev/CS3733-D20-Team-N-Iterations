package edu.wpi.N.views;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import java.util.ArrayList;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;

public class SecurityRequestController implements Controller {

  private App mainApp;

  // Add FXML Tags Here
  @FXML JFXComboBox<String> cmbo_text;
  @FXML JFXCheckBox cb_isEmergency;
  @FXML JFXTextArea txtf_description;
  @FXML
  JFXCheckBox cb_susPerson, cb_susPackage, cb_harassment, cb_weapons, cb_shouting, cb_violence;

  private ArrayList<JFXCheckBox> checkBoxes = new ArrayList<>();

  private ObservableList<String> fuzzySearchTextList =
      // List that fills TextViews
      FXCollections.observableArrayList();
  LinkedList<DbNode> fuzzySearchNodeList = new LinkedList<>();
  DbNode currentNode = null;

  private String countVal = "";

  public SecurityRequestController() throws DBException {}

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @Override
  public void setSingleton(StateSingleton singleton) {}

  public void initialize() throws DBException {

    cmbo_text.getEditor().setOnKeyTyped(this::locationTextChanged);
    LinkedList<String> languages = ServiceDB.getLanguages();
    ObservableList<String> langList = FXCollections.observableList(languages);

    checkBoxes.add(cb_susPerson);
    checkBoxes.add(cb_susPackage);
    checkBoxes.add(cb_harassment);
    checkBoxes.add(cb_weapons);
    checkBoxes.add(cb_shouting);
    checkBoxes.add(cb_violence);
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

  // Create Security Request
  @FXML
  public void createNewSecRequest() throws DBException {

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

    String notes = "Report details:\n\n";

    for (JFXCheckBox cb : checkBoxes) {
      if (cb.isSelected()) {
        notes = notes + cb.getText() + "\n";
      }
    }

    if (txtf_description.getText().isBlank() && !cb_isEmergency.isSelected()) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Description required for non-emergencies.");
      errorAlert.show();
      return;
    } else {
      notes = notes + "\n" + txtf_description;
    }

    String isEmergency = "";

    if (cb_isEmergency.isSelected()) {
      isEmergency = "Emergency";
    } else {
      isEmergency = "Non-emergency";
    }

    int securityRequest = ServiceDB.addSecurityReq(notes, nodeID, isEmergency);

    txtf_description.clear();
    for (JFXCheckBox cb : checkBoxes) {
      cb.setSelected(false);
    }
    cmbo_text.getItems().clear();
    cb_isEmergency.setSelected(false);

    Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confAlert.setContentText("Request Recieved");
    confAlert.show();
  }
}
