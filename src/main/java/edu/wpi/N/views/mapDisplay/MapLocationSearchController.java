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

  @FXML public TextField txt_firstLocation;
  @FXML public TextField txt_secondLocation;
  @FXML ListView lst_fuzzySearch;
  @FXML TextField activeText;
  @FXML JFXButton btn_search;
  @FXML JFXToggleButton tg_handicap;
  @FXML JFXButton btn_reset;
  @FXML Pane btn_restRoom;
  @FXML Pane btn_infodesk;
  @FXML Pane btn_quickexit;
  public static DbNode[] nodes = new DbNode[2];
  public static String nodeName;

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

  public void setDBNode(int index, DbNode node) {
    this.nodes[index] = node;
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

  public static void setHitboxSearchNodes(DbNode node, String textField) {

    if (textField.equals("Start")) {
      nodes[0] = node;
      nodeName = node.getLongName();
    }

    if (textField.equals("Destination")) {
      nodes[1] = node;
      nodeName = node.getLongName();
    }
  }

  public void setNodeName() {
    this.txt_firstLocation.setText(nodeName);
  }

  public void clearSecondEntry() {
    this.txt_secondLocation.clear();
    this.nodes[1] = null;
  }
}
