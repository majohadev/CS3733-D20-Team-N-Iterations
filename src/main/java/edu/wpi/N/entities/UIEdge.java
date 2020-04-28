package edu.wpi.N.entities;

import javafx.scene.shape.Line;

public class UIEdge {
  Line line;
  DbNode[] nodes;

  public UIEdge(Line line, DbNode[] nodes) {
    this.nodes = nodes;
    this.line = line;
  }

  public void setLine(Line line) {
    this.line = line;
  }

  public Line getLine() {
    return this.line;
  }

  public void setDBNodes(DbNode[] nodes) {
    this.nodes = nodes;
  }

  public DbNode[] getDBNodes() {
    return this.nodes;
  }
}
