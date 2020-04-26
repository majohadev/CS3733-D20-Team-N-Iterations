package edu.wpi.N.entities;

import java.util.LinkedList;
import javafx.scene.shape.Circle;

public class UINode {
  Circle circle;
  DbNode node;
  LinkedList<UIEdge> edges;

  public UINode(Circle circle, DbNode node) {
    this.circle = circle;
    this.node = node;
    this.edges = new LinkedList<>();
  }

  public void setCircle(Circle circle) {
    this.circle = circle;
  }

  public Circle getCircle() {
    return this.circle;
  }

  public void setDBNode(DbNode node) {
    this.node = node;
  }

  public DbNode getDBNode() {
    return this.node;
  }

  public void setEdges() {
    this.edges = edges;
  }

  public LinkedList<UIEdge> getEdges() {
    return edges;
  }

  public void addEdge(UIEdge edge) {
    this.edges.add(edge);
  }

  public void removeEdge(UIEdge edge) {
    this.edges.remove(edge);
  }
}
