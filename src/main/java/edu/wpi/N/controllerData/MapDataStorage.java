package edu.wpi.N.controllerData;

import com.google.common.collect.HashBiMap;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import java.util.LinkedList;

public class MapDataStorage {

    // Current floor/node vars
    private String currentBuilding;
    private int currentFloor;

    private LinkedList<DbNode> allFloorNodes; // stores all the nodes on the floor
    private LinkedList<String> longNamesList = new LinkedList<>(); // Stores Floor Node names

    private DbNode defaultNode;

    public MapDataStorage ()  throws DBException {

        currentBuilding = "Faulkner";
        currentFloor = 4;
        defaultNode = MapDB.getNode("NHALL00804");
        if (defaultNode == null) defaultNode = allFloorNodes.getFirst();
    }

    /**
     * getAllFloorNodes - Retrieve all the nodes on this floor, refreshing from DB if requested
     * @param doRefresh Whether or not to clear current floor's nodes and reload from DB
     * @return List of current floor's nodes
     */
    public LinkedList<DbNode> getFloorNodes(boolean doRefresh) {
        if (doRefresh) {
            try {
                allFloorNodes = MapDB.floorNodes(currentFloor, currentBuilding);
            } catch (DBException e) {
                System.out.println("MAP WARNING: Loading " + currentBuilding + ", floor " + currentFloor + "nodes failed.");
                e.printStackTrace();
            }
        }
        return allFloorNodes;
    }

    /**
     * setCurrentBuilding - Attempt to set the current building, then set entrance floor as current
     * @param building - Building name (with campus name) to retrieve
     * @return Boolean stating whether the operation was successful (false if building isn't found)
     */
    public boolean setCurrentBuilding(String building) {
        return false;
    }

    /**
     * setCurrentBuildingAndFloor - Attempt to set the current building, then set entrance floor as current
     * @param building - Building name (with campus name) to retrieve
     * @param floor - Floor to retrieve
     * @return Boolean stating whether the operation was successful (false if floor or building isn't found)
     */
    public boolean setCurrentBuildingAndFloor(String building, int floor) {
        return false;
    }

    /**
     * setCurrentFloor - Attempt to set the current floor
     * @param floor - Floor to retrieve
     * @return Boolean stating whether the operation was successful (false if floor isn't found)
     */
    public boolean setCurrentFloor (int floor) {
        this.currentFloor = floor;
        return true;
    }

}
