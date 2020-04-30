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
import java.io.IOException;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class TemplateController implements Controller {

  private App mainApp;
  private StateSingleton singleton;
  // Add FXML Tags Here
  @FXML JFXComboBox<String> cmbo_text;
  @FXML JFXComboBox<String> cmbo_selectLang;
  @FXML JFXTextArea txtf_langNotes;
  @FXML AnchorPane translatorRequestPage;

  private ObservableList<String> fuzzySearchTextList =
      // List that fills TextViews
      FXCollections.observableArrayList();
  LinkedList<DbNode> fuzzySearchNodeList = new LinkedList<>();
  DbNode currentNode = null;

  private String countVal = "";

  public TemplateController() throws DBException {}

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @Override
  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public void initialize() throws DBException {

    cmbo_text.getEditor().setOnKeyTyped(this::locationTextChanged);
    LinkedList<String> languages = ServiceDB.getLanguages();
    ObservableList<String> langList = FXCollections.observableList(languages);
    cmbo_selectLang.setItems(langList);
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
  public void createNewTranslator() throws DBException, IOException {

    String langSelection = cmbo_selectLang.getSelectionModel().getSelectedItem();
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

    translatorRequestPage.setVisible(false);
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("mainServicePage.fxml"));
    translatorRequestPage.getChildren().setAll(currentPane);
    translatorRequestPage.setVisible(true);
  }
}
