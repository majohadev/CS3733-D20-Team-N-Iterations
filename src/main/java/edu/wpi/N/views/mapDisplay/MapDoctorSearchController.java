package edu.wpi.N.views.mapDisplay;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import edu.wpi.N.AppClass;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.entities.employees.Doctor;
import edu.wpi.N.views.Controller;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class MapDoctorSearchController implements Controller {
  AppClass mainApp;
  private StateSingleton singleton;

  @FXML TextField txt_doctor;
  @FXML TextField txt_location;
  @FXML ListView lst_fuzzySearch;
  @FXML TextField activeText;
  @FXML JFXButton btn_search;
  @FXML JFXToggleButton tg_handicap;
  @FXML JFXButton btn_reset;

  // Guides for map location search
  @FXML Line lineSearchOne;
  @FXML Line lineSearchTwo;
  @FXML Line lineSearchThree;
  @FXML Line lineSearchFour;

  DbNode[] nodes = new DbNode[2];

  /** Function displays lines indicating the way-finding feature for 10 seconds */
  public void showGuideLines() {
    lineSearchOne.setVisible(true);
    lineSearchTwo.setVisible(true);
    lineSearchThree.setVisible(true);
    lineSearchFour.setVisible(true);

    PauseTransition delay = new PauseTransition(Duration.seconds(10));

    delay.setOnFinished(
        event -> {
          lineSearchOne.setVisible(false);
          lineSearchTwo.setVisible(false);
          lineSearchThree.setVisible(false);
          lineSearchFour.setVisible(false);
        });

    delay.play();
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

  public void onSearchDoctor(KeyEvent e) throws DBException {
    activeText = (TextField) e.getSource();
    if (activeText == txt_location) {
      nodes[0] = null;
    } else {
      nodes[1] = null;
    }
    lst_fuzzySearch.getSelectionModel().clearSelection();
    NewMapDisplayController.fuzzyDoctorSearch(activeText, lst_fuzzySearch);
  }

  public void onItemSelected(MouseEvent e) {
    try {
      ListView lst = (ListView) e.getSource();
      if (activeText == txt_location) {
        activeText.setText(
            lst.getSelectionModel().getSelectedItem().toString()
                + ", "
                + ((DbNode) lst.getSelectionModel().getSelectedItem()).getBuilding());
        nodes[0] = (DbNode) lst.getSelectionModel().getSelectedItem();
      } else {
        if (lst.getSelectionModel().getSelectedItem() instanceof Doctor) {
          activeText.setText(lst.getSelectionModel().getSelectedItem().toString());
          Doctor doc = (Doctor) lst.getSelectionModel().getSelectedItem();
          lst.getSelectionModel().clearSelection();
          lst.getItems().clear();
          lst.getItems().addAll(doc.getLoc());
        } else {
          System.out.println("Hello");
          activeText.setText(
              lst.getSelectionModel().getSelectedItem().toString()
                  + ", "
                  + ((DbNode) lst.getSelectionModel().getSelectedItem()).getBuilding());
          nodes[1] = (DbNode) lst.getSelectionModel().getSelectedItem();
        }
      }
    } catch (NullPointerException ex) {
      return;
    }
  }

  public void clearDbNodes() {
    this.nodes[0] = null;
    this.nodes[1] = null;
  }

  public JFXButton getSearchButton() {
    return this.btn_search;
  }

  public DbNode[] getDBNodes() {
    return this.nodes;
  }

  public boolean getHandicap() {
    return this.tg_handicap.isSelected();
  }

  public TextField getTxtDoctor() {
    return this.txt_doctor;
  }

  public TextField getTextLocation() {
    return this.txt_location;
  }

  public ListView getFuzzyList() {
    return this.lst_fuzzySearch;
  }

  public void setKioskLocation(DbNode node) {
    this.nodes[0] = node;
    this.lst_fuzzySearch.getSelectionModel().select(0);
  }

  public JFXButton getResetButton() {
    return this.btn_reset;
  }

  public JFXToggleButton getTgHandicap() {
    return this.tg_handicap;
  }

  @Override
  public void setMainApp(AppClass mainApp) {
    this.mainApp = mainApp;
  }
}
