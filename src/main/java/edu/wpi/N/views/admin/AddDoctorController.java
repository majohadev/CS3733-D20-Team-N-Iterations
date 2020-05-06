package edu.wpi.N.views.admin;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DoctorDB;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.DbNode;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;

public class AddDoctorController {

  @FXML JFXTextField txtf_docuser;
  @FXML JFXTextField txtf_docpass;
  @FXML JFXTextField txtf_docfn;
  @FXML JFXTextField txtf_docln;
  @FXML JFXTextField txtf_docfield;
  @FXML JFXTextField txtf_docid;
  @FXML JFXTextField txtf_docoffice;
  @FXML JFXListView lst_docoffice;

  NewAdminController newAdminController;

  private ObservableList<String> fuzzySearchTextListDoctorOffices =
      FXCollections.observableArrayList();
  private LinkedList<DbNode> fuzzySearchDoctorOffice = new LinkedList<>();
  private LinkedList<DbNode> fuzzySearchNodeList = new LinkedList<>();
  LinkedList<String> longNamesList = new LinkedList<>(); // Stores Floor Node names
  LinkedList<DbNode> allFloorNodes; // stores all the nodes on the floor

  public void setAdminController(NewAdminController adminController) {
    this.newAdminController = adminController;
  }

  @FXML
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

    newAdminController.populateTable();
    txtf_docfn.clear();
    txtf_docln.clear();
    txtf_docfield.clear();
    txtf_docuser.clear();
    txtf_docpass.clear();
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

  public void addOffice() throws DBException {
    int currentSelection = lst_docoffice.getSelectionModel().getSelectedIndex();

    try {
      if (currentSelection == -1) {
        Alert acceptReq = new Alert(Alert.AlertType.ERROR);
        acceptReq.setContentText("Invalid / No Location Selected");
        acceptReq.show();

        return;
      }

      if (!ServiceDB.getEmployee(Integer.parseInt(txtf_docid.getText()))
          .getServiceType()
          .equals("Medicine")) {

        Alert invalidID = new Alert(Alert.AlertType.ERROR);
        invalidID.setContentText("Employee isn't a doctor.");
        invalidID.show();

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

      for (DbNode node : DoctorDB.getDoctor(doctorID).getLoc()) {
        if (node.equals(addOfficeNode)) {
          Alert acceptReq = new Alert(Alert.AlertType.ERROR);
          acceptReq.setContentText("Doctor already works there");
          acceptReq.show();

          return;
        }
      }

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
    } catch (DBException e) {
      Alert acceptReq = new Alert(Alert.AlertType.ERROR);
      acceptReq.setContentText(e.getMessage());
      acceptReq.show();

      return;
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

    try {
      if (txtf_docid.getText().equals("")) {
        Alert invalidID = new Alert(Alert.AlertType.ERROR);
        invalidID.setContentText("Invalid ID");
        invalidID.show();

        return;
      }

      if (!ServiceDB.getEmployee(Integer.parseInt(txtf_docid.getText()))
          .getServiceType()
          .equals("Medicine")) {

        Alert invalidID = new Alert(Alert.AlertType.ERROR);
        invalidID.setContentText("Employee isn't a doctor.");
        invalidID.show();

        return;
      }

      int currentSelection = lst_docoffice.getSelectionModel().getSelectedIndex();
      int docid = Integer.parseInt(txtf_docid.getText());

      if (currentSelection == -1) {
        Alert acceptReq = new Alert(Alert.AlertType.ERROR);
        acceptReq.setContentText("Invalid / No Location Selected");
        acceptReq.show();

        return;
      }

      DbNode removeOfficeNode = fuzzySearchNodeList.get(currentSelection);
      System.out.println(removeOfficeNode.getLongName());

      for (DbNode node : DoctorDB.getDoctor(docid).getLoc()) {
        if (node.getLongName().equals(removeOfficeNode.getLongName())) {

          DoctorDB.removeOffice(Integer.parseInt(txtf_docid.getText()), removeOfficeNode);

          Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
          acceptReq.setContentText("Office " + removeOfficeNode.getLongName() + " removed.");
          acceptReq.show();

          return;
        }
      }
    } catch (DBException e) {
      Alert acceptReq = new Alert(Alert.AlertType.ERROR);
      acceptReq.setContentText(e.getMessage());
      acceptReq.show();

      return;
    }
    Alert acceptReq = new Alert(Alert.AlertType.ERROR);
    acceptReq.setContentText("Doctor does not work there");
    acceptReq.show();

    txtf_docid.clear();
    txtf_docoffice.clear();
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
}
