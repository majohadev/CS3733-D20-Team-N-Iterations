package edu.wpi.N.controllerData;

import com.google.common.collect.HashBiMap;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.UINode;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class MapDataStorage {

  // Current floor/node vars
  private String currentBuilding;
  private int currentFloor;
  private int numOfFloors = 5;

  private LinkedList<HashBiMap<UINode, DbNode>> allFloors = new LinkedList<>();
  //private LinkedList<DbNode> thisFloor = new LinkedList<DbNode>();

  private HashMap<String, Image> floorMaps = new HashMap<>();

  public MapDataStorage() {

    currentBuilding = "Faulkner";
    currentFloor = 4;

    URL res;
    String name, path;
    for (int i = 1; i <= numOfFloors; i++) {
      name = "Floor" + i;
      path = "edu/wpi/N/images/" + name + "TeamN.png";  // Need to edit this to be more accommodating
      res = getClass().getClassLoader().getResource(path);
      floorMaps.put(name, new Image(res.toString()));
    }


  }

  public Image getMap(int floorNum) {
    return floorMaps.get("Floor"+ floorNum);
  }


  /**
   * setCurrentBuilding - Attempt to set the current building, then set entrance floor as current
   *
   * @param building - Building name (with campus name) to retrieve
   * @return Boolean stating whether the operation was successful (false if building isn't found)
   */
  public boolean setCurrentBuilding(String building) {
    return false;
  }

  /**
   * setCurrentBuildingAndFloor - Attempt to set the current building, then set entrance floor as
   * current
   *
   * @param building - Building name (with campus name) to retrieve
   * @param floor - Floor to retrieve
   * @return Boolean stating whether the operation was successful (false if floor or building isn't
   *     found)
   */
  public boolean setCurrentBuildingAndFloor(String building, int floor) {
    return false;
  }

  /**
   * setCurrentFloor - Attempt to set the current floor
   *
   * @param floor - Floor to retrieve
   * @return Boolean stating whether the operation was successful (false if floor isn't found)
   */
  public boolean setCurrentFloor(int floor) {
    this.currentFloor = floor;
    return true;
  }
}
