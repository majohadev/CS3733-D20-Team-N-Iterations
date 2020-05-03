package edu.wpi.N.views.mapEditor;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MapEditorAddShaftController {
  @FXML JFXButton btn_cancel;
  @FXML JFXButton btn_confirm;
  @FXML JFXListView lst_add_shaft_node;
  // LinkedList<DbNode> nodesInShaft;

  public void addLstAddShaftNode(String shortName) {
    Label label = new Label(shortName);
    lst_add_shaft_node.getItems().add(label);
  }

  public void removeLstAddShaftNode(String shortName) {
    lst_add_shaft_node.getItems().removeIf(node -> ((Label) node).getText().equals(shortName));
  }

  @FXML
  public JFXButton getBtnCancel() {
    return btn_cancel;
  }

  @FXML
  public JFXButton getBtnConfirm() {
    return btn_confirm;
  }

  public void clearAllFields() {
    lst_add_shaft_node.getItems().removeAll(lst_add_shaft_node.getItems());
  }
}
