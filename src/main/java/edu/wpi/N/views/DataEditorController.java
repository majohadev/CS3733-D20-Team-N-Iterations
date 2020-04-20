package edu.wpi.N.views;

import edu.wpi.N.App;
import edu.wpi.N.Main;
import edu.wpi.N.database.CSVParser;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
  public void onDownloadNodesClicked() throws IOException {}

  @FXML
  public void onDownloadEdgesClicked() throws IOException {}

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
