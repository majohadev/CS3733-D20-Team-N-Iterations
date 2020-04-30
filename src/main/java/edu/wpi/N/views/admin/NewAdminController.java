package edu.wpi.N.views.admin;

import com.jfoenix.controls.*;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.*;
import edu.wpi.N.database.*;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Service;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.entities.employees.Employee;
import edu.wpi.N.entities.employees.Translator;
import edu.wpi.N.entities.request.Request;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class NewAdminController implements Controller, Initializable {

  private App mainApp = null;

  private StateSingleton singleton;

  @Override
  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  @FXML JFXPasswordField pwf_newpass;
  @FXML JFXPasswordField pwf_confpass;
  @FXML JFXTextField txtf_docoffices;
  @FXML JFXTextField txtf_newuser;
  @FXML JFXTextField txtf_docuser;
  @FXML JFXTextField txtf_docpass;
  @FXML JFXTextField txtf_docfn;
  @FXML JFXTextField txtf_docln;
  @FXML JFXTextField txtf_docfield;
  @FXML JFXTextField txtf_empfn;
  @FXML JFXTextField txtf_empln;
  @FXML JFXTextField txtf_languages;
  @FXML JFXTextField txtf_docid;
  @FXML JFXTextField txtf_adminuser;
  @FXML JFXTextField txtf_adminpass;
  @FXML JFXTextField txtf_cpoldpass;
  @FXML JFXTextField txtf_cpnewpass;
  @FXML JFXTextField txtf_cpuser;
  @FXML JFXTextField txtf_empid;
  @FXML JFXTextField txtf_newLang;
  @FXML StackPane sp_getpanes;
  @FXML Pane pn_pane1;
  @FXML Pane pn_pane2;
  @FXML Pane pn_pane3;
  @FXML JFXButton btn_EmployeeEdit;
  @FXML JFXButton btn_AccountEdit;
  @FXML JFXButton btn_ViewRequests;
  @FXML JFXButton btn_EditMap;
  @FXML JFXButton btn_addLanguage;
  @FXML JFXButton btn_removeLanguage;
  @FXML TableView<Employee> tbl_Employees;
  @FXML CheckBox cb_translator;
  @FXML Label lbl_languages;
  @FXML JFXListView lst_docoffice;
  @FXML JFXButton btn_Accept;
  @FXML JFXButton btn_Deny;
  @FXML ChoiceBox<Employee> cb_Employee;
  @FXML ChoiceBox<Service> cb_employeeTypes;
  @FXML TableView<Request> tb_RequestTable = new TableView<Request>();
  @FXML TableView<String> tb_languages = new TableView<String>();
  @FXML JFXCheckBox ch_requestFilter;
  @FXML Label lbl_title;
  @FXML JFXTextField txtf_rmuser;
  @FXML JFXButton btn_arduino;
  @FXML TableView<String> tb_languagesRemove;
  @FXML ChoiceBox<Employee> cb_EmployeeRemove;

  ObservableList<Request> tableData = FXCollections.observableArrayList();
  ObservableList<String> languageData = FXCollections.observableArrayList();
  ObservableList<String> langDataRemove = FXCollections.observableArrayList();

  private LinkedList<DbNode> fuzzySearchDoctorOffice = new LinkedList<>();
  private ObservableList<String> fuzzySearchTextListDoctorOffices =
      FXCollections.observableArrayList();
  private LinkedList<DbNode> fuzzySearchNodeList = new LinkedList<>();
  LinkedList<String> longNamesList = new LinkedList<>(); // Stores Floor Node names
  LinkedList<DbNode> allFloorNodes; // stores all the nodes on the floor
  ObservableList<Employee> emps = FXCollections.observableArrayList();

  private static class selfFactory<G>
      implements Callback<TableColumn.CellDataFeatures<G, G>, ObservableValue<G>> {
    public selfFactory() {}

    @Override
    public ObservableValue<G> call(TableColumn.CellDataFeatures<G, G> param) {
      return new ReadOnlyObjectWrapper<>(param.getValue());
    }
  }

  public static class nodeLongName
      implements Callback<TableColumn.CellDataFeatures<Request, String>, ObservableValue<String>> {

    public nodeLongName() {}

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Request, String> param) {
      try {
        DbNode node = MapDB.getNode(param.getValue().getNodeID());
        if (node == null) {
          return new ReadOnlyObjectWrapper<>("Invalid Location");
        }
        return new ReadOnlyObjectWrapper<>(node.getLongName());
      } catch (DBException e) {
        return new ReadOnlyObjectWrapper<>("Invalid Location");
      }
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {

      pn_pane1.setVisible(false);
      pn_pane2.setVisible(false);
      pn_pane3.setVisible(false);

      populateRequestTable();
      populateChoiceBox();
      populateLanguageTable();
      tableSetup();
      populateTable();
      populateEmployeeType();
      setTitleLabel();

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

      allFloorNodes = MapDB.allNodes();
      for (DbNode node : allFloorNodes) {
        longNamesList.add(node.getLongName());
      }

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
    initializeTable();
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

  public void changePanes(MouseEvent e) throws DBException {
    if (e.getSource() == btn_AccountEdit) {
      System.out.println("In Account Edit");
      pn_pane1.setVisible(true);
      pn_pane2.setVisible(false);
      pn_pane3.setVisible(false);
      populateTable();
    } else if (e.getSource() == btn_EmployeeEdit) {
      System.out.println("In Employee Edit");
      pn_pane2.setVisible(true);
      pn_pane1.setVisible(false);
      pn_pane3.setVisible(false);
      populateTable();
    } else if (e.getSource() == btn_ViewRequests) {
      System.out.println("In View Requests");
      pn_pane3.setVisible(true);
      pn_pane1.setVisible(false);
      pn_pane2.setVisible(false);
      populateTable();
    }
  }

  /** Pops up a new window with File Manager */
  @FXML
  private void popUpFileManager() {
    try {
      Stage stage = new Stage();
      Parent root;
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("fileManagementScreen.fxml"));
      root = loader.load();
      Scene scene = new Scene(root);
      stage.setScene(scene);

      // DataEditorController controller = (DataEditorController) loader.getController();

      stage.initModality(Modality.APPLICATION_MODAL);
      stage.show();
    } catch (Exception ex) {
      ex.printStackTrace();
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText("Error when openning File Manager Window");
      errorAlert.showAndWait();
    }
  }

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

  /*
   Removes an employee from the database
  */

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

  /*
  REFACTOR TO SUPPORT ALL NEW EMPLOYEES
   */
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

  public void adminEditMap() throws IOException {
    mainApp.switchScene("views/mapEditor/mapEditor.fxml", singleton);
  }

  public void returnToPrev() throws IOException {
    mainApp.switchScene("views/newHomePage.fxml", singleton);
  }

  public void addAdmin() throws DBException {
    try {

      if (txtf_adminuser.getText().equals("") | txtf_adminpass.getText().equals("")) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText("Empty Username or Password");
        errorAlert.show();

        return;
      }

      LoginDB.createAdminLogin(txtf_adminuser.getText(), txtf_adminpass.getText());

      Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
      acceptReq.setContentText("Admin " + txtf_adminuser.getText() + " was added.");
      acceptReq.show();

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }

    txtf_adminuser.clear();
    txtf_adminpass.clear();
  }

  public void changeAccountPass() throws DBException {
    try {
      if (txtf_cpnewpass.getText().equals(txtf_cpoldpass.getText()))
        throw new DBException("New password same as old password");
      LoginDB.changePass(txtf_cpuser.getText(), txtf_cpoldpass.getText(), txtf_cpnewpass.getText());

      Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
      acceptReq.setContentText("Password Changed");
      acceptReq.show();

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }

    txtf_cpnewpass.clear();
    txtf_cpoldpass.clear();
    txtf_cpuser.clear();
  }

  public void logoutUser() throws DBException {
    try {
      LoginDB.logout();
      mainApp.switchScene("views/newLogin.fxml", singleton);
    } catch (DBException | IOException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

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
    txtf_docoffices.clear();
  }

  public void fuzzySearchDoctorsOffices(KeyEvent keyInput) throws DBException {
    String currentText = txtf_docoffices.getText();
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
    String currentText = txtf_docoffices.getText();
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

  public void removeOffice() throws DBException {
    System.out.println(txtf_docid.getText());

    int currentSelection = lst_docoffice.getSelectionModel().getSelectedIndex();

    if (currentSelection == -1) {
      Alert acceptReq = new Alert(Alert.AlertType.ERROR);
      acceptReq.setContentText("Invalid / No Location Selected");
      acceptReq.show();

      return;
    }

    DbNode removeOfficeNode = fuzzySearchNodeList.get(currentSelection);

    if (txtf_docid.getText().equals("")) {
      Alert invalidID = new Alert(Alert.AlertType.ERROR);
      invalidID.setContentText("Invalid ID");
      invalidID.show();

      return;
    }

    int doctorID = Integer.parseInt(txtf_docid.getText());

    try {
      for (DbNode node : DoctorDB.getDoctor(doctorID).getLoc()) {
        System.out.println(removeOfficeNode.getNodeID());
        System.out.println(node.getNodeID());
        if (node.getNodeID().equals(removeOfficeNode.getNodeID())) {
          DoctorDB.removeOffice(doctorID, removeOfficeNode);

          Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
          acceptReq.setContentText("Office " + removeOfficeNode.getLongName() + " was removed.");
          acceptReq.show();

          return;
        }
      }

      Alert invalidLoc = new Alert(Alert.AlertType.ERROR);
      invalidLoc.setContentText(DoctorDB.getDoctor(doctorID).getName() + " does not work there.");
      invalidLoc.show();

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }

    txtf_docoffices.clear();
    txtf_docid.clear();
  }

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

  private void initializeTable() {

    // Request Table
    TableColumn<Request, Integer> requestID = new TableColumn<>("ID");
    requestID.setMaxWidth(30);
    requestID.setMinWidth(30);
    requestID.setCellValueFactory(new PropertyValueFactory<Request, Integer>("requestID"));

    TableColumn<Request, Employee> emp_assigned = new TableColumn<>("Assigned");
    emp_assigned.setMaxWidth(100);
    emp_assigned.setMinWidth(100);
    emp_assigned.setCellValueFactory(new PropertyValueFactory<Request, Employee>("emp_assigned"));

    TableColumn<Request, String> notes = new TableColumn<>("Notes");
    notes.setMaxWidth(150);
    notes.setMinWidth(150);
    notes.setCellValueFactory(new PropertyValueFactory<Request, String>("reqNotes"));

    TableColumn<Request, String> nodeID = new TableColumn<>("Location");
    nodeID.setMaxWidth(100);
    nodeID.setMinWidth(100);
    nodeID.setCellValueFactory(new nodeLongName());

    TableColumn<Request, String> status = new TableColumn<>("Status");
    status.setMaxWidth(100);
    status.setMinWidth(100);
    status.setCellValueFactory(new PropertyValueFactory<Request, String>("status"));

    TableColumn<Request, String> attr1 = new TableColumn<>("Attribute 1");
    attr1.setMaxWidth(100);
    attr1.setMinWidth(100);
    attr1.setCellValueFactory(new PropertyValueFactory<Request, String>("Atr1"));

    TableColumn<Request, String> service = new TableColumn<>("Service");
    service.setMaxWidth(75);
    service.setMinWidth(75);
    service.setCellValueFactory(new PropertyValueFactory<Request, String>("serviceType"));

    // Language Table for Translators
    TableColumn<String, String> languages = new TableColumn<>("Languages");
    languages.setMaxWidth(150);
    languages.setMinWidth(150);
    languages.setCellValueFactory(new NewAdminController.selfFactory<String>());

    TableColumn<String, String> langRem = new TableColumn<>("Languages");
    langRem.setMaxWidth(150);
    langRem.setMinWidth(150);
    langRem.setCellValueFactory(new NewAdminController.selfFactory<String>());

    TableColumn<Request, String> attr2 = new TableColumn<>("Attribute 2");
    attr2.setMaxWidth(100);
    attr2.setMinWidth(100);
    attr2.setCellValueFactory(new PropertyValueFactory<Request, String>("Atr2"));

    TableColumn<Request, String> attr3 = new TableColumn<>("Attribute 3");
    attr3.setMaxWidth(100);
    attr3.setMinWidth(100);
    attr3.setCellValueFactory(new PropertyValueFactory<Request, String>("Atr3"));

    TableColumn<Request, String> attr4 = new TableColumn<>("Attribute 4");
    attr4.setMaxWidth(100);
    attr4.setMinWidth(100);
    attr4.setCellValueFactory(new PropertyValueFactory<Request, String>("Atr4"));

    // Initializes Columns
    tb_RequestTable
        .getColumns()
        .addAll(
            requestID, service, emp_assigned, notes, nodeID, status, attr1, attr2, attr3, attr4);
    tb_languages.getColumns().addAll(languages);
    tb_languagesRemove.getColumns().addAll(langRem);
  }

  public void populateRequestTable() throws DBException {
    LinkedList<Request> reqs = ServiceDB.getRequests();
    tableData.setAll(reqs);

    ch_requestFilter
        .selectedProperty()
        .addListener(
            (ov, old, val) -> {
              try {
                if (val) {
                  LinkedList<Request> rqs = ServiceDB.getOpenRequests();
                  tableData.setAll(rqs);
                } else {
                  LinkedList<Request> rqs = ServiceDB.getRequests();
                  tableData.setAll(rqs);
                }
              } catch (DBException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setContentText(e.getMessage());
                errorAlert.show();
              }
            });
    tb_RequestTable.setItems(tableData);
  }

  public void populateLanguageTable() {
    cb_Employee
        .valueProperty()
        .addListener(
            (ov, old, emp) -> {
              if (emp instanceof Translator) {
                languageData.setAll(((Translator) emp).getLanguages());
              } else {
                languageData.setAll(new LinkedList<String>());
              }
            });

    cb_EmployeeRemove
        .valueProperty()
        .addListener(
            (ov, old, emp) -> {
              if (emp instanceof Translator) {
                langDataRemove.setAll(((Translator) emp).getLanguages());
              } else {
                langDataRemove.setAll(new LinkedList<>());
              }
            });

    tb_languages.setItems(languageData);
    tb_languagesRemove.setItems(langDataRemove);
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @FXML
  private void acceptRow(MouseEvent e) {
    try {
      if (e.getSource() == btn_Accept
          && (ServiceDB.getRequest(
                      tb_RequestTable.getSelectionModel().getSelectedItems().get(0).getRequestID())
                  .getEmp_assigned()
              != null)) {
        ServiceDB.completeRequest(
            tb_RequestTable.getSelectionModel().getSelectedItems().get(0).getRequestID(), "");

        Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
        acceptReq.setContentText("Request Accepted");
        acceptReq.show();

        if (ch_requestFilter.isSelected()) {
          tb_RequestTable
              .getItems()
              .removeAll(tb_RequestTable.getSelectionModel().getSelectedItem());
        } else {
          LinkedList<Request> reqs = ServiceDB.getRequests();
          tableData.setAll(reqs);
        }
      } else if (e.getSource() == btn_Deny) { // This case needs a status check
        ServiceDB.denyRequest(
            tb_RequestTable.getSelectionModel().getSelectedItems().get(0).getRequestID(), "");

        Alert denyReq = new Alert(Alert.AlertType.WARNING);
        denyReq.setContentText("Request Denied");
        denyReq.show();

        if (ch_requestFilter.isSelected()) {
          tb_RequestTable
              .getItems()
              .removeAll(tb_RequestTable.getSelectionModel().getSelectedItem());
        } else {
          LinkedList<Request> reqs = ServiceDB.getRequests();
          tableData.setAll(reqs);
        }
      } else if (e.getSource() == btn_Accept
          && (ServiceDB.getRequest(
                      tb_RequestTable.getSelectionModel().getSelectedItems().get(0).getRequestID())
                  .getEmp_assigned()
              == null)) {
        Alert needEmp = new Alert(Alert.AlertType.ERROR);
        needEmp.setContentText("Needs an Assigned Employee");
        needEmp.show();
      }
    } catch (DBException ex) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(ex.getMessage());
      errorAlert.show();
    }
  }

  private void assignEmployeeToRequest(int employee, int ID) throws DBException {
    Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
    try {
      ServiceDB.assignToRequest(employee, ID);
      confAlert.setContentText(
          cb_Employee.getSelectionModel().getSelectedItem().getName()
              + " was assigned to the request");
      confAlert.show();
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
    populateRequestTable();
  }

  @FXML
  private void assignPressed(MouseEvent e) throws DBException {
    int eID;
    int rID;
    try {
      eID = cb_Employee.getSelectionModel().getSelectedItem().getID();
      rID = tb_RequestTable.getSelectionModel().getSelectedItem().getRequestID();
    } catch (NullPointerException indx) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a request and an employee!");
      errorAlert.show();
      return;
    }
    assignEmployeeToRequest(eID, rID);
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

  public void editMap() throws IOException {
    mainApp.switchScene("views/mapEdit.fxml", singleton);
  }

  /*
  Populates employee table based on the getEmployees() besides doctors
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

  /*
  Sets the "Welcome" message based on the username
   */
  public void setTitleLabel() throws DBException {
    try {
      lbl_title.setText("Welcome, " + LoginDB.currentLogin());
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  public void removeLogin() throws DBException {
    try {

      if (txtf_rmuser.getText().equals("")) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText("Invalid Username or Input");
        errorAlert.show();

        return;
      }
      LoginDB.removeLogin(txtf_rmuser.getText());
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }

    Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
    acceptReq.setContentText("Login " + txtf_rmuser.getText() + " was removed.");
    acceptReq.show();

    txtf_rmuser.clear();
  }

  @FXML
  public void loadArduino() {
    Stage stage = new Stage();
    Parent root = null;
    try {
      root = FXMLLoader.load(getClass().getResource("arduinoInterface.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    Scene scene = new Scene(root);
    stage.setScene(scene);
    // stage.initModality(Modality.APPLICATION_MODAL);
    stage.show();
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
}
