package edu.wpi.N.views;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class TimeController implements Initializable {
  @FXML Label date;
  @FXML Label time;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initClock();
  }

  private void initClock() {

    Timeline clock =
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                e -> {
                  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
                  time.setText(LocalDateTime.now().format(formatter));
                  date.setText(
                      DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                          .format(LocalDate.now()));
                }),
            new KeyFrame(Duration.seconds(1)));
    clock.setCycleCount(Animation.INDEFINITE);
    clock.play();
  }
}
