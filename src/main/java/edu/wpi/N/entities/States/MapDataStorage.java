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
                .getResource("edu/wpi/N/images/Floor1Reclor.png")
                .toString()));
    floorMaps.put(
        "Floor2",
        new Image(
            getClass()
                .getClassLoader()
                .getResource("edu/wpi/N/images/Floor2TeamN.png")
                .toString()));
    floorMaps.put(
        "Floor3",
        new Image(
            getClass()
                .getClassLoader()
                .getResource("edu/wpi/N/images/Floor3TeamN.png")
                .toString()));
    floorMaps.put(
        "Floor4",
        new Image(
            getClass()
                .getClassLoader()
                .getResource("edu/wpi/N/images/Floor4SolidBackground.png")
                .toString()));
    floorMaps.put(
        "Floor5",
        new Image(
            getClass()
                .getClassLoader()
                .getResource("edu/wpi/N/images/Floor5TeamN.png")
                .toString()));
  }

  public Image getMap(String building, int floorNum) {
    return floorMaps.get("Floor" + floorNum);
  }

  public void refreshAllNodes() {
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
