package edu.wpi.N.views.mapDisplay;

import edu.wpi.N.App;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class MapThumbnailsController implements Controller {

    App mainApp;
    private StateSingleton singleton;

    // Arranges the thumbnails
    @FXML HBox hb_layout;

    // Thumbnail viewports
    private HashMap<String, ImageView> thumbs = new HashMap<>();

    @Override
    public void setMainApp(App mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void initialize() {

    }

    public void setThumbs(LinkedList<DbNode> nodes) {

        thumbs.clear();

        ArrayList<Integer> faulknerFloors = new ArrayList<>();
        ArrayList<Integer> mainFloors = new ArrayList<>();

        for (DbNode node : nodes) {

            String building = node.getBuilding();
            int floor = node.getFloor();

            if (building.equals("Faulkner")) {
                if (!faulknerFloors.contains(floor)) {

                    faulknerFloors.add(floor);
                    ImageView newThumb = new ImageView();
                    newThumb.setImage(singleton.mapImageLoader.getMap(building, floor));
                    newThumb.setFitHeight(100);
                    String key = building + "\nFloor " + floor;
                    thumbs.put(key, newThumb);
                }
            } else if (node.getBuilding().equals("Main")) {
                if (!mainFloors.contains(node.getFloor())) {

                    mainFloors.add(floor);
                    ImageView newThumb = new ImageView();
                    newThumb.setImage(singleton.mapImageLoader.getMap(building, floor));
                    newThumb.setFitHeight(100);
                    String key = building + "\nFloor " + floor;
                    thumbs.put(key, newThumb);
                }
            }
        }

        for (ImageView thumb : thumbs.values()) {
            hb_layout.getChildren().add(thumb);
        }

    }


}
