package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import java.util.HashMap;
import java.util.LinkedList;
import org.bridj.util.Pair;

public class Algorithm {
  private IPathFinder pathFinder;
  private HashMap<String, LinkedList<DbNode>> mapData;

  public Algorithm() throws DBException {
    this.pathFinder = new AStar();
    uploadMapData();
  }

  public HashMap<String, LinkedList<DbNode>> getMapData() {
    return this.mapData;
  }

  // Getter
  public IPathFinder getPathFinder(IPathFinder pathFinder) {
    return this.pathFinder;
  }

  // Setter
  public void setPathFinder(IPathFinder pathFinder) {
    this.pathFinder = pathFinder;
  }

  /** Uploads Nodes and Edges from Database into Hashmap */
  public void uploadMapData() throws DBException {
    this.mapData = MapDB.loadMapData();
  }

  /**
   * Finds the shortest path from Start to Goal node using the algorithm that pathfinder is set to
   *
   * @param startNode: The start node
   * @param endNode: The destination node
   * @param handicap: Boolean saying whether path should be handicap accessible only
   * @throws DBException
   * @return: Path object indicating the shortest path to the goal Node from Start Node
   */
  public Path findPath(DbNode startNode, DbNode endNode, boolean handicap) throws DBException {
    try {
      return pathFinder.findPath(mapData, startNode, endNode, handicap);
    } catch (DBException e) {
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Function returns the Path to the nearest 'Quick Search Location'
   *
   * @param startNode, DbNode of starting node
   * @param nodeType, String the type of node you want (must be length 4)
   * @return Path, path from start node to closest (eucledian) end node of requested type
   * @throws DBException
   */
  public Path findQuickAccess(DbNode startNode, String nodeType) throws DBException {
    try {
      return pathFinder.findQuickAccess(mapData, startNode, nodeType);
    } catch (DBException e) {
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Finds the best path from the start to a stop node of the type given, then the best path from
   * the stop to the end
   *
   * @param start: Start node
   * @param end: End node
   * @param nodeType: nodeType desired for the stop node
   * @param handicap: Boolean saying whether path should be handicap accessible only
   * @return: A pair of paths (start to stop, stop to end)
   * @throws DBException
   */
  public Pair<Path, Path> getPathWithStop(
      DbNode start, DbNode end, String nodeType, boolean handicap) throws DBException {
    try {
      return pathFinder.getPathWithStop(mapData, start, end, nodeType, handicap);
    } catch (DBException e) {
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Finds the best path from the start to the stop node given, then the best path from the stop to
   * the end
   *
   * @param start: Start node
   * @param end: End node
   * @param stop: Stop node
   * @param handicap: Boolean saying whether path should be handicap accessible only
   * @return: A pair of paths (start to stop, stop to end)
   * @throws DBException
   */
  public Pair<Path, Path> getPathWithStop(DbNode start, DbNode end, DbNode stop, boolean handicap)
      throws DBException {
    try {
      return pathFinder.getPathWithStop(mapData, start, end, stop, handicap);
    } catch (DBException e) {
      e.printStackTrace();
      throw e;
    }
  }
}
