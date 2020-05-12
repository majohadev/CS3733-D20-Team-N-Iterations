package edu.wpi.N.views.admin;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.ServiceDB;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class StatisticsController implements Initializable {

  @FXML CategoryAxis xAxis;
  @FXML NumberAxis yAxis;
  @FXML CategoryAxis xAxisLang;
  @FXML NumberAxis yAxisLang;
  @FXML CategoryAxis xAxisPath;
  @FXML NumberAxis yAxisPath;
  @FXML CategoryAxis xAxisReqLoc;
  @FXML NumberAxis yAxisRecLoc;
  @FXML BarChart<String, Integer> bc_ServiceData;
  @FXML BarChart<String, Integer> bc_pathData;
  @FXML BarChart<String, Integer> bc_recLocData;
  @FXML BarChart<String, Integer> bc_popLangData;

  XYChart.Series<String, Integer> requestData = new XYChart.Series<>();
  XYChart.Series<String, Integer> pathData = new XYChart.Series<>();
  XYChart.Series<String, Integer> requestLocation = new XYChart.Series<>();
  XYChart.Series<String, Integer> languageData = new XYChart.Series<>();

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    try {
      initServiceData();
      initLocData();
      initPathData();
      initRecLang();
      popFromDBServices();
      popFromDBPath();
      popFromDBPopularLang();
      popFromDBReqLoc();
    } catch (DBException e) {
      e.printStackTrace();
    }
  }

  public void initServiceData() {
    bc_ServiceData.setTitle("Number of Service Requests");
    xAxis.setLabel("Service Type");
    yAxis.setLabel("Number of Requests");
    // xAxis.setStyle("-fx-text-fill: #e6ebf2");
    bc_ServiceData.setLegendVisible(false);
  }

  public void initPathData() {
    bc_pathData.setTitle("Most Used Paths");
    xAxisPath.setLabel("Path");
    yAxisPath.setLabel("Times Visited");
    xAxis.setStyle("-fx-text-fill: #e6ebf2");
    bc_pathData.setLegendVisible(false);
  }

  public void initLocData() {
    bc_recLocData.setTitle("Most Used Request Locations");
    xAxisReqLoc.setLabel("Location");
    yAxisRecLoc.setLabel("Times Used");
    bc_recLocData.setLegendVisible(false);
  }

  public void initRecLang() {
    bc_popLangData.setTitle("Most Requested Languages");
    xAxisLang.setLabel("Language");
    yAxisLang.setLabel("Times Requested");
    bc_popLangData.setLegendVisible(false);
  }

  public void popFromDBServices() throws DBException {
    bc_ServiceData.getData().clear();
    int n = ServiceDB.popularServices().size();

    if (n > 5) {
      n = 5;
    }

    for (int i = 0; i < n; i++) {
      requestData
          .getData()
          .add(
              new XYChart.Data<>(
                  ServiceDB.popularServices().get(i).getKey(),
                  ServiceDB.popularServices().get(i).getValue()));
    }

    bc_ServiceData.getData().addAll(requestData);
  }

  public void popFromDBPath() throws DBException {
    bc_pathData.getData().clear();
    int n = ServiceDB.popularPathLocations().size();
    if (n > 5) {
      n = 5;
    }

    for (int i = 0; i < n; i++) {
      pathData
          .getData()
          .add(
              new XYChart.Data<>(
                  ServiceDB.popularPathLocations().get(i).getKey().getLongName(),
                  ServiceDB.popularPathLocations().get(i).getValue()));
    }
    bc_pathData.getData().addAll(pathData);
  }

  public void popFromDBReqLoc() throws DBException {
    bc_recLocData.getData().clear();

    int n = ServiceDB.popularReqLocations().size();

    if (n > 5) {
      n = 5;
    }

    for (int i = 0; i < n; i++) {
      requestLocation
          .getData()
          .add(
              new XYChart.Data<>(
                  ServiceDB.popularReqLocations().get(i).getKey().getLongName(),
                  ServiceDB.popularReqLocations().get(i).getValue()));
    }
    bc_recLocData.getData().addAll(requestLocation);
  }

  public void popFromDBPopularLang() throws DBException {
    bc_popLangData.getData().clear();

    int n = ServiceDB.popularLanguages().size();

    if (n > 5) {
      n = 5;
    }

    for (int i = 0; i < n; i++) {
      languageData
          .getData()
          .add(
              new XYChart.Data<>(
                  ServiceDB.popularLanguages().get(i).getKey(),
                  ServiceDB.popularLanguages().get(i).getValue()));
    }

    bc_popLangData.getData().addAll(languageData);
  }
}
