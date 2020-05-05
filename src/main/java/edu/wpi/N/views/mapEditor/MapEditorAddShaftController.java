package edu.wpi.N.views.mapEditor;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import edu.wpi.N.database.DBException;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class MapEditorAddShaftController {
  @FXML JFXButton btn_cancel;
  @FXML JFXButton btn_confirm;
  @FXML JFXListView lst_add_shaft_node;
  @FXML Pane pn_listClipper;

  public void initialize() throws DBException, IOException {
    clipList();
  }

  private void clipList() {
    final Rectangle outputClip = new Rectangle();
    outputClip.setArcWidth(10);
    outputClip.setArcHeight(10);
    pn_listClipper.setClip(outputClip);

    pn_listClipper
        .layoutBoundsProperty()
        .addListener(
            (ov, oldValue, newValue) -> {
              outputClip.setWidth(newValue.getWidth());
              outputClip.setHeight(newValue.getHeight());
            });
  }

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
