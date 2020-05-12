package edu.wpi.N.views.mapDisplay;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MapLocationSearchController implements Controller {
  App mainApp;
  private StateSingleton singleton;

  @FXML TextField txt_firstLocation;
  @FXML TextField txt_secondLocation;
  @FXML ListView lst_fuzzySearch;
  @FXML TextField activeText;
  @FXML JFXButton btn_search;
  @FXML JFXToggleButton tg_handicap;
  @FXML JFXButton btn_reset;
  @FXML Pane btn_restRoom;
  @FXML Pane btn_infodesk;
  @FXML Pane btn_quickexit;
  DbNode[] nodes = new DbNode[2];

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void onSearchLocation(KeyEvent e) throws DBException {
    activeText = (TextField) e.getSource();
    if (activeText == txt_firstLocation) {
      nodes[0] = null;
    } else {
      nodes[1] = null;
    }
    lst_fuzzySearch.getSelectionModel().clearSelection();
    NewMapDisplayController.fuzzyLocationSearch(activeText, lst_fuzzySearch);
  }

  public void onItemSelected(MouseEvent e) {
    try {
      ListView lst = (ListView) e.getSource();
      activeText.setText(
          lst.getSelectionModel().getSelectedItem().toString()
              + ", "
              + ((DbNode) lst.getSelectionModel().getSelectedItem()).getBuilding());
      if (activeText == txt_firstLocation) {
        nodes[0] = (DbNode) lst.getSelectionModel().getSelectedItem();
      } else {
        nodes[1] = (DbNode) lst.getSelectionModel().getSelectedItem();
      }
      if (nodes[0].getBuilding().equals("Faulkner")) {
        btn_infodesk.setOpacity(.4);
        btn_infodesk.setDisable(true);
      } else {
        btn_infodesk.setOpacity(1);
        btn_infodesk.setDisable(false);
      }
    } catch (NullPointerException ex) {
      return;
    }
  }

  /**
   * Function select the Start Location field and enters the given long Name. Then populates the
   * fuzzy search list with suggestions and selects the first item
   *
   * @param locationName long name of the start location
   * @param isFirstLocation true if selecting start location, false if selecting end location
   */
  public void searchStartOrEndLocationForBot(String locationName, boolean isFirstLocation) {

    if (isFirstLocation) {
      // clear previous entry
      nodes[0] = null;

      // set text
      txt_firstLocation.setText(locationName);
      activeText = txt_firstLocation;
    } else {
      // clear previous entry
      nodes[1] = null;

      // set text
      txt_secondLocation.setText(locationName);
      activeText = txt_secondLocation;
    }

    lst_fuzzySearch.getSelectionModel().clearSelection();

    try {
      // populate list of suggestions
      NewMapDisplayController.fuzzyLocationSearch(activeText, lst_fuzzySearch);
    } catch (DBException ex) {
      ex.printStackTrace();
    }

    selectBestSuggestionFromDropDown();
  }

  /**
   * Selects the best suggestion from the drop down menu after entering location (either first or
   * second)
   */
  private void selectBestSuggestionFromDropDown() {
    try {
      lst_fuzzySearch.getSelectionModel().selectFirst();
      activeText.setText(
          lst_fuzzySearch.getSelectionModel().getSelectedItem().toString()
              + ", "
              + ((DbNode) lst_fuzzySearch.getSelectionModel().getSelectedItem()).getBuilding());
      if (activeText == txt_firstLocation) {
        nodes[0] = (DbNode) lst_fuzzySearch.getSelectionModel().getSelectedItem();
      } else {
        nodes[1] = (DbNode) lst_fuzzySearch.getSelectionModel().getSelectedItem();
      }
      if (nodes[0].getBuilding().equals("Faulkner")) {
        btn_infodesk.setOpacity(.4);
        btn_infodesk.setDisable(true);
      } else {
        btn_infodesk.setOpacity(1);
        btn_infodesk.setDisable(false);
      }
    } catch (NullPointerException ex) {
      ex.printStackTrace();
      return;
    }
  }

  public JFXButton getSearchButton() {
    return this.btn_search;
  }

  public DbNode[] getDBNodes() {
    return this.nodes;
  }

  public void clearDbNodes() {
    this.nodes[0] = null;
    this.nodes[1] = null;
  }

  public boolean getHandicap() {
    return this.tg_handicap.isSelected();
  }

  public TextField getTextFirstLocation() {
    return this.txt_firstLocation;
  }

  public TextField getTextSecondLocation() {
    return this.txt_secondLocation;
  }

  public JFXButton getResetButton() {
    return this.btn_reset;
  }

  public void setKioskLocation(DbNode node) {
    this.nodes[0] = node;
    if (node.getBuilding().equals("Faulkner")) {
      btn_infodesk.setOpacity(.4);
      btn_infodesk.setDisable(true);
    } else {
      btn_infodesk.setOpacity(1);
      btn_infodesk.setDisable(false);
    }
  }

  public ListView getFuzzyList() {
    return this.lst_fuzzySearch;
  }

  public JFXToggleButton getTgHandicap() {
    return this.tg_handicap;
  }

  public Pane getBtnRestRoom() {
    return this.btn_restRoom;
  }

  public Pane getBtnInfoDesk() {
    return this.btn_infodesk;
  }

  public Pane getBtnQuickExit() {
    return this.btn_quickexit;
  }
}
