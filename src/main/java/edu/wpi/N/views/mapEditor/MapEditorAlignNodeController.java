package edu.wpi.N.views.mapEditor;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MapEditorAlignNodeController {

  @FXML JFXButton btn_cancel;
  @FXML JFXButton btn_confirm;
  @FXML JFXListView lst_align_node;
  @FXML JFXCheckBox alignX;
  @FXML JFXCheckBox alignY;
  @FXML JFXTextField txt_Pos;

  @FXML
  public JFXButton getBtnCancel() {
    return btn_cancel;
  }

  public JFXButton getBtnConfirm() {
    return btn_confirm;
  }

  public void addLstAlignNode(String shortName) {
    Label label = new Label(shortName);
    lst_align_node.getItems().add(label);
  }

  public void removeLstAlignNode(String shortName) {
    lst_align_node.getItems().removeIf(node -> ((Label) node).getText().equals(shortName));
  }

  public String getPos() {
    if (txt_Pos.getText() == null || txt_Pos.getText().trim().isEmpty()) {
      return null;
    }
    return txt_Pos.getText();
  }

  public boolean getAlignXSelected() {
    return alignX.isSelected() && !alignY.isSelected();
  }

  public boolean getAlignYSelected() {
    return alignY.isSelected() && !alignX.isSelected();
  }

  public void clearAllFields() {
    txt_Pos.clear();
    alignX.setSelected(false);
    alignY.setSelected(false);
  }

  //    public void alignLstAlignNode(String shortName) {
  //        if (alignX.isSelected() && !alignY.isSelected()) {
  //
  //        } else if (alignY.isSelected() && !alignX.isSelected()) {
  //            //MapDB.modifyNode()
  //        } else {
  //            displayErrorMessage("Invalid input");
  //        }
  //    }
}
