package edu.wpi.N.entities;

// Node = Graph Node (limited data)
// Needed for A* algorithm
public class Node implements Comparable<Node>, INode {
  private double xcoord, ycoord;
  public double score;
  public String ID;

  public Node(double xcoord, double ycoord, String id) {
    this.xcoord = xcoord;
    this.ycoord = ycoord;
    this.ID = id;
  }

  public double getX() {
    return xcoord;
  }

  public double getY() {
    return ycoord;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Node)) return false;
    Node other = (Node) o;
    return this.ID.equals(other.ID);
  }

  @Override
  public int compareTo(Node other) {
    if (this.score > other.score) {
      return 1;
    }
    if (this.score < other.score) {
      return -1;
    } else return 0;
  }
}
