package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.*;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class MedicineRequestController implements Controller, Initializable {

  private StateSingleton singleton;

  @Override
  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  private App mainApp;

  private LinkedList<DbNode> fuzzySearchPatientLocation = new LinkedList<>();
  private ObservableList<String> fuzzySearchTextListPatientLocations =
      FXCollections.observableArrayList();
  private LinkedList<DbNode> fuzzySearchNodeList = new LinkedList<>();
  LinkedList<String> longNamesList = new LinkedList<>(); // Stores Floor Node names
  LinkedList<DbNode> allFloorNodes; // stores all the nodes on the floor

  @FXML ComboBox<String> cb_units;
  @FXML TableView tb_patients;
  @FXML JFXTextField txtf_patient;
  @FXML JFXTextField txtf_medicine;
  @FXML JFXTextField txtf_dosage;
  @FXML JFXTextArea txtf_notes;
  @FXML JFXTextField txtf_patientLocation;
  @FXML JFXListView lst_patientLocations;
  @FXML AnchorPane ap_prep;
  @FXML AnchorPane ap_tableview;
  @FXML JFXButton btn_prescribe;
  @FXML JFXButton btn_viewreq;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void changeScene(MouseEvent e) {
    if (e.getSource() == btn_prescribe) {
      ap_prep.setVisible(true);
      ap_tableview.setVisible(false);
    } else if (e.getSource() == btn_viewreq) {
      ap_prep.setVisible(false);
      ap_tableview.setVisible(true);
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableOfPatients();
    populateChoiceBox();

    ap_tableview.setVisible(false);
    ap_prep.setVisible(false);

    try {

      // populateDoctorList();
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

  public void initializeTableOfPatients() {
    TableColumn<MedicineRequest, String> patient = new TableColumn<>("Patient Name");
    patient.setMaxWidth(150);
    patient.setMinWidth(150);
    patient.setCellValueFactory(new PropertyValueFactory<MedicineRequest, String>("atr3"));

    TableColumn<MedicineRequest, String> meds = new TableColumn<>("Medicine");
    meds.setMaxWidth(150);
    meds.setMinWidth(150);
    meds.setCellValueFactory(new PropertyValueFactory<MedicineRequest, String>("atr1"));

    TableColumn<MedicineRequest, Double> dosage = new TableColumn<>("Dosage");
    dosage.setMaxWidth(75);
    dosage.setMinWidth(75);
    dosage.setCellValueFactory(new PropertyValueFactory<MedicineRequest, Double>("atr2"));

    TableColumn<MedicineRequest, String> assignedDoctor = new TableColumn<>("Assigned Doctor");
    assignedDoctor.setMaxWidth(150);
    assignedDoctor.setMinWidth(150);
    assignedDoctor.setCellValueFactory(
        new PropertyValueFactory<MedicineRequest, String>("emp_assigned"));

    TableColumn<MedicineRequest, String> notes = new TableColumn<>("Notes");
    notes.setMaxWidth(150);
    notes.setMinWidth(150);
    notes.setCellValueFactory(new PropertyValueFactory<MedicineRequest, String>("reqNotes"));

    tb_patients.getColumns().addAll(patient, meds, dosage, assignedDoctor, notes);
  }

  public void populateChoiceBox() {
    LinkedList<String> dosages = new LinkedList<>();
    ObservableList<String> population = FXCollections.observableArrayList();
    dosages.add("mg");
    dosages.add("cc");
    dosages.add("g");
    population.setAll(dosages);
    cb_units.setItems(population);
  }

  @FXML
  public void createMedRequest() throws DBException {

    try {

      if (txtf_patient.getText().equals("")) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText("No Patient");
        errorAlert.show();
        return;
      } else if (txtf_medicine.getText().equals("")) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText("No Medicine Given");
        errorAlert.show();
        return;
      } else if (txtf_patientLocation.getText().equals("")) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText("No Location");
        errorAlert.show();
        return;
      } else if (txtf_dosage.getText().equals("")) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText("Invalid Dosage");
        errorAlert.show();
        return;
      } else if (cb_units.getSelectionModel().getSelectedItem() == null) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText("No Dosage Given");
        errorAlert.show();
        return;
      }

      int currentSelection = lst_patientLocations.getSelectionModel().getSelectedIndex();
      DbNode medLocation = fuzzySearchNodeList.get(currentSelection);
      String dosage = cb_units.getSelectionModel().getSelectedItem();

      for (DbNode node : allFloorNodes) {
        if (node.getNodeID().equals(medLocation.getNodeID())) {
          ServiceDB.addMedReq(
              txtf_notes.getText(),
              medLocation.getNodeID(),
              txtf_medicine.getText(),
              Double.parseDouble(txtf_dosage.getText()),
              dosage,
              txtf_patient.getText());

          Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
          confAlert.setContentText(
              txtf_medicine.getText() + " prescription made for " + txtf_patient.getText());
          confAlert.show();

          populateMedicineRequests();
          break;
        }
      }
    } catch (DBException | NumberFormatException e) {
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

  public void logout() throws IOException, DBException {
    mainApp.switchScene("views/newHomePage.fxml", singleton);
    LoginDB.logout();
  }
}
