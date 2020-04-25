package edu.wpi.N.views;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.DbNode;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class TemplateController implements Controller {

  private App mainApp;

  // @FXML JFXTextField txt_enterLocation;
  @FXML JFXComboBox<String> cmbo_text;
  @FXML JFXComboBox<String> cmbo_selectLang;
  @FXML JFXAutoCompletePopup<String> autoCompletePopup = new JFXAutoCompletePopup<>();

  private ObservableList<String> fuzzySearchTextList =
      // List that fills TextViews
      FXCollections.observableArrayList();
  LinkedList<DbNode> fuzzySearchNodeList = new LinkedList<>();

  public TemplateController() throws DBException {}

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException {

    // SelectionHandler sets the value of the comboBox
    autoCompletePopup.setSelectionHandler(
        event -> {
          cmbo_text.setValue(event.getObject());
        });
    TextField editor = cmbo_text.getEditor();
    editor
        .textProperty()
        .addListener(
            observable -> {
              try {
                autofillLocation(editor.getText());
              } catch (DBException e) {
                e.printStackTrace();
              }
            });

    LinkedList<String> languages = ServiceDB.getLanguages();
    ObservableList<String> langList = FXCollections.observableList(languages);
    cmbo_selectLang.setItems(langList);
  }

  @FXML
  public void autofillLocation(String currentText) throws DBException {
    System.out.println(currentText);
    if (currentText.length() > 1) {
      fuzzySearchNodeList = FuzzySearchAlgorithm.suggestLocations(currentText);
      LinkedList<String> fuzzySearchStringList = new LinkedList<>();
      if (fuzzySearchNodeList != null) {

        for (DbNode node : fuzzySearchNodeList) {
          fuzzySearchStringList.add(node.getLongName());
        }
        fuzzySearchTextList = FXCollections.observableList(fuzzySearchStringList);
      }
      System.out.println(fuzzySearchTextList);
      cmbo_text.getItems().setAll(fuzzySearchTextList);
    }
    if (fuzzySearchTextList == null) fuzzySearchTextList.add("  ");
  }
}
