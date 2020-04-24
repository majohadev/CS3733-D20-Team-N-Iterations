package edu.wpi.N.views;

import edu.wpi.N.App;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import javax.swing.*;
import java.awt.*;

public class MapEditorController implements Controller {
  App mainApp;

  @FXML Pane pn_display;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  // Nodes Add
  public void onPaneDisplayClicked(MouseEvent event) {
    if (event.getClickCount() == 2) {
      makeCursorMapNode(event);
      showNodesAddDialogue();
    }
  }

  private void makeCursorMapNode(MouseEvent event) {
    Circle mapNode = new Circle();
    mapNode.setRadius(7);
    mapNode.setCenterX(event.getX());
    mapNode.setCenterY(event.getY());
    mapNode.setOpacity(0.8);
    pn_display.getChildren().add(mapNode);
  }

  private void showNodesAddDialogue() {
    JTextField shortName = new JTextField();
    JTextField longName= new JTextField();
    JPanel panel = new JPanel();
   panel.add(new JLabel )
  }


}
