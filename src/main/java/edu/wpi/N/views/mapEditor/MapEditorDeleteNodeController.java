package edu.wpi.N.views.mapEditor;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import edu.wpi.N.database.DBException;
import java.io.IOException;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class MapEditorDeleteNodeController {
  @FXML JFXButton btn_cancel;
  @FXML JFXButton btn_confirm;
  @FXML JFXListView lst_delete_node;
  @FXML Pane pn_listClipper;

  public static LinkedList<Integer> xandY = new LinkedList<>();

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

  public LinkedList<Integer> getxandY() {
    return xandY;
  }

  public void addLstDeleteNode(String shortName) {
    Label label = new Label(shortName);
    lst_delete_node.getItems().add(label);
  }

  public void addXandY(int xpos, int ypos) {
    Label label = new Label(Integer.toString(xpos));
    Label label1 = new Label(Integer.toString(ypos));
    lst_delete_node.getItems().add(label);
    lst_delete_node.getItems().add(label1);
    xandY.add(xpos);
    xandY.add(ypos);
  }

  public void clearXandY() {
    xandY.clear();
  }

  public void removeLstDeleteNode(String shortName) {
    lst_delete_node.getItems().removeIf(node -> ((Label) node).getText().equals(shortName));
  }
}
