package edu.wpi.N.views.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.employees.Employee;
import edu.wpi.N.entities.employees.Translator;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class EditEmployeeController implements Initializable {

  @FXML JFXButton btn_addLang;
  @FXML JFXTextField txtf_empid;
  @FXML JFXTextField txtf_newLang;
  @FXML TableView<String> tb_languagesRemove;
  @FXML JFXButton btn_removeLang;
  @FXML ChoiceBox<Employee> cb_languageRemove;

  NewAdminController newAdminController;

  ObservableList<String> languageData = FXCollections.observableArrayList();

  private static class selfFactory<G>
      implements Callback<TableColumn.CellDataFeatures<G, G>, ObservableValue<G>> {
    public selfFactory() {}

    @Override
    public ObservableValue<G> call(TableColumn.CellDataFeatures<G, G> param) {
      return new ReadOnlyObjectWrapper<>(param.getValue());
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      populateChoiceBox();
    } catch (DBException e) {
      e.printStackTrace();
    }
    populateLanguageTable();
    initTable();
  }

  public void updateTranslator(MouseEvent event) throws DBException {

    try {
      if (event.getSource() == btn_addLang) {

        if (txtf_empid.getText().equals("")) {
          Alert errorAlert = new Alert(Alert.AlertType.ERROR);
          errorAlert.setContentText("Needs user ID");
          errorAlert.show();

          return;
        }

        int empID = Integer.parseInt(txtf_empid.getText());

        for (Employee emps : ServiceDB.getEmployees()) {
          if (emps.getID() == empID) {
            if (emps.getServiceType().equals("Translator")) {
              if (txtf_newLang.getText().equals("")) {

                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setContentText("Needs Language");
                errorAlert.show();

                return;
              }

              ServiceDB.addLanguage(empID, txtf_newLang.getText());

              populateChoiceBox();
              populateLanguageTable();

              txtf_empid.clear();
              txtf_newLang.clear();

              Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
              acceptReq.setContentText(ServiceDB.getEmployee(empID).getName() + " was updated.");
              acceptReq.show();

              return;
            }
          }
        }

        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText("User is not a Translator");
        errorAlert.show();

        return;

      } else if (event.getSource() == btn_removeLang) {

        if (cb_languageRemove.getSelectionModel().getSelectedIndex() < 0) {
          Alert acceptReq = new Alert(Alert.AlertType.ERROR);
          acceptReq.setContentText("No Employee Selected");
          acceptReq.show();

          return;
        }

        if (!cb_languageRemove
            .getSelectionModel()
            .getSelectedItem()
            .getServiceType()
            .equals("Translator")) {

          Alert acceptReq = new Alert(Alert.AlertType.ERROR);
          acceptReq.setContentText("Employee is not a Translator");
          acceptReq.show();

          return;
        }

        if (tb_languagesRemove.getSelectionModel().getSelectedItem() == null) {
          Alert acceptReq = new Alert(Alert.AlertType.ERROR);
          acceptReq.setContentText("Select a Language");
          acceptReq.show();

          return;
        }

        ServiceDB.removeLanguage(
            cb_languageRemove.getSelectionModel().getSelectedItem().getID(),
            tb_languagesRemove.getSelectionModel().getSelectedItem());

        Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
        acceptReq.setContentText(
            "Removed: "
                + tb_languagesRemove.getSelectionModel().getSelectedItems()
                + " from "
                + cb_languageRemove.getSelectionModel().getSelectedItem().getName());
        acceptReq.show();

        populateChoiceBox();
        populateLanguageTable();
      }
    } catch (DBException | NumberFormatException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }

    txtf_empid.clear();
    txtf_newLang.clear();
  }

  public void setAdminController(NewAdminController newAdminController) {
    this.newAdminController = newAdminController;
  }

  public void populateLanguageTable() {
    cb_languageRemove
        .valueProperty()
        .addListener(
            (ov, old, emp) -> {
              if (emp instanceof Translator) {
                languageData.setAll(((Translator) emp).getLanguages());
              } else {
                languageData.setAll(new LinkedList<String>());
              }
            });
    tb_languagesRemove.setItems(languageData);
  }

  public void populateChoiceBox() throws DBException {
    try {
      LinkedList<Employee> empList = ServiceDB.getEmployees();
      ObservableList<Employee> empObv = FXCollections.observableArrayList();

      for (Employee emp : empList) {
        if (!emp.getServiceType().equals("Medicine")) {
          empObv.add(emp);
        }
      }

      cb_languageRemove.setItems(empObv);
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  public void initTable() {
    // Language Table for Translators
    TableColumn<String, String> languages = new TableColumn<>("Languages");
    languages.setMaxWidth(150);
    languages.setMinWidth(150);
    languages.setCellValueFactory(new EditEmployeeController.selfFactory<String>());

    tb_languagesRemove.getColumns().addAll(languages);
  }
}
