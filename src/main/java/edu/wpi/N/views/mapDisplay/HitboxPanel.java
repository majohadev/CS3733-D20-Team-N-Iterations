package edu.wpi.N.views.mapDisplay;

import com.jfoenix.controls.JFXButton;
import edu.wpi.N.App;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.views.Controller;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class HitboxPanel implements Controller {

  @FXML JFXButton btn_start;
  @FXML JFXButton btn_destination;
  @FXML AnchorPane pn_menu;
  public static DbNode hitboxNode;
  public static NewMapDisplayController cont;
  private App mainApp;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public static void setHitboxNode(DbNode clickedNode, NewMapDisplayController shit) {
    hitboxNode = clickedNode;
    cont = shit;
  }

  @FXML
  public void onStartPointClicked() {
    cont.setSearchNodesHitboxClicks(hitboxNode, "Start");
    pn_menu.setVisible(false);
    pn_menu.setMouseTransparent(true);
    pn_menu.getParent().setMouseTransparent(true);
  }

  @FXML
  public void onEndPointClicked() {
    cont.setSearchNodesHitboxClicks(hitboxNode, "Destination");
    pn_menu.setVisible(false);
    pn_menu.setMouseTransparent(true);
    pn_menu.getParent().setMouseTransparent(true);
  }
}
