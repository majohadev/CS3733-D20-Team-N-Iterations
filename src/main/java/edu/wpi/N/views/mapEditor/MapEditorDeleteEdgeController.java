package edu.wpi.N.views.mapEditor;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import edu.wpi.N.database.DBException;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class MapEditorDeleteEdgeController {
  @FXML JFXButton btn_cancel;
  @FXML JFXButton btn_confirm;
  @FXML JFXListView lst_delete_edge;
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
