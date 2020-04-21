package edu.wpi.N.views;

import edu.wpi.N.App;
import edu.wpi.N.Main;
import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import edu.wpi.N.entities.DbNode;
import java.io.*;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

public class DataEditorController implements Controller {
  App mainApp = null;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @FXML Button btn_select;
  @FXML Button btn_done;
  @FXML Label lbl_filePath;
  @FXML Button btn_select_edges;
  @FXML Label lbl_filePath_edges;
  @FXML Button btn_default;
  @FXML Button btn_uploadnode;
  @FXML Button btn_uploadedge;
  @FXML Button btn_downloadnode;
  @FXML Button btn_downloadedge;

  final String DEFAULT_NODES = "csv/MapEnodes.csv";
  final String DEFAULT_PATHS = "csv/MapEedges.csv";
  final InputStream INPUT_NODES_DEFAULT = Main.class.getResourceAsStream(DEFAULT_NODES);;
  final InputStream INPUT_EDGES_DEFAULT = Main.class.getResourceAsStream(DEFAULT_PATHS);

  public void initialize() {
    lbl_filePath.setText(DEFAULT_NODES);
    lbl_filePath_edges.setText(DEFAULT_PATHS);
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
  public void onUploadNodesClicked() throws IOException, DBException {

    DbController.clearNodes();

    String path = lbl_filePath.getText();
    if (path.equals(DEFAULT_NODES)) {
      CSVParser.parseCSV(INPUT_NODES_DEFAULT);
    } else {
      CSVParser.parseCSVfromPath(path);
    }
  }

  @FXML
  public void onUploadEdgesClicked() throws IOException {

    // For edges
    String path_edges = lbl_filePath_edges.getText();
    if (path_edges.equals(DEFAULT_PATHS)) {
      CSVParser.parseCSV(INPUT_EDGES_DEFAULT);
    } else {
      CSVParser.parseCSVfromPath(path_edges);
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

      LinkedList<DbNode> csvNodeList = DbController.allNodes();

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

      LinkedList<String> edgesList = DbController.exportEdges();

      for (String str : edgesList) {
        csvWriter.append(str);
        csvWriter.append("\n");
      }

      csvWriter.flush();
      csvWriter.close();
    }
  }

  @FXML
  public void onDoneClicked() throws IOException {
    mainApp.switchScene("views/home.fxml");
  }

  @FXML
  public void onDefaultClicked(MouseEvent event) {
    lbl_filePath.setText(DEFAULT_NODES);
    lbl_filePath_edges.setText(DEFAULT_PATHS);
  }
}
