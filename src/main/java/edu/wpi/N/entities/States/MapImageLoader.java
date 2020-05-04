package edu.wpi.N.entities.States;

import java.util.HashMap;
import javafx.scene.image.Image;

public class MapImageLoader {

  private HashMap<String, Image> floorMaps = new HashMap<>();

  /** Loads all map images of the application once */
  public MapImageLoader() {
    storeImage("Faulkner1", "edu/wpi/N/images/map/Floor1Reclor.png");
    storeImage("Faulkner2", "edu/wpi/N/images/map/Floor2TeamN.png");
    storeImage("Faulkner3", "edu/wpi/N/images/map/Floor3TeamN.png");
    storeImage("Faulkner4", "edu/wpi/N/images/map/Floor4SolidBackground.png");
    storeImage("Faulkner5", "edu/wpi/N/images/map/Floor5TeamN.png");
    storeImage("Main F1", "edu/wpi/N/images/map/F1.png");
    storeImage("Main F2", "edu/wpi/N/images/map/F2.png");
    storeImage("Main F3", "edu/wpi/N/images/map/F3.png");
    storeImage("Main Ground", "edu/wpi/N/images/map/ground.png");
    storeImage("Main L1", "edu/wpi/N/images/map/L1.png");
    storeImage("Main L2", "edu/wpi/N/images/map/L2.png");
  }

  /**
   * stores the short name and the specified image path into the application
   *
   * @param shortName the short
   * @param pathName the path of the image
   */
  private void storeImage(String shortName, String pathName) {
    floorMaps.put(
        shortName, new Image(getClass().getClassLoader().getResource(pathName).toString()));
  }

  /**
   * return the image of a map depending on the building and floor
   *
   * @param building the building of the map
   * @param floor the floor of the map
   * @return the image of the map
   */
  public Image getMap(String building, int floor) {
    return floorMaps.get(building + floor);
  }
}
