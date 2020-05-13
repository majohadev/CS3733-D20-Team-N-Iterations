package edu.wpi.N.views.mapDisplay;

import edu.wpi.N.App;
import edu.wpi.N.algorithms.Icon;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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
  private HashMap<String, Region> vBoxes = new HashMap<>();

  // Thumb corresponding to the currently displayed floor
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
          building = "Faulkner";
          if (!faulknerFloors.contains(floor)) {
            floorName = "F" + floor;
            faulknerFloors.add(floor);
          } else {
            continue;
          }

        } else {
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
        }

      } else {
        continue;
      }

      if (thumbs.size() > 0) {
        String lastHospital = thumbs.getLast().getHospital();
        if (!lastHospital.equals(building)) {
          if (lastHospital.equals("Faulkner") || building.equals("Faulkner")) {
            thumbs.add(new Thumb("Drive", "Street View", 0));
          }
        }
      }

      thumbs.add(new Thumb(building, floorName, floor));
    }

    hb_layout.getChildren().clear();

    // Fill in thumbnails
    for (int i = 0; i < thumbs.size(); i++) {

      Thumb thumb = thumbs.get(i);

      VBox vbox = new VBox();
      vbox.setAlignment(Pos.CENTER);
      vbox.setStyle("-fx-border-color: #263051; -fx-border-width: 1; -fx-border-radius: 5");

      // Make label

      Label label = new Label();
      label.setTextFill(Color.WHITE);
      label.setTextAlignment(TextAlignment.CENTER);
      label.setFont(Font.font(20));
      label.setStyle("-fx-font-weight: bold;");
      label.setPadding(new Insets(5));
      label.setText(thumb.getHospital() + " - " + thumb.getFloorName());

      // Make image view

      ImageView thumbImgView = new ImageView();
      thumbImgView.setFitHeight(100);
      thumbImgView.setPreserveRatio(true);
      thumbImgView.setSmooth(true);
      thumbImgView.setCache(true);
      thumbImgView.setPickOnBounds(true);
      thumbImgView.setOnMouseClicked(
          e -> {
            try {
              mapDisplay.handleFloorButtonClicked(thumb.getFloorName());
            } catch (DBException ex) {
              ex.printStackTrace();
            }
          });

      Image image;

      if (thumb.getFloorName().equals("Street View")) {
        image = singleton.mapImageLoader.getIcon(Icon.PATH_WHITE);
        thumbImgView.setFitHeight(90);
        label.setText("Drive to " + thumbs.get(i + 1).getHospital());
        vBoxes.put("Drive", vbox);
      } else if (thumb.getHospital().equals("Faulkner")) {
        image = singleton.mapImageLoader.getMap("Faulkner", thumb.getFloorNum());
        vBoxes.put("Faulkner" + thumb.getFloorNum(), vbox);
      } else {
        image = singleton.mapImageLoader.getMap("Main", thumb.getFloorNum());
        vBoxes.put("Main" + thumb.getFloorNum(), vbox);
      }

      thumbImgView.setImage(image);

      vbox.getChildren().addAll(label, thumbImgView);

      // links.put(vbox, thumb);

      // Add new maps to the left of the window to preserve order
      hb_layout.getChildren().add(vbox);
    }

    if (hb_layout.getChildren().size() > 0) {
      highlightBox(hb_layout.getChildren().get(0));
    }
  }

  private void highlightBox(Node element) {
    if (element instanceof Region) {

      Region region = (Region) element;

      if (currentBox != null) {
        currentBox.setStyle("-fx-border-color: #263051; -fx-border-width: 1; -fx-border-radius: 5");
      }

      String style = "-fx-border-color: white; -fx-border-width: 5; -fx-border-radius: 5";
      region.setStyle(style);
      currentBox = region;
    }
  }

  public void pickThumbnail(String key) {

    if (vBoxes.get(key) != null) {
      highlightBox(vBoxes.get(key));
    } else {
      System.out.println("Couldn't find " + key);
    }
  }
}
