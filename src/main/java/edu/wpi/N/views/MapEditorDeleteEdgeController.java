package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MapEditorDeleteEdgeController {
  @FXML JFXButton btn_cancel;
  @FXML JFXButton btn_confirm;
  @FXML JFXListView lst_delete_edge;

  public JFXButton getBtnCancel() {
    return btn_cancel;
  }

  public JFXButton getBtnConfirm() {
    return btn_confirm;
  }

  public void addLstDeleteNode(String shortName) {
    Label label = new Label(shortName);
    lst_delete_edge.getItems().add(label);
  }

  public void removeLstDeleteNode(String shortName) {
    lst_delete_edge.getItems().removeIf(node -> ((Label) node).getText().equals(shortName));
  }
}
