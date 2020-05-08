package edu.wpi.N.entities;

import edu.wpi.N.views.mapDisplay.MapBaseController;
import java.util.ArrayList;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

public class UIDispEdge {

  private UIDispNode nodeA, nodeB;
  private boolean selected;
  private boolean highlighted;
  private Line marker = new Line();
  private MapBaseController mbc;

  private final Color DEFAULT_LINE_COLOR = Color.BLACK;
  private final Color SELECTED_LINE_COLOR = Color.LAWNGREEN;
  private final Color PATH_LINE_COLOR = Color.DODGERBLUE;
  private final double DEFAULT_LINE_WIDTH = 4;
  private final double LIGHT_OPACITY = 0.00;
  private final double HEAVY_OPACITY = 0.8;
  private final ArrayList<Double> DASH_PATTERN =
      new ArrayList<>() {
        {
          add(20d);
          add(10d);
        }
      };

  private double maxOffset() {
    return DASH_PATTERN.stream().reduce(0d, (a, b) -> (a + b));
  }

  private Timeline pathAnimTimeline = new Timeline();

  public UIDispEdge(boolean showing, UIDispNode nodeA, UIDispNode nodeB) {

    marker.getStrokeDashArray().setAll(DASH_PATTERN);
    marker.setStrokeWidth(DEFAULT_LINE_WIDTH);
    marker.setStroke(DEFAULT_LINE_COLOR);
    marker.setStrokeLineCap(StrokeLineCap.ROUND);
    marker.setOpacity(LIGHT_OPACITY);

    // marker.setOnMouseClicked(mouseEvent -> this.onMarkerClicked(mouseEvent));
    // marker.setCursor(Cursor.HAND); // Cursor points when over edges

    setNodes(nodeA, nodeB);

    pathAnimTimeline.setCycleCount(Timeline.INDEFINITE);
    KeyFrame keyS =
        new KeyFrame(
            Duration.ZERO, new KeyValue(marker.strokeDashOffsetProperty(), 0, Interpolator.LINEAR));
    KeyFrame keyE =
        new KeyFrame(
            Duration.millis(2000),
            new KeyValue(marker.strokeDashOffsetProperty(), maxOffset(), Interpolator.LINEAR));
    pathAnimTimeline.getKeyFrames().addAll(keyS, keyE);

    setVisible(showing);
    setSelected(false);
    setHighlighted(false);
  }

  public void updateMarkerPos() {
    if (nodeA != null && nodeB != null) {
      setVisible(true);
      marker.setStartX(nodeA.getX());
      marker.setStartY(nodeA.getY());
      marker.setEndX(nodeB.getX());
      marker.setEndY(nodeB.getY());
    } else {
      setVisible(false);
    }
  }

  // Needed to flip direction of animated line toward destination
  public void pointEdgeToward(UIDispNode node) {
    if (node != null && node == this.nodeB) {
      this.nodeB = this.nodeA;
      this.nodeA = node;
      updateMarkerPos();
    }
  }

  public boolean toggleSelected() {
    setSelected(!this.selected);
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
    if (selected) {
      // Selected appearance
      marker.setStroke(SELECTED_LINE_COLOR);
      marker.setOpacity(HEAVY_OPACITY);
    } else {
      // Deselected appearance
      marker.setStroke(DEFAULT_LINE_COLOR);
      marker.setOpacity(LIGHT_OPACITY);
    }
  }

  public boolean getSelected() {
    return selected;
  }

  public void setBaseMap(MapBaseController mbc) {
    this.mbc = mbc;
  }

  /*
  public void onMarkerClicked(MouseEvent e) {

    toggleSelected();
    if (mbc != null) {
      mbc.onUIEdgeClicked(e, this);
    }
  }
   */

  public void setVisible(boolean visible) {
    marker.setVisible(visible);
  }

  public void setHighlighted(boolean highlighted) {
    this.highlighted = highlighted;
    setVisible(true);
    if (highlighted) {
      // Path appearance
      marker.setStroke(PATH_LINE_COLOR);
      marker.setOpacity(HEAVY_OPACITY);
      startAnim();
    } else {
      // Non-path appearance
      marker.setStroke(DEFAULT_LINE_COLOR);
      marker.setOpacity(LIGHT_OPACITY);
      stopAnim();
    }
  }

  private void startAnim() {
    pathAnimTimeline.stop();
    marker.getStrokeDashArray().setAll(DASH_PATTERN);
    pathAnimTimeline.play();
  }

  private void stopAnim() {
    pathAnimTimeline.stop();
    marker.getStrokeDashArray().clear();
  }

  public void placeOnPane(Pane pane) {
    if (marker.getParent() == null) {
      pane.getChildren().add(this.marker);
    }
  }

  public void setNodes(UIDispNode nodeA, UIDispNode nodeB) {
    if (nodeA != null && nodeB != null) {
      if (nodeA != nodeB && nodeA.edgeTo(nodeB) == null) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        updateMarkerPos();
      }
    }
  }

  // Assuming bi-directionality
  public boolean leadsTo(UIDispNode node) {
    return (node == nodeA || node == nodeB);
  }

  public void breakSelf() {
    setVisible(false);
    this.nodeA.breakEdgeTo(nodeB);
  }

  // Edges are equal if they have the same end UINodes
  @Override
  public boolean equals(Object other) {
    if (other instanceof UIDispEdge) {
      UIDispEdge otherEdge = (UIDispEdge) other;
      return (otherEdge.nodeA == nodeA && otherEdge.nodeB == nodeB)
          || (otherEdge.nodeB == nodeA && otherEdge.nodeA == nodeB);
    }
    return false;
  }
}
