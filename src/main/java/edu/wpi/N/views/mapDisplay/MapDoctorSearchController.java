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

public class MapDoctorSearchController implements Controller {
  App mainApp;
  private StateSingleton singleton;

  @FXML TextField txt_doctor;
  @FXML TextField txt_location;
  @FXML ListView lst_fuzzySearch;
  @FXML TextField activeText;
  @FXML JFXButton btn_search;
  @FXML JFXToggleButton tg_handicap;
  @FXML JFXButton btn_reset;

  DbNode[] nodes = new DbNode[2];

  public void onSearchLocation(KeyEvent e) throws DBException {
    activeText = (TextField) e.getSource();
    NewMapDisplayController.fuzzyLocationSearch(activeText, lst_fuzzySearch);
  }

  public void onSearchDoctor(KeyEvent e) throws DBException {
    activeText = (TextField) e.getSource();
    NewMapDisplayController.fuzzyDoctorSearch(activeText, lst_fuzzySearch);
  }

  public void onItemSelected(MouseEvent e) {
    try {
      ListView lst = (ListView) e.getSource();
      activeText.setText(lst.getSelectionModel().getSelectedItem().toString());
      if (activeText == txt_location) {
        nodes[0] = (DbNode) lst.getSelectionModel().getSelectedItem();
      } else {
        nodes[1] = (DbNode) lst.getSelectionModel().getSelectedItem();
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

  public boolean getHandicap() {
    return this.tg_handicap.isSelected();
  }

  public TextField getTxtDoctor() {
    return this.txt_doctor;
  }

  public TextField getTextLocation() {
    return this.txt_location;
  }

  public ListView getFuzzyList() {
    return this.lst_fuzzySearch;
  }

  public void setKioskLocation(DbNode node) {
    this.nodes[1] = node;
  }

  public JFXButton getResetButton() {
    return this.btn_reset;
  }

  public JFXToggleButton getTgHandicap() {
    return this.tg_handicap;
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }
}
