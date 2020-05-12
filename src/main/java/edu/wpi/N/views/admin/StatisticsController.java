package edu.wpi.N.views.admin;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Service;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

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
      printPopLocations();
    } catch (DBException e) {
      e.printStackTrace();
    }
  }

  public void initServiceData() {
    bc_ServiceData.setTitle("Number of Service Requests");
    xAxis.setLabel("Service Type");
    yAxis.setLabel("Number of Requests");
    xAxis.setStyle("-fx-text-fill: #e6ebf2");
  }

  public void initPathData() {
    bc_pathData.setTitle("Most Used Paths");
    xAxisPath.setLabel("Path");
    yAxisPath.setLabel("Times Visited");
    xAxis.setStyle("-fx-text-fill: #e6ebf2");
  }

  public void initLocData() {
    bc_recLocData.setTitle("Most Used Request Locations");
    xAxisReqLoc.setLabel("Location");
    yAxisRecLoc.setLabel("Times Used");
    xAxis.setStyle("-fx-text-fill: #e6ebf2");
  }

  public void initRecLang() {
    bc_popLangData.setTitle("Most Requested Languages");
    xAxisLang.setLabel("Language");
    yAxisLang.setLabel("Times Requested");
    xAxis.setStyle("-fx-text-fill: #e6ebf2");
  }

  public void popFromDBServices() throws DBException {
    for (Service service : ServiceDB.getServices()) {
      for (Pair<String, Integer> val : ServiceDB.popularServices()) {
        if (service.getServiceType().equals(val.getKey())) {
          for (int i = 0; i < 5; i++) { // Only adds the top 5
            requestData.getData().add(new XYChart.Data<>(service.getServiceType(), val.getValue()));
          }
        }
      }
    }
    bc_ServiceData.getData().addAll(requestData);
  }

  public void popFromDBPath() throws DBException {
    for (DbNode node : MapDB.allNodes()) {
      // System.out.println("Here");
      for (Pair<DbNode, Integer> val : ServiceDB.popularPathLocations()) {
        System.out.println("In Second Loop");
        System.out.println("Value: " + val.getValue() + " Key: " + val.getKey());
        if (node.equals(val.getKey())) {
          System.out.println("Added");
          pathData.getData().add(new XYChart.Data<>(node.getLongName(), val.getValue()));
        }
      }
    }
    bc_pathData.getData().addAll(pathData);
  }

  public void printPopLocations() throws DBException {
    for (Pair<DbNode, Integer> val : ServiceDB.popularPathLocations()) {
      System.out.println(val.getKey().getLongName());
    }
  }

  public void popFromDBReqLoc() throws DBException {
    for (DbNode node : MapDB.allNodes()) {
      for (Pair<DbNode, Integer> val : ServiceDB.popularReqLocations()) {
        for (int i = 0; i < 5; i++) {
          if (node.equals(val.getKey())) {
            requestLocation.getData().add(new XYChart.Data<>(node.getLongName(), val.getValue()));
          }
        }
      }
      bc_recLocData.getData().addAll(requestLocation);
    }
  }

  // Works
  public void popFromDBPopularLang() throws DBException {
    for (int i = 0; i < 5; i++) {
      for (String lang : ServiceDB.getLanguages()) {
        for (Pair<String, Integer> val : ServiceDB.popularLanguages()) {
          if (lang.equals(val.getKey())) {
            languageData.getData().add(new XYChart.Data<>(lang, val.getValue()));
          }
        }
        bc_popLangData.getData().addAll(languageData);
      }
    }
  }
}
