package edu.wpi.N.views.mapDisplay;

import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.util.ArrayList;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class MapThumbnailsController implements Controller {

  App mainApp;
  private StateSingleton singleton;
  private NewMapDisplayController mapDisplay;

  // Arranges the thumbnails
  @FXML HBox hb_layout;

  private LinkedList<Thumb> thumbs = new LinkedList<>();
  // private LinkedHashMap<Region, Thumb> links = new LinkedHashMap<>();

  // Thumb corresponding to the currently diplayed floor
  private Region currentBox;

  /**
   * constructor for the map thumbnail controller class
   *
   * @param singleton the singleton which is initiated at the beginning of the program
   */
  public MapThumbnailsController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void setMapDisplay(NewMapDisplayController mapDisplay) {
    this.mapDisplay = mapDisplay;
  }

  @FXML
  public void initialize() {}

  private class Thumb {
    private String hospital;
    private String floorName;
    private int floorNum;

    private Thumb(String hospital, String floorName, int floorNum) {

      this.hospital = hospital;
      this.floorName = floorName;
      this.floorNum = floorNum;
    }

    private String getHospital() {
      return this.hospital;
    }

    private String getFloorName() {
      return this.floorName;
    }

    private int getFloorNum() {
      return this.floorNum;
    }
  }

  public void setThumbs(Path path) {

    thumbs.clear();

    ArrayList<Integer> faulknerFloors = new ArrayList<>();
    ArrayList<Integer> mainFloors = new ArrayList<>();

    for (DbNode node : path.getPath()) {

      String building = node.getBuilding();
      int floor = node.getFloor();

      String floorName = "";

      if (!(node.getNodeType().equals("ELEV") || node.getNodeType().equals("STAI"))) {

        if (building.equals("Faulkner")) {
          if (!faulknerFloors.contains(floor)) {
            floorName = "F" + floor;
            faulknerFloors.add(floor);
          } else {
            continue;
          }

        } else if (building.equals("Main")) {
          if (!mainFloors.contains(floor)) {
            switch (floor) {
              case 1:
                floorName = "L1";
                break;
              case 2:
                floorName = "L2";
                break;
              case 3:
                floorName = "G";
                break;
              case 4:
                floorName = "1";
                break;
              case 5:
                floorName = "2";
                break;
              case 6:
                floorName = "3";
                break;
              default:
                System.out.println("Invalid floor thumbnail!");
            }
            mainFloors.add(floor);
          } else {
            continue;
          }

        } else {
          continue;
        }

      } else {
        continue;
      }

      if (!thumbs.getLast().getHospital().equals(building)) {
        thumbs.add(new Thumb("Driving", "Street View", 0));
      }

      thumbs.add(new Thumb(building, floorName, floor));
    }

    hb_layout.getChildren().clear();

    for (Thumb thumb : thumbs) {

      ImageView thumbImgView = new ImageView();
      thumbImgView.setImage(
          singleton.mapImageLoader.getMap(thumb.getHospital(), thumb.getFloorNum()));
      thumbImgView.setFitHeight(100);
      thumbImgView.setPreserveRatio(true);
      thumbImgView.setSmooth(true);
      thumbImgView.setCache(true);
      thumbImgView.setOnMouseClicked(
          e -> {
            try {
              mapDisplay.handleFloorButtonClicked(thumb.getFloorName());
              highlightBox(thumbImgView.getParent());
            } catch (DBException ex) {
              ex.printStackTrace();
            }
          });

      Label label = new Label();
      label.setTextFill(Color.WHITE);
      label.setTextAlignment(TextAlignment.CENTER);
      label.setFont(Font.font(20));
      label.setStyle("-fx-font-weight: bold;");
      label.setPadding(new Insets(5));

      label.setText(thumb.getHospital() + " - " + thumb.getFloorName());

      VBox vbox = new VBox();
      vbox.setAlignment(Pos.CENTER);
      vbox.getChildren().addAll(label, thumbImgView);
      vbox.setStyle("-fx-border-color: #263051;" + "-fx-border-width: 1;");

      // links.put(vbox, thumb);

      // Add new maps to the left of the window to preserve order
      hb_layout.getChildren().add(vbox);
    }

    highlightBox(hb_layout.getChildren().get(0));
  }

  private void highlightBox(Node element) {
    if (element instanceof Region) {

      Region region = (Region) element;

      if (currentBox != null) {
        currentBox.setStyle("-fx-border-color: #263051;" + "-fx-border-width: 1;");
      }

      String style = "-fx-border-color: white;" + "-fx-border-width: 5;";
      region.setStyle(style);
      currentBox = region;
    }
  }
}
