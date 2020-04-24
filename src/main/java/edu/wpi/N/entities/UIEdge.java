package edu.wpi.N.entities;

import java.util.LinkedList;
import javafx.scene.shape.Line;

public class UIEdge {
  Line line;
  LinkedList<DbNode> nodes;

  public UIEdge(Line line, LinkedList nodes) {
    this.nodes = nodes;
    this.line = line;
  }

  public void setLine(Line line) {
    this.line = line;
  }

  public Line getLine() {
    return this.line;
  }

  public void setDBNodes(LinkedList<DbNode> nodes) {
    this.nodes = nodes;
  }

  public LinkedList<DbNode> getDBNodes() {
    return this.nodes;
  }
}
