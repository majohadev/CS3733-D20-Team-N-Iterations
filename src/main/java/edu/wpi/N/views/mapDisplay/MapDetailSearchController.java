package edu.wpi.N.views.mapDisplay;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
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
  @FXML JFXButton btn_search;
  @FXML JFXButton btn_doctor;
  @FXML JFXToggleButton tg_handicap;
  @FXML JFXButton btn_reset;
  @FXML DbNode[] nodes = new DbNode[1];

  private StateSingleton singleton;
  private NewMapDisplayController con;
  private DepartmentClicked deptHandler = new DepartmentClicked();
  private BuildingClicked buildHandler = new BuildingClicked();
  private AlphabetClicked alphaHandler = new AlphabetClicked();

  @Override
  public void setMainApp(App mainApp) {}

  private class BuildingClicked implements ChangeListener<String> {

    public BuildingClicked() {
      super();
    }

    @Override
    public void changed(
        ObservableValue<? extends String> observable, String oldVal, String newVal) {
      if (newVal != null) {
        try {
          ObservableList<DbNode> nodes =
              FXCollections.observableArrayList(MapDB.searchVisNode(-1, newVal, null, null));
          lst_fuzzySearch.setItems(nodes);
          lst_selection.setVisible(false);
          lst_fuzzySearch.setVisible(true);
          cmb_detail.getSelectionModel().select(-1);
        } catch (DBException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private class AlphabetClicked implements ChangeListener<String> {

    public AlphabetClicked() {
      super();
    }

    @Override
    public void changed(
        ObservableValue<? extends String> observable, String oldVal, String newVal) {}
  }

  private class DepartmentClicked implements ChangeListener<String> {

    public DepartmentClicked() {
      super();
    }

    @Override
    public void changed(
        ObservableValue<? extends String> observable, String oldVal, String newVal) {}
  }

  private class nodeClicked implements ChangeListener<DbNode> {

    @Override
    public void changed(
        ObservableValue<? extends DbNode> observable, DbNode oldVal, DbNode newVal) {}
  }

  public MapDetailSearchController(StateSingleton singleton, NewMapDisplayController con) {
    this.singleton = singleton;
    this.con = con;
  }

  public void initialize() {
    System.out.println("Initialize");
    cmb_detail
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, old, newval) -> {
              if (newval != null) {
                lst_fuzzySearch.setVisible(false);
                lst_selection.setVisible(true);
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
    lst_fuzzySearch.getSelectionModel().selectedItemProperty().addListener(new nodeClicked());
    populateChangeOption();
  }

  public void onSearchLocation(KeyEvent e) throws DBException {
    activeText = (TextField) e.getSource();
    lst_selection.getSelectionModel().clearSelection();
    NewMapDisplayController.fuzzyLocationSearch(activeText, lst_fuzzySearch);
  }

  public static void BuildingSearch(String option, ListView lst) throws DBException {
    ObservableList<DbNode> list;
    LinkedList<DbNode> buildings = new LinkedList<>();
    buildings = MapDB.searchVisNode(-1, option, null, null);
    list = FXCollections.observableList(buildings);
    lst.setItems(list);
  }

  public static void AlphabeticalSearch(String option, ListView lst) throws DBException {
    ObservableList<DbNode> list;
    LinkedList<DbNode> alphabet;
    alphabet = MapDB.getRoomsByFirstLetter(option.charAt(0));
    list = FXCollections.observableList(alphabet);
    lst.setItems(list);
  }

  public static void DepartmentSearch(String option, ListView lst) throws DBException {
    ObservableList<DbNode> list;
    LinkedList<DbNode> depart = new LinkedList<>();
    LinkedList<String> lst_nodeID = new LinkedList<>();
    lst_nodeID = MapDB.getNodeIDbyField(option);
    for (String s : lst_nodeID) {
      depart.add(MapDB.getNode(s));
    }
    list = FXCollections.observableList(depart);
    lst.setItems(list);
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
    LinkedList<String> buildingTypes = new LinkedList<>();
    buildingTypes.add("Faulkner");
    buildingTypes.add("45 Francis");
    buildingTypes.add("15 Francis");
    buildingTypes.add("BTM");
    buildingTypes.add("Shapiro");
    buildingTypes.add("Tower");
    buildingTypes.add("FLEX");
    ObservableList<String> direct;
    direct = FXCollections.observableList(buildingTypes);
    lst_selection.setItems(direct);
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

  public JFXButton getBtn_search() {
    return btn_search;
  }

  public Boolean getTg_handicap() {
    return tg_handicap.isSelected();
  }

  public JFXToggleButton getHandicap() {
    return this.tg_handicap;
  }

  public JFXButton getBtn_reset() {
    return btn_reset;
  }

  public DbNode[] getDBNodes() {
    return this.nodes;
  }

  public JFXButton getBtn_doctor() {
    return btn_doctor;
  }
}
