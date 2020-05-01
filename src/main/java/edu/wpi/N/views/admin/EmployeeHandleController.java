package edu.wpi.N.views.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.Service;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.entities.employees.Employee;
import edu.wpi.N.entities.request.Request;
import edu.wpi.N.views.Controller;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class EmployeeHandleController implements Controller, Initializable {

    @FXML TableView<Employee> tbl_Employees;
    @FXML ChoiceBox<Employee> cb_Employee;
    @FXML ChoiceBox<Employee> cb_EmployeeRemove;
    @FXML ChoiceBox<Service> cb_employeeTypes;
    @FXML TableView<String> tb_languagesRemove;

    private static class selfFactory<G>
            implements Callback<TableColumn.CellDataFeatures<G, G>, ObservableValue<G>> {
        public selfFactory() {}

        @Override
        public ObservableValue<G> call(TableColumn.CellDataFeatures<G, G> param) {
            return new ReadOnlyObjectWrapper<>(param.getValue());
        }
    }

    @Override
    public void setMainApp(App mainApp) {

    }

    @Override
    public void setSingleton(StateSingleton singleton) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void tableSetup() {
        TableColumn<Employee, Integer> empID = new TableColumn<>("ID");
        empID.setMaxWidth(100);
        empID.setMinWidth(100);
        empID.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("ID"));

        TableColumn<Employee, String> name = new TableColumn<>("Name");
        name.setMaxWidth(200);
        name.setMinWidth(200);
        name.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));

        TableColumn<Employee, Service> serviceType = new TableColumn<>("Service Type");
        serviceType.setMaxWidth(200);
        serviceType.setMinWidth(200);
        serviceType.setCellValueFactory(new PropertyValueFactory<Employee, Service>("ServiceType"));

        tbl_Employees.getColumns().addAll(empID, name, serviceType);
    }

    public void populateChoiceBox() throws DBException {
        try {
            LinkedList<Employee> empList = ServiceDB.getEmployees();
            ObservableList<Employee> empObv = FXCollections.observableArrayList();
            empObv.addAll(empList);
            cb_Employee.setItems(empObv);
            cb_EmployeeRemove.setItems(empObv);
        } catch (DBException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setContentText(e.getMessage());
            errorAlert.show();
        }
    }

    public void populateEmployeeType() throws DBException {
        LinkedList<Service> employeeList = new LinkedList<Service>();
        ObservableList<Service> empTypeList = FXCollections.observableArrayList();

        for (Service services : ServiceDB.getServices()) {
            if (!services.getServiceType().equals("Medicine")) {
                employeeList.add(services);
            }
        }
        empTypeList.addAll(employeeList);
        cb_employeeTypes.setItems(empTypeList);
    }

    public void removeLanguage() throws DBException {

        if (cb_EmployeeRemove.getSelectionModel().getSelectedIndex() <= -1) {

            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setContentText("Needs user ID");
            errorAlert.show();

            return;
        }

        int empID = cb_EmployeeRemove.getSelectionModel().getSelectedItem().getID();

        ServiceDB.removeLanguage(empID, tb_languagesRemove.getSelectionModel().getSelectedItem());
        String remLang = tb_languagesRemove.getSelectionModel().getSelectedItem();
        tb_languagesRemove.getItems().remove(remLang);

        Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
        acceptReq.setContentText(ServiceDB.getEmployee(empID).getName() + " languages were removed");
        acceptReq.show();
    }

    public void langRem(){

        TableColumn<String, String> langRem = new TableColumn<>("Languages");
        langRem.setMaxWidth(150);
        langRem.setMinWidth(150);
        langRem.setCellValueFactory(new EmployeeHandleController.selfFactory<String>());

        tb_languagesRemove.getColumns().addAll(langRem);
    }

}
