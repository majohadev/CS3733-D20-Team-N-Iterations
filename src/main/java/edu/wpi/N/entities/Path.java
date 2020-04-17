package edu.wpi.N.entities;

import java.util.LinkedList;

public class Path {
  private LinkedList<DbNode> path;

  // constructor
  public Path(LinkedList<DbNode> path) {
    this.path = path;
  }

  public LinkedList<DbNode> getPath() {
    return this.path;
  }
}
