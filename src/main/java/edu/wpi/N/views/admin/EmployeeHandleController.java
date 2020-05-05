package edu.wpi.N.views.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DoctorDB;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Service;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.entities.employees.Employee;
import edu.wpi.N.entities.employees.Translator;
import edu.wpi.N.views.Controller;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class EmployeeHandleController implements Controller, Initializable {

  @FXML TableView<Employee> tbl_Employees;
  @FXML ChoiceBox<Employee> cb_EmployeeRemove;
  @FXML ChoiceBox<Service> cb_employeeTypes;
  @FXML CheckBox cb_translator;
  @FXML TableView<String> tb_languagesRemove;
  @FXML JFXTextField txtf_empid;
  @FXML JFXTextField txtf_newLang;
  @FXML JFXButton btn_addLanguage;
  @FXML JFXButton btn_removeLanguage;
  @FXML JFXTextField txtf_docuser;
  @FXML JFXTextField txtf_docpass;
  @FXML JFXTextField txtf_docfn;
  @FXML JFXTextField txtf_docln;
  @FXML JFXTextField txtf_docfield;
  @FXML JFXTextField txtf_docid;
  @FXML JFXTextField txtf_docoffice;
  @FXML JFXListView lst_docoffice;
  @FXML JFXTextField txtf_empfn;
  @FXML JFXTextField txtf_empln;
  @FXML JFXTextField txtf_languages;
  @FXML Label lbl_languages;

  private ObservableList<Employee> emps = FXCollections.observableArrayList();
  private ObservableList<String> languageData = FXCollections.observableArrayList();
  private ObservableList<String> fuzzySearchTextListDoctorOffices =
      FXCollections.observableArrayList();
  private LinkedList<DbNode> fuzzySearchDoctorOffice = new LinkedList<>();
  private LinkedList<DbNode> fuzzySearchNodeList = new LinkedList<>();
  LinkedList<String> longNamesList = new LinkedList<>(); // Stores Floor Node names
  LinkedList<DbNode> allFloorNodes; // stores all the nodes on the floor

  private static class selfFactory<G>
      implements Callback<TableColumn.CellDataFeatures<G, G>, ObservableValue<G>> {
    public selfFactory() {}

    @Override
    public ObservableValue<G> call(TableColumn.CellDataFeatures<G, G> param) {
      return new ReadOnlyObjectWrapper<>(param.getValue());
    }
  }

  @Override
  public void setMainApp(App mainApp) {}

  public void setSingleton(StateSingleton singleton) {}

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    try {

      populateEmployeeType();
      populateChoiceBox();
      populateTable();
      populateLanguageTable();
      langRem();

      allFloorNodes = MapDB.allNodes();
      for (DbNode node : allFloorNodes) {
        longNamesList.add(node.getLongName());
      }

      cb_employeeTypes
          .valueProperty()
          .addListener(
              (ob, old, newVal) -> {
                if (newVal.getServiceType().equals("Translator")) {
                  txtf_languages.setVisible(true);
                  lbl_languages.setVisible(true);
                } else {
                  txtf_languages.setVisible(false);
                  lbl_languages.setVisible(false);
                }
              });

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
    tableSetup();
  }

  /** Setup for the table of employees */
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

  /**
   * Populates the ChoiceBox for employee language removal
   *
   * @throws DBException
   */
  public void populateChoiceBox() throws DBException {
    try {
      LinkedList<Employee> empList = ServiceDB.getEmployees();
      ObservableList<Employee> empObv = FXCollections.observableArrayList();
      empObv.addAll(empList);
      cb_EmployeeRemove.setItems(empObv);
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  /**
   * Populates the ChoiceBox for each different employee type that is offered from the DB (Based on
   * Services, filters out the doctor)
   *
   * @throws DBException
   */
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

  /**
   * Removes a language from the given table based on selection
   *
   * @throws DBException
   */
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

  /** Initializes table of languages for language removal */
  public void langRem() {

    TableColumn<String, String> langRem = new TableColumn<>("Languages");
    langRem.setMaxWidth(150);
    langRem.setMinWidth(150);
    langRem.setCellValueFactory(new EmployeeHandleController.selfFactory<String>());

    tb_languagesRemove.getColumns().addAll(langRem);
  }

  // TODO: Update to parse and suit multiple languages

  /**
   * Updates a translator based on the given language (Currently can only take one language at a
   * time)
   *
   * @param event
   * @throws DBException
   */
  public void updateTranslator(MouseEvent event) throws DBException {

    if (txtf_empid.getText().equals("")) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Needs user ID");
      errorAlert.show();

      return;
    }

    int empID = Integer.parseInt(txtf_empid.getText());

    try {
      if (event.getSource() == btn_addLanguage) {

        if (txtf_newLang.getText().equals("")) {
          Alert errorAlert = new Alert(Alert.AlertType.ERROR);
          errorAlert.setContentText("Needs Language");
          errorAlert.show();

          return;
        }

        ServiceDB.addLanguage(empID, txtf_newLang.getText());

        tb_languagesRemove.getItems().add(txtf_newLang.getText());

        Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
        acceptReq.setContentText(ServiceDB.getEmployee(empID).getName() + " languages were added");
        acceptReq.show();
      }
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }

    txtf_empid.clear();
    txtf_newLang.clear();
  }

  // TODO: Add edge case where doctor has no first name?

  /**
   * Adds a doctor to the database
   *
   * @throws DBException
   */
  public void addDoc() throws DBException {
    String fullName = (txtf_docfn.getText() + " " + txtf_docln.getText());

    try {

      if (txtf_docuser.getText().equals("")
          || txtf_docpass.getText().equals("")
          || txtf_docfn.getText().equals(""))
        throw new DBException("One or more arguments is NULL");

      DoctorDB.addDoctor(
          fullName, txtf_docfield.getText(), txtf_docuser.getText(), txtf_docpass.getText(), null);

      Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
      confAlert.setContentText("Doctor " + fullName + " was added.");
      confAlert.show();

    } catch (DBException er) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(er.getMessage());
      errorAlert.show();
    }

    populateTable();
    txtf_docfn.clear();
    txtf_docln.clear();
    txtf_docfield.clear();
    txtf_docuser.clear();
    txtf_docpass.clear();
  }

  /**
   * Populates the table of employees (Needed for displaying employee ID's for editing and also
   * removing employees)
   *
   * @throws DBException
   */
  public void populateTable() throws DBException {
    try {
      tbl_Employees.getItems().clear();
      LinkedList<Employee> empList = ServiceDB.getEmployees();
      emps.addAll(empList);
      tbl_Employees.setItems(emps);

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  // TODO: Add edge case of adding the same office twice

  /**
   * Adds an office to the doctor with a given ID (Edge case of adding the same office NEEDED)
   *
   * @throws DBException
   */
  public void addOffice() throws DBException {
    int currentSelection = lst_docoffice.getSelectionModel().getSelectedIndex();

    if (currentSelection == -1) {
      Alert acceptReq = new Alert(Alert.AlertType.ERROR);
      acceptReq.setContentText("Invalid / No Location Selected");
      acceptReq.show();

      return;
    }

    DbNode addOfficeNode = fuzzySearchNodeList.get(currentSelection);

    if (txtf_docid.getText().equals("")) {
      Alert invalidID = new Alert(Alert.AlertType.ERROR);
      invalidID.setContentText("Invalid ID");
      invalidID.show();

      return;
    }

    int doctorID = Integer.parseInt(txtf_docid.getText());

    try {
      DoctorDB.addOffice(doctorID, addOfficeNode);

      Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
      acceptReq.setContentText("Office " + addOfficeNode.getLongName() + " was added.");
      acceptReq.show();

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }

    txtf_docid.clear();
    txtf_docoffice.clear();
  }

  /**
   * Removes an office from the given doctor ID (Refactor to use doctor name rather than ID? Could
   * cause edge case exceptions)
   *
   * @throws DBException
   */
  public void removeOffice() throws DBException {
    System.out.println(txtf_docid.getText());

    int currentSelection = lst_docoffice.getSelectionModel().getSelectedIndex();

    if (currentSelection == -1) {
      Alert acceptReq = new Alert(Alert.AlertType.ERROR);
      acceptReq.setContentText("Invalid / No Location Selected");
      acceptReq.show();

      return;
    }
  }

  public void addEmployee() throws DBException {
    String name = txtf_empfn.getText() + " " + txtf_empln.getText();
    try {
      if (txtf_empfn.getText().equals("")) throw new DBException("Employee has no first name");
      if (cb_employeeTypes.getValue().getServiceType().equals("Translator")) {
        String[] arrOfString = txtf_languages.getText().split(",");
        LinkedList<String> languages = new LinkedList<>();
        for (String a : arrOfString) {
          languages.add(a);
        }
        ServiceDB.addTranslator(name, languages);

        Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
        acceptReq.setContentText("Employee " + name + " was added.");
        acceptReq.show();
      } else if (cb_employeeTypes.getValue().getServiceType().equals("Laundry")) {
        ServiceDB.addLaundry(name);

        Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
        acceptReq.setContentText("Employee " + name + " was added.");
        acceptReq.show();
      } else if (cb_employeeTypes.getValue().getServiceType().equals("Wheelchair")) {
        ServiceDB.addWheelchairEmployee(name);

        Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
        acceptReq.setContentText("Employee " + name + " was added.");
        acceptReq.show();
      } else if (cb_employeeTypes.getValue().getServiceType().equals("IT")) {
        ServiceDB.addIT(name);

        Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
        acceptReq.setContentText("Employee " + name + " was added.");
        acceptReq.show();
      } else if (cb_employeeTypes.getValue().getServiceType().equals("Emotional Support")) {
        ServiceDB.addEmotionalSupporter(name);

        Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
        acceptReq.setContentText("Employee " + name + " was added.");
        acceptReq.show();
      } else if (cb_employeeTypes.getValue().getServiceType().equals("Sanitation")) {
        ServiceDB.addSanitationEmp(name);

        Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
        acceptReq.setContentText("Employee " + name + " was added.");
        acceptReq.show();
      } else if (cb_employeeTypes.getValue().getServiceType().equals("Flower")) {
        ServiceDB.addFlowerDeliverer(name);

        Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
        acceptReq.setContentText("Employee " + name + " was added.");
        acceptReq.show();
      } else if (cb_employeeTypes.getValue().getServiceType().equals("Internal Transportation")) {
        ServiceDB.addInternalTransportationEmployee(name);

        Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
        acceptReq.setContentText("Employee " + name + " was added.");
        acceptReq.show();
      } else if (cb_employeeTypes.getValue().getServiceType().equals("Security")) {
        ServiceDB.addSecurityOfficer(name);

        Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
        acceptReq.setContentText("Employee " + name + " was added.");
        acceptReq.show();
      }

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }

    txtf_empfn.clear();
    txtf_empln.clear();
    txtf_languages.clear();
    populateTable();
    populateChoiceBox();
  }

  public void deleteEmployee() throws DBException {
    try {

      if (tbl_Employees.getSelectionModel().getSelectedIndex() <= -1) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText("Select an Employee");
        errorAlert.show();

        return;
      }

      ServiceDB.removeEmployee(tbl_Employees.getSelectionModel().getSelectedItem().getID());

      Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
      confAlert.setContentText("Employee removed");
      confAlert.show();

      int removeLine = tbl_Employees.getSelectionModel().getSelectedIndex();
      tbl_Employees.getItems().remove(removeLine);
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
    populateChoiceBox();
  }

  public void fuzzySearchDoctorsOffices(KeyEvent keyInput) throws DBException {
    String currentText = txtf_docoffice.getText();
    fuzzySearchDoctorOffice = FuzzySearchAlgorithm.suggestLocations(currentText);
    LinkedList<String> fuzzySearchStringList = new LinkedList<>();
    if (fuzzySearchDoctorOffice != null) {

      for (DbNode node : fuzzySearchDoctorOffice) {
        fuzzySearchStringList.add(node.getLongName());
      }

      fuzzySearchTextListDoctorOffices = FXCollections.observableList(fuzzySearchStringList);
    } else fuzzySearchTextListDoctorOffices = FXCollections.observableList(longNamesList);
    lst_docoffice.setItems(fuzzySearchTextListDoctorOffices);
  }

  @FXML
  private void searchByLocationTextFill(KeyEvent inputMethodEvent) throws DBException {
    String currentText = txtf_docoffice.getText();
    fuzzySearchNodeList = FuzzySearchAlgorithm.suggestLocations(currentText);
    LinkedList<String> fuzzySearchStringList = new LinkedList<>();
    if (fuzzySearchNodeList != null) {

      for (DbNode node : fuzzySearchNodeList) {
        fuzzySearchStringList.add(node.getLongName());
      }

      fuzzySearchTextListDoctorOffices = FXCollections.observableList(fuzzySearchStringList);
    } else fuzzySearchTextListDoctorOffices = FXCollections.observableList(longNamesList);
    lst_docoffice.setItems(fuzzySearchTextListDoctorOffices);
  }

  public void populateLanguageTable() {
    cb_EmployeeRemove
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
}
