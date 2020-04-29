package edu.wpi.N.entities;

import edu.wpi.N.views.MapBaseController;
import java.util.LinkedList;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class UIDispNode {

  private LinkedList<UIDispEdge> connectedEdges = new LinkedList<>();
  private boolean selected = false;
  private Shape marker;
  private MapBaseController mbc;

  final Color DEFAULT_NODE_COLOR = Color.PURPLE;
  final Color START_NODE_COLOR = Color.RED;
  final Color END_NODE_COLOR = Color.GREEN;
  final double DEFAULT_NODE_OPACITY = 0.7;
  final double DEFAULT_NODE_RADIUS = 7;

  private Color currentSelectColor = DEFAULT_NODE_COLOR;

  public UIDispNode(boolean showing) {
    marker = new Circle();
    ((Circle) marker).setRadius(DEFAULT_NODE_RADIUS);
    marker.setFill(DEFAULT_NODE_COLOR);
    marker.setOpacity(DEFAULT_NODE_OPACITY);
    // marker.setOnMouseClicked(mouseEvent -> this.onMarkerClicked(mouseEvent));
    marker.setCursor(Cursor.HAND); // Cursor points when over nodes
    setVisible(false);
  }

  public boolean toggleSelected() {
    setSelected(!this.selected);
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
    if (selected) {
      // Selected appearance
      marker.setFill(currentSelectColor);
    } else {
      // Deselected appearance
      marker.setFill(DEFAULT_NODE_COLOR);
    }
  }

  public void setVisible(boolean visible) {
    marker.setVisible(visible);
  }

  public void setStart() {

    marker.setFill(START_NODE_COLOR);
    setVisible(true);
  }

  public void setEnd() {
    marker.setFill(END_NODE_COLOR);
    setVisible(true);
  }

  public void setNormalNode() {
    marker.setFill(DEFAULT_NODE_COLOR);
    setVisible(false);
  }

  // Place node marker on given plane
  public void placeOnPane(Pane pane) {
    if (marker.getParent() == null) {
      pane.getChildren().add(this.marker);
    }
  }

  // Set node position
  public void setPos(double x, double y) {
    if (marker.getParent() != null) {
      this.marker.setLayoutX(x);
      this.marker.setLayoutY(y);

      for (UIDispEdge edge : connectedEdges) {
        edge.updateMarkerPos(); // Move all lines with the node
      }
    }
  }

  // X position getter
  public double getX() {
    return this.marker.getLayoutX();
  }

  // Y position getter
  public double getY() {
    return this.marker.getLayoutY();
  }

  // Get whether this node is selected
  public boolean getSelected() {
    return selected;
  }

  // Set base map controller to report back to
  public void setBaseMap(MapBaseController mbc) {
    this.mbc = mbc;
  }

  /*
  // Handles mouse clicks on node marker
  public void onMarkerClicked(MouseEvent e) {
    toggleSelected();
    if (mbc != null) {
      mbc.onUINodeClicked(e, this);
    }
  }
   */

  // Return added edge
  public UIDispEdge addEdgeTo(UIDispNode other, UIDispEdge edgeToRecycle) {

    if (edgeTo(other) == null) {
      if (edgeToRecycle == null) {
        UIDispEdge newEdge = new UIDispEdge(true, this, other);
        this.connectedEdges.add(newEdge);
        other.connectedEdges.add(newEdge);
        newEdge.updateMarkerPos();
        return newEdge;
      } else {
        edgeToRecycle.setVisible(true);
        edgeToRecycle.setNodes(this, other);
        this.connectedEdges.add(edgeToRecycle);
        other.connectedEdges.add(edgeToRecycle);
        edgeToRecycle.updateMarkerPos();
        return edgeToRecycle;
      }
    }
    return null; // Edge exists already
  }

  // Return whether edge was broken
  public boolean breakEdgeTo(UIDispNode other) {
    UIDispEdge toBreak = edgeTo(other);
    if (toBreak != null) {
      connectedEdges.remove(toBreak);
      other.connectedEdges.remove(toBreak);
      toBreak.setNodes(null, null);
      return true;
    }
    return false; // Edge does not exist
  }

  // Assuming bi-directionality
  public UIDispEdge edgeTo(UIDispNode other) {
    for (UIDispEdge edge : connectedEdges) {
      if (edge.leadsTo(other)) {
        return edge;
      }
    }
    return null;
  }

  // Nodes are equal if they have the same position and edges
  @Override
  public boolean equals(Object other) {
    if (other instanceof UIDispNode) {
      UIDispNode otherNode = (UIDispNode) other;
      return (this.getX() == otherNode.getX()
          && this.getY() == otherNode.getY()
          && this.connectedEdges.equals(otherNode.connectedEdges));
    }
    return false;
  }
}
