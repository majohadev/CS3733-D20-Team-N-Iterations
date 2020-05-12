package edu.wpi.N.algorithms;

import edu.wpi.N.entities.DbNode;

public class Direction {
  DbNode zoomNode;
  Icon icon;
  Level level;
  String direction;

  public Direction(String direction, Level level, DbNode zoomNode, Icon icon) {
    this.direction = direction;
    this.level = level;
    this.zoomNode = zoomNode;
    this.icon = icon;
  }

  public DbNode getNode() {
    return this.zoomNode;
  }

  public Icon getIcon() {
    return this.icon;
  }

  public Level getLevel() {
    return this.level;
  }

  @Override
  public String toString() {
    return this.direction;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Direction) {
      Direction other = (Direction) o;
      return this.direction.equals(other.direction);
    }
    return false;
  }
}
