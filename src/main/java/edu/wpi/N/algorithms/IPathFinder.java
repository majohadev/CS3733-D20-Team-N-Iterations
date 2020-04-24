package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;

public interface IPathFinder {
  Path findPath(DbNode start, DbNode end) throws DBException;
}
