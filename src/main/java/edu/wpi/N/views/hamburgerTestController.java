package edu.wpi.N.views;

import com.jfoenix.controls.*;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.Algorithm;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.entities.employees.Doctor;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.SneakyThrows;

public class hamburgerTestController implements Controller, Initializable {

  private StateSingleton singleton;

  @Override
  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  private App mainApp = null;
  final float IMAGE_WIDTH = 2475;
  final float IMAGE_HEIGHT = 1485;
  final float MAP_WIDTH = 1678;
  final float MAP_HEIGHT = 1010;
  final float HORIZONTAL_SCALE = (MAP_WIDTH) / IMAGE_WIDTH;
  final float VERTICAL_SCALE = (MAP_HEIGHT) / IMAGE_HEIGHT;
  @FXML ImageView img_map;
  @FXML Pane pn_display;
  @FXML Pane pn_changeFloor;
  @FXML JFXTextField txt_firstLocation;
  @FXML JFXTextField txt_secondLocation;
  @FXML JFXListView lst_firstLocation;
  @FXML JFXListView lst_secondLocation;
  // @FXML JFXButton btn_searchdoc;
  @FXML TextField txtf_doctorname;
  @FXML ListView lst_doctornames;
  // @FXML Button btn_searchdoc;
  @FXML ListView lst_doctorlocations;
  @FXML Button btn_findpathdoc;
  @FXML JFXButton btn_findPath;
  @FXML JFXButton btn_home;
  @FXML private JFXButton btn_floors, btn_floor1, btn_floor2, btn_floor3, btn_floor4, btn_floor5;
  @FXML TitledPane pn_locationSearch;
  @FXML Accordion acc_search;
  @FXML Text txt_description;
  @FXML JFXCheckBox handicapp1;
  @FXML JFXCheckBox handicapp2;

  private final int DEFAULT_FLOOR = 1;
  private final String DEFAULT_BUILDING = "FAULKNER";
  private int currentFloor;
  private String currentBuilding;

  // sphagetting code I guesss
  private ArrayList<String> directions;

  HashMap<String, DbNode> stringNodeConversion = new HashMap<>();
  LinkedList<String> allLongNames = new LinkedList<>();
  LinkedList<DbNode> allFloorNodes = new LinkedList<>();
  private ObservableList<String> fuzzySearchDoctorList = FXCollections.observableArrayList();
  private LinkedList<Doctor> searchedDoc = new LinkedList<>();
  private LinkedList<DbNode> doctorNodes = new LinkedList<>();
  JFXNodesList nodesList;
  LinkedList<DbNode> pathNodes;
  String[] imgPaths =
      new String[] {
        "/edu/wpi/N/images/Floor1TeamN.png",
        "/edu/wpi/N/images/Floor2TeamN.png",
        "/edu/wpi/N/images/Floor3TeamN.png",
        "/edu/wpi/N/images/Floor4TeamN.png",
        "/edu/wpi/N/images/Floor5TeamN.png"
      };

  private enum Mode {
    NO_STATE,
    PATH_STATE;
  }

  Mode mode;

  @SneakyThrows
  @Override
  public void initialize(URL location, ResourceBundle resourceBundle) {
    try {
      initializeChangeFloorButtons();
      this.currentFloor = DEFAULT_FLOOR;
      this.currentBuilding = DEFAULT_BUILDING;
      this.mode = Mode.NO_STATE;
      this.allFloorNodes = MapDB.allNodes();
      initializeConversions();
      defaultKioskNode();
      acc_search.setExpandedPane(pn_locationSearch);
    } catch (Exception ex) {
      ex.printStackTrace();
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText(
          "Map view couldn't be opened. Make sure to upload Nodes and Edges as CSVs");
      errorAlert.showAndWait();
    }
  }

  private void initializeConversions() {
    for (DbNode node : allFloorNodes) {
      stringNodeConversion.put(node.getLongName(), node);
      allLongNames.add(node.getLongName());
    }
  }

  public void onSearchFirstLocation(KeyEvent inputMethodEvent) throws DBException {
    LinkedList<DbNode> fuzzySearchNodeList;
    ObservableList<String> fuzzySearchTextList;
    LinkedList<String> fuzzySearchStringList = new LinkedList<>();

    String currentText = txt_firstLocation.getText();
    fuzzySearchNodeList = FuzzySearchAlgorithm.suggestLocations(currentText);
    if (fuzzySearchNodeList != null) {
      for (DbNode node : fuzzySearchNodeList) {
        fuzzySearchStringList.add(node.getLongName());
      }
      fuzzySearchTextList = FXCollections.observableList(fuzzySearchStringList);
    } else fuzzySearchTextList = FXCollections.observableList(this.allLongNames);
    lst_firstLocation.setItems(fuzzySearchTextList);
  }

  public void onSearchSecondLocation(KeyEvent inputMethodEvent) throws DBException {
    LinkedList<DbNode> fuzzySearchNodeList;
    ObservableList<String> fuzzySearchTextList;
    LinkedList<String> fuzzySearchStringList = new LinkedList<>();

    String currentText = txt_secondLocation.getText();
    fuzzySearchNodeList = FuzzySearchAlgorithm.suggestLocations(currentText);
    if (fuzzySearchNodeList != null) {
      for (DbNode node : fuzzySearchNodeList) {
        fuzzySearchStringList.add(node.getLongName());
      }
      fuzzySearchTextList = FXCollections.observableList(fuzzySearchStringList);
    } else fuzzySearchTextList = FXCollections.observableList(this.allLongNames);
    lst_secondLocation.setItems(fuzzySearchTextList);
  }

  @FXML
  private void searchByDoctorTextFill(KeyEvent keyEvent) throws DBException {
    String currentText = txtf_doctorname.getText();
    if (currentText.length() > 1) {
      searchedDoc = FuzzySearchAlgorithm.suggestDoctors(currentText);
      LinkedList<String> fuzzySearchStringList = new LinkedList<>();
      for (Doctor doctors : searchedDoc) {
        fuzzySearchStringList.add(doctors.getName());
      }
      fuzzySearchDoctorList = FXCollections.observableList(fuzzySearchStringList);
      lst_doctornames.setItems(fuzzySearchDoctorList);
    }
  }

  @FXML
  private void onFindDoctorClicked(MouseEvent event) throws Exception {
    int currentSelection = lst_doctornames.getSelectionModel().getSelectedIndex();
    System.out.println(currentSelection);
    Doctor selectedDoc = searchedDoc.get(currentSelection);
    System.out.println(selectedDoc);
    doctorNodes = selectedDoc.getLoc();
    LinkedList<String> docNames = new LinkedList<>();
    for (DbNode nodes : doctorNodes) {
      docNames.add(nodes.getLongName());
    }
    ObservableList<String> doctorsLocations = FXCollections.observableList(docNames);
    lst_doctorlocations.setItems(doctorsLocations);
  }

  public void onBtnPathfindClicked(MouseEvent event) throws Exception {
    this.mode = Mode.PATH_STATE;
    pn_display.getChildren().removeIf(node -> node instanceof Line);
    enableAllFloorButtons();
    String firstSelection = (String) lst_firstLocation.getSelectionModel().getSelectedItem();
    String secondSelection = (String) lst_secondLocation.getSelectionModel().getSelectedItem();
    jumpToFloor(imgPaths[stringNodeConversion.get(firstSelection).getFloor() - 1]);
    currentFloor = stringNodeConversion.get(firstSelection).getFloor();
    try {
      findPath(stringNodeConversion.get(firstSelection), stringNodeConversion.get(secondSelection));
    } catch (NullPointerException e) {
      displayErrorMessage("Path does not exist");
      return;
    }
  }

  @FXML
  private void onDoctorPathFindClicked(MouseEvent event) throws Exception {
    this.mode = Mode.PATH_STATE;
    pn_display.getChildren().removeIf(node -> node instanceof Line);
    enableAllFloorButtons();
    String firstSelection = (String) lst_firstLocation.getSelectionModel().getSelectedItem();
    String secondSelection = (String) lst_doctorlocations.getSelectionModel().getSelectedItem();
    jumpToFloor(imgPaths[stringNodeConversion.get(firstSelection).getFloor() - 1]);
    currentFloor = stringNodeConversion.get(firstSelection).getFloor();
    try {
      findPath(stringNodeConversion.get(firstSelection), stringNodeConversion.get(secondSelection));
    } catch (NullPointerException e) {
      displayErrorMessage("Path does not exist");
      return;
    }
  }

  private void enableAllFloorButtons() {
    for (int i = 1; i < nodesList.getChildren().size(); i++) {
      JFXButton btn = (JFXButton) nodesList.getChildren().get(i);
      btn.setDisable(false);
    }
  }

  private void findPath(DbNode node1, DbNode node2) throws DBException {
    boolean handicap = false;
    if (handicapp1.isSelected() || handicapp2.isSelected()) {
      handicap = true;
    }
    if (node1.getFloor() <= node2.getFloor()) {
      Path path;
      Algorithm myAStar = new Algorithm();
      try {
        path = myAStar.findPath(node1, node2, handicap);
        ArrayList<String> directions = path.getDirections();
        for (String s : directions) {
          System.out.println(s);
        }
        System.out.println("Start angle " + path.getStartAngle(MapDB.getKioskAngle()));
      } catch (NullPointerException e) {
        displayErrorMessage("The path does not exist");
        return;
      }
      pathNodes = path.getPath();
    } else {
      Path path = singleton.savedAlgo.findPath(node2, node1, handicap);
      pathNodes = path.getPath();
      ArrayList<String> directions = path.getDirections();
      for (String s : directions) {
        System.out.println(s);
      }
      System.out.println("Start angle " + path.getStartAngle(MapDB.getKioskAngle()));
    }
    disableNonPathFloors(pathNodes);
    drawPath(pathNodes);
    // set textual decriptions
    setTextDecription(new Path(pathNodes));
  }

  private void disableNonPathFloors(LinkedList<DbNode> pathNodes) {
    LinkedList<Integer> activeFloors = new LinkedList();
    for (DbNode node : pathNodes) {
      if (!(node.getNodeType().equals("ELEV") || node.getNodeType().equals("STAI"))) {
        if (!activeFloors.contains(node.getFloor())) {
          activeFloors.add(node.getFloor());
        }
      }
    }
    for (int i = 1; i < nodesList.getChildren().size(); i++) {
      JFXButton btn = (JFXButton) nodesList.getChildren().get(i);
      if (!activeFloors.contains(Integer.parseInt(btn.getText()))) {
        btn.setDisable(true);
      }
    }
  }

  private void drawPath(LinkedList<DbNode> pathNodes) {
    DbNode firstNode;
    DbNode secondNode;
    for (int i = 0; i < pathNodes.size() - 1; i++) {
      firstNode = pathNodes.get(i);
      secondNode = pathNodes.get(i + 1);
      if (firstNode.getFloor() == currentFloor && secondNode.getFloor() == currentFloor) {
        Line line =
            new Line(
                scaleX(firstNode.getX()),
                scaleY(firstNode.getY()),
                scaleX(secondNode.getX()),
                scaleY(secondNode.getY()));
        styleLine(line);
        pn_display.getChildren().add(line);
      }
    }
  }

  public void onBtnResetPathClicked() throws DBException {
    mode = Mode.NO_STATE;
    defaultKioskNode();
    enableAllFloorButtons();
    pn_display.getChildren().removeIf(node -> node instanceof Line);
    txt_firstLocation.clear();
    txt_secondLocation.clear();
    lst_firstLocation.getItems().clear();
    lst_secondLocation.getItems().clear();
  }

  private double scaleX(double x) {
    return x * HORIZONTAL_SCALE;
  }

  private double scaleY(double y) {
    return y * VERTICAL_SCALE;
  }

  public void initializeChangeFloorButtons() throws DBException {
    // MapDB.setKiosk("NSERV00301", 0);
    // MapDB.setKiosk("NSERV00103", 0);
    btn_floors = new JFXButton("Floors");
    btn_floor1 = new JFXButton("1");
    btn_floor2 = new JFXButton("2");
    btn_floor3 = new JFXButton("3");
    btn_floor4 = new JFXButton("4");
    btn_floor5 = new JFXButton("5");
    btn_floors.setButtonType(JFXButton.ButtonType.RAISED);
    btn_floor1.setButtonType(JFXButton.ButtonType.RAISED);
    btn_floor2.setButtonType(JFXButton.ButtonType.RAISED);
    btn_floor3.setButtonType(JFXButton.ButtonType.RAISED);
    btn_floor4.setButtonType(JFXButton.ButtonType.RAISED);
    btn_floor5.setButtonType(JFXButton.ButtonType.RAISED);
    btn_floors
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floors.getStyleClass().addAll("animated-option-button");
    btn_floor1
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floor1.getStyleClass().addAll("animated-option-button");
    btn_floor1.setOnMouseClicked(
        e -> {
          currentFloor = 1;
          try {
            setFloorImg("/edu/wpi/N/images/Floor1TeamN.png");
          } catch (DBException ex) {
            ex.printStackTrace();
          }
        });
    btn_floor2
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floor2.getStyleClass().addAll("animated-option-button");
    btn_floor2.setOnMouseClicked(
        e -> {
          currentFloor = 2;
          try {
            setFloorImg("/edu/wpi/N/images/Floor2TeamN.png");
          } catch (DBException ex) {
            ex.printStackTrace();
          }
        });
    btn_floor3
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floor3.getStyleClass().addAll("animated-option-button");
    btn_floor3.setOnMouseClicked(
        e -> {
          currentFloor = 3;
          try {
            setFloorImg("/edu/wpi/N/images/Floor3TeamN.png");
          } catch (DBException ex) {
            ex.printStackTrace();
          }
        });
    btn_floor4
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floor4.getStyleClass().addAll("animated-option-button");
    btn_floor4.setOnMouseClicked(
        e -> {
          currentFloor = 4;
          try {
            setFloorImg("/edu/wpi/N/images/Floor4TeamN.png");
          } catch (DBException ex) {
            ex.printStackTrace();
          }
        });
    btn_floor5
        .getStylesheets()
        .addAll(getClass().getResource("/edu/wpi/N/views/MapDisplayFloors.css").toExternalForm());
    btn_floor5.getStyleClass().addAll("animated-option-button");
    btn_floor5.setOnMouseClicked(
        e -> {
          currentFloor = 5;
          try {
            setFloorImg("/edu/wpi/N/images/Floor5TeamN.png");
          } catch (DBException ex) {
            ex.printStackTrace();
          }
        });
    nodesList = new JFXNodesList();
    nodesList.addAnimatedNode(btn_floors);
    nodesList.addAnimatedNode(btn_floor5);
    nodesList.addAnimatedNode(btn_floor4);
    nodesList.addAnimatedNode(btn_floor3);
    nodesList.addAnimatedNode(btn_floor2);
    nodesList.addAnimatedNode(btn_floor1);
    nodesList.setSpacing(20);
    pn_changeFloor.getChildren().add(nodesList);
  }

  private void setFloorImg(String path) throws DBException {
    pn_display.getChildren().removeIf(node -> node instanceof Line);
    if (mode == Mode.PATH_STATE) {
      drawPath(pathNodes);
    }
    if (mode == Mode.NO_STATE) {
      defaultKioskNode();
    }
    Image img = new Image(getClass().getResourceAsStream(path));
    img_map.setImage(img);
  }

  private void jumpToFloor(String path) throws DBException {
    Image img = new Image(getClass().getResourceAsStream(path));
    img_map.setImage(img);
  }

  /**
   * Finds and draws path to the nearest bathroom
   *
   * @param e
   */
  @FXML
  private void findPathToBathroom(MouseEvent e) throws DBException {
    try {
      this.mode = Mode.PATH_STATE;
      pn_display.getChildren().removeIf(node -> node instanceof Line);
      enableAllFloorButtons();
      String startSelection = (String) lst_firstLocation.getSelectionModel().getSelectedItem();
      DbNode startNode = stringNodeConversion.get(startSelection);
      Path pathToBathroom = singleton.savedAlgo.findQuickAccess(startNode, "REST");
      drawPath(pathToBathroom.getPath());
      // set textual decriptions
      setTextDecription(pathToBathroom);
    } catch (Exception ex) {
      ex.printStackTrace();
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText("Path to bathroom wasn't found");
      errorAlert.showAndWait();
    }
  }

  /**
   * Finds and draws path to the Cafetaria
   *
   * @param e
   */
  @FXML
  private void findPathToCafetaria(MouseEvent e) {
    try {
      this.mode = Mode.PATH_STATE;
      pn_display.getChildren().removeIf(node -> node instanceof Line);
      enableAllFloorButtons();

      boolean handicap = false;
      if (handicapp1.isSelected() || handicapp2.isSelected()) {
        handicap = true;
      }

      String startSelection = (String) lst_firstLocation.getSelectionModel().getSelectedItem();
      DbNode startNode = stringNodeConversion.get(startSelection);

      DbNode endNode = MapDB.getNode("MRETL00203");

      if (endNode != null) {
        Path pathToCafetaria = singleton.savedAlgo.findPath(startNode, endNode, handicap);
        drawPath(pathToCafetaria.getPath());
        // set textual descriptions
        setTextDecription(pathToCafetaria);
      }

    } catch (Exception ex) {
      ex.printStackTrace();
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText("Path to cafeteria wasn't found");
      errorAlert.showAndWait();
    }
  }
  /**
   * Finds and draws path to the Starbucks
   *
   * @param e
   */
  @FXML
  private void findPathToStarBucks(MouseEvent e) {
    try {
      this.mode = Mode.PATH_STATE;
      pn_display.getChildren().removeIf(node -> node instanceof Line);
      enableAllFloorButtons();

      boolean handicap = false;
      if (handicapp1.isSelected() || handicapp2.isSelected()) {
        handicap = true;
      }

      String startSelection = (String) lst_firstLocation.getSelectionModel().getSelectedItem();
      DbNode startNode = stringNodeConversion.get(startSelection);

      DbNode endNode = MapDB.getNode("NRETL00201");

      if (endNode != null) {
        Path pathToStarBucks = singleton.savedAlgo.findPath(startNode, endNode, handicap);
        drawPath(pathToStarBucks.getPath());
        // set textual descriptions
        setTextDecription(pathToStarBucks);
      }

    } catch (Exception ex) {
      ex.printStackTrace();
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText("Path to cafeteria wasn't found");
      errorAlert.showAndWait();
    }
  }

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void onBtnHomeClicked() throws IOException {
    mainApp.switchScene("views/newHomePage.fxml", singleton);
  }

  public void displayErrorMessage(String str) {
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    errorAlert.setHeaderText("Invalid input");
    errorAlert.setContentText(str);
    errorAlert.showAndWait();
  }

  /**
   * Function generates and sets textual description label to Textual Descriptions
   *
   * @param path
   */
  private void setTextDecription(Path path) {
    try {
      // Convert the array of textual descriptions to text
      String directionsAsText = "";
      directions = path.getDirections();
      for (String s : directions) {
        directionsAsText += s;
        directionsAsText += "\n";
      }

      // Check to make sure that directionAsText isn't empty
      if (!directionsAsText.equals("")) {
        txt_description.setText(directionsAsText);
      }

    } catch (Exception ex) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText("Textual descriptions could not be generated");
      errorAlert.showAndWait();
    }
  }

  /** Function displays a pop-up window with user's directions */
  @FXML
  private void displayQRCode() throws IOException {
    try {
      Stage stage = new Stage();
      Parent root;
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("qrPopUp.fxml"));
      root = loader.load();
      Scene scene = new Scene(root);
      stage.setScene(scene);

      QrPopUpController controller = (QrPopUpController) loader.getController();
      controller.displayQrCode(directions);

      stage.initModality(Modality.APPLICATION_MODAL);
      stage.show();
    } catch (Exception ex) {
      ex.printStackTrace();
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText("QR code with directions could not be generated");
      errorAlert.showAndWait();
    }
  }

  private void defaultKioskNode() throws DBException {
    LinkedList<String> kiosks = new LinkedList<>();
    if (currentFloor == 1) {
      txt_firstLocation.setText(MapDB.getNode("NSERV00301").getLongName());
      kiosks.add(MapDB.getNode("NSERV00301").getLongName());
      ObservableList<String> textList = FXCollections.observableList(kiosks);
      lst_firstLocation.setItems(textList);
      lst_firstLocation.getSelectionModel().select(0);

    } else if (currentFloor == 3) {
      txt_firstLocation.setText(MapDB.getNode("NSERV00103").getLongName());
      kiosks.add(MapDB.getNode("NSERV00103").getLongName());
      ObservableList<String> textList = FXCollections.observableList(kiosks);
      lst_firstLocation.setItems(textList);
      lst_firstLocation.getSelectionModel().select(0);
    } else {
      txt_firstLocation.clear();
      lst_firstLocation.getItems().clear();
    }
  }

  private void styleLine(Line line) {
    line.setStrokeWidth(5);
    line.setStrokeLineCap(StrokeLineCap.ROUND);
    line.setStrokeLineJoin(StrokeLineJoin.ROUND);
  }
  // Upon clicking find path to location button call this method
  /*    @FXML
  private void onDoctorPathFindClicked(MouseEvent event) throws Exception {
    pn_path.getChildren().removeIf(node -> node instanceof Line);
    int currentSelection = lst_doctorlocations.getSelectionModel().getSelectedIndex();
    DbNode destinationNode = doctorNodes.get(currentSelection);
    if (selectedNodes.size() < 1) selectedNodes.add(defaultNode);
    selectedNodes.add(destinationNode);
    // if (selectedNodes.size() < 2) selectedNodes.add(defaultNode);
    onBtnFindClicked(event);
    selectedNodes.clear();
  }*/
}
