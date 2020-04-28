package edu.wpi.N.controllerData;

import com.google.common.collect.HashBiMap;
import edu.wpi.N.algorithms.AStar;
import edu.wpi.N.algorithms.Algorithm;
import edu.wpi.N.algorithms.BFS;
import edu.wpi.N.algorithms.DFS;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.UIDispNode;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import javafx.scene.image.Image;

public class MapDataStorage {

  private int numOfFloors = 5;

  private LinkedList<HashBiMap<UIDispNode, DbNode>> allFloors = new LinkedList<>();
  // private LinkedList<DbNode> thisFloor = new LinkedList<DbNode>();

  private HashMap<String, Image> floorMaps = new HashMap<>();
  private Algorithm algorithmSetting = new Algorithm();

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

  public Algorithm getAlgorithmSetting() {
    return algorithmSetting;
  }

  public void setDFS() {
    algorithmSetting.setPathFinder(new DFS());
  }

  public void setBFS() {
    algorithmSetting.setPathFinder(new BFS());
  }

  public void setAStar() {
    algorithmSetting.setPathFinder(new AStar());
  }

  public Image getMap(int floorNum) {
    return floorMaps.get("Floor" + floorNum);
  }
}
