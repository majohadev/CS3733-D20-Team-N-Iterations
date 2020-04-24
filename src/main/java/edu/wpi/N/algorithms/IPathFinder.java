package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;

public interface IPathFinder {
  Path findPath(DbNode startNode, DbNode endNode, boolean handicap) throws DBException;
}
