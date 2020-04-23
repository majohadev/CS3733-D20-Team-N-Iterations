package edu.wpi.N.algorithms;

import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;

public interface PathFinder {
  Path findPath(DbNode start, DbNode end);
}
