package edu.wpi.N.controllerData;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import javafx.scene.image.Image;

public class MapDataStorage {

  private int numOfFloors = 5;

  // private LinkedList<HashBiMap<UIDispNode, DbNode>> allFloors = new LinkedList<>();
  private LinkedList<DbNode> allDbNodes = new LinkedList<DbNode>();

  private HashMap<String, Image> floorMaps = new HashMap<>();

  public MapDataStorage() {
    URL res;
    String name, path;
    for (int i = 1; i <= numOfFloors; i++) {
      name = "Floor" + i;
      path = "edu/wpi/N/images/" + name + "TeamN.png"; // Need to edit this to be more accommodating
      res = getClass().getClassLoader().getResource(path);
      floorMaps.put(name, new Image(res.toString()));
    }
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
