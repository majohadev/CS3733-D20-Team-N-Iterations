package edu.wpi.N.views;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.*;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.entities.employees.Doctor;
import edu.wpi.N.entities.request.MedicineRequest;
import edu.wpi.N.entities.request.Request;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

public class MedicineRequestController implements Controller, Initializable {

  private StateSingleton singleton;

  @Override
  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  private App mainApp;

  ObservableList<Request> tableData = FXCollections.observableArrayList();
  ObservableList<String> languageData = FXCollections.observableArrayList();
  private LinkedList<DbNode> fuzzySearchPatientLocation = new LinkedList<>();
  private ObservableList<String> fuzzySearchTextListPatientLocations =
      FXCollections.observableArrayList();
  private LinkedList<DbNode> fuzzySearchNodeList = new LinkedList<>();
  LinkedList<String> longNamesList = new LinkedList<>(); // Stores Floor Node names
  LinkedList<DbNode> allFloorNodes; // stores all the nodes on the floor

  @FXML ComboBox<Doctor> cb_doctors;
  @FXML TableView tb_patients;
  @FXML Label lbl_empname;
  @FXML JFXTextField txtf_patient;
  @FXML JFXTextField txtf_medicine;
  @FXML JFXTextField txtf_dosage;
  @FXML JFXTextArea txtf_notes;
  @FXML JFXTextField txtf_patientLocation;
  @FXML JFXListView lst_patientLocations;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  // Temp Placeholder for Testing
  public void returnToRequests() throws IOException {
    this.mainApp.switchScene("home.fxml", singleton);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableOfPatients();

    try {
      populateDoctorList();
      populateMedicineRequests();

      allFloorNodes = MapDB.allNodes();
      for (DbNode node : allFloorNodes) {
        longNamesList.add(node.getLongName());
      }

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  public void populateDoctorList() throws DBException {
    ObservableList<Doctor> docs = FXCollections.observableArrayList();
    try {
      docs.addAll(DoctorDB.getDoctors());
      cb_doctors.getItems().addAll(docs);
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  public void initializeTableOfPatients() {
    TableColumn<MedicineRequest, String> patient = new TableColumn<>("Patient Name");
    patient.setMaxWidth(150);
    patient.setMinWidth(150);
    patient.setCellValueFactory(new PropertyValueFactory<MedicineRequest, String>("patient"));

    TableColumn<MedicineRequest, String> meds = new TableColumn<>("Medicine");
    meds.setMaxWidth(150);
    meds.setMinWidth(150);
    meds.setCellValueFactory(new PropertyValueFactory<MedicineRequest, String>("medicineName"));

    TableColumn<MedicineRequest, Double> dosage = new TableColumn<>("Dosage");
    dosage.setMaxWidth(75);
    dosage.setMinWidth(75);
    dosage.setCellValueFactory(new PropertyValueFactory<MedicineRequest, Double>("dosage"));

    TableColumn<MedicineRequest, String> units = new TableColumn<>("Units");
    units.setMaxWidth(75);
    units.setMinWidth(75);
    units.setCellValueFactory(new PropertyValueFactory<MedicineRequest, String>("units"));

    TableColumn<MedicineRequest, String> assignedDoctor = new TableColumn<>("Assigned Doctor");
    assignedDoctor.setMaxWidth(150);
    assignedDoctor.setMinWidth(150);
    assignedDoctor.setCellValueFactory(
        new PropertyValueFactory<MedicineRequest, String>("emp_assigned"));

    TableColumn<MedicineRequest, String> notes = new TableColumn<>("Notes");
    notes.setMaxWidth(150);
    notes.setMinWidth(150);
    notes.setCellValueFactory(new PropertyValueFactory<MedicineRequest, String>("reqNotes"));

    tb_patients.getColumns().addAll(patient, meds, dosage, units, assignedDoctor, notes);
  }

  @FXML
  public void createMedRequest() throws DBException {

    int currentSelection = lst_patientLocations.getSelectionModel().getSelectedIndex();
    DbNode medLocation = fuzzySearchNodeList.get(currentSelection);

    try {

      for (DbNode node : allFloorNodes) {
        if (node.getNodeID().equals(medLocation.getNodeID())) {
          ServiceDB.addMedReq(
              txtf_notes.getText(),
              medLocation.getNodeID(),
              txtf_medicine.getText(),
              Double.parseDouble(txtf_dosage.getText()),
              "ml",
              txtf_patient.getText());
        }
      }
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  public void fuzzySearchDoctorsOffices(KeyEvent keyInput) throws DBException {
    String currentText = txtf_patientLocation.getText();
    fuzzySearchPatientLocation = FuzzySearchAlgorithm.suggestLocations(currentText);
    LinkedList<String> fuzzySearchStringList = new LinkedList<>();
    if (fuzzySearchPatientLocation != null) {

      for (DbNode node : fuzzySearchPatientLocation) {
        fuzzySearchStringList.add(node.getLongName());
      }

      fuzzySearchTextListPatientLocations = FXCollections.observableList(fuzzySearchStringList);
    } else fuzzySearchTextListPatientLocations = FXCollections.observableList(longNamesList);
    lst_patientLocations.setItems(fuzzySearchTextListPatientLocations);
  }

  @FXML
  private void searchByLocationTextFill(KeyEvent inputMethodEvent) throws DBException {
    String currentText = txtf_patientLocation.getText();
    fuzzySearchNodeList = FuzzySearchAlgorithm.suggestLocations(currentText);
    LinkedList<String> fuzzySearchStringList = new LinkedList<>();
    if (fuzzySearchNodeList != null) {

      for (DbNode node : fuzzySearchNodeList) {
        fuzzySearchStringList.add(node.getLongName());
      }

      fuzzySearchTextListPatientLocations = FXCollections.observableList(fuzzySearchStringList);
    } else fuzzySearchTextListPatientLocations = FXCollections.observableList(longNamesList);
    lst_patientLocations.setItems(fuzzySearchTextListPatientLocations);
  }

  /*
  public void populateTable() throws DBException {
    try {
      tbl_Employees.getItems().clear();
      LinkedList<Employee> empList = ServiceDB.getEmployees();
      emps.addAll(empList);
      tbl_Employees.setItems(emps);

      tbl_Employees
          .getSelectionModel()
          .selectedItemProperty()
          .addListener(
              (obs, old, newVal) -> {
                try {
                  emps.setAll(ServiceDB.getEmployees());
                } catch (DBException e) {
                  e.printStackTrace();
                }
              });

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }
   */

  public void populateMedicineRequests() throws DBException {

    try {
      LinkedList<Request> medReq = new LinkedList<>();
      for (Request allReq : ServiceDB.getRequests()) {
        if (allReq.getServiceType().equals("Medicine")) {
          medReq.add(allReq);
        }
      }

      tb_patients.getItems().clear();
      ObservableList<Request> allMedReq = FXCollections.observableArrayList();
      allMedReq.addAll(medReq);
      tb_patients.setItems(allMedReq);

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }
}
