package edu.wpi.N.views.mapDisplay;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.views.Controller;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class MapDetailSearchController implements Controller {

  @FXML JFXComboBox cmb_detail;
  @FXML TextField txt_location;
  @FXML ListView lst_fuzzySearch;
  @FXML TextField activeText;
  @FXML JFXButton btn_search;
  @FXML JFXButton btn_doctor;
  @FXML JFXToggleButton tg_handicap;
  @FXML JFXButton btn_reset;
  @FXML DbNode[] nodes = new DbNode[1];

  @Override
  public void setMainApp(App mainApp) {}

  public void initialize() {
    populateChangeOption();
  }

  public void onSearchLocation(KeyEvent e) throws DBException {
    activeText = (TextField) e.getSource();
    lst_fuzzySearch.getSelectionModel().clearSelection();
    NewMapDisplayController.fuzzyLocationSearch(activeText, lst_fuzzySearch);
  }

  public void onSelectOption(MouseEvent e) throws DBException {
    Object option = cmb_detail.getSelectionModel().getSelectedItem();
    if (option == null) {
      return;
    }
    String firstOption = option.toString();
    // System.out.println(firstOption);
    if (firstOption.equals("Building")) {
      onSelectBuilding(e, firstOption);
    } else if (option.equals("Department")) {

    } else {
      onSelectAlphabet(e, firstOption);
    }
  }

  public void onSelectBuilding(MouseEvent e, String option) throws DBException {
    populateChangeBuilding();
    lst_fuzzySearch.getSelectionModel().clearSelection();
    NewMapDisplayController.BuildingSearch(option, lst_fuzzySearch);
  }

  public void onSelectAlphabet(MouseEvent e, String option){
    //populateChangeAlphabet();
  }

  public void onItemSelected(MouseEvent e) {
    ListView lv = (ListView) e.getSource();
    if (activeText == txt_location) {
      activeText.setText(
          lv.getSelectionModel().getSelectedItem().toString()
              + ", "
              + ((DbNode) lv.getSelectionModel().getSelectedItem()).getBuilding());
      nodes[0] = (DbNode) lv.getSelectionModel().getSelectedItem();
    } else {
      if (lv.getSelectionModel().getSelectedItems().get(0) instanceof String) {
        // onSelectOption(e);
      } else if (lv.getSelectionModel().getSelectedItems().get(0) instanceof Character) {
        // onSelectOption(e);
      } else {

      }
    }
  }

  public void populateChangeOption() {
    LinkedList<String> directTypes = new LinkedList<>();
    directTypes.add("Building");
    directTypes.add("Alphabetical");
    directTypes.add("Department");
    ObservableList<String> direct = FXCollections.observableArrayList();
    direct.addAll(directTypes);
    cmb_detail.setItems(direct);
  }

  public void populateChangeBuilding() {
    LinkedList<String> buildingTypes = new LinkedList<>();
    buildingTypes.add("Faulkner");
    buildingTypes.add("45 Francis");
    buildingTypes.add("15 Francis");
    buildingTypes.add("BTM");
    buildingTypes.add("Shapiro");
    buildingTypes.add("Tower");
    buildingTypes.add("FLEX");
    ObservableList<String> direct;
    direct = FXCollections.observableList(buildingTypes);
    lst_fuzzySearch.setItems(direct);
  }

  public void clearDbNodes() {
    this.nodes[0] = null;
    this.nodes[1] = null;
  }

  public JFXComboBox getCmb_detail() {
    return cmb_detail;
  }

  public TextField getTxt_location() {
    return txt_location;
  }

  public ListView getLst_fuzzySearch() {
    return lst_fuzzySearch;
  }

  public TextField getActiveText() {
    return activeText;
  }

  public JFXButton getBtn_search() {
    return btn_search;
  }

  public Boolean getTg_handicap() {
    return tg_handicap.isSelected();
  }

  public JFXToggleButton getHandicap() {
    return this.tg_handicap;
  }

  public JFXButton getBtn_reset() {
    return btn_reset;
  }

  public DbNode[] getDBNodes() {
    return this.nodes;
  }

  public JFXButton getBtn_doctor() {
    return btn_doctor;
  }
}
