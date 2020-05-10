package edu.wpi.N.views.mapDisplay;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import java.io.IOException;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.views.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.w3c.dom.Text;

import java.util.LinkedList;

public class MapDetailSearchController implements Controller {

  @FXML JFXComboBox cmb_detail;
  @FXML TextField txt_location;
  @FXML ListView lst_fuzzySearch;
  @FXML TextField activeText;
  @FXML JFXButton btn_search;
  @FXML JFXToggleButton tg_handicap;
  @FXML JFXButton btn_reset;
  @FXML DbNode[] nodes = new DbNode[2];

  @Override
  public void setMainApp(App mainApp) {}

  public void initialize(){
      LinkedList<String> searchOption = new LinkedList<>();
      searchOption.add("Building");
      searchOption.add("Department");
      ObservableList<String> searchList = FXCollections.observableArrayList(searchOption);
      cmb_detail.setItems(searchList);
  }
  public void onSearchLocation(KeyEvent e) throws DBException {
    activeText = (TextField) e.getSource();
    if (activeText == txt_location) {
      nodes[0] = null;
    } else {
      nodes[1] = null;
    }
    lst_fuzzySearch.getSelectionModel().clearSelection();
    NewMapDisplayController.fuzzyLocationSearch(activeText, lst_fuzzySearch);
  }

  public void onSelectOption(MouseEvent e){
    Object option = cmb_detail.getSelectionModel().getSelectedItem();
    if(option == null){
      return;
    }
    String firstOption = option.toString();
    if(firstOption.equals("Building")){
      onSelectBuilding(e);
    }
    else if(firstOption.equals("Department")){

    }
    else

  }

  public void onSelectBuilding(MouseEvent e){
    activeText = (TextField) e.getSource();
    if(activeText == txt_location){
      nodes[0] = null;
    }
    else
      nodes[1] = null;
    lst_fuzzySearch.getSelectionModel().clearSelection();

  }

  public void onItemSelected(MouseEvent e) {
    ListView lv = (ListView) e.getSource();
    if (activeText == txt_location) {
      // activeText.setText();
    }
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

  public JFXToggleButton getTg_handicap() {
    return tg_handicap;
  }

  public JFXButton getBtn_reset() {
    return btn_reset;
  }
}
