package edu.wpi.N.views.mapDisplay;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import java.util.LinkedList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

public class MapDetailSearchController implements Controller {

  @FXML JFXComboBox<String> cmb_detail;
  @FXML TextField txt_location;
  @FXML ListView<String> lst_selection;
  @FXML ListView<DbNode> lst_fuzzySearch;
  @FXML TextField activeText;

  @FXML DbNode[] nodes = new DbNode[1];

  private StateSingleton singleton;
  private NewMapDisplayController con;
  private DepartmentClicked deptHandler = new DepartmentClicked();
  private BuildingClicked buildHandler = new BuildingClicked();
  private AlphabetClicked alphaHandler = new AlphabetClicked();

  @Override
  public void setMainApp(App mainApp) {}

  private class BuildingClicked implements ChangeListener<String> {

    public BuildingClicked() {}

    @Override
    public void changed(
        ObservableValue<? extends String> observable, String oldVal, String newVal) {
      if (newVal != null) {
        try {
          ObservableList<DbNode> nodes =
              FXCollections.observableArrayList(MapDB.searchVisNode(-1, newVal, null, null));
          lst_fuzzySearch.setItems(nodes);
          lst_selection.setVisible(false);
          lst_selection.setMouseTransparent(true);
          lst_fuzzySearch.setVisible(true);
          lst_fuzzySearch.setMouseTransparent(false);
          lst_selection.getSelectionModel().selectedItemProperty().removeListener(this);
          cmb_detail.getSelectionModel().clearSelection();
        } catch (DBException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private class AlphabetClicked implements ChangeListener<String> {

    public AlphabetClicked() {}

    @Override
    public void changed(
        ObservableValue<? extends String> observable, String oldVal, String newVal) {
      if (newVal != null) {
        try {
          ObservableList<DbNode> nodes =
              FXCollections.observableArrayList(MapDB.getRoomsByFirstLetter(newVal.charAt(0)));
          lst_fuzzySearch.setItems(nodes);
          lst_selection.setVisible(false);
          lst_selection.setMouseTransparent(true);
          lst_fuzzySearch.setVisible(true);
          lst_fuzzySearch.setMouseTransparent(false);
          lst_selection.getSelectionModel().selectedItemProperty().removeListener(this);
          cmb_detail.getSelectionModel().clearSelection();
        } catch (DBException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private class DepartmentClicked implements ChangeListener<String> {

    public DepartmentClicked() {}

    @Override
    public void changed(
        ObservableValue<? extends String> observable, String oldVal, String newVal) {
      try {
        ObservableList<DbNode> nodes =
            FXCollections.observableArrayList(MapDB.getNodesbyField(newVal));
        lst_fuzzySearch.setItems(nodes);
        lst_selection.setVisible(false);
        lst_selection.setMouseTransparent(true);
        lst_fuzzySearch.setVisible(true);
        lst_fuzzySearch.setMouseTransparent(false);
        lst_selection.getSelectionModel().selectedItemProperty().removeListener(this);
        cmb_detail.getSelectionModel().clearSelection();
      } catch (DBException e) {
        e.printStackTrace();
      }
    }
  }

  private class nodeClicked implements ChangeListener<DbNode> {

    @Override
    public void changed(
        ObservableValue<? extends DbNode> observable, DbNode oldVal, DbNode newVal) {
      try {
        con.nodeFromDirectory((newVal));
      } catch (DBException | IOException e) {
        e.printStackTrace();
      }
    }
  }

  public MapDetailSearchController(StateSingleton singleton, NewMapDisplayController con) {
    this.singleton = singleton;
    this.con = con;
  }

  public void initialize() {
    cmb_detail
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, old, newval) -> {
              if (newval != null) {
                lst_fuzzySearch.setVisible(false);
                lst_fuzzySearch.setMouseTransparent(true);
                lst_selection.setVisible(true);
                lst_selection.setMouseTransparent(false);
                if (newval.equals("Building")) {
                  populateChangeBuilding();
                  lst_selection
                      .getSelectionModel()
                      .selectedItemProperty()
                      .removeListener(deptHandler);
                  lst_selection
                      .getSelectionModel()
                      .selectedItemProperty()
                      .removeListener(alphaHandler);
                  lst_selection
                      .getSelectionModel()
                      .selectedItemProperty()
                      .addListener(buildHandler);
                } else if (newval.equals("Alphabetical")) {
                  populateChangeAlphabetical();
                  lst_selection
                      .getSelectionModel()
                      .selectedItemProperty()
                      .removeListener(deptHandler);
                  lst_selection
                      .getSelectionModel()
                      .selectedItemProperty()
                      .addListener(alphaHandler);
                  lst_selection
                      .getSelectionModel()
                      .selectedItemProperty()
                      .removeListener(buildHandler);
                } else if (newval.equals("Department")) {
                  populateChangeDepartment();
                  lst_selection.getSelectionModel().selectedItemProperty().addListener(deptHandler);
                  lst_selection
                      .getSelectionModel()
                      .selectedItemProperty()
                      .removeListener(alphaHandler);
                  lst_selection
                      .getSelectionModel()
                      .selectedItemProperty()
                      .removeListener(buildHandler);
                }
              }
            });
    lst_fuzzySearch.setVisible(false);
    lst_fuzzySearch.setMouseTransparent(true);
    lst_fuzzySearch.getSelectionModel().selectedItemProperty().addListener(new nodeClicked());
    populateChangeOption();
  }

  public void onSearchLocation(KeyEvent e) throws DBException {
    activeText = (TextField) e.getSource();
    lst_selection.getSelectionModel().clearSelection();
    NewMapDisplayController.fuzzyLocationSearch(activeText, lst_fuzzySearch);
  }

  public void populateChangeDepartment() {
    try {
      lst_selection.setItems(FXCollections.observableList(MapDB.getFields()));
    } catch (DBException e) {
      e.printStackTrace();
    }
  }

  public void populateChangeAlphabetical() {
    LinkedList<String> alphabet = new LinkedList<>();
    char c = 'A';
    while (c <= 'Z') {
      alphabet.add(String.valueOf(c));
      c++;
    }
    lst_selection.setItems(FXCollections.observableList(alphabet));
  }

  public void populateChangeOption() {
    LinkedList<String> directTypes = new LinkedList<>();
    directTypes.add("Building");
    directTypes.add("Alphabetical");
    directTypes.add("Department");
    ObservableList<String> direct = FXCollections.observableArrayList();
    direct.addAll(directTypes);
    cmb_detail.setItems(direct);
  }

  public void populateChangeBuilding() {
    try {
      lst_selection.setItems(FXCollections.observableList(MapDB.getBuildings()));
    } catch (DBException e) {
      e.printStackTrace();
    }
  }

  public void clearDbNodes() {
    this.nodes[0] = null;
    this.nodes[1] = null;
  }

  public JFXComboBox getCmb_detail() {
    return cmb_detail;
  }

  public TextField getTxt_location() {
    return txt_location;
  }

  public ListView getLst_selection() {
    return lst_selection;
  }

  public TextField getActiveText() {
    return activeText;
  }

  public DbNode[] getDBNodes() {
    return this.nodes;
  }
}
