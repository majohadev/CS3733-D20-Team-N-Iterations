package edu.wpi.N.views.admin;

import edu.wpi.N.App;
import edu.wpi.N.Main;
import edu.wpi.N.database.*;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.entities.employees.Employee;
import edu.wpi.N.views.Controller;
import java.io.*;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

public class DataEditorController implements Controller {
  private StateSingleton singleton;

  App mainApp = null;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @FXML Button btn_done;

  @FXML Label lbl_filePath;
  @FXML Label lbl_filePath_edges;
  @FXML Label lbl_filePath_employees;
  @FXML Label lbl_filePath_detail;
  @FXML Label lbl_filePath_hitbox;

  final String DEFAULT_NODES = "/edu/wpi/N/csv/newNodes.csv";
  final String DEFAULT_PATHS = "/edu/wpi/N/csv/newEdges.csv";
  final String DEFAULT_EMPLOYEES = "/edu/wpi/N/csv/Employees.csv";
  final String DEFAULT_DETAIL = "/edu/wpi/N/csv/Detail.csv";
  final String DEFAULT_HITBOXES = "/edu/wpi/N/csv/hitBoxesCompleteBuilding.csv";
  final InputStream INPUT_NODES_DEFAULT = Main.class.getResourceAsStream(DEFAULT_NODES);
  final InputStream INPUT_EDGES_DEFAULT = Main.class.getResourceAsStream(DEFAULT_PATHS);
  final InputStream INPUT_EMPLOYEES_DEFAULT = Main.class.getResourceAsStream(DEFAULT_EMPLOYEES);
  final InputStream INPUT_HITBOXES_DEFAULT = Main.class.getResourceAsStream(DEFAULT_HITBOXES);
  final InputStream INPUT_DETAIL_DEFAULT = Main.class.getResourceAsStream(DEFAULT_DETAIL);

  // Inject singleton
  public DataEditorController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  public void initialize() {
    lbl_filePath.setText(DEFAULT_NODES);
    lbl_filePath_edges.setText(DEFAULT_PATHS);
    lbl_filePath_employees.setText(DEFAULT_EMPLOYEES);
    lbl_filePath_detail.setText(DEFAULT_DETAIL);
    // onUploadDetailClicked();
  }

  @FXML
  public void onSelectClicked(MouseEvent event) {
    FileChooser fc = new FileChooser();
    fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    File selectedFile = fc.showOpenDialog(null);
    if (selectedFile != null) {
      lbl_filePath.setText(selectedFile.getAbsolutePath());
      lbl_filePath.setDisable(false);
    } else {
      System.out.println("The file is invalid");
    }
  }

  @FXML
  public void onSelectEdgesClicked(MouseEvent event) {
    FileChooser fc = new FileChooser();
    fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    File selectedFile = fc.showOpenDialog(null);
    if (selectedFile != null) {
      lbl_filePath_edges.setText(selectedFile.getAbsolutePath());
      lbl_filePath_edges.setDisable(false);
    } else {
      System.out.println("The file is invalid");
    }
  }

  @FXML
  public void onSelectEmployeesClicked(MouseEvent event) {
    FileChooser fc = new FileChooser();
    fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    File selectedFile = fc.showOpenDialog(null);
    if (selectedFile != null) {
      lbl_filePath_employees.setText(selectedFile.getAbsolutePath());
      lbl_filePath_employees.setDisable(false);
    } else {
      System.out.println("The file is invalid");
    }
  }

  @FXML
  public void onSelectHitboxesClicked(MouseEvent event) {
    FileChooser fc = new FileChooser();
    fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    File selectedFile = fc.showOpenDialog(null);
    if (selectedFile != null) {
      lbl_filePath_hitbox.setText(selectedFile.getAbsolutePath());
      lbl_filePath_hitbox.setDisable(false);
    } else {
      System.out.println("The file is invalid");
    }
  }

  @FXML
  public void onSelectDirectoryClicked(MouseEvent event) {
    FileChooser fc = new FileChooser();
    fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    File selectedFile = fc.showOpenDialog(null);
    if (selectedFile != null) {
      lbl_filePath_detail.setText(selectedFile.getAbsolutePath());
      lbl_filePath_detail.setDisable(false);
    } else System.out.println("The file is invalid");
  }

  /** Loads selected hitbox csv */
  @FXML
  public void onUploadHitboxClicked() {
    try {
      // Upload Hitboxes
      String path = lbl_filePath_hitbox.getText();
      if (path.equals(DEFAULT_HITBOXES)) {
        CSVParser.parseCSVHitBoxes(INPUT_HITBOXES_DEFAULT);
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setContentText("Your Hitbox CSV File Has Been Successfully Uploaded");
        confirmAlert.show();
      } else {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setContentText("Your Hitbox CSV File Has Been Successfully Uploaded");
        confirmAlert.show();
        CSVParser.parseCSVDetailFromPath(path);
        // CSVParser.parseCSVHitBoxesFromPath(path);
      }
    } catch (Exception e) {
      e.printStackTrace();
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText(
          "Couldn't load hitbox file. Make sure to select correct file. Make sure to uploaded Nodes first");
      errorAlert.showAndWait();
    }
  }

  @FXML
  public void onUploadNodesEdgesClicked() throws IOException, DBException {
    MapDB.clearNodes();

    String path = lbl_filePath.getText();
    if (path.equals(DEFAULT_NODES)) {
      CSVParser.parseCSV(INPUT_NODES_DEFAULT);
    } else {
      CSVParser.parseCSVfromPath(path);
    }

    // For edges
    String path_edges = lbl_filePath_edges.getText();
    if (path_edges.equals(DEFAULT_PATHS)) {
      CSVParser.parseCSV(INPUT_EDGES_DEFAULT);
    } else {
      CSVParser.parseCSVfromPath(path_edges);
    }

    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmAlert.setContentText("Your Nodes and Edges CSV Files Have Been Successfully Uploaded");
    confirmAlert.show();
    // reload map data into singleton
    singleton.savedAlgo.uploadMapData();

    // MapDB.setKiosk("NSERV00301", 180);
  }

  /**
   * Loads selected employees.csv or default
   *
   * @throws IOException
   * @throws DBException
   */
  @FXML
  public void onUploadEmployeesClicked() throws IOException, DBException {
    try {
      // Clear previous employees
      deleteAllEmployees();
      // Upload employees
      String path = lbl_filePath_employees.getText();
      if (path.equals(DEFAULT_EMPLOYEES)) {
        CSVParser.parseCSVEmployees(INPUT_EMPLOYEES_DEFAULT);
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setContentText("Your Employee CSV File Has Been Successfully Uploaded");
        confirmAlert.show();
      } else {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setContentText("Your Employee CSV File Has Been Successfully Uploaded");
        confirmAlert.show();
        CSVParser.parseCSVEmployeesFromPath(path);
      }
    } catch (Exception e) {
      e.printStackTrace();
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText(
          "Couldn't load employee file. Make sure to select correct file. Make sure to uploaded Nodes first");
      errorAlert.showAndWait();
    }
  }

  public void onUploadDetailClicked() {
    try {
      // Clear previous employees
      MapDB.clearDetail();
      // Upload employees
      String path = lbl_filePath_detail.getText();
      // String path = DEFAULT_DETAIL;
      if (path.equals(DEFAULT_DETAIL)) {
        CSVParser.parseDetail(INPUT_DETAIL_DEFAULT);
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setContentText("Your Directory CSV File Has Been Successfully Uploaded");
        confirmAlert.show();
      } else {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setContentText("Your Directory CSV File Has Been Successfully Uploaded");
        confirmAlert.show();
        CSVParser.parseCSVDetailFromPath(path);
      }
    } catch (Exception e) {
      e.printStackTrace();
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText(
          "Couldn't load directory file. Make sure to select correct file. Make sure to uploaded Nodes first");
      errorAlert.showAndWait();
    }
  }

  /** Function removes all employees from database, including doctors */
  private void deleteAllEmployees() {
    try {
      for (Employee employee : ServiceDB.getEmployees()) {
        ServiceDB.removeEmployee(employee.getID());
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText("Clear previous employees. Please try again later");
      errorAlert.showAndWait();
    }
  }

  @FXML
  public void onDownloadNodesClicked() throws IOException, DBException {

    FileChooser fileChooser = new FileChooser();

    // Set extension filter for csv files
    FileChooser.ExtensionFilter extFilter =
        new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
    fileChooser.getExtensionFilters().add(extFilter);

    // Show save file dialog
    File file = fileChooser.showSaveDialog(null);

    if (file != null) {
      FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(), true);
      BufferedWriter csvWriter = new BufferedWriter(fileWriter);

      // nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName,teamAssigned
      csvWriter.append("nodeID");
      csvWriter.append(",");
      csvWriter.append("xcoord");
      csvWriter.append(",");
      csvWriter.append("ycoord");
      csvWriter.append(",");
      csvWriter.append("floor");
      csvWriter.append(",");
      csvWriter.append("building");
      csvWriter.append(",");
      csvWriter.append("nodeType");
      csvWriter.append(",");
      csvWriter.append("longName");
      csvWriter.append(",");
      csvWriter.append("shortName");
      csvWriter.append(",");
      csvWriter.append("teamAssigned");
      csvWriter.append("\n");

      LinkedList<DbNode> csvNodeList = MapDB.allNodes();

      for (int index = 0; index < csvNodeList.size(); index++) {
        DbNode indexNode = csvNodeList.get(index);
        csvWriter.append(indexNode.getNodeID());
        csvWriter.append(",");
        csvWriter.append(Integer.toString(indexNode.getX()));
        csvWriter.append(",");
        csvWriter.append(Integer.toString(indexNode.getY()));
        csvWriter.append(",");
        csvWriter.append(Integer.toString(indexNode.getFloor()));
        csvWriter.append(",");
        csvWriter.append(indexNode.getBuilding());
        csvWriter.append(",");
        csvWriter.append(indexNode.getNodeType());
        csvWriter.append(",");
        csvWriter.append(indexNode.getLongName());
        csvWriter.append(",");
        csvWriter.append(indexNode.getShortName());
        csvWriter.append(",");
        csvWriter.append(indexNode.getTeamAssigned());
        csvWriter.append("\n");
      }
      csvWriter.flush();
      csvWriter.close();
    }
    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmAlert.setContentText("Your CSV File Has Been Successfully Downloaded");
    confirmAlert.show();
  }

  @FXML
  public void onDownloadEdgesClicked() throws IOException, DBException {

    FileChooser fileChooser = new FileChooser();

    // Set extension filter for csv files
    FileChooser.ExtensionFilter extFilter =
        new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
    fileChooser.getExtensionFilters().add(extFilter);

    // Show save file dialog
    File file = fileChooser.showSaveDialog(null);

    if (file != null) {
      FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(), true);
      BufferedWriter csvWriter = new BufferedWriter(fileWriter);
      csvWriter.append("edgeID");
      csvWriter.append(",");
      csvWriter.append("startNode");
      csvWriter.append(",");
      csvWriter.append("endNode");
      csvWriter.append("\n");

      LinkedList<String> edgesList = MapDB.exportEdges();

      for (String str : edgesList) {
        csvWriter.append(str);
        csvWriter.append("\n");
      }

      csvWriter.flush();
      csvWriter.close();
    }

    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmAlert.setContentText("Your CSV File Has Been Successfully Downloaded");
    confirmAlert.show();
  }

  // TODO: change name of the function or a button to match
  @FXML
  public void onDoneClicked() throws IOException {
    // mainApp.switchScene("views/newHomePage.fxml", singleton);
  }

  /**
   * Function loads and displays default paths to Nodes and Edges
   *
   * @param event
   */
  @FXML
  private void onDefaultClicked(MouseEvent event) {
    lbl_filePath.setText(DEFAULT_NODES);
    lbl_filePath_edges.setText(DEFAULT_PATHS);
  }

  /**
   * Function loads and displays default path to Employees.csv
   *
   * @param event
   */
  @FXML
  private void onDefaultEmployeesClicked(MouseEvent event) {
    lbl_filePath_employees.setText(DEFAULT_EMPLOYEES);
  }

  /**
   * Function loads and displays default path to hitBoxesCompleteBuilding.csv
   *
   * @param event
   */
  @FXML
  private void onDefaultHitboxesClicked(MouseEvent event) {
    lbl_filePath_hitbox.setText(DEFAULT_HITBOXES);
  }

  @FXML
  private void onDefaultDepartmentClicked(MouseEvent event) {
    lbl_filePath_detail.setText(DEFAULT_DETAIL);
  }
}
