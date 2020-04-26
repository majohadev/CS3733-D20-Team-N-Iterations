package edu.wpi.N.views;

import edu.wpi.N.App;

import java.net.URL;
import java.util.ResourceBundle;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.apache.derby.impl.sql.compile.DB2LengthOperatorNode;

public class BetweenFloorsController implements Controller, Initializable {
    App mainApp = null;
    DbNode node;

  public BetweenFloorsController(DbNode node) throws DBException {
    this.node = node;
  }

  @Override
    public void setMainApp(App mainApp) {
        this.mainApp = mainApp;
    }

    final Color DEFAULT_LINE_COLOR = Color.BLACK;
    final Color DEFAULT_CIRCLE_COLOR = Color.PURPLE;
    final int DEFAULT_RADIUS = 15;

    final double DEFAULT_LINE_WIDTH = 4;
    @FXML
    private AnchorPane parent;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        Circle circle1 = createCircle(1000, 500);
        Circle circle2 = createCircle(1000, 450);
        Circle circle3 = createCircle(1000, 400);
        Circle circle4 = createCircle(1000, 350);
        Circle circle5 = createCircle(1000, 300);
        Line line12 =
                createLine(
                        circle1, circle2,
                        DEFAULT_LINE_COLOR);
        Line line23 =
                createLine(
                        circle2, circle3,
                        DEFAULT_LINE_COLOR);
        Line line34 =
                createLine(
                        circle3, circle4,
                        DEFAULT_LINE_COLOR);
        Line line45 =
                createLine(
                        circle4, circle5,
                        DEFAULT_LINE_COLOR);
    }

    private Line createLine(Circle c1, Circle c2, Color c) {
        double x1 = c1.getCenterX();
        double x2 = c2.getCenterX();
        double y1 = c1.getCenterY();
        double y2 = c2.getCenterY();
        Line line = new Line(x1, y1, x2, y2);
        line.setStroke(c);
        line.setStrokeWidth(DEFAULT_LINE_WIDTH);
        parent.getChildren().add(line);
        line.toBack();
        return line;
    }

    private Circle createCircle(double x, double y) {
        Circle circle = new Circle(DEFAULT_RADIUS, DEFAULT_CIRCLE_COLOR);
        circle.setCenterX(x);
        circle.setCenterY(y);
        parent.getChildren().add(circle);
        circle.toFront();
        return circle;
    }
}
