package edu.wpi.N.entities.States;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import java.util.HashMap;
import java.util.LinkedList;
import javafx.scene.image.Image;

public class MapDataStorage {

  private int numOfFloors = 5;

  // private LinkedList<HashBiMap<UIDispNode, DbNode>> allFloors = new LinkedList<>();
  private LinkedList<DbNode> allDbNodes = new LinkedList<DbNode>();

  private HashMap<String, Image> floorMaps = new HashMap<>();

  public MapDataStorage() {

    floorMaps.put(
        "Floor1",
        new Image(
            getClass()
                .getClassLoader()
                .getResource("edu/wpi/N/images/map/Floor1Reclor.png")
                .toString()));
    floorMaps.put(
        "Floor2",
        new Image(
            getClass()
                .getClassLoader()
                .getResource("edu/wpi/N/images/map/Floor2TeamN.png")
                .toString()));
    floorMaps.put(
        "Floor3",
        new Image(
            getClass()
                .getClassLoader()
                .getResource("edu/wpi/N/images/map/Floor3TeamN.png")
                .toString()));
    floorMaps.put(
        "Floor4",
        new Image(
            getClass()
                .getClassLoader()
                .getResource("edu/wpi/N/images/map/Floor4SolidBackground.png")
                .toString()));
    floorMaps.put(
        "Floor5",
        new Image(
            getClass()
                .getClassLoader()
                .getResource("edu/wpi/N/images/map/Floor5TeamN.png")
                .toString()));
  }

  /**
   * Gets the image of necessary building and floor number
   *
   * @param building: building name
   * @param floorNum: floor number
   * @return: Image of specified building and floor number
   */
  public Image getMap(String building, int floorNum) {
    return floorMaps.get("Floor" + floorNum);
  }

  /** Gets all the Nodes and Edges from database and sets the necessary attribute */
  public void refreshMapData() {
    try {
      allDbNodes = MapDB.allNodes();
    } catch (DBException e) {
      e.printStackTrace();
    }
  }

  public LinkedList<DbNode> getAllDbNodes() {
    return allDbNodes;
  }
}
