package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import java.util.HashMap;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;

public class MapDisplaySideController {
  @FXML JFXTextField txt_firstLocation;
  @FXML JFXTextField txt_secondLocation;
  @FXML JFXListView lst_firstLocation;
  @FXML JFXListView lst_secondLocation;
  @FXML JFXButton btn_findPath;
  HashMap<String, DbNode> stringNodeConversion = new HashMap<>();
  LinkedList<String> allLongNames = new LinkedList<>();

  //  public void setLists(
  //      HashMap<String, DbNode> stringNodeConversion, LinkedList<String> allLongNames) {
  //    this.stringNodeConversion = stringNodeConversion;
  //    this.allLongNames = allLongNames;
  //  }

  public JFXTextField getTextFirstLocation() {
    return txt_firstLocation;
  }

  public JFXTextField getTextSecondLocation() {
    return txt_secondLocation;
  }

  public JFXListView getLstFirstLocation() {
    return lst_firstLocation;
  }

  public JFXListView getLstSecondLocation() {
    return lst_secondLocation;
  }

  public JFXButton getBtnFindPath() {
    return btn_findPath;
  }

  public void onSearchFirstLocation(KeyEvent inputMethodEvent) throws DBException {
    LinkedList<DbNode> fuzzySearchNodeList;
    ObservableList<String> fuzzySearchTextList;
    LinkedList<String> fuzzySearchStringList = new LinkedList<>();

    String currentText = txt_firstLocation.getText();
    fuzzySearchNodeList = FuzzySearchAlgorithm.suggestLocations(currentText);
    if (fuzzySearchNodeList != null) {
      for (DbNode node : fuzzySearchNodeList) {
        fuzzySearchStringList.add(node.getLongName());
      }
      fuzzySearchTextList = FXCollections.observableList(fuzzySearchStringList);
    } else fuzzySearchTextList = FXCollections.observableList(this.allLongNames);
    lst_firstLocation.setItems(fuzzySearchTextList);
  }

  //  private void onLocationPathFindClicked(MouseEvent event) throws Exception {
  //    pn_display.getChildren().removeIf(node -> node instanceof Line);
  ////    int currentSelection = lst_locationsorted.getSelectionModel().getSelectedIndex();
  ////    DbNode destinationNode = fuzzySearchNodeList.get(currentSelection);
  ////    if (selectedNodes.size() < 1) selectedNodes.add(defaultNode);
  ////    selectedNodes.add(destinationNode);
  ////    onBtnFindClicked(event);
  ////    selectedNodes.clear();
  ////  }

}
