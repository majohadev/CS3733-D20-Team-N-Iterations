package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import java.util.HashMap;
import java.util.LinkedList;
import org.bridj.util.Pair;

public interface IPathFinder {
  Path findPath(
      HashMap<String, LinkedList<DbNode>> mapData,
      DbNode startNode,
      DbNode endNode,
      boolean handicap)
      throws DBException;

  Path findQuickAccess(HashMap<String, LinkedList<DbNode>> mapData, DbNode start, String nodeType)
      throws DBException;

  Pair<Path, Path> getPathWithStop(
      HashMap<String, LinkedList<DbNode>> mapData,
      DbNode start,
      DbNode end,
      DbNode stop,
      boolean handicap)
      throws DBException;

  // Overloaded method to add Quick Access Stop
  Pair<Path, Path> getPathWithStop(
      HashMap<String, LinkedList<DbNode>> mapData,
      DbNode start,
      DbNode end,
      String nodeType,
      boolean handicap)
      throws DBException;
}
